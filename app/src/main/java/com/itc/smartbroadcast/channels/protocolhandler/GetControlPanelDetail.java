package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.ControlPanelInfo;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.bytesToHexString;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.checkSum;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.hexstrToIp;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.hexstrToMac;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * author： lghandroid
 * created：2018/9/7 16:09
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: _查询控制面板详细信息
 */

public class GetControlPanelDetail {

    private static final int HEAD_PACKET_LENGTH = 28;                            //包头
    private static final int END_PACKET_LENGTH = 4;                              //包尾
    private static final int BIND_DEVICE_COUNT_LENGTH = 1;                       //绑定设备总数
    private static final int BIND_DEVICE_IP_LENGTH = 4;                          //绑定设备IP
    private static final int BIND_DEVICE_MAC_LENGTH = 6;                         //绑定设备MAC

    public static GetControlPanelDetail init() {
        return new GetControlPanelDetail();
    }


    /**
     * 业务处理
     *
     * @param list 获取控制面板详细信息
     */
    public void handler(List<byte[]> list) {
        ControlPanelInfo controlPanelInfo = getControlPanelDetail(list.get(0));
        Gson gson = new Gson();
        String json = gson.toJson(controlPanelInfo);
        BaseBean bean = new BaseBean();
        bean.setType("getControlPanelDetail");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        Log.i("jsonResult", "handler: " + jsonResult);
        Log.i("jsonResult", "deviceSize: " + controlPanelInfo.getDeviceMsgList().size());
        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 通过byte数组获取ControlPanelInfo对象
     *
     * @param bytes
     * @return ControlPanelInfo对象
     */
    public ControlPanelInfo getControlPanelDetail(byte[] bytes) {
        ControlPanelInfo controlPanelInfo = new ControlPanelInfo();
        List<ControlPanelInfo.DeviceMsgInner> deviceMsgList = new ArrayList<>();
        //获取有效字节数
        bytes = subBytes(bytes, HEAD_PACKET_LENGTH, bytes.length - END_PACKET_LENGTH - HEAD_PACKET_LENGTH);
        //绑定设备总数
        int bindDeviceCount = byteToInt(subBytes(bytes, 0, BIND_DEVICE_COUNT_LENGTH)[0]);
        //一个终端信息item所占字节数
        int itemBt = BIND_DEVICE_MAC_LENGTH;
        byte[] deviceMsgListBt = SmartBroadCastUtils.subBytes(bytes, BIND_DEVICE_COUNT_LENGTH, itemBt * bindDeviceCount);
        for (int i = 0; i < deviceMsgListBt.length; i += itemBt) {
            ControlPanelInfo.DeviceMsgInner deviceMsg = new ControlPanelInfo.DeviceMsgInner();
            String deviceMac = hexstrToMac(bytesToHexString(subBytes(deviceMsgListBt, i, BIND_DEVICE_MAC_LENGTH)));
            deviceMsg.setBindDeviceMac(deviceMac);
            deviceMsgList.add(deviceMsg);
        }
        controlPanelInfo.setBingDeviceCount(bindDeviceCount);
        controlPanelInfo.setDeviceMsgList(deviceMsgList);
        return controlPanelInfo;
    }

    /**
     * 发送获取控制面板详情指令包
     */
    public static void sendCMD(String host) {
        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getControlPanelDetailCmd(), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host, getControlPanelDetailCmd());
        }
    }


    /**
     * 获取控制面板详情发送指令
     *
     * @return
     */
    public static byte[] getControlPanelDetailCmd() {
        StringBuffer cmdStr = new StringBuffer();
        //起始标志
        cmdStr.append("AA55");
        //长度
        cmdStr.append("0000");
        //命令
        cmdStr.append("08B1");
        //本机mac
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
}
