package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.MusicMsgInfo;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.checkSum;

/**
 * author： lik
 * created：2018/8/23 8:48
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: _获取指定文件夹歌曲库列表
 */

public class GetAlarmMusicList {

    public static final int FOLDER_NAME_LENGTH = 32;                   //文件夹名称
    private static final int MUSIC_NAME_LENGTH = 64;                   //曲目名称
    private static final int MUSIC_TIME_LENGTH = 2;                    //曲目时间
    private static final int HEAD_PACKET_LENGTH = 28;                  //包头

    public static GetAlarmMusicList init() {
        return new GetAlarmMusicList();
    }

    //主要业务处理，字节转对象，对象转集合再合并，最终输出json字符串
    public void handler(List<byte[]> list) {

        List<MusicMsgInfo> musicList = new ArrayList<>();

        for (byte[] b : list) {
            List<MusicMsgInfo> musicFolderInfos = byteToFoundDeviceInfo(b);
            musicList = objectMerging(musicList, musicFolderInfos);
        }

        Gson gson = new Gson();
        String json = gson.toJson(musicList);

        BaseBean baseBean = new BaseBean();
        baseBean.setType("getAlarmMusicList");
        baseBean.setData(json);
        String toJson = gson.toJson(baseBean);
        Log.i("jsonResult", "handler: " + toJson);

        EventBus.getDefault().post(toJson);

    }

    //数据封装成对象
    private List<MusicMsgInfo> byteToFoundDeviceInfo(byte[] b) {
        List<MusicMsgInfo> musicInfoList = new ArrayList<>();
        //当前歌曲总数
        int musicCount = SmartBroadCastUtils.byteToInt(SmartBroadCastUtils.subBytes(b, HEAD_PACKET_LENGTH + 2, 1)[0]);
        //一个item所占字节数
        int itemBt = MUSIC_NAME_LENGTH + MUSIC_TIME_LENGTH;
        byte[] musicListBt = SmartBroadCastUtils.subBytes(b, HEAD_PACKET_LENGTH + 3, itemBt * musicCount);

        for (int i = 0; i < musicListBt.length; i += itemBt) {
            MusicMsgInfo musicInfo = new MusicMsgInfo();
            //曲目名称
            String musicName = SmartBroadCastUtils.byteToStr(SmartBroadCastUtils.subBytes(musicListBt, i, MUSIC_NAME_LENGTH));
            //曲目时间
            int musicTime = SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(musicListBt, i + MUSIC_NAME_LENGTH, MUSIC_TIME_LENGTH));

            musicInfo.setMusicName(musicName);
            Log.i("GetAlarmMusicList", "name: "+musicName+"\nHEX:"+SmartBroadCastUtils.bytesToHexString(SmartBroadCastUtils.subBytes(musicListBt, i, MUSIC_NAME_LENGTH)));
            musicInfo.setMusicTime(musicTime);

            musicInfoList.add(musicInfo);

        }

        return musicInfoList;
    }


    private List<MusicMsgInfo> objectMerging(List<MusicMsgInfo> musicFolderInfos1, List<MusicMsgInfo> musicFolderInfos2) {

        List<MusicMsgInfo> musicInfoList = new ArrayList<>();
        for (MusicMsgInfo musicInfo : musicFolderInfos1) {
            musicInfoList.add(musicInfo);
        }
        for (MusicMsgInfo musicInfo : musicFolderInfos2) {
            musicInfoList.add(musicInfo);
        }
        return musicInfoList;
    }

    //发送指令 需要传递文件夹名称
    public static void sendCMD(String host) {


        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getMusicList(""), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host,getMusicList(""));
        }
    }

    //获取音乐文件夹列表
    public static byte[] getMusicListCmd(String hex) {
        StringBuffer cmdStr = new StringBuffer();
        //起始标志
        cmdStr.append("AA55");
        //长度
        cmdStr.append("0000");
        //命令
        cmdStr.append("03B7");
        //本机mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //文件夹名称
        cmdStr.append(hex);
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //校验值
        cmdStr.append(checkSum(cmdStr.substring(4)));
        //结束标志
        cmdStr.append("55AA");
        byte[] bytes = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return bytes;
    }

    /**
     * 获取指定文件夹歌曲库列表
     *
     * @return
     */
    public static byte[] getMusicList(String musicFolder) {
        String hexMusicFolder = SmartBroadCastUtils.chinese2Hex(musicFolder, GetAlarmMusicList.FOLDER_NAME_LENGTH);
        String allHexMusicHolder = SmartBroadCastUtils.str2HexStr(musicFolder);

        int count = GetAlarmMusicList.FOLDER_NAME_LENGTH * 2;
        if (allHexMusicHolder.length() < count) {
            byte[] bytes = getMusicListCmd(hexMusicFolder);
            return bytes;

        } else if (allHexMusicHolder.length() == count) {
            byte[] bytes = getMusicListCmd(allHexMusicHolder);
            return bytes;
        }
        return new byte[0];
    }
}
