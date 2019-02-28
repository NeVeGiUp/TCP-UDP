package com.itc.smartbroadcast.bean;

import java.util.List;

/**
 * 即时任务详细信息
 * Created by lik on 18-8-30.
 */

public class InstantTaskDetail {
    private int taskNum;

    private int deviceSize;

    private List<Device> devicesList;

    public int getTaskNum() {
        return taskNum;
    }
    public void setTaskNum(int taskNum) {
        this.taskNum = taskNum;
    }

    public int getDeviceSize() {
        return deviceSize;
    }

    public void setDeviceSize(int deviceSize) {
        this.deviceSize = deviceSize;
    }

    public List<Device> getDevicesList() {
        return devicesList;
    }

    public void setDevicesList(List<Device> devicesList) {
        this.devicesList = devicesList;
    }

    public static class Device {
        //设备MAC
        private String deviceMac;
        //设备分区信息,数据为[1,1,0,...0,1]这样的十位int型数组
        private int[] deviceZoneMsg;

        public String getDeviceMac() {
            return deviceMac;
        }

        public void setDeviceMac(String deviceMac) {
            this.deviceMac = deviceMac;
        }

        public int[] getDeviceZoneMsg() {
            return deviceZoneMsg;
        }
        public void setDeviceZoneMsg(int[] deviceZoneMsg) {
            this.deviceZoneMsg = deviceZoneMsg;
        }
    }
}
