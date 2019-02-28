package com.itc.smartbroadcast.event.account;

/**
 * author： youmu
 * created：2018/8/9 20:08
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.event.account
 * describe:音乐文件夹
 */

public class MusicLibInfo {
    public byte[] getDatapacket() {
        return datapacket;
    }

    private byte[] datapacket;

    public MusicLibInfo(byte[] packet) {
        this.datapacket = packet;
    }
}
