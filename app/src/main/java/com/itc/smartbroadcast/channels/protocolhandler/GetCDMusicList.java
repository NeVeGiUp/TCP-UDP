package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.CDMusic;
import com.itc.smartbroadcast.bean.CDMusicList;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteArrayToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * Created by lik on 18-9-18.
 */

public class GetCDMusicList {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度

    public final static int TASK_NUM_LENGTH = 2;                             //任务编号长度
    public final static int RANDOM_ID_LENGTH = 2;                            //随机ID号长度

    public final static int NOW_PAGE_NUM_LENGTH = 2;                        //页编号
    public final static int SAVE_DEVICE_TYPE_LENGTH = 1;                     //存储设备类型
    public final static int MUSIC_NAME_CODE_LENGTH = 1;                      //音乐名编码格式
    public final static int MUSIC_NAME_BYTE_LENGTH = 64;                      //音乐名字节数
    public final static int NOW_MUSIC_NUM_LENGTH = 2;                      //曲目编号

    public static GetCDMusicList init() {
        return new GetCDMusicList();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {

        CDMusicList cdMusicList = getCDInstantStatus(list.get(0));

        Gson gson = new Gson();

        String json = gson.toJson(cdMusicList);

        BaseBean bean = new BaseBean();

        bean.setType("getCDMusicList");

        bean.setData(json);

        String jsonResult = gson.toJson(bean);

        Log.i("jsonResult", "handler: " + jsonResult);

        EventBus.getDefault().post(jsonResult);
    }

    private CDMusicList getCDInstantStatus(byte[] bytes) {

        CDMusicList cdMusicList = new CDMusicList();

        cdMusicList.setTerminalMac(SmartBroadCastUtils.hexstrToMac(SmartBroadCastUtils.bytesToHexString(SmartBroadCastUtils.subBytes(bytes, 6, 6))));
        int index = HEAD_PACKAGE_LENGTH;

        cdMusicList.setTaskNum(byteArrayToInt(subBytes(bytes, index, TASK_NUM_LENGTH)));
        index += TASK_NUM_LENGTH;
        cdMusicList.setRandomId(byteArrayToInt(subBytes(bytes, index, RANDOM_ID_LENGTH)));
        index += RANDOM_ID_LENGTH;

        cdMusicList.setNowMusicNum(SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, index, NOW_MUSIC_NUM_LENGTH)));
        index += NOW_MUSIC_NUM_LENGTH;
        cdMusicList.setSaveDeviceType(SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, index, SAVE_DEVICE_TYPE_LENGTH)));
        index += SAVE_DEVICE_TYPE_LENGTH;
        cdMusicList.setMusicNameCode(SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, index, MUSIC_NAME_CODE_LENGTH)));
        index += MUSIC_NAME_CODE_LENGTH;

        int end = SmartBroadCastUtils.subBytes(bytes, index, bytes.length-index).length - END_PACKAGE_LENGTH;
        byte[] musicBytes = SmartBroadCastUtils.subBytes(bytes, index, end);

        int itemSize = MUSIC_NAME_BYTE_LENGTH + NOW_MUSIC_NUM_LENGTH;

        List<CDMusic> musicList = new ArrayList<>();
        for (int i = 0; i < musicBytes.length; i += itemSize) {
            CDMusic music = new CDMusic();
            music.setNum(SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, index, NOW_MUSIC_NUM_LENGTH)));
            index += NOW_MUSIC_NUM_LENGTH;
            String musicName = SmartBroadCastUtils.byteToStrUTF16BE(SmartBroadCastUtils.subBytes(bytes, index, MUSIC_NAME_BYTE_LENGTH));
            music.setMusicName(musicName);
            index += MUSIC_NAME_BYTE_LENGTH;
            musicList.add(music);
        }
        cdMusicList.setMusicNameList(musicList);
        return cdMusicList;
    }
}
