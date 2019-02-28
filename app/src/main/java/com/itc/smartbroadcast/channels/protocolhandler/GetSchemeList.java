package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.Scheme;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToDate;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToStr;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * 获取方案列表
 * Created by lik on 2018/8/22.
 */

public class GetSchemeList {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int SCHEME_SIZE_LENGTH = 1;                          //方案总数
    public final static int SCHEME_NUM_LENGTH = 1;                           //方案编号
    public final static int SCHEME_STATUS_LENGTH = 1;                        //方案状态
    public final static int SCHEME_NAME_LENGTH = 32;                         //方案名称
    public final static int SCHEME_START_DATE_LENGTH = 3;                    //方案开始时间
    public final static int SCHEME_END_DATE_LENGTH = 3;                      //方案结束时间

    public static GetSchemeList init() {
        return new GetSchemeList();
    }


    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {

        List<Scheme> schemeList = getScheme(list.get(0));

        Gson gson = new Gson();

        String json = gson.toJson(schemeList);

        BaseBean bean = new BaseBean();

        bean.setType("getSchemeList");

        bean.setData(json);

        String jsonResult = gson.toJson(bean);


        System.out.println(jsonResult);
        System.out.println(schemeList.size());

        Log.i("jsonResult", "handler: " + jsonResult);
        Log.i("jsonResult", "handler: size" + schemeList.size());

        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 通过byte数组获取SchemeTask对象
     *
     * @param bytes
     * @return Scheme对象
     */
    public List<Scheme> getScheme(byte[] bytes) {
        List<Scheme> list = new ArrayList<>();

        //每一个对象对应的总字节数
        int itemSize = SCHEME_NUM_LENGTH + SCHEME_STATUS_LENGTH + SCHEME_NAME_LENGTH + SCHEME_START_DATE_LENGTH + SCHEME_END_DATE_LENGTH;
        //头部多余数
        int head = HEAD_PACKAGE_LENGTH + SCHEME_SIZE_LENGTH;
        //去掉方案总数的字节
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        //遍历取出对象
        for (int i = 0; i <= bytes.length - itemSize; i += itemSize) {

            Scheme scheme = new Scheme();

            //游标
            int index = 0;
            int schemeNum = byteToInt(bytes[0 + i]);
            index += SCHEME_NUM_LENGTH;
            int schemeStatus = byteToInt(bytes[index + i]);
            index += SCHEME_STATUS_LENGTH;
            String schemeName = byteToStr(subBytes(bytes, index + i, SCHEME_NAME_LENGTH));
            index += SCHEME_NAME_LENGTH;
            String schemeStartDate = byteToDate(subBytes(bytes, index + i, SCHEME_START_DATE_LENGTH));
            index += SCHEME_START_DATE_LENGTH;
            String schemeEndDate = byteToDate(subBytes(bytes, index + i, SCHEME_END_DATE_LENGTH));

            scheme.setSchemeNum(schemeNum);
            scheme.setSchemeStatus(schemeStatus);
            scheme.setSchemeName(schemeName);
            scheme.setSchemeStartDate(schemeStartDate);
            scheme.setSchemeEndDate(schemeEndDate);
            list.add(scheme);
        }
        return list;
    }

    /**
     * 发送获取方案命令
     */
    public static void sendCMD(String host) {

        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getSchemeList(), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host,getSchemeList());
        }

    }


    /**
     * 获取方案列表
     *
     * @return
     */
    public static byte[] getSchemeList() {
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("00B3");
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
        //添加校验码
        cmdStr.append("" + SmartBroadCastUtils.checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");
        byte[] bytes = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return bytes;
    }
}
