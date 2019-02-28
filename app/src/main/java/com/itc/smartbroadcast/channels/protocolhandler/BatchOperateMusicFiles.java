package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.OperateMusicFilesInfo;
import com.itc.smartbroadcast.bean.OperateMusicFilesResult;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.checkSum;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.chinese2Hex;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * author： lghandroid
 * created：2018/11/15 14:08
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: 批量复制，移动，删除到指定文件夹(乐库)里的音乐协议,多包发送
 */


public class BatchOperateMusicFiles {

    public final static int HEAD_PACKAGE_LENGTH = 28;                        //包头长度
    public final static int END_PACKAGE_LENGTH = 4;                          //包尾长度
    public final static int INIT_FOLDER_NAME_LENGTH = 32;                    //初始文件夹名称
    public final static int MUSIC_NAME_LENGTH = 64;                          //曲目名称
    public final static int TARGET_FOLDER_NAME_LENGTH = 32;                  //目标文件夹名称
    public final static int OPERATE_STATE_LENGTH = 1;                        //操作状态
    public final static int OPERATE_TYPE_LENGTH = 1;                         //操作类型
    public final static int PACKAGE_TOTAL_LENGTH = 1;                        //包总数
    public final static int PACKAGE_NUM_LENGTH = 1;                          //包序号
    public final static int MUSIC_TOTAL_LENGTH = 1;                          //歌曲总数


    public static BatchOperateMusicFiles init() {
        return new BatchOperateMusicFiles();
    }

