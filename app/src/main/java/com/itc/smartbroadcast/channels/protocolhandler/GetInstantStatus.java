package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.CDInstantStatus;
import com.itc.smartbroadcast.bean.CollectorInstantStatus;
import com.itc.smartbroadcast.bean.FMInstantStatus;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteArrayToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * @Content :
 * @Author : lik
 * @Time : 18-9-13 下午5:23
 */
public class GetInstantStatus {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度

    public final static int TASK_NUM_LENGTH = 2;                             //任务编号长度
    public final static int RANDOM_ID_LENGTH = 2;                            //随机ID号长度

    public final static int DEVICE_MAC_LENGTH = 6;                           //设备mac地址
    public final static int DEVICE_MODEL_LENGTH = 32;                        //设备型号
    public final static int MUSIC_SIZE_LENGTH = 2;                           //曲目总数
    public final static int NOW_MUSIC_NUM_LENGTH = 2;                        //曲目编号
    public final static int NOW_MUSIC_TIME_LENGTH = 3;                       //当前曲目时长
    public final static int NOW_TIME_LENGTH = 3;                             //当前播放时间
    public final static int SAVE_DEVICE_TYPE_LENGTH = 1;                     //存储设备类型
    public final static int PLAY_STATUS_LENGTH = 1;                          //播放状态
    public final static int CD_STATUS_LENGTH = 1;                            //CD机状态
    public final static int MUSIC_TYPE_LENGTH = 1;                           //音乐格式
    public final static int PLAY_MODEL_LENGTH = 1;                           //播放模式
    public final static int MUSIC_NAME_CODE_LENGTH = 1;                      //音乐名编码格式
    public final static int music_name_byte_length = 1;                      //音乐名字节数


    public final static int MODULATION_MODE_LENGTH = 1;                      //调制模式
    public final static int CHANNEL_SIZE_LENGTH = 1;                         //总频道数
    public final static int NOW_CHANNEL_NUM_LENGTH = 1;                      //当前频道编号
    public final static int NOW_CHANNEL_FREQUENCY = 2;                       //当前频道频率

    public final static int DEVICE_VOLUME_LENGTH = 1;                       //设备音量
    public final static int PAGE_MARKET_LENGTH = 1;                         //翻页标志

    public static GetInstantStatus init() {
        return new GetInstantStatus();
    }


    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {


        int index = HEAD_PACKAGE_LENGTH;
        index += DEVICE_MAC_LENGTH;
        index += TASK_NUM_LENGTH;
        index += RANDOM_ID_LENGTH;

        String deviceModel = SmartBroadCastUtils.byteToStr(subBytes(list.get(0), index, DEVICE_MODEL_LENGTH));

        if (deviceModel.equals("TX-8627")) {    //CD机
            CDInstantStatus CDInstantStatus = getCDInstantStatus(list.get(0));

            Gson gson = new Gson();

            String json = gson.toJson(CDInstantStatus);

            BaseBean bean = new BaseBean();

            bean.setType("getCdInstantStatus");

            bean.setData(json);

            String jsonResult = gson.toJson(bean);

            Log.i("jsonResult", "handler: " + jsonResult);

            EventBus.getDefault().post(jsonResult);
        } else if (deviceModel.equals("TX-8628")) {    //FM
            FMInstantStatus fmInstantStatus = getFMInstantStatus(list.get(0));

            Gson gson = new Gson();

            String json = gson.toJson(fmInstantStatus);

            BaseBean bean = new BaseBean();

            bean.setType("getFmInstantStatus");

            bean.setData(json);

            String jsonResult = gson.toJson(bean);

            Log.i("jsonResult", "handler: " + jsonResult);

            EventBus.getDefault().post(jsonResult);
        } else if (deviceModel.equals("TX-8601")) {   //网络采集器
            CollectorInstantStatus collectorInstantStatus = getCollectorInstantStatus(list.get(0));

            Gson gson = new Gson();

            String json = gson.toJson(collectorInstantStatus);

            BaseBean bean = new BaseBean();

            bean.setType("getCollectorInstantStatus");

            bean.setData(json);

            String jsonResult = gson.toJson(bean);

            Log.i("jsonResult", "handler: " + jsonResult);

            EventBus.getDefault().post(jsonResult);
        }
    }

