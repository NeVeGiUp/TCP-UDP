package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

/**
 * Created by ligh on 18-9-6.
 * describe _功放设备详细信息
 */

public class PowerAmplifierInfo extends BaseModel {

    //高音增益
    private int highGain;
    //低音增益
    private int lowGain;
    //S0-S5类型混音使能状态
    private int[] mixingEnableState_s;
    //P0-P5类型混音使能状态
    private int[] mixingEnableState_p;
    //E0-E5类型混音使能状态
    private int[] mixingEnableState_e;
    //终端默认音量
    private int volume;

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public int getHighGain() {
        return highGain;
    }

    public void setHighGain(int highGain) {
        this.highGain = highGain;
    }

    public int getLowGain() {
        return lowGain;
    }

    public void setLowGain(int lowGain) {
        this.lowGain = lowGain;
    }

    public int[] getMixingEnableState_s() {
        return mixingEnableState_s;
    }

    public void setMixingEnableState_s(int[] mixingEnableState_s) {
        this.mixingEnableState_s = mixingEnableState_s;
    }

    public int[] getMixingEnableState_p() {
        return mixingEnableState_p;
    }

    public void setMixingEnableState_p(int[] mixingEnableState_p) {
        this.mixingEnableState_p = mixingEnableState_p;
    }

    public int[] getMixingEnableState_e() {
        return mixingEnableState_e;
    }

    public void setMixingEnableState_e(int[] mixingEnableState_e) {
        this.mixingEnableState_e = mixingEnableState_e;
    }
}
