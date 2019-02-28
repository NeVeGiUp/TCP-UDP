package com.itc.smartbroadcast.bean;

import java.util.List;

/**
 * 定时任务详情
 * Created by lik on 2018/8/25.
 */

public class TaskDetail {

    //任务编号
    private int taskNum;
    //设备总数
    private int deviceSize;
    //曲目总数
    private int musicSize;
    //曲目列表
    private List<Music> musicList;
    //设备列表
    private List<Device> deviceList;

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

    public int getMusicSize() {
        return musicSize;
    }

    public void setMusicSize(int musicSize) {
        this.musicSize = musicSize;
    }

    public List<Music> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
    }

    public List<Device> getDeviceList() {
        return deviceList;
    }

    public void setDeviceList(List<Device> deviceList) {
        this.deviceList = deviceList;
    }

    public static class Music {
        //曲目名称
        private String musicName;
        //曲目url
        private String musicPath;

        public String getMusicName() {
            return musicName;
        }

        public void setMusicName(String musicName) {
            this.musicName = musicName;
        }

        public String getMusicPath() {
            return musicPath;
        }

        public void setMusicPath(String musicPath) {
            this.musicPath = musicPath;
        }
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
