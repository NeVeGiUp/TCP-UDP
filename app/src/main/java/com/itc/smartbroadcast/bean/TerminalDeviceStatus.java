package com.itc.smartbroadcast.bean;

/**
 * 终端设备状态
 * Created by lik on 18-9-28.
 */

public class TerminalDeviceStatus {

    //播放状态(0:正在播放中，1:繁忙，2:离线)
    private int playStatus;
    //功放当前播放音频类型（0：当前优先级为S，1：当前优先级为P，2：当前优先级为E）
    private int priority;
    //通道状态int数组，从下标为0开始到下标为4分别为通道1-5的状态，若值为1表示繁忙，0空闲
    private int [] channelStatus;
    //发送目标IP
    private String targetIp;
    //发送目标mac
    private String targetMac;

    public int getPlayStatus() {
        return playStatus;
    }

    public void setPlayStatus(int playStatus) {
        this.playStatus = playStatus;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int[] getChannelStatus() {
        return channelStatus;
    }

    public void setChannelStatus(int[] channelStatus) {
        this.channelStatus = channelStatus;
    }

    public String getTargetIp() {
        return targetIp;
    }

    public void setTargetIp(String targetIp) {
        this.targetIp = targetIp;
    }

    public String getTargetMac() {
        return targetMac;
    }

    public void setTargetMac(String targetMac) {
        this.targetMac = targetMac;
    }
}
