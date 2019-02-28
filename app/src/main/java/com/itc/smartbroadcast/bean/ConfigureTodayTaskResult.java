package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

/**
 * Created by Ligh on 18-10-12.
 * describe _配置今日任务结果
 */

public class ConfigureTodayTaskResult extends BaseModel{

    //配置今日任务状态结果  0:失败   1：成功
    private int result;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
