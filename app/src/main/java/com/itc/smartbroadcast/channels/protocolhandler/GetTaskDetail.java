package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.Task;
import com.itc.smartbroadcast.bean.TaskDetail;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.conver2HexStr;

/**
 * 获取定时器详情信息
 * Created by lik on 2018/8/25.
 */

public class GetTaskDetail {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int ALL_PACKAGE_LENGTH = 1;                          //总包数长度
    public final static int NOW_PACKAGE_LENGTH = 1;                          //当前包序列长度
    public final static int UPLOAD_TYPE_LENGTH = 1;                          //上传类型
    public final static int TASK_NUM_LENGTH = 2;                             //任务编号
    public final static int LIST_SIZE_LENGTH = 1;                            //曲目或设备总数
    public final static int DEVICE_ZONE_MSG_LENGTH = 2;                      //设备分区信息
    public final static int DEVICE_MAC_LENGTH = 6;                           //设备mac地址
    public final static int MUSIC_PATH_LENGTH = 32;                          //曲目PATH或URL
    public final static int MUSIC_NAME_LENGTH = 64;                          //曲目名称


    public static GetTaskDetail init() {
        return new GetTaskDetail();
    }


    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {

        List<byte[]> musicByteList = new ArrayList<>();
        List<byte[]> deviceByteList = new ArrayList<>();
        List<TaskDetail.Music> musicList = new ArrayList<>();
        List<TaskDetail.Device> deviceList = new ArrayList<>();
        TaskDetail taskDetail = new TaskDetail();
        //分包处理
        for (byte[] bytes : list) {
            if (judgeType(bytes) == 1) {  //曲目类型
                musicByteList.add(bytes);
            } else {  //设备类型
                deviceByteList.add(bytes);
            }
        }

        for (byte[] bytes : musicByteList) {
            musicList = musicObjectMerging(musicList, getMusicList(bytes));
        }
        for (byte[] bytes : deviceByteList) {
            deviceList = deviceObjectMerging(deviceList, getDeviceList(bytes));
        }
        taskDetail = getTaskDetail(list.get(0), musicList, deviceList);

        Gson gson = new Gson();
        String json = gson.toJson(taskDetail);
        BaseBean bean = new BaseBean();
        bean.setType("getTaskDetail");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        Log.i("jsonResult", "handler: " + jsonResult);
        Log.i("jsonResult", "musicSize: " + musicList.size());
        Log.i("jsonResult", "deviceList: " + deviceList.size());
        EventBus.getDefault().post(jsonResult);
    }


    /**
     * Music对象列表合并
     *
     * @param objects1 对象列表1
     * @param objects2 对象列表2
     * @return
     */

    private List<TaskDetail.Music> musicObjectMerging(List<TaskDetail.Music> objects1, List<TaskDetail.Music> objects2) {

        List<TaskDetail.Music> objList = new ArrayList<>();
        for (TaskDetail.Music obj : objects1) {
            objList.add(obj);
        }
        for (TaskDetail.Music obj : objects2) {
            objList.add(obj);
        }
        return objList;

    }

    /**
     * Music对象列表合并
     *
     * @param objects1 对象列表1
     * @param objects2 对象列表2
     * @return
     */

    private List<TaskDetail.Device> deviceObjectMerging(List<TaskDetail.Device> objects1, List<TaskDetail.Device> objects2) {

        List<TaskDetail.Device> objList = new ArrayList<>();
        for (TaskDetail.Device obj : objects1) {
            objList.add(obj);
        }
        for (TaskDetail.Device obj : objects2) {
            objList.add(obj);
        }
        return objList;

    }


