package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.ControlPanelInfo;
import com.itc.smartbroadcast.bean.EditControlPanelResult;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.checkSum;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.ipToHex;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * author： lghandroid
 * created：2018/12/21 14:04
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: 配置控制面板绑定设备协议
 */


public class EditControlPanel {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int CONFIGURE_STATE_LENGTH = 1;                      //配置状态


    public static EditControlPanel init() {
        return new EditControlPanel();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {
        EditControlPanelResult result = getControlPanelResult(list.get(0));
        Gson gson = new Gson();
        String json = gson.toJson(result);
        BaseBean bean = new BaseBean();
        bean.setType("editControlPanelResult");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        Log.i("jsonResult", "handler: " + jsonResult);
        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 获取配置控制面板设备状态
     *
     * @param bytes
     * @return
     */
    public EditControlPanelResult getControlPanelResult(byte[] bytes) {
        //包头
        int head = HEAD_PACKAGE_LENGTH;
        //去掉包头，尾字节
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        EditControlPanelResult result = new EditControlPanelResult();
        int resultStatus = byteToInt(subBytes(bytes, 0, CONFIGURE_STATE_LENGTH)[0]);
        result.setResult(resultStatus);
        return result;
    }


    /**
     * 终端设备信息配置
     *
     * @param host       ip地址
     * @param detailInfo TerminalDetailInfo对象
     */
    public static void sendCMD(String host, ControlPanelInfo detailInfo) {
        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getEditDeviceMsg(detailInfo), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host, getEditDeviceMsg(detailInfo));
        }
    }


    /**
     * 获取编辑终端信息的byte流
     *
     * @param detailInfo
     * @return
     */
    public static byte[] getEditDeviceMsg(ControlPanelInfo detailInfo) {
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("09B1");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //绑定设备总数
        String hexDeviceCount = Integer.toHexString(detailInfo.getBingDeviceCount());
        cmdStr.append(hexDeviceCount.length() == 1 ? "0" + hexDeviceCount : hexDeviceCount);
        //设备信息 * 总数
        for (ControlPanelInfo.DeviceMsgInner deviceMsgInner : detailInfo.getDeviceMsgList()) {
            //目标终端MAC
            cmdStr.append(deviceMsgInner.getBindDeviceMac().replace("-", ""));
        }
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //校验值
        cmdStr.append(checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");
        Log.i("editControlPanel", "editControlPanel: " + cmdStr);
        byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return result;
    }


}
