package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

/**
 * author： lghandroid
 * created：2018/8/27 16:49
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.bean
 * describe: _分区添加，删除，编辑返回数据
 */

public class EditPartitionResult extends BaseModel {

    //分区编辑操作符
    private String operator;
    //分区编号
    private int partitionNum;
    //配置结果
    private int result;

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public int getPartitionNum() {
        return partitionNum;
    }

    public void setPartitionNum(int partitionNum) {
        this.partitionNum = partitionNum;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }


}
