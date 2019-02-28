package com.itc.smartbroadcast.bean;

/**
 * Created by Ligh on 18-9-21.
 * describe _popwindow下拉菜单信息
 */

public class DropBeans {
    //菜单类型
    private String type;
    //是否选中状态
    private boolean isChoosed;

    public DropBeans(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isChoosed() {
        return isChoosed;
    }

    public void setChoosed(boolean choosed) {
        isChoosed = choosed;
    }
}
