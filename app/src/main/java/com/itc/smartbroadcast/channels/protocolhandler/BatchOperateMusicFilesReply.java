package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.BatchOperateMusicMsgReplyInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToStr;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.checkSum;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * author： lghandroid
 * created：2018/11/16 10:15
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: 批量复制，移动，删除到指定文件夹(乐库)里的音乐回复协议
 */


public class BatchOperateMusicFilesReply {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int BATCH_OPERATOR_LENGTH = 1;                       //批量操作符
    public final static int FILE_OPERATOR_LENGTH = 1;                        //文件操作符
    public final static int STATUS_PROMPT_LENGTH = 1;                        //状态提示符操作符
    public final static int MUSIC_NAME_LENGTH = 64;                          //曲目名称
    public final static int MUSIC_FOLDER_NAME_LENGTH = 32;                   //曲目文件夹名称


    public static BatchOperateMusicFilesReply init() {
        return new BatchOperateMusicFilesReply();
    }


    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {
        BatchOperateMusicMsgReplyInfo batchOperateMusicFilesReplyResult = getBatchOperateMusicFilesReplyResult(list.get(0));
        Gson gson = new Gson();
        String json = gson.toJson(batchOperateMusicFilesReplyResult);
        BaseBean bean = new BaseBean();
        bean.setType("BatchOperateMusicMsgReplyResult");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        Log.i("jsonResult", "handler: " + jsonResult);
        EventBus.getDefault().post(jsonResult);
        //定时器主动推送的信息，客户端回复定时器
        sendCMD(AppDataCache.getInstance().getString("loginIp"));
    }


    /**
     * 批量操作音乐库文件回复信息
     *
     * @param bytes
     * @return
     */
    public BatchOperateMusicMsgReplyInfo getBatchOperateMusicFilesReplyResult(byte[] bytes) {
        //包头
        int head = HEAD_PACKAGE_LENGTH;
        //去掉包头，尾字节
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        BatchOperateMusicMsgReplyInfo batchOperateMusicMsgReplyInfo = new BatchOperateMusicMsgReplyInfo();
        int index = 0;
        //批量操作符
        batchOperateMusicMsgReplyInfo.setBatchOperator(byteToInt(subBytes(bytes, index, BATCH_OPERATOR_LENGTH)[0]));
        index += BATCH_OPERATOR_LENGTH;
        //文件操作符
        batchOperateMusicMsgReplyInfo.setFileOperator(byteToInt(subBytes(bytes, index, FILE_OPERATOR_LENGTH)[0]));
        index += FILE_OPERATOR_LENGTH;
        //状态提示符
        batchOperateMusicMsgReplyInfo.setStatusPrompt(byteToInt(subBytes(bytes, index, STATUS_PROMPT_LENGTH)[0]));
        index += STATUS_PROMPT_LENGTH;
        //曲目文件夹名称
        batchOperateMusicMsgReplyInfo.setMusicFolderName(byteToStr(subBytes(bytes, index, MUSIC_FOLDER_NAME_LENGTH)));
        index += MUSIC_FOLDER_NAME_LENGTH;
        //曲目名称
        batchOperateMusicMsgReplyInfo.setMusicName(byteToStr(subBytes(bytes, index, MUSIC_NAME_LENGTH)));
        return batchOperateMusicMsgReplyInfo;
    }


    /**
     * 发送确认信息给定时器
     *
     * @param host ip地址
     */
    public static void sendCMD(String host) {
        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getBatchOPerateMusicFilesReplyBytes(), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host, getBatchOPerateMusicFilesReplyBytes());
        }
    }


    /**
     * 获取发送确认信息字节
     *
     * @return
     */
    private static byte[] getBatchOPerateMusicFilesReplyBytes() {
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("07B8");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //添加应答提示
        cmdStr.append("00");
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
