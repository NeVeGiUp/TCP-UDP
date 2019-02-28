package com.itc.smartbroadcast.bean;

import java.util.List;

/**
 * content:报警设备指定端口绑定设备
 * author:lik
 * date: 18-10-11 上午9:11
 */
public class AlarmPortDevice {

    //端口编号
    private int portNum;
    //端口名称
    private String portName;
    //端口曲目路径
    private String portMusicPath;
    //端口曲目名称
    private String portMusicName;
    //设备总数
    private int deviceCount;
    //端口绑定的设备mac列表
    private List<String> portDeviceMacList;

    public int getPortNum() {
        return portNum;
    }

    public void setPortNum(int portNum) {
        this.portNum = portNum;
    }

    public String getPortName() {
        return portName;
    }

    public void setPortName(String portName) {
        this.portName = portName;
    }

    public String getPortMusicPath() {
        return portMusicPath;
    }

    public void setPortMusicPath(String portMusicPath) {
        this.portMusicPath = portMusicPath;
    }

    public String getPortMusicName() {
        return portMusicName;
    }

    public void setPortMusicName(String portMusicName) {
        this.portMusicName = portMusicName;
    }

    public int getDeviceCount() {
        return deviceCount;
    }

    public void setDeviceCount(int deviceCount) {
        this.deviceCount = deviceCount;
    }

    public List<String> getPortDeviceMacList() {
        return portDeviceMacList;
    }

    public void setPortDeviceMacList(List<String> portDeviceMacList) {
        this.portDeviceMacList = portDeviceMacList;
    }
}
