package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

import java.util.ArrayList;

/**
 * Created by Ligh on 18-10-10.
 * describe _批量复制，移动，删除到指定文件夹(乐库)里的音乐回复信息
 */

public class BatchOperateMusicMsgReplyInfo extends BaseModel {

    //状态提示符  0:正在复制  1：正在移动  2：正在删除
    private int statusPrompt;
    //批量操作符  0:批量操作完成  1：批量操作中  2：批量操作失败
    private int batchOperator;
    //文件操作符  0：文件操作成功  1：文件路径错误  2：文件总数超过上限   3：操作繁忙冲突错误
    private int fileOperator;
    //曲目文件夹名称
    private String musicFolderName;
    //曲目名称
    private String musicName;

    public int getBatchOperator() {
        return batchOperator;
    }

    public int getStatusPrompt() {
        return statusPrompt;
    }

    public void setStatusPrompt(int statusPrompt) {
        this.statusPrompt = statusPrompt;
    }

    public void setBatchOperator(int batchOperator) {
        this.batchOperator = batchOperator;
    }

    public int getFileOperator() {
        return fileOperator;
    }

    public void setFileOperator(int fileOperator) {
        this.fileOperator = fileOperator;
    }

    public String getMusicFolderName() {
        return musicFolderName;
    }

    public void setMusicFolderName(String musicFolderName) {
        this.musicFolderName = musicFolderName;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }
}
