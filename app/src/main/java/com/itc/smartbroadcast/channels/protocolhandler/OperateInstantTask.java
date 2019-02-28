package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.InstantTask;
import com.itc.smartbroadcast.bean.OperateInstantTaskResult;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * 操作即时任务
 * Created by lik on 18-8-31.
 */

public class OperateInstantTask {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int TASK_NUM_LENGTH = 2;                                    //任务编号
    public final static int USER_NUM_LENGTH = 2;                                    //用户编号
    public final static int RESULT = 1;                                      //结果

    public static OperateInstantTask init() {
        return new OperateInstantTask();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {

        OperateInstantTaskResult operateInstantTaskResult = getOperateInstantTaskResult(list.get(0));

        Gson gson = new Gson();

        String json = gson.toJson(operateInstantTaskResult);

        BaseBean bean = new BaseBean();

        bean.setType("operateInstantTaskResult");

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
    public OperateInstantTaskResult getOperateInstantTaskResult(byte[] bytes) {

        //头部多余数
        int head = HEAD_PACKAGE_LENGTH;
        //去掉方案总数的字节
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        OperateInstantTaskResult operateInstantTaskResult = new OperateInstantTaskResult();

        int index = 0;
        operateInstantTaskResult.setTaskNum(SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, index, TASK_NUM_LENGTH)));
        index += TASK_NUM_LENGTH;
        operateInstantTaskResult.setUserNum(SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, index, USER_NUM_LENGTH)));
        index += USER_NUM_LENGTH;
        if (SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, index, RESULT)) > 0) {
            operateInstantTaskResult.setResult(1);
        } else {
            operateInstantTaskResult.setResult(0);
        }
        return operateInstantTaskResult;
    }

    /**
     * 发送操作即时任务命令
     *
     * @param host 定时器IP
     * @param type 播放或者暂停（type为0为停止，type为1为执行）
     */
    public static void sendCMD(String host, InstantTask instantTask, int userNum, int type) {


        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {

                switch (type) {
                    case 0:     //停止即时任务
                    {
                        byte[] bytes = SmartBroadCastUtils.CloudUtil(getStopCMD(instantTask, userNum), host, false);
                        NettyTcpClient.getInstance().sendPackage(host, bytes);
                    }
                    break;
                    case 1:     //执行即时任务
                    {
                        byte[] bytes = SmartBroadCastUtils.CloudUtil(getExecuteCMD(instantTask, userNum), host, false);
                        NettyTcpClient.getInstance().sendPackage(host, bytes);
                    }
                    break;
                }
            }
        } else {
            switch (type) {
                case 0:     //停止即时任务
                    NettyUdpClient.getInstance().sendPackage(host, getStopCMD(instantTask, userNum));
                    break;
                case 1:     //执行即时任务
                    NettyUdpClient.getInstance().sendPackage(host, getExecuteCMD(instantTask, userNum));
                    break;
            }
        }


    }

    /**
     * 获取即时任务停止命令
     *
     * @return
     */
    public static byte[] getStopCMD(InstantTask instantTask, int userNum) {
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("04B4");
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
        String taskNum = SmartBroadCastUtils.intToUint16Hex(instantTask.getTaskNum());
        cmdStr.append(taskNum);
        //添加用户编号
        String userNumHex = SmartBroadCastUtils.intToUint16Hex(userNum);
        cmdStr.append(userNumHex);
        //添加播放控制
        cmdStr.append("00");
        //添加校验码
        cmdStr.append("" + SmartBroadCastUtils.checkSum(cmdStr.substring(4)));
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex(cmdStr.substring(4).length() / 2));
        //添加结束标志
        cmdStr.append("55AA");
        byte[] bytes = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return bytes;
    }

    /**
     * 获取即时任务停止命令
     *
     * @return
     */
    public static byte[] getExecuteCMD(InstantTask instantTask, int userNum) {
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("04B4");
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
        String taskNum = SmartBroadCastUtils.intToUint16Hex(instantTask.getTaskNum());
        cmdStr.append(taskNum);
        //添加任务编号
        String userNumHex = SmartBroadCastUtils.intToUint16Hex(userNum);
        cmdStr.append(userNumHex);
        //添加播放控制
        cmdStr.append("01");
        //添加校验码
        cmdStr.append("" + SmartBroadCastUtils.checkSum(cmdStr.substring(4)));
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex(cmdStr.substring(4).length() / 2));
        //添加结束标志
        cmdStr.append("55AA");
        byte[] bytes = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return bytes;
    }

}
