package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

/**
 * author： lghandroid
 * created：2018/10/2 14:29
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.bean
 * describe: _编辑采集音源类型结果
 */

public class EditSoundSourceTypeResult extends BaseModel {

    // 1:成功  0：失败
    private int result;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }


}
