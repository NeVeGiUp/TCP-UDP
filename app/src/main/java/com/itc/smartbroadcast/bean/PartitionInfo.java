package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

import java.util.List;

/**
 * author： lghandroid
 * created：2018/8/27 17:40
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.bean
 * describe: _分区信息
 */

public class PartitionInfo extends BaseModel {


    //分区操作符
    private int operator;
    //分区号
    private int partitionNum;
    //账户ID
    private int accountId;
    //分区名称
    private String partitionName;
    //包含终端数
    private int deviceCount;
    //终端mac列表 mac地址格式42-4c-45-00-0a-01
    private List<String> deviceMacList;
    //物理分区列表
    private List<PhysicalPartitionInfo> phycicalPartitionList;

    public int getOperator() {
        return operator;
    }

    public void setOperator(int operator) {
        this.operator = operator;
    }

    public int getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(int partitionNum) {
        this.partitionNum = partitionNum;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getPartitionName() {
        return partitionName;
    }

    public void setPartitionName(String partitionName) {
        this.partitionName = partitionName;
    }

    public int getDeviceCount() {
        return deviceCount;
    }

    public void setDeviceCount(int deviceCount) {
        this.deviceCount = deviceCount;
    }

    public List<String> getDeviceMacList() {
        return deviceMacList;
    }

    public void setDeviceMacList(List<String> deviceMacList) {
        this.deviceMacList = deviceMacList;
    }

    public List<PhysicalPartitionInfo> getPhycicalPartitionList() {
        return phycicalPartitionList;
    }

    public void setPhycicalPartitionList(List<PhysicalPartitionInfo> phycicalPartitionList) {
        this.phycicalPartitionList = phycicalPartitionList;
    }


}
