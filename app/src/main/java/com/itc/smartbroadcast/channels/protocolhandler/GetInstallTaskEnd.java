package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.GetInstallTaskEndResult;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteArrayToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;


/**
 * 获取及时任务结束
 */
public class GetInstallTaskEnd {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int TASK_NUM_LENGTH = 2;                             //任务编号长度
    public final static int RANDOM_ID_LENGTH = 2;                            //随机ID号长度
    public final static int RESULT = 1;                                      //结果

    public static GetInstallTaskEnd init() {
        return new GetInstallTaskEnd();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {

        byte [] bytes = list.get(0);

        GetInstallTaskEndResult getInstallTaskEndResult = new GetInstallTaskEndResult();

        int index = HEAD_PACKAGE_LENGTH;

        getInstallTaskEndResult.setTaskNum(byteArrayToInt(subBytes(bytes, index, TASK_NUM_LENGTH)));
        index += TASK_NUM_LENGTH;
        getInstallTaskEndResult.setRandomId(byteArrayToInt(subBytes(bytes, index, RANDOM_ID_LENGTH)));
        index += RANDOM_ID_LENGTH;

        Gson gson = new Gson();
        String json = gson.toJson(getInstallTaskEndResult);
        BaseBean bean = new BaseBean();
        bean.setType("getInstallTaskEnd");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        com.orhanobut.logger.Logger.json(jsonResult);
        Log.i("jsonResult", "handler: " + jsonResult);
        EventBus.getDefault().post(jsonResult);
    }
}
