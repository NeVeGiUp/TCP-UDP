package com.itc.smartbroadcast.bean;

/**
 * @Content :  FM任务状态
 * @Author : lik
 * @Time : 18-9-13 下午5:26
 */
public class FMInstantStatus {

    //任务编号
    private int taskNum;
    //随机ID号
    private int randomId;
    //设备mac地址
    private String deviceMac;
    //设备型号
    private String deviceModel;
    //设备音量
    private int deviceVolume;
    //调制模式，0为FM模式，1为AM模式
    private int modulationMode;
    //播放状态(1:播放，2:暂停)
    private int playStatus;
    //总频道数
    private int channelSize;
    //当前频道号
    private int nowChannelNum;
    //当前频率
    private int nowChannelFrequency;


    public int getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(int taskNum) {
        this.taskNum = taskNum;
    }

    public int getRandomId() {
        return randomId;
    }

    public void setRandomId(int randomId) {
        this.randomId = randomId;
    }

    public int getDeviceVolume() {
        return deviceVolume;
    }

    public void setDeviceVolume(int deviceVolume) {
        this.deviceVolume = deviceVolume;
    }

    public int getPlayStatus() {
        return playStatus;
    }

    public void setPlayStatus(int playStatus) {
        this.playStatus = playStatus;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public int getModulationMode() {
        return modulationMode;
    }

    public void setModulationMode(int modulationMode) {
        this.modulationMode = modulationMode;
    }

    public int getChannelSize() {
        return channelSize;
    }

    public void setChannelSize(int channelSize) {
        this.channelSize = channelSize;
    }

    public int getNowChannelNum() {
        return nowChannelNum;
    }

    public void setNowChannelNum(int nowChannelNum) {
        this.nowChannelNum = nowChannelNum;
    }

    public int getNowChannelFrequency() {
        return nowChannelFrequency;
    }

    public void setNowChannelFrequency(int nowChannelFrequency) {
        this.nowChannelFrequency = nowChannelFrequency;
    }
}
