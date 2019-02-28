package com.itc.smartbroadcast.util;

import android.text.TextUtils;

import com.itc.smartbroadcast.application.SmartBroadcastApplication;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * author： lghandroid
 * created：2018/8/3 16:55
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.util
 * describe: 16进制与字符串之间转换工具类
 */

public class SmartBroadCastUtils {

    /**
     * 16进制转换成为string类型字符串
     *
     * @param s
     * @return
     */
    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "UTF-16LE");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }


    /**
     * 二进制数据转化为16进制字符串（中间加的‘：’还有‘；’是为了查看下标，也可以自行去掉）：
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        String hv = "";
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    /**
     * 将16进制字符串转换为byte[]
     *
     * @param str 十六进制数据源
     * @return 转换后的byte[]
     */
    public static byte[] HexStringtoBytes(String str) {
        if (str == null || str.trim().equals("")) {
            return new byte[0];
        }
        str = str.replace(" ", "");
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length() / 2; i++) {
            String subStr = str.substring(i * 2, i * 2 + 2);
            bytes[i] = (byte) Integer.parseInt(subStr, 16);
        }

        return bytes;
    }


    /**
     * 截取byte数组
     *
     * @param src   byte[]数据源
     * @param begin 开始索引
     * @param count 数量
     * @return 截取后的byte[]
     */
    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;

    }


    /**
     * 字符串转换成为16进制(无需Unicode编码)
     *
     * @param str
     * @return
     */
    public static String str2HexStr(String str) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        //返回指定的字符集CharSet
        Charset charset = Charset.forName("UTF-16LE");
        byte[] bs = str.getBytes(charset);
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            // sb.append(' ');
        }

        return sb.toString();
    }


    /**
     * 16进制直接转换成为字符串(无需Unicode解码)
     *
     * @param hexStr
     * @return
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;
        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        return new String(bytes);
    }


    /**
     * byte字节直接转成字符串
     *
     * @param bytes
     * @return
     */
    public static String byteToStr(byte[] bytes) {

        String strdata = null;
        try {
            strdata = new String(bytes, "UTF-16LE");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        strdata = strdata.replace("\u0000", ""); // \U0000去掉多余的十六进制00
        return strdata;
    }


    /**
     * byte字节直接转成字符串
     *
     * @param bytes
     * @return
     */
    public static String byteToStrUTF16BE(byte[] bytes) {
        String strdata = null;
        try {
            strdata = new String(bytes, "UTF-16BE");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        strdata = strdata.replace("\u0000", ""); // \U0000去掉多余的十六进制00
        return strdata;
    }


    /**
     * 十六进制字符串转十进制IP
     *
     * @param hexstr
     * @return
     */
    public static String hexstrToIp(String hexstr) {
        if (TextUtils.isEmpty(hexstr) || hexstr.length() != 8)
            return "";

        String strs = "";
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < hexstr.length() + 1; i += 2) {
            if (i < hexstr.length()) {
                strs = hexstr.substring(i, i + 2);
                int parseInt = Integer.parseInt(strs, 16);
                if (i < hexstr.length() - 2) {
                    stringBuffer.append(parseInt).append(".");
                } else {
                    stringBuffer.append(parseInt);
                }
            }

        }
        return stringBuffer.toString();
    }


    /**
     * byteArray转int
     */
    public static int byteArrayToInt(byte[] des) {
        //byte转int
        int value = 0;
        for (int i = 0; i < des.length; i++) {
            if (i == 0) {
                value = value | ((des[i] & 0xff));
            } else {
                value = value | ((des[i] & 0xff) << (int) Math.pow(8, i));
            }
        }
        return value;
    }


    /**
     * int转uint16Hex
     */
    public static String intToUint16Hex(int date) {
        String str = Integer.toHexString(date);
        int len = (4 - str.length());
        for (int i = 0; i < len; i++) {
            str = "0" + str;
        }
        String result = str.substring(2, 4) + str.substring(0, 2);
        return result;
    }


    /**
     * int转uint16Hex
     *
     * @param date
     * @return
     */
    public static String intToUint8Hex(int date) {
        String str = Integer.toHexString(date);
        return str.length() == 1 ? "0" + str : str;
    }


    /**
     * byte转int
     *
     * @param des
     * @return
     */
    public static int byteToInt(byte des) {
        return des & 0xFF;
    }


    /**
     * byte转换为二进制字符串,每个字节以" "隔开
     **/
    public static String conver2HexStr(byte b) {
        String result = Integer.toBinaryString(byteToInt(b));
        for (int i = result.length(); i < 8; i++) {
            result = "0" + result;
        }
        return result;
    }


    /**
     * 十六进制字符串转版本信息
     *
     * @param hexVersion
     * @return
     */
    public static String hexToVersion(String hexVersion) {
        if (TextUtils.isEmpty(hexVersion) || hexVersion.length() != 4)
            return "";
        String strs = "";
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < hexVersion.length() + 1; i += 2) {
            if (i < hexVersion.length()) {
                strs = hexVersion.substring(i, i + 2);
                int parseInt = Integer.parseInt(strs, 16);
                if (i == 0) {
                    stringBuffer.append("v").append(parseInt).append(".");
                } else {
                    if (parseInt < 10) {
                        stringBuffer.append("0").append(parseInt);
                    } else {
                        stringBuffer.append(parseInt);
                    }
                }
            }
        }
        return stringBuffer.toString();
    }


    /**
     * 将byte转为时间
     *
     * @param b
     * @return 日期YYYY-MM-DD
     */
    public static String byteToTime(byte[] b) {
        int h = SmartBroadCastUtils.byteToInt(b[0]);
        int m = SmartBroadCastUtils.byteToInt(b[1]);
        int s = SmartBroadCastUtils.byteToInt(b[2]);
        String result = (h >= 10 ? h : "0" + h) + ":" + (m >= 10 ? m : "0" + m) + ":" + (s >= 10 ? s : "0" + s);
        return result;
    }


    /**
     * 将byte转为秒数
     *
     * @param b
     * @return
     */
    public static int byteToContinue(byte[] b) {
        int h = SmartBroadCastUtils.byteToInt(b[0]);
        int m = SmartBroadCastUtils.byteToInt(b[1]);
        int s = SmartBroadCastUtils.byteToInt(b[2]);
        return h * 60 * 60 + m * 60 + s;
    }


    /**
     * 将time转Hex
     *
     * @param date
     * @return 日期YYYY-MM-DD
     */
    public static String timeToHex(String date) {
        String[] dates = date.split(":");
        int h = Integer.parseInt(dates[0]);
        int m = Integer.parseInt(dates[1]);
        int s = Integer.parseInt(dates[2]);
        String yearStr = Integer.toHexString(h);
        String monthStr = Integer.toHexString(m);
        String dayStr = Integer.toHexString(s);
        String result = (yearStr.length() == 1 ? "0" + yearStr : yearStr) + (monthStr.length() == 1 ? "0" + monthStr : monthStr) + (dayStr.length() == 1 ? "0" + dayStr : dayStr);
        return result;
    }


    /**
     * 将time转Hex
     *
     * @param continueTime
     * @return 日期YYYY-MM-DD
     */
    public static String continueToHex(int continueTime) {
        int h = continueTime / (60 * 60);
        int m = (continueTime / 60) % 60;
        int s = continueTime % 60;
        String yearStr = Integer.toHexString(h);
        String monthStr = Integer.toHexString(m);
        String dayStr = Integer.toHexString(s);
        String result = (yearStr.length() == 1 ? "0" + yearStr : yearStr) + (monthStr.length() == 1 ? "0" + monthStr : monthStr) + (dayStr.length() == 1 ? "0" + dayStr : dayStr);
        return result;
    }


    /**
     * 将byte转为时间
     *
     * @param b
     * @return 日期YYYY-MM-DD
     */
    public static String byteToDate(byte[] b) {
        int year = 2000 + SmartBroadCastUtils.byteToInt(b[0]);
        int month = SmartBroadCastUtils.byteToInt(b[1]);
        int day = SmartBroadCastUtils.byteToInt(b[2]);
        String result = year + "-" + (month >= 10 ? month : "0" + month) + "-" + (day >= 10 ? day : "0" + day);
        return result;
    }


    /**
     * 将时间转为Hex
     *
     * @param date YYYY-MM-DD
     * @return Hex
     */
    public static String dateToHex(String date) {

        String[] dates = date.split("-");
        int year = Integer.parseInt(dates[0]) - 2000;
        int month = Integer.parseInt(dates[1]);
        int day = Integer.parseInt(dates[2]);
        String yearStr = Integer.toHexString(year);
        String monthStr = Integer.toHexString(month);
        String dayStr = Integer.toHexString(day);
        String result = (yearStr.length() == 1 ? "0" + yearStr : yearStr) + (monthStr.length() == 1 ? "0" + monthStr : monthStr) + (dayStr.length() == 1 ? "0" + dayStr : dayStr);
        return result;
    }


    /**
     * 获取Mac地址
     *
     * @param bytes 6个字节的byte流
     * @return Hex
     */
    public static String getMacAddress(byte[] bytes) {
        String data = bytesToHexString(bytes);
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < data.length(); i += 2) {
            if (i < (data.length() - 2)) {
                result.append(data.substring(i, i + 2));
                result.append("-");
            } else {
                result.append(data.substring(i, i + 2));
            }
        }
        return result.toString();
    }


    /**
     * 获取校验码
     *
     * @param data
     * @return
     */
    public static String checkSum(String data) {
        byte[] bytes = SmartBroadCastUtils.HexStringtoBytes(data);
        int sum = 0;
        for (int i = 0; i < bytes.length; i++) {
            sum += SmartBroadCastUtils.byteToInt(bytes[i]);
        }
        sum = (sum & 0xFFFF) + (sum >> 16);
        if (sum > 0xFFFF) {
            sum = (sum & 0xFFFF) + (sum >> 16);
        }
        int d = (int) sum;
        int result = (int) ~d;
        String resultStr = Integer.toHexString(result).substring(Integer.toHexString(result).length() - 4, Integer.toHexString(result).length());
        resultStr = resultStr.substring(2, 4) + resultStr.substring(0, 2);
        return resultStr;
    }


    /**
     * 中文转十六进制
     *
     * @param chinese   中文
     * @param byteCount 该中文所占字节数
     * @return
     */
    public static String chinese2Hex(String chinese, int byteCount) {
        String hexChinese = SmartBroadCastUtils.str2HexStr(chinese);
        StringBuffer buffer = new StringBuffer(hexChinese);

        int count = byteCount * 2;
        if (hexChinese.length() < count) {
            for (int i = 0; i < count - hexChinese.length(); i++) {
                buffer = buffer.append("0");
            }
            return buffer.toString();

        } else if (hexChinese.length() == count) {
            return hexChinese;
        }
        return "";
    }


    /**
     * 中文转十六进制
     *
     * @param hexChinese 中文
     * @param byteCount  该中文所占字节数
     * @return
     */
    public static String hexFormat(String hexChinese, int byteCount) {

        StringBuffer buffer = new StringBuffer(hexChinese);

        int count = byteCount * 2;
        if (hexChinese.length() < count) {
            for (int i = 0; i < count - hexChinese.length(); i++) {
                buffer = buffer.append("0");
            }
            return buffer.toString();

        } else if (hexChinese.length() == count) {
            return hexChinese;
        }
        return "";
    }


    /**
     * 十六进制字符串转MAC地址
     *
     * @param hexstr
     * @return
     */
    public static String hexstrToMac(String hexstr) {
        if (TextUtils.isEmpty(hexstr) || hexstr.length() != 12)
            return "";
        String strs = "";
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < hexstr.length() + 1; i += 2) {
            if (i < hexstr.length()) {
                strs = hexstr.substring(i, i + 2);
                if (i < hexstr.length() - 2) {
                    stringBuffer.append(strs).append("-");
                } else {
                    stringBuffer.append(strs);
                }
            }
        }
        return stringBuffer.toString();
    }


    /**
     * ip地址转化为十六进制
     *
     * @param ipAddr
     * @return
     */
    public static String ipToHex(String ipAddr) {
        StringBuffer buffer = new StringBuffer();
        String[] split = ipAddr.trim().split("\\.");
        for (String s : split) {
            String hexString = Integer.toHexString(Integer.parseInt(s));
            if (hexString.length() == 1) {
                buffer.append("0").append(hexString);
            } else {
                buffer.append(hexString);
            }
        }
        return buffer.toString();
    }


    //获取MAC
    public static String getMac() {
        String macSerial = "";
        String str = "";
        try {
            Process pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ");
            InputStreamReader ir = new InputStreamReader(pp.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);

            for (; null != str; ) {
                str = input.readLine();
                if (str != null) {
                    macSerial = str.trim().replaceAll(":", "");// 去空格
                    break;
                }
            }
        } catch (IOException ex) {
            // 赋予默认值
            ex.printStackTrace();
        }
        return macSerial;
    }


    /**
     * 将秒数转化为时分秒
     *
     * @param time 时间段对应的秒数
     * @return 时分秒格式时间戳
     */
    public static String secToTime(int time) {
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0) {
            return "00:00:00";
        } else {
            hour = (time / (60 * 60)) % 24;
            minute = (time / 60) % 60;
            second = time % 60;
            return timeFormat(hour) + ":" + timeFormat(minute) + ":" + timeFormat(second);
        }
    }


    /**
     * 时间格式化
     *
     * @param num
     * @return
     */
    public static String timeFormat(int num) {
        String retStr = null;
        if (num >= 0 && num < 10) {
            retStr = "0" + Integer.toString(num);
        } else {
            retStr = "" + num;
        }
        return retStr;
    }


    /**
     * 云协议工具类
     *
     * @param bytes   原协议数据（UDP协议数据）
     * @param host    设备ip
     * @param isOrder 是否为指令（是为指令，否为透传）
     * @return 封装完的TCP透传协议数据
     */
    public static byte[] CloudUtil(byte[] bytes, String host, boolean isOrder) {
        String data = bytesToHexString(bytes);
        StringBuffer sendData = new StringBuffer();
        sendData.append(data);
        //替换云指令
        sendData.replace(36, 38, "01");
        //重新校验
        String hexCheck = sendData.substring(4, sendData.length() - 8);
        String checkNum = checkSum(hexCheck);
        sendData.replace(sendData.length() - 8, sendData.length() - 4, checkNum);

        StringBuffer cloudHeader = new StringBuffer();
        //云服务标识（itcy）
        cloudHeader.append("79637469");
        //数据长度
        int dateL = (sendData.length() / 2) - 2;
        int cloudHeaderL = 28;
        String lengthHex = SmartBroadCastUtils.intToUint16Hex(dateL + cloudHeaderL);
        cloudHeader.append(lengthHex);
        //子系统目标IP
        String[] ipSrc = host.split("\\.");
        cloudHeader.append(intToUint8Hex(Integer.parseInt(ipSrc[0])));
        cloudHeader.append(intToUint8Hex(Integer.parseInt(ipSrc[1])));
        cloudHeader.append(intToUint8Hex(Integer.parseInt(ipSrc[2])));
        cloudHeader.append(intToUint8Hex(Integer.parseInt(ipSrc[3])));
        //子系统目标mac
        cloudHeader.append(SmartBroadcastApplication.cloudMacAddress);
        //控制ID,本地Mac地址
        String mac = DeviceUtils.getMacAddress();
        String cloudMacAddress = mac.replace(":", "");
        cloudHeader.append(cloudMacAddress);
        //协议类型，0：穿透指令，1：指令
        if (isOrder) {
            cloudHeader.append("01");
        } else {
            cloudHeader.append("00");
        }
        //设备标识，0：主机设备，1：手机
        cloudHeader.append("01");
        //保留字段
        cloudHeader.append("FFFFFFFFFFFFFFFF");
        cloudHeader.append(sendData);
        byte[] result = SmartBroadCastUtils.HexStringtoBytes(cloudHeader.toString());
        return result;
    }

    /**
     * 云协议工具类
     *
     * @param byteList 原协议数据（UDP协议数据）
     * @param host     设备ip
     * @param isOrder  是否为指令（是为指令，否为透传）
     * @return 封装完的TCP透传协议数据
     */
    public static List<byte[]> CloudUtil(List<byte[]> byteList, String host, boolean isOrder) {
        List<byte[]> resultList = new ArrayList<>();
        for (byte[] bytes : byteList) {
            String data = bytesToHexString(bytes);
            StringBuffer sendData = new StringBuffer();
            sendData.append(data);
            //替换云指令
            sendData.replace(36, 38, "01");
            //重新校验
            String hexCheck = sendData.substring(4, sendData.length() - 8);
            String checkNum = checkSum(hexCheck);
            sendData.replace(sendData.length() - 8, sendData.length() - 4, checkNum);

            StringBuffer cloudHeader = new StringBuffer();
            //云服务标识（itcy）
            cloudHeader.append("79637469");
            //数据长度
            int dateL = (sendData.length() / 2) - 2;
            int cloudHeaderL = 28;
            String lengthHex = SmartBroadCastUtils.intToUint16Hex(dateL + cloudHeaderL);
            cloudHeader.append(lengthHex);
            //子系统目标IP
            String[] ipSrc = host.split("\\.");
            cloudHeader.append(intToUint8Hex(Integer.parseInt(ipSrc[0])));
            cloudHeader.append(intToUint8Hex(Integer.parseInt(ipSrc[1])));
            cloudHeader.append(intToUint8Hex(Integer.parseInt(ipSrc[2])));
            cloudHeader.append(intToUint8Hex(Integer.parseInt(ipSrc[3])));
            //子系统目标mac
            cloudHeader.append(SmartBroadcastApplication.cloudMacAddress);
            //控制ID,本地Mac地址
            String mac = DeviceUtils.getMacAddress();
            String cloudMacAddress = mac.replace(":", "");
            cloudHeader.append(cloudMacAddress);
            //协议类型，0：穿透指令，1：指令
            if (isOrder) {
                cloudHeader.append("01");
            } else {
                cloudHeader.append("00");
            }
            //设备标识，0：主机设备，1：手机
            cloudHeader.append("01");
            //保留字段
            cloudHeader.append("FFFFFFFFFFFFFFFF");
            cloudHeader.append(sendData);
            byte[] result = SmartBroadCastUtils.HexStringtoBytes(cloudHeader.toString());
            resultList.add(result);
        }
        return resultList;
    }


    /**
     * 异或加密
     *
     * @param data        用户名/密码
     * @param secretKeyStr 异或密钥
     * @return 十六进制字符串
     */
    public static String encryption(String data, String secretKeyStr) {
        byte[] secretKeys = HexStringtoBytes(secretKeyStr);
        byte[] bytes = SmartBroadCastUtils.HexStringtoBytes(SmartBroadCastUtils.str2HexStr(data));
        byte[] re = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            re[i] = (byte) (bytes[i] ^ secretKeys[i]);
        }
        String result = SmartBroadCastUtils.bytesToHexString(re);
        return result;
    }


    /**
     * 异或解密
     *
     * @param datas       用户名/密码
     * @param secreKeyStr 异或密钥
     * @return 十六进制字符串
     */
    public static String decrypt(byte[] datas, String secreKeyStr) {
        byte[] secretKeys = HexStringtoBytes(secreKeyStr);
        byte[] bytes = byteRemoveZero(datas);
        byte[] re = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            re[i] = (byte) (bytes[i] ^ secretKeys[i]);
        }
        String result = SmartBroadCastUtils.byteToStr(re);
        return result;
    }


    /**
     * 字节数组去0操作
     *
     * @param data
     * @return
     */
    public static byte[] byteRemoveZero(byte[] data) {
        String hexData = bytesToHexString(data);
        hexData = hexData.replace("0000", "");
        return HexStringtoBytes(hexData);
    }


    /**
     * 判断字符串是否为十六进制
     * @param str
     * @return
     */
    public static boolean isHex(String str){
        String regex="^[A-Fa-f0-9]+$";
        if(str.matches(regex)){
            return true;
        }else{
            return false;
        }
    }

}
