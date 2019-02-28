package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.MusicUploadStatusResult;
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
 * created：2018/10/10 9:25
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: 查询定时器是否正在传输文件处于繁忙状态
 */


public class QueryMusicUploadStatus {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int UPLOAD_STATUS_LENGTH = 1;                      //上传状态


    public static QueryMusicUploadStatus init() {
        return new QueryMusicUploadStatus();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {

        MusicUploadStatusResult musicUploadStatusResult = getMusicUploadStatusResult(list.get(0));

        Gson gson = new Gson();

        String json = gson.toJson(musicUploadStatusResult);

        BaseBean bean = new BaseBean();

        bean.setType("QueryMusicUploadStatus");

        bean.setData(json);

        String jsonResult = gson.toJson(bean);

        Log.i("jsonResult", "handler: " + jsonResult);

        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 获取音乐上传状态
     *
     * @param bytes
     * @return
     */
    public MusicUploadStatusResult getMusicUploadStatusResult(byte[] bytes) {
        //包头
        int head = HEAD_PACKAGE_LENGTH;
        //去掉包头，尾字节
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        MusicUploadStatusResult uploadStatusResult = new MusicUploadStatusResult();
        uploadStatusResult.setResult(byteToInt(subBytes(bytes, 0, UPLOAD_STATUS_LENGTH)[0]));
        return uploadStatusResult;
    }


    /**
     * 发送获取音乐上传状态指令
     *
     * @param host                ip地址
     */
    public static void sendCMD(String host) {

        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getUploadMusicStatusBytes(), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host,getUploadMusicStatusBytes());
        }

    }


    private static byte[] getUploadMusicStatusBytes() {
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("03B8");
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
