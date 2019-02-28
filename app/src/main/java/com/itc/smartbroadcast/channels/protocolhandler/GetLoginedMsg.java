package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.LoginedInfo;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.util.ConfigUtils;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteArrayToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToStr;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.bytesToHexString;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.checkSum;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.chinese2Hex;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.decrypt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.encryption;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.hexFormat;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.hexStringToString;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.hexToVersion;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.hexstrToIp;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.hexstrToMac;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * author： lghandroid
 * created：2018/8/21 17:28
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: _获取登录信息
 */

public class GetLoginedMsg {

    private static final int USER_NAME_LENGTH = 32;                              //用户名
    private static final int USER_PHONE_NUM_LENGTH = 32;                         //用户手机号
    private static final int USER_PSW_LENGTH = 14;                               //用户密码
    private static final int LOGIN_STATE_LENGTH = 1;                             //登录状态
    private static final int SYSTEM_PSW_LENGTH = 14;                             //系统密码
    private static final int USER_TYPE_LENGTH = 1;                               //账户类型
    private static final int USER_NUM_LENGTH = 1;                                //账户编号
    private static final int DEVICE_MAC_LENGTH = 6;                              //设备mac
    private static final int DEVICE_MECHANICAL_CODE_LENGTH = 10;                 //设备机械码
    private static final int DEVICE_TYPE_LENGTH = 32;                            //设备类型
    private static final int DEVICE_BRAND_LENGTH = 32;                           //设备品牌
    private static final int HOST_NAME_LENGTH = 32;                              //主机名称
    private static final int HOST_VERSION_LENGTH = 2;                            //主机版本
    private static final int IP_MODE_LENGTH = 1;                                 //IP获取方式
    private static final int IP_SUBNET_MASK_LENGTH = 4;                          //IP掩码
    private static final int IP_GATEWAY_LENGTH = 4;                              //IP网关
    private static final int REGISTER_STATE_LENGTH = 1;                          //注册状态
    private static final int REGISTER_EFFECTIVE_TIME_LENGTH = 2;                 //注册有效剩余时间
    private static final int OPERABLE_DEVICE_COUNT_LENGTH = 1;                   //可操作设备列表总数
    private static final int OPERABLE_DEVICE_MAC_LENGTH = 6;                     //可操作设备mac
    private static final int HEAD_PACKET_LENGTH = 28;                            //包头
    private static final int END_PACKET_LENGTH = 4;                              //包尾

    public static GetLoginedMsg init() {
        return new GetLoginedMsg();
    }


