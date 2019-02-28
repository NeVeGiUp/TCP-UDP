package com.itc.smartbroadcast.activity.personal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.music.ShowMusicListActivity;
import com.itc.smartbroadcast.adapter.DepolySystemAdapter;
import com.itc.smartbroadcast.adapter.MyAdapter;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.ConfigureTargetHostInfo;
import com.itc.smartbroadcast.bean.ConfigureTargetHostResult;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.ConfigureTargetHost;
import com.itc.smartbroadcast.channels.protocolhandler.SearchDeviceList;
import com.itc.smartbroadcast.util.ToastUtil;
import com.itc.smartbroadcast.widget.custom.CommonProgressDialog;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DepolySystemActivity extends AppCompatActivity {

    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R.id.et_target_timer_ip)
    EditText etTargetTimerIp;
    @BindView(R.id.bt_s_device)
    Button btSDevice;
    @BindView(R.id.bt_config_device)
    Button btConfigDevice;
    @BindView(R.id.list_depoly_system)
    ListView listDepolySystem;
    private CommonProgressDialog progressDialog;
    private DepolySystemAdapter deviceAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depoly_system);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);
    }
    @Override
    public void onResume() {
        EventBus.getDefault().register(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);
        String baseData = baseBean.getData();
        Log.i("搜索到的设备>>>", baseData);
        if ("searchDeviceList".equals(baseBean.getType())) {
            List<FoundDeviceInfo> infoList = JSONArray.parseArray(baseData, FoundDeviceInfo.class);
            List<FoundDeviceInfo> deviceInfos = new ArrayList<>();
            deviceInfos.addAll(infoList);
//            MyAdapter<FoundDeviceInfo> deviceAdapter = new MyAdapter<FoundDeviceInfo>((ArrayList) deviceInfos, R.layout.item_system_depoly) {
//                @Override
//                public void bindView(ViewHolder holder, FoundDeviceInfo obj) {
//                    holder.setText(R.id.tv_device_name,obj.getDeviceName());
//                    holder.setText(R.id.tv_target_ip,"绑定主机："+obj.getDeviceIp());
//                }
//            };
            deviceAdapter = new DepolySystemAdapter(deviceInfos,this);
            progressDialog.dismiss();
            listDepolySystem.setAdapter(deviceAdapter);
        }
        if ("ConfigureTargetHostResult".equals(baseBean.getType())){
            ConfigureTargetHostResult configureTargetHostResult = gson.fromJson(baseBean.getData(),ConfigureTargetHostResult.class);
            if (configureTargetHostResult.getResult()==1){
                ToastUtil.show(this,"配置成功,正在重新获取设备列表...");
                progressDialog = new CommonProgressDialog(this);  //登录进度条
                progressDialog.show();
                SearchDeviceList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
            }
        }
    }

    @OnClick({R.id.rl_back, R.id.bt_s_device, R.id.bt_config_device})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.bt_s_device:
                progressDialog = new CommonProgressDialog(this);  //登录进度条
                progressDialog.show();
                SearchDeviceList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
                break;
            case R.id.bt_config_device:
                String targetIp = etTargetTimerIp.getText().toString().trim();
                List<String> macList = new ArrayList<>();
                for (FoundDeviceInfo deviceInfo :deviceAdapter.getCheckBoxIDList()){
                    macList.add(deviceInfo.getDeviceMac());
                }
                ConfigureTargetHostInfo configureTargetHostInfo = new ConfigureTargetHostInfo();
                configureTargetHostInfo.setIp(targetIp);
                configureTargetHostInfo.setDeviceTotal(macList.size());
                configureTargetHostInfo.setMacList(macList);
                ConfigureTargetHost.sendCMD(AppDataCache.getInstance().getString("loginIp"),configureTargetHostInfo);
                break;
        }
    }
}
