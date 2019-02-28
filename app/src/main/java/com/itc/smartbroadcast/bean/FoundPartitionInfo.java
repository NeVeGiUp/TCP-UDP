package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

import java.io.Serializable;
import java.util.List;

/**
 * author： lghandroid
 * created：2018/8/23 9:04
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.bean
 * describe: _分区列表信息载体类
 */

public class FoundPartitionInfo extends BaseModel implements Serializable {

    //分区编号
    private String partitionNum;
    //账户ID
    private int accountId;
    //分区名称
    private String name;
//分区包含的设备详情
    private List<FoundDeviceInfo> deviceInfoList;

    public List<FoundDeviceInfo> getDeviceInfoList() {
        return deviceInfoList;
    }

    public void setDeviceInfoList(List<FoundDeviceInfo> deviceInfoList) {
        this.deviceInfoList = deviceInfoList;
    }

    public String getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(String partitionNum) {
        this.partitionNum = partitionNum;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
