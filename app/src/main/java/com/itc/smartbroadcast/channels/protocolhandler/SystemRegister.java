package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.SystemRegisterInfo;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.checkSum;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.isHex;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.str2HexStr;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * author： lghandroid
 * created：2018/9/10 16:20
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: _系统注册
 */

public class SystemRegister {

    private static final int REGISTER_STATE_LENGTH = 1;                          //注册状态
    private static final int REGISTER_CODE_LENGTH = 20;                          //注册码
    private static final int HEAD_PACKET_LENGTH = 28;                            //包头
    private static final int END_PACKET_LENGTH = 4;                              //包尾

    public static SystemRegister init() {
        return new SystemRegister();
    }


    /**
     * 业务处理
     *
     * @param list 获取系统注册返回信息
     */
    public void handler(List<byte[]> list) {

        SystemRegisterInfo systemRegisterInfo = getSystemRegisterResult(list.get(0));

        Gson gson = new Gson();

        String json = gson.toJson(systemRegisterInfo);


        BaseBean bean = new BaseBean();

        bean.setType("getSystemRegisterResult");

        bean.setData(json);

        String jsonResult = gson.toJson(bean);

        Log.i("jsonResult", "handler: " + jsonResult);

        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 通过byte数组获取SystemRegisterInfo对象
     *
     * @param bytes
     * @return SystemRegisterInfo对象
     */
    public SystemRegisterInfo getSystemRegisterResult(byte[] bytes) {

        //获取有效字节数
        bytes = subBytes(bytes, HEAD_PACKET_LENGTH, bytes.length - END_PACKET_LENGTH - HEAD_PACKET_LENGTH);
        SystemRegisterInfo registerInfo = new SystemRegisterInfo();
        //注册状态
        int registerState = byteToInt(subBytes(bytes, 0, REGISTER_STATE_LENGTH)[0]);
        registerInfo.setRegisterState(registerState);
        return registerInfo;
    }

    /**
     * 发送获取系统注册信息指令包
     */
    public static void sendCMD(String host,String registerCode) {


        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getSystemRegisterCmd(registerCode), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host, getSystemRegisterCmd(registerCode));
        }

    }


    /**
     * 获取系统注册发送指令
     *
     * @return
     */
    public static byte[] getSystemRegisterCmd(String registerCode) {
        StringBuffer cmdStr = new StringBuffer();
        //起始标志
        cmdStr.append("AA55");
        //长度
        cmdStr.append("0000");
        //命令
        cmdStr.append("0DB9");
        //本机mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //注册码
        cmdStr.append(registerCode);
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
