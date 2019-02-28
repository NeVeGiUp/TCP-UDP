package com.itc.smartbroadcast.bean;

public class MusicCover {
    private String musicname;
    private String musicamount;

    public MusicCover(String musicname, String musicamount){
        this.musicname = musicname;
        this.musicamount = musicamount;
    }

    public String getMusicname() {
        return musicname;
    }

    public void setMusicname(String musicname) {
        this.musicname = musicname;
    }

    public String getMusicamount() {
        return musicamount;
    }

    public void setMusicamount(String musicamount) {
        this.musicamount = musicamount;
    }
}