    /**
     * 业务处理
     *
     * @param list 获取登录信息
     */
    public void handler(List<byte[]> list) {
        LoginedInfo loginedInfo = getLoginedMsg(list.get(0));
        Gson gson = new Gson();
        String json = gson.toJson(loginedInfo);
        BaseBean bean = new BaseBean();
        bean.setType("getLoginedMsg");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        Log.i("jsonResult", "handler: " + jsonResult);
        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 通过byte数组获取LoginedInfo对象
     *
     * @param bytes
     * @return Scheme对象
     */
    public LoginedInfo getLoginedMsg(byte[] bytes) {
        int index = 0;
        //获取有效字节数
        bytes = subBytes(bytes, HEAD_PACKET_LENGTH, bytes.length - END_PACKET_LENGTH - HEAD_PACKET_LENGTH);
        LoginedInfo loginedInfo = new LoginedInfo();
        //用户名 解密(因后台下发用户名为加密)
        String userName = decrypt(subBytes(bytes, 0, USER_NAME_LENGTH),ConfigUtils.USERNAME_SECRETKEY);
        index += USER_NAME_LENGTH;
        //用户手机号
        String userPhoneNum = byteToStr((subBytes(bytes, index, USER_PHONE_NUM_LENGTH)));
        index += USER_PHONE_NUM_LENGTH;
        //登录状态
        String loginState = bytesToHexString(subBytes(bytes, index, LOGIN_STATE_LENGTH));
        index += LOGIN_STATE_LENGTH;
        //系统密码
        String systemPsw = byteToStr(subBytes(bytes, index, SYSTEM_PSW_LENGTH));
        index += SYSTEM_PSW_LENGTH;
        //账户类型
        String userType = bytesToHexString(subBytes(bytes, index, USER_TYPE_LENGTH));
        index += USER_TYPE_LENGTH;
        //账户编号
        int userNum = byteToInt(subBytes(bytes, index, USER_NUM_LENGTH)[0]);
        index += USER_NUM_LENGTH;
        //设备MAC
        String deviceMac = hexstrToMac(bytesToHexString(subBytes(bytes, index, DEVICE_MAC_LENGTH)));
        index += DEVICE_MAC_LENGTH;
        //设备机械码
        String deviceMechanicalCode = bytesToHexString(subBytes(bytes, index, DEVICE_MECHANICAL_CODE_LENGTH));
        index += DEVICE_MECHANICAL_CODE_LENGTH;
        //设备类型
        String deviceType = byteToStr(subBytes(bytes, index, DEVICE_TYPE_LENGTH));
        index += DEVICE_TYPE_LENGTH;
        //设备品牌
        String deviceBrand = byteToStr(subBytes(bytes, index, DEVICE_BRAND_LENGTH));
        index += DEVICE_BRAND_LENGTH;
        //主机名称
        String hostName = byteToStr(subBytes(bytes, index, HOST_NAME_LENGTH));
        index += HOST_NAME_LENGTH;
        //主机版本
        String hostVersion = hexToVersion(bytesToHexString(subBytes(bytes, index, HOST_VERSION_LENGTH)));
        index += HOST_VERSION_LENGTH;
        //IP获取方式
        String ipMode = bytesToHexString(subBytes(bytes, index, IP_MODE_LENGTH));
        index += IP_MODE_LENGTH;
        //IP掩码
        String ipSubnetMask = hexstrToIp(bytesToHexString(subBytes(bytes, index, IP_SUBNET_MASK_LENGTH)));
        index += IP_SUBNET_MASK_LENGTH;
        //IP网关
        String ipGateway = hexstrToIp(bytesToHexString(subBytes(bytes, index, IP_GATEWAY_LENGTH)));
        index += IP_GATEWAY_LENGTH;
        //注册状态
        String registerState = bytesToHexString(subBytes(bytes, index, REGISTER_STATE_LENGTH));
        index += REGISTER_STATE_LENGTH;
        //注册有效剩余时间
        int registerEffectiveTime = byteArrayToInt(subBytes(bytes, index, REGISTER_EFFECTIVE_TIME_LENGTH));
        index += REGISTER_EFFECTIVE_TIME_LENGTH;
        //可操作设备列表总数
        int operableDeviceCount = byteToInt(subBytes(bytes, index, OPERABLE_DEVICE_COUNT_LENGTH)[0]);
        index += OPERABLE_DEVICE_COUNT_LENGTH;
        //可操作设备mac
        ArrayList<String> macList = new ArrayList<>();
        //有可操作的终端情况下，获取MAC列表
        if (0 != operableDeviceCount) {
            String hexMac = bytesToHexString(subBytes(bytes, index, OPERABLE_DEVICE_MAC_LENGTH * operableDeviceCount));
            for (int i = 0; i < hexMac.length(); i += 12) {
                String operableDeviceMac = hexstrToMac(hexMac.substring(i, i + 12));
                macList.add(operableDeviceMac);
            }
        }
        loginedInfo.setUserName(userName);
        loginedInfo.setUserPhoneNum(userPhoneNum);
        loginedInfo.setLoginState(loginState);
        loginedInfo.setSystemPsw(systemPsw);
        loginedInfo.setUserType(userType);
        loginedInfo.setUserNum(userNum);
        loginedInfo.setDeviceMac(deviceMac);
        loginedInfo.setDeviceMechanicalCode(deviceMechanicalCode);
        loginedInfo.setDeviceType(deviceType);
        loginedInfo.setDeviceBrand(deviceBrand);
        loginedInfo.setHostName(hostName);
        loginedInfo.setHostVersion(hostVersion);
        loginedInfo.setIpAcquisitionMode(ipMode);
        loginedInfo.setSubnetMask(ipSubnetMask);
        loginedInfo.setGateway(ipGateway);
        loginedInfo.setRegisterState(registerState);
        loginedInfo.setRegisterEffectiveTime(registerEffectiveTime);
        loginedInfo.setOperableDeviceCount(operableDeviceCount);
        loginedInfo.setOperableDeviceMacList(macList);
        return loginedInfo;
    }

    /**
     * 发送ip，用户名，密码,登录设备（00：手机设备  01：话筒设备）
     */
    public static void sendCMD(String host, String userName, String userPsw, String loginDevice) {
        NettyUdpClient.getInstance().sendPackage(host, getLoginCmd(userName, userPsw, loginDevice));
    }

    /**
     * 用户名，密码,登录设备（0：登录  1：登出）
     */
    public static void sendCloudCMD(String host, String phoneNum, int isLogin) {
        //判断TCP连接是否连接上
        if (NettyTcpClient.isConnSucc) {
            byte[] bytes = SmartBroadCastUtils.CloudUtil(getLoginCloudCmd(phoneNum, isLogin), host, false);
            NettyTcpClient.getInstance().sendPackage(host, bytes);
        }
    }


    /**
     * 获取登录发送指令
     *
     * @return
     */
    public static byte[] getLoginCloudCmd(String phoneNum, int isLogin) {
        StringBuffer cmdStr = new StringBuffer();
        //起始标志
        cmdStr.append("AA55");
        //长度
        cmdStr.append("0000");
        //命令
        cmdStr.append("06BE");
        //本机mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //登录操作符
        if (isLogin == 0) {
            cmdStr.append("00");
        } else {
            cmdStr.append("01");
        }
        //手机号
        cmdStr.append(chinese2Hex(phoneNum, USER_PHONE_NUM_LENGTH));
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //校验值
        cmdStr.append(checkSum(cmdStr.substring(4)));
        //结束标志
        cmdStr.append("55AA");
        byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return result;
    }

    /**
     * 获取登录发送指令
     *
     * @return
     */
    public static byte[] getLoginCmd(String userName, String userPsw, String loginDevice) {
        StringBuffer cmdStr = new StringBuffer();
        //起始标志
        cmdStr.append("AA55");
        //长度
        cmdStr.append("0000");
        //命令
        cmdStr.append("00B9");
        //本机mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //用户名 加密
        userName = SmartBroadCastUtils.encryption(userName,ConfigUtils.USERNAME_SECRETKEY);
        cmdStr.append(hexFormat(userName, USER_NAME_LENGTH));
        //用户密码 加密
        userPsw = SmartBroadCastUtils.encryption(userPsw,ConfigUtils.PASSWORD_SECRETKEY);
        cmdStr.append(hexFormat(userPsw, USER_PSW_LENGTH));
        //登录设备
        cmdStr.append(loginDevice);
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //校验值
        cmdStr.append(checkSum(cmdStr.substring(4)));
        //结束标志
        cmdStr.append("55AA");
        byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return result;
    }


}
