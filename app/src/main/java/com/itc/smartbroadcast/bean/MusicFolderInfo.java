package com.itc.smartbroadcast.bean;

import com.itc.smartbroadcast.base.BaseModel;

/**
 * author： lghandroid
 * created：2018/8/23 9:04
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.bean
 * describe: _曲目库音乐文件夹列表
 */

public class MusicFolderInfo extends BaseModel {
    // 文件夹名称
    private String musicFolderName;
    //曲目总数
    private int allMusicNum;
    //曲目溢出标志  0:不溢出  1：溢出
    private int overflowFalg;
    //文件夹封面
    private int musicCover;

    public int getOverflowFalg() {
        return overflowFalg;
    }

    public void setOverflowFalg(int overflowFalg) {
        this.overflowFalg = overflowFalg;
    }

    public String getMusicFolderName() {
        return musicFolderName;
    }

    public void setMusicFolderName(String musicFolderName) {
        this.musicFolderName = musicFolderName;
    }

    public int getAllMusicNum() {
        return allMusicNum;
    }

    public void setAllMusicNum(int allMusicNum) {
        this.allMusicNum = allMusicNum;
    }

    public int getMusicCover() {
        return musicCover;
    }

    public void setMusicCover(int musicCover) {
        this.musicCover = musicCover;
    }
}
