package com.itc.smartbroadcast.bean;

/**
 * @Content :  CD任务状态
 * @Author : lik
 * @Time : 18-9-13 下午5:26
 */
public class CDInstantStatus {


    //任务编号
    private int taskNum;
    //随机ID号
    private int randomId;
    //设备mac地址
    private String deviceMac;
    //设备型号
    private String deviceModel;
    //设备音量
    private int deviceVolume;
    //曲目数量
    private int musicSize;
    //当前曲目序号
    private int nowMusicNum;
    //翻页标志
    private int pageMarket;
    //当前曲目时长
    private String nowMusicTime;
    //当前时间
    private String nowTime;
    //存储设备类型(0:光盘，1:U盘，2:SD卡)
    private int saveDeviceType;
    /**
     * 播放状态:
     * 1:正在播放
     * 2:暂停
     * 3:快进32倍
     * 4:快退32倍
     * 5:停止
     * 16:快进2倍
     * 32:快进4倍
     * 48:快进8倍
     * 64:快进16倍
     * 80:快退2倍
     * 96:快退4倍
     * 112:快退8倍
     * 128:快退16倍
     */
    private int playStatus;
    /**
     * 当前CD机状态
     * 0：无存储设备
     * 1：未知碟片
     * 2：出仓
     * 3：进仓
     * 5：读碟中
     * 6：读碟完成
     */
    private int cdStatus;

    /**
     * 当前音频类型
     * 0：未知类型
     * 1：AC3
     * 2：DTS
     * 3：MPEG
     * 4：mp3
     * 5：wam
     * 6：LPCM
     * 7：AAC
     */
    private int musicType;
    /**
     * 播放模式
     * 0:顺序播放
     * 1:单曲播放
     * 2:全部播放（默认模式）
     * 4:仅仅播放单曲，播放完停止
     */
    private int playModel;
    /**
     * 文件名编码格式:
     * 0:unicode(默认模式)
     * 1:cp936
     */
    private int musicNameCode;
    //文件名字节长度
    private int musicNameLength;
    //音乐名
    private String musicName;

    public int getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(int taskNum) {
        this.taskNum = taskNum;
    }

    public int getRandomId() {
        return randomId;
    }

    public void setRandomId(int randomId) {
        this.randomId = randomId;
    }

    public int getPageMarket() {
        return pageMarket;
    }

    public void setPageMarket(int pageMarket) {
        this.pageMarket = pageMarket;
    }

    public int getDeviceVolume() {
        return deviceVolume;
    }

    public void setDeviceVolume(int deviceVolume) {
        this.deviceVolume = deviceVolume;
    }

    public String getDeviceMac() {
        return deviceMac;
    }

    public void setDeviceMac(String deviceMac) {
        this.deviceMac = deviceMac;
    }

    public int getMusicSize() {
        return musicSize;
    }

    public void setMusicSize(int musicSize) {
        this.musicSize = musicSize;
    }

    public int getNowMusicNum() {
        return nowMusicNum;
    }

    public void setNowMusicNum(int nowMusicNum) {
        this.nowMusicNum = nowMusicNum;
    }

    public String getNowMusicTime() {
        return nowMusicTime;
    }

    public void setNowMusicTime(String nowMusicTime) {
        this.nowMusicTime = nowMusicTime;
    }

    public int getSaveDeviceType() {
        return saveDeviceType;
    }

    public void setSaveDeviceType(int saveDeviceType) {
        this.saveDeviceType = saveDeviceType;
    }

    public int getPlayStatus() {
        return playStatus;
    }

    public void setPlayStatus(int playStatus) {
        this.playStatus = playStatus;
    }

    public int getCdStatus() {
        return cdStatus;
    }

    public void setCdStatus(int cdStatus) {
        this.cdStatus = cdStatus;
    }

    public int getMusicType() {
        return musicType;
    }

    public void setMusicType(int musicType) {
        this.musicType = musicType;
    }

    public int getPlayModel() {
        return playModel;
    }

    public void setPlayModel(int playModel) {
        this.playModel = playModel;
    }

    public int getMusicNameCode() {
        return musicNameCode;
    }

    public void setMusicNameCode(int musicNameCode) {
        this.musicNameCode = musicNameCode;
    }

    public int getMusicNameLength() {
        return musicNameLength;
    }

    public void setMusicNameLength(int musicNameLength) {
        this.musicNameLength = musicNameLength;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getNowTime() {
        return nowTime;
    }

    public void setNowTime(String nowTime) {
        this.nowTime = nowTime;
    }
}
