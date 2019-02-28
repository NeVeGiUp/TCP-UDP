package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseResultModel;

import java.util.List;

/**
 * @ author : lgh_ai
 * @ e-mail : lgh_developer@163.com
 * @ date   : 19-2-17 上午11:18
 * @ desc   : 配置目标主机参数
 */
public class ConfigureTargetHostInfo extends BaseResultModel {

    //目标主机IP
    private String ip;
    //终端设备总数
    private int deviceTotal;
    //设备总数 * 设备MAC
    private List<String> macList;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getDeviceTotal() {
        return deviceTotal;
    }

    public void setDeviceTotal(int deviceTotal) {
        this.deviceTotal = deviceTotal;
    }

    public List<String> getMacList() {
        return macList;
    }

    public void setMacList(List<String> macList) {
        this.macList = macList;
    }
}
