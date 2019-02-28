package com.itc.smartbroadcast.bean;
import android.os.Parcel;
import android.os.Parcelable;

public class Music implements Parcelable{

    private String musicName;
    private String musicPath;
    // icon
    private String image;
    // 艺术家
    private String artist;
    // 长度
    private double length;
    // 播放时长
    private int playLength;
    // 音乐id
    private int id;
    // 音乐标题
    private String title;

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public double getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPlayLength() {
        return playLength;
    }

    public void setPlayLength(int playLength) {
        this.playLength = playLength;
    }


    public String getMusicPath() {
        return musicPath;
    }

    public void setMusicPath(String musicPath) {
        this.musicPath = musicPath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(musicName);
        dest.writeString(musicPath);
        dest.writeString(image);
        dest.writeString(artist);
        dest.writeDouble(length);
        dest.writeInt(id);
        dest.writeString(title);
    }
    /**
     * 必须用 public static final 修饰符
     * 对象必须用 CREATOR
     */
    public static final Creator<Music> CREATOR = new Creator<Music>() {

        @Override
        public Music createFromParcel(Parcel source) {

            Music music = new Music();
            music.setMusicName(source.readString());
            music.setMusicPath(source.readString());
            music.setImage(source.readString());
            music.setArtist(source.readString());
            music.setLength(source.readInt());
            music.setId(source.readInt());
            music.setTitle(source.readString());

            return music;
        }

        @Override
        public Music[] newArray(int size) {
            return new Music[size];
        }

    };
}
