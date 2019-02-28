package com.itc.smartbroadcast.application;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.itc.smartbroadcast.channels.http.CloudProtocolModel;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.ConfigUtils;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.ToastUtil;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;
import com.tencent.bugly.Bugly;


/**
 * 全局基类
 */
public class SmartBroadcastApplication extends Application {

    private static SmartBroadcastApplication application;
    private static Context context;

    public static boolean replyData = true;                     //用于做无响应再次发送全局变量


    public static boolean isCloud = false;                      //判断是否为云登录
    public static String cloudMacAddress = "0000000000";      //选中登录的云主机mac地址
    public static String usernameSecretKey = "";      //选中登录的云主机mac地址

    private static Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            ToastUtil.show(context, (String) msg.obj);
            super.handleMessage(msg);
        }
    };

    public synchronized static SmartBroadcastApplication getInstance() {
        if (application == null) {
            throw new IllegalStateException("Application is not created.");
        }
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        context = getApplicationContext();

        //初始化子系统mac地址
        String mac = DeviceUtils.getMacAddress();
        cloudMacAddress = mac.replace(":", "");

        // 第三个参数为SDK调试模式开关，调试模式的行为特性如下：输出详细的Bugly SDK的Log；
        // 每一条Crash都会被立即上报；自定义日志将会在Logcat中输出。建议在测试阶段建议设置成true，发布时设置为false。
        Bugly.init(getApplicationContext(), "98f91c1d2b", true);   //bugly初始化
        Logger.addLogAdapter(new AndroidLogAdapter());

        new Thread(new Runnable() {
            @Override
            public void run() {
                //初始化UDP连接
                NettyUdpClient.getInstance().initServer();
                //初始化TCP连接
                NettyTcpClient.getInstance().connServer(ConfigUtils.TCP_HOST, ConfigUtils.TCP_PORT);
//                NettyTcpClient.getInstance().initServer();

            }
        }).start();

        //获取Authorization 云授权
        CloudProtocolModel.getCloudAuthorization("20882088","nGk5R2wrnZqQ02bed29rjzax1QWRIu1O",this);

    }

    /**
     * e
     * 获取上下文
     */
    public static Context getContext() {
        return context;
    }

    /**
     * 获取上下文
     */
    public static void showToast(String content) {
        Message msg = new Message();
        msg.obj = content;
        myHandler.sendMessage(msg);
    }

}
