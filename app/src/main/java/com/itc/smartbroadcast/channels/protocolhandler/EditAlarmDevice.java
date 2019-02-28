package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.AlarmDeviceDetail;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditAlarmDeviceResult;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * content:配置报警设备基础信息
 * author:lik
 * date: 18-10-11 上午10:07
 */
public class EditAlarmDevice {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int RESULT = 1;                                      //结果

    public static EditAlarmDevice init() {
        return new EditAlarmDevice();
    }


    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {

        EditAlarmDeviceResult editAlarmDeviceResult = getTaskResult(list.get(0));

        Gson gson = new Gson();

        String json = gson.toJson(editAlarmDeviceResult);

        BaseBean bean = new BaseBean();

        bean.setType("editAlarmDeviceResult");

        bean.setData(json);

        String jsonResult = gson.toJson(bean);

        Log.i("jsonResult", "handler: " + jsonResult);

        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 获取编辑结果
     *
     * @param bytes
     * @return
     */
    public EditAlarmDeviceResult getTaskResult(byte[] bytes) {

        //头部多余数
        int head = HEAD_PACKAGE_LENGTH;
        //去掉方案总数的字节
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        EditAlarmDeviceResult editAlarmDeviceResult = new EditAlarmDeviceResult();
        if (SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, 0, RESULT)) > 0) {
            editAlarmDeviceResult.setResult(1);
        } else {
            editAlarmDeviceResult.setResult(0);
        }
        return editAlarmDeviceResult;
    }


    /**
     * 配置报警设备基础信息
     *
     * @param host              ip地址
     * @param alarmDeviceDetail alarmDeviceDetail对象
     */
    public static void sendCMD(String host, AlarmDeviceDetail alarmDeviceDetail) {

        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getEditAlarmDevice(alarmDeviceDetail), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host, getEditAlarmDevice(alarmDeviceDetail));
        }
    }

    /**
     * 获取编辑字节流
     *
     * @param alarmDeviceDetail
     * @return
     */
    private static byte[] getEditAlarmDevice(AlarmDeviceDetail alarmDeviceDetail) {

        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("04B7");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
//        //添加报警设备mac
//        cmdStr.append(alarmDeviceDetail.getDeviceMac().replace("-", ""));
        //添加报警端口响应模式
        String portResponseMode = Integer.toHexString(alarmDeviceDetail.getPortResponseMode());
        cmdStr.append((portResponseMode.length() == 1) ? "0" + portResponseMode : portResponseMode);
        //添加报警触发解除模式
        String triggerMode = Integer.toHexString(alarmDeviceDetail.getTriggerMode());
        cmdStr.append((triggerMode.length() == 1) ? "0" + triggerMode : triggerMode);
        //添加报警设备播放模式
        String playMode = Integer.toHexString(alarmDeviceDetail.getPlayMode());
        cmdStr.append((playMode.length() == 1) ? "0" + playMode : playMode);
        //添加报警设备播放音量
        String playVolume = Integer.toHexString(alarmDeviceDetail.getPlayVolume());
        cmdStr.append((playVolume.length() == 1) ? "0" + playVolume : playVolume);
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //添加校验码
        cmdStr.append("" + SmartBroadCastUtils.checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");
        Log.i("msg", "getAddTask: " + cmdStr);
        byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return result;
    }

}
