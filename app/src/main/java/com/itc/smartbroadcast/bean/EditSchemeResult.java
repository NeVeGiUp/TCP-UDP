package com.itc.smartbroadcast.bean;

/**
 * 编辑方案返回数据
 * Created by lik on 2018/8/24.
 */

public class EditSchemeResult {

    //方案编号
    private int schemeNum;
    //返回结果（0：失败,1：成功）
    private int result;

    public int getSchemeNum() {
        return schemeNum;
    }

    public void setSchemeNum(int schemeNum) {
        this.schemeNum = schemeNum;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }
}
