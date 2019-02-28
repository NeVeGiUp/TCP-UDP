package com.itc.smartbroadcast.activity.event;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.event.utils.TaskUtils;
import com.itc.smartbroadcast.base.Base2Activity;
import com.itc.smartbroadcast.bean.AlarmDeviceDetail;
import com.itc.smartbroadcast.bean.AlarmPortDevice;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditAlarmPortDeviceListResult;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.bean.MusicMsgInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.EditAlarmPortDeviceList;
import com.itc.smartbroadcast.channels.protocolhandler.GetAlarmPortDeviceList;
import com.itc.smartbroadcast.util.StringUtils;
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


/**
 * 报警任务端口配置
 */

public class AlarmPortConfigActivity extends Base2Activity {


    public static int REQUEST_ALARM_MUSIC_CODE = 1;
    public static int REQUEST_DEVICE_CODE = 2;

    Context mContext;
    @BindView(R.id.bt_back_event)
    ImageView btBackEvent;
    @BindView(R.id.ll_done)
    LinearLayout llDone;
    @BindView(R.id.et_task_name)
    EditText etTaskName;
    @BindView(R.id.tv_select_music)
    TextView tvSelectMusic;
    @BindView(R.id.rl_select_music)
    RelativeLayout rlSelectMusic;
    @BindView(R.id.tv_alarm_response_device_info)
    TextView tvAlarmResponseDeviceInfo;
    @BindView(R.id.rl_alarm_response_device)
    RelativeLayout rlAlarmResponseDevice;
    @BindView(R.id.btn_get_device)
    Button btnGetDevice;
    @BindView(R.id.tv_alarm_response_device)
    TextView tvAlarmResponseDevice;
    @BindView(R.id.ll_alarm_response_device)
    LinearLayout llAlarmResponseDevice;
    @BindView(R.id.btn_clear_config)
    Button btnClearConfig;
    @BindView(R.id.btn_clear_config_gone)
    Button btnClearConfigGone;


    int portNum = 255;


