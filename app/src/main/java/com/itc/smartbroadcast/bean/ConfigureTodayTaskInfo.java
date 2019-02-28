package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

/**
 * Created by Ligh on 18-10-12.
 * describe _配置（切换）今日任务信息
 */

public class ConfigureTodayTaskInfo extends BaseModel {

    //今日执行周几任务  周一到周日
    private int executeTaskWeek;
    //今日执行任务的日期  YYYY-MM-dd
    private int[] executeTaskDate;

    public int getExecuteTaskWeek() {
        return executeTaskWeek;
    }

    public void setExecuteTaskWeek(int executeTaskWeek) {
        this.executeTaskWeek = executeTaskWeek;
    }

    public int[] getExecuteTaskDate() {
        return executeTaskDate;
    }

    public void setExecuteTaskDate(int[] executeTaskDate) {
        this.executeTaskDate = executeTaskDate;
    }
}
