package com.itc.smartbroadcast.channels.protocolhandler;

import android.util.Log;

import com.google.gson.Gson;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.AccountListInfo;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.ConfigUtils;
import com.itc.smartbroadcast.util.DeviceUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToInt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.byteToStr;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.bytesToHexString;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.checkSum;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.decrypt;
import static com.itc.smartbroadcast.util.SmartBroadCastUtils.subBytes;

/**
 * author： lghandroid
 * created：2018/9/8 16:09
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels.protocolhandler
 * describe: _获取账户列表
 */

public class GetAccountList {

    private static final int HEAD_PACKET_LENGTH = 28;                         //包头
    private static final int END_PACKET_LENGTH = 4;                           //包尾
    private static final int ACCOUNT_TOTAL_LENGTH = 1;                        //用户总数
    private static final int ACCOUNT_TYPE_LENGTH = 1;                         //用户类型
    private static final int ACCOUNT_NUM_LENGTH = 1;                          //账户编号
    private static final int ACCOUNT_NAME_LENGTH = 32;                        //账户名称
    private static final int USER_PHONE_NUM_LENGTH = 32;                      //用户手机号
    private static final int ACCOUNT_PSW_LENGTH = 14;                         //账户密码
    private static final int LOGINED_TIME_LENGTH = 6;                         //登录时间
    private static final int ACCOUNT_CREATE_TIME_LENGTH = 6;                  //账户创建时间
    private static final int ACCOUNT_DEVICE_TOTAL_LENGTH = 1;                 //账户设备总数

    public static GetAccountList init() {
        return new GetAccountList();
    }


    /**
     * 业务处理
     *
     * @param list _获取账户列表
     */
    public void handler(List<byte[]> list) {
        List<AccountListInfo.AccountDataInner> accountListInfoList = new ArrayList<>();
        for (byte[] b : list) {
            //获取每个包里面的用户信息集合
            List<AccountListInfo.AccountDataInner> accountListInfo = getAccountList(b);
            //合并每个包的用户信息集合
            accountListInfoList = objectMerging(accountListInfoList, accountListInfo);
        }
        Gson gson = new Gson();
        String json = gson.toJson(accountListInfoList);
        BaseBean bean = new BaseBean();
        bean.setType("getAccountList");
        bean.setData(json);
        String jsonResult = gson.toJson(bean);
        Log.i("jsonResult", "handler: " + jsonResult);
        Log.i("jsonResult", "AccountTotal: " + accountListInfoList.size());
        EventBus.getDefault().post(jsonResult);
    }

