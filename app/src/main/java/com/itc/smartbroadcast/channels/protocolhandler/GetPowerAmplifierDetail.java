package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.PowerAmplifierInfo;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.checkSum;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.conver2HexStr;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * author： lghandroid
 * created：2018/9/2 16:09
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: _查询指定功放或接收设备详细信息
 */

public class GetPowerAmplifierDetail {

    private static final int HEAD_PACKET_LENGTH = 28;                           //包头
    private static final int END_PACKET_LENGTH = 4;                             //包尾
    private static final int VOLUME_LENGTH = 1;                                 //终端音量
    private static final int HIGH_GAIN_LENGTH = 1;                              //高音增益
    private static final int LOW_GAIN_LENGTH = 1;                               //低音增益
    private static final int MIXING_ENABLE_STATE_S_LENGTH = 1;                  //S0-S5类型混音使能状态
    private static final int MIXING_ENABLE_STATE_P_LENGTH = 1;                  //P0-P5类型混音使能状态
    private static final int MIXING_ENABLE_STATE_E_LENGTH = 1;                  //E0-E5类型混音使能状态

    public static GetPowerAmplifierDetail init() {
        return new GetPowerAmplifierDetail();
    }


    /**
     * 业务处理
     *
     * @param list 获取指定功放或接收设备详细信息
     */
    public void handler(List<byte[]> list) {
        List<PowerAmplifierInfo> detailList = getPowerAmplifierDetail(list.get(0));
        Gson gson = new Gson();
        String json = gson.toJson(detailList);
        BaseBean bean = new BaseBean();
        bean.setType("getPowerAmplifierDetail");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        Log.i("jsonResult", "handler: " + jsonResult);
        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 通过byte数组获取PowerAmplifierInfo对象
     *
     * @param bytes
     * @return PowerAmplifierInfo对象
     */
    public List<PowerAmplifierInfo> getPowerAmplifierDetail(byte[] bytes) {
        List<PowerAmplifierInfo> list = new ArrayList<>();
        int index = 0;
        //获取有效字节数
        bytes = subBytes(bytes, HEAD_PACKET_LENGTH, bytes.length - END_PACKET_LENGTH - HEAD_PACKET_LENGTH);
        PowerAmplifierInfo powerAmplifierInfo = new PowerAmplifierInfo();
        //高音增益
        int highGain = (int) subBytes(bytes, 0, HIGH_GAIN_LENGTH)[0];
        index += HIGH_GAIN_LENGTH;
        //低音增益
        int lowGain = (int) subBytes(bytes, index, LOW_GAIN_LENGTH)[0];
        index += LOW_GAIN_LENGTH;
        //S0-S5混音使能状态
        int[] mixingEnableState_s = getBitArray(subBytes(bytes, index, MIXING_ENABLE_STATE_S_LENGTH));
        index += MIXING_ENABLE_STATE_S_LENGTH;
        //P0-P5混音使能状态
        int[] mixingEnableState_p = getBitArray(subBytes(bytes, index , MIXING_ENABLE_STATE_P_LENGTH));
        index += MIXING_ENABLE_STATE_P_LENGTH;
        //E0-E5混音使能状态
        int[] mixingEnableState_e = getBitArray(subBytes(bytes, index, MIXING_ENABLE_STATE_E_LENGTH));
        index += MIXING_ENABLE_STATE_E_LENGTH;
        //终端默认音量
        int volume = byteToInt(subBytes(bytes, index, VOLUME_LENGTH)[0]);
        powerAmplifierInfo.setHighGain(highGain);
        powerAmplifierInfo.setLowGain(lowGain);
        powerAmplifierInfo.setVolume(volume);
        powerAmplifierInfo.setMixingEnableState_s(mixingEnableState_s);
        powerAmplifierInfo.setMixingEnableState_p(mixingEnableState_p);
        powerAmplifierInfo.setMixingEnableState_e(mixingEnableState_e);
        list.add(powerAmplifierInfo);
        return list;
    }

    /**
     * 发送获取功放设备详情指令包
     */
    public static void sendCMD(String host) {
        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getPowerAmplifierDetailCmd(), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host,getPowerAmplifierDetailCmd());
        }
    }


    /**
     * 获取功放设备详情发送指令
     *
     * @return
     */
    public static byte[] getPowerAmplifierDetailCmd() {
        StringBuffer cmdStr = new StringBuffer();
        //起始标志
        cmdStr.append("AA55");
        //长度
        cmdStr.append("0000");
        //命令
        cmdStr.append("05B1");
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


    /**
     * 获取bit有效位数 数据都为1和0
     *
     * @param b
     * @return
     */
    public int[] getBitArray(byte[] b) {
        String binaryStr = conver2HexStr(b[0]);
        int[] binaryArr = new int[binaryStr.length()];
        int index = 0;
        for (int j = binaryArr.length - 1; j >= 0; j--) {
            binaryArr[index] = Integer.parseInt(String.valueOf(binaryStr.charAt(j)));
            index++;
        }
        return binaryArr;
    }


}
