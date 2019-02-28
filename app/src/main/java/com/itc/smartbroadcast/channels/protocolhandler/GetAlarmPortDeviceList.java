package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.AlarmPortDevice;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * content:获取报警端口绑定的设备列表
 * author:lik
 * date: 18-10-11 上午9:09
 */
public class GetAlarmPortDeviceList {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int PORT_NUM_LENGTH = 1;                             //端口编号
    public final static int PORT_NAME_LENGTH = 32;                           //端口名称
    public final static int PORT_MUSIC_PATH_LENGTH = 32;                     //端口曲目路径
    public final static int PORT_MUSIC_NAME_LENGTH = 64;                     //端口曲目名称
    public final static int DEVICE_COUNT_LENGTH = 1;                         //设备总数
    public final static int PORT_DEVICE_MAC_LIST_LENGTH = 6;                 //端口绑定的设备mac列表

    public static GetAlarmPortDeviceList init() {
        return new GetAlarmPortDeviceList();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {

        AlarmPortDevice alarmPortDevice = getAlarmPortDeviceList(list.get(0));

        Gson gson = new Gson();

        String json = gson.toJson(alarmPortDevice);

        BaseBean bean = new BaseBean();

        bean.setType("getAlarmPortDeviceList");

        bean.setData(json);

        String jsonResult = gson.toJson(bean);


        System.out.println(jsonResult);

        Log.i("jsonResult", "handler: " + jsonResult);

        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 根据byte数组获取报警端口绑定设备列表
     *
     * @param bytes
     * @return
     */
    private AlarmPortDevice getAlarmPortDeviceList(byte[] bytes) {

        AlarmPortDevice alarmPortDevice = new AlarmPortDevice();
        int index = HEAD_PACKAGE_LENGTH;
        alarmPortDevice.setPortNum(SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, index, PORT_NUM_LENGTH)));
        index += PORT_NUM_LENGTH;
        alarmPortDevice.setPortName(SmartBroadCastUtils.byteToStr(SmartBroadCastUtils.subBytes(bytes, index, PORT_NAME_LENGTH)));
        index += PORT_NAME_LENGTH;
        alarmPortDevice.setPortMusicPath(SmartBroadCastUtils.byteToStr(SmartBroadCastUtils.subBytes(bytes, index, PORT_MUSIC_PATH_LENGTH)));
        index += PORT_MUSIC_PATH_LENGTH;
        alarmPortDevice.setPortMusicName(SmartBroadCastUtils.byteToStr(SmartBroadCastUtils.subBytes(bytes, index, PORT_MUSIC_NAME_LENGTH)));
        index += PORT_MUSIC_NAME_LENGTH;
        int deviceCount = SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, index, DEVICE_COUNT_LENGTH));
        alarmPortDevice.setDeviceCount(deviceCount);
        index += DEVICE_COUNT_LENGTH;
        List<String> deviceMacList = new ArrayList<>();
        for (int i = 0; i < deviceCount; i++) {
            String macHex = SmartBroadCastUtils.bytesToHexString(SmartBroadCastUtils.subBytes(bytes, index, PORT_DEVICE_MAC_LIST_LENGTH));
            String mac = SmartBroadCastUtils.hexstrToMac(macHex);
            deviceMacList.add(mac);
            index += PORT_DEVICE_MAC_LIST_LENGTH;
        }
        alarmPortDevice.setPortDeviceMacList(deviceMacList);
        return alarmPortDevice;
    }

    /**
     * 发送获取报警任务详情命令
     */
    public static void sendCMD(String host, AlarmPortDevice alarmPortDevice) {

        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getAlarmDetail(alarmPortDevice), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host,getAlarmDetail(alarmPortDevice));
        }

    }

    /**
     * 获取获取报警任务详情byte[]
     *
     * @return
     */
    private static byte[] getAlarmDetail(AlarmPortDevice alarmPortDevice) {


        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("01B7");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //添加查询的端口号
        cmdStr.append(SmartBroadCastUtils.intToUint16Hex(alarmPortDevice.getPortNum()));
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
