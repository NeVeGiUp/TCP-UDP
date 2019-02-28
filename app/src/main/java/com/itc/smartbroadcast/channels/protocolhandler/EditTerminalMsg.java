package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditTerminalResult;
import com.itc.smartbroadcast.bean.TerminalDetailInfo;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.checkSum;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.chinese2Hex;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.conver2HexStr;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.ipToHex;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * author： lghandroid
 * created：2018/8/27 14:04
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: 向定时器或终端设备配置设备信息
 */


public class EditTerminalMsg {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int CONFIGURE_STATE_LENGTH = 1;                      //配置状态
    public final static int TERMINAL_NAME_LENGTH = 32;                       //设备名称


    public static EditTerminalMsg init() {
        return new EditTerminalMsg();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {
        EditTerminalResult setResult = getPartitionResult(list.get(0));
        Gson gson = new Gson();
        String json = gson.toJson(setResult);
        BaseBean bean = new BaseBean();
        bean.setType("editTerminalResult");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        Log.i("jsonResult", "handler: " + jsonResult);
        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 获取终端设备配置状态
     *
     * @param bytes
     * @return
     */
    public EditTerminalResult getPartitionResult(byte[] bytes) {
        //包头
        int head = HEAD_PACKAGE_LENGTH;
        //去掉包头，尾字节
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        EditTerminalResult result = new EditTerminalResult();
        int[] resultArr = getBitArray(subBytes(bytes, 0, CONFIGURE_STATE_LENGTH));
        result.setResult(resultArr);
        return result;
    }


    /**
     * 终端设备信息配置
     *
     * @param host       ip地址
     * @param detailInfo TerminalDetailInfo对象
     */
    public static void sendCMD(String host, TerminalDetailInfo detailInfo,boolean isDelete) {
        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getEditDeviceMsg(detailInfo,isDelete), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host, getEditDeviceMsg(detailInfo,isDelete));
        }
    }


    /**
     * 获取编辑终端信息的byte流
     *
     * @param detailInfo
     * @return
     */
    public static byte[] getEditDeviceMsg(TerminalDetailInfo detailInfo, boolean isDelete) {
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("04B1");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //配置位段标示符
        if (isDelete) {
            cmdStr.append("40");
        } else {
            cmdStr.append("7F");
        }
        //目标终端MAC
        cmdStr.append(detailInfo.getTerminalMac().replace("-", ""));
        //设备名称
        cmdStr.append(chinese2Hex(detailInfo.getTerminalName(), TERMINAL_NAME_LENGTH));
        //IP获取方式 "00"代表手动获取  “01”代表自动获取
        cmdStr.append("0" + String.valueOf(detailInfo.getTerminalIpMode()));
        //IP地址
        cmdStr.append(ipToHex(detailInfo.getTerminalIp()));
        //IP掩码
        cmdStr.append(ipToHex(detailInfo.getTerminalSubnet()));
        //IP网关
        cmdStr.append(ipToHex(detailInfo.getTerminalGateway()));
        //音源类型
        cmdStr.append(detailInfo.getTerminalSoundCate());
        //设备优先级默音权限  "00"-普通级别(能被默音)  "80"-高优先级别（不能被默音）
        cmdStr.append(detailInfo.getTerminalPriority());
        //默音音量
        String defVolume = Integer.toHexString(detailInfo.getTerminalDefVolume());
        cmdStr.append(defVolume.length() == 1 ? "0" + defVolume : defVolume);
        //设备默认音量 1-100
        String setVolume = Integer.toHexString(detailInfo.getTerminalSetVolume());
        cmdStr.append(setVolume.length() == 1 ? "0" + setVolume : setVolume);
        //旧密码
        cmdStr.append(chinese2Hex(detailInfo.getTerminalOldPsw(), 14));
        //新密码
        cmdStr.append(chinese2Hex(detailInfo.getTerminalNewPsw(), 14));
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //校验值
        cmdStr.append(checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");
        Log.i("editTerminalMsg", "editTerminalMsg: " + cmdStr);
        byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return result;
    }

    /**
     * 获取bit有效位数 数据都为1和0
     *
     * @param b
     * @return
     */
    public int[] getBitArray(byte[] b) {
        String binaryStr = conver2HexStr(b[0]);
        int[] binaryArr = new int[binaryStr.length()];
        int index = 0;
        for (int j = binaryArr.length - 1; j >= 0; j--) {
            binaryArr[index] = Integer.parseInt(String.valueOf(binaryStr.charAt(j)));
            index++;
        }
        return binaryArr;
    }
}
