package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

/**
 * Created by Ligh on 18-9-11.
 * describe _系统注册信息
 */

public class SystemRegisterInfo extends BaseModel {
    //注册状态 0:成功  1：注册码无效  2注册码已被使用
    private int registerState;
    //注册码
    private String registerCode;

    public int getRegisterState() {
        return registerState;
    }

    public void setRegisterState(int registerState) {
        this.registerState = registerState;
    }

    public String getRegisterCode() {
        return registerCode;
    }

    public void setRegisterCode(String registerCode) {
        this.registerCode = registerCode;
    }
}
