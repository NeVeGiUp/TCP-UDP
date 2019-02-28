package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.FileOperationProgressResult;
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
 * created：2018/10/10 14:30
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: 文件操作进度信息协议
 */


public class FileOperationProgress {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int FILE_STATE_OPERATION_LENGTH = 1;                 //文件状态符
    public final static int PROGRESS_LENGTH = 1;                             //文件操作完成度


    public static FileOperationProgress init() {
        return new FileOperationProgress();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {
        FileOperationProgressResult operationProgressResult = getFileOPerationProgressResult(list.get(0));
        Gson gson = new Gson();
        String json = gson.toJson(operationProgressResult);
        BaseBean bean = new BaseBean();
        bean.setType("FileOperationProgressResult");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        Log.i("jsonResult", "handler: " + jsonResult);
        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 获取文件操作进度信息
     *
     * @param bytes
     * @return
     */
    public FileOperationProgressResult getFileOPerationProgressResult(byte[] bytes) {
        //包头
        int head = HEAD_PACKAGE_LENGTH;
        //去掉包头，尾字节
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        FileOperationProgressResult fileOperationProgressResult = new FileOperationProgressResult();
        //文件状态符
        int index = 0;
        fileOperationProgressResult.setFileStateOperator(byteToInt(subBytes(bytes, 0, FILE_STATE_OPERATION_LENGTH)[0]));
        index+= FILE_STATE_OPERATION_LENGTH;
        //文件操作完成度
        fileOperationProgressResult.setProgress(byteToInt(subBytes(bytes, index, PROGRESS_LENGTH)[0]));
        return fileOperationProgressResult;
    }


    /**
     * 获取文件操作进度信息指令
     *
     * @param host                  定时器ip地址
     */
    public static void sendCMD(String host) {
        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getFileOperationProgressBytes(), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host,getFileOperationProgressBytes());
        }
    }


    /**
     * 获取文件操作进度信息发送字节流
     *
     * @return
     */
    private static byte[] getFileOperationProgressBytes() {
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("05B8");
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


}
