package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

/**
 * author： lghandroid
 * created：2018/8/30 10:33
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.bean
 * describe: _终端设备配置结果
 */

public class EditTerminalResult extends BaseModel {

    //终端设备配置状态结果 [1,1,1,1,1,1,1,0] index下标为1表示为配置成功，0表示配置失败
    private int[] result;

    public int[] getResult() {
        return result;
    }

    public void setResult(int[] result) {
        this.result = result;
    }

}
