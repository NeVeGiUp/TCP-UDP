package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditTaskDetailResult;
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
import static com.itc.smartbroadcast.channels.protocolhandler.GetTaskDetail.TASK_NUM_LENGTH;
import static com.itc.smartbroadcast.channels.protocolhandler.GetTaskDetail.UPLOAD_TYPE_LENGTH;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.HexStringtoBytes;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * 编辑任务详情
 * Created by lik on 2018/8/27.
 */

public class EditTaskDetail {


    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int RESULT = 1;                                      //结果

    private static int ETHERNET_SIZE = 1024;    //自定义以太网每个包最大发送1024k数据

    private static int EFFECTIVE_SIZE = ETHERNET_SIZE - (HEAD_PACKAGE_LENGTH + END_PACKAGE_LENGTH + ALL_PACKAGE_LENGTH + NOW_PACKAGE_LENGTH + UPLOAD_TYPE_LENGTH + TASK_NUM_LENGTH + LIST_SIZE_LENGTH);


    public static EditTaskDetail init() {
        return new EditTaskDetail();
    }


    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {

        EditTaskDetailResult editTaskDetailResult = getEditTaskDetailResult(list.get(0));

        Gson gson = new Gson();

        String json = gson.toJson(editTaskDetailResult);

        BaseBean bean = new BaseBean();

        bean.setType("editTaskDetailResult");

        bean.setData(json);

        String jsonResult = gson.toJson(bean);

        Log.i("jsonResult", "handler: " + jsonResult);

        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 获取编辑任务详情结果
     *
     * @param bytes
     * @return
     */
    public EditTaskDetailResult getEditTaskDetailResult(byte[] bytes) {

        //头部多余数
        int head = HEAD_PACKAGE_LENGTH;
        //去掉方案总数的字节
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        EditTaskDetailResult editTaskDetailResult = new EditTaskDetailResult();
        if (SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, 0, RESULT)) > 0) {
            editTaskDetailResult.setResult(1);
        } else {
            editTaskDetailResult.setResult(0);
        }

        return editTaskDetailResult;
    }

    /**
     * 编辑定时任务详情
     *
     * @param host       ip地址
     * @param taskDetail taskDetail对象
     * @param type       操作类型：
     *                   （0：添加，1：修改，2：删除）
     */
    public static void sendCMD(String host, TaskDetail taskDetail, int type) {


        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                switch (type) {
                    case 0: //添加
                        List<byte[]> bytes = SmartBroadCastUtils.CloudUtil(getAddTaskDetail(taskDetail), host, false);
                        NettyTcpClient.getInstance().sendPackages(host, bytes);
                        break;
                }
            }
        } else {
            switch (type) {
                case 0: //添加
                    NettyUdpClient.getInstance().sendPackages(host, getAddTaskDetail(taskDetail));
                    break;
            }
        }


    }

    public static List<byte[]> getAddTaskDetail(TaskDetail taskDetail) {

        List<byte[]> resultList = new ArrayList<>();
        List<String> musicList = getMusicList(taskDetail);
        List<String> deviceList = getDeviceList(taskDetail);
        int index = 0;//记录当前包序号
        String taskNum = SmartBroadCastUtils.intToUint16Hex(taskDetail.getTaskNum());
        String packageSize = Integer.toHexString(musicList.size() + deviceList.size());
        packageSize = (packageSize.length() == 1) ? "0" + packageSize : packageSize;
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
        return intToUint16Hex(count);
    }

    //int转uint16Hex
    public static String intToUint16Hex(int date) {
        String str = Integer.toHexString(date);
        int len = (4 - str.length());
        for (int i = 0; i < len; i++) {
            str = "0" + str;
        }
        return str;
    }

    /**
     * 获取Mac地址信息
     *
     * @param mac
     * @return
     */
    public static String getDeviceMac(String mac) {

        String[] macItems = mac.split("-");
        String result = "";
        for (String macItem : macItems) {
            result += macItem;
        }
        return result;
    }


}
