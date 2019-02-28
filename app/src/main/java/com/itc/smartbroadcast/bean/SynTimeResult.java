package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

/**
 * author： lghandroid
 * created：2018/9/3 11:42
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.bean
 * describe: _同步时间结果
 */

public class SynTimeResult extends BaseModel {

    //同步时间结果  1:成功  0：失败
    private int result;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }


}
