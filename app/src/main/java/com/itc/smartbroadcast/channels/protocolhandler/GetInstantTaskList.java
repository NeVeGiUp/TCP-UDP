package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.InstantTask;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteArrayToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToContinue;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToStr;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * 获取即时任务
 * Created by lik on 18-8-30.
 */

public class GetInstantTaskList {


    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int ALL_PACKAGE_LENGTH = 1;                          //总包数长度
    public final static int NOW_PACKAGE_LENGTH = 1;                          //当前包序列长度
    public final static int TASK_SIZE_LENGTH = 1;                            //任务数量
    public final static int TASK_ACCOUNT_NUM = 1;                            //账户编号
    public final static int TASK_NUM_LENGTH = 2;                             //任务编号
    public final static int TASK_NAME_LENGTH = 32;                           //任务名称
    public final static int TASK_TERMINAL_MAC = 6;                           //终端Mac地址
    public final static int TASK_PRIORITY_LENGTH = 1;                        //任务优先级
    public final static int TASK_VOLUME_LENGTH = 1;                          //任务音量
    public final static int TASK_CONTINUE_LENGTH = 3;                        //持续时间
    public final static int TASK_REMOTE_CONTROL_KEY_INFO = 1;                //遥控按键信息
    public final static int TASK_STATUS_LENGTH = 1;                          //即时任务状态

    public static GetInstantTaskList init() {
        return new GetInstantTaskList();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {

        List<InstantTask> instantTaskList = new ArrayList<>();

        for (byte[] bytes : list) {
            instantTaskList = objectMerging(instantTaskList, getTask(bytes));
        }

        Gson gson = new Gson();
        String json = gson.toJson(instantTaskList);
        BaseBean bean = new BaseBean();
        bean.setType("getInstantTaskList");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        Log.i("jsonResult", "handler: " + jsonResult);
        Log.i("jsonResult", "size: " + instantTaskList.size());
        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 通过byte数组获取InstantTask对象
     *
     * @param bytes
     * @return Task对象
     */
    public List<InstantTask> getTask(byte[] bytes) {
        List<InstantTask> list = new ArrayList<>();
        int itemSize = TASK_ACCOUNT_NUM + TASK_NUM_LENGTH + TASK_NAME_LENGTH + TASK_TERMINAL_MAC + TASK_PRIORITY_LENGTH +
                TASK_VOLUME_LENGTH + TASK_CONTINUE_LENGTH + TASK_REMOTE_CONTROL_KEY_INFO + TASK_STATUS_LENGTH;
        int head = HEAD_PACKAGE_LENGTH + ALL_PACKAGE_LENGTH + NOW_PACKAGE_LENGTH + TASK_SIZE_LENGTH;
        //去掉多余的数据
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        //遍历取出对象
        for (int i = 0; i <= bytes.length - itemSize; i += itemSize) {
            //byte游标
            int index = 0;
            InstantTask task = new InstantTask();
            //设置账户编号
            task.setAccountNum(byteToInt(bytes[index + i]));
            index += TASK_ACCOUNT_NUM;
            //设置任务编号
            task.setTaskNum(byteArrayToInt(subBytes(bytes, index + i, TASK_NUM_LENGTH)));
            index += TASK_NUM_LENGTH;
            //设置名称
            task.setTaskName(byteToStr(subBytes(bytes, index + i, TASK_NAME_LENGTH)));
            index += TASK_NAME_LENGTH;
            //设置终端Mac地址
            task.setTerminalMac(SmartBroadCastUtils.getMacAddress(subBytes(bytes, index + i, TASK_TERMINAL_MAC)));
            index += TASK_TERMINAL_MAC;
            //设置优先级
            task.setPriority(byteToInt(bytes[index + i]));
            index += TASK_PRIORITY_LENGTH;
            //设置音量
            task.setVolume(byteToInt(bytes[index + i]));
            index += TASK_VOLUME_LENGTH;
            //设置持续时间
            task.setContinueDate(byteToContinue(subBytes(bytes, index + i, TASK_CONTINUE_LENGTH)));
            index += TASK_CONTINUE_LENGTH;
            //遥控按键信息
            task.setRemoteControlKeyInfo(byteArrayToInt(subBytes(bytes, index + i, TASK_REMOTE_CONTROL_KEY_INFO)));
            index += TASK_REMOTE_CONTROL_KEY_INFO;
            task.setStatus(byteArrayToInt(subBytes(bytes, index + i, TASK_STATUS_LENGTH)));
            list.add(task);
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

    private List<InstantTask> objectMerging(List<InstantTask> tasks1, List<InstantTask> tasks2) {

        List<InstantTask> taskList = new ArrayList<InstantTask>();
        for (InstantTask task : tasks1) {
            taskList.add(task);
        }
        for (InstantTask task : tasks2) {
            taskList.add(task);
        }
        return taskList;
    }


    /**
     * 发送获取任务列表命令
     */
    public static void sendCMD(String host) {

        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getInstantTaskList(), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host,getInstantTaskList());
        }

    }

    /**
     * 获取任务列表
     *
     * @return
     */
    public static byte[] getInstantTaskList() {
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("00B4");
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
