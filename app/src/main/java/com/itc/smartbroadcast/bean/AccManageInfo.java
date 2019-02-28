package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

import java.util.List;

/**
 * author： lghandroid
 * created：2018/9/3 14:36
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.bean
 * describe: _编辑账户管理信息
 */

public class AccManageInfo extends BaseModel {
    //用户管理操作符
    private String accOperator;
    //账户编号
    private int accNum;
    //配置的账户名
    private String accName;
    //更改账户权限
    private String accAuthority;
    //账户密码
    private String accPsw;
    //用户手机号
    private String userPhoneNum;
    //可操作设备总数
    private int accDeviceCount;
    //可操作设备的MAC列表
    private List<String> accMacList;

    public String getUserPhoneNum() {
        return userPhoneNum;
    }

    public void setUserPhoneNum(String phoneNum) {
        this.userPhoneNum = phoneNum;
    }

    public String getAccOperator() {
        return accOperator;
    }

    public void setAccOperator(String accOperator) {
        this.accOperator = accOperator;
    }

    public int getAccNum() {
        return accNum;
    }

    public void setAccNum(int accNum) {
        this.accNum = accNum;
    }

    public String getAccName() {
        return accName;
    }

    public void setAccName(String accName) {
        this.accName = accName;
    }

    public String getAccAuthority() {
        return accAuthority;
    }

    public void setAccAuthority(String accAuthority) {
        this.accAuthority = accAuthority;
    }

    public String getAccPsw() {
        return accPsw;
    }

    public void setAccPsw(String accPsw) {
        this.accPsw = accPsw;
    }

    public int getAccDeviceCount() {
        return accDeviceCount;
    }

    public void setAccDeviceCount(int accDeviceCount) {
        this.accDeviceCount = accDeviceCount;
    }

    public List<String> getAccMacList() {
        return accMacList;
    }

    public void setAccMacList(List<String> accMacList) {
        this.accMacList = accMacList;
    }
}
