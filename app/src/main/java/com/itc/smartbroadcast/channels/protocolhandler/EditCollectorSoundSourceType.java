package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditSoundSourceTypeResult;
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
 * created：2018/11/2 14:22
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: 配置指定采集器音源类型   普通音源/话筒音源
 */


public class EditCollectorSoundSourceType {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int RESULT_LENGTH = 1;                               //配置采集器音源类型结果


    public static EditCollectorSoundSourceType init() {
        return new EditCollectorSoundSourceType();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {
        EditSoundSourceTypeResult editSoundSourceTypeResult = editSoundSourceTypeResult(list.get(0));
        Gson gson = new Gson();
        String json = gson.toJson(editSoundSourceTypeResult);
        BaseBean bean = new BaseBean();
        bean.setType("editSoundSourceTypeResult");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        Log.i("jsonResult", "handler: " + jsonResult);
        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 获取采集器音源类型结果
     *
     * @param bytes
     * @return
     */
    public EditSoundSourceTypeResult editSoundSourceTypeResult(byte[] bytes) {
        //包头
        int head = HEAD_PACKAGE_LENGTH;
        //去掉包头，尾字节
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        EditSoundSourceTypeResult editSoundSourceTypeResult = new EditSoundSourceTypeResult();
        editSoundSourceTypeResult.setResult(byteToInt(subBytes(bytes, 0, RESULT_LENGTH)[0]));
        return editSoundSourceTypeResult;
    }


    /**
     * 发送采集器音源类型包
     *
     * @param host ip地址
     */
    public static void sendCMD(String host, int type) {
        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getEditSoundSourceTypeBytes(type), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host, getEditSoundSourceTypeBytes(type));
        }
    }


    /**
     * 获取集器音源类型指令
     *
     * @return
     */
    private static byte[] getEditSoundSourceTypeBytes(int type) {
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("0BB1");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //采集器音源类型
        cmdStr.append(String.valueOf(type).length() == 1 ? "0" + type : type);
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
