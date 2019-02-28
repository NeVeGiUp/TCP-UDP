package com.itc.smartbroadcast.channels.protocolhandler;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.checkSum;

public class HeartBeat {


    public final static int VERSION = 64;                           //版本号

    /**
     * 获取查询定时器在线状态指令
     *
     * @param host 定时器ip地址
     */
    public static void sendCMD(String host) {

        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getTcpHeartBrat(), host, true);
                NettyTcpClient.getInstance().sendPackageNotRetrySend(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackageNotRetrySend(host, getUdpHeartBrat());
        }
    }

    private static byte[] getUdpHeartBrat() {

        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("0CB9");
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
        //校验值
        cmdStr.append(checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");
        byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return result;
    }


    private static byte[] getTcpHeartBrat() {

        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("05BE");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //添加手机MAC
        cmdStr.append(mac.replace(":", ""));
        //添加软件版本型号
        String versionHex = SmartBroadCastUtils.str2HexStr(SmartBroadcastApplication.getContext().getResources().getString(R.string.version));
        if (versionHex.length() > VERSION * 2) {
            versionHex = versionHex.substring(0, VERSION * 2);
        } else {
            int len = versionHex.length();
            int count = (VERSION * 2) - versionHex.length();
            for (int i = 0; i < count; i++) {
                versionHex += "0";
            }
        }
        cmdStr.append(versionHex);
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //校验值
        cmdStr.append(checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");
        byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return result;
    }


}
