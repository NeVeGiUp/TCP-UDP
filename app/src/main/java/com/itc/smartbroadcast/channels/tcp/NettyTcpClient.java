package com.itc.smartbroadcast.channels.tcp;

import android.util.Log;

import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.base.Base2Activity;
import com.itc.smartbroadcast.util.AppUtil;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;
import com.itc.smartbroadcast.util.ToastUtil;

import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.bytes.ByteArrayDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;

public class NettyTcpClient {


    public static final String TAG = "NettyTcpClient";
    public static NettyTcpClient nettyClient;
    private static Channel channel;

    private String host; //ip地址
    private int port; //端口号
    // 是否停止
    private boolean isStop = false;
    private boolean isPortError = true;
    public static boolean isConnecting = false; //防止多个连接同时进行导致无限重连

    public static boolean isConnSucc = false;

    private ScheduledExecutorService executorService;

    private EventLoopGroup eventLoopGroup;//EventLoop线程组
    private Bootstrap b;

    private static Date nowDate;

    /**
     * NettyTcpClient
     *
     * @return 单例对象
     */
    public synchronized static NettyTcpClient getInstance() {
        if (nettyClient == null) {
            synchronized (NettyTcpClient.class) {
                if (nettyClient == null) {
                    nettyClient = new NettyTcpClient();
                }
            }
        }
        return nettyClient;
    }


    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            if (channel != null) {
                //发送心跳
                JSONObject object = new JSONObject();
            }
        }
    };


    public void initServer() throws InterruptedException {

        eventLoopGroup = new NioEventLoopGroup();
        b = new Bootstrap();
        b.group(eventLoopGroup);
        b.option(ChannelOption.SO_KEEPALIVE, true);
        b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);
        b.channel(NioSocketChannel.class);
        b.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelPipeline pipeline = socketChannel.pipeline();
                pipeline.addLast(new IdleStateHandler(6, 10, 0));
                //自定义分隔符解码类
                pipeline.addLast(new DelimiterBasedFrameDecoder(1572, Unpooled.copiedBuffer(SmartBroadCastUtils.HexStringtoBytes("1a2938475665748392a1"))));
                pipeline.addLast(new ByteArrayDecoder());
                pipeline.addLast("handler", new NettyTcpClientHandler());
            }
        });
    }


    @ChannelHandler.Sharable
    public static class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
        final EventExecutorGroup group = new DefaultEventExecutorGroup(2);

        public ServerChannelInitializer() throws InterruptedException {
        }

        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {


            ChannelPipeline pipeline = socketChannel.pipeline();

            pipeline.addLast("handler", new NettyTcpClientHandler());
        }
    }


    /**
     * Returns {@code CR ('\r')} and {@code LF ('\n')} delimiters, which could
     * be used for text-based line protocols.
     */
    public static ByteBuf[] lineDelimiter() {
        return new ByteBuf[]{
                Unpooled.wrappedBuffer(new byte[]{'\r', '\n'}),
                Unpooled.wrappedBuffer(new byte[]{'\n'}),
        };
    }


    //连接通道
    public void connServer(String mhost, int mport) {

        try {
            initServer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.host = mhost;
        this.port = mport;
        isStop = false;
        isPortError = true;
        if (executorService != null && executorService.isShutdown()) {
            executorService.shutdown();
            executorService = null;
        }
        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    //连接服务器
                    if (channel != null && channel.isOpen()) {
                        channel.close();
                        channel = null;
                    }
                    channel = b.connect(host, port).sync().channel();
                    if (channel.isOpen()) {
                        isConnSucc = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(NettyTcpClient.TAG, "连接失败");
                    isConnSucc = false;
                } finally {
                    if (isConnSucc) {
                        if (executorService != null) {
                            executorService.shutdown();
                            executorService = null;
                        }
                    }
                }
            }
        }, 1, 3, TimeUnit.SECONDS);
    }

    //停止服务
    public synchronized void onStop() {
        isStop = true;
        if (channel != null && channel.isOpen()) {
            channel.close();
        }
        if (executorService != null) {
            executorService.shutdown();
            executorService = null;
        }
    }


    /**
     * 单包发送
     * 发送udp指令数据包到指定的ip与端口
     */

    public void sendPackage(final String host, final byte[] udpcommand) {

        if (!AppUtil.isNetworkAvailable(SmartBroadcastApplication.getContext())) {//当前页面判断是否有网络
            ToastUtil.show(SmartBroadcastApplication.getContext(), "当前网络未连接，请检查网络！");
            return;
        }
        // 向网段类所有机器广播发UDP，这是想客户端发送内容
        String hexString = SmartBroadCastUtils.bytesToHexString(udpcommand);
        Log.e(TAG, "发送数据包到" + host + " >>> " + hexString);
        ByteBuf byteBuf = Unpooled.copiedBuffer(udpcommand);
        try {
            channel.writeAndFlush(byteBuf).sync();
            if (SmartBroadcastApplication.replyData) {
                SmartBroadcastApplication.replyData = false;
                if (Base2Activity.getCommonProgressDialog() != null && !Base2Activity.getCommonProgressDialog().isShowing()) {
                    Base2Activity.getCommonProgressDialog().show();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        nowDate = new Date();
                        judgmentDataRecovery(host, udpcommand);
                    }
                }).start();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void judgmentDataRecovery(String host, byte[] udpcommand) {

        int index = 0;
        boolean bol = true;
        while (bol) {

            if (SmartBroadcastApplication.replyData) {
                bol = false;
                if (Base2Activity.getCommonProgressDialog() != null && Base2Activity.getCommonProgressDialog().isShowing()) {
                    Base2Activity.getCommonProgressDialog().dismiss();
                }
            }

            if (index == 5) {
                SmartBroadcastApplication.replyData = true;
                SmartBroadcastApplication.showToast("未检测到主机，终端已离线！");
                if (Base2Activity.getCommonProgressDialog() != null && Base2Activity.getCommonProgressDialog().isShowing()) {
                    Base2Activity.getCommonProgressDialog().dismiss();
                }
                bol = false;
            }

            if (new Date().getTime() - nowDate.getTime() >= 2000) {
                {
                    Log.i(TAG, "judgmentDataRecovery: " + index);
                    nowDate = new Date();
                    sendPackage(host, udpcommand);
                }
                index++;
            }
        }
    }

    /**
     * 多包发送
     * 发送udp指令数据包到指定的ip与端口
     */

    public void sendPackages(String host, List<byte[]> list) {

        for (byte[] udpcommand : list) {
            sendPackage(host, udpcommand);
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * 单包发送
     * 发送udp指令数据包到指定的ip与端口
     */

    public void sendPackageNotRetrySend(final String host, final byte[] udpcommand) {


        if (!AppUtil.isNetworkAvailable(SmartBroadcastApplication.getContext())) {//当前页面判断是否有网络
            ToastUtil.show(SmartBroadcastApplication.getContext(), "当前网络未连接，请检查网络！");
            return;
        }

        // 向网段类所有机器广播发UDP，这是想客户端发送内容
        String hexString = SmartBroadCastUtils.bytesToHexString(udpcommand);
        Log.e(TAG, "发送数据包到" + host + " >>> " + hexString);
        ByteBuf byteBuf = Unpooled.copiedBuffer(udpcommand);
        try {
            channel.writeAndFlush(byteBuf).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    //发送TCP心跳包

    public static void sendheartPackage() {

    }


}
