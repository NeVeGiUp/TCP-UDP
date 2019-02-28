package com.itc.smartbroadcast.channels.udp;

import android.util.Log;

import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.base.Base2Activity;
import com.itc.smartbroadcast.channels.protocolhandler.HeartBeat;
import com.itc.smartbroadcast.util.AppUtil;
import com.itc.smartbroadcast.util.ConfigUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;
import com.itc.smartbroadcast.util.ToastUtil;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;


/**
 * author： lghandroid
 * created：2018/7/24 18:30
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels
 * describe: udp协议连接，发送数据类
 */
public class NettyUdpClient {

    public static final String TAG = "NettyUdpClient";
    public static NettyUdpClient nettyClient;
    private static Channel channel;
    private static Date nowDate;

    private static boolean isHeart = false;
    private static String heartHost = "";

    /**
     * NettyUdpClient单例
     *
     * @return 单例对象
     */
    public synchronized static NettyUdpClient getInstance() {
        if (nettyClient == null) {
            synchronized (NettyUdpClient.class) {
                if (nettyClient == null) {
                    nettyClient = new NettyUdpClient();
                }
            }
        }
        return nettyClient;
    }

    public void initServer() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //开始客户端的服务，和管道的设置
            Bootstrap b = new Bootstrap();
            //由于我们用的是UDP协议，所以要用NioDatagramChannel来创建
            b.group(group).channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_RCVBUF, 2048 * 1024)// 设置UDP读缓冲区为2M
                    .option(ChannelOption.SO_SNDBUF, 1024 * 1024)// 设置UDP写缓冲区为1M
                    .option(ChannelOption.SO_BROADCAST, true)//允许广播
                    .handler(new NettyUdpClientHandler());//设置消息处理器
            //服务端绑定的管道的端口 监听端口


            channel = b.bind(ConfigUtils.UDP_AUDIO_PORT).sync().channel();

        } catch (Exception e) {
            group.shutdownGracefully();
        } finally {
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
            NettyUdpClientHandler.buffList.clear();
            channel.writeAndFlush(new DatagramPacket(byteBuf, new InetSocketAddress(host, ConfigUtils.UDP_COMMAND_PORT))).sync();
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

            if (new Date().getTime() - nowDate.getTime() >= 1000) {
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
            sendPackageNotRetrySend(host, udpcommand);
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
            NettyUdpClientHandler.buffList.clear();
            channel.writeAndFlush(new DatagramPacket(byteBuf, new InetSocketAddress(host, ConfigUtils.UDP_COMMAND_PORT))).sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void sendHeartBeatCMD(final String host, final int userNum) {
        heartHost = host;
        if (!isHeart) {
            isHeart = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    boolean bol = true;
                    while (bol) {

                        if (!AppUtil.isNetworkAvailable(SmartBroadcastApplication.getContext())) {//当前页面判断是否有网络
                        } else {
                            HeartBeat.sendCMD(heartHost);
                        }
                        try {
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }
}
