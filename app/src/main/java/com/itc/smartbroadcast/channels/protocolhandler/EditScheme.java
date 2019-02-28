package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditSchemeResult;
import com.itc.smartbroadcast.bean.Scheme;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.itc.smartbroadcast.channels.protocolhandler.GetSchemeList.SCHEME_NAME_LENGTH;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * 编辑方案
 * Created by lik on 2018/8/23.
 */

public class EditScheme {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int SCHEME_NUM_LENGTH = 1;                           //方案编号
    public final static int SCHEME_CONFIGURE_LENGTH = 1;                     //方案配置符
    public final static int RESULT = 1;                                      //结果

    public static EditScheme init() {
        return new EditScheme();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {

        EditSchemeResult schemeResult = getSchemeResult(list.get(0));

        Gson gson = new Gson();

        String json = gson.toJson(schemeResult);

        BaseBean bean = new BaseBean();

        bean.setType("editSchemeResult");

        bean.setData(json);

        String jsonResult = gson.toJson(bean);

        Log.i("jsonResult", "handler: " + jsonResult);

        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 获取编辑方案结果
     *
     * @param bytes
     * @return
     */
    public EditSchemeResult getSchemeResult(byte[] bytes) {

        //头部多余数
        int head = HEAD_PACKAGE_LENGTH;
        //去掉方案总数的字节
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        EditSchemeResult editSchemeResult = new EditSchemeResult();
        editSchemeResult.setSchemeNum(SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, 0, SCHEME_NUM_LENGTH)));
        editSchemeResult.setResult(SmartBroadCastUtils.byteArrayToInt(SmartBroadCastUtils.subBytes(bytes, SCHEME_NUM_LENGTH + SCHEME_CONFIGURE_LENGTH, RESULT)));
        return editSchemeResult;
    }


