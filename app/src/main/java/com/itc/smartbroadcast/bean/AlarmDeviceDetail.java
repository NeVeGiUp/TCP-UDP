package com.itc.smartbroadcast.bean;

import java.util.List;

/**
 * 报警任务详情
 */
public class AlarmDeviceDetail {

    //报警设备端口数
    private int portCount;
    /**
     * 报警设备端口响应模式
     * 0:单区报警
     * 1:邻区+1报警
     * 2:邻区+2报警
     * 3:邻区+3报警
     * 4:邻区+4报警
     * 5:全区报警
     */
    private int portResponseMode;
    /**
     * 报警触发模式
     * 0:自动解除报警
     * 1:手动解除报警
     */
    private int triggerMode;
    /**
     * 报警播放模式
     * 0:单曲循环
     * 1:单曲播放
     */
    private int playMode;
    /**
     * 报警播放音量
     */
    private int playVolume;
    /**
     * 报警端口配置标志位
     * int[]
     * 从第0~15位依次代表端口1~16是否配置（0否1是）
     */
    private int [] isAlarmPortSet;

    private String deviceMac;

    private String deviceName;

    private String deviceIp;

    private List<String> portNameList;

    public List<String> getPortNameList() {
        return portNameList;
    }

    public void setPortNameList(List<String> portNameList) {
        this.portNameList = portNameList;
    }

    public int getPlayVolume() {
        return playVolume;
    }

    public void setPlayVolume(int playVolume) {
        this.playVolume = playVolume;
    }

    public int[] getIsAlarmPortSet() {
        return isAlarmPortSet;
    }

    public void setIsAlarmPortSet(int[] isAlarmPortSet) {
        this.isAlarmPortSet = isAlarmPortSet;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public int getPortCount() {
        return portCount;
    }

    public void setPortCount(int portCount) {
        this.portCount = portCount;
    }

    public int getPortResponseMode() {
        return portResponseMode;
    }

    public void setPortResponseMode(int portResponseMode) {
        this.portResponseMode = portResponseMode;
    }

    public int getTriggerMode() {
        return triggerMode;
    }

    public void setTriggerMode(int triggerMode) {
        this.triggerMode = triggerMode;
    }

    public int getPlayMode() {
        return playMode;
    }

    public void setPlayMode(int playMode) {
        this.playMode = playMode;
    }
}
