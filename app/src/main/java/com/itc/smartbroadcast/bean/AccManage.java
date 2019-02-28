package com.itc.smartbroadcast.bean;


import java.util.List;

public class AccManage{
    //用户类型
    private String accountType;
    //账户编号
    private int accountNum;
    //账户名称
    private String accountName;
    //用户密码
    private String accountPsw;
    //登录时间
    private String loginedTime;
    //账户创建时间
    private String accountCreateTime;
    //账户设备总数
    private int accountDeviceTotal;

    public int getAccountDeviceTotal() {
        return accountDeviceTotal;
    }

    public void setAccountDeviceTotal(int accountDeviceTotal) {
        this.accountDeviceTotal = accountDeviceTotal;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public int getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(int accountNum) {
        this.accountNum = accountNum;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountPsw() {
        return accountPsw;
    }

    public void setAccountPsw(String accountPsw) {
        this.accountPsw = accountPsw;
    }

    public String getLoginedTime() {
        return loginedTime;
    }

    public void setLoginedTime(String loginedTime) {
        this.loginedTime = loginedTime;
    }

    public String getAccountCreateTime() {
        return accountCreateTime;
    }

    public void setAccountCreateTime(String accountCreateTime) {
        this.accountCreateTime = accountCreateTime;
    }
}