package com.itc.smartbroadcast.channels.tcp;

import android.util.Log;

import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.channels.protocolhandler.BatchEditTask;
import com.itc.smartbroadcast.channels.protocolhandler.BatchOperateMusicFiles;
import com.itc.smartbroadcast.channels.protocolhandler.BatchOperateMusicFilesReply;
import com.itc.smartbroadcast.channels.protocolhandler.ConfigureTodayTask;
import com.itc.smartbroadcast.channels.protocolhandler.DeviceControl;
import com.itc.smartbroadcast.channels.protocolhandler.EditAccountManage;
import com.itc.smartbroadcast.channels.protocolhandler.EditAlarmDevice;
import com.itc.smartbroadcast.channels.protocolhandler.EditAlarmPortDeviceList;
import com.itc.smartbroadcast.channels.protocolhandler.EditCollectorSoundSourceType;
import com.itc.smartbroadcast.channels.protocolhandler.EditInstantTask;
import com.itc.smartbroadcast.channels.protocolhandler.EditMusicFolderName;
import com.itc.smartbroadcast.channels.protocolhandler.EditPartition;
import com.itc.smartbroadcast.channels.protocolhandler.EditPowerAmplifierDetail;
import com.itc.smartbroadcast.channels.protocolhandler.EditScheme;
import com.itc.smartbroadcast.channels.protocolhandler.EditTask;
import com.itc.smartbroadcast.channels.protocolhandler.EditTerminalMsg;
import com.itc.smartbroadcast.channels.protocolhandler.ExecuteTaskDate;
import com.itc.smartbroadcast.channels.protocolhandler.FileOperationProgress;
import com.itc.smartbroadcast.channels.protocolhandler.GetAccountList;
import com.itc.smartbroadcast.channels.protocolhandler.GetAlarmDeviceDetail;
import com.itc.smartbroadcast.channels.protocolhandler.GetAlarmMusicFolderList;
import com.itc.smartbroadcast.channels.protocolhandler.GetAlarmMusicList;
import com.itc.smartbroadcast.channels.protocolhandler.GetAlarmPortDeviceList;
import com.itc.smartbroadcast.channels.protocolhandler.GetCDMusicList;
import com.itc.smartbroadcast.channels.protocolhandler.GetCollectorSoundSourceType;
import com.itc.smartbroadcast.channels.protocolhandler.GetControlPanelDetail;
import com.itc.smartbroadcast.channels.protocolhandler.GetDeviceList;
import com.itc.smartbroadcast.channels.protocolhandler.GetInstallTaskEnd;
import com.itc.smartbroadcast.channels.protocolhandler.GetInstantStatus;
import com.itc.smartbroadcast.channels.protocolhandler.GetInstantTaskDetail;
import com.itc.smartbroadcast.channels.protocolhandler.GetInstantTaskList;
import com.itc.smartbroadcast.channels.protocolhandler.GetLoginedMsg;
import com.itc.smartbroadcast.channels.protocolhandler.GetMusicFolderList;
import com.itc.smartbroadcast.channels.protocolhandler.GetMusicList;
import com.itc.smartbroadcast.channels.protocolhandler.GetOperatorDeviceList;
import com.itc.smartbroadcast.channels.protocolhandler.GetPartitionList;
import com.itc.smartbroadcast.channels.protocolhandler.GetPowerAmplifierDetail;
import com.itc.smartbroadcast.channels.protocolhandler.GetSchemeList;
import com.itc.smartbroadcast.channels.protocolhandler.GetTaskDetail;
import com.itc.smartbroadcast.channels.protocolhandler.GetTaskList;
import com.itc.smartbroadcast.channels.protocolhandler.GetTerminalDetail;
import com.itc.smartbroadcast.channels.protocolhandler.GetTerminalDeviceStatus;
import com.itc.smartbroadcast.channels.protocolhandler.OperateInstantTask;
import com.itc.smartbroadcast.channels.protocolhandler.OperateMusicFiles;
import com.itc.smartbroadcast.channels.protocolhandler.QueryMusicUploadStatus;
import com.itc.smartbroadcast.channels.protocolhandler.SynchronizationTime;
import com.itc.smartbroadcast.channels.protocolhandler.SystemRegister;
import com.itc.smartbroadcast.channels.protocolhandler.TaskOperation;
import com.itc.smartbroadcast.channels.protocolhandler.TimerStatusQuery;
import com.itc.smartbroadcast.util.ConfigUtils;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import java.util.ArrayList;
import java.util.List;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

