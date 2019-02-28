package com.itc.smartbroadcast.bean;

/**
 * Created by Ligh on 18-11-3.ing
 * describe _物理分区，mac值  创建，编辑，删除分区需要的信息
 */

public class PhysicalPartitionInfo {

    //设备mac
    private String mac;
    //物理分区
    private int[] phycicalPartition;

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int[] getPhycicalPartition() {
        return phycicalPartition;
    }

    public void setPhycicalPartition(int[] phycicalPartition) {
        this.phycicalPartition = phycicalPartition;
    }
}
