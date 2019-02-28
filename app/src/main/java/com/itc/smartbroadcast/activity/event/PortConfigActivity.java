package com.itc.smartbroadcast.activity.event;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.adapter.event.AlarmAdapter;
import com.itc.smartbroadcast.adapter.event.PortConfigAdapter;
import com.itc.smartbroadcast.base.Base2Activity;
import com.itc.smartbroadcast.bean.AlarmDeviceDetail;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditAlarmDeviceResult;
import com.itc.smartbroadcast.channels.protocolhandler.GetAlarmDeviceDetail;
import com.itc.smartbroadcast.util.ToastUtil;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PortConfigActivity extends Base2Activity {

    Context mContext;
    @BindView(R.id.bt_back_event)
    ImageView btBackEvent;
    @BindView(R.id.rv_port)
    RecyclerView rvPort;
    private AlarmDeviceDetail alarmDeviceDetail = new AlarmDeviceDetail();

    PortConfigAdapter portConfigAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_port_config);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);

        mContext = this;

        Intent intent = getIntent();
        String alarmDeviceDetailJson = intent.getStringExtra("alarmDeviceDetail");
        alarmDeviceDetail = JSONObject.parseObject(alarmDeviceDetailJson, AlarmDeviceDetail.class);
        mContext = this;
        rvPort.setLayoutManager(new LinearLayoutManager(this));
        rvPort.setHasFixedSize(true);
        rvPort.setFocusableInTouchMode(false);
        rvPort.requestFocus();
        portConfigAdapter = new PortConfigAdapter(this);
        rvPort.setAdapter(portConfigAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        //初始化数据
        initData();
    }

    private void initData() {

        //获取详细信息
        GetAlarmDeviceDetail.sendCMD(alarmDeviceDetail.getDeviceIp());
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {
        portConfigAdapter.setList(alarmDeviceDetail.getIsAlarmPortSet(),alarmDeviceDetail);
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
                alarmDeviceDetail.setDeviceName(this.alarmDeviceDetail.getDeviceName());
                alarmDeviceDetail.setDeviceIp(this.alarmDeviceDetail.getDeviceIp());
                this.alarmDeviceDetail = alarmDeviceDetail;
                initView();
            }
        }
    }

    @OnClick({R.id.bt_back_event, R.id.rv_port})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_back_event:
                finish();
                break;
            case R.id.rv_port:
                break;
        }
    }
}
