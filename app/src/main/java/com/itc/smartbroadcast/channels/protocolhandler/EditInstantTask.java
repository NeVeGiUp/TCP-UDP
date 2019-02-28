package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditInstantTaskResult;
import com.itc.smartbroadcast.bean.InstantTask;
import com.itc.smartbroadcast.bean.InstantTaskDetail;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.itc.smartbroadcast.channels.protocolhandler.GetInstantTaskList.TASK_NAME_LENGTH;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.continueToHex;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.intToUint16Hex;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * 编辑即时任务
 * Created by lik on 18-8-30.
 */

public class EditInstantTask {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int TASK_NUM_LENGTH = 2;                             //任务编号
    public final static int RESULT = 1;                                      //结果
    public final static int TASK_STATUS_LENGTH = 1;                          //即时任务状态

    public static EditInstantTask init() {
        return new EditInstantTask();
    }


    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {

        EditInstantTaskResult editInstantTaskResult = getTaskResult(list.get(0));

        Gson gson = new Gson();

        String json = gson.toJson(editInstantTaskResult);

        BaseBean bean = new BaseBean();

        bean.setType("editInstantTaskResult");

        bean.setData(json);

        String jsonResult = gson.toJson(bean);

        Log.i("jsonResult", "handler: " + jsonResult);

        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 获取编辑任务结果
     *
     * @param bytes
     * @return
     */
    public EditInstantTaskResult getTaskResult(byte[] bytes) {

        //头部多余数
        int head = HEAD_PACKAGE_LENGTH;
        //去掉方案总数的字节
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        EditInstantTaskResult editTaskResult = new EditInstantTaskResult();
        editTaskResult.setTaskNum(SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, 0, TASK_NUM_LENGTH)));
        if (SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, TASK_NUM_LENGTH, RESULT)) > 0) {
            editTaskResult.setResult(1);
        } else {
            editTaskResult.setResult(0);
        }
        return editTaskResult;
    }


    /**
     * 编辑即时任务
     *
     * @param host ip地址
     * @param task task对象
     * @param type 操作类型：
     *             （0：添加，1：修改，2：删除）
     */
    public static void sendCMD(String host, InstantTask task, InstantTaskDetail instantTaskDetail, int type) {


        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {

                switch (type) {
                    case 0: //添加
                    {
                        byte[] bytes = SmartBroadCastUtils.CloudUtil(getAddTask(task, instantTaskDetail), host, false);
                        NettyTcpClient.getInstance().sendPackage(host, bytes);
                    }
                    break;
                    case 1: //修改
                    {
                        byte[] bytes = SmartBroadCastUtils.CloudUtil(getEditTask(task, instantTaskDetail), host, false);
                        NettyTcpClient.getInstance().sendPackage(host, bytes);
                    }
                    break;
                    case 2: //删除
                    {
                        byte[] bytes = SmartBroadCastUtils.CloudUtil(getDeleteTask(task), host, false);
                        NettyTcpClient.getInstance().sendPackage(host, bytes);
                    }
                    break;
                }
            }
        } else {
            switch (type) {
                case 0: //添加
                    NettyUdpClient.getInstance().sendPackage(host, getAddTask(task, instantTaskDetail));
                    break;
                case 1: //修改
                    NettyUdpClient.getInstance().sendPackage(host, getEditTask(task, instantTaskDetail));
                    break;
                case 2: //删除
                    NettyUdpClient.getInstance().sendPackage(host, getDeleteTask(task));
                    break;
            }
        }
    }

    /**
     * 获取添加定时任务的byte流
     *
     * @param task
     * @return
     */
    public static byte[] getDeleteTask(InstantTask task) {

        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("03B4");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //添加任务操作符
        cmdStr.append("01");
        //添加账户编号
        String userNum = Integer.toHexString(task.getAccountNum());
        cmdStr.append((userNum.length() == 1) ? "0" + userNum : userNum);
        //添加任务编号
        String taskNum = SmartBroadCastUtils.intToUint16Hex(task.getTaskNum());
        cmdStr.append(taskNum);
        //获取任务名称的16进制
        String taskNameHex = "";
        int count = (TASK_NAME_LENGTH * 2);
        for (int i = 0; i < count; i++) {
            taskNameHex += "0";
        }
        //添加任务名称
        cmdStr.append(taskNameHex);
        //添加终端Mac
        cmdStr.append("000000000000");
        //添加任务优先级，保留不使用
        cmdStr.append("00");
        //添加任务音量
        cmdStr.append("00");
        //添加持续时间
        cmdStr.append("000000");
        //添加遥控按键信息
        cmdStr.append("00");
        //添加设备总数
        cmdStr.append("00");
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //添加校验码
        cmdStr.append("" + SmartBroadCastUtils.checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");
        Log.i("msg", "sendInstant: " + cmdStr);
        byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return result;
    }

    /**
     * 获取编辑定时任务的byte流
     *
     * @param task
     * @return
     */
    public static byte[] getEditTask(InstantTask task, InstantTaskDetail instantTaskDetail) {

        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("03B4");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //添加任务操作符
        cmdStr.append("02");
        //添加账户编号
        String accountNum = Integer.toHexString(task.getAccountNum());
        cmdStr.append((accountNum.length() == 1) ? "0" + accountNum : accountNum);
        //添加任务编号
        String taskNum = SmartBroadCastUtils.intToUint16Hex(task.getTaskNum());
        cmdStr.append(taskNum);
        //获取任务名称的16进制
        String taskNameHex = SmartBroadCastUtils.str2HexStr(task.getTaskName());
        if (taskNameHex.length() > TASK_NAME_LENGTH * 2) {
            taskNameHex = taskNameHex.substring(0, TASK_NAME_LENGTH * 2);
        } else {
            int len = taskNameHex.length();
            int count = (TASK_NAME_LENGTH * 2) - taskNameHex.length();
            for (int i = 0; i < count; i++) {
                taskNameHex += "0";
            }
        }
        //添加任务名称
        cmdStr.append(taskNameHex);
        //添加终端Mac
        cmdStr.append(getDeviceMac(task.getTerminalMac()));
        //添加任务优先级，保留不使用
        String priority = Integer.toHexString(task.getPriority());
        cmdStr.append((priority.length() == 1) ? "0" + priority : priority);
        //添加任务音量
        String schemeVolume = Integer.toHexString(task.getVolume());
        cmdStr.append((schemeVolume.length() == 1) ? "0" + schemeVolume : schemeVolume);
        //添加持续时间
        cmdStr.append(continueToHex(task.getContinueDate()));
        //添加遥控按键信息
        String remoteControlKeyInfo = Integer.toHexString(task.getRemoteControlKeyInfo());
        cmdStr.append((remoteControlKeyInfo.length() == 1) ? "0" + remoteControlKeyInfo : remoteControlKeyInfo);
        //添加设备总数
        String deviceSize = Integer.toHexString(instantTaskDetail.getDevicesList().size());
        cmdStr.append((deviceSize.length() == 1) ? "0" + deviceSize : deviceSize);
        //添加设备
        cmdStr.append(getDeviceList(instantTaskDetail));
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //添加校验码
        cmdStr.append("" + SmartBroadCastUtils.checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");
        Log.i("msg", "sendInstant: " + cmdStr);
        byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return result;
    }


    /**
     * 获取添加定时任务的byte流
     *
     * @param task
     * @return
     */
    public static byte[] getAddTask(InstantTask task, InstantTaskDetail instantTaskDetail) {

        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("03B4");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //添加任务操作符
        cmdStr.append("00");
        //添加账户编号
        String accountNum = Integer.toHexString(task.getAccountNum());
        cmdStr.append((accountNum.length() == 1) ? "0" + accountNum : accountNum);
        //添加任务编号，添加操作为0000
        cmdStr.append("0000");
        //获取任务名称的16进制
        String taskNameHex = SmartBroadCastUtils.str2HexStr(task.getTaskName());
        if (taskNameHex.length() > TASK_NAME_LENGTH * 2) {
            taskNameHex = taskNameHex.substring(0, TASK_NAME_LENGTH * 2);
        } else {
            int len = taskNameHex.length();
            int count = (TASK_NAME_LENGTH * 2) - taskNameHex.length();
            for (int i = 0; i < count; i++) {
                taskNameHex += "0";
            }
        }
        //添加任务名称
        cmdStr.append(taskNameHex);
        //添加终端Mac
        cmdStr.append(getDeviceMac(task.getTerminalMac()));
        //添加任务优先级，保留不使用
        String priority = Integer.toHexString(task.getPriority());
        cmdStr.append((priority.length() == 1) ? "0" + priority : priority);
        //添加任务音量
        String schemeVolume = Integer.toHexString(task.getVolume());
        cmdStr.append((schemeVolume.length() == 1) ? "0" + schemeVolume : schemeVolume);
        //添加持续时间
        cmdStr.append(continueToHex(task.getContinueDate()));
        //添加遥控按键信息
        String remoteControlKeyInfo = Integer.toHexString(task.getRemoteControlKeyInfo());
        cmdStr.append((remoteControlKeyInfo.length() == 1) ? "0" + remoteControlKeyInfo : remoteControlKeyInfo);
        //添加设备总数
        String deviceSize = Integer.toHexString(instantTaskDetail.getDevicesList().size());
        cmdStr.append((deviceSize.length() == 1) ? "0" + deviceSize : deviceSize);
        //添加设备
        cmdStr.append(getDeviceList(instantTaskDetail));
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //添加校验码
        cmdStr.append("" + SmartBroadCastUtils.checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");
        Log.i("msg", "sendInstant: " + cmdStr);
        byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return result;
    }


    /**
     * 获取设备集合
     *
     * @param instantTaskDetail
     * @return
     */
    public static String getDeviceList(InstantTaskDetail instantTaskDetail) {
        StringBuffer sb = new StringBuffer();
        for (InstantTaskDetail.Device device : instantTaskDetail.getDevicesList()) {
            sb.append(getDevice(device));
        }
        String result = sb.toString();
        return result;
    }

    /**
     * 获取设备Hex
     *
     * @param device
     * @return
     */
    public static String getDevice(InstantTaskDetail.Device device) {

        String deviceZoneMsg = getDeviceZoneMsg(device.getDeviceZoneMsg());
        String deviceMac = getDeviceMac(device.getDeviceMac());
        return (deviceMac + deviceZoneMsg);
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

    /**
     * 获取分区信息
     *
     * @param data
     * @return
     */
    public static String getDeviceZoneMsg(int[] data) {

        int count = 0;
        int index = 0;
        for (int i : data) {
            count += (i * Math.pow(2, index));
            index++;
        }
        String re = intToUint16Hex(count);
        return re;
    }


}
