package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.DeviceControlResult;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * @Content : DeviceControl 设备控制（CD，FM，音频采集器）
 * @Author : lik
 * @Time : 18-9-3 下午2:14
 */

public class DeviceControl {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int RESULT = 1;                                      //结果

    public static DeviceControl init() {
        return new DeviceControl();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {

        DeviceControlResult deviceControlResult = getDeviceControlResult(list.get(0));

        Gson gson = new Gson();

        String json = gson.toJson(deviceControlResult);

        BaseBean bean = new BaseBean();

        bean.setType("deviceControlResult");

        bean.setData(json);

        String jsonResult = gson.toJson(bean);

        Log.i("jsonResult", "handler: " + jsonResult);

        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 获取编辑任务详情结果
     *
     * @param bytes
     * @return
     */

    public DeviceControlResult getDeviceControlResult(byte[] bytes) {

        //头部多余数
        int head = HEAD_PACKAGE_LENGTH;
        //去掉方案总数的字节
        DeviceControlResult deviceControlResult = new DeviceControlResult();
        deviceControlResult.setResult(SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, head, RESULT)));
        return deviceControlResult;
    }

    /**
     * 发送控制设备命令
     *
     * @param userNum         USER_NUM
     * @param foundDeviceInfo 需要用到设备的IP和Mac地址
     * @param type            操控类型（播放，暂停...）
     * @param controlParam    控制参数，当曲目播放时，用于指定曲目的序号。当调节音量是，用于指定音量值。
     */
    public static void sendCMD(int userNum, FoundDeviceInfo foundDeviceInfo, int type, int controlParam, int taskNum) {
        /**
         *  ********************************************************************************************************************************************************************************************
         * 当操控设备为CD机时
         * type的值对应的操作
         * case 1:     //播放
         * case 2:     //暂停
         * case 3:     //上一首
         * case 4:     //下一首
         * case 5:     //停止播放
         * case 6:     //切换（光盘/U盘/SD卡）
         * case 13:    //指定曲目播放
         * case 14:    //进出仓控制
         * case 15:    //音量调节
         * case 7:     //顺序播放
         * case 8:     //单曲循环
         * case 9:     //全部循环
         * case 10:    //仅播放单曲
         * case 16:    //快进
         * case 17:    //快退
         * case 12:    //获取当前状态
         * case 19:    //指定播放进度
         ********************************************************************************************************************************************************************************************
         * 当操控设备为FM(收音机)时
         * type的值对应的操作
         * case 0:     //播放
         * case 1:     //暂停
         * case 2:     //频道-
         * case 3:     //频道+
         * case 4:     //频率-
         * case 5:     //频率+
         * case 6:     //BAND模式切换
         * case 7:     //自动搜台
         * case 12:    //获取当前状态
         *********************************************************************************************************************************************************************************************
         * 当操控设备音频采集器时
         * type的值对应的操作
         * case 1:     //播放
         * case 5:     //停止播放
         * case 15:    //音乐调节
         * case 12:    //获取当前状态
         ******************************************************************************************************************************************************************************************
         */

        String host = foundDeviceInfo.getDeviceIp();

        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getCMD(userNum, foundDeviceInfo, type, controlParam, taskNum), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host, getCMD(userNum, foundDeviceInfo, type, controlParam, taskNum));
        }


    }

    /**
     * 获取控制发送命令
     *
     * @param userNum         随机控制码userNum
     * @param foundDeviceInfo 用于获取终端设备信息
     * @param type            操作类型
     * @param controlParam    控制参数
     * @return 发送命令
     */
    public static byte[] getCMD(int userNum, FoundDeviceInfo foundDeviceInfo, int type, int controlParam, int taskNum) {

        StringBuffer cmd = new StringBuffer();
        //添加起始标志
        cmd.append("AA55");
        //添加长度
        cmd.append("0000");
        //添加命令
        cmd.append("05B4");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmd.append(mac.replace(":", ""));
        //添加控制ID
        cmd.append("000000000000");
        //添加云转发指令
        cmd.append("00");
        //保留字段
        cmd.append("000000000000000000");

        //添加即时任务ID
        String taskNumStr = SmartBroadCastUtils.intToUint16Hex(taskNum);
        cmd.append(taskNumStr);

        //添加随机控制吗
        cmd.append(SmartBroadCastUtils.intToUint16Hex(userNum));
        //添加设备控制码
        String typeStr = Integer.toHexString(type);
        cmd.append((typeStr.length() == 1) ? "0" + typeStr : typeStr);
        //添加控制参数
        cmd.append(SmartBroadCastUtils.intToUint16Hex(controlParam));
        //修改长度
        cmd.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmd.substring(4).length() + 4) / 2));
        //添加校验码
        cmd.append("" + SmartBroadCastUtils.checkSum(cmd.substring(4)));
        //添加结束标志
        cmd.append("55AA");
        byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmd.toString());
        return result;
    }
}