    /**
     * 业务处理
     *
     * @param list 获取到的byte数据列表
     */
    public void handler(List<byte[]> list) {
        OperateMusicFilesResult operateMusicFilesResult = getBatchOperateMusicFilesResult(list.get(0));
        Gson gson = new Gson();
        String json = gson.toJson(operateMusicFilesResult);
        BaseBean bean = new BaseBean();
        bean.setType("BatchOperateMusicFilesResult");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        Log.i("jsonResult", "handler: " + jsonResult);
        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 批量操作音乐库文件状态结果
     *
     * @param bytes
     * @return
     */
    public OperateMusicFilesResult getBatchOperateMusicFilesResult(byte[] bytes) {
        //包头
        int head = HEAD_PACKAGE_LENGTH;
        //去掉包头，尾字节
        bytes = subBytes(bytes, head, bytes.length - (head + END_PACKAGE_LENGTH));
        OperateMusicFilesResult operateMusicFilesResult = new OperateMusicFilesResult();
        //操作类型
        operateMusicFilesResult.setOperatorType(byteToInt(subBytes(bytes, 0, OPERATE_TYPE_LENGTH)[0]));
        //操作状态
        operateMusicFilesResult.setResult(byteToInt(subBytes(bytes, OPERATE_TYPE_LENGTH, OPERATE_STATE_LENGTH)[0]));
        return operateMusicFilesResult;
    }


    /**
     * 发送操作音乐文件状态指令，包括复制，移动和删除
     *
     * @param host                  ip地址
     * @param operateMusicFilesInfo OperateMusicFilesInfo对象
     *                              00：复制， 01：移动， 02：删除）
     */
    public static void sendCMD(String host, OperateMusicFilesInfo operateMusicFilesInfo) {
        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                List<byte[]> bytes = SmartBroadCastUtils.CloudUtil(getBatchOPerateMusicFilesBytes(operateMusicFilesInfo), host, false);
                NettyTcpClient.getInstance().sendPackages(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackages(host, getBatchOPerateMusicFilesBytes(operateMusicFilesInfo));
        }
    }


    /**
     * 获取批量操作音乐文件字节串
     *
     * @param operateMusicFilesInfo
     * @return
     */
    private static List<byte[]> getBatchOPerateMusicFilesBytes(OperateMusicFilesInfo operateMusicFilesInfo) {
        //单包最多可发送曲目数，超过此数量需分包
        int packageMusicNum = (1024 - (HEAD_PACKAGE_LENGTH + END_PACKAGE_LENGTH + PACKAGE_TOTAL_LENGTH + PACKAGE_NUM_LENGTH + OPERATE_TYPE_LENGTH + INIT_FOLDER_NAME_LENGTH
                + TARGET_FOLDER_NAME_LENGTH + MUSIC_TOTAL_LENGTH)) / MUSIC_NAME_LENGTH;
        //客户端需要处理的曲目总数
        int musicTotal = operateMusicFilesInfo.getMusicTotal();
        List<byte[]> resultList = new ArrayList<>();
        //截取每个包曲目名称集合
        ArrayList<String> musicNameList = operateMusicFilesInfo.getMusicNameList();
        if (musicTotal > packageMusicNum) {
            //包总数
            int packageSize = 0;
            //包序号
            int packageIndex = 0;
            //余数
            int remainder = musicTotal % packageMusicNum;
            if (remainder > 0) {
                packageSize = (musicTotal / packageMusicNum) + 1;
            } else {
                packageSize = (musicTotal / packageMusicNum);
            }
            for (int i = 0; i < packageSize; i++) {
                //多包
                StringBuffer cmdStr = new StringBuffer();
                //添加起始标志
                cmdStr.append("AA55");
                //添加长度
                cmdStr.append("0000");
                //添加命令
                cmdStr.append("06B8");
                //添加本机Mac
                String mac = DeviceUtils.getMacAddress();
                cmdStr.append(mac.replace(":", ""));
                //添加控制ID
                cmdStr.append("000000000000");
                //添加云转发指令
                cmdStr.append("00");
                //保留字段
                cmdStr.append("000000000000000000");
                //添加总包数
                cmdStr.append(String.valueOf(packageSize).length() == 1 ? "0" + packageSize : packageSize);
                //添加当前包序号
                cmdStr.append(String.valueOf(packageIndex).length() == 1 ? "0" + packageIndex : packageIndex);
                //文件操作符
                String operator = operateMusicFilesInfo.getOperator();
                cmdStr.append(operator);
                if (!"03".equals(operator)) {
                    //初始文件夹名称
                    cmdStr.append(chinese2Hex(operateMusicFilesInfo.getInitFolderName(), INIT_FOLDER_NAME_LENGTH));
                    //目标文件夹名称
                    if (!"02".equals(operateMusicFilesInfo.getOperator()))
                        //复制、移动操作需传递有效的目标文件夹
                        cmdStr.append(chinese2Hex(operateMusicFilesInfo.getTargetFolderName(), TARGET_FOLDER_NAME_LENGTH));
                    else
                        //删除操作无需传递目标文件夹名称，该字段目的是填充字节数
                        cmdStr.append(chinese2Hex("目标文件夹名称", TARGET_FOLDER_NAME_LENGTH));
                    //歌曲总数
                    String musicNameListStr = getMusicNameListHex(musicNameList, i, packageMusicNum);

                    String total = Integer.toHexString(musicNameListStr.length() / (MUSIC_NAME_LENGTH * 2));
                    cmdStr.append(total.length() == 1 ? "0" + total : total);
                    cmdStr.append(musicNameListStr);
                }
                //修改长度
                cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
                //校验值
                cmdStr.append(checkSum(cmdStr.substring(4)));
                //添加结束标志
                cmdStr.append("55AA");
                packageIndex++;
                byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
                resultList.add(result);
            }
        } else {
            //单包
            StringBuffer cmdStr = new StringBuffer();
            //添加起始标志
            cmdStr.append("AA55");
            //添加长度
            cmdStr.append("0000");
            //添加命令
            cmdStr.append("06B8");
            //添加本机Mac
            String mac = DeviceUtils.getMacAddress();
            cmdStr.append(mac.replace(":", ""));
            //添加控制ID
            cmdStr.append("000000000000");
            //添加云转发指令
            cmdStr.append("00");
            //保留字段
            cmdStr.append("000000000000000000");
            //添加总包数
            cmdStr.append("01");
            //添加当前包序号
            cmdStr.append("00");
            //文件操作符
            String operator = operateMusicFilesInfo.getOperator();
            cmdStr.append(operator);
            if (!"03".equals(operator)) {
                //初始文件夹名称
                cmdStr.append(chinese2Hex(operateMusicFilesInfo.getInitFolderName(), INIT_FOLDER_NAME_LENGTH));
                //目标文件夹名称
                if (!"02".equals(operateMusicFilesInfo.getOperator()))
                    //复制、移动操作需传递有效的目标文件夹
                    cmdStr.append(chinese2Hex(operateMusicFilesInfo.getTargetFolderName(), TARGET_FOLDER_NAME_LENGTH));
                else
                    //删除操作无需传递目标文件夹名称，该字段目的是填充字节数
                    cmdStr.append(chinese2Hex("目标文件夹名称", TARGET_FOLDER_NAME_LENGTH));
                //歌曲总数
                String total = Integer.toHexString(operateMusicFilesInfo.getMusicTotal());
                cmdStr.append(total.length() == 1 ? "0" + total : total);
                //曲目名称
                for (String musicName : musicNameList) {
                    cmdStr.append(chinese2Hex(musicName.trim(), MUSIC_NAME_LENGTH));
                }
            }
            //修改长度
            cmdStr.replace(4, 8, SmartBroadCastUtils.intToUint16Hex((cmdStr.substring(4).length() + 4) / 2));
            //校验值
            cmdStr.append(checkSum(cmdStr.substring(4)));
            //添加结束标志
            cmdStr.append("55AA");
            byte[] result = SmartBroadCastUtils.HexStringtoBytes(cmdStr.toString());
            resultList.add(result);
        }
        return resultList;
    }


    public static String getMusicNameListHex(ArrayList<String> list, int packetIndex, int packageMusicNum) {
        String result = "";
        for (int i = packetIndex * packageMusicNum; i < list.size(); i++) {
            if (i < ((packetIndex + 1) * (packageMusicNum))) {
                result += SmartBroadCastUtils.chinese2Hex(list.get(i).trim(), MUSIC_NAME_LENGTH);
            }
        }
        return result;
    }


}
