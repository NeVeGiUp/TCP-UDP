package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

/**
 * author： lghandroid
 * created：2018/10/2 14:29
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.bean
 * describe: _采集音源类型结果
 */

public class GetSoundSourceTypeResult extends BaseModel {

    // 1:话筒音源  0：普通音源
    private int result;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }


}
