package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.TimerStatusQueryResult;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.checkSum;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * author： lghandroid
 * created：2018/10/18 11:36
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: 定时器在线状态查询
 */


public class TimerStatusQuery {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int TIME_MSG_LENGTH = 6;                             //时间信息
    public final static int SD_CARD_STATE_LENGTH = 1;                        //sd卡状态
    public final static int RETAIN_LENGTH = 8;                               //保留字段
    public final static int TASK_TOTAL_LENGTH = 1;                           //任务总数
    public final static int TASK_NUM_LENGTH = 2;                             //任务编号


    public static TimerStatusQuery init() {
        return new TimerStatusQuery();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {
        TimerStatusQueryResult timerStatusQueryResult = getTimerStatusQueryResult(list.get(0));
        Gson gson = new Gson();
        String json = gson.toJson(timerStatusQueryResult);
        BaseBean bean = new BaseBean();
        bean.setType("TimerStatusQueryResult");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        Log.i("jsonResult", "handler: " + jsonResult);
        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 获取定时器在线状态结果
     *
     * @param bytes
     * @return
     */
    public TimerStatusQueryResult getTimerStatusQueryResult(byte[] bytes) {
        //包头
        int head = HEAD_PACKAGE_LENGTH;
        //去掉包头，尾字节
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        TimerStatusQueryResult timerStatusQueryResult = new TimerStatusQueryResult();
        //时间信息
        int index = 0;
        timerStatusQueryResult.setTimeMsg(byteToDates(subBytes(bytes, 0, TIME_MSG_LENGTH)));
        index += TIME_MSG_LENGTH;
        //sd状态
        timerStatusQueryResult.setSdcardStatus(byteToInt(subBytes(bytes, index, SD_CARD_STATE_LENGTH)[0]));
/*        index += SD_CARD_STATE_LENGTH;
        保留字段
        index += RETAIN_LENGTH;
        任务总数
        int taskTotal = byteToInt(subBytes(bytes, index, TASK_TOTAL_LENGTH)[0]);
        timerStatusQueryResult.setTaskTotal(taskTotal);
        index += TASK_TOTAL_LENGTH;
        //当前进行中任务编号,定时器允许进行中任务最多4个
        byte[] taskNumBytes = SmartBroadCastUtils.subBytes(bytes, index, taskTotal * TASK_NUM_LENGTH);
        ArrayList<Integer> taskNumList = new ArrayList<>();
        for (int j = 0; j < taskNumBytes.length; j += TASK_NUM_LENGTH) {
            if (j < taskNumBytes.length) {
                int taskNum = byteArrayToInt(subBytes(taskNumBytes, j, 2));
                taskNumList.add(taskNum);
            }
        }*/
        return timerStatusQueryResult;
    }


    /**
     * 获取查询定时器在线状态指令
     *
     * @param host 定时器ip地址
     */
    public static void sendCMD(String host, int userNum) {

        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getTimerStatusQueryBytes(userNum), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host, getTimerStatusQueryBytes(userNum));
        }
    }


    /**
     * 获取日定时器在线状态字节流
     *
     * @return
     */
    private static byte[] getTimerStatusQueryBytes(int userNum) {
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("05B9");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //账户编号
        String hexUserNum = Integer.toHexString(userNum);
        cmdStr.append(hexUserNum.length() == 1 ? "0" + hexUserNum : hexUserNum);
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //校验值
        cmdStr.append(checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");
        byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return result;
    }


    /**
     * 将byte转为时间
     *
     * @param b
     * @return 日期YYYY-MM-DD
     */
    public String byteToDates(byte[] b) {
        int year = 2000 + SmartBroadCastUtils.byteToInt(b[0]);
        int month = SmartBroadCastUtils.byteToInt(b[1]);
        int day = SmartBroadCastUtils.byteToInt(b[2]);
        int hour = SmartBroadCastUtils.byteToInt(b[3]);
        int minute = SmartBroadCastUtils.byteToInt(b[4]);
        int second = SmartBroadCastUtils.byteToInt(b[5]);
        String result = year + "年" + (month >= 10 ? month : "0" + month) + "月" + (day >= 10 ? day : "0" + day)
                + "日 " + (hour >= 10 ? hour : "0" + hour) + ":" + (minute >= 10 ? minute : "0" + minute) + ":" + (second >= 10 ? second : "0" + second);
        return result;
    }

}
