package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.OperatorDeviceListInfo;
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
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.hexstrToMac;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * author： lghandroid
 * created：2018/8/21 17:28
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: _账户可操作的设备列表查询
 */

public class GetOperatorDeviceList {

    private static final int ACCOUNT_NUM_LENGTH = 1;                             //账户编号
    private static final int OPERABLE_DEVICE_COUNT_LENGTH = 1;                   //可操作设备列表总数
    private static final int OPERABLE_DEVICE_MAC_LENGTH = 6;                     //可操作设备mac
    private static final int HEAD_PACKET_LENGTH = 28;                            //包头
    private static final int END_PACKET_LENGTH = 4;                              //包尾

    public static GetOperatorDeviceList init() {
        return new GetOperatorDeviceList();
    }


    /**
     * 业务处理
     *
     * @param list 所有账户可操作设备列表数据
     */
    public void handler(List<byte[]> list) {
        OperatorDeviceListInfo operatorDeviceListInfo = getOperatorDeviceList(list.get(0));
        Gson gson = new Gson();
        String json = gson.toJson(operatorDeviceListInfo);
        BaseBean bean = new BaseBean();
        bean.setType("getOperatorDeviceList");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        Log.i("jsonResult", "handler: " + jsonResult);
        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 通过byte数组获取OperatorDeviceListInfo对象
     *
     * @param bytes
     * @return Scheme对象
     */
    public OperatorDeviceListInfo getOperatorDeviceList(byte[] bytes) {
        int index = 0;
        //获取有效字节数
        bytes = subBytes(bytes, HEAD_PACKET_LENGTH, bytes.length - END_PACKET_LENGTH - HEAD_PACKET_LENGTH);
        OperatorDeviceListInfo operatorDeviceListInfo = new OperatorDeviceListInfo();
        //账户编号
        int accountNum = byteToInt(subBytes(bytes, 0, ACCOUNT_NUM_LENGTH)[0]);
        index += ACCOUNT_NUM_LENGTH;
        //可操作设备列表总数
        int operableDeviceTotal = byteToInt(subBytes(bytes, index, OPERABLE_DEVICE_COUNT_LENGTH)[0]);
        index += OPERABLE_DEVICE_COUNT_LENGTH;
        //可操作设备mac
        ArrayList<String> macList = new ArrayList<>();
        //有可操作的终端情况下，获取MAC列表
        if (0 != operableDeviceTotal) {
            String hexMac = bytesToHexString(subBytes(bytes, index, OPERABLE_DEVICE_MAC_LENGTH * operableDeviceTotal));
            for (int i = 0; i < hexMac.length(); i += 12) {
                String operableDeviceMac = hexstrToMac(hexMac.substring(i, i + 12));
                macList.add(operableDeviceMac);
            }
        }
        operatorDeviceListInfo.setAccountNum(accountNum);
        operatorDeviceListInfo.setDeviceTotal(operableDeviceTotal);
        operatorDeviceListInfo.setOperableDeviceMacList(macList);

        return operatorDeviceListInfo;
    }

    /**
     * 发送账户编号
     */
    public static void sendCMD(String host, int accountNum) {
        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getOperatorDeviceListCmd(accountNum), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host,getOperatorDeviceListCmd(accountNum));
        }
    }


    /**
     * 获取账户可操作设备列表指令
     *
     * @return
     */
    public static byte[] getOperatorDeviceListCmd(int accountNum) {
        StringBuffer cmdStr = new StringBuffer();
        //起始标志
        cmdStr.append("AA55");
        //长度
        cmdStr.append("0000");
        //命令
        cmdStr.append("02B9");
        //本机mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //账户编号
        String hexAccountNum = Integer.toHexString(accountNum);
        cmdStr.append(hexAccountNum.length() == 1 ? "0" + hexAccountNum : hexAccountNum);
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
