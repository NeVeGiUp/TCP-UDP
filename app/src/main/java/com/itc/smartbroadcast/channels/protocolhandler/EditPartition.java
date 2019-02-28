package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditPartitionResult;
import com.itc.smartbroadcast.bean.PartitionInfo;
import com.itc.smartbroadcast.bean.PhysicalPartitionInfo;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteArrayToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.bytesToHexString;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.checkSum;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.chinese2Hex;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.intToUint16Hex;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * author： lghandroid
 * created：2018/8/27 14:04
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: _分区添加、删除、编辑
 */


public class EditPartition {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int OPERATOR_LENGTH = 1;                             //分区操作符号
    public final static int PARTITION_NUM_LENGTH = 2;                        //分区号
    public final static int RESULT_LENGTH = 1;                               //配置状态
    public final static int PARTITION_LENGTH = 32;                           //分区名称


    public static EditPartition init() {
        return new EditPartition();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {
        EditPartitionResult partitionResult = getPartitionResult(list.get(0));
        Gson gson = new Gson();
        String json = gson.toJson(partitionResult);
        BaseBean bean = new BaseBean();
        bean.setType("editPartitionResult");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        Log.i("jsonResult", "handler: " + jsonResult);
        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 获取添加，删除，编辑分区结果
     *
     * @param bytes
     * @return
     */
    public EditPartitionResult getPartitionResult(byte[] bytes) {
        int index = 0;
        //包头
        int head = HEAD_PACKAGE_LENGTH;
        //去掉包头，尾字节
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        EditPartitionResult partitionInfo = new EditPartitionResult();

        partitionInfo.setOperator(bytesToHexString(subBytes(bytes, index, OPERATOR_LENGTH)));
        index += OPERATOR_LENGTH;

        partitionInfo.setPartitionNum(byteArrayToInt(subBytes(bytes, index, PARTITION_NUM_LENGTH)));
        index += PARTITION_NUM_LENGTH;

        partitionInfo.setResult(byteToInt(subBytes(bytes, index, RESULT_LENGTH)[0]));

        return partitionInfo;
    }


    /**
     * 分区配置
     *
     * @param host          ip地址
     * @param partitionInfo scheme对象
     * @param type          操作类型：
     *                      （0：添加，1：删除，2：编辑）
     */
    public static void sendCMD(String host, PartitionInfo partitionInfo, int type) {
        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                switch (type) {
                    case 0: //添加分区
                    {
                        byte[] bytes = SmartBroadCastUtils.CloudUtil(getAddPartition(partitionInfo), host, false);
                        NettyTcpClient.getInstance().sendPackage(host, bytes);
                    }
                    break;
                    case 1: //删除分区
                    {
                        byte[] bytes = SmartBroadCastUtils.CloudUtil(getDeletePartition(partitionInfo), host, false);
                        NettyTcpClient.getInstance().sendPackage(host, bytes);
                    }
                    break;
                    case 2: //编辑分区
                    {
                        byte[] bytes = SmartBroadCastUtils.CloudUtil(getEditPartition(partitionInfo), host, false);
                        NettyTcpClient.getInstance().sendPackage(host, bytes);
                    }
                    break;
                }
            }
        } else {
            switch (type) {
                case 0: //添加分区
                    NettyUdpClient.getInstance().sendPackage(host, getAddPartition(partitionInfo));
                    break;
                case 1: //删除分区
                    NettyUdpClient.getInstance().sendPackage(host, getDeletePartition(partitionInfo));
                    break;
                case 2: //编辑分区
                    NettyUdpClient.getInstance().sendPackage(host, getEditPartition(partitionInfo));
                    break;
            }
        }


    }

    /**
     * 获取编辑分区的byte流
     *
     * @param partition
     * @return
     */
    public static byte[] getEditPartition(PartitionInfo partition) {
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("02B2");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //编辑分区操作符
        cmdStr.append("02");
        //编辑分区号 2字节
        cmdStr.append(intToUint16Hex(partition.getPartitionNum()));
        //编辑账户ID 1字节
        String accountId = Integer.toHexString(partition.getAccountId());
        cmdStr.append(accountId.length() == 1 ? "0" + accountId : accountId);
        //编辑分区名称 32字节
        cmdStr.append(chinese2Hex(partition.getPartitionName(), PARTITION_LENGTH));
        //编辑分区包含终端数 1字节
        String deviceNum = Integer.toHexString(partition.getDeviceCount());
        cmdStr.append((deviceNum.length() == 1) ? "0" + deviceNum : deviceNum);
        //增加八分区的设备列表，包含mac和物理分区
        List<PhysicalPartitionInfo> phycicalPartitionList = partition.getPhycicalPartitionList();
        for (PhysicalPartitionInfo info : phycicalPartitionList) {
            cmdStr.append(info.getMac().replace("-", ""));
            if (info.getPhycicalPartition() != null) {
                cmdStr.append(getDeviceZoneMsg(info.getPhycicalPartition()));
            } else {
                cmdStr.append("0000");
            }
        }
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //校验值
        cmdStr.append(checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");
        Log.i("partitionmsg", "getEditPartition: " + cmdStr);
        byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString().trim());
        return result;
    }


