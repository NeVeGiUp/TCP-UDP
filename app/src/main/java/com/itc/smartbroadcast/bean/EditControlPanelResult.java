package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

/**
 * author： lghandroid
 * created：2018/8/30 10:50
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.bean
 * describe: _配置控制面板绑定设备结果
 *
 */

public class EditControlPanelResult extends BaseModel {

    //1:成功  0:失败
    private int result;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

}
