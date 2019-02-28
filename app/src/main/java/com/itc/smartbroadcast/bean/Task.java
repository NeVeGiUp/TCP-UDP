package com.itc.smartbroadcast.bean;

/**
 * 任务
 * Created by lik on 2018/8/22.
 */

public class Task {

    //任务编号
    private int taskNum;
    //方案编号
    private int schemeNum;
    //任务名称
    private String taskName;
    //任务状态(0否1是)
    private int taskStatus;
    //任务优先级
    private int taskPriority;
    //任务音量(0~100)
    private int taskVolume;
    //重复模式(0:按周重复，1:按日期重复)
    private int taskDuplicationPattern;
    //按周重复模式(数组为7位，每一个都是0或1，在下标加1代表是周几，数组的值为（0否1是）)
    private int[] taskWeekDuplicationPattern;
    //按日期重复模式(数组为10位不能多，不能少，每一个数组代表一个日期)，日期格式为：YYYY-MM-DD
    private String[] taskDateDuplicationPattern;
    //开始时间,日期格式为：HH:MM:SS
    private String taskStartDate;
    //持续时间,定时任务持续播放秒数
    private int taskContinueDate;
    //播放模式(0:顺序播放，1:循环播放，2:随机播放)
    private int taskPlayMode;
    //播放曲目总数
    private int taskPlayTotal;
    //任务测试状态
    private int taskTestStatus;

    public int getTaskTestStatus() {
        return taskTestStatus;
    }

    public void setTaskTestStatus(int taskTestStatus) {
        this.taskTestStatus = taskTestStatus;
    }

    public int getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(int taskNum) {
        this.taskNum = taskNum;
    }

    public int getSchemeNum() {
        return schemeNum;
    }

    public void setSchemeNum(int schemeNum) {
        this.schemeNum = schemeNum;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public int getTaskPriority() {
        return taskPriority;
    }

    public void setTaskPriority(int taskPriority) {
        this.taskPriority = taskPriority;
    }

    public int getTaskVolume() {
        return taskVolume;
    }

    public void setTaskVolume(int taskVolume) {
        this.taskVolume = taskVolume;
    }

    public int getTaskDuplicationPattern() {
        return taskDuplicationPattern;
    }

    public void setTaskDuplicationPattern(int taskDuplicationPattern) {
        this.taskDuplicationPattern = taskDuplicationPattern;
    }

    public int[] getTaskWeekDuplicationPattern() {
        return taskWeekDuplicationPattern;
    }

    public void setTaskWeekDuplicationPattern(int[] taskWeekDuplicationPattern) {
        this.taskWeekDuplicationPattern = taskWeekDuplicationPattern;
    }

    public String[] getTaskDateDuplicationPattern() {
        return taskDateDuplicationPattern;
    }

    public void setTaskDateDuplicationPattern(String[] taskDateDuplicationPattern) {
        this.taskDateDuplicationPattern = taskDateDuplicationPattern;
    }

    public String getTaskStartDate() {
        return taskStartDate;
    }

    public void setTaskStartDate(String taskStartDate) {
        this.taskStartDate = taskStartDate;
    }

    public int getTaskContinueDate() {
        return taskContinueDate;
    }

    public void setTaskContinueDate(int taskContinueDate) {
        this.taskContinueDate = taskContinueDate;
    }

    public int getTaskPlayMode() {
        return taskPlayMode;
    }

    public void setTaskPlayMode(int taskPlayMode) {
        this.taskPlayMode = taskPlayMode;
    }

    public int getTaskPlayTotal() {
        return taskPlayTotal;
    }

    public void setTaskPlayTotal(int taskPlayTotal) {
        this.taskPlayTotal = taskPlayTotal;
    }
}
