package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

import java.util.List;

/**
 * Created by ligh on 18-9-5.
 * describe _登录成功后用户信息
 */

public class LoginedInfo extends BaseModel {

    //用户名
    private String userName;
    //用户手机号
    private String userPhoneNum;
    //登录状态
    private String loginState;
    //系统密码
    private String systemPsw;
    //账户类型
    private String userType;
    //账户编号
    private int userNum;
    //设备MAC
    private String deviceMac;
    //设备机械码
    private String deviceMechanicalCode;
    //设备类型
    private String deviceType;
    //设备品牌
    private String deviceBrand;
    //主机名称
    private String hostName;
    //主机版本
    private String hostVersion;
    //IP获取方式
    private String ipAcquisitionMode;
    //IP掩码
    private String subnetMask;
    //IP网关
    private String gateway;
    //注册状态
    private String registerState;
    //注册有效剩余时间
    private int registerEffectiveTime;
    //可操作设备列表总数
    private int operableDeviceCount;
    //可操作的设备MAC
    private List<String> operableDeviceMacList;

    public String getUserPhoneNum() {
        return userPhoneNum;
    }

    public void setUserPhoneNum(String userPhoneNum) {
        this.userPhoneNum = userPhoneNum;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLoginState() {
        return loginState;
    }

    public void setLoginState(String loginState) {
        this.loginState = loginState;
    }

    public String getSystemPsw() {
        return systemPsw;
    }

    public void setSystemPsw(String systemPsw) {
        this.systemPsw = systemPsw;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public int getUserNum() {
        return userNum;
    }

    public void setUserNum(int userNum) {
        this.userNum = userNum;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public String getDeviceMechanicalCode() {
        return deviceMechanicalCode;
    }

    public void setDeviceMechanicalCode(String deviceMechanicalCode) {
        this.deviceMechanicalCode = deviceMechanicalCode;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceBrand() {
        return deviceBrand;
    }

    public void setDeviceBrand(String deviceBrand) {
        this.deviceBrand = deviceBrand;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getHostVersion() {
        return hostVersion;
    }

    public void setHostVersion(String hostVersion) {
        this.hostVersion = hostVersion;
    }


    public String getIpAcquisitionMode() {
        return ipAcquisitionMode;
    }

    public void setIpAcquisitionMode(String ipAcquisitionMode) {
        this.ipAcquisitionMode = ipAcquisitionMode;
    }

    public String getSubnetMask() {
        return subnetMask;
    }

    public void setSubnetMask(String subnetMask) {
        this.subnetMask = subnetMask;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getRegisterState() {
        return registerState;
    }

    public void setRegisterState(String registerState) {
        this.registerState = registerState;
    }

    public int getRegisterEffectiveTime() {
        return registerEffectiveTime;
    }

    public void setRegisterEffectiveTime(int registerEffectiveTime) {
        this.registerEffectiveTime = registerEffectiveTime;
    }

    public int getOperableDeviceCount() {
        return operableDeviceCount;
    }

    public void setOperableDeviceCount(int operableDeviceCount) {
        this.operableDeviceCount = operableDeviceCount;
    }

    public List<String> getOperableDeviceMacList() {
        return operableDeviceMacList;
    }

    public void setOperableDeviceMacList(List<String> operableDeviceMacList) {
        this.operableDeviceMacList = operableDeviceMacList;
    }
}
