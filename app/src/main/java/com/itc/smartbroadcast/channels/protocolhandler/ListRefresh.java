package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.GetInstallTaskEndResult;
import com.itc.smartbroadcast.bean.ListRefreshResult;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteArrayToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;



/**
 * author： lghandroid
 * created：2018/2/18 11:08
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: 定时器主动推送刷新app列表协议(包括设备、分区、音乐、账户、今日任务列表)
 */
public class ListRefresh {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int RESULT = 1;                                      //结果

    public static ListRefresh init() {
        return new ListRefresh();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {

        byte [] bytes = list.get(0);
        ListRefreshResult listRefreshResult = new ListRefreshResult();
        int index = HEAD_PACKAGE_LENGTH;
        listRefreshResult.setResult(byteArrayToInt(subBytes(bytes, index, RESULT)));

        Gson gson = new Gson();
        String json = gson.toJson(listRefreshResult);
        BaseBean bean = new BaseBean();
        bean.setType("listRefresh");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        com.orhanobut.logger.Logger.json(jsonResult);
        Log.i("jsonResult", "handler: " + jsonResult);
        EventBus.getDefault().post(jsonResult);
    }
}
