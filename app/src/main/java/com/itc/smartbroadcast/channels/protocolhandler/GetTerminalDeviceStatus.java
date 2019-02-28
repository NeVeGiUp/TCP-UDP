package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.TerminalDeviceStatus;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteArrayToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToDate;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.bytesToHexString;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.conver2HexStr;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * 获取终端状态
 * Created by lik on 18-9-28.
 */

public class GetTerminalDeviceStatus {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度

    public final static int DEVICE_SIZE_LENGTH = 1;                          //设备总数长度

    public final static int PLAY_STATUS_LENGTH = 1;                          //播放状态长度
    public final static int PRIORITY_LENGTH = 1;                             //优先级长度
    public final static int CHANNEL_STATUS_LENGTH = 1;                       //通道状态数量
    public final static int TARGET_IP_LENGTH = 4;                            //目标IP
    public final static int TARGET_MAC_LENGTH = 6;                           //目标Mac


    public static GetTerminalDeviceStatus init() {
        return new GetTerminalDeviceStatus();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {

        List<TerminalDeviceStatus> terminalDeviceStatusList = new ArrayList<>();

        for (byte[] bytes : list) {
            terminalDeviceStatusList = objectMerging(terminalDeviceStatusList, getDeviceStatus(bytes));
        }

        Gson gson = new Gson();
        String json = gson.toJson(terminalDeviceStatusList);
        BaseBean bean = new BaseBean();
        bean.setType("getTerminalDeviceStatusList");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        com.orhanobut.logger.Logger.json(jsonResult);
        Log.i("jsonResult", "handler: " + jsonResult);
        Log.i("jsonResult", "size: " + terminalDeviceStatusList.size());
        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 通过byte数组获取Task对象
     *
     * @param bytes
     * @return Task对象
     */
    public List<TerminalDeviceStatus> getDeviceStatus(byte[] bytes) {
        List<TerminalDeviceStatus> list = new ArrayList<>();
        int itemSize = PLAY_STATUS_LENGTH + PRIORITY_LENGTH + CHANNEL_STATUS_LENGTH + TARGET_IP_LENGTH + TARGET_MAC_LENGTH;

        int head = HEAD_PACKAGE_LENGTH + DEVICE_SIZE_LENGTH;
        //去掉多余的数据
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        //遍历取出对象
        for (int i = 0; i <= bytes.length - itemSize; i += itemSize) {
            //byte游标
            int index = 0;
            TerminalDeviceStatus terminalDeviceStatus = new TerminalDeviceStatus();
            //设置播放状态
            terminalDeviceStatus.setPlayStatus(byteArrayToInt(subBytes(bytes, index + i, PLAY_STATUS_LENGTH)));
            index += PLAY_STATUS_LENGTH;
            //优先级类型
            terminalDeviceStatus.setPriority(byteArrayToInt(subBytes(bytes, index + i, PRIORITY_LENGTH)));
            index += PRIORITY_LENGTH;
            //设置通道状态
            terminalDeviceStatus.setChannelStatus(getWeekDuplicationPattern(subBytes(bytes, index + i, CHANNEL_STATUS_LENGTH)));
            index += CHANNEL_STATUS_LENGTH;
            terminalDeviceStatus.setTargetIp(SmartBroadCastUtils.hexstrToIp(bytesToHexString(subBytes(bytes, index + i, TARGET_IP_LENGTH))));
            index += TARGET_IP_LENGTH;
            terminalDeviceStatus.setTargetMac(SmartBroadCastUtils.hexstrToMac(bytesToHexString(subBytes(bytes, index + i, TARGET_MAC_LENGTH))));
            list.add(terminalDeviceStatus);
        }
        return list;
    }

    /**
     * 对象列表合并
     *
     * @param tasks1 对象列表1
     * @param tasks2 对象列表2
     * @return
     */

    private List<TerminalDeviceStatus> objectMerging(List<TerminalDeviceStatus> tasks1, List<TerminalDeviceStatus> tasks2) {

        List<TerminalDeviceStatus> taskList = new ArrayList<TerminalDeviceStatus>();
        for (TerminalDeviceStatus task : tasks1) {
            taskList.add(task);
        }
        for (TerminalDeviceStatus task : tasks2) {
            taskList.add(task);
        }
        return taskList;

    }


    /**
     * 发送获取终端状态列表命令
     */
    public static void sendCMD(String host) {


        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil( getTaskList(), host, false);
                NettyTcpClient.getInstance().sendPackageNotRetrySend(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackageNotRetrySend(host, getTaskList());
        }
    }

    /**
     * 获取终端状态
     *
     * @return
     */
    public static byte[] getTaskList() {
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("04BF");
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


    /**
     * 获取周模式
     *
     * @param b
     * @return
     */
    public int[] getWeekDuplicationPattern(byte[] b) {
        String taskWeekDuplicationPatternConver = conver2HexStr(b[0]);
        int[] taskWeekDuplicationPattern = new int[taskWeekDuplicationPatternConver.length()];
        int index = 0;
        for (int j = taskWeekDuplicationPattern.length - 1; j >= 0; j--) {
            taskWeekDuplicationPattern[index] = Integer.parseInt(String.valueOf(taskWeekDuplicationPatternConver.charAt(j)));
            index++;
        }
        return taskWeekDuplicationPattern;
    }

    /**
     * 获取日期模式数据
     *
     * @param b
     * @return
     */
    public String[] getDateDuplicationPattern(byte[] b) {

        String[] result = new String[b.length / 3];
        int index = 0;
        for (int i = 0; i <= b.length - 3; i += 3) {
            result[index] = byteToDate(subBytes(b, i, 3));
            index++;
        }
        return result;
    }
}
