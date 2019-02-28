package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

/**
 * author： lghandroid
 * created：2018/9/3 11:42
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.bean
 * describe: _编辑账户管理结果
 */

public class EditAccManageResult extends BaseModel {

    //添加账户value=00: 编辑账户value=01: 删除账户value=02
    private String accOperator;
    //账户编号
    private int accNum;
    //配置状态value=00 :配置失败 value=01 :配置成功
    private String configureState;

    public String getAccOperator() {
        return accOperator;
    }

    public void setAccOperator(String accOperator) {
        this.accOperator = accOperator;
    }

    public int getAccNum() {
        return accNum;
    }

    public void setAccNum(int accNum) {
        this.accNum = accNum;
    }

    public String getConfigureState() {
        return configureState;
    }

    public void setConfigureState(String configureState) {
        this.configureState = configureState;
    }


}
