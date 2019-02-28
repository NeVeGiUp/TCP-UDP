package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.TerminalDetailInfo;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.bytesToHexString;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.checkSum;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.hexstrToIp;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.hexstrToMac;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * author： lghandroid
 * created：2018/8/21 17:28
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: _获取终端详情
 */

public class GetTerminalDetail {

    private static final int MAC_LENGTH = 6;                              //设备mac
    private static final int IP_MODE_LENGTH = 1;                          //IP获取模式
    private static final int SUBNET_LENGTH = 4;                           //子网掩码
    private static final int GATEWAY_LENGTH = 4;                          //网关
    private static final int SOUND_CATE_LENGTH = 1;                       //音源类别
    private static final int PRIORITY_LENGTH = 1;                         //设备优先级
    private static final int DEF_VOLUME_LENGTH = 1;                       //默认音量
    private static final int HEAD_PACKET_LENGTH = 28;                     //包头
    private static final int END_PACKET_LENGTH = 4;                       //包尾

    public static GetTerminalDetail init() {
        return new GetTerminalDetail();
    }


    /**
     * 业务处理
     *
     * @param list 获取取终端详情信息
     */
    public void handler(List<byte[]> list) {
        List<TerminalDetailInfo> schemeList = getTerminalDetail(list.get(0));
        Gson gson = new Gson();
        String json = gson.toJson(schemeList);
        BaseBean bean = new BaseBean();
        bean.setType("getTerminalDetail");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        Log.i("jsonResult", "handler: " + jsonResult);
        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 通过byte数组获取TerminalDetail对象
     *
     * @param bytes
     * @return Scheme对象
     */
    public List<TerminalDetailInfo> getTerminalDetail(byte[] bytes) {
        List<TerminalDetailInfo> list = new ArrayList<>();
        int index = 0;
        //获取详情有效字节数
        bytes = subBytes(bytes, HEAD_PACKET_LENGTH, bytes.length - END_PACKET_LENGTH - HEAD_PACKET_LENGTH);
        TerminalDetailInfo detailInfo = new TerminalDetailInfo();
        //设备mac
        String mac = hexstrToMac(bytesToHexString(subBytes(bytes, 0, MAC_LENGTH)));
        //IP获取模式
        int ipMode = byteToInt(subBytes(bytes, MAC_LENGTH, IP_MODE_LENGTH)[0]);
        index += MAC_LENGTH;
        //子网掩码
        String subnet = hexstrToIp(bytesToHexString(subBytes(bytes, index + IP_MODE_LENGTH, SUBNET_LENGTH)));
        index += IP_MODE_LENGTH;
        //网关
        String gateway = hexstrToIp(bytesToHexString(subBytes(bytes, index + SUBNET_LENGTH, GATEWAY_LENGTH)));
        index += SUBNET_LENGTH;
        //音源类别
        String soundCate = bytesToHexString(subBytes(bytes, index + GATEWAY_LENGTH, SOUND_CATE_LENGTH));
        index += GATEWAY_LENGTH;
        //设备优先级
        String priority = bytesToHexString(subBytes(bytes, index + SOUND_CATE_LENGTH, PRIORITY_LENGTH));
        index += SOUND_CATE_LENGTH;
        //默认音量
        String defVolume = bytesToHexString(subBytes(bytes, index + PRIORITY_LENGTH, DEF_VOLUME_LENGTH));
        if ("00".equals(priority)) {
            detailInfo.setTerminalPriority("00");
        } else if ("80".equals(priority)) {
            detailInfo.setTerminalPriority("80");
        }
        detailInfo.setTerminalSoundCate(soundCate);
        detailInfo.setTerminalIpMode(ipMode);
        detailInfo.setTerminalMac(mac);
        detailInfo.setTerminalSubnet(subnet);
        detailInfo.setTerminalGateway(gateway);
        detailInfo.setTerminalDefVolume(Integer.parseInt(defVolume,16));
        list.add(detailInfo);
        return list;
    }

    /**
     * 发送获取终端详情信息,host需换成指定终端的ip
     */
    public static void sendCMD(String host) {
        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil( getTerminalDetail(), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host, getTerminalDetail());
        }
    }


    /**
     * 获取终端详情信息
     *
     * @return
     */
    public static byte[] getTerminalDetail() {
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("02B1");
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
        //结束标志
        cmdStr.append("55AA");
        byte[] bytes = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return bytes;
    }


}
