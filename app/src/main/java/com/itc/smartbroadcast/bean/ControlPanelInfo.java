package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

import java.util.List;

/**
 * Created by Ligh on 18-9-7.
 * describe _控制面板详细信息
 */

public class ControlPanelInfo extends BaseModel {
    //绑定接收设备总数
    private int bingDeviceCount;
    //设备信息*总数
    private List<DeviceMsgInner> deviceMsgList;

    public int getBingDeviceCount() {
        return bingDeviceCount;
    }

    public void setBingDeviceCount(int bingDeviceCount) {
        this.bingDeviceCount = bingDeviceCount;
    }

    public List<DeviceMsgInner> getDeviceMsgList() {
        return deviceMsgList;
    }

    public void setDeviceMsgList(List<DeviceMsgInner> deviceMsgList) {
        this.deviceMsgList = deviceMsgList;
    }

      public static class DeviceMsgInner extends BaseModel{
        //绑定的设备IP
        private String bindDeviceIp;
        //绑定的设备MAC
        private String bindDeviceMac;
        //绑定的设备信息
        private List<FoundDeviceInfo> panelDeviceList;

        public String getBindDeviceIp() {
            return bindDeviceIp;
        }

        public void setBindDeviceIp(String bindDeviceIp) {
            this.bindDeviceIp = bindDeviceIp;
        }

        public String getBindDeviceMac() {
            return bindDeviceMac;
        }

        public void setBindDeviceMac(String bindDeviceMac) {
            this.bindDeviceMac = bindDeviceMac;
        }

          public List<FoundDeviceInfo> getPanelDeviceList() {
              return panelDeviceList;
          }

          public void setPanelDeviceList(List<FoundDeviceInfo> panelDeviceList) {
              this.panelDeviceList = panelDeviceList;
          }
      }
}
