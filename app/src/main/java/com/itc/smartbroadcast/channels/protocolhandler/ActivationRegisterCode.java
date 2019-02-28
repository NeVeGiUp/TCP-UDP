package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.ActivationRegisterCodeResult;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.TimerStatusQueryResult;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.util.DeviceUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.CloudUtil;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.HexStringtoBytes;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.checkSum;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.chinese2Hex;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.intToUint16Hex;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * author： lghandroid
 * created：2019/1/03 17:39
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: 客户端激活注册吗
 */


public class ActivationRegisterCode {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int REGISTER_CODE_LENGTH = 10;                       //注册码
    public final static int REGISTER_STATUS_LENGTH = 1;                      //注册状态

    public static ActivationRegisterCode init() {
        return new ActivationRegisterCode();
    }


    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {
        ActivationRegisterCodeResult activationRegisterCodeResult = getActivationRegisterCodeResult(list.get(0));
        Gson gson = new Gson();
        String json = gson.toJson(activationRegisterCodeResult);
        BaseBean bean = new BaseBean();
        bean.setType("ActivationRegisterCodeResult");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        Log.i("jsonResult", "handler: " + jsonResult);
        EventBus.getDefault().post(jsonResult);
    }


    /**
     * 获取定时器在线状态结果
     *
     * @param bytes
     * @return
     */
    public ActivationRegisterCodeResult getActivationRegisterCodeResult(byte[] bytes) {
        //包头
        int head = HEAD_PACKAGE_LENGTH;
        //去掉包头，尾字节
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        ActivationRegisterCodeResult activationRegisterCodeResult = new ActivationRegisterCodeResult();
        int registerStatus = byteToInt(subBytes(bytes, 0, REGISTER_STATUS_LENGTH)[0]);
        activationRegisterCodeResult.setResult(registerStatus);
        return activationRegisterCodeResult;
    }


    /**
     * 获取查询定时器在线状态指令
     *
     * @param host 定时器ip地址
     */
    public static void sendCMD(String host, String registerCode) {
        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = CloudUtil(getActivationRegisterCodeBytes(registerCode), host,false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host,getActivationRegisterCodeBytes(registerCode));
        }
    }


    /**
     * 获取客户端激活注册码字节流
     *
     * @return
     */
    private static byte[] getActivationRegisterCodeBytes(String registerCode) {
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("0dB9");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //注册码
        cmdStr.append(chinese2Hex(registerCode, REGISTER_CODE_LENGTH));
        //修改长度
        cmdStr.replace(4, 8, intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //校验值
        cmdStr.append(checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");
        byte[] result = HexStringtoBytes(cmdStr.toString());
        return result;
    }

}
