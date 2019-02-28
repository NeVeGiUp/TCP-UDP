package com.itc.smartbroadcast.bean;

/**
 * 即时任务
 * Created by lik on 18-8-30.
 */

public class InstantTask {

    //账户编号
    private int accountNum;
    //任务编号
    private int taskNum;
    //任务名称
    private String taskName;
    //终端Mac地址
    private String terminalMac;
    //任务优先级
    private int priority;
    //音量
    private int volume;
    //持续时间
    private int continueDate;
    //遥控按键信息
    private int remoteControlKeyInfo;
    //即时任务状态
    private int status;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(int accountNum) {
        this.accountNum = accountNum;
    }

    public int getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(int taskNum) {
        this.taskNum = taskNum;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTerminalMac() {
        return terminalMac;
    }

    public void setTerminalMac(String terminalMac) {
        this.terminalMac = terminalMac;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getContinueDate() {
        return continueDate;
    }

    public void setContinueDate(int continueDate) {
        this.continueDate = continueDate;
    }

    public int getRemoteControlKeyInfo() {
        return remoteControlKeyInfo;
    }

    public void setRemoteControlKeyInfo(int remoteControlKeyInfo) {
        this.remoteControlKeyInfo = remoteControlKeyInfo;
    }
}
