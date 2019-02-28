package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditPowerAmplifierResult;
import com.itc.smartbroadcast.bean.PowerAmplifierInfo;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.checkSum;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * author： lghandroid
 * created：2018/9/2 16:09
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: _编辑指定功放或接收设备详细信息
 */

public class EditPowerAmplifierDetail {

    private static final int HEAD_PACKET_LENGTH = 28;                           //包头
    private static final int END_PACKET_LENGTH = 4;                             //包尾
    private static final int EDIT_RESULT_LENGTH = 1;                            //编辑功放信息结果

    public static EditPowerAmplifierDetail init() {
        return new EditPowerAmplifierDetail();
    }


    /**
     * 业务处理
     *
     * @param list 编辑指定功放或接收设备详细信息
     */
    public void handler(List<byte[]> list) {
        EditPowerAmplifierResult result = editPowerAmplifierDetailResult(list.get(0));
        Gson gson = new Gson();
        String json = gson.toJson(result);
        BaseBean bean = new BaseBean();
        bean.setType("editPowerAmplifierDetail");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        Log.i("jsonResult", "handler: " + jsonResult);
        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 通过byte数组获取PEditPowerAmplifierResult对象
     *
     * @param bytes
     * @return EditPowerAmplifierResult对象
     */
    public EditPowerAmplifierResult editPowerAmplifierDetailResult(byte[] bytes) {
        //获取有效字节数
        bytes = subBytes(bytes, HEAD_PACKET_LENGTH, bytes.length - END_PACKET_LENGTH - HEAD_PACKET_LENGTH);
        EditPowerAmplifierResult result = new EditPowerAmplifierResult();
        //编辑功放信息结果
        int editResult = byteToInt(subBytes(bytes, 0, EDIT_RESULT_LENGTH)[0]);
        result.setResult(editResult);
        return result;
    }

    /**
     * 发送编辑功放设备详情指令包
     */
    public static void sendCMD(String host, PowerAmplifierInfo powerAmplifierInfo) {
        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(editPowerAmplifierDetailCmd(powerAmplifierInfo), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host, editPowerAmplifierDetailCmd(powerAmplifierInfo));
        }
    }


    /**
     * 获取编辑功放设备详情发送指令
     *
     * @return
     */
    public static byte[] editPowerAmplifierDetailCmd(PowerAmplifierInfo powerAmplifier) {
        StringBuffer cmdStr = new StringBuffer();
        //起始标志
        cmdStr.append("AA55");
        //长度
        cmdStr.append("0000");
        //命令
        cmdStr.append("06B1");
        //本机mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //高音增益
        int highGain = powerAmplifier.getHighGain();
        if (highGain >= 0) {
            String hexHihGain = Integer.toHexString(highGain);
            cmdStr.append(hexHihGain.length() == 1 ? "0" + hexHihGain : hexHihGain);
        } else {
            String hexHihGain = Integer.toHexString(highGain).substring(6);
            cmdStr.append(hexHihGain);
        }
        //低音增益
        int lowGain = powerAmplifier.getLowGain();
        if (lowGain >= 0) {
            String hexLowGain = Integer.toHexString(lowGain);
            cmdStr.append(hexLowGain.length() == 1 ? "0" + hexLowGain : hexLowGain);
        } else {
            String hexLowGain = Integer.toHexString(lowGain).substring(6);
            cmdStr.append(hexLowGain);
        }
        //S0-S5类型混音使能状态
        String mixingEnableState_s = getHexMixingEnableState(powerAmplifier.getMixingEnableState_s());
        cmdStr.append((mixingEnableState_s.length() == 1) ? "0" + mixingEnableState_s : mixingEnableState_s);
        //P0-P5类型混音使能状态
        String mixingEnableState_p = getHexMixingEnableState(powerAmplifier.getMixingEnableState_p());
        cmdStr.append((mixingEnableState_p.length() == 1) ? "0" + mixingEnableState_p : mixingEnableState_p);
        //E0-E5类型混音使能状态
        String mixingEnableState_e = getHexMixingEnableState(powerAmplifier.getMixingEnableState_e());
        cmdStr.append((mixingEnableState_e.length() == 1) ? "0" + mixingEnableState_e : mixingEnableState_e);
        //终端默认音量
        String volume = Integer.toHexString(powerAmplifier.getVolume());
        cmdStr.append(volume.length() == 1 ? "0" + volume : volume);
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //校验值
        cmdStr.append(checkSum(cmdStr.substring(4)));
        //结束标志
        cmdStr.append("55AA");
        byte[] bytes = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return bytes;
    }


    /**
     * 获取十六进制混音使能状态
     *
     * @param ints
     * @return
     */
    public static String getHexMixingEnableState(int[] ints) {
        int data = 0;
        for (int i = 0; i < 5; i++) {
            data += ints[i] * Math.pow(2, i);
        }
        return Integer.toHexString(data);
    }


}