    private CDInstantStatus getCDInstantStatus(byte[] bytes) {

        CDInstantStatus CDInstantStatus = new CDInstantStatus();
        int index = HEAD_PACKAGE_LENGTH;

        CDInstantStatus.setTaskNum(byteArrayToInt(subBytes(bytes, index, TASK_NUM_LENGTH)));
        index += TASK_NUM_LENGTH;
        CDInstantStatus.setRandomId(byteArrayToInt(subBytes(bytes, index, RANDOM_ID_LENGTH)));
        index += RANDOM_ID_LENGTH;

        CDInstantStatus.setDeviceMac(SmartBroadCastUtils.getMacAddress(subBytes(bytes, index, DEVICE_MAC_LENGTH)));
        index += DEVICE_MAC_LENGTH;
        CDInstantStatus.setDeviceModel(SmartBroadCastUtils.byteToStr(subBytes(bytes, index, DEVICE_MODEL_LENGTH)));
        index += DEVICE_MODEL_LENGTH;
        CDInstantStatus.setDeviceVolume(byteArrayToInt(subBytes(bytes, index, DEVICE_VOLUME_LENGTH)));
        index += DEVICE_VOLUME_LENGTH;
        CDInstantStatus.setMusicSize(byteArrayToInt(subBytes(bytes, index, MUSIC_SIZE_LENGTH)));
        index += MUSIC_SIZE_LENGTH;
        CDInstantStatus.setNowMusicNum(byteArrayToInt(subBytes(bytes, index, NOW_MUSIC_NUM_LENGTH)));
        index += NOW_MUSIC_NUM_LENGTH;
        CDInstantStatus.setPageMarket(byteArrayToInt(subBytes(bytes, index, PAGE_MARKET_LENGTH)));
        index += PAGE_MARKET_LENGTH;
        int s = SmartBroadCastUtils.byteToInt(subBytes(bytes, index, NOW_MUSIC_TIME_LENGTH)[2]);
        int m = SmartBroadCastUtils.byteToInt(subBytes(bytes, index, NOW_MUSIC_TIME_LENGTH)[1]);
        int h = SmartBroadCastUtils.byteToInt(subBytes(bytes, index, NOW_MUSIC_TIME_LENGTH)[0]);
        CDInstantStatus.setNowMusicTime(h + ":" + m + ":" + s);
        index += NOW_MUSIC_TIME_LENGTH;
        int s1 = SmartBroadCastUtils.byteToInt(subBytes(bytes, index, NOW_TIME_LENGTH)[2]);
        int m1 = SmartBroadCastUtils.byteToInt(subBytes(bytes, index, NOW_TIME_LENGTH)[1]);
        int h1 = SmartBroadCastUtils.byteToInt(subBytes(bytes, index, NOW_TIME_LENGTH)[0]);
        CDInstantStatus.setNowTime(h1 + ":" + m1 + ":" + s1);
        index += NOW_TIME_LENGTH;
        CDInstantStatus.setSaveDeviceType(byteArrayToInt(subBytes(bytes, index, SAVE_DEVICE_TYPE_LENGTH)));
        index += SAVE_DEVICE_TYPE_LENGTH;
        CDInstantStatus.setPlayStatus(byteArrayToInt(subBytes(bytes, index, PLAY_STATUS_LENGTH)));
        index += PLAY_STATUS_LENGTH;
        CDInstantStatus.setCdStatus(byteArrayToInt(subBytes(bytes, index, CD_STATUS_LENGTH)));
        index += CD_STATUS_LENGTH;
        CDInstantStatus.setMusicType(byteArrayToInt(subBytes(bytes, index, MUSIC_TYPE_LENGTH)));
        index += MUSIC_TYPE_LENGTH;
        CDInstantStatus.setPlayModel(byteArrayToInt(subBytes(bytes, index, PLAY_MODEL_LENGTH)));
        index += PLAY_MODEL_LENGTH;
        CDInstantStatus.setMusicNameCode(byteArrayToInt(subBytes(bytes, index, MUSIC_NAME_CODE_LENGTH)));
        index += PLAY_MODEL_LENGTH;
        int nameLength = byteArrayToInt(subBytes(bytes, index, music_name_byte_length));
        index += music_name_byte_length;
        CDInstantStatus.setMusicName(SmartBroadCastUtils.byteToStrUTF16BE(subBytes(bytes, index, nameLength)));
        return CDInstantStatus;
    }


