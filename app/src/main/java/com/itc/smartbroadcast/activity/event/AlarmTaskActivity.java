package com.itc.smartbroadcast.activity.event;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.adapter.child.EventChildInstantTaskAdapter;
import com.itc.smartbroadcast.adapter.event.AlarmAdapter;
import com.itc.smartbroadcast.base.Base2Activity;
import com.itc.smartbroadcast.bean.AlarmDeviceDetail;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditInstantTaskResult;
import com.itc.smartbroadcast.bean.EditSchemeResult;
import com.itc.smartbroadcast.bean.EditTaskResult;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.bean.InstantTask;
import com.itc.smartbroadcast.bean.Scheme;
import com.itc.smartbroadcast.bean.Task;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.GetAlarmDeviceDetail;
import com.itc.smartbroadcast.channels.protocolhandler.GetDeviceList;
import com.itc.smartbroadcast.channels.protocolhandler.GetInstantTaskList;
import com.itc.smartbroadcast.channels.protocolhandler.GetSchemeList;
import com.itc.smartbroadcast.channels.protocolhandler.GetTaskList;
import com.itc.smartbroadcast.popupwindow.MoreWindow;
import com.itc.smartbroadcast.util.ToastUtil;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AlarmTaskActivity extends Base2Activity {
    @BindView(R.id.bt_back_event)
    ImageView btBackEvent;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.im_add_instant_task)
    ImageView imAddInstantTask;
    @BindView(R.id.ll_showWindow)
    LinearLayout llShowWindow;
    @BindView(R.id.rv_alarm)
    RecyclerView rvAlarm;

    private MoreWindow mMoreWindow;

    List<AlarmDeviceDetail> alarmDeviceDetailList = new ArrayList<>();
    AlarmAdapter alarmAdapter;


    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmtask);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        //初始化数据
        initData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void initData() {
        String deviceListJson = AppDataCache.getInstance().getString("deviceList");
        List<FoundDeviceInfo> foundDeviceInfoList = JSONArray.parseArray(deviceListJson, FoundDeviceInfo.class);
        alarmDeviceDetailList.clear();
        for (FoundDeviceInfo deviceInfo : foundDeviceInfoList) {
            if ("TX-8623".equals(deviceInfo.getDeviceMedel()) && "在线".equals(deviceInfo.getDeviceStatus())) {
                AlarmDeviceDetail alarmDeviceDetail = new AlarmDeviceDetail();
                alarmDeviceDetail.setDeviceName(deviceInfo.getDeviceName());
                alarmDeviceDetail.setDeviceMac(deviceInfo.getDeviceMac());
                alarmDeviceDetail.setDeviceIp(deviceInfo.getDeviceIp());
                alarmDeviceDetail.setPlayMode(255);
                alarmDeviceDetail.setTriggerMode(255);
                alarmDeviceDetail.setPortResponseMode(255);
                alarmDeviceDetail.setPortCount(255);
                alarmDeviceDetailList.add(alarmDeviceDetail);
                //获取详细信息
                GetAlarmDeviceDetail.sendCMD(deviceInfo.getDeviceIp());
            }
        }
        alarmAdapter.setList(alarmDeviceDetailList);
    }

    private void initView() {
        context = this;
        rvAlarm.setLayoutManager(new LinearLayoutManager(this));
        rvAlarm.setHasFixedSize(true);
        rvAlarm.setFocusableInTouchMode(false);
        rvAlarm.requestFocus();
        alarmAdapter = new AlarmAdapter(this);
        rvAlarm.setAdapter(alarmAdapter);
    }


    @OnClick({R.id.bt_back_event, R.id.ll_showWindow})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_back_event:
                finish();
                break;
            case R.id.ll_showWindow:
                showMoreWindow(view);
                break;
        }
    }

    private void showMoreWindow(View view) {
        if (null == mMoreWindow) {
            mMoreWindow = new MoreWindow(this);
            mMoreWindow.init();
        }
        mMoreWindow.showMoreWindow(view, 100);
    }


    /**
     * 数据回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);
        if ("getAlarmDeviceDetail".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                AlarmDeviceDetail alarmDeviceDetail = JSONObject.parseObject(data, AlarmDeviceDetail.class);
                for (int i = 0; i < alarmDeviceDetailList.size(); i++) {
                    if (alarmDeviceDetail.getDeviceMac().equals(alarmDeviceDetailList.get(i).getDeviceMac())) {
                        alarmDeviceDetail.setDeviceName(alarmDeviceDetailList.get(i).getDeviceName());
                        alarmDeviceDetail.setDeviceIp(alarmDeviceDetailList.get(i).getDeviceIp());
                        alarmDeviceDetailList.set(i, alarmDeviceDetail);
                    }
                }
                alarmAdapter.setList(alarmDeviceDetailList);
            }
        }
    }
}