    private AlarmDeviceDetail alarmDeviceDetail = new AlarmDeviceDetail();
    private AlarmPortDevice alarmPortDevice = new AlarmPortDevice();
    List<FoundDeviceInfo> deviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_port_config);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);

        mContext = this;

        Intent intent = getIntent();
        String alarmDeviceDetailJson = intent.getStringExtra("alarmDeviceDetail");
        alarmDeviceDetail = JSONObject.parseObject(alarmDeviceDetailJson, AlarmDeviceDetail.class);

        initView();

        EventBus.getDefault().register(this);
        //初始化数据
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initView() {

        portNum = getIntent().getIntExtra("portNum", 255);
        String alarmDeviceDetailJson = getIntent().getStringExtra("alarmDeviceDetail");
        alarmDeviceDetail = JSONObject.parseObject(alarmDeviceDetailJson, AlarmDeviceDetail.class);
        llAlarmResponseDevice.setVisibility(View.GONE);

        //权限加入
        if (TaskUtils.getIsManager()) {


        } else {
            etTaskName.setEnabled(false);
            rlSelectMusic.setEnabled(false);
            btnGetDevice.setVisibility(View.GONE);
            llDone.setVisibility(View.GONE);
            btnClearConfig.setVisibility(View.GONE);
            btnClearConfigGone.setVisibility(View.GONE);
            ToastUtil.show(mContext, "温馨提示：普通用户不能编辑信息！");
        }
    }

    private void initData() {


        AlarmPortDevice alarmPortDevice = new AlarmPortDevice();
        alarmPortDevice.setPortNum(portNum);
        //获取端口信息
        GetAlarmPortDeviceList.sendCMD(alarmDeviceDetail.getDeviceIp(), alarmPortDevice);

    }

    /**
     * 监听Back键按下事件,方法2:
     * 注意:
     * 返回值表示:是否能完全处理该事件
     * 在此处返回false,所以会继续传播该事件.
     * 在具体项目中此处的返回值视情况而定.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            back();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void back() {


        //权限加入
        if (!TaskUtils.getIsManager()){
            finish();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = View.inflate(this, R.layout.dialog_tips, null);
        final TextView tvMsg = (TextView) v.findViewById(R.id.tv_msg);
        final Button btnOk = (Button) v.findViewById(R.id.btn_ok);
        final Button btnNo = (Button) v.findViewById(R.id.btn_no);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(v);
        tvMsg.setText("信息未保存，确定退出吗？");
        btnNo.setVisibility(View.VISIBLE);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    @OnClick({R.id.bt_back_event, R.id.ll_done, R.id.rl_select_music, R.id.rl_alarm_response_device, R.id.btn_get_device, R.id.btn_clear_config})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_back_event:
                back();
                break;
            case R.id.ll_done:
                complete();
                break;
            case R.id.rl_select_music:
                getAlarmMusicDevice();
                break;
            case R.id.rl_alarm_response_device:
                if (llAlarmResponseDevice.getVisibility() == View.VISIBLE) {
                    llAlarmResponseDevice.setVisibility(View.GONE);
                } else {
                    llAlarmResponseDevice.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_get_device:
                getDevice();
                break;
            case R.id.btn_clear_config:
                clearConfig();
                break;
        }
    }


    private void clearConfig() {
        alarmPortDevice.setPortName("");
        alarmPortDevice.setPortMusicName("");
        alarmPortDevice.setPortMusicPath("");
        alarmPortDevice.setDeviceCount(0);
        List<String> deviceMac = new ArrayList<>();
        alarmPortDevice.setPortDeviceMacList(deviceMac);
        EditAlarmPortDeviceList.sendCMD(alarmDeviceDetail.getDeviceIp(), alarmPortDevice);
    }

    private void complete() {

        String portName = etTaskName.getText().toString().trim();

        if (!StringUtils.checkName(portName)) {
            ToastUtil.show(mContext, "端口名称必须是汉字、字母和数字并且在2到15个字符之间！", Toast.LENGTH_SHORT);
            return;
        }
        String musicName = tvSelectMusic.getText().toString().trim();
        alarmPortDevice.setPortName(portName);
        alarmPortDevice.setPortMusicName(musicName);
        alarmPortDevice.setPortMusicPath("");
        alarmPortDevice.setDeviceCount(deviceList.size());
        List<String> deviceMac = new ArrayList<>();
        for (FoundDeviceInfo deviceInfo : deviceList) {
            deviceMac.add(deviceInfo.getDeviceMac());
        }
        alarmPortDevice.setPortDeviceMacList(deviceMac);
        EditAlarmPortDeviceList.sendCMD(alarmDeviceDetail.getDeviceIp(), alarmPortDevice);

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

        if ("getAlarmPortDeviceList".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                AlarmPortDevice alarmPortDevice = JSONObject.parseObject(data, AlarmPortDevice.class);
                this.alarmPortDevice = alarmPortDevice;

                List<String> deviceMac = alarmPortDevice.getPortDeviceMacList();

                deviceList.clear();
                String deviceListStr = AppDataCache.getInstance().getString("deviceList");
                List<FoundDeviceInfo> deviceList1 = JSONArray.parseArray(deviceListStr, FoundDeviceInfo.class);
                for (String mac : deviceMac) {
                    for (FoundDeviceInfo device : deviceList1) {
                        if (device.getDeviceMac().equals(mac)) {
                            deviceList.add(device);
                        }
                    }
                }
                String str = "";
                for (FoundDeviceInfo device : deviceList) {
                    str += device.getDeviceName() + "\n";
                }
                tvAlarmResponseDevice.setText(str);
                if (deviceList.size() > 0) {
                    tvAlarmResponseDeviceInfo.setText("已配置");
                } else {
                    tvAlarmResponseDeviceInfo.setText("未配置");
                }
                setData();
            }
        }
        if ("editAlarmPortDeviceList".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                EditAlarmPortDeviceListResult editAlarmPortDeviceListResult = JSONObject.parseObject(data, EditAlarmPortDeviceListResult.class);
                if (editAlarmPortDeviceListResult.getResult() == 1) {
                    ToastUtil.show(mContext, "配置成功！");
                    //获取端口信息
                    GetAlarmPortDeviceList.sendCMD(alarmDeviceDetail.getDeviceIp(), alarmPortDevice);
                    this.finish();
                }else{
                    ToastUtil.show(mContext, "配置失败！");
                }
            }
        }
    }


    public void getAlarmMusicDevice() {
        Intent intent = new Intent(mContext, ChooseAlarmMusicActivity.class);
        intent.putExtra("ip", alarmDeviceDetail.getDeviceIp());
        intent.putExtra("selectMusic", tvSelectMusic.getText().toString().trim());
        startActivityForResult(intent, REQUEST_ALARM_MUSIC_CODE);
    }


    public void getDevice() {
        Intent intent = new Intent(mContext, ChoosePlayTerminalActivityToAlarm.class);
        Gson gson = new Gson();
        String deviceListJson = gson.toJson(deviceList);
        intent.putExtra("deviceList", deviceListJson);
        intent.putExtra("alarm", "alarm");
        startActivityForResult(intent, REQUEST_DEVICE_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_ALARM_MUSIC_CODE == requestCode) {       //获取音乐

            String musicStr = data.getStringExtra("music");
            if (!musicStr.equals("")) {
                MusicMsgInfo musicMsgInfo = JSONObject.parseObject(musicStr, MusicMsgInfo.class);
                tvSelectMusic.setText(musicMsgInfo.getMusicName());
            }
        }

        if (REQUEST_DEVICE_CODE == requestCode) {       //获取设备

            String deviceListStr = data.getStringExtra("deviceList");
            if (!deviceListStr.equals("")) {
                deviceList.clear();
                Log.i("result", "onActivityResult: " + deviceListStr);
                deviceList.addAll(JSONArray.parseArray(deviceListStr, FoundDeviceInfo.class));
                String str = "";
                for (FoundDeviceInfo device : deviceList) {
                    str += device.getDeviceName() + "\n";
                }
                tvAlarmResponseDevice.setText(str);
                if (deviceList.size() > 0) {
                    tvAlarmResponseDeviceInfo.setText("已配置");
                } else {
                    tvAlarmResponseDeviceInfo.setText("未配置");
                }
            }
        }
    }


    private void setData() {
        tvSelectMusic.setText(alarmPortDevice.getPortMusicName());
        if (alarmPortDevice.getPortName() == null || alarmPortDevice.getPortName().equals("")){
            etTaskName.setText("端口"+portNum);
        }else{
            etTaskName.setText(alarmPortDevice.getPortName());
        }


        //权限加入
        if (TaskUtils.getIsManager()) {


        } else {
            btnClearConfigGone.setVisibility(View.GONE);
            btnClearConfig.setVisibility(View.GONE);
            return;
        }

        if (tvSelectMusic.getText().toString().equals("") && etTaskName.getText().toString().equals("") && tvAlarmResponseDeviceInfo.getText().toString().equals("未配置")) {
            btnClearConfigGone.setVisibility(View.VISIBLE);
            btnClearConfig.setVisibility(View.GONE);
        } else {
            btnClearConfigGone.setVisibility(View.GONE);
            btnClearConfig.setVisibility(View.VISIBLE);
        }
    }
}
