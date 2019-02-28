package com.itc.smartbroadcast.bean;

/**
 * 操作即时任务回馈
 * Created by lik on 18-8-31.
 */

public class OperateInstantTaskResult {

    //任务编号
    private int taskNum;
    //用户编号
    private int userNum;
    //返回结果（0：失败,1：成功）
    private int result;

    public int getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(int taskNum) {
        this.taskNum = taskNum;
    }

    public int getUserNum() {
        return userNum;
    }

    public void setUserNum(int userNum) {
        this.userNum = userNum;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
