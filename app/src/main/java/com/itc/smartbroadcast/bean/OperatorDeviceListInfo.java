package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by Ligh on 18-9-30.
 * describe _账户可操作设备列表信息
 */

public class OperatorDeviceListInfo extends BaseModel{

    //账户编号
    private int accountNum;
    //可操作设备列表总数
    private int deviceTotal;
    //可操作设备MAC
    private List<String> operableDeviceMacList;
    //账户拥有设备MAC
    private List<FoundDeviceInfo> accDeviceList;

    public List<FoundDeviceInfo> getAccDeviceList() {
        return accDeviceList;
    }

    public void setAccDeviceList(List<FoundDeviceInfo> accDeviceList) {
        this.accDeviceList = accDeviceList;
    }

    public int getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(int accountNum) {
        this.accountNum = accountNum;
    }

    public int getDeviceTotal() {
        return deviceTotal;
    }

    public void setDeviceTotal(int deviceTotal) {
        this.deviceTotal = deviceTotal;
    }

    public List<String> getOperableDeviceMacList() {
        return operableDeviceMacList;
    }

    public void setOperableDeviceMacList(List<String> operableDeviceMacList) {
        this.operableDeviceMacList = operableDeviceMacList;
    }
}