/**
 * author：  lghandroid
 * created： 2018/7/24 18:30
 * project： SmartBroadcast
 * package： com.itc.smartbroadcast.channels
 * describe: 该类是netty接收数据类(整理数据以接口回调和eventbus方式中转和传递数据给相应得页面)，包括获取下发数据，重连等
 */

public class NettyTcpClientHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    List<byte[]> buffList = new ArrayList<>();

    String lastProtocolType = "";

    /**
     * TCP通道是否开启
     *
     * @param ctx 通道对象
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        //注意，TCP的通道至始至终只有一个，关了就不能接收了。
        Log.e(NettyTcpClient.TAG, "TCP通道已经开启 ");
        buffList.clear();
    }


    /**
     * 异常通道
     *
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        Log.e(NettyTcpClient.TAG, "exceptionCaught");
    }


    /**
     * 连接成功触发channelActive
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Log.e(NettyTcpClient.TAG, "成功连接！");


    }


    /**
     * 断开连接触发channelInactive
     * <p>
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Log.e(NettyTcpClient.TAG, "断开连接");
        NettyTcpClient.getInstance().connServer(ConfigUtils.TCP_HOST, ConfigUtils.TCP_PORT);
    }


    /**
     * @param ctx   通道对象
     * @param msgOb 发送数据ip与端口
     * @throws Exception 若主机连接则不会回调此方法
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msgOb) throws Exception {

        byte[] alldata = (byte[]) msgOb;

        Log.e(NettyTcpClient.TAG, "接收服务器数据包 >>> " + SmartBroadCastUtils.bytesToHexString(alldata));

        String body = new String(alldata, "UTF-16LE");
        alldata = SmartBroadCastUtils.subBytes(alldata, 32, alldata.length - 32);

        //包数
        byte[] packetCountBt = SmartBroadCastUtils.subBytes(alldata, 28, 1);
        int packetSize = SmartBroadCastUtils.byteToInt(packetCountBt[0]);

        //当前包序号
        byte[] packetIndexBt = SmartBroadCastUtils.subBytes(alldata, 29, 1);
        int packetIndex = SmartBroadCastUtils.byteToInt(packetIndexBt[0]) + 1;

        //分类码，如B9
        byte[] byProtocolType = SmartBroadCastUtils.subBytes(alldata, 5, 1);
        String hexProtocolType = SmartBroadCastUtils.bytesToHexString(byProtocolType);

        //指令，如00
        byte[] byCommandType = SmartBroadCastUtils.subBytes(alldata, 4, 1);
        String hexCommandType = SmartBroadCastUtils.bytesToHexString(byCommandType);

        //多包分类处理
        if (!lastProtocolType.equals(hexProtocolType + hexCommandType)) {
            buffList.clear();
        }
        lastProtocolType = hexProtocolType + hexCommandType;

        switch (hexProtocolType) {
            case "ax": //音频协议类
                break;
            case "bx": //网络间通讯协议类
                break;
            case "b0": //终端设备管理类型
                break;
            case "b1": //设备列表获取更新及配置管理类型
                buffList.add(alldata);
                dvDeviceType(packetSize, packetIndex, ctx, hexCommandType, buffList);
                break;
            case "b2": //分区配置与管理类型
                buffList.add(alldata);
                dvPartitionType(packetSize, packetIndex, ctx, hexCommandType, buffList);
                break;
            case "b3": //定时任务类型
                buffList.add(alldata);
                dvTimedTaskType(packetSize, packetIndex, ctx, hexCommandType, buffList);
                break;
            case "b4": //即时任务类型
                buffList.add(alldata);
                dvInstantTaskType(packetSize, packetIndex, ctx, hexCommandType, buffList);
                break;
            case "b5": //寻呼功能管理类型
                break;
            case "b6": //监听功能管理类型
                break;
            case "b7": //报警管理类型
                buffList.add(alldata);
                dvAlarmType(packetSize, packetIndex, ctx, hexCommandType, buffList);
                break;
            case "b8": //定时器音乐库类型
                buffList.add(alldata);
                dvMusicType(packetSize, packetIndex, ctx, hexCommandType, buffList);
                break;
            case "b9": //账户管理类型
                buffList.add(alldata);
                dvAccountManageType(packetSize, packetIndex, ctx, hexCommandType, buffList);
                break;
            case "ba": //控制面板类型
                break;
            case "bb": //时间同步
                buffList.add(alldata);
                dvSynTimeType(packetSize, packetIndex, ctx, hexCommandType, buffList);
                break;
            case "be": //账户管理类型
                buffList.add(alldata);
                cloudManageType(packetSize, packetIndex, ctx, hexCommandType, buffList);
                break;
            case "bf":  //全局控制
                buffList.add(alldata);
                dvGlobalControl(packetSize, packetIndex, ctx, hexCommandType, buffList);
                break;
            case "cx": //外设与模块间通讯协议类
                break;
            case "c0": //外设与模块间发送协议类型
                break;
            case "dx": //心跳包类型
                break;
            case "d0": //为模块发送心跳包
                break;
        }


        super.channelRead(ctx, msgOb);   //这里调用得是channelRead0（）
    }


    /**
     * 接收主机数据源头
     *
     * @param ctx 通道对象
     * @param msg 数据对象
     * @throws Exception 可根据返回的校验值判断是哪个请求返回的数据
     *                   下发数据组成（数据头14个字节 + 具体数据 + 数据尾4个字节）作为数据过滤根据
     *                   重发机制，在发送数据没有回调数据的时候执行，次数约为3-5次，超过次数还没收到回调数据提示网络问题
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg)
            throws Exception {

    }
    /**
     * 报警设备信息交互
     *
     * @param packetSize
     * @param packetIndex
     * @param ctx
     * @param hexCommandType
     * @param buffList
     */
    private void dvAlarmType(int packetSize, int packetIndex, ChannelHandlerContext ctx, String hexCommandType, List<byte[]> buffList) {
        switch (hexCommandType) {
            case "00":  //获取报警任务详情
                GetAlarmDeviceDetail.init().handler(buffList);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;

            case "01":  //获取报警端口绑定设备列表
                GetAlarmPortDeviceList.init().handler(buffList);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "02":
                if (subcontractHandler(packetSize, packetIndex, ctx, buffList)) {
                    GetAlarmMusicFolderList.init().handler(buffList);
                    buffList.clear();
                    SmartBroadcastApplication.replyData = true;
                }
                break;
            case "03":
                if (subcontractHandler(packetSize, packetIndex, ctx, buffList)) {
                    GetAlarmMusicList.init().handler(buffList);
                    buffList.clear();
                    SmartBroadcastApplication.replyData = true;
                }
                break;
            case "04":  //编辑报警任务
                EditAlarmDevice.init().handler(buffList);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "05":  //报警设备配置报警端口绑定的设备列表协议
                EditAlarmPortDeviceList.init().handler(buffList);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            default:
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
        }
    }

    private void dvGlobalControl(int packetSize, int packetIndex, ChannelHandlerContext ctx, String hexCommandType, List<byte[]> buffList) {

        switch (hexCommandType) {
            case "04":  //获取终端设备状态协议
                GetTerminalDeviceStatus.init().handler(buffList);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            default:
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
        }

    }


    /**
     * 账户管理
     *
     * @param hexCommandType
     * @param buffList
     */
    private void dvAccountManageType(int packetSize, int packetIndex, ChannelHandlerContext ctx, String hexCommandType, List<byte[]> buffList) {
        switch (hexCommandType) {

            case "00":  //账户登录
                GetLoginedMsg.init().handler(buffList);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "01":  //获取账户列表
                if (subcontractHandler(packetSize, packetIndex, ctx, buffList)) {
                    GetAccountList.init().handler(buffList);
                    buffList.clear();
                    SmartBroadcastApplication.replyData = true;
                }
                break;
            case "02":  //账户获取可操作设备列表
                GetOperatorDeviceList.init().handler(buffList);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "03":  //账户添加，编辑和删除
                EditAccountManage.init().handler(buffList);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "0d":  //系统注册
                SystemRegister.init().handler(buffList);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "05":  //定时器在线状态查询
                TimerStatusQuery.init().handler(buffList);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            default:
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
        }

    }


    /**
     * 云协议
     *
     * @param hexCommandType
     * @param buffList
     */
    private void cloudManageType(int packetSize, int packetIndex, ChannelHandlerContext ctx, String hexCommandType, List<byte[]> buffList) {
        switch (hexCommandType) {

            case "06":  //账户登录
                GetLoginedMsg.init().handler(buffList);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            default:
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
        }

    }


    /**
     * 音乐库二级分类
     *
     * @param hexCommandType
     * @param buffList
     */
    private void dvMusicType(int packetSize, int packetIndex, ChannelHandlerContext ctx, String hexCommandType, List<byte[]> buffList) {
        switch (hexCommandType) {
            case "00":  //获取曲目库音乐文件夹列表
                if (subcontractHandler(packetSize, packetIndex, ctx, buffList)) {
                    GetMusicFolderList.init().handler(buffList);
                    buffList.clear();
                    SmartBroadcastApplication.replyData = true;
                }
                break;
            case "01":  //获取指定文件夹歌曲库列表
                if (subcontractHandler(packetSize, packetIndex, ctx, buffList)) {
                    GetMusicList.init().handler(buffList);
                    buffList.clear();
                    SmartBroadcastApplication.replyData = true;
                }
                break;
            case "02":  //修改指定音乐文件夹名称，包括添加，删除音乐文件夹
                EditMusicFolderName.init().handler(buffList);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "03":  //查询定时器是否正在传输文件处于繁忙状态
                QueryMusicUploadStatus.init().handler(buffList);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "04":  //复制，移动，删除到指定文件夹(乐库)里的音乐协议
                OperateMusicFiles.init().handler(buffList);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "05":  //文件操作进度协议
                FileOperationProgress.init().handler(buffList);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "06":  //批量复制，移动，删除到指定文件夹(乐库)里的音乐协议
                BatchOperateMusicFiles.init().handler(buffList);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "07":  //批量编辑音乐回复协议(定时器主动推送信息，客户端回复定时器)
                BatchOperateMusicFilesReply.init().handler(buffList);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            default:
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
        }
    }

    /**
     * 分区配置管理二级分类
     *
     * @param packetSize
     * @param packetIndex
     * @param ctx
     * @param hexCommandType
     * @param buffList
     */
    private void dvPartitionType(int packetSize, int packetIndex, ChannelHandlerContext ctx, String hexCommandType, List<byte[]> buffList) {
        switch (hexCommandType) {
            case "00":  //获取分区列表
                if (subcontractHandler(packetSize, packetIndex, ctx, buffList)) {  //有分包情况下
                    GetPartitionList.init().handler(buffList);
                    buffList.clear();
                    SmartBroadcastApplication.replyData = true;
                }
                break;
            case "02":  //编辑分区结果
                EditPartition.init().handler(buffList);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            default:
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
        }
    }


    //多包处理
    public boolean subcontractHandler(int packetSize, int packetIndex, ChannelHandlerContext ctx, List<byte[]> buffList) {
        //如果是最后一个包
        if (packetSize == packetIndex && packetSize == buffList.size()) {
            return true;
        } else {
            return false;
        }
    }


    //设备列表及配置管理二次分类
    private void dvDeviceType(int packetSize, int packetIndex, ChannelHandlerContext ctx, String hexCommandType, List<byte[]> buffList) {
        switch (hexCommandType) {
            case "00":  //获取设备列表
                if (subcontractHandler(packetSize, packetIndex, ctx, buffList)) {  //有分包情况下
                    GetDeviceList.init().handler(buffList);
                    buffList.clear();
                    SmartBroadcastApplication.replyData = true;
                }
                break;
            case "02":  //获取终端详情
                GetTerminalDetail.init().handler(buffList);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "04":  //配置终端设备信息
                EditTerminalMsg.init().handler(buffList);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "05":  //查询功放设备详情
                GetPowerAmplifierDetail.init().handler(buffList);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "06":  //编辑功放设备详情
                EditPowerAmplifierDetail.init().handler(buffList);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "08":  //查询控制面板详情
                GetControlPanelDetail.init().handler(buffList);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "0a":  //查询采集器音源类型
                GetCollectorSoundSourceType.init().handler(buffList);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "0b":  //配置采集器音源类型
                EditCollectorSoundSourceType.init().handler(buffList);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            default:
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
        }
    }


    /**
     * 定时任务类型二次分类
     *
     * @param hexCommandType
     * @param list
     */
    private void dvTimedTaskType(int packetSize, int packetIndex, ChannelHandlerContext ctx, String hexCommandType, List<byte[]> list) {
        switch (hexCommandType) {
            case "00":  //获取方案列表
                GetSchemeList.init().handler(list);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "01":  //获取任务列表
                if (subcontractHandler(packetSize, packetIndex, ctx, buffList)) {
                    GetTaskList.init().handler(list);
                    buffList.clear();
                    SmartBroadcastApplication.replyData = true;
                }
                break;
            case "04":  //获取定时任务详情
                if (subcontractHandler(packetSize, packetIndex, ctx, buffList)) {
                    GetTaskDetail.init().handler(list);
                    buffList.clear();
                    SmartBroadcastApplication.replyData = true;
                }
                break;
            case "05":  //修改方案返回数据
                EditScheme.init().handler(list);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "06":
//                EditTask.init().handler(list);
//                buffList.clear();
//                SmartBroadcastApplication.replyData = true;
                break;
            case "07":  //修改任务
                EditTask.init().handler(list);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "09":  //查询今日执行周几任务
                ExecuteTaskDate.init().handler(list);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "0b":  //配置，切换今日任务
                ConfigureTodayTask.init().handler(list);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "0d":  //批量编辑任务
                BatchEditTask.init().handler(list);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "0e":  //任务启动停止操作
                TaskOperation.init().handler(list);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            default:
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
        }
    }

    /**
     * 即时任务类型二次分类
     *
     * @param hexCommandType
     * @param list
     */
    private void dvInstantTaskType(int packetSize, int packetIndex, ChannelHandlerContext ctx, String hexCommandType, List<byte[]> list) {
        switch (hexCommandType) {
            case "00":  //获取即时任务列表
                if (subcontractHandler(packetSize, packetIndex, ctx, buffList)) {
                    GetInstantTaskList.init().handler(list);
                    buffList.clear();
                    SmartBroadcastApplication.replyData = true;
                }
                break;
            case "03":  //获取编辑即时任务结果
                EditInstantTask.init().handler(list);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "02":  //获取即时任务详情
                GetInstantTaskDetail.init().handler(list);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "04":  //获取操作即时任务结果
                OperateInstantTask.init().handler(list);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "05":  //获取操作即时任务结果
                DeviceControl.init().handler(list);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "06":  //获取CD机回复状态
                GetInstantStatus.init().handler(list);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "09":  //CD机更新曲目
                GetCDMusicList.init().handler(list);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            case "0b"://CD机回复任务结束状态
                GetInstallTaskEnd.init().handler(list);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            default:
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
        }
    }


    /**
     * 时间同步
     *
     * @param packetSize
     * @param packetIndex
     * @param ctx
     * @param hexCommandType
     * @param buffList
     */
    private void dvSynTimeType(int packetSize, int packetIndex, ChannelHandlerContext ctx, String hexCommandType, List<byte[]> buffList) {
        switch (hexCommandType) {

            case "00":  //时间同步
                SynchronizationTime.init().handler(buffList);
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;
            default:
                buffList.clear();
                SmartBroadcastApplication.replyData = true;
                break;

        }
    }

    /**
     * 读数据完成
     *
     * @param ctx 通道对象
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
        Log.e(NettyTcpClient.TAG, "TCP--channelReadComplete");
    }


}