package com.itc.smartbroadcast.event.account;

/**
 * author： lghandroid
 * created：2018/8/9 20:08
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.event.account
 * describe:登录数据载体类
 */

public class LoginInfo {
    public byte[] getDatapacket() {
        return datapacket;
    }

    private byte[] datapacket;

    public LoginInfo(byte[] packet) {
        this.datapacket = packet;
    }
}