    /**
     * 编辑方案
     *
     * @param host   ip地址
     * @param scheme scheme对象
     * @param type   操作类型：
     *               （0：添加，1：修改，2：删除）
     */
    public static void sendCMD(String host, Scheme scheme, int type) {


        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {


                switch (type) {
                    case 0: //添加
                    {
                        byte[] bytes = SmartBroadCastUtils.CloudUtil(getAddScheme(scheme), host, false);
                        NettyTcpClient.getInstance().sendPackage(host, bytes);
                    }
                    break;
                    case 1: //修改
                    {
                        byte[] bytes = SmartBroadCastUtils.CloudUtil(getEditScheme(scheme), host, false);
                        NettyTcpClient.getInstance().sendPackage(host, bytes);
                    }
                    break;
                    case 2: //删除
                    {
                        byte[] bytes = SmartBroadCastUtils.CloudUtil(getDeleteScheme(scheme), host, false);
                        NettyTcpClient.getInstance().sendPackage(host, bytes);
                    }
                    break;
                    case 3: //克隆
                    {
                        byte[] bytes = SmartBroadCastUtils.CloudUtil(getCopyScheme(scheme), host, false);
                        NettyTcpClient.getInstance().sendPackage(host, bytes);
                    }
                    break;
                }


            }
        } else {
            switch (type) {
                case 0: //添加
                    NettyUdpClient.getInstance().sendPackage(host, getAddScheme(scheme));
                    break;
                case 1: //修改
                    NettyUdpClient.getInstance().sendPackage(host, getEditScheme(scheme));
                    break;
                case 2: //删除
                    NettyUdpClient.getInstance().sendPackage(host, getDeleteScheme(scheme));
                    break;
                case 3: //克隆
                    NettyUdpClient.getInstance().sendPackage(host, getCopyScheme(scheme));
                    break;
            }
        }
    }


    /**
     * 获取克隆方案的byte流
     *
     * @param scheme
     * @return
     */
    public static byte[] getCopyScheme(Scheme scheme) {
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("05B3");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //克隆方案操作符
        cmdStr.append("03");
        //获取方案编号
        String schemeNum = Integer.toHexString(scheme.getSchemeNum());
        cmdStr.append((schemeNum.length() == 1) ? "0" + schemeNum : schemeNum);

        /**
         * 0 0 0: 1
         * 0 0 1: 2
         * 0 1 0: 3
         * 0 1 1: 4
         * 1 0 0: 5
         * 1 0 1: 6
         * 1 1 0: 7
         * 1 1 1: 8
         */
        //方案配置,设置为所有字段都修改
        cmdStr.append("FF");
        //添加方案，方案状态默认为失效00
        String schemeStatus;
        if (scheme.getSchemeStatus() == 0) {
            schemeStatus = "00";
        } else {
            schemeStatus = "01";
        }
        cmdStr.append(schemeStatus);
        //获取方案名称的16进制
        String schemeNameHex = SmartBroadCastUtils.str2HexStr(scheme.getSchemeName());
        if (schemeNameHex.length() > SCHEME_NAME_LENGTH * 2) {
            schemeNameHex = schemeNameHex.substring(0, SCHEME_NAME_LENGTH * 2);
        } else {
            int len = schemeNameHex.length();
            int count = (SCHEME_NAME_LENGTH * 2) - schemeNameHex.length();
            for (int i = 0; i < count; i++) {
                schemeNameHex += "0";
            }
        }
        //添加方案名称
        cmdStr.append(schemeNameHex);
        //添加方案开始时间
        cmdStr.append(SmartBroadCastUtils.dateToHex(scheme.getSchemeStartDate()));
        //添加方案结束时间
        cmdStr.append(SmartBroadCastUtils.dateToHex(scheme.getSchemeEndDate()));
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //添加校验码
        cmdStr.append("" + SmartBroadCastUtils.checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");
        Log.i("msg", "getAddScheme: " + cmdStr);
        byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return result;
    }


    /**
     * 获取编辑方案的byte流
     *
     * @param scheme
     * @return
     */
    public static byte[] getEditScheme(Scheme scheme) {
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("05B3");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //删除方案操作符
        cmdStr.append("02");
        //获取方案编号
        String schemeNum = Integer.toHexString(scheme.getSchemeNum());
        cmdStr.append((schemeNum.length() == 1) ? "0" + schemeNum : schemeNum);

        /**
         * 0 0 0: 1
         * 0 0 1: 2
         * 0 1 0: 3
         * 0 1 1: 4
         * 1 0 0: 5
         * 1 0 1: 6
         * 1 1 0: 7
         * 1 1 1: 8
         */
        //方案配置,设置为所有字段都修改
        cmdStr.append("FF");
        //添加方案，方案状态默认为失效00
        String schemeStatus;
        if (scheme.getSchemeStatus() == 0) {
            schemeStatus = "00";
        } else {
            schemeStatus = "01";
        }
        cmdStr.append(schemeStatus);
        //获取方案名称的16进制
        String schemeNameHex = SmartBroadCastUtils.str2HexStr(scheme.getSchemeName());
        if (schemeNameHex.length() > SCHEME_NAME_LENGTH * 2) {
            schemeNameHex = schemeNameHex.substring(0, SCHEME_NAME_LENGTH * 2);
        } else {
            int len = schemeNameHex.length();
            int count = (SCHEME_NAME_LENGTH * 2) - schemeNameHex.length();
            for (int i = 0; i < count; i++) {
                schemeNameHex += "0";
            }
        }
        //添加方案名称
        cmdStr.append(schemeNameHex);
        //添加方案开始时间
        cmdStr.append(SmartBroadCastUtils.dateToHex(scheme.getSchemeStartDate()));
        //添加方案结束时间
        cmdStr.append(SmartBroadCastUtils.dateToHex(scheme.getSchemeEndDate()));
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //添加校验码
        cmdStr.append("" + SmartBroadCastUtils.checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");
        Log.i("msg", "getAddScheme: " + cmdStr);
        byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return result;
    }


    /**
     * 获取删除方案的byte流
     *
     * @param scheme
     * @return
     */
    public static byte[] getDeleteScheme(Scheme scheme) {
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("05B3");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //删除方案操作符
        cmdStr.append("01");
        //获取方案编号
        String schemeNum = Integer.toHexString(scheme.getSchemeNum());
        cmdStr.append((schemeNum.length() == 1) ? "0" + schemeNum : schemeNum);
        //删除方案，方案配置字段全为0
        cmdStr.append("00");
        //删除方案，方案状态默认为失效00
        cmdStr.append("00");
        //获取方案名称的16进制
        String schemeNameHex = "";
        for (int i = 0; i < SCHEME_NAME_LENGTH; i++) {
            schemeNameHex += "00";
        }
        //添加方案名称
        cmdStr.append(schemeNameHex);
        //添加方案开始时间
        cmdStr.append("000000");
        //添加方案结束时间
        cmdStr.append("000000");
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //添加校验码
        cmdStr.append("" + SmartBroadCastUtils.checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");
        Log.i("msg", "getAddScheme: " + cmdStr);

        byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return result;
    }

    /**
     * 获取添加方案的byte流
     *
     * @param scheme
     * @return
     */
    public static byte[] getAddScheme(Scheme scheme) {

        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("05B3");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //添加方案操作符
        cmdStr.append("00");
        //添加方案，因为此处为添加操作，所以方案编号为00
        cmdStr.append("00");
        //方案，方案配置字段全添加为1
        cmdStr.append("FF");
        //添加方案，方案状态默认为失效00
        cmdStr.append("00");
        //获取方案名称的16进制
        String schemeNameHex = SmartBroadCastUtils.str2HexStr(scheme.getSchemeName());
        if (schemeNameHex.length() > SCHEME_NAME_LENGTH * 2) {
            schemeNameHex = schemeNameHex.substring(0, SCHEME_NAME_LENGTH * 2);
        } else {
            int len = schemeNameHex.length();
            int count = (SCHEME_NAME_LENGTH * 2) - schemeNameHex.length();
            for (int i = 0; i < count; i++) {
                schemeNameHex += "0";
            }
        }
        //添加方案名称
        cmdStr.append(schemeNameHex);
        //添加方案开始时间
        cmdStr.append(SmartBroadCastUtils.dateToHex(scheme.getSchemeStartDate()));
        //添加方案结束时间
        cmdStr.append(SmartBroadCastUtils.dateToHex(scheme.getSchemeEndDate()));
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //添加校验码
        cmdStr.append("" + SmartBroadCastUtils.checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");
        Log.i("msg", "getAddScheme: " + cmdStr);
        byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return result;
    }
}
