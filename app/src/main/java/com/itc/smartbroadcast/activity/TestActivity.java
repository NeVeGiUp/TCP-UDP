package com.itc.smartbroadcast.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.bean.PartitionInfo;
import com.itc.smartbroadcast.bean.TerminalDetailInfo;
import com.itc.smartbroadcast.channels.protocolhandler.EditPartition;
import com.itc.smartbroadcast.channels.tcp.NettyTcpClient;
import com.itc.smartbroadcast.util.ConfigUtils;

import java.util.ArrayList;

import static com.itc.smartbroadcast.channels.protocolhandler.GetSchemeList.getSchemeList;

public class TestActivity extends AppCompatActivity {


    String name = "test11111";
    private ListView lv_test;


    private String strArr[] = {"TCP调试"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initView();
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, strArr);
        lv_test.setAdapter(adapter);
        lv_test.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0: //获取打铃方案
                        NettyTcpClient.getInstance().sendPackage("172.16.12.212",getSchemeList());
                        break;
                }
            }
        });
    }

    private void editTerminalMsg() {
        TerminalDetailInfo detailInfo = new TerminalDetailInfo();
        //音源设备mac
        detailInfo.setTerminalMac("42-4c-45-00-cc-01");
        //设备名称
        detailInfo.setTerminalName("Android音源设备测试_1");
        //IP获取方式 0为静态获取，1为动态获取
        detailInfo.setTerminalIpMode(0);
        //设备IP
        detailInfo.setTerminalIp("172.16.13.121");
        //设备子网掩码
        detailInfo.setTerminalSubnet("255.255.255.0");
        //设备网关
        detailInfo.setTerminalGateway("172.16.13.254");
        //设备音源类型
        detailInfo.setTerminalSoundCate("10");
        //设备优先级
        detailInfo.setTerminalPriority("00");
        //设备默音音量
        detailInfo.setTerminalDefVolume(20);
        //设备默认音量
        detailInfo.setTerminalSetVolume(100);
        //系统旧密码
        detailInfo.setTerminalOldPsw("123");
        //系统新密码
        detailInfo.setTerminalNewPsw("123");
        //host为指定终端IP地址
//        EditTerminalMsg.sendCMD("172.16.13.121", detailInfo);
    }

    private void editPartition() {
        PartitionInfo partition = new PartitionInfo();
        ArrayList<String> macList = new ArrayList<>();
        macList.add("42-4c-45-00-0a-01");
        partition.setPartitionNum(0);  //分区号
        partition.setAccountId(0);
        partition.setPartitionName("Android新建分区1");
        partition.setDeviceCount(1);
        partition.setDeviceMacList(macList);
        EditPartition.sendCMD(ConfigUtils.HOST, partition, 2);
    }

    private void deletePartition() {
        PartitionInfo partition = new PartitionInfo();
        ArrayList<String> macList = new ArrayList<>();
        macList.add("42-4c-45-00-0a-01");
        partition.setPartitionNum(0);   //分区号：分区创建完成时会分配
        partition.setAccountId(0);
        partition.setPartitionName("Android新建分区1");
        partition.setDeviceCount(1);
        partition.setDeviceMacList(macList);
        EditPartition.sendCMD(ConfigUtils.HOST, partition, 1);
    }

    private void addPartition() {
        PartitionInfo partition = new PartitionInfo();
        ArrayList<String> macList = new ArrayList<>();
        macList.add("42-4c-45-00-aa-04");
        partition.setAccountId(0);
        partition.setPartitionName("Android新建分区1");
        partition.setDeviceCount(1);
        partition.setDeviceMacList(macList);
        EditPartition.sendCMD(ConfigUtils.HOST, partition, 0);
    }

    public void initView() {
        lv_test = (ListView) findViewById(R.id.lv_test);
    }
}
