package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.AlarmPortDevice;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditAlarmPortDeviceListResult;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * content:编辑报警端口绑定的设备
 * author:lik
 * date: 18-10-11 上午9:09
 */
public class EditAlarmPortDeviceList {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int PORT_NUM_LENGTH = 1;                             //端口编号
    public final static int PORT_NAME_LENGTH = 32;                           //端口名称
    public final static int PORT_MUSIC_PATH_LENGTH = 32;                     //端口曲目路径
    public final static int PORT_MUSIC_NAME_LENGTH = 64;                     //端口曲目名称
    public final static int DEVICE_COUNT_LENGTH = 1;                         //设备总数
    public final static int PORT_DEVICE_MAC_LIST_LENGTH = 6;                 //端口绑定的设备mac列表
    public final static int RESULT = 1;                                      //结果

    public static EditAlarmPortDeviceList init() {
        return new EditAlarmPortDeviceList();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {

        EditAlarmPortDeviceListResult editAlarmPortDeviceListResult = getEditAlarmPortDeviceListResult(list.get(0));

        Gson gson = new Gson();

        String json = gson.toJson(editAlarmPortDeviceListResult);

        BaseBean bean = new BaseBean();

        bean.setType("editAlarmPortDeviceList");

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
    private EditAlarmPortDeviceListResult getEditAlarmPortDeviceListResult(byte[] bytes) {

        EditAlarmPortDeviceListResult editAlarmPortDeviceListResult = new EditAlarmPortDeviceListResult();
        int index = HEAD_PACKAGE_LENGTH;
        editAlarmPortDeviceListResult.setPortNum(SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, index, PORT_NUM_LENGTH)));
        index += PORT_NUM_LENGTH;
        if (SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, index, RESULT)) > 0) {
            editAlarmPortDeviceListResult.setResult(1);
        } else {
            editAlarmPortDeviceListResult.setResult(0);
        }
        return editAlarmPortDeviceListResult;
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
            NettyUdpClient.getInstance().sendPackage(host, getAlarmDetail(alarmPortDevice));
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
        cmdStr.append("05B7");
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
        String portNum = Integer.toHexString(alarmPortDevice.getPortNum());
        cmdStr.append((portNum.length() == 1) ? "0" + portNum : portNum);
        //获取端口名称的16进制
        String portNameHex = SmartBroadCastUtils.str2HexStr(alarmPortDevice.getPortName());
        if (portNameHex.length() > PORT_NAME_LENGTH * 2) {
            portNameHex = portNameHex.substring(0, PORT_NAME_LENGTH * 2);
        } else {
            int len = portNameHex.length();
            int count = (PORT_NAME_LENGTH * 2) - portNameHex.length();
            for (int i = 0; i < count; i++) {
                portNameHex += "0";
            }
        }
        cmdStr.append(portNameHex);
        //获取曲目路径
        String musicPathHex = SmartBroadCastUtils.str2HexStr(alarmPortDevice.getPortMusicPath());
        if (musicPathHex.length() > PORT_MUSIC_PATH_LENGTH * 2) {
            musicPathHex = musicPathHex.substring(0, PORT_MUSIC_PATH_LENGTH * 2);
        } else {
            int len = musicPathHex.length();
            int count = (PORT_MUSIC_PATH_LENGTH * 2) - musicPathHex.length();
            for (int i = 0; i < count; i++) {
                musicPathHex += "0";
            }
        }
        cmdStr.append(musicPathHex);

        //获取曲目名称
        String musicNameHex = SmartBroadCastUtils.str2HexStr(alarmPortDevice.getPortMusicName());
        if (musicNameHex.length() > PORT_MUSIC_NAME_LENGTH * 2) {
            musicNameHex = musicNameHex.substring(0, PORT_MUSIC_NAME_LENGTH * 2);
        } else {
            int len = musicNameHex.length();
            int count = (PORT_MUSIC_NAME_LENGTH * 2) - musicNameHex.length();
            for (int i = 0; i < count; i++) {
                musicNameHex += "0";
            }
        }
        cmdStr.append(musicNameHex);
        //添加终端数目
        String deviceSize = Integer.toHexString(alarmPortDevice.getPortDeviceMacList().size());
        cmdStr.append((deviceSize.length() == 1) ? "0" + deviceSize : deviceSize);

        for (int i = 0; i < alarmPortDevice.getPortDeviceMacList().size(); i++) {
            //添加终端Mac
            cmdStr.append(getDeviceMac(alarmPortDevice.getPortDeviceMacList().get(i)));
        }
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //添加校验码
        cmdStr.append("" + SmartBroadCastUtils.checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");
        byte[] bytes = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return bytes;
    }

    /**
     * 获取Mac地址信息
     *
     * @param mac
     * @return
     */
    public static String getDeviceMac(String mac) {

        String[] strs = mac.split("-");
        String result = "";
        for (String str : strs) {
            result += str;
        }
        return result;
    }

}