    /**
     * 通过byte数组获取AccountListInfo对象
     *
     * @param bytes
     * @return AccountListInfo对象
     */
    public List<AccountListInfo.AccountDataInner> getAccountList(byte[] bytes) {
        List<AccountListInfo.AccountDataInner> accountDataList = new ArrayList<>();
        //获取有效字节数
        bytes = subBytes(bytes, HEAD_PACKET_LENGTH, bytes.length - END_PACKET_LENGTH - HEAD_PACKET_LENGTH);
        //用户总数
        int accountTotal = byteToInt(subBytes(bytes, 2, ACCOUNT_TOTAL_LENGTH)[0]);
        //一个账户信息item所占字节数
        int itemBt = ACCOUNT_TYPE_LENGTH + ACCOUNT_NUM_LENGTH + ACCOUNT_NAME_LENGTH + USER_PHONE_NUM_LENGTH + ACCOUNT_PSW_LENGTH +
                LOGINED_TIME_LENGTH + ACCOUNT_CREATE_TIME_LENGTH + ACCOUNT_DEVICE_TOTAL_LENGTH;
        byte[] accountDataListBt = SmartBroadCastUtils.subBytes(bytes, ACCOUNT_TOTAL_LENGTH + 2, itemBt * accountTotal);
        for (int i = 0; i < accountDataListBt.length; i += itemBt) {
            AccountListInfo.AccountDataInner accountData = new AccountListInfo.AccountDataInner();
            int index = 0;
            //用户类型
            String accountType = bytesToHexString(subBytes(accountDataListBt, i + index, ACCOUNT_TYPE_LENGTH));
            index += ACCOUNT_TYPE_LENGTH;
            //账户编号
            int accountNum = byteToInt(subBytes(accountDataListBt, i + index, ACCOUNT_NUM_LENGTH)[0]);
            index += ACCOUNT_NUM_LENGTH;
            //账户名称
            String accountName = decrypt(subBytes(accountDataListBt, i + index, ACCOUNT_NAME_LENGTH),ConfigUtils.USERNAME_SECRETKEY);
            index += ACCOUNT_NAME_LENGTH;
            //账户手机号
            String accountPhoneNum = byteToStr(subBytes(accountDataListBt, i + index, USER_PHONE_NUM_LENGTH));
            index += USER_PHONE_NUM_LENGTH;
            //账户密码
            String accountPsw = decrypt(subBytes(accountDataListBt, i + index, ACCOUNT_PSW_LENGTH),ConfigUtils.PASSWORD_SECRETKEY);
            index += ACCOUNT_PSW_LENGTH;
            //登录时间
            String loginedTime = byteToDates(subBytes(accountDataListBt, i + index, LOGINED_TIME_LENGTH));
            index += LOGINED_TIME_LENGTH;
            //账户创建时间
            String accountCreatTime = byteToDates(subBytes(accountDataListBt, i + index, ACCOUNT_CREATE_TIME_LENGTH));
            index += ACCOUNT_CREATE_TIME_LENGTH;
            //账户设备总数
            int deviceTotal = byteToInt(subBytes(accountDataListBt, i + index, ACCOUNT_DEVICE_TOTAL_LENGTH)[0]);
            accountData.setAccountType(accountType);
            accountData.setAccountNum(accountNum);
            accountData.setAccountName(accountName);
            accountData.setAccountPhoneNum(accountPhoneNum);
            accountData.setAccountPsw(accountPsw);
            accountData.setLoginedTime(loginedTime);
            accountData.setAccountCreateTime(accountCreatTime);
            accountData.setAccountDeviceTotal(deviceTotal);
            accountDataList.add(accountData);
        }
        return accountDataList;
    }


    private List<AccountListInfo.AccountDataInner> objectMerging
            (List<AccountListInfo.AccountDataInner> accountDataInnerList1, List<AccountListInfo.AccountDataInner> accountDataInnerList2) {

        List<AccountListInfo.AccountDataInner> accountDataInnerList = new ArrayList<>();
        for (AccountListInfo.AccountDataInner accountDataInfo : accountDataInnerList1) {
            accountDataInnerList.add(accountDataInfo);
        }
        for (AccountListInfo.AccountDataInner accountDataInfo : accountDataInnerList2) {
            accountDataInnerList.add(accountDataInfo);
        }
        return accountDataInnerList;

    }


    /**
     * 发送获取账户列表指令包
     */
    public static void sendCMD(String host) {

        //判断是否为云协议
        if (SmartBroadcastApplication.isCloud) {
            //判断TCP连接是否连接上
            if (NettyTcpClient.isConnSucc) {
                byte[] bytes = SmartBroadCastUtils.CloudUtil(getAccountListCmd(), host, false);
                NettyTcpClient.getInstance().sendPackage(host, bytes);
            }
        } else {
            NettyUdpClient.getInstance().sendPackage(host, getAccountListCmd());
        }


    }


    /**
     * 获取账户列表发送指令
     *
     * @return
     */
    public static byte[] getAccountListCmd() {
        StringBuffer cmdStr = new StringBuffer();
        //起始标志
        cmdStr.append("AA55");
        //长度
        cmdStr.append("0000");
        //命令
        cmdStr.append("01B9");
        //本机mac
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
        String result = year + "-" + (month >= 10 ? month : "0" + month) + "-" + (day >= 10 ? day : "0" + day) + " " + (hour >= 10 ? hour : "0" + hour) + ":" +
                (minute >= 10 ? minute : "0" + minute) + ":" + (second >= 10 ? second : "0" + second);
        return result;
    }

}
