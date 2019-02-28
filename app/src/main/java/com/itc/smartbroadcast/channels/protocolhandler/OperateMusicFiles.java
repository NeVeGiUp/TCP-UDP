package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.OperateMusicFilesInfo;
import com.itc.smartbroadcast.bean.OperateMusicFilesResult;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.checkSum;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.chinese2Hex;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * author： lghandroid
 * created：2018/10/10 14:30
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: 复制，移动，删除到指定文件夹(乐库)里的音乐协议
 */


public class OperateMusicFiles {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int INIT_FOLDER_NAME_LENGTH = 32;                    //初始文件夹名称
    public final static int MUSIC_NAME_LENGTH = 64;                          //曲目名称
    public final static int TARGET_FOLDER_NAME_LENGTH = 32;                  //目标文件夹名称
    public final static int OPERATE_STATE_LENGTH = 1;                        //操作状态


    public static OperateMusicFiles init() {
        return new OperateMusicFiles();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {

        OperateMusicFilesResult operateMusicFilesResult = getOperateMusicFilesResult(list.get(0));

        Gson gson = new Gson();

        String json = gson.toJson(operateMusicFilesResult);

        BaseBean bean = new BaseBean();

        bean.setType("OperateMusicFilesResult");

        bean.setData(json);

        String jsonResult = gson.toJson(bean);

        Log.i("jsonResult", "handler: " + jsonResult);

        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 操作音乐库文件状态结果
     *
     * @param bytes
     * @return
     */
    public OperateMusicFilesResult getOperateMusicFilesResult(byte[] bytes) {
        //包头
        int head = HEAD_PACKAGE_LENGTH;
        //去掉包头，尾字节
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));

        OperateMusicFilesResult operateMusicFilesResult = new OperateMusicFilesResult();
        //操作状态
        operateMusicFilesResult.setResult(byteToInt(subBytes(bytes, 0, OPERATE_STATE_LENGTH)[0]));

        return operateMusicFilesResult;
    }


    /**
     * 发送操作音乐文件状态指令，包括复制，移动和删除
     *
     * @param host                  ip地址
     * @param operateMusicFilesInfo OperateMusicFilesInfo对象
     *                              00：复制， 01：移动， 02：删除）
     */
    public static void sendCMD(String host, OperateMusicFilesInfo operateMusicFilesInfo) {
        String operator = operateMusicFilesInfo.getOperator();


        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                if ("00".equals(operator) || "01".equals(operator))
                {
                    byte[] bytes = SmartBroadCastUtils.CloudUtil(getOPerateMusicFilesBytes(operateMusicFilesInfo), host, false);
                    NettyTcpClient.getInstance().sendPackageNotRetrySend(host, bytes);
                } else {
                    byte[] bytes = SmartBroadCastUtils.CloudUtil(getOPerateMusicFilesBytes(operateMusicFilesInfo), host, false);
                    NettyTcpClient.getInstance().sendPackageNotRetrySend(host, bytes);
                }
            }
        } else {

            if ("00".equals(operator) || "01".equals(operator))
                NettyUdpClient.getInstance().sendPackageNotRetrySend(host, getOPerateMusicFilesBytes(operateMusicFilesInfo));
            else
                NettyUdpClient.getInstance().sendPackageNotRetrySend(host, getOPerateMusicFilesBytes(operateMusicFilesInfo));

        }

    }


    /**
     * 获取操作音乐文件字节串
     *
     * @param operateMusicFilesInfo
     * @return
     */
    private static byte[] getOPerateMusicFilesBytes(OperateMusicFilesInfo operateMusicFilesInfo) {
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("04B8");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //文件操作符
        cmdStr.append(operateMusicFilesInfo.getOperator());
        //初始文件夹名称
        cmdStr.append(chinese2Hex(operateMusicFilesInfo.getInitFolderName(), INIT_FOLDER_NAME_LENGTH));
        //曲目名称
        cmdStr.append(chinese2Hex(operateMusicFilesInfo.getMusicName(), MUSIC_NAME_LENGTH));
        //目标文件夹名称
        if (!"02".equals(operateMusicFilesInfo.getOperator()))
            //复制、移动操作需传递有效的目标文件夹
            cmdStr.append(chinese2Hex(operateMusicFilesInfo.getTargetFolderName(), TARGET_FOLDER_NAME_LENGTH));
        else
            //删除操作无需传递目标文件夹名称，该字段目的是填充字节数
            cmdStr.append(chinese2Hex("目标文件夹名称", TARGET_FOLDER_NAME_LENGTH));
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