    private FMInstantStatus getFMInstantStatus(byte[] bytes) {

        FMInstantStatus fmInstantStatus = new FMInstantStatus();
        int index = HEAD_PACKAGE_LENGTH;

        fmInstantStatus.setTaskNum(byteArrayToInt(subBytes(bytes, index, TASK_NUM_LENGTH)));
        index += TASK_NUM_LENGTH;
        fmInstantStatus.setRandomId(byteArrayToInt(subBytes(bytes, index, RANDOM_ID_LENGTH)));
        index += RANDOM_ID_LENGTH;

        fmInstantStatus.setDeviceMac(SmartBroadCastUtils.getMacAddress(subBytes(bytes, index, DEVICE_MAC_LENGTH)));
        index += DEVICE_MAC_LENGTH;
        fmInstantStatus.setDeviceModel(SmartBroadCastUtils.byteToStr(subBytes(bytes, index, DEVICE_MODEL_LENGTH)));
        index += DEVICE_MODEL_LENGTH;
        fmInstantStatus.setDeviceVolume(byteArrayToInt(subBytes(bytes, index, DEVICE_VOLUME_LENGTH)));
        index += DEVICE_VOLUME_LENGTH;
        fmInstantStatus.setPlayStatus(byteArrayToInt(subBytes(bytes, index, PLAY_STATUS_LENGTH)));
        index += PLAY_STATUS_LENGTH;
        fmInstantStatus.setModulationMode(byteArrayToInt(subBytes(bytes, index, MODULATION_MODE_LENGTH)));
        index += MODULATION_MODE_LENGTH;
        fmInstantStatus.setChannelSize(byteArrayToInt(subBytes(bytes, index, CHANNEL_SIZE_LENGTH)));
        index += CHANNEL_SIZE_LENGTH;
        fmInstantStatus.setNowChannelNum(byteArrayToInt(subBytes(bytes, index, NOW_CHANNEL_NUM_LENGTH)));
        index += NOW_CHANNEL_NUM_LENGTH;
        fmInstantStatus.setNowChannelFrequency(byteArrayToInt(subBytes(bytes, index, NOW_CHANNEL_FREQUENCY)));
        return fmInstantStatus;
    }

    private CollectorInstantStatus getCollectorInstantStatus(byte[] bytes) {

        CollectorInstantStatus collectorInstantStatus = new CollectorInstantStatus();
        int index = HEAD_PACKAGE_LENGTH;

        collectorInstantStatus.setTaskNum(byteArrayToInt(subBytes(bytes, index, TASK_NUM_LENGTH)));
        index += TASK_NUM_LENGTH;
        collectorInstantStatus.setRandomId(byteArrayToInt(subBytes(bytes, index, RANDOM_ID_LENGTH)));
        index += RANDOM_ID_LENGTH;

        collectorInstantStatus.setDeviceMac(SmartBroadCastUtils.getMacAddress(subBytes(bytes, index, DEVICE_MAC_LENGTH)));
        index += DEVICE_MAC_LENGTH;
        collectorInstantStatus.setDeviceModel(SmartBroadCastUtils.byteToStr(subBytes(bytes, index, DEVICE_MODEL_LENGTH)));
        index += DEVICE_MODEL_LENGTH;
        collectorInstantStatus.setDeviceVolume(byteArrayToInt(subBytes(bytes, index, DEVICE_VOLUME_LENGTH)));
        index += DEVICE_VOLUME_LENGTH;
        collectorInstantStatus.setPlayStatus(byteArrayToInt(subBytes(bytes, index, PLAY_STATUS_LENGTH)));
        index += PLAY_STATUS_LENGTH;
        return collectorInstantStatus;
    }


}
