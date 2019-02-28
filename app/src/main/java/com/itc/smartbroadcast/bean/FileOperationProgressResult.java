package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

/**
 * Created by Ligh on 18-10-11.
 * describe _文件操作进度信息
 */

public class FileOperationProgressResult extends BaseModel {

    //文件操作符 0:文件操作中  1：操作完成  2：操作失败
    private int fileStateOperator;
    //文件操作完成度 0-100
    private int progress;

    public int getFileStateOperator() {
        return fileStateOperator;
    }

    public void setFileStateOperator(int fileStateOperator) {
        this.fileStateOperator = fileStateOperator;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
