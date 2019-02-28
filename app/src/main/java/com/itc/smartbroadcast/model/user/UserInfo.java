package com.itc.smartbroadcast.model.user;

import com.itc.smartbroadcast.base.BaseModel;

/**
 * author： lghandroid
 * created：2018/8/4 10:51
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.model.user
 * describe: 用于存储用户登录信息
 */

public class UserInfo extends BaseModel {

    private String username; //用户名
    private String loginstatus; //登录状态
    private String syspassword; //系统密码
    private String accounttype; //账户类型
    private String accountnum; //账户编号
    private String dvmac;  //设备MAC
    private String dvtype; //设备类型
    private String dvbrand; //设备品牌
    private String ipaccess; //IP获取方式
    private String ipcode; //IP掩码
    private String ipgateway; //IP网关
    private String regstatus; //注册状态
    private String regtime; //注册有效剩余时间
    private String dvlistnum; //可操作设备列表总数
    private String dvmacoperate; //可操作的终端设备MAC

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLoginstatus() {
        return loginstatus;
    }

    public void setLoginstatus(String loginstatus) {
        this.loginstatus = loginstatus;
    }

    public String getSyspassword() {
        return syspassword;
    }

    public void setSyspassword(String syspassword) {
        this.syspassword = syspassword;
    }

    public String getAccounttype() {
        return accounttype;
    }

    public void setAccounttype(String accounttype) {
        this.accounttype = accounttype;
    }

    public String getAccountnum() {
        return accountnum;
    }

    public void setAccountnum(String accountnum) {
        this.accountnum = accountnum;
    }

    public String getDvmac() {
        return dvmac;
    }

    public void setDvmac(String dvmac) {
        this.dvmac = dvmac;
    }

    public String getDvtype() {
        return dvtype;
    }

    public void setDvtype(String dvtype) {
        this.dvtype = dvtype;
    }

    public String getDvbrand() {
        return dvbrand;
    }

    public void setDvbrand(String dvbrand) {
        this.dvbrand = dvbrand;
    }

    public String getIpaccess() {
        return ipaccess;
    }

    public void setIpaccess(String ipaccess) {
        this.ipaccess = ipaccess;
    }

    public String getIpcode() {
        return ipcode;
    }

    public void setIpcode(String ipcode) {
        this.ipcode = ipcode;
    }

    public String getIpgateway() {
        return ipgateway;
    }

    public void setIpgateway(String ipgateway) {
        this.ipgateway = ipgateway;
    }

    public String getRegstatus() {
        return regstatus;
    }

    public void setRegstatus(String regstatus) {
        this.regstatus = regstatus;
    }

    public String getRegtime() {
        return regtime;
    }

    public void setRegtime(String regtime) {
        this.regtime = regtime;
    }

    public String getDvlistnum() {
        return dvlistnum;
    }

    public void setDvlistnum(String dvlistnum) {
        this.dvlistnum = dvlistnum;
    }

    public String getDvmacoperate() {
        return dvmacoperate;
    }

    public void setDvmacoperate(String dvmacoperate) {
        this.dvmacoperate = dvmacoperate;
    }


}
