package com.itc.smartbroadcast.event.account;

/**
 * author： lghandroid
 * created：2018/8/9 20:08
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.event.account
 * describe:创建定时任务载体类
 */

public class CreatTimedTaskInfo {
    public byte[] getDatapacket() {
        return datapacket;
    }

    private byte[] datapacket;

    public CreatTimedTaskInfo(byte[] packet) {
        this.datapacket = packet;
    }
}
