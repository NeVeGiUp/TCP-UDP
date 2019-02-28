package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

/**
 * author： lghandroid
 * created：2018/8/23 9:04
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.bean
 * describe: _歌曲库列表音乐信息
 */

public class MusicMsgInfo extends BaseModel {


    //文件夹名称
    private String musicFolderName;
    //曲目名称
    private String musicName;
    //曲目时间
    private int musicTime;

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

    public int getMusicTime() {
        return musicTime;
    }

    public void setMusicTime(int musicTime) {
        this.musicTime = musicTime;
    }


}
