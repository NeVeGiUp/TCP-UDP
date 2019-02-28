package com.itc.smartbroadcast.util;

/**
 * author： lghandroid
 * created：2018/7/24 19:03
 * project：SmartBroadcast
 * package：com.itc.smartbroadcast.util
 * describe: 项目一些静态参数
 * 主机ip ：172.16.12.112
 */

public class ConfigUtils {

    public static final String HOST = "172.16.13.112";                   // udp服务器地址
    public static final String TCP_HOST = "yunbo.itc-pa.cn";              // TCP服务器地址
    public static final int TCP_PORT = 7003;

//    public static final String TCP_HOST = "172.16.12.177";              // TCP服务器地址
//    public static final int TCP_PORT = 6666;

    public static final int UDP_COMMAND_PORT = 8805;                     // udp控制指令通讯端口号
    public static final int UDP_AUDIO_PORT = 8805;                       // udp网络音频通讯端口号
    public static final String BASEURL = "http://" + TCP_HOST + ":88/";    // http url

    public static String USERNAME_SECRETKEY = "5E7F5DDE5E0272319012601D75355B5067099650516C53F8781453D190E8";
    public static String PASSWORD_SECRETKEY = "5E7F5DDE5E0272319012601D";

    public static String token = "sdf";
}
