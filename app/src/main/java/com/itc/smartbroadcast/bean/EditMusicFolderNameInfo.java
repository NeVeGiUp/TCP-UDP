package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

/**
 * Created by Ligh on 18-10-9.
 * describe _修改音乐文件夹名称信息
 */

public class EditMusicFolderNameInfo extends BaseModel {

    //文件夹操作符 添加：00  删除:01  编辑：02
    private String operator;
    //文件夹名称
    private String folderName;
    //修改后的文件夹名称（注：添加/删除操作时，无需设置该值）
    private String updateFolderName;

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getFolderName() {
        return folderName;
    }

    public void setFolderName(String folderName) {
        this.folderName = folderName;
    }

    public String getUpdateFolderName() {
        return updateFolderName;
    }

    public void setUpdateFolderName(String updateFolderName) {
        this.updateFolderName = updateFolderName;
    }
}