    /**
     * 获取删除分区的byte流
     *
     * @param partition
     * @return
     */
    public static byte[] getDeletePartition(PartitionInfo partition) {

        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("02B2");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //删除分区操作符
        cmdStr.append("01");
        //删除分区号 2字节
        cmdStr.append(intToUint16Hex(partition.getPartitionNum()));
        //删除账户ID 1字节
        String accountId = Integer.toHexString(partition.getAccountId());
        cmdStr.append(accountId.length() == 1 ? "0" + accountId : accountId);
        //删除分区名称 32字节
        cmdStr.append(chinese2Hex(partition.getPartitionName(), PARTITION_LENGTH));
        //删除分区包含终端数 1字节
        String deviceNum = Integer.toHexString(partition.getDeviceCount());
        cmdStr.append((deviceNum.length() == 1) ? "0" + deviceNum : deviceNum);
        //增加八分区的设备列表，包含mac和物理分区
        List<PhysicalPartitionInfo> phycicalPartitionList = partition.getPhycicalPartitionList();
        if (phycicalPartitionList != null) {
            for (PhysicalPartitionInfo info : phycicalPartitionList) {
                cmdStr.append(info.getMac().replace("-", ""));
                if (info.getPhycicalPartition() != null) {
                    cmdStr.append(getDeviceZoneMsg(info.getPhycicalPartition()));
                } else {
                    cmdStr.append("0000");
                }
            }
        }
        //添加校验值
        cmdStr.append(checkSum(cmdStr.substring(4)));
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex(cmdStr.substring(4).length() / 2));
        //添加结束标志
        cmdStr.append("55AA");
        Log.i("partitionmsg", "getDeletePartition: " + cmdStr);
        byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString().trim());
        return result;
    }

    /**
     * 获取添加分区的byte流
     *
     * @param partition
     * @return
     */
    public static byte[] getAddPartition(PartitionInfo partition) {

        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("02B2");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //添加分区操作符
        cmdStr.append("00");
        //添加分区号 2字节
        cmdStr.append("0000");
        //账户ID 1字节
        String accountId = Integer.toHexString(partition.getAccountId());
        cmdStr.append(accountId.length() == 1 ? "0" + accountId : accountId);
        //添加分区名称 32字节
        cmdStr.append(chinese2Hex(partition.getPartitionName(), PARTITION_LENGTH));
        //添加分区包含终端数 1字节
        String deviceNum = Integer.toHexString(partition.getDeviceCount());
        cmdStr.append((deviceNum.length() == 1) ? "0" + deviceNum : deviceNum);
        //增加八分区的设备列表，包含mac和物理分区
        List<PhysicalPartitionInfo> phycicalPartitionList = partition.getPhycicalPartitionList();
        for (PhysicalPartitionInfo info : phycicalPartitionList) {
            cmdStr.append(info.getMac().replace("-", ""));
            if (info.getPhycicalPartition() != null) {
                cmdStr.append(getDeviceZoneMsg(info.getPhycicalPartition()));
            } else {
                cmdStr.append("0000");
            }
        }
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex(cmdStr.substring(4).length() / 2));
        //添加校验值
        cmdStr.append((checkSum(cmdStr.substring(4))));
        //添加结束标志
        cmdStr.append("55AA");
        Log.i("partitionmsg", "getAddPartition: " + cmdStr);
        byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString().trim());
        return result;
    }


    /**
     * 获取分区信息
     *
     * @param data
     * @return
     */
    public static String getDeviceZoneMsg(int[] data) {

        int count = 0;
        int index = 0;
        for (int i : data) {
            count += (i * Math.pow(2, index));
            index++;
        }
        return intToUint16Hex(count);
    }
}
