package com.itc.smartbroadcast.channels.protocolhandler;


import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditTaskResult;
import com.itc.smartbroadcast.bean.Task;
import com.itc.smartbroadcast.bean.TaskDetail;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.itc.smartbroadcast.channels.protocolhandler.GetTaskDetail.ALL_PACKAGE_LENGTH;
import static com.itc.smartbroadcast.channels.protocolhandler.GetTaskDetail.DEVICE_MAC_LENGTH;
import static com.itc.smartbroadcast.channels.protocolhandler.GetTaskDetail.DEVICE_ZONE_MSG_LENGTH;
import static com.itc.smartbroadcast.channels.protocolhandler.GetTaskDetail.LIST_SIZE_LENGTH;
import static com.itc.smartbroadcast.channels.protocolhandler.GetTaskDetail.MUSIC_NAME_LENGTH;
import static com.itc.smartbroadcast.channels.protocolhandler.GetTaskDetail.MUSIC_PATH_LENGTH;
import static com.itc.smartbroadcast.channels.protocolhandler.GetTaskDetail.NOW_PACKAGE_LENGTH;
import static com.itc.smartbroadcast.channels.protocolhandler.GetTaskDetail.UPLOAD_TYPE_LENGTH;
import static com.itc.smartbroadcast.channels.protocolhandler.GetTaskList.TASK_NAME_LENGTH;
import static com.itc.smartbroadcast.channels.protocolhandler.GetTaskList.TASK_START_DATE_LENGTH;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.HexStringtoBytes;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.intToUint16Hex;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * 编辑定时任务
 * Created by lik on 2018/8/24.
 */

public class EditTask {


    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int TASK_NUM_LENGTH = 2;                             //任务编号
    public final static int RESULT = 1;                                      //结果


    private static int ETHERNET_SIZE = 1024;    //自定义以太网每个包最大发送1024k数据

    private static int EFFECTIVE_SIZE = ETHERNET_SIZE - (HEAD_PACKAGE_LENGTH + END_PACKAGE_LENGTH + ALL_PACKAGE_LENGTH + NOW_PACKAGE_LENGTH + UPLOAD_TYPE_LENGTH + TASK_NUM_LENGTH + LIST_SIZE_LENGTH);


    public static EditTask init() {
        return new EditTask();
    }


    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {

        EditTaskResult schemeResult = getTaskResult(list.get(0));

        Gson gson = new Gson();

        String json = gson.toJson(schemeResult);

        BaseBean bean = new BaseBean();

        bean.setType("editTaskResult");

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
    public EditTaskResult getTaskResult(byte[] bytes) {

        //头部多余数
        int head = HEAD_PACKAGE_LENGTH;
        //去掉方案总数的字节
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        EditTaskResult editTaskResult = new EditTaskResult();
        editTaskResult.setTaskNum(SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, 0, TASK_NUM_LENGTH)));

