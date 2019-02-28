package com.itc.smartbroadcast.bean;

public class RingingTask{
    private String planname;
    private String starttime;
    private String overtime;
    private String taskamout;
    public RingingTask(String planname, String starttime, String overtime, String taskamout){
            this.planname = planname;
            this.starttime = starttime;
            this.overtime = overtime;
            this.taskamout = taskamout;
    }
    public String getPlanname() {
        return planname;
    }

    public void setPlanname(String planname) {
        this.planname = planname;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getOvertime() {
        return overtime;
    }

    public void setOvertime(String overtime) {
        this.overtime = overtime;
    }

    public String getTaskamout() {
        return taskamout;
    }

    public void setTaskamout(String taskamout) {
        this.taskamout = taskamout;
    }
}