    /**
     * 判断包的类型
     *
     * @param bytes
     * @return 0：设备类型，1：曲目类型
     */
    public int judgeType(byte[] bytes) {
        int start = HEAD_PACKAGE_LENGTH + ALL_PACKAGE_LENGTH + NOW_PACKAGE_LENGTH;
        return SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, start, UPLOAD_TYPE_LENGTH));
    }

    /**
     * 获取曲目列表
     *
     * @param bytes
     * @return
     */
    public List<TaskDetail.Music> getMusicList(byte[] bytes) {

        List<TaskDetail.Music> musicList = new ArrayList<>();

        int start = HEAD_PACKAGE_LENGTH + ALL_PACKAGE_LENGTH + NOW_PACKAGE_LENGTH + UPLOAD_TYPE_LENGTH + TASK_NUM_LENGTH + LIST_SIZE_LENGTH;
        int itemSize = MUSIC_NAME_LENGTH + MUSIC_PATH_LENGTH;
        byte[] data = SmartBroadCastUtils.subBytes(bytes, start, (bytes.length - (start + END_PACKAGE_LENGTH)));

        for (int i = 0; i <= data.length - itemSize; i += itemSize) {
            TaskDetail.Music music = new TaskDetail.Music();
            //游标
            int index = 0;
            Log.i("getMusicList", "getDeviceList: " + SmartBroadCastUtils.bytesToHexString(SmartBroadCastUtils.subBytes(data, index + i, MUSIC_PATH_LENGTH)));
            music.setMusicPath(SmartBroadCastUtils.byteToStr(SmartBroadCastUtils.subBytes(data, index + i, MUSIC_PATH_LENGTH)));
            index += MUSIC_PATH_LENGTH;
            music.setMusicName(SmartBroadCastUtils.byteToStr(SmartBroadCastUtils.subBytes(data, index + i, MUSIC_NAME_LENGTH)));
            musicList.add(music);
        }
        return musicList;
    }
    /**
     * 获取设备列表
     *
     * @param bytes
     * @return
     */
    public List<TaskDetail.Device> getDeviceList(byte[] bytes) {
        List<TaskDetail.Device> deviceList = new ArrayList<>();

        int start = HEAD_PACKAGE_LENGTH + ALL_PACKAGE_LENGTH + NOW_PACKAGE_LENGTH + UPLOAD_TYPE_LENGTH + TASK_NUM_LENGTH + LIST_SIZE_LENGTH;
        int itemSize = DEVICE_MAC_LENGTH + DEVICE_ZONE_MSG_LENGTH;
        byte[] data = SmartBroadCastUtils.subBytes(bytes, start, (bytes.length - (start + END_PACKAGE_LENGTH)));

        for (int i = 0; i <= data.length - itemSize; i += itemSize) {
            TaskDetail.Device device = new TaskDetail.Device();
            //游标
            int index = 0;
            device.setDeviceZoneMsg(getDeviceZoneMsg(SmartBroadCastUtils.subBytes(data, index + i, DEVICE_ZONE_MSG_LENGTH)));
            index += DEVICE_ZONE_MSG_LENGTH;
            device.setDeviceMac(SmartBroadCastUtils.getMacAddress(SmartBroadCastUtils.subBytes(data, index + i, DEVICE_MAC_LENGTH)));

            deviceList.add(device);
        }
        return deviceList;
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
     * 获取任务详情
     *
     * @param bytes
     * @param musicList  曲目列表
     * @param deviceList 设备列表
     * @return
     */
    public TaskDetail getTaskDetail(byte[] bytes, List<TaskDetail.Music> musicList, List<TaskDetail.Device> deviceList) {

        TaskDetail taskDetail = new TaskDetail();
        int start = HEAD_PACKAGE_LENGTH + ALL_PACKAGE_LENGTH + NOW_PACKAGE_LENGTH + UPLOAD_TYPE_LENGTH;
        int taskNum = SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, start, TASK_NUM_LENGTH));
        taskDetail.setTaskNum(taskNum);
        taskDetail.setDeviceSize(deviceList.size());
        taskDetail.setMusicSize(musicList.size());
        taskDetail.setMusicList(musicList);
        taskDetail.setDeviceList(deviceList);
        return taskDetail;
    }


    /**
     * 获取定时任务详情
     *
     * @param host ip地址
     * @param task task对象
     */
    public static void sendCMD(String host, Task task) {


        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getTaskDetailList(task), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host,getTaskDetailList(task));
        }

    }

    /**
     * 获取定时任务详情
     *
     * @return
     */
    public static byte[] getTaskDetailList(Task task) {


        String taskNum = SmartBroadCastUtils.intToUint16Hex(task.getTaskNum());

        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("04B3");
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
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //添加校验码
        cmdStr.append("" + SmartBroadCastUtils.checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");
        byte[] bytes = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return bytes;
//
//        byte[] bytes = SmartBroadCastUtils.HexStringtoBytes("AA 55 06 00 04 B3 FF FF FF FF FF FF AA BB 00 00 " + taskNum + " " + SmartBroadCastUtils.checkSum("06 00 04 B3 FF FF FF FF FF FF AA BB " + taskNum) + " 55 AA");
//
//        return bytes;
    }


}
