package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

/**
 * author： lghandroid
 * created：2018/9/3 11:42
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.bean
 * describe: _音乐上传状态结果
 */

public class MusicUploadStatusResult extends BaseModel {

    //上传状态 0:可以上传  1：不能上传
    private int result;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
