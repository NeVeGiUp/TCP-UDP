package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

import java.util.List;

/**
 * Created by Ligh on 18-9-10.
 * describe _账户列表信息
 */

public class AccountListInfo extends BaseModel {
    //用户总数
    private int accountTotal;
    //账户资料*总数
    private List<AccountDataInner> accountData;

    public int getAccountTotal() {
        return accountTotal;
    }

    public void setAccountTotal(int accountTotal) {
        this.accountTotal = accountTotal;
    }

    public List<AccountDataInner> getAccountData() {
        return accountData;
    }

    public void setAccountData(List<AccountDataInner> accountData) {
        this.accountData = accountData;
    }

    public static class AccountDataInner {
        //用户类型
        private String accountType;
        //账户编号
        private int accountNum;
        //账户名称
        private String accountName;
        //账户手机号
        private String accountPhoneNum;
        //用户密码
        private String accountPsw;
        //登录时间
        private String loginedTime;
        //账户创建时间
        private String accountCreateTime;
        //账户设备总数
        private int accountDeviceTotal;

        public String getAccountPhoneNum() {
            return accountPhoneNum;
        }

        public void setAccountPhoneNum(String accountPhoneNum) {
            this.accountPhoneNum = accountPhoneNum;
        }

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
}
