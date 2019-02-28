package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditMusicFolderNameInfo;
import com.itc.smartbroadcast.bean.EditMusicFolderNameResult;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.checkSum;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.chinese2Hex;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * author： lghandroid
 * created：2018/10/9 15:10
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: 修改指定文件夹名称，包括添加，删除，修改文件夹
 */


public class EditMusicFolderName {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int FOLDER_NAME_LENGTH = 32;                         //文件夹名称
    public final static int UPDATE_FOLDER_NAME_LENGTH = 32;                  //修改后的文件夹名称
    public final static int CONFIGURE_STATE_LENGTH = 1;                      //配置状态


    public static EditMusicFolderName init() {
        return new EditMusicFolderName();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {
        EditMusicFolderNameResult editfolderNameResult = getEditMusicFolderNameResult(list.get(0));
        Gson gson = new Gson();
        String json = gson.toJson(editfolderNameResult);
        BaseBean bean = new BaseBean();
        bean.setType("EditMusicFolderNameResult");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        Log.i("jsonResult", "handler: " + jsonResult);
        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 添加，编辑，删除音乐文件夹名称
     *
     * @param bytes
     * @return
     */
    public EditMusicFolderNameResult getEditMusicFolderNameResult(byte[] bytes) {
        //包头
        int head = HEAD_PACKAGE_LENGTH;
        //去掉包头，尾字节
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        EditMusicFolderNameResult nameResult = new EditMusicFolderNameResult();
        nameResult.setResult(byteToInt(subBytes(bytes, 0, CONFIGURE_STATE_LENGTH)[0]));
        return nameResult;
    }


    /**
     * 修改音乐文件夹名称，配置
     *
     * @param host                ip地址
     * @param musicFolderNameInfo EditMusicFolderNameInfo对象
     *                            0：添加，1：删除，2：编辑）
     */
    public static void sendCMD(String host, EditMusicFolderNameInfo musicFolderNameInfo) {
        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getEditMusicFolderNameBytes(musicFolderNameInfo), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host, getEditMusicFolderNameBytes(musicFolderNameInfo));
        }
    }


    private static byte[] getEditMusicFolderNameBytes(EditMusicFolderNameInfo musicFolderNameInfo) {
        StringBuffer cmdStr = new StringBuffer();
        //添加起始标志
        cmdStr.append("AA55");
        //添加长度
        cmdStr.append("0000");
        //添加命令
        cmdStr.append("02B8");
        //添加本机Mac
        String mac = DeviceUtils.getMacAddress();
        cmdStr.append(mac.replace(":", ""));
        //添加控制ID
        cmdStr.append("000000000000");
        //添加云转发指令
        cmdStr.append("00");
        //保留字段
        cmdStr.append("000000000000000000");
        //操作符
        cmdStr.append(musicFolderNameInfo.getOperator());
        //文件夹名称
        cmdStr.append(chinese2Hex(musicFolderNameInfo.getFolderName(), FOLDER_NAME_LENGTH));
        //修改后的文件夹名称
        if ("02".equals(musicFolderNameInfo.getOperator()))
            //编辑操作需传递有效的修改后的文件夹名称
            cmdStr.append(chinese2Hex(musicFolderNameInfo.getUpdateFolderName(), UPDATE_FOLDER_NAME_LENGTH));
        else
            //添加、删除操作无需传递修改后的文件夹名称，该字段目的是填充字节数
            cmdStr.append(chinese2Hex("修改后的文件夹名称", UPDATE_FOLDER_NAME_LENGTH));
        //修改长度
        cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
        //校验值
        cmdStr.append(checkSum(cmdStr.substring(4)));
        //添加结束标志
        cmdStr.append("55AA");
        byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
        return result;
    }


}