        editTaskResult.setResult(SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, TASK_NUM_LENGTH, RESULT)));

        return editTaskResult;
    }


    /**
     * 编辑定时任务
     *
     * @param host ip地址
     * @param task task对象
     * @param type 操作类型：
     *             （0：添加，1：修改，2：删除）
     */
    public static void sendCMD(String host, Task task, TaskDetail taskDetail, int type) {


        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {


                switch (type) {
                    case 0: //添加
                    {
                        List<byte[]> bytes = SmartBroadCastUtils.CloudUtil(getAddTask(task, taskDetail), host, false);
                        NettyTcpClient.getInstance().sendPackages(host, bytes);
                    }
                    break;
                    case 1: //修改
                    {
                        List<byte[]> bytes = SmartBroadCastUtils.CloudUtil(getEditTask(task, taskDetail), host, false);
                        NettyTcpClient.getInstance().sendPackages(host, bytes);
                    }
                    break;
                    case 2: //删除
                    {
                        List<byte[]> bytes = SmartBroadCastUtils.CloudUtil(getDeleteTask(task), host, false);
                        NettyTcpClient.getInstance().sendPackages(host, bytes);
                    }
                    break;
                    case 3: //克隆
                    {
                        List<byte[]> bytes = SmartBroadCastUtils.CloudUtil(getCopyTask(task), host, false);
                        NettyTcpClient.getInstance().sendPackages(host, bytes);
                    }
                    break;
                }
            }
        } else {
            switch (type) {
                case 0: //添加
                    NettyUdpClient.getInstance().sendPackages(host, getAddTask(task, taskDetail));
                    break;
                case 1: //修改
                    NettyUdpClient.getInstance().sendPackages(host, getEditTask(task, taskDetail));
                    break;
                case 2: //删除
                    NettyUdpClient.getInstance().sendPackages(host, getDeleteTask(task));
                    break;
                case 3: //克隆
                    NettyUdpClient.getInstance().sendPackages(host, getCopyTask(task));
                    break;
            }
        }


    }

    /**
     * 获取编辑方案的byte流
     *
     * @param task
     * @return
     */
    public static List<byte[]> getEditTask(Task task, TaskDetail taskDetail) {

        List<byte[]> resultList = new ArrayList<>();
        List<String> musicList = getMusicList(taskDetail);
        List<String> deviceList = getDeviceList(taskDetail);
        int index = 0;//记录当前包序号
        String taskNum = SmartBroadCastUtils.intToUint16Hex(taskDetail.getTaskNum());
        String packageSize = Integer.toHexString(musicList.size() + deviceList.size() + 1);
        packageSize = ((packageSize.length() == 1) ? "0" + packageSize : packageSize);
        //基本信息包
        {
            StringBuffer cmdStr = new StringBuffer();
            //添加起始标志
            cmdStr.append("AA55");
            //添加长度
            cmdStr.append("0000");
            //添加命令
            cmdStr.append("07B3");
            //添加本机Mac
            String mac = DeviceUtils.getMacAddress();
            cmdStr.append(mac.replace(":", ""));
            //添加控制ID
            cmdStr.append("000000000000");
            //添加云转发指令
            cmdStr.append("00");
            //保留字段
            cmdStr.append("000000000000000000");

            //添加任务编号
            cmdStr.append(taskNum);
            //添加总包数
            cmdStr.append((packageSize.length() == 1) ? ("0" + packageSize) : packageSize);
            //添加当前包序号
            String indexNow = Integer.toHexString(index);
            cmdStr.append((indexNow.length() == 1) ? ("0" + indexNow) : indexNow);
            //添加上传类型:设备
            cmdStr.append("02");

            //添加任务操作符
            cmdStr.append("02");
            //添加修改标志位为FF
            cmdStr.append("FF");
            //添加方案编号
            String schemeNum = Integer.toHexString(task.getSchemeNum());
            cmdStr.append((schemeNum.length() == 1) ? "0" + schemeNum : schemeNum);
            //添加任务编号
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
            //添加任务，任务状态默认为失效00

            String taskStatus = Integer.toHexString(task.getTaskStatus());
            cmdStr.append((taskStatus.length() == 1) ? "0" + taskStatus : taskStatus);
            //添加任务优先级，保留不使用
            String priority = Integer.toHexString(task.getTaskPriority());
            cmdStr.append((priority.length() == 1) ? "0" + priority : priority);
            //添加任务音量
            String schemeVolume = Integer.toHexString(task.getTaskVolume());
            cmdStr.append((schemeVolume.length() == 1) ? "0" + schemeVolume : schemeVolume);
            //添加任务重复模式
            String taskDuplicationPattern = Integer.toHexString(task.getTaskDuplicationPattern());
            cmdStr.append((taskDuplicationPattern.length() == 1) ? "0" + taskDuplicationPattern : taskDuplicationPattern);
            //添加任务周重复模式
            String taskWeekDuplicationPattern = getWeekDuplicationPattern(task.getTaskWeekDuplicationPattern());
            cmdStr.append((taskWeekDuplicationPattern.length() == 1) ? "0" + taskWeekDuplicationPattern : taskWeekDuplicationPattern);
            //添加任务日期重复模式
            String taskDateDuplicationPattern = getDateDuplicationPattern(task.getTaskDateDuplicationPattern());
            cmdStr.append((taskDateDuplicationPattern.length() == 1) ? "0" + taskDateDuplicationPattern : taskDateDuplicationPattern);
            //添加任务开始时间
            cmdStr.append(SmartBroadCastUtils.timeToHex(task.getTaskStartDate()));
            //添加持续时间
            cmdStr.append(SmartBroadCastUtils.continueToHex(task.getTaskContinueDate()));
            //添加播放模式
            String taskPlayMode = Integer.toHexString(task.getTaskPlayMode());
            cmdStr.append((taskPlayMode.length() == 1) ? "0" + taskPlayMode : taskPlayMode);
            //添加音乐总数
            String taskPlayTotal = Integer.toHexString(task.getTaskPlayTotal());
            cmdStr.append((taskPlayTotal.length() == 1) ? "0" + taskPlayTotal : taskPlayTotal);
            //修改长度
            cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
            //添加校验码
            cmdStr.append("" + SmartBroadCastUtils.checkSum(cmdStr.substring(4)));
            //添加结束标志
            cmdStr.append("55AA");

            byte[] bytes = HexStringtoBytes(cmdStr.toString());
            resultList.add(bytes);
            index++;
        }
        //设备信息包
        for (String deviceStr : deviceList) {
            StringBuffer dataBf = new StringBuffer();
            //添加起始标志
            dataBf.append("AA55");
            //添加长度
            dataBf.append("0000");
            //添加命令
            dataBf.append("07B3");
            //添加本机Mac
            String mac = DeviceUtils.getMacAddress();
            dataBf.append(mac.replace(":", ""));
            //添加控制ID
            dataBf.append("000000000000");
            //添加云转发指令
            dataBf.append("00");
            //保留字段
            dataBf.append("000000000000000000");
            //添加任务编号
            dataBf.append(taskNum);
            //添加总包数
            dataBf.append((packageSize.length() == 1) ? ("0" + packageSize) : packageSize);
            //添加当前包序号
            String indexNow = Integer.toHexString(index);
            dataBf.append((indexNow.length() == 1) ? ("0" + indexNow) : indexNow);
            //添加上传类型:设备
            dataBf.append("00");
            //添加设备总数
            String deviceSize = Integer.toHexString(((deviceStr.length() / 2) / (DEVICE_ZONE_MSG_LENGTH + DEVICE_MAC_LENGTH)));
            dataBf.append((deviceSize.length() == 1) ? ("0" + deviceSize) : deviceSize);
            //添加数据
            dataBf.append(deviceStr);
            //修改长度
            dataBf.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((dataBf.substring(4).length() + 4) / 2));
            //添加校验码
            dataBf.append("" + SmartBroadCastUtils.checkSum(dataBf.substring(4)));
            //添加结束标志
            dataBf.append("55AA");

            byte[] bytes = HexStringtoBytes(dataBf.toString());
            resultList.add(bytes);
            index++;
        }
        //音乐信息包
        for (String musicStr : musicList) {
            StringBuffer dataBf = new StringBuffer();
            //添加起始标志
            dataBf.append("AA55");
            //添加长度
            dataBf.append("0000");
            //添加命令
            dataBf.append("07B3");
            //添加本机Mac
            String mac = DeviceUtils.getMacAddress();
            dataBf.append(mac.replace(":", ""));
            //添加控制ID
            dataBf.append("000000000000");
            //添加云转发指令
            dataBf.append("00");
            //保留字段
            dataBf.append("000000000000000000");
            //添加任务编号
            dataBf.append(taskNum);
            //添加总包数
            dataBf.append((packageSize.length() == 1) ? ("0" + packageSize) : packageSize);
            //添加当前包序号
            String indexNow = Integer.toHexString(index);
            dataBf.append((indexNow.length() == 1) ? ("0" + indexNow) : indexNow);
            //添加上传类型:音乐
            dataBf.append("01");
            //添加音乐总数
            String deviceSize = Integer.toHexString(((musicStr.length() / 2) / (MUSIC_PATH_LENGTH + MUSIC_NAME_LENGTH)));
            dataBf.append((deviceSize.length() == 1) ? ("0" + deviceSize) : deviceSize);
            //添加数据
            dataBf.append(musicStr);
            //修改长度
            dataBf.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((dataBf.substring(4).length() + 4) / 2));
            //添加校验码
            dataBf.append("" + SmartBroadCastUtils.checkSum(dataBf.substring(4)));
            //添加结束标志
            dataBf.append("55AA");

            byte[] bytes = HexStringtoBytes(dataBf.toString());
            resultList.add(bytes);
            index++;
        }
        return resultList;
    }

    /**
     * 获取删除方案的byte流
     *
     * @param task
     * @return
     */
    public static List<byte[]> getCopyTask(Task task) {


        List<byte[]> resultList = new ArrayList<>();

        int index = 0;//记录当前包序号
        String packageSize = Integer.toHexString(1);
        packageSize = ((packageSize.length() == 1) ? "0" + packageSize : packageSize);

        String taskNum = SmartBroadCastUtils.intToUint16Hex(task.getTaskNum());
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("07B3");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");

        //添加任务编号
        cmdStr.append(taskNum);
        //添加总包数
        cmdStr.append((packageSize.length() == 1) ? ("0" + packageSize) : packageSize);
        //添加当前包序号
        String indexNow = Integer.toHexString(index);
        cmdStr.append((indexNow.length() == 1) ? ("0" + indexNow) : indexNow);
        //添加上传类型:任务基本信息
        cmdStr.append("02");

        //添加任务操作符
        cmdStr.append("03");
        //添加修改标志位为FF
        cmdStr.append("FF");
        //添加方案编号
        cmdStr.append("00");
        //添加任务编号

        cmdStr.append((taskNum.length() == 1) ? "0" + taskNum : taskNum);
        //获取任务名称的16进制
        String taskNameHex = "";
        int count = (TASK_NAME_LENGTH * 2);
        for (int i = 0; i < count; i++) {
            taskNameHex += "0";
        }

        //添加任务名称
        cmdStr.append(taskNameHex);
        //添加任务，任务状态默认为失效01
        cmdStr.append("01");
        //添加任务优先级，保留不使用
        cmdStr.append("00");
        //添加任务音量
        cmdStr.append("00");
        //添加任务重复模式
        cmdStr.append("00");
        //添加任务周重复模式
        cmdStr.append("00");
        //添加任务日期重复模式
        cmdStr.append("00");
        //添加任务开始时间
        cmdStr.append("000000");
        //添加持续时间
        cmdStr.append("000000");
        //添加播放模式
        cmdStr.append("00");
        //添加音乐总数
        cmdStr.append("00");
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //添加校验码
        cmdStr.append("" + SmartBroadCastUtils.checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");
        Log.i("msg", "getAddTask: " + cmdStr);
        byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());

        resultList.add(result);
        return resultList;
    }


    /**
     * 获取删除方案的byte流
     *
     * @param task
     * @return
     */
    public static List<byte[]> getDeleteTask(Task task) {


        List<byte[]> resultList = new ArrayList<>();

        int index = 0;//记录当前包序号
        String packageSize = Integer.toHexString(1);
        packageSize = ((packageSize.length() == 1) ? "0" + packageSize : packageSize);

        String taskNum = SmartBroadCastUtils.intToUint16Hex(task.getTaskNum());
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("07B3");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");

        //添加任务编号
        cmdStr.append(taskNum);
        //添加总包数
        cmdStr.append((packageSize.length() == 1) ? ("0" + packageSize) : packageSize);
        //添加当前包序号
        String indexNow = Integer.toHexString(index);
        cmdStr.append((indexNow.length() == 1) ? ("0" + indexNow) : indexNow);
        //添加上传类型:任务基本信息
        cmdStr.append("02");

        //添加任务操作符
        cmdStr.append("01");
        //添加修改标志位为FF
        cmdStr.append("FF");
        //添加方案编号
        cmdStr.append("00");
        //添加任务编号

        cmdStr.append((taskNum.length() == 1) ? "0" + taskNum : taskNum);
        //获取任务名称的16进制
        String taskNameHex = "";
        int count = (TASK_NAME_LENGTH * 2);
        for (int i = 0; i < count; i++) {
            taskNameHex += "0";
        }
        //添加任务名称
        cmdStr.append(taskNameHex);
        //添加任务，任务状态默认为失效01
        cmdStr.append("01");
        //添加任务优先级，保留不使用
        cmdStr.append("00");
        //添加任务音量
        cmdStr.append("00");
        //添加任务重复模式
        cmdStr.append("00");
        //添加任务周重复模式
        cmdStr.append("00");
        //添加任务日期重复模式
        cmdStr.append("00");
        //添加任务开始时间
        cmdStr.append("000000");
        //添加持续时间
        cmdStr.append("000000");
        //添加播放模式
        cmdStr.append("00");
        //添加音乐总数
        cmdStr.append("00");
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //添加校验码
        cmdStr.append("" + SmartBroadCastUtils.checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");
        Log.i("msg", "getAddTask: " + cmdStr);
        byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());

        resultList.add(result);
        return resultList;
    }

    /**
     * 获取添加定时任务的byte流
     *
     * @param task
     * @return
     */
    public static List<byte[]> getAddTask(Task task, TaskDetail taskDetail) {

        List<byte[]> resultList = new ArrayList<>();
        List<String> musicList = getMusicList(taskDetail);
        List<String> deviceList = getDeviceList(taskDetail);
        int index = 0;//记录当前包序号
        String taskNum = "0000";//任务编号
        String packageSize = Integer.toHexString(musicList.size() + deviceList.size() + 1);
        packageSize = (packageSize.length() == 1) ? "0" + packageSize : packageSize;
        //基本信息包
        {
            StringBuffer cmdStr = new StringBuffer();
            //添加头部
            //添加起始标志
            cmdStr.append("AA55");
            //添加长度
            cmdStr.append("0000");
            //添加命令
            cmdStr.append("07B3");
            //添加本机Mac
            String mac = DeviceUtils.getMacAddress();
            cmdStr.append(mac.replace(":", ""));
            //添加控制ID
            cmdStr.append("000000000000");
            //添加云转发指令
            cmdStr.append("00");
            //保留字段
            cmdStr.append("000000000000000000");

            //添加任务编号
            cmdStr.append(taskNum);
            //添加总包数
            cmdStr.append((packageSize.length() == 1) ? ("0" + packageSize) : packageSize);
            //添加当前包序号
            String indexNow = Integer.toHexString(index);
            cmdStr.append((indexNow.length() == 1) ? ("0" + indexNow) : indexNow);
            //添加上传类型:设备
            cmdStr.append("02");

            //添加任务操作符
            cmdStr.append("00");
            //添加修改标志位为FF
            cmdStr.append("FF");
            //添加方案编号
            String schemeNum = Integer.toHexString(task.getSchemeNum());
            cmdStr.append((schemeNum.length() == 1) ? "0" + schemeNum : schemeNum);
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
            //添加任务，任务状态默认为生效01
            cmdStr.append("01");
            //添加任务优先级，保留不使用
            String priority = Integer.toHexString(task.getTaskPriority());
            cmdStr.append((priority.length() == 1) ? "0" + priority : priority);
            //添加任务音量
            String schemeVolume = Integer.toHexString(task.getTaskVolume());
            cmdStr.append((schemeVolume.length() == 1) ? "0" + schemeVolume : schemeVolume);
            //添加任务重复模式
            String taskDuplicationPattern = Integer.toHexString(task.getTaskDuplicationPattern());
            cmdStr.append((taskDuplicationPattern.length() == 1) ? "0" + taskDuplicationPattern : taskDuplicationPattern);
            //添加任务周重复模式
            String taskWeekDuplicationPattern = getWeekDuplicationPattern(task.getTaskWeekDuplicationPattern());
            cmdStr.append((taskWeekDuplicationPattern.length() == 1) ? "0" + taskWeekDuplicationPattern : taskWeekDuplicationPattern);
            //添加任务日期重复模式
            String taskDateDuplicationPattern = getDateDuplicationPattern(task.getTaskDateDuplicationPattern());
            cmdStr.append((taskDateDuplicationPattern.length() == 1) ? "0" + taskDateDuplicationPattern : taskDateDuplicationPattern);
            //添加任务开始时间
            cmdStr.append(SmartBroadCastUtils.timeToHex(task.getTaskStartDate()));
            //添加持续时间
            cmdStr.append(SmartBroadCastUtils.continueToHex(task.getTaskContinueDate()));
            //添加播放模式
            String taskPlayMode = Integer.toHexString(task.getTaskPlayMode());
            cmdStr.append((taskPlayMode.length() == 1) ? "0" + taskPlayMode : taskPlayMode);
            //添加音乐总数
            String taskPlayTotal = Integer.toHexString(task.getTaskPlayTotal());
            cmdStr.append((taskPlayTotal.length() == 1) ? "0" + taskPlayTotal : taskPlayTotal);
            //修改长度
            cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
            //添加校验码
            cmdStr.append("" + SmartBroadCastUtils.checkSum(cmdStr.substring(4)));
            //添加结束标志
            cmdStr.append("55AA");

            byte[] bytes = HexStringtoBytes(cmdStr.toString());
            resultList.add(bytes);
            index++;
        }
        //设备信息包
        for (String deviceStr : deviceList) {
            StringBuffer dataBf = new StringBuffer();
            //添加起始标志
            dataBf.append("AA55");
            //添加长度
            dataBf.append("0000");
            //添加命令
            dataBf.append("07B3");
            //添加本机Mac
            String mac = DeviceUtils.getMacAddress();
            dataBf.append(mac.replace(":", ""));
            //添加控制ID
            dataBf.append("000000000000");
            //添加云转发指令
            dataBf.append("00");
            //保留字段
            dataBf.append("000000000000000000");
            //添加任务编号
            dataBf.append(taskNum);
            //添加总包数
            dataBf.append((packageSize.length() == 1) ? ("0" + packageSize) : packageSize);
            //添加当前包序号
            String indexNow = Integer.toHexString(index);
            dataBf.append((indexNow.length() == 1) ? ("0" + indexNow) : indexNow);
            //添加上传类型:设备
            dataBf.append("00");
            //添加设备总数
            String deviceSize = Integer.toHexString(((deviceStr.length() / 2) / (DEVICE_ZONE_MSG_LENGTH + DEVICE_MAC_LENGTH)));
            dataBf.append((deviceSize.length() == 1) ? ("0" + deviceSize) : deviceSize);
            //添加数据
            dataBf.append(deviceStr);
            //修改长度
            dataBf.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((dataBf.substring(4).length() + 4) / 2));
            //添加校验码
            dataBf.append("" + SmartBroadCastUtils.checkSum(dataBf.substring(4)));
            //添加结束标志
            dataBf.append("55AA");

            byte[] bytes = HexStringtoBytes(dataBf.toString());
            resultList.add(bytes);
            index++;
        }
        //音乐信息包
        for (String musicStr : musicList) {
            StringBuffer dataBf = new StringBuffer();
            //添加起始标志
            dataBf.append("AA55");
            //添加长度
            dataBf.append("0000");
            //添加命令
            dataBf.append("07B3");
            //添加本机Mac
            String mac = DeviceUtils.getMacAddress();
            dataBf.append(mac.replace(":", ""));
            //添加控制ID
            dataBf.append("000000000000");
            //添加云转发指令
            dataBf.append("00");
            //保留字段
            dataBf.append("000000000000000000");
            //添加任务编号
            dataBf.append(taskNum);
            //添加总包数
            dataBf.append((packageSize.length() == 1) ? ("0" + packageSize) : packageSize);
            //添加当前包序号
            String indexNow = Integer.toHexString(index);
            dataBf.append((indexNow.length() == 1) ? ("0" + indexNow) : indexNow);
            //添加上传类型:音乐
            dataBf.append("01");
            //添加音乐总数
            String deviceSize = Integer.toHexString(((musicStr.length() / 2) / (MUSIC_PATH_LENGTH + MUSIC_NAME_LENGTH)));
            dataBf.append((deviceSize.length() == 1) ? ("0" + deviceSize) : deviceSize);
            //添加数据
            dataBf.append(musicStr);
            //修改长度
            dataBf.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((dataBf.substring(4).length() + 4) / 2));
            //添加校验码
            dataBf.append("" + SmartBroadCastUtils.checkSum(dataBf.substring(4)));
            //添加结束标志
            dataBf.append("55AA");

            byte[] bytes = HexStringtoBytes(dataBf.toString());
            resultList.add(bytes);
            index++;
        }
        return resultList;
    }


    /**
     * 获取周模式
     *
     * @param ints
     * @return
     */
    public static String getWeekDuplicationPattern(int[] ints) {
        int data = 0;
        for (int i = 0; i < 7; i++) {
            data += ints[i] * Math.pow(2, i);
        }
        return Integer.toHexString(data);
    }

    /**
     * 获取日期模式
     *
     * @param strArr
     * @return
     */
    public static String getDateDuplicationPattern(String[] strArr) {
        String str = "";
        for (int i = 0; i < strArr.length; i++) {
            str += SmartBroadCastUtils.dateToHex(strArr[i]);
        }
        if (strArr.length > 10) {
            str = str.substring(0, TASK_START_DATE_LENGTH * 2 * 10);
        } else {
            for (int i = 0; i < (10 - strArr.length); i++) {
                str += "FFFFFF";
            }
        }
        return str;
    }


    /**
     * 获取音乐的包集合
     *
     * @param taskDetail
     * @return
     */
    public static List<String> getMusicList(TaskDetail taskDetail) {
        int size = EFFECTIVE_SIZE / (MUSIC_PATH_LENGTH + MUSIC_NAME_LENGTH);
        List<String> result = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        for (int j = 0; j < (taskDetail.getMusicList().size() / size + 1); j++) {
            for (int i = j * size; i < (taskDetail.getMusicList().size()); i++) {
                sb.append(getMusic(taskDetail.getMusicList().get(i)));
                if (i == (((j + 1) * size) - 1)) {
                    break;
                }
            }
            result.add(sb.toString());
            sb = new StringBuffer();
        }
        return result;
    }

    /**
     * 获取音乐Hex
     *
     * @param music
     * @return
     */
    public static String getMusic(TaskDetail.Music music) {

        //获取音乐名称的16进制
        String musicName = SmartBroadCastUtils.str2HexStr(music.getMusicName());
        if (musicName.length() > MUSIC_NAME_LENGTH * 2) {
            musicName = musicName.substring(0, MUSIC_NAME_LENGTH * 2);
        } else {
            int len = musicName.length();
            int count = (MUSIC_NAME_LENGTH * 2) - musicName.length();
            for (int i = 0; i < count; i++) {
                musicName += "0";
            }
        }
        //获取音乐路径的16进制
        String music_path = SmartBroadCastUtils.str2HexStr(music.getMusicPath());
        if (music_path.length() > MUSIC_PATH_LENGTH * 2) {
            music_path = music_path.substring(0, MUSIC_PATH_LENGTH * 2);
        } else {
            int len = music_path.length();
            int count = (MUSIC_PATH_LENGTH * 2) - music_path.length();
            for (int i = 0; i < count; i++) {
                music_path += "0";
            }
        }
        return (music_path + musicName);
    }

    /**
     * 获取设备集合包
     *
     * @param taskDetail
     * @return
     */
    public static List<String> getDeviceList(TaskDetail taskDetail) {
        int size = EFFECTIVE_SIZE / (DEVICE_ZONE_MSG_LENGTH + DEVICE_MAC_LENGTH);
        List<String> result = new ArrayList<>();
        StringBuffer sb = new StringBuffer();
        for (int j = 0; j < (((taskDetail.getDeviceList().size())) / size + 1); j++) {
            for (int i = j * size; i < (taskDetail.getDeviceList().size()); i++) {
                sb.append(getDevice(taskDetail.getDeviceList().get(i)));
                if (i == (((j + 1) * size) - 1)) {
                    break;
                }
            }
            result.add(sb.toString());
            sb = new StringBuffer();
        }
        return result;
    }

    /**
     * 获取设备Hex
     *
     * @param device
     * @return
     */
    public static String getDevice(TaskDetail.Device device) {

        String deviceZoneMsg = getDeviceZoneMsg(device.getDeviceZoneMsg());
        String deviceMac = getDeviceMac(device.getDeviceMac());
        return (deviceZoneMsg + deviceMac);
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

//    //int转uint16Hex
//    public static String intToUint16Hex(int date) {
//        String str = Integer.toHexString(date);
//        int len = (4 - str.length());
//        for (int i = 0; i < len; i++) {
//            str = "0" + str;
//        }
//        return str;
//    }

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
