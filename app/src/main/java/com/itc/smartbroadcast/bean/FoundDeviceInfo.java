package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

import java.util.ArrayList;

/**
 * author： lghandroid
 * created：2018/8/20 13:54
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.bean
 * describe: 发现页面设备列表item数据载体
 */

public class FoundDeviceInfo extends BaseModel{

    //设备名称
    private String deviceName;
    //设备IP
    private String deviceIp;
    //设备状态
    private String deviceStatus;
    //设备MAC
    private String deviceMac;
    //设备音量
    private int deviceVoice;
    //设备型号
    private String deviceMedel;
    //设备版本信息
    private String deviceVersionMsg;
    //设备分区信息
    private ArrayList<Integer> deviceZoneMsg;
    //物理分区标志位信息
    private ArrayList<int[]> deviceZoneFlagMsg;
    //设备所属用户
    private ArrayList<Integer> deviceOfUser;
    //型号的类型
    private String zhDeviceType;
    //是否被选中
    private boolean checkStatus;
    //八分区配置
    private int [] deviceZone;
    //系统密码
    private String sysPassword;
    //目标主机IP
    private String hostIp;

    public String getSysPassword() {
        return sysPassword;
    }

    public void setSysPassword(String sysPassword) {
        this.sysPassword = sysPassword;
    }

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public int[] getDeviceZone() {
        return deviceZone;
    }

    public void setDeviceZone(int[] deviceZone) {
        this.deviceZone = deviceZone;
    }

    public ArrayList<int[]> getDeviceZoneFlagMsg() {
        return deviceZoneFlagMsg;
    }

    public void setDeviceZoneFlagMsg(ArrayList<int[]> deviceZoneFlagMsg) {
        this.deviceZoneFlagMsg = deviceZoneFlagMsg;
    }


    public ArrayList<Integer> getDeviceOfUser() {
        return deviceOfUser;
    }

    public void setDeviceOfUser(ArrayList<Integer> deviceOfUser) {
        this.deviceOfUser = deviceOfUser;
    }

    public String getZhDeviceType() {
        return zhDeviceType;
    }

    public void setZhDeviceType(String zhDeviceType) {
        this.zhDeviceType = zhDeviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public int getDeviceVoice() {
        return deviceVoice;
    }

    public void setDeviceVoice(int deviceVoice) {
        this.deviceVoice = deviceVoice;
    }

    public String getDeviceMedel() {
        return deviceMedel;
    }

    public void setDeviceMedel(String deviceMedel) {
        this.deviceMedel = deviceMedel;
    }

    public String getDeviceVersionMsg() {
        return deviceVersionMsg;
    }

    public void setDeviceVersionMsg(String deviceVersionMsg) {
        this.deviceVersionMsg = deviceVersionMsg;
    }

    public ArrayList<Integer> getDeviceZoneMsg() {
        return deviceZoneMsg;
    }

    public void setDeviceZoneMsg(ArrayList<Integer> deviceZoneMsg) {
        this.deviceZoneMsg = deviceZoneMsg;
    }

    public boolean isCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(boolean checkStatus) {
        this.checkStatus = checkStatus;
    }

}
