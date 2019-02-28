package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.Task;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteArrayToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToContinue;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToDate;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToStr;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToTime;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.conver2HexStr;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * 获取任务列表
 * Created by lik on 2018/8/22.
 */

public class GetTaskList {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int ALL_PACKAGE_LENGTH = 1;                          //总包数长度
    public final static int NOW_PACKAGE_LENGTH = 1;                          //当前包序列长度
    public final static int TASK_SIZE_LENGTH = 1;                            //任务数量
    public final static int SCHEME_NUM_LENGTH = 1;                           //方案编号
    public final static int TASK_NUM_LENGTH = 2;                             //任务编号
    public final static int TASK_NAME_LENGTH = 32;                           //任务名称
    public final static int TASK_STATUS_LENGTH = 1;                          //任务状态
    public final static int TASK_PRIORITY_LENGTH = 1;                        //任务优先级
    public final static int TASK_VOLUME_LENGTH = 1;                          //任务音量
    public final static int TASK_DUPLICATION_PATTERN_LENGTH = 1;             //重复模式
    public final static int TASK_WEEK_DUPLICATION_PATTERN_LENGTH = 1;        //按周重复模式
    public final static int TASK_DATE_DUPLICATION_PATTERN_LENGTH = 10 * 3;   //按日期重复模式
    public final static int TASK_START_DATE_LENGTH = 3;                       //开始时间
    public final static int TASK_CONTINUE_LENGTH = 3;                        //持续时间
    public final static int TASK_PLAY_MODE_LENGTH = 1;                       //播放模式
    public final static int TASK_PLAY_TOTAL_LENGTH = 1;                      //播放曲目总数
    public final static int TASK_TEST_STATUS_LENGTH = 1;                     //任务测试状态

    public static GetTaskList init() {
        return new GetTaskList();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {

        List<Task> taskList = new ArrayList<>();

        for (byte[] bytes : list) {
            taskList = objectMerging(taskList, getTask(bytes));
        }

        Gson gson = new Gson();
        String json = gson.toJson(taskList);
        BaseBean bean = new BaseBean();
        bean.setType("getTaskList");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        com.orhanobut.logger.Logger.json(jsonResult);
        Log.i("jsonResult", "handler: " + jsonResult);
        Log.i("jsonResult", "size: " + taskList.size());
        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 通过byte数组获取Task对象
     *
     * @param bytes
     * @return Task对象
     */
    public List<Task> getTask(byte[] bytes) {
        List<Task> list = new ArrayList<>();
        int itemSize = SCHEME_NUM_LENGTH + TASK_NUM_LENGTH + TASK_NAME_LENGTH + TASK_STATUS_LENGTH + TASK_PRIORITY_LENGTH + TASK_VOLUME_LENGTH +
                TASK_DUPLICATION_PATTERN_LENGTH + TASK_WEEK_DUPLICATION_PATTERN_LENGTH + TASK_DATE_DUPLICATION_PATTERN_LENGTH + TASK_START_DATE_LENGTH +
                TASK_CONTINUE_LENGTH + TASK_PLAY_MODE_LENGTH + TASK_PLAY_TOTAL_LENGTH + TASK_TEST_STATUS_LENGTH;
        int head = HEAD_PACKAGE_LENGTH + ALL_PACKAGE_LENGTH + NOW_PACKAGE_LENGTH + TASK_SIZE_LENGTH;

        //去掉多余的数据
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        //遍历取出对象
        for (int i = 0; i <= bytes.length - itemSize; i += itemSize) {

            //byte游标
            int index = 0;
            Task task = new Task();
            //设置方案编号
            task.setSchemeNum(byteToInt(bytes[index + i]));
            index += SCHEME_NUM_LENGTH;
            //设置任务编号
            task.setTaskNum(byteArrayToInt(subBytes(bytes, index + i, TASK_NUM_LENGTH)));
            index += TASK_NUM_LENGTH;
            //设置名称
            task.setTaskName(byteToStr(subBytes(bytes, index + i, TASK_NAME_LENGTH)));
            index += TASK_NAME_LENGTH;
            //设置状态
            task.setTaskStatus(byteToInt(bytes[index + i]));
            index += TASK_STATUS_LENGTH;
            //设置优先级
            task.setTaskPriority(byteToInt(bytes[index + i]));
            index += TASK_PRIORITY_LENGTH;
            //设置音量
            task.setTaskVolume(byteToInt(bytes[index + i]));
            index += TASK_VOLUME_LENGTH;
            //设置循环模式
            task.setTaskDuplicationPattern(byteToInt(bytes[index + i]));
            index += TASK_DUPLICATION_PATTERN_LENGTH;
            //设置周模式
            task.setTaskWeekDuplicationPattern(getWeekDuplicationPattern(subBytes(bytes, index + i, TASK_WEEK_DUPLICATION_PATTERN_LENGTH)));
            index += TASK_WEEK_DUPLICATION_PATTERN_LENGTH;
            //设置日期模式
            task.setTaskDateDuplicationPattern(getDateDuplicationPattern(subBytes(bytes, index + i, TASK_DATE_DUPLICATION_PATTERN_LENGTH)));
            index += TASK_DATE_DUPLICATION_PATTERN_LENGTH;
            //设置开始时间
            task.setTaskStartDate(byteToTime(subBytes(bytes, index + i, TASK_START_DATE_LENGTH)));
            index += TASK_START_DATE_LENGTH;
            //设置持续时间
            task.setTaskContinueDate(byteToContinue(subBytes(bytes, index + i, TASK_CONTINUE_LENGTH)));
            index += TASK_CONTINUE_LENGTH;
            //设置播放模式
            task.setTaskPlayMode(byteArrayToInt(subBytes(bytes, index + i, TASK_PLAY_MODE_LENGTH)));
            index += TASK_PLAY_MODE_LENGTH;
            //设置播放总数
            task.setTaskPlayTotal(byteArrayToInt(subBytes(bytes, index + i, TASK_PLAY_TOTAL_LENGTH)));
            index += TASK_PLAY_TOTAL_LENGTH;
            //设置任务测试状态
            task.setTaskTestStatus(byteArrayToInt(subBytes(bytes, index + i, TASK_TEST_STATUS_LENGTH)));
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

    private List<Task> objectMerging(List<Task> tasks1, List<Task> tasks2) {

        List<Task> taskList = new ArrayList<Task>();
        for (Task task : tasks1) {
            taskList.add(task);
        }
        for (Task task : tasks2) {
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
                byte[] bytes = SmartBroadCastUtils.CloudUtil( getTaskList(), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host, getTaskList());
        }

    }

    /**
     * 获取任务列表
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
        cmdStr.append("01B3");
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
