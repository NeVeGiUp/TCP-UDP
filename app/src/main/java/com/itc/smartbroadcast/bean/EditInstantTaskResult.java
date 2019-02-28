package com.itc.smartbroadcast.bean;

/**
 * 修改即时任务结果
 * Created by lik on 18-8-31.
 */

public class EditInstantTaskResult {
    //任务编号
    private int taskNum;
    //返回结果（0：失败,1：成功）
    private int result;

    public int getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(int taskNum) {
        this.taskNum = taskNum;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
