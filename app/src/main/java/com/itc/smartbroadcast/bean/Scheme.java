package com.itc.smartbroadcast.bean;

/**
 * 方案
 * Created by lik on 2018/8/22.
 */

public class Scheme {

    //方案编码
    private int schemeNum;
    //方案状态：0否1是
    private int schemeStatus;
    //方案名称
    private String schemeName;
    //方案开始日期（YYYY-MM-DD）
    private String schemeStartDate;
    //方案结束日期（YYYY-MM-DD）
    private String schemeEndDate;

    public int getSchemeNum() {
        return schemeNum;
    }

    public void setSchemeNum(int schemeNum) {
        this.schemeNum = schemeNum;
    }

    public int getSchemeStatus() {
        return schemeStatus;
    }

    public void setSchemeStatus(int schemeStatus) {
        this.schemeStatus = schemeStatus;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public String getSchemeStartDate() {
        return schemeStartDate;
    }

    public void setSchemeStartDate(String schemeStartDate) {
        this.schemeStartDate = schemeStartDate;
    }

    public String getSchemeEndDate() {
        return schemeEndDate;
    }

    public void setSchemeEndDate(String schemeEndDate) {
        this.schemeEndDate = schemeEndDate;
    }
}
