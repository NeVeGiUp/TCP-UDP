package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.SynTimeResult;
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
 * created：2018/9/3 11:33
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: 时间同步
 */


public class SynchronizationTime {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int CURRENCY_LENGTH = 1;                             //同步时间通用字节长度


    public static SynchronizationTime init() {
        return new SynchronizationTime();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {

        SynTimeResult synTimeResult = getSynTimeResult(list.get(0));

        Gson gson = new Gson();

        String json = gson.toJson(synTimeResult);

        BaseBean bean = new BaseBean();

        bean.setType("SynchronizationTime");

        bean.setData(json);

        String jsonResult = gson.toJson(bean);

        Log.i("jsonResult", "handler: " + jsonResult);

        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 同步时间结果
     *
     * @param bytes
     * @return
     */
    public SynTimeResult getSynTimeResult(byte[] bytes) {
        //包头
        int head = HEAD_PACKAGE_LENGTH;
        //去掉包头，尾字节
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        SynTimeResult synTimeResult = new SynTimeResult();
        synTimeResult.setResult(byteToInt(subBytes(bytes, 0, CURRENCY_LENGTH)[0]));
        return synTimeResult;
    }


    /**
     * 时间同步，发送定时器更新
     *
     * @param host      ip地址
     * @param operator  同步方式
     * @param timeArray 详细日期时间数组
     */
    public static void sendCMD(String host, String operator, int[] timeArray) {


        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getSynTimeBytes(operator, timeArray), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host, getSynTimeBytes(operator, timeArray));
        }
    }


    /**
     * 发送时间同步指令
     *
     * @param operator
     * @param timeArray
     * @return
     */
    private static byte[] getSynTimeBytes(String operator, int[] timeArray) {
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("00BB");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //日期同步状态符  0:自动同步  1：手动同步
        cmdStr.append(operator);
        //详细日期时间
        for (int i = 0; i < timeArray.length; i++) {
            String hexTime = Integer.toHexString(timeArray[i]);
            cmdStr.append(hexTime.length() == 1 ? "0" + hexTime : hexTime);
        }
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
