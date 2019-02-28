package com.itc.smartbroadcast.util;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;

/**
 * author： lghandroid
 * created：2018/7/24 17:30
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.channels
 * describe: 字节数组操作工具类
 */

public class ByteUtil {
    /**
     * 注释：int到字节数组的转换！
     *
     * @param number
     * @return
     */
    public static byte[] intToByte(int number) {
        int temp = number;
        byte[] b = new byte[4];
        for (int i = 0; i < b.length; i++) {
            b[i] = Integer.valueOf(temp & 0xff).byteValue();
            //将最低位保存在最低位
            temp = temp >> 8; // 向右移8位
        }
        return b;
    }

    /**
     * 小端byte转大端int
     * 注释：字节数组到int的转换！
     *
     * @param
     * @return
     */
    public static int byteToInt(byte[] b) {
        int s;
        int s0 = b[0] & 0xff;// 最低位
        int s1 = b[1] & 0xff;
        int s2 = b[2] & 0xff;
        int s3 = b[3] & 0xff;
        s3 <<= 24;
        s2 <<= 16;
        s1 <<= 8;
        s = s0 | s1 | s2 | s3;
        return s;
    }


    /**
     * 大端short转小端byte
     * 注释：字节数组到int的转换！
     *
     * @param
     * @return
     */
    public static byte[] shortTobyte(short iValue) {
        byte[] result = new byte[2];
        //由高位到低位
        // 先写short的最后一个字节
        result[0] = (byte)(iValue & 0xFF);
        // short 倒数第二个字节
        result[1] = (byte)((iValue & 0xFF00) >> 8 );
        return result;
    }

    /**
     * 注释：short到字节数组的转换！
     *
     * @param temp temp
     * @return
     */
    public static byte[] shortToByte(int temp) {
//        int temp = number;
        byte[] b = new byte[2];
        for (int i = 0; i < b.length; i++) {
            b[i] = Integer.valueOf(temp & 0xff).byteValue();
            //将最低位保存在最低位
            temp = temp >> 8; // 向右移8位
        }
        return b;
    }

    /**
     * 注释：字节数组到short的转换！
     *
     * @param b b
     * @return
     */
    public static short byteToShort(byte[] b) {
        short s;
        short s0 = (short) (b[0] & 0xff);// 最低位
        short s1 = (short) (b[1] & 0xff);
        s1 <<= 8;
        s = (short) (s0 | s1);
        return s;
    }
    public static String FormetFileSize(long file) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString;
        if (file < 1024) {
            fileSizeString = df.format((double) file) + "B";
        } else if (file < 1048576) {
            fileSizeString = df.format((double) file / 1024) + "KB";
        } else if (file < 1073741824) {
            fileSizeString = df.format((double) file / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) file / 1073741824) + "G";
        }
        return fileSizeString;
    }

    public static byte[] byteUtf8ToGbk(byte[] bytes){
        byte[] voteDdata = null;
        try {
            voteDdata = new String(bytes).getBytes("GBK");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return voteDdata;
    }

    /**
     * 转换byte数组为short
     */
    public static short Bytes2Short_LE(byte[] bytes){
        if(bytes.length < 2)
            return -1;
        short iRst = (short) ((bytes[0] & 0xFF) << 8);
        iRst |= (short)((bytes[1] & 0xFF));
        return iRst;
    }

    /**
     * 转换short为byte
     */
    public static byte[] ShortToByte_LE(short iValue){
        byte[] bytes1 = new byte[2];
        bytes1[0] =(byte)((iValue&0xFF00)>>8);
        bytes1[1] =  (byte)(iValue&0xFF);
        return bytes1;
    }
    /**
     * 大端int转小端byte
     * 注释：字节数组到int的转换！
     */
    public static byte[] IntToBytes_LE(int iValue){
        byte[] rst = new byte[4];
        // 先写int的最后一个字节
        rst[0] = (byte)(iValue & 0xFF);
        // int 倒数第二个字节
        rst[1] = (byte)((iValue & 0xFF00) >> 8 );
        // int 倒数第三个字节
        rst[2] = (byte)((iValue & 0xFF0000) >> 16 );
        // int 第一个字节
        rst[3] = (byte)((iValue & 0xFF000000) >> 24 );
        return rst;
    }

    /**
     * 字符转换为字节
     */
    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }

    /**
     * 16进制字符串转字节数组
     */
    public static byte[] hexString2Bytes(String hex) {

        if ((hex == null) || (hex.equals(""))){
            return null;
        }
        else if (hex.length()%2 != 0){
            return null;
        }
        else{
            hex = hex.toUpperCase();
            int len = hex.length()/2;
            byte[] b = new byte[len];
            char[] hc = hex.toCharArray();
            for (int i=0; i<len; i++){
                int p=2*i;
                b[i] = (byte) (charToByte(hc[p]) << 4 | charToByte(hc[p+1]));
            }
            return b;
        }

    }

    /*
    * 字节数组转16进制字符串
    */
    public static String bytes2HexString(byte[] b) {
        String r = "";

        for (int i = 0; i < b.length; i++) {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            r += hex.toUpperCase();
        }

        return r;
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        for (int i=begin; i<begin+count; i++) bs[i-begin] = src[i];
        return bs;
    }

   /**
    * 异或校验和
    **/
   public static byte getXor(byte[] datas){
       byte temp=datas[0];
       for (int i = 1; i <datas.length; i++) {
           temp ^=datas[i];
       }
       return temp;
   }

    //使用1字节就可以表示b
    public static String numToHex8(int b) {
        return String.format("%02x", b);//2表示需要两个16进行数
    }

    //需要使用2字节表示b
    public static String numToHex16(int b) {
        return String.format("%04x", b);
    }

    //需要使用4字节表示b
    public static String numToHex32(int b) {
        return String.format("%08x", b);
    }
}