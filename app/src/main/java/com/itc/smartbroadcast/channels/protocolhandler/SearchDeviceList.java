package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToStr;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.checkSum;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * author： lghandroid
 * created：2018/12/28 10:10
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: _搜索局域网内所有设备
 */

public class SearchDeviceList {

    private static final int NAME_LENGTH = 32;                  //设备名称字节数
    private static final int HOST_IP_LENGTH = 4;                //设备目标主机IP字节数
    private static final int MAC_LENGTH = 6;                    //设备MAC字节数
    private static final int SYS_PSW_LENGTH = 14;               //设备系统登录密码
    private static final int VOICE_LENGTH = 1;                  //设备音量字节数
    private static final int STATUS_LENGTH = 1;                 //设备状态字节数
    private static final int MODEL_LENGTH = 32;                 //设备型号字节数
    private static final int VERSION_LENGTH = 2;                //设备版本信息字节数
    private static final int HEAD_PACKET_LENGTH = 28;           //包头

    public static SearchDeviceList init() {
        return new SearchDeviceList();
    }


    //主要业务处理，字节转对象，对象转集合再合并，最终输出json字符串
    public void handler(List<byte[]> list) {
        List<FoundDeviceInfo> foundDeviceInfoList = new ArrayList<FoundDeviceInfo>();
        for (byte[] b : list) {
            List<FoundDeviceInfo> searchDeviceInfos = byteToSearchDeviceInfo(b);
            foundDeviceInfoList = objectMerging(foundDeviceInfoList, searchDeviceInfos);
        }
        Gson gson = new Gson();
        String json = gson.toJson(foundDeviceInfoList);
        BaseBean baseBean = new BaseBean();
        baseBean.setType("searchDeviceList");
        baseBean.setData(json);
        String toJson = gson.toJson(baseBean);
        Log.i("jsonResult", "handler: " + toJson);
        EventBus.getDefault().post(toJson);
    }


