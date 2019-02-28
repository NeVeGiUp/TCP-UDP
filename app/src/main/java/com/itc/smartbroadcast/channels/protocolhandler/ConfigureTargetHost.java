package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.ConfigureTargetHostInfo;
import com.itc.smartbroadcast.bean.ConfigureTargetHostResult;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.checkSum;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.chinese2Hex;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.ipToHex;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * author： lghandroid
 * created：2019/1/28 14:08
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: 配置目标主机协议
 */


public class ConfigureTargetHost {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int TARGET_HOST_IP = 4;                              //目标主机ip
    public final static int DEVICE_TOTAL = 1;                                //终端设备总数
    public final static int DEVICE_MAC = 6;                                  //设备MAC
    public final static int SEND_STATUS = 1;                                 //发送状态
    public final static int PACKAGE_TOTAL_LENGTH = 1;                        //包总数
    public final static int PACKAGE_NUM_LENGTH = 1;                          //包序号


    public static ConfigureTargetHost init() {
        return new ConfigureTargetHost();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {
        ConfigureTargetHostResult result = getConfigureTargetHostResult(list.get(0));
        Gson gson = new Gson();
        String json = gson.toJson(result);
        BaseBean bean = new BaseBean();
        bean.setType("ConfigureTargetHostResult");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        Log.i("jsonResult", "handler: " + jsonResult);
        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 配置目标主机状态结果
     *
     * @param bytes
     * @return
     */
    public ConfigureTargetHostResult getConfigureTargetHostResult(byte[] bytes) {
        //包头
        int head = HEAD_PACKAGE_LENGTH;
        //去掉包头，尾字节
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        ConfigureTargetHostResult result = new ConfigureTargetHostResult();
        //发送状态
        result.setResult(byteToInt(subBytes(bytes, 0, SEND_STATUS)[0]));
        return result;
    }


    /**
     * 发送配置目标主机请求
     *
     * @param host                    ip地址
     * @param configureTargetHostInfo OperateMusicFilesInfo数据对象
     */
    public static void sendCMD(String host, ConfigureTargetHostInfo configureTargetHostInfo) {
        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                List<byte[]> bytes = SmartBroadCastUtils.CloudUtil(getConfigureTargetHostBytes(configureTargetHostInfo), host, false);
                NettyTcpClient.getInstance().sendPackages(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackages(host, getConfigureTargetHostBytes(configureTargetHostInfo));
        }
    }


    /**
     * 获取配置目标主机字节串
     *
     * @param configureTargetHostInfo
     * @return
     */
    private static List<byte[]> getConfigureTargetHostBytes(ConfigureTargetHostInfo configureTargetHostInfo) {
        //单包最多可发送曲目数，超过此数量需分包
        int packageTargetHostNum = (1024 - (HEAD_PACKAGE_LENGTH + END_PACKAGE_LENGTH + PACKAGE_TOTAL_LENGTH + PACKAGE_NUM_LENGTH + TARGET_HOST_IP +
                DEVICE_TOTAL)) / DEVICE_MAC;
        //客户端需要处理的曲目总数
        int deviceTotal = configureTargetHostInfo.getDeviceTotal();
        List<byte[]> resultList = new ArrayList<>();
        //截取每个包曲目名称集合
        List<String> macList = configureTargetHostInfo.getMacList();
        if (deviceTotal > packageTargetHostNum) {
            //包总数
            int packageSize = 0;
            //包序号
            int packageIndex = 0;
            //余数
            int remainder = deviceTotal % packageTargetHostNum;
            if (remainder > 0) {
                packageSize = (deviceTotal / packageTargetHostNum) + 1;
            } else {
                packageSize = (deviceTotal / packageTargetHostNum);
            }
            for (int i = 0; i < packageSize; i++) {
                //多包
                StringBuffer cmdStr = new StringBuffer();
                //添加起始标志
                cmdStr.append("AA55");
                //添加长度
                cmdStr.append("0000");
                //添加命令
                cmdStr.append("0ABF");
                //添加本机Mac
                String mac = DeviceUtils.getMacAddress();
                cmdStr.append(mac.replace(":", ""));
                //添加控制ID
                cmdStr.append("000000000000");
                //添加云转发指令
                cmdStr.append("00");
                //保留字段
                cmdStr.append("000000000000000000");
                //添加总包数
                cmdStr.append(String.valueOf(packageSize).length() == 1 ? "0" + packageSize : packageSize);
                //添加当前包序号
                cmdStr.append(String.valueOf(packageIndex).length() == 1 ? "0" + packageIndex : packageIndex);
                //目标主机IP
                String hostIP = ipToHex(configureTargetHostInfo.getIp());
                cmdStr.append(hostIP);
                //设备总数
                String macListStr = getMacListHex(macList, i, packageTargetHostNum);
                String total = Integer.toHexString(macListStr.length() / (DEVICE_MAC * 2));
                cmdStr.append(total.length() == 1 ? "0" + total : total);
                //设备MAC
                cmdStr.append(macListStr);
                //修改长度
                cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
                //校验值
                cmdStr.append(checkSum(cmdStr.substring(4)));
                //添加结束标志
                cmdStr.append("55AA");
                packageIndex++;
                byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
                resultList.add(result);
            }
        } else {
            //单包
            StringBuffer cmdStr = new StringBuffer();
            //添加起始标志
            cmdStr.append("AA55");
            //添加长度
            cmdStr.append("0000");
            //添加命令
            cmdStr.append("0ABF");
            //添加本机Mac
            String mac = DeviceUtils.getMacAddress();
            cmdStr.append(mac.replace(":", ""));
            //添加控制ID
            cmdStr.append("000000000000");
            //添加云转发指令
            cmdStr.append("00");
            //保留字段
            cmdStr.append("000000000000000000");
            //添加总包数
            cmdStr.append("01");
            //添加当前包序号
            cmdStr.append("00");
            //目标主机IP
            String hostIP = ipToHex(configureTargetHostInfo.getIp());
            cmdStr.append(hostIP);
            //设备总数
            String total = Integer.toHexString(configureTargetHostInfo.getDeviceTotal());
            cmdStr.append(total.length() == 1 ? "0" + total : total);
            //设备MAC
            for (String deviceMac : macList) {
                cmdStr.append(deviceMac.replaceAll("-",""));
            }
            //修改长度
            cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
            //校验值
            cmdStr.append(checkSum(cmdStr.substring(4)));
            //添加结束标志
            cmdStr.append("55AA");
            byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
            resultList.add(result);
        }
        return resultList;
    }


    public static String getMacListHex(List<String> list, int packetIndex, int packageMusicNum) {
        String result = "";
        for (int i = packetIndex * packageMusicNum; i < list.size(); i++) {
            if (i < ((packetIndex + 1) * (packageMusicNum))) {
                result += list.get(i).trim().replaceAll("-","");
            }
        }
        return result;
    }


}
