package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.ExecuteTaskDateResult;
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
 * created：2018/10/11 19:25
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: 今日执行周几任务
 */


public class ExecuteTaskDate {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int EXECUTE_WEEK_LENGTH = 1;                         //今日执行任务周
    public final static int EXECUTE_DATE_LENGTH = 3;                         //今日执行任务日期


    public static ExecuteTaskDate init() {
        return new ExecuteTaskDate();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {
        ExecuteTaskDateResult executeTaskDateResult = getExecuteTaskDateResult(list.get(0));
        Gson gson = new Gson();
        String json = gson.toJson(executeTaskDateResult);
        BaseBean bean = new BaseBean();
        bean.setType("ExecuteTaskDateResult");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        Log.i("jsonResult", "handler: " + jsonResult);
        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 今日执行周几任务信息
     *
     * @param bytes
     * @return
     */
    public ExecuteTaskDateResult getExecuteTaskDateResult(byte[] bytes) {
        //包头
        int head = HEAD_PACKAGE_LENGTH;
        //去掉包头，尾字节
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        ExecuteTaskDateResult executeTaskDateResult = new ExecuteTaskDateResult();
        //今日执行任务周
        int index = 0;
        executeTaskDateResult.setExecuteTaskWeek(byteToInt(subBytes(bytes, 0, EXECUTE_WEEK_LENGTH)[0]));
        index+= EXECUTE_WEEK_LENGTH;
        //今日执行任务日期
        executeTaskDateResult.setExecuteTaskDate(byteToDates(subBytes(bytes, index, EXECUTE_DATE_LENGTH)));
        return executeTaskDateResult;
    }


    /**
     * 获取今日执行周几任务指令
     *
     * @param host                  定时器ip地址
     */
    public static void sendCMD(String host) {
        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getExecuteTaskDateBytes(), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host, getExecuteTaskDateBytes());
        }
    }


    /**
     * 获取日执行周几任务字节流
     *
     * @return
     */
    private static byte[] getExecuteTaskDateBytes() {
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("09B3");
        //添加本机Mac
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
        String result = year + "-" + (month >= 10 ? month : "0" + month) + "-" + (day >= 10 ? day : "0" + day);
        return result;
    }

}
