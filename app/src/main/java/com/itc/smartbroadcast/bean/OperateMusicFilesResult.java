package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

/**
 * Created by Ligh on 18-10-10.
 * describe _操作音乐库文件状态
 */

public class OperateMusicFilesResult extends BaseModel {

    //操作状态 0:不能操作  1：可以操作
    private int result;
    //操作类型
    private int operatorType;

    public int getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(int operatorType) {
        this.operatorType = operatorType;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
