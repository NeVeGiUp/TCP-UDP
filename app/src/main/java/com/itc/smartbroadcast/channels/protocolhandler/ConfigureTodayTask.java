package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.ConfigureTodayTaskInfo;
import com.itc.smartbroadcast.bean.ConfigureTodayTaskResult;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.checkSum;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * author： lghandroid
 * created：2018/10/12 10:03
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: 配置今日任务，切换执行周几任务
 */


public class ConfigureTodayTask {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int CONFIGURE_STATE_LENGTH = 1;                      //配置状态


    public static ConfigureTodayTask init() {
        return new ConfigureTodayTask();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {
        ConfigureTodayTaskResult configureTodayTaskResult = getConfigureTodayTaskResult(list.get(0));
        Gson gson = new Gson();
        String json = gson.toJson(configureTodayTaskResult);
        BaseBean bean = new BaseBean();
        bean.setType("ConfigureTodayTaskResult");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        Log.i("jsonResult", "handler: " + jsonResult);
        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 获取今日任务配置结果
     *
     * @param bytes
     * @return
     */
    public ConfigureTodayTaskResult getConfigureTodayTaskResult(byte[] bytes) {
        //包头
        int head = HEAD_PACKAGE_LENGTH;
        //去掉包头，尾字节
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        ConfigureTodayTaskResult configureTodayTaskResult = new ConfigureTodayTaskResult();
        //配置结果
        configureTodayTaskResult.setResult(byteToInt(subBytes(bytes, 0, CONFIGURE_STATE_LENGTH)[0]));
        return configureTodayTaskResult;
    }


    /**
     * 发送获取今日任务配置结果指令
     *
     * @param host                   定时器IP
     * @param configureTodayTaskInfo 配置今日任务信息对象
     */
    public static void sendCMD(String host, ConfigureTodayTaskInfo configureTodayTaskInfo) {
        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getConfigureTodayTaskBytes(configureTodayTaskInfo), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host, getConfigureTodayTaskBytes(configureTodayTaskInfo));
        }
    }


    /**
     * 获取今日任务配置结果字节流
     *
     * @param configureTodayTaskInfo 配置今日任务信息对象
     * @return
     */
    private static byte[] getConfigureTodayTaskBytes(ConfigureTodayTaskInfo configureTodayTaskInfo) {
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("0BB3");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //今日执行周几任务
        String hexExecuteTodayTaskWeek = Integer.toHexString(configureTodayTaskInfo.getExecuteTaskWeek());
        cmdStr.append(hexExecuteTodayTaskWeek.length() == 1 ? "0" + hexExecuteTodayTaskWeek : hexExecuteTodayTaskWeek);
        //详细日期时间
        int[] executeTaskDate = configureTodayTaskInfo.getExecuteTaskDate();
        for (int i = 0; i < executeTaskDate.length; i++) {
            String hexTime = Integer.toHexString(executeTaskDate[i]);
            cmdStr.append(hexTime.length() == 1 ? "0" + hexTime : hexTime);
        }
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //校验值
        cmdStr.append(checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");
        byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return result;
    }


}
