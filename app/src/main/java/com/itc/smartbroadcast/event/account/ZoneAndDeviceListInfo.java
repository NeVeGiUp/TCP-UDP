package com.itc.smartbroadcast.event.account;

/**
 * author： lghandroid
 * created：2018/8/9 20:08
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.event.account
 * describe:发现首页分区和设备终端列表载体类
 */

public class ZoneAndDeviceListInfo {


    private byte[] devicedatapacket;
    private byte[] Zonedatapacket;

    public byte[] getZonedatapacket() {
        return Zonedatapacket;
    }

    public byte[] getDevicedatapacket() {
        return devicedatapacket;
    }

    public ZoneAndDeviceListInfo(byte[] devicepacket, byte[] zonepacket) {
        this.devicedatapacket = devicepacket;
        this.Zonedatapacket = zonepacket;
    }
}
