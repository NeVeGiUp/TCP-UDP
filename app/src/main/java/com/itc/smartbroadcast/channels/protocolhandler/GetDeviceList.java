package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteArrayToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.checkSum;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.conver2HexStr;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * author： lghandroid
 * created：2018/8/21 17:28
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: _获取设备列表
 */

public class GetDeviceList {

    private static final int NAME_LENGTH = 32;                  //设备名称字节数
    private static final int IP_LENGTH = 4;                     //设备IP字节数
    private static final int MAC_LENGTH = 6;                    //设备MAC字节数
    private static final int VOICE_LENGTH = 1;                  //设备音量字节数
    private static final int STATUS_LENGTH = 1;                 //设备状态字节数
    private static final int MODEL_LENGTH = 32;                 //设备型号字节数
    private static final int VERSION_LENGTH = 2;                //设备版本信息字节数
    private static final int PARTITION_LENGTH = 20;             //设备分区信息字节数
    private static final int PARTITION_FLAG_LENGTH = 20;        //物理分区标志位信息字节数
    private static final int HEAD_PACKET_LENGTH = 28;           //包头

    int waitPartitionCount = 0;                                 //待分区设备计数器


    public static GetDeviceList init() {
        return new GetDeviceList();
    }


    /**
     * 主要业务处理，字节转对象，对象转集合再合并，最终输出json字符串
     * @param list
     */
    public void handler(List<byte[]> list) {
        List<FoundDeviceInfo> foundDeviceInfoList = new ArrayList<FoundDeviceInfo>();
        for (byte[] b : list) {
            List<FoundDeviceInfo> FoundDeviceInfos = byteToFoundDeviceInfo(b);
            foundDeviceInfoList = objectMerging(foundDeviceInfoList, FoundDeviceInfos);
        }
        AppDataCache.getInstance().putString("waitPartitionCount", waitPartitionCount + "");
        Gson gson = new Gson();
        String json = gson.toJson(foundDeviceInfoList);
        BaseBean baseBean = new BaseBean();
        baseBean.setType("getDeviceList");
        baseBean.setData(json);
        String toJson = gson.toJson(baseBean);
        Log.i("jsonResult", "handler: " + toJson);
        //缓存终端设备数量
        AppDataCache.getInstance().putString("deviceCount", foundDeviceInfoList.size() + "");
        EventBus.getDefault().post(toJson);
    }


