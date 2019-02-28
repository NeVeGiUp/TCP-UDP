package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

/**
 * Created by Ligh on 19-1-4.
 * describe _客户端激活注册机器结果
 */

public class ActivationRegisterCodeResult extends BaseModel {

    //0:注册成功  1:注册码无效  2:注册码已被使用
    private int result;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
