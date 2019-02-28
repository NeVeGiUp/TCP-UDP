package com.itc.smartbroadcast.bean;

/**
 * content:编辑报警端口结果
 * author:lik
 * date: 18-10-15 下午3:40
 */
public class EditAlarmPortDeviceListResult {

    //端口编号
    private int portNum;
    //返回结果（0：失败,1：成功）
    private int result;

    public int getPortNum() {
        return portNum;
    }

    public void setPortNum(int portNum) {
        this.portNum = portNum;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
