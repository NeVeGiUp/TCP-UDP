package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.AccManageInfo;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditAccManageResult;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.ConfigUtils;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.bytesToHexString;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.checkSum;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.chinese2Hex;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.encryption;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.hexFormat;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * author： lghandroid
 * created：2018/9/3 11:33
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: 账户管理配置,添加，编辑，删除账户
 */


public class EditAccountManage {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int OPERATOR_LENGTH = 1;                             //账户操作符
    public final static int ACC_NUM_LENGTH = 1;                              //账户编号
    public final static int CONFIGURE_STATE = 1;                             //配置状态
    public final static int ACC_NAME_LENGTH = 32;                            //账户名
    public final static int ACC_PSW_LENGTH = 14;                             //账户密码
    public final static int ACC_PHONE_NUM_LENGTH = 32;                       //用户手机号


    public static EditAccountManage init() {
        return new EditAccountManage();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {
        EditAccManageResult accManageResult = getPartitionResult(list.get(0));
        Gson gson = new Gson();
        String json = gson.toJson(accManageResult);
        BaseBean bean = new BaseBean();
        bean.setType("editAccountManage");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        Log.i("jsonResult", "handler: " + jsonResult);
        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 添加，编辑，删除账户结果
     *
     * @param bytes
     * @return
     */
    public EditAccManageResult getPartitionResult(byte[] bytes) {
        //包头
        int head = HEAD_PACKAGE_LENGTH;
        //去掉包头，尾字节
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        EditAccManageResult accManageResult = new EditAccManageResult();
        accManageResult.setAccOperator(bytesToHexString(subBytes(bytes, 0, OPERATOR_LENGTH)));
        accManageResult.setAccNum(byteToInt(subBytes(bytes, OPERATOR_LENGTH, ACC_NUM_LENGTH)[0]));
        accManageResult.setConfigureState(bytesToHexString(subBytes(bytes, OPERATOR_LENGTH + ACC_NUM_LENGTH, CONFIGURE_STATE)));
        return accManageResult;
    }


    /**
     * 账户配置
     *
     * @param host          ip地址
     * @param accManageInfo AccManageInfo对象
     * @param type          操作类型：
     *                      （0：添加，1：编辑，2：删除）
     */
    public static void sendCMD(String host, AccManageInfo accManageInfo, int type) {
        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                switch (type) {
                    case 0: //添加
                    {
                        byte[] bytes = SmartBroadCastUtils.CloudUtil(getEditAccBytes(accManageInfo, "00"), host, false);
                        NettyTcpClient.getInstance().sendPackage(host, bytes);
                    }
                    break;
                    case 1: //编辑
                    {
                        byte[] bytes = SmartBroadCastUtils.CloudUtil(getEditAccBytes(accManageInfo, "01"), host, false);
                        NettyTcpClient.getInstance().sendPackage(host, bytes);
                    }
                    break;
                    case 2: //删除
                    {
                        byte[] bytes = SmartBroadCastUtils.CloudUtil(getEditAccBytes(accManageInfo, "02"), host, false);
                        NettyTcpClient.getInstance().sendPackage(host, bytes);
                    }
                    break;
                }
            }
        } else {
            switch (type) {
                case 0: //添加
                    NettyUdpClient.getInstance().sendPackage(host, getEditAccBytes(accManageInfo, "00"));
                    break;
                case 1: //编辑
                    NettyUdpClient.getInstance().sendPackage(host, getEditAccBytes(accManageInfo, "01"));
                    break;
                case 2: //删除
                    NettyUdpClient.getInstance().sendPackage(host, getEditAccBytes(accManageInfo, "02"));
                    break;
            }
        }


    }


    private static byte[] getEditAccBytes(AccManageInfo accManageInfo, String operator) {
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("03B9");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //操作符
        cmdStr.append(operator);
        //账户编号 1字节
        String accNum = Integer.toHexString(accManageInfo.getAccNum());
        cmdStr.append(accNum.length() == 1 ? "0" + accNum : accNum);
        //账户名 加密
        String accName = encryption(accManageInfo.getAccName(),ConfigUtils.USERNAME_SECRETKEY);
        cmdStr.append(hexFormat(accName, ACC_NAME_LENGTH));
        //更改用户权限 1字节 00:管理员  01:普通用户  02:来宾用户
        cmdStr.append(accManageInfo.getAccAuthority());
        //账户密码  加密
        String accPsw = encryption(accManageInfo.getAccPsw(),ConfigUtils.PASSWORD_SECRETKEY);
        cmdStr.append(hexFormat(accPsw, ACC_PSW_LENGTH));
        //手机号码
        cmdStr.append(chinese2Hex(accManageInfo.getUserPhoneNum(), ACC_PHONE_NUM_LENGTH));
        //可操作的设备总数
        String accDeviceCount = Integer.toHexString(accManageInfo.getAccDeviceCount());
        cmdStr.append(accDeviceCount.length() == 1 ? "0" + accDeviceCount : accDeviceCount);
        //添加分区终端MAC列表 6*N个字节  传进来得MAC列表 deviceMacList
        List<String> deviceMacList = accManageInfo.getAccMacList();
        for (int i = 0; i < deviceMacList.size(); i++) {
            //传进来得mac地址42-4c-45-00-0a-01，转成424c45000a01
            cmdStr.append(deviceMacList.get(i).replace("-", ""));
        }
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
