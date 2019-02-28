package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

/**
 * Created by Ligh on 18-10-11.
 * describe _今日执行周几任务结果
 */

public class ExecuteTaskDateResult extends BaseModel {

    //执行周  周一 到 周日
    private int executeTaskWeek;
    //执行日期  年月日
    private String executeTaskDate;

    public int getExecuteTaskWeek() {
        return executeTaskWeek;
    }

    public void setExecuteTaskWeek(int executeTaskWeek) {
        this.executeTaskWeek = executeTaskWeek;
    }

    public String getExecuteTaskDate() {
        return executeTaskDate;
    }

    public void setExecuteTaskDate(String executeTaskDate) {
        this.executeTaskDate = executeTaskDate;
    }
}