    //数据封装成对象
    private List<FoundDeviceInfo> byteToSearchDeviceInfo(byte[] b) {
        List<FoundDeviceInfo> foundDeviceInfoList = new ArrayList<FoundDeviceInfo>();
        //局域网设备数量
        int deviceCount = SmartBroadCastUtils.byteToInt(SmartBroadCastUtils.subBytes(b, HEAD_PACKET_LENGTH + 2, 1)[0]);
        Log.e("jsonResult", "局域网设备数量: " + deviceCount);
        //封装设备列表数据集合
        int itemBt = MAC_LENGTH + NAME_LENGTH + STATUS_LENGTH + VOICE_LENGTH + SYS_PSW_LENGTH + MODEL_LENGTH + VERSION_LENGTH + HOST_IP_LENGTH;  //一个设备item所占字节数
        byte[] deviceListBt = SmartBroadCastUtils.subBytes(b, HEAD_PACKET_LENGTH + 3, itemBt * deviceCount);
        int index;   //定义字节数游标
        for (int i = 0; i < deviceListBt.length; i += itemBt) {
            FoundDeviceInfo deviceInfo = new FoundDeviceInfo();
            index = 0;
            //设备MAC
            String deviceMac = SmartBroadCastUtils.hexstrToMac(SmartBroadCastUtils.bytesToHexString(SmartBroadCastUtils.subBytes(deviceListBt, i + index, MAC_LENGTH)));
            index += MAC_LENGTH;
            //设备名称
            String deviceName = SmartBroadCastUtils.byteToStr(SmartBroadCastUtils.subBytes(deviceListBt, i+ index, NAME_LENGTH));
            index += NAME_LENGTH;
            //终端状态
            String hexStatus = SmartBroadCastUtils.bytesToHexString(SmartBroadCastUtils.subBytes(deviceListBt, i + index, STATUS_LENGTH));
            index += STATUS_LENGTH;
            //终端音量
            int deviceVoice = SmartBroadCastUtils.byteToInt(SmartBroadCastUtils.subBytes(deviceListBt, i + index, VOICE_LENGTH)[0]);
            index += VOICE_LENGTH;
            //系统密码
            String sysPsw = byteToStr(subBytes(deviceListBt, i + index, SYS_PSW_LENGTH));
//            byte[] pswBytes = SmartBroadCastUtils.subBytes(deviceListBt, i + index, SYS_PSW_LENGTH - 2);
//            StringBuffer sysPsw = new StringBuffer();
//            for (int j = 0; j < pswBytes.length; j += 2) {
//                int arrayToInt = byteArrayToInt(subBytes(pswBytes, j, 2));
//                sysPsw.append(String.valueOf(arrayToInt));
//            }
            index += SYS_PSW_LENGTH;
            //终端型号
            String deviceModel = SmartBroadCastUtils.byteToStr(SmartBroadCastUtils.subBytes(deviceListBt, i + index, MODEL_LENGTH));
            index += MODEL_LENGTH;
            //终端版本信息
            String hexVersion = SmartBroadCastUtils.bytesToHexString(SmartBroadCastUtils.subBytes(deviceListBt, i + index, VERSION_LENGTH));
            String deviceVersion = SmartBroadCastUtils.hexToVersion(hexVersion);
            index += VERSION_LENGTH;
            //目标主机IP
            String hexIp = SmartBroadCastUtils.bytesToHexString(SmartBroadCastUtils.subBytes(deviceListBt, i + index, HOST_IP_LENGTH));
            String hostIP = SmartBroadCastUtils.hexstrToIp(hexIp);

            deviceInfo.setDeviceMac(deviceMac);
            deviceInfo.setDeviceName(deviceName);
            deviceInfo.setDeviceVoice(deviceVoice);
            deviceInfo.setSysPassword(sysPsw.toString());
            deviceInfo.setDeviceMedel(deviceModel);
            deviceInfo.setDeviceVersionMsg(deviceVersion);
            deviceInfo.setDeviceIp(hostIP);
            if ("00".equals(hexStatus)) {
                deviceInfo.setDeviceStatus("离线");
            } else if ("01".equals(hexStatus)) {
                deviceInfo.setDeviceStatus("在线");
            } else if ("02".equals(hexStatus)) {
                deviceInfo.setDeviceStatus("占用");
            } else if ("03".equals(hexStatus)) {
                deviceInfo.setDeviceStatus("正在呼寻");
            } else if ("04".equals(hexStatus)) {
                deviceInfo.setDeviceStatus("正在监听");
            } else if ("fe".equals(hexStatus)) {
                deviceInfo.setDeviceStatus("故障");
            } else if ("ff".equals(hexStatus)) {
                deviceInfo.setDeviceStatus("密码错误");
            }
            foundDeviceInfoList.add(deviceInfo);
        }
        return foundDeviceInfoList;
    }


    private List<FoundDeviceInfo> objectMerging(List<FoundDeviceInfo> foundDeviceInfos1, List<FoundDeviceInfo> foundDeviceInfos2) {
        List<FoundDeviceInfo> foundDeviceInfoList = new ArrayList<FoundDeviceInfo>();
        for (FoundDeviceInfo foundDeviceInfo : foundDeviceInfos1) {
            foundDeviceInfoList.add(foundDeviceInfo);
        }
        for (FoundDeviceInfo foundDeviceInfo : foundDeviceInfos2) {
            foundDeviceInfoList.add(foundDeviceInfo);
        }
        return foundDeviceInfoList;
    }


    //发送指令
    public static void sendCMD(String host) {
        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getDeviceList(), host, false);
                NettyTcpClient.getInstance().sendPackageNotRetrySend(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackageNotRetrySend(host, getDeviceList());
        }
    }


    //搜索局域网内设备命令
    public static byte[] getDeviceList() {
        StringBuffer cmdStr = new StringBuffer();
        //起始标志
        cmdStr.append("AA55");
        //长度
        cmdStr.append("0000");
        //命令
        cmdStr.append("0CBF");
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
        //校验值
        cmdStr.append(checkSum(cmdStr.substring(4)));
        //结束标志
        cmdStr.append("55AA");
        byte[] bytes = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return bytes;
    }

}