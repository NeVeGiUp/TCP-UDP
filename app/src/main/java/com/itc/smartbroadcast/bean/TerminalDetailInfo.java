package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

/**
 * author： lghandroid
 * created：2018/8/24 15:00
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.bean
 * describe: _终端详情信息
 * terminal --- tmn 缩写
 */

public class TerminalDetailInfo extends BaseModel {

    //设备MAC
    private String terminalMac;
    //IP获取模式
    private int terminalIpMode;
    //子网掩码
    private String terminalSubnet;
    //网关
    private String terminalGateway;
    //音源类别
    private String terminalSoundCate;
    //设备优先级默音权限
    private String terminalPriority;
    //默音音量
    private int terminalDefVolume;

    /*---------------------编辑终端详情需要增加的字段---------------------*/
    //设备名称
    private String terminalName;
    //设备IP地址
    private String terminalIp;
    //设置设备默认音量
    private int terminalSetVolume;
    //系统旧密码
    private String terminalOldPsw;
    //系统新密码
    private String terminalNewPsw;


    public String getTerminalMac() {
        return terminalMac;
    }

    public void setTerminalMac(String terminalMac) {
        this.terminalMac = terminalMac;
    }

    public int getTerminalIpMode() {
        return terminalIpMode;
    }

    public void setTerminalIpMode(int terminalIpMode) {
        this.terminalIpMode = terminalIpMode;
    }

    public String getTerminalSubnet() {
        return terminalSubnet;
    }

    public void setTerminalSubnet(String terminalSubnet) {
        this.terminalSubnet = terminalSubnet;
    }

    public String getTerminalGateway() {
        return terminalGateway;
    }

    public void setTerminalGateway(String terminalGateway) {
        this.terminalGateway = terminalGateway;
    }

    public String getTerminalSoundCate() {
        return terminalSoundCate;
    }

    public void setTerminalSoundCate(String terminalSoundCate) {
        this.terminalSoundCate = terminalSoundCate;
    }

    public String getTerminalPriority() {
        return terminalPriority;
    }

    public void setTerminalPriority(String terminalPriority) {
        this.terminalPriority = terminalPriority;
    }

    public int getTerminalDefVolume() {
        return terminalDefVolume;
    }

    public void setTerminalDefVolume(int terminalDefVolume) {
        this.terminalDefVolume = terminalDefVolume;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

    public String getTerminalIp() {
        return terminalIp;
    }

    public void setTerminalIp(String terminalIp) {
        this.terminalIp = terminalIp;
    }

    public int getTerminalSetVolume() {
        return terminalSetVolume;
    }

    public void setTerminalSetVolume(int terminalSetVolume) {
        this.terminalSetVolume = terminalSetVolume;
    }

    public String getTerminalOldPsw() {
        return terminalOldPsw;
    }

    public void setTerminalOldPsw(String terminalOldPsw) {
        this.terminalOldPsw = terminalOldPsw;
    }

    public String getTerminalNewPsw() {
        return terminalNewPsw;
    }

    public void setTerminalNewPsw(String terminalNewPsw) {
        this.terminalNewPsw = terminalNewPsw;
    }



}
