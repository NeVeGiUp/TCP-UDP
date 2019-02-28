package com.itc.smartbroadcast.activity.found;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.MainActivity;
import com.itc.smartbroadcast.adapter.MyAdapter;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditAccManageResult;
import com.itc.smartbroadcast.bean.EditPartitionResult;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.bean.FoundPartitionInfo;
import com.itc.smartbroadcast.bean.PartitionInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.EditPartition;
import com.itc.smartbroadcast.channels.protocolhandler.GetDeviceList;
import com.itc.smartbroadcast.channels.protocolhandler.GetPartitionList;
import com.itc.smartbroadcast.helper.CustomDialog;
import com.itc.smartbroadcast.popupwindow.MoreWindow;
import com.itc.smartbroadcast.util.CharacterParser;
import com.itc.smartbroadcast.util.ConfigUtils;
import com.itc.smartbroadcast.util.ToastUtil;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * create by youmu on 2018/7
 */
public class PartitionManageActivity extends AppCompatActivity {
    private MyAdapter<FoundPartitionInfo> PtAdapter = null;
    private List<FoundPartitionInfo> PtData = null;
    private Context mContext;
    private ListView list_partitionList;
    private ImageView morewindow,btnBackFound;
    MoreWindow mMoreWindow;
    List<FoundDeviceInfo> deviceInfoList;
    List<FoundPartitionInfo> partInfoList;
    List<FoundPartitionInfo> userInfoList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partition_manage);
        //EventBus.getDefault().register(this);
        StatusBarUtil.setColor(PartitionManageActivity.this, getResources().getColor(R.color.colorMain),0);
        init();
    }

    private void init() {
        morewindow = (ImageView)findViewById(R.id.im_more_window);
        btnBackFound = (ImageView)findViewById(R.id.bt_back_found);
        list_partitionList = (ListView)findViewById(R.id.list_partition_list);
        GetPartitionList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
        btnBackFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(PartitionManageActivity.this, MainActivity.class);
//                intent.putExtra("jump","found");
//                startActivity(intent);
                finish();
            }
        });
        morewindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (partInfoList == null){
                }else if (partInfoList.size()>64){
                    ToastUtil.show(PartitionManageActivity.this,"无法创建更多分区！");
                }else {
                    Intent intent = new Intent(PartitionManageActivity.this,CreatePartitionActivity.class);
                    startActivity(intent);
                }
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        final Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);
        if ("getPartitionList".equals(baseBean.getType())) {
            final String data = baseBean.getData();
            Log.i("分区信息》》》", data);
            if (data != null) {
                GetDeviceList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
                PtData = new ArrayList<>();
                partInfoList = JSONArray.parseArray(data, FoundPartitionInfo.class);
                Collections.sort(partInfoList, new Comparator<FoundPartitionInfo>() {
                    @Override
                    public int compare(FoundPartitionInfo t1, FoundPartitionInfo t2) {
                        String s1 = CharacterParser.getInstance().getSelling(t1.getName());
                        String s2 = CharacterParser.getInstance().getSelling(t2.getName());
                            return s1.compareTo(s2);
                        }
                });
                if (AppDataCache.getInstance().getString("userType").equals("00")) {
                    PtData.addAll(partInfoList);
                }else {
                    userInfoList = new ArrayList<>();
                    for (FoundPartitionInfo foundPartitionInfo :partInfoList){
                        if (foundPartitionInfo.getAccountId() == AppDataCache.getInstance().getInt("userNum") ){
                            userInfoList.add(foundPartitionInfo);
                        }
                    }
                    PtData.addAll(userInfoList);
                }

                PtAdapter = new MyAdapter<FoundPartitionInfo>((ArrayList) PtData, R.layout.item_found_partition) {
                    @Override
                    public void bindView(ViewHolder holder, FoundPartitionInfo obj) {
                        holder.setText(R.id.partitino_name_tv, obj.getName());
                        if (obj.getDeviceInfoList() == null){
                            holder.setText(R.id.partitino_device_tv, "0个终端");
                        }else {
                            holder.setText(R.id.partitino_device_tv, obj.getDeviceInfoList().size()+"个终端");
                        }
                    }
                };
                list_partitionList.setAdapter(PtAdapter);
                PtAdapter.notifyDataSetChanged();
                list_partitionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        FoundPartitionInfo foundPartitionInfo = PtData.get(position);
                        Intent intent = new Intent();
                        intent.setClass(PartitionManageActivity.this,EditPartitionActivity.class);
                        List<FoundDeviceInfo> deviceOfPart = foundPartitionInfo.getDeviceInfoList();
                        String  sDeviceOfPart = gson.toJson(deviceOfPart);
                        String partName = foundPartitionInfo.getName();
                        String partNum = foundPartitionInfo.getPartitionNum();
                        Log.i("选择的分区ID>>>",partNum);
                        String accId = foundPartitionInfo.getAccountId()+"";
                        intent.putExtra("AccId",accId);
                        intent.putExtra("PartNum",partNum);
                        intent.putExtra("PartName",partName);
                        intent.putExtra("PartInfo",sDeviceOfPart);
                        PartitionManageActivity.this.startActivityForResult(intent,6);
                    }
                });
                list_partitionList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                        deletePart(position);
                        return true;
                    }
                });
            }
        }
        if ("getDeviceList".equals(baseBean.getType())){
            String data = baseBean.getData();
            if (data != null && partInfoList != null) {
                deviceInfoList = JSONArray.parseArray(data, FoundDeviceInfo.class);
                putDeviceInPart();
                PtAdapter.notifyDataSetChanged();
            }
        }
        if ("editPartitionResult".equals(baseBean.getType())) {
            EditPartitionResult editPartitionResult = gson.fromJson(baseBean.getData(), EditPartitionResult.class);
            String operator = editPartitionResult.getOperator();
            int result = editPartitionResult.getResult();
            if (operator.equals("00")){
                if (result == 0){
                    ToastUtil.show(PartitionManageActivity.this,"新建失败");
                }else {
                    ToastUtil.show(PartitionManageActivity.this,"新建成功");
                }
            }
            if (operator.equals("01")){
                if (result == 0){
                    ToastUtil.show(PartitionManageActivity.this,"删除失败");
                }else {
                    init();
                    ToastUtil.show(PartitionManageActivity.this,"删除成功");
                }
            }
            if (operator.equals("02")){
                if (result == 0){
                    ToastUtil.show(PartitionManageActivity.this,"修改失败");
                }else {
                    ToastUtil.show(PartitionManageActivity.this,"修改成功");
                }
            }

        }
    }

    private void putDeviceInPart(){
        for (FoundPartitionInfo foundPartitionInfo : partInfoList) {
            foundPartitionInfo.setDeviceInfoList(new ArrayList<FoundDeviceInfo>());
            for (FoundDeviceInfo foundDeviceInfo : deviceInfoList) {
                if(foundDeviceInfo.getDeviceZoneMsg().contains(Integer.valueOf(foundPartitionInfo.getPartitionNum()))) {
                    foundPartitionInfo.getDeviceInfoList().add(foundDeviceInfo);
                }
            }
        }
    }


    private void deletePart(final int position){
        Vibrator vb = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
        vb.vibrate(100);
        CustomDialog dialog = new CustomDialog(PartitionManageActivity.this, "提示", "确定要删除当前分区？",
                "取消", "确认", new CustomDialog.OnOkClickListener() {
            @Override
            public void onClick() {
                FoundPartitionInfo foundPartitionInfo = PtAdapter.getList().get(position);
                PartitionInfo partition = new PartitionInfo();
                ArrayList<String> macList = new ArrayList<>();
                macList.add("42-4c-45-00-0a-01");
                int partNum = Integer.parseInt(foundPartitionInfo.getPartitionNum());
                Log.i("删除的分区ID>>>",foundPartitionInfo.getPartitionNum()+"+"+foundPartitionInfo.getName());
                partition.setPartitionNum(partNum);   //分区号：分区创建完成时会分配
                partition.setPartitionName("1");
                partition.setDeviceCount(1);
                partition.setAccountId(0);
                partition.setDeviceMacList(macList);
                EditPartition.sendCMD(AppDataCache.getInstance().getString("loginIp"), partition, 1);

            }
        });
        dialog.show();
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
//            Intent intent = new Intent(PartitionManageActivity.this, MainActivity.class);
//            intent.putExtra("jump","found");
//            startActivity(intent);
            finish();
            return true;
        }else {
            return super.dispatchKeyEvent(event);
        }
    }

    private void showMoreWindow(View view) {
        if (null == mMoreWindow) {
            mMoreWindow = new MoreWindow(PartitionManageActivity.this);
            mMoreWindow.init();
        }
        mMoreWindow.showMoreWindow(view, 100);
    }

    @Override
    protected void onResume(){
        init();
       EventBus.getDefault().register(this);
        super.onResume();
    }
    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

//    @Override
//    protected void onDestroy() {
//        EventBus.getDefault().unregister(this);
//        super.onDestroy();
//    }



}
