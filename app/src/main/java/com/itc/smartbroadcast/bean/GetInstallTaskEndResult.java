package com.itc.smartbroadcast.bean;

/**
 * 编辑方案返回数据
 * Created by lik on 2018/8/24.
 */

public class GetInstallTaskEndResult {


    //任务编号
    private int taskNum;
    //随机ID号
    private int randomId;
    //返回结果（0：失败,1：成功）
    private int result;


    public int getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(int taskNum) {
        this.taskNum = taskNum;
    }

    public int getRandomId() {
        return randomId;
    }

    public void setRandomId(int randomId) {
        this.randomId = randomId;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
