package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.MusicFolderInfo;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.checkSum;

/**
 * author： lghandroid
 * created：2018/8/23 8:48
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: _获取曲目库文件夹列表
 */

public class GetMusicFolderList {

    private static final int HEAD_PACKET_LENGTH = 28;               //包头
    private static final int FOLDER_NAME_LENGTH = 32;               //文件夹名称
    private static final int ALL_SONG_LENGTH = 1;                   //曲目总数
    private static final int OVER_FLOW_FLAG_LENGTH = 1;             //歌曲数量溢出标志

    public static GetMusicFolderList init() {
        return new GetMusicFolderList();
    }

    //主要业务处理，字节转对象，对象转集合再合并，最终输出json字符串
    public void handler(List<byte[]> list) {
        List<MusicFolderInfo> musicFolderList = new ArrayList<>();
        for (byte[] b : list) {
            List<MusicFolderInfo> musicFolderInfos = byteToFoundDeviceInfo(b);
            musicFolderList = objectMerging(musicFolderList, musicFolderInfos);
        }
        Gson gson = new Gson();
        String json = gson.toJson(musicFolderList);
        BaseBean baseBean = new BaseBean();
        baseBean.setType("getMusicFolderList");
        baseBean.setData(json);
        String toJson = gson.toJson(baseBean);
        Log.i("jsonResult", "handler: " + toJson);
        EventBus.getDefault().post(toJson);

    }

    //数据封装成对象
    private List<MusicFolderInfo> byteToFoundDeviceInfo(byte[] b) {
        List<MusicFolderInfo> musicFolderInfoList = new ArrayList<>();
        //当前文件夹总数
        int musicFolderCount = SmartBroadCastUtils.byteToInt(SmartBroadCastUtils.subBytes(b, HEAD_PACKET_LENGTH + 2, 1)[0]);
        Log.i("music", "当前文件夹总数: " + musicFolderCount);
        int itemBt = FOLDER_NAME_LENGTH + ALL_SONG_LENGTH + OVER_FLOW_FLAG_LENGTH;  //一个item所占字节数
        byte[] musicFolderListBt = SmartBroadCastUtils.subBytes(b, HEAD_PACKET_LENGTH + 3, itemBt * musicFolderCount);
        for (int i = 0; i < musicFolderListBt.length; i += itemBt) {
            int index = 0;
            MusicFolderInfo musicFolderInfo = new MusicFolderInfo();
            //文件夹名称
            String musicFolderName = SmartBroadCastUtils.byteToStr(SmartBroadCastUtils.subBytes(musicFolderListBt, i, FOLDER_NAME_LENGTH));
            index += FOLDER_NAME_LENGTH;
            //曲目总数
            int allSongNum = SmartBroadCastUtils.byteToInt(SmartBroadCastUtils.subBytes(musicFolderListBt, i + index, ALL_SONG_LENGTH)[0]);
            index += ALL_SONG_LENGTH;
            //曲目数量溢出标志
            int overflowFlag = SmartBroadCastUtils.byteToInt(SmartBroadCastUtils.subBytes(musicFolderListBt, i + index, OVER_FLOW_FLAG_LENGTH)[0]);
            musicFolderInfo.setMusicFolderName(musicFolderName);
            musicFolderInfo.setAllMusicNum(allSongNum);
            musicFolderInfo.setOverflowFalg(overflowFlag);
            musicFolderInfoList.add(musicFolderInfo);
        }
        return musicFolderInfoList;
    }


    private List<MusicFolderInfo> objectMerging(List<MusicFolderInfo> musicFolderInfos1, List<MusicFolderInfo> musicFolderInfos2) {
        List<MusicFolderInfo> musicFolderInfosoList = new ArrayList<>();
        for (MusicFolderInfo musicFolderInfo : musicFolderInfos1) {
            musicFolderInfosoList.add(musicFolderInfo);
        }
        for (MusicFolderInfo musicFoldereInfo : musicFolderInfos2) {
            musicFolderInfosoList.add(musicFoldereInfo);
        }
        return musicFolderInfosoList;
    }

    //发送指令
    public static void sendCMD(String host) {
        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getMusicFolderList(), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host,getMusicFolderList());
        }
    }

    //获取音乐文件夹列表
    public static byte[] getMusicFolderList() {
        StringBuffer cmdStr = new StringBuffer();
        //起始标志
        cmdStr.append("AA55");
        //长度
        cmdStr.append("0000");
        //命令
        cmdStr.append("00B8");
        //本机mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //校验值
        cmdStr.append(checkSum(cmdStr.substring(4)));
        //结束标志
        cmdStr.append("55AA");
        byte[] bytes = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return bytes;
    }
}
