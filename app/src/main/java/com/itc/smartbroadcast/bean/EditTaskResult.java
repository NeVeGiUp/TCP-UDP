package com.itc.smartbroadcast.bean;

/**
 * 编辑方案返回数据
 * Created by lik on 2018/8/24.
 */

public class EditTaskResult {

    //任务编号
    private int taskNum;
    //返回结果（0：成功,1：任务已满，2：基本信息配置失败，4：设备列表配置失败，8：音乐列表配置失败）
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
