package com.itc.smartbroadcast.channels.protocolhandler;


import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.AlarmDeviceDetail;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.conver2HexStr;

/**
 * 获取报警任务详情
 * Created by lik on 18-10-10.
 */
public class GetAlarmDeviceDetail {


    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int PORT_COUNT_LENGTH = 1;                           //报警设备端口数
    public final static int PORT_RESPONSE_MODE_LENGTH = 1;                   //报警设备端口响应模式
    public final static int TRIGGER_MODE_LENGTH = 1;                         //报警触发模式
    public final static int PLAY_MODE_LENGTH = 1;                            //报警播放模式
    public final static int PLAY_VOLUME_LENGTH = 1;                          //报警播放音量
    public final static int IS_ALARM_PORT_SET_LENGTH = 2;                    //报警端口配置标志位
    public final static int ALARM_PORT_NAME_LENGTH = 32;                     //报警端口名称


    public static GetAlarmDeviceDetail init() {
        return new GetAlarmDeviceDetail();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {

        AlarmDeviceDetail alarmDeviceDetail = getAlarmDeviceDetail(list.get(0));

        Gson gson = new Gson();

        String json = gson.toJson(alarmDeviceDetail);

        BaseBean bean = new BaseBean();

        bean.setType("getAlarmDeviceDetail");

        bean.setData(json);

        String jsonResult = gson.toJson(bean);


        System.out.println(jsonResult);

        Log.i("jsonResult", "handler: " + jsonResult);

        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 根据byte数组获取报警任务对象
     *
     * @param bytes
     * @return
     */
    private AlarmDeviceDetail getAlarmDeviceDetail(byte[] bytes) {

        AlarmDeviceDetail alarmDeviceDetail = new AlarmDeviceDetail();
        alarmDeviceDetail.setDeviceMac(SmartBroadCastUtils.hexstrToMac(SmartBroadCastUtils.bytesToHexString(SmartBroadCastUtils.subBytes(bytes, 6, 6))));
        int index = HEAD_PACKAGE_LENGTH;
        alarmDeviceDetail.setPortCount(SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, index, PORT_COUNT_LENGTH)));
        index += PORT_COUNT_LENGTH;
        alarmDeviceDetail.setPortResponseMode(SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, index, PORT_RESPONSE_MODE_LENGTH)));
        index += PORT_RESPONSE_MODE_LENGTH;
        alarmDeviceDetail.setTriggerMode(SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, index, TRIGGER_MODE_LENGTH)));
        index += TRIGGER_MODE_LENGTH;
        alarmDeviceDetail.setPlayMode(SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, index, PLAY_MODE_LENGTH)));
        index += PLAY_MODE_LENGTH;
        alarmDeviceDetail.setPlayVolume(SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, index, PLAY_VOLUME_LENGTH)));
        index += PLAY_VOLUME_LENGTH;
        alarmDeviceDetail.setIsAlarmPortSet(getIsAlarmPortSet(SmartBroadCastUtils.subBytes(bytes, index, IS_ALARM_PORT_SET_LENGTH)));
        index += (IS_ALARM_PORT_SET_LENGTH + 2);

        List<String> portNameList = new ArrayList<>();
        for (int i = 0; i < 16; i++) {
            String portName = SmartBroadCastUtils.byteToStr(SmartBroadCastUtils.subBytes(bytes, index, ALARM_PORT_NAME_LENGTH));
            index += ALARM_PORT_NAME_LENGTH;
            portNameList.add(portName);
        }
        alarmDeviceDetail.setPortNameList(portNameList);

        return alarmDeviceDetail;
    }


    /**
     * 获取报警端口配置标志位
     *
     * @param bytes
     * @return
     */
    public int[] getIsAlarmPortSet(byte[] bytes) {

        String byteStr1 = conver2HexStr(bytes[0]);
        String byteStr2 = conver2HexStr(bytes[1]);

        int[] result = new int[16];

        int index = 0;
        for (int j = byteStr1.length() - 1; j >= 0; j--) {
            result[index] = Integer.parseInt(String.valueOf(byteStr1.charAt(j)));
            index++;
        }
        for (int j = byteStr2.length() - 1; index < 16; j--) {
            result[index] = Integer.parseInt(String.valueOf(byteStr2.charAt(j)));
            index++;
        }
        return result;
    }


    /**
     * 发送获取报警任务详情命令
     */
    public static void sendCMD(String host) {
        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getAlarmDetail(), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host,getAlarmDetail());
        }
    }

    /**
     * 获取获取报警任务详情byte[]
     *
     * @return
     */
    private static byte[] getAlarmDetail() {


        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("00B7");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //添加校验码
        cmdStr.append("" + SmartBroadCastUtils.checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");
        byte[] bytes = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return bytes;
    }
}
