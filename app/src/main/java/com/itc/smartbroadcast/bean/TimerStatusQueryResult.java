package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

import java.util.ArrayList;

/**
 * Created by Ligh on 18-10-18.
 * describe _定时器在线状态查询
 */

public class TimerStatusQueryResult  extends BaseModel{

    //时间信息 年月日时分秒
    private String timeMsg;
    //sd卡状态 0:sd卡工作正常   1：sd卡拔出
    private int sdcardStatus;
    //任务总数  定时/打铃
    private int taskTotal;
    //当前进行中的任务编号
    private ArrayList<Integer> taskNum;

    public int getTaskTotal() {
        return taskTotal;
    }

    public void setTaskTotal(int taskTotal) {
        this.taskTotal = taskTotal;
    }

    public ArrayList<Integer> getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(ArrayList<Integer> taskNum) {
        this.taskNum = taskNum;
    }

    public String getTimeMsg() {
        return timeMsg;
    }

    public void setTimeMsg(String timeMsg) {
        this.timeMsg = timeMsg;
    }

    public int getSdcardStatus() {
        return sdcardStatus;
    }

    public void setSdcardStatus(int sdcardStatus) {
        this.sdcardStatus = sdcardStatus;
    }
}
