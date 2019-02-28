package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

import java.util.ArrayList;

/**
 * Created by Ligh on 18-10-10.
 * describe _复制，移动，删除到指定文件夹(乐库)里的音乐
 */

public class OperateMusicFilesInfo extends BaseModel {

    //文件操作符  00：复制  01：移动  02：删除
    private String operator;
    //初始文件夹名称
    private String initFolderName;
    //曲目名称
    private String musicName;
    //目标文件夹名称（注：删除操作无需设置该值）
    private String targetFolderName;
    //批量歌曲总数
    private int musicTotal;
    //批量曲目名称列表
    private ArrayList<String> musicNameList;

    public ArrayList<String> getMusicNameList() {
        return musicNameList;
    }

    public void setMusicNameList(ArrayList<String> musicNameList) {
        this.musicNameList = musicNameList;
    }

    public int getMusicTotal() {
        return musicTotal;
    }

    public void setMusicTotal(int musicTotal) {
        this.musicTotal = musicTotal;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getInitFolderName() {
        return initFolderName;
    }

    public void setInitFolderName(String initFolderName) {
        this.initFolderName = initFolderName;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getTargetFolderName() {
        return targetFolderName;
    }

    public void setTargetFolderName(String targetFolderName) {
        this.targetFolderName = targetFolderName;
    }
}
