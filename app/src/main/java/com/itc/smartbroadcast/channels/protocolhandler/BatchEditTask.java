package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.BatchEditTaskResult;
import com.itc.smartbroadcast.bean.Task;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.HexStringtoBytes;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

public class BatchEditTask {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int RESULT = 1;                                      //结果


    public static BatchEditTask init() {
        return new BatchEditTask();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {
        BatchEditTaskResult batchEditTaskResult = getTaskResult(list.get(0));

        Gson gson = new Gson();

        String json = gson.toJson(batchEditTaskResult);

        BaseBean bean = new BaseBean();

        bean.setType("batchEditTaskResult");

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
    public BatchEditTaskResult getTaskResult(byte[] bytes) {

        //头部多余数
        int head = HEAD_PACKAGE_LENGTH;
        //去掉方案总数的字节
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        BatchEditTaskResult batchEditTaskResult = new BatchEditTaskResult();
        batchEditTaskResult.setResult(SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, 0, RESULT)));
        return batchEditTaskResult;
    }


    /**
     * 批量编辑定时任务
     *
     * @param host     ip地址
     * @param taskList taskList
     * @param type     操作类型：
     *                 （0：添加，1：修改，2：删除）
     */
    public static void sendCMD(String host, List<Task> taskList, int type, int continueTime, int changeTime) {

        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                switch (type) {
                    case 0: //批量修改
                    {
                        byte[] bytes = SmartBroadCastUtils.CloudUtil(getBatchEditTask(taskList, continueTime, changeTime), host, false);
                        NettyTcpClient.getInstance().sendPackageNotRetrySend(host, bytes);
                    }
                    break;
                    case 1: //批量删除
                    {
                        byte[] bytes = SmartBroadCastUtils.CloudUtil(getBatchDeleteTask(taskList), host, false);
                        NettyTcpClient.getInstance().sendPackageNotRetrySend(host, bytes);
                    }
                    break;
                }

            }
        } else {
            switch (type) {
                case 0: //批量修改
                    NettyUdpClient.getInstance().sendPackageNotRetrySend(host, getBatchEditTask(taskList, continueTime, changeTime));
                    break;
                case 1: //批量删除
                    NettyUdpClient.getInstance().sendPackageNotRetrySend(host, getBatchDeleteTask(taskList));
                    break;
            }
        }


    }

    private static byte[] getBatchDeleteTask(List<Task> taskList) {
        StringBuffer cmdStr = new StringBuffer();
        //添加头部
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("0DB3");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //添加批量编辑操作符（删除）
        cmdStr.append("01");
        //添加持续时间
        cmdStr.append("00");
        //添加持续时间
        cmdStr.append("000000");
        //添加提前延后时间
        cmdStr.append("000000");
        //添加任务总数
        String taskSize = Integer.toHexString(taskList.size());
        cmdStr.append((taskSize.length() == 1) ? "0" + taskSize : taskSize);
        //添加任务编号
        for (int i = 0; i < taskList.size(); i++) {
            String taskNum = SmartBroadCastUtils.intToUint16Hex(taskList.get(i).getTaskNum());
            cmdStr.append(taskNum);
        }
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //添加校验码
        cmdStr.append("" + SmartBroadCastUtils.checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");

        byte[] bytes = HexStringtoBytes(cmdStr.toString());

        return bytes;
    }

    private static byte[] getBatchEditTask(List<Task> taskList, int continueTime, int changeTime) {
        StringBuffer cmdStr = new StringBuffer();
        //添加头部
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("0DB3");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");

        //添加批量编辑操作符（修改）
        if (changeTime == 0) {
            cmdStr.append("00");
        }
        if (changeTime > 0) {
            cmdStr.append("03");
        }
        if (changeTime < 0) {
            cmdStr.append("02");
        }
        //添加持续时间操作符
        if (continueTime == 0) {
            cmdStr.append("00");
        } else {
            cmdStr.append("01");
        }
        //添加持续时间
        {
            int h = continueTime / 3600;
            String hStr = Integer.toHexString(h);
            cmdStr.append((hStr.length() == 1) ? "0" + hStr : hStr);
            int m = (continueTime / 60) % 60;
            String mStr = Integer.toHexString(m);
            cmdStr.append((mStr.length() == 1) ? "0" + mStr : mStr);
            int s = continueTime % 60;
            String sStr = Integer.toHexString(s);
            cmdStr.append((sStr.length() == 1) ? "0" + sStr : sStr);
        }
        //添加提前延后时间
        {
            int h = Math.abs(changeTime) / 3600;
            String hStr = Integer.toHexString(h);
            cmdStr.append((hStr.length() == 1) ? "0" + hStr : hStr);
            int m = (Math.abs(changeTime) / 60) % 60;
            String mStr = Integer.toHexString(m);
            cmdStr.append((mStr.length() == 1) ? "0" + mStr : mStr);
            int s = Math.abs(changeTime) % 60;
            String sStr = Integer.toHexString(s);
            cmdStr.append((sStr.length() == 1) ? "0" + sStr : sStr);
        }
        //添加任务总数
        String taskSize = Integer.toHexString(taskList.size());
        cmdStr.append((taskSize.length() == 1) ? "0" + taskSize : taskSize);
        //添加任务编号
        for (int i = 0; i < taskList.size(); i++) {
            String taskNum = SmartBroadCastUtils.intToUint16Hex(taskList.get(i).getTaskNum());
            cmdStr.append(taskNum);
        }
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //添加校验码
        cmdStr.append("" + SmartBroadCastUtils.checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");

        byte[] bytes = HexStringtoBytes(cmdStr.toString());

        return bytes;
    }
}
