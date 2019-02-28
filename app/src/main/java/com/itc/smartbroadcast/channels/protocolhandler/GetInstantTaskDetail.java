package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.InstantTaskDetail;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.List;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.conver2HexStr;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * 获取即时任务详情
 * Created by lik on 18-8-31.
 */

public class GetInstantTaskDetail {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int TASK_NUM_LENGTH = 2;                             //任务编号
    public final static int DEVICE_SIZE_LENGTH = 1;                          //任务编号
    public final static int DEVICE_MAC_LENGTH = 6;                           //设备mac地址
    public final static int DEVICE_ZONE_MSG_LENGTH = 2;                      //设备分区信息

    public static GetInstantTaskDetail init() {
        return new GetInstantTaskDetail();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {

        InstantTaskDetail instantTaskDetail = getInstantTaskDetail(list.get(0));

        Gson gson = new Gson();

        String json = gson.toJson(instantTaskDetail);

        BaseBean bean = new BaseBean();

        bean.setType("instantTaskDetail");

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
    public InstantTaskDetail getInstantTaskDetail(byte[] bytes) {

        //头部多余数
        int head = HEAD_PACKAGE_LENGTH;
        //去掉方案总数的字节
        byte [] data = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        InstantTaskDetail instantTaskDetail = new InstantTaskDetail();
        instantTaskDetail.setTaskNum(SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(data, 0, TASK_NUM_LENGTH)));
        instantTaskDetail.setDevicesList(getDeviceList(bytes));
        return instantTaskDetail;
    }

    /**
     * 获取设备列表
     *
     * @param bytes
     * @return
     */
    public List<InstantTaskDetail.Device> getDeviceList(byte[] bytes) {

        List<InstantTaskDetail.Device> deviceList = new ArrayList<>();
        int start = HEAD_PACKAGE_LENGTH + TASK_NUM_LENGTH + DEVICE_SIZE_LENGTH;
        int itemSize = DEVICE_MAC_LENGTH + DEVICE_ZONE_MSG_LENGTH;
        byte[] data = SmartBroadCastUtils.subBytes(bytes, start, (bytes.length - (start + END_PACKAGE_LENGTH)));

        for (int i = 0; i <= data.length - itemSize; i += itemSize) {
            InstantTaskDetail.Device device = new InstantTaskDetail.Device();
            //游标
            int index = 0;
            device.setDeviceMac(SmartBroadCastUtils.getMacAddress(SmartBroadCastUtils.subBytes(data, index + i, DEVICE_MAC_LENGTH)));
            index += DEVICE_MAC_LENGTH;
            device.setDeviceZoneMsg(getDeviceZoneMsg(SmartBroadCastUtils.subBytes(data, index + i, DEVICE_ZONE_MSG_LENGTH)));
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
     * 获取即时任务详情
     *
     * @param host ip地址
     * @param instantTaskDetail InstantTaskDetail
     */
    public static void sendCMD(String host, InstantTaskDetail instantTaskDetail) {

        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getInstantTaskDetailList(instantTaskDetail), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host,getInstantTaskDetailList(instantTaskDetail));
        }
    }

    /**
     * 获取定时任务详情
     *
     * @return
     */
    public static byte[] getInstantTaskDetailList(InstantTaskDetail instantTaskDetail) {

        String taskNum = SmartBroadCastUtils.intToUint16Hex(instantTaskDetail.getTaskNum());
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("02B4");
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

    }


}
