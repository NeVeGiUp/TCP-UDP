package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.Task;
import com.itc.smartbroadcast.bean.TaskOperationResult;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

public class TaskOperation {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int RESULT = 1;                                      //结果

    public static TaskOperation init() {
        return new TaskOperation();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {

        TaskOperationResult taskOperationResult = getTaskOperationResult(list.get(0));

        Gson gson = new Gson();

        String json = gson.toJson(taskOperationResult);

        BaseBean bean = new BaseBean();

        bean.setType("taskOperationResult");

        bean.setData(json);

        String jsonResult = gson.toJson(bean);

        Log.i("jsonResult", "handler: " + jsonResult);

        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 获取任务操作结果
     *
     * @param bytes
     * @return
     */
    private TaskOperationResult getTaskOperationResult(byte[] bytes) {
        //头部多余数
        int head = HEAD_PACKAGE_LENGTH;
        //去掉方案总数的字节
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        TaskOperationResult taskOperationResult = new TaskOperationResult();
        taskOperationResult.setResult(SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, 0, RESULT)));
        return taskOperationResult;
    }

    /**
     * 任务操作
     *
     * @param host ip地址
     * @param task task
     * @param type 操作类型：
     *             （0：禁止，1：启动）
     */
    public static void sendCMD(String host, Task task, int type) {


        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {

                switch (type) {
                    case 1: //启动任务

                    {
                        byte[] bytes = SmartBroadCastUtils.CloudUtil(getStartTask(task), host, false);
                        NettyTcpClient.getInstance().sendPackageNotRetrySend(host, bytes);
                    }

                    break;
                    case 0: //禁止任务
                    {
                        byte[] bytes = SmartBroadCastUtils.CloudUtil(getProhibitTask(task), host, false);
                        NettyTcpClient.getInstance().sendPackageNotRetrySend(host, bytes);
                    }
                    break;
                }
            }
        } else {
            switch (type) {
                case 1: //启动任务
                    NettyUdpClient.getInstance().sendPackage(host, getStartTask(task));
                    break;
                case 0: //禁止任务
                    NettyUdpClient.getInstance().sendPackage(host, getProhibitTask(task));
                    break;
            }
        }


    }

    private static byte[] getProhibitTask(Task task) {

        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("0eB3");
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
        String taskNum = SmartBroadCastUtils.intToUint16Hex(task.getTaskNum());
        cmdStr.append(taskNum);
        //添加操作标志
        cmdStr.append("00");
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //添加校验码
        cmdStr.append("" + SmartBroadCastUtils.checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");
        Log.i("msg", "getAddScheme: " + cmdStr);
        byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return result;
    }

    private static byte[] getStartTask(Task task) {
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("0eB3");
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
        String taskNum = SmartBroadCastUtils.intToUint16Hex(task.getTaskNum());
        cmdStr.append(taskNum);
        //添加操作标志
        cmdStr.append("01");
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //添加校验码
        cmdStr.append("" + SmartBroadCastUtils.checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");
        Log.i("msg", "getAddScheme: " + cmdStr);
        byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return result;
    }

}
