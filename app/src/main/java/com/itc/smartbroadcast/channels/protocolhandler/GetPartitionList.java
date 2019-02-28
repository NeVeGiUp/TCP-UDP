package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.FoundPartitionInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteArrayToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToStr;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.checkSum;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * author： lghandroid
 * created：2018/8/23 8:48
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: _获取已创建分区列表
 */

public class GetPartitionList {

    private static final int NUM_LENGTH = 2;                       //指定分区对应的编号
    private static final int ACCOUNT_ID_LENGTH = 1;                //账户ID
    private static final int NAME_LENGTH = 32;                     //指定分区名称
    private static final int HEAD_PACKET_LENGTH = 28;              //包头


    public static GetPartitionList init() {
        return new GetPartitionList();
    }
    //主要业务处理，字节转对象，对象转集合再合并，最终输出json字符串
    public void handler(List<byte[]> list) {
        List<FoundPartitionInfo> foundPartitionInfoList = new ArrayList<FoundPartitionInfo>();
        for (byte[] b : list) {
            List<FoundPartitionInfo> FoundDeviceInfos = byteToFoundDeviceInfo(b);
            foundPartitionInfoList = objectMerging(foundPartitionInfoList, FoundDeviceInfos);
        }
        Gson gson = new Gson();
        String json = gson.toJson(foundPartitionInfoList);
        BaseBean baseBean = new BaseBean();
        baseBean.setType("getPartitionList");
        baseBean.setData(json);
        String toJson = gson.toJson(baseBean);
        Log.i("jsonResult", "handler: " + toJson);
        AppDataCache.getInstance().putString("partitinoCount", foundPartitionInfoList.size() + "");
        EventBus.getDefault().post(toJson);

    }

    //数据封装成对象
    private List<FoundPartitionInfo> byteToFoundDeviceInfo(byte[] b) {
        List<FoundPartitionInfo> foundDeviceInfoList = new ArrayList<FoundPartitionInfo>();
        //分区总数
        int partitionCount = SmartBroadCastUtils.byteToInt(subBytes(b, HEAD_PACKET_LENGTH + 2, 1)[0]);
        Log.e("found", "分区数量: " + partitionCount);
        //一个分区item所占字节数
        int itemBt = NUM_LENGTH + ACCOUNT_ID_LENGTH + NAME_LENGTH;
        byte[] partitionListBt = subBytes(b, HEAD_PACKET_LENGTH + 3, itemBt * partitionCount);
        for (int i = 0; i < partitionListBt.length; i += itemBt) {
            FoundPartitionInfo partitionInfo = new FoundPartitionInfo();
            int index = 0;
            //分区编号
            int partitionNum = byteArrayToInt(subBytes(partitionListBt, i, NUM_LENGTH));
            index += NUM_LENGTH;
            //账户编号
            int accountId = byteToInt(subBytes(partitionListBt, i + index, ACCOUNT_ID_LENGTH)[0]);
            index += ACCOUNT_ID_LENGTH;
            //分区名称
            String partitionName = byteToStr(subBytes(partitionListBt, i + index, NAME_LENGTH));
            partitionInfo.setPartitionNum(partitionNum + "");
            partitionInfo.setAccountId(accountId);
            partitionInfo.setName(partitionName);
            foundDeviceInfoList.add(partitionInfo);
        }

        return foundDeviceInfoList;
    }


    private List<FoundPartitionInfo> objectMerging(List<FoundPartitionInfo> foundDeviceInfos1, List<FoundPartitionInfo> foundDeviceInfos2) {

        List<FoundPartitionInfo> foundPartitinoInfoList = new ArrayList<FoundPartitionInfo>();
        for (FoundPartitionInfo foundDeviceInfo : foundDeviceInfos1) {
            foundPartitinoInfoList.add(foundDeviceInfo);
        }
        for (FoundPartitionInfo foundDeviceInfo : foundDeviceInfos2) {
            foundPartitinoInfoList.add(foundDeviceInfo);
        }
        return foundPartitinoInfoList;

    }

    //发送指令
    public static void sendCMD(String host) {

        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getPartitionList(), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host,getPartitionList());
        }

    }

    //获取已创建分区列表
    public static byte[] getPartitionList() {
        StringBuffer cmdStr = new StringBuffer();
        //起始标志
        cmdStr.append("AA55");
        //长度
        cmdStr.append("0000");
        //命令
        cmdStr.append("00B2");
        //本机mac
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
        //结束标志
        cmdStr.append("55AA");
        byte[] bytes = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return bytes;
    }
}
