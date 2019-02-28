package com.itc.smartbroadcast.bean;

import java.util.List;

/**
 * @Content :  CD音乐列表
 * @Author : lik
 * @Time : 18-9-13 下午5:26
 */
public class CDMusicList {

    //任务编号
    private int taskNum;
    //随机ID号
    private int randomId;
    //当前页
    private int nowPageNum;
    //设备型号
    private String deviceModel;
    //存储设备类型(0:光盘，1:U盘，2:SD卡)
    private int saveDeviceType;
    /**
     * 文件名编码格式：
     * 0:unicode(默认模式)
     * 1：cp936
     */
    private int musicNameCode;
    //文件名字节长度
    private int musicNameLength;
    //音乐名
    private List<CDMusic> musicNameList;

    private String terminalMac;


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

    public int getNowPageNum() {
        return nowPageNum;
    }

    public void setNowPageNum(int nowPageNum) {
        this.nowPageNum = nowPageNum;
    }

    public String getTerminalMac() {
        return terminalMac;
    }

    public void setTerminalMac(String terminalMac) {
        this.terminalMac = terminalMac;
    }

    public int getNowMusicNum() {
        return nowPageNum;
    }

    public void setNowMusicNum(int nowPageNum) {
        this.nowPageNum = nowPageNum;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public int getSaveDeviceType() {
        return saveDeviceType;
    }

    public void setSaveDeviceType(int saveDeviceType) {
        this.saveDeviceType = saveDeviceType;
    }

    public int getMusicNameCode() {
        return musicNameCode;
    }

    public void setMusicNameCode(int musicNameCode) {
        this.musicNameCode = musicNameCode;
    }

    public int getMusicNameLength() {
        return musicNameLength;
    }

    public void setMusicNameLength(int musicNameLength) {
        this.musicNameLength = musicNameLength;
    }

    public List<CDMusic> getMusicNameList() {
        return musicNameList;
    }

    public void setMusicNameList(List<CDMusic> musicNameList) {
        this.musicNameList = musicNameList;
    }



}