    /**
     * 数据封装成对象
     * @param b
     * @return
     */
    private List<FoundDeviceInfo> byteToFoundDeviceInfo(byte[] b) {
        List<FoundDeviceInfo> foundDeviceInfoList = new ArrayList<FoundDeviceInfo>();
        //设备数量
        int deviceCount = SmartBroadCastUtils.byteToInt(SmartBroadCastUtils.subBytes(b, HEAD_PACKET_LENGTH + 2, 1)[0]);
        Log.e("found", "设备数量: " + deviceCount);
        //封装设备列表数据集合
        int itemBt = NAME_LENGTH + IP_LENGTH + MAC_LENGTH + VOICE_LENGTH + STATUS_LENGTH + MODEL_LENGTH + VERSION_LENGTH +
                PARTITION_LENGTH + PARTITION_FLAG_LENGTH;  //一个设备item所占字节数
        byte[] deviceListBt = SmartBroadCastUtils.subBytes(b, HEAD_PACKET_LENGTH + 3, itemBt * deviceCount);
        int index;   //定义字节数游标
        for (int i = 0; i < deviceListBt.length; i += itemBt) {
            FoundDeviceInfo deviceInfo = new FoundDeviceInfo();
            index = 0;
            //设备名称
            String deviceName = SmartBroadCastUtils.byteToStr(SmartBroadCastUtils.subBytes(deviceListBt, i, NAME_LENGTH));
            index += NAME_LENGTH;
            //设备IP
            String hexIp = SmartBroadCastUtils.bytesToHexString(SmartBroadCastUtils.subBytes(deviceListBt, i + index, IP_LENGTH));
            String deviceIp = SmartBroadCastUtils.hexstrToIp(hexIp);
            index += IP_LENGTH;
            //设备MAC
            String deviceMac = SmartBroadCastUtils.hexstrToMac(SmartBroadCastUtils.bytesToHexString(SmartBroadCastUtils.subBytes(deviceListBt, i + index, MAC_LENGTH)));
            index += MAC_LENGTH;
            //终端音量
            int deviceVoice = SmartBroadCastUtils.byteToInt(SmartBroadCastUtils.subBytes(deviceListBt, i + index, VOICE_LENGTH)[0]);
            index += VOICE_LENGTH;
            //终端状态
            String hexStatus = SmartBroadCastUtils.bytesToHexString(SmartBroadCastUtils.subBytes(deviceListBt, i + index, STATUS_LENGTH));
            index += STATUS_LENGTH;
            //终端型号
            String deviceModel = SmartBroadCastUtils.byteToStr(SmartBroadCastUtils.subBytes(deviceListBt, i + index, MODEL_LENGTH));
            index += MODEL_LENGTH;
            //终端版本信息
            String hexVersion = SmartBroadCastUtils.bytesToHexString(SmartBroadCastUtils.subBytes(deviceListBt, i + index, VERSION_LENGTH));
            String deviceVersion = SmartBroadCastUtils.hexToVersion(hexVersion);
            index += VERSION_LENGTH;
            //分区信息
            byte[] partitionInfoBytes = SmartBroadCastUtils.subBytes(deviceListBt, i + index, PARTITION_LENGTH);
            ArrayList<Integer> numList = new ArrayList<>();
            for (int j = 0; j < partitionInfoBytes.length; j += 2) {
                if (j < partitionInfoBytes.length) {
                    int num = byteArrayToInt(subBytes(partitionInfoBytes, j, 2));
                    if (65535 != num) {   //Unit16 ffff 转为int数为65535
                        numList.add(num);
                    }
                }
            }
            index += PARTITION_LENGTH;
            //物理分区标志位
            byte[] partitionFlagBytes = SmartBroadCastUtils.subBytes(deviceListBt, i + index, PARTITION_FLAG_LENGTH);
            ArrayList<int[]> flagList = new ArrayList<>();
            for (int j = 0; j < partitionFlagBytes.length; j += 2) {
                if (j < partitionFlagBytes.length) {
                    int[] num = getDeviceZoneMsg(subBytes(partitionFlagBytes, j, 2));
                    if (numList.size() > 0) {   //设备有分区编号的情况下物理分区标志位才有效
                        flagList.add(num);
                    }
                }
            }
            deviceInfo.setDeviceName(deviceName);
            deviceInfo.setDeviceIp(deviceIp);
            deviceInfo.setDeviceMac(deviceMac);
            deviceInfo.setDeviceMedel(deviceModel);
            deviceInfo.setDeviceVoice(deviceVoice);
            deviceInfo.setDeviceVersionMsg(deviceVersion);
            deviceInfo.setDeviceZoneMsg(numList);
            deviceInfo.setDeviceZoneFlagMsg(flagList);
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
            }else if ("fd".equals(hexStatus)){
                deviceInfo.setDeviceStatus("空载");
            }else {
                deviceInfo.setDeviceStatus("未知");
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
            if (foundDeviceInfo.getDeviceZoneMsg().size() == 0) {  //终端设备分区信息无分区编号为待分区设备
                waitPartitionCount++;
            }
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
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host,getDeviceList());
        }
    }


    /**
     * 设备列表命令
     * @return
     */
    public static byte[] getDeviceList() {
        StringBuffer cmdStr = new StringBuffer();
        //起始标志
        cmdStr.append("AA55");
        //长度
        cmdStr.append("0000");
        //命令
        cmdStr.append("00B1");
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


    /**
     * 获取分区信息
     *
     * @param bytes
     * @return
     */
    public int[] getDeviceZoneMsg(byte[] bytes) {
        String byteStr1 = conver2HexStr(bytes[1]);
        String byteStr2 = conver2HexStr(bytes[0]);
        int[] result = new int[10];
        int index = 0;
        for (int j = byteStr2.length() - 1; j >= 0; j--) {
            result[index] = Integer.parseInt(String.valueOf(byteStr2.charAt(j)));
            index++;
        }
        for (int j = byteStr1.length() - 1; index < 10; j--) {
            result[index] = Integer.parseInt(String.valueOf(byteStr1.charAt(j)));
            index++;
        }
        return result;
    }
}