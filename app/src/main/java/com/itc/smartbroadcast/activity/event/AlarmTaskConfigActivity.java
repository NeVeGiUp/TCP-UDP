package com.itc.smartbroadcast.activity.event;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.event.utils.TaskUtils;
import com.itc.smartbroadcast.base.Base2Activity;
import com.itc.smartbroadcast.bean.AlarmDeviceDetail;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditAlarmDeviceResult;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.bean.MusicMsgInfo;
import com.itc.smartbroadcast.channels.protocolhandler.EditAlarmDevice;
import com.itc.smartbroadcast.channels.protocolhandler.GetAlarmDeviceDetail;
import com.itc.smartbroadcast.util.ToastUtil;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AlarmTaskConfigActivity extends Base2Activity {

    @BindView(R.id.bt_back_event)
    ImageView btBackEvent;
    @BindView(R.id.tv_alarm_name)
    TextView tvAlarmName;
    @BindView(R.id.tv_alarm_ip)
    TextView tvAlarmIp;
    @BindView(R.id.tv_play_mode)
    TextView tvPlayMode;
    @BindView(R.id.rl_play_mode)
    RelativeLayout rlPlayMode;
    @BindView(R.id.tv_alarm_response_mode)
    TextView tvAlarmResponseMode;
    @BindView(R.id.rl_alarm_response_mode)
    RelativeLayout rlAlarmResponseMode;
    @BindView(R.id.tv_alarm_trigger_mode)
    TextView tvAlarmTriggerMode;
    @BindView(R.id.rl_alarm_trigger_mode)
    RelativeLayout rlAlarmTriggerMode;
    @BindView(R.id.tv_alarm_port_count)
    TextView tvAlarmPortCount;
    @BindView(R.id.rl_alarm_port_count)
    RelativeLayout rlAlarmPortCount;

    Context mContext;
    @BindView(R.id.tv_volume)
    TextView tvVolume;
    @BindView(R.id.bt_volume)
    RelativeLayout btVolume;


    private AlarmDeviceDetail alarmDeviceDetail = new AlarmDeviceDetail();

    public static int REQUEST_VOLUME_CODE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmtaskconfig);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);

        mContext = this;

        Intent intent = getIntent();
        String alarmDeviceDetailJson = intent.getStringExtra("alarmDeviceDetail");
        alarmDeviceDetail = JSONObject.parseObject(alarmDeviceDetailJson, AlarmDeviceDetail.class);

        initView();
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

    public void getVolume() {
        Intent intent = new Intent(this, SelectVolumeActivityToAlarm.class);
        intent.putExtra("volume", tvVolume.getText().toString().trim());
        startActivityForResult(intent, REQUEST_VOLUME_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_VOLUME_CODE == requestCode) {       //获取音量
            String volume = data.getStringExtra("volume");

            if (!volume.equals("back")){
                String volume1 = "";
                if (volume.equals("") || volume.equals("0")) {
                    volume1 = "";
                } else {
                    volume1 = volume;
                }
                if (volume1 == null || volume1.equals("")) {
                    volume = "128";
                }

                alarmDeviceDetail.setPlayVolume(Integer.parseInt(volume));
                EditAlarmDevice.sendCMD(alarmDeviceDetail.getDeviceIp(), alarmDeviceDetail);
            }

        }
    }

    private void initView() {

        tvAlarmName.setText(alarmDeviceDetail.getDeviceName());
        tvAlarmIp.setText(alarmDeviceDetail.getDeviceIp());
        tvAlarmPortCount.setText(alarmDeviceDetail.getPortCount() + "");
        if (alarmDeviceDetail.getPlayMode() == 0) {
            tvPlayMode.setText("单曲循环");
        } else if (alarmDeviceDetail.getPlayMode() == 1) {
            tvPlayMode.setText("单曲播放");
        }

        if (alarmDeviceDetail.getPlayVolume() == 128) {
            tvVolume.setText("");
        } else {
            tvVolume.setText(alarmDeviceDetail.getPlayVolume() + "");
        }

        if (alarmDeviceDetail.getTriggerMode() == 0) {
            tvAlarmTriggerMode.setText("自动解除");
        } else if (alarmDeviceDetail.getTriggerMode() == 1) {
            tvAlarmTriggerMode.setText("手动解除");
        }
        switch (alarmDeviceDetail.getPortResponseMode()) {
            case 0:
                tvAlarmResponseMode.setText("单区报警");
                break;
            case 1:
                tvAlarmResponseMode.setText("邻区+1报警");
                break;
            case 2:
                tvAlarmResponseMode.setText("邻区+2报警");
                break;
            case 3:
                tvAlarmResponseMode.setText("邻区+3报警");
                break;
            case 4:
                tvAlarmResponseMode.setText("邻区+4报警");
                break;
            case 5:
                tvAlarmResponseMode.setText("全区报警");
                break;
        }
    }


    @OnClick({R.id.bt_back_event, R.id.rl_play_mode, R.id.rl_alarm_response_mode, R.id.rl_alarm_trigger_mode, R.id.rl_alarm_port_count, R.id.bt_volume})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_back_event:
                finish();
                break;

            case R.id.bt_volume:
                if (TaskUtils.getIsManager()) {
                    getVolume();
                } else {
                    ToastUtil.show(mContext, "对不起，普通用户没有该功能权限！");
                }
                break;
            case R.id.rl_play_mode:     //播放模式
                if (TaskUtils.getIsManager()) {
                    editPlayMode(view);
                } else {
                    ToastUtil.show(mContext, "对不起，普通用户没有该功能权限！");
                }
                break;
            case R.id.rl_alarm_response_mode:   //报警响应模式
                if (TaskUtils.getIsManager()) {
                    editAlarmResponseMode(view);
                } else {
                    ToastUtil.show(mContext, "对不起，普通用户没有该功能权限！");
                }
                break;
            case R.id.rl_alarm_trigger_mode:    //解除报警模式
                if (TaskUtils.getIsManager()) {
                    editAlarmTriggerMode(view);
                } else {
                    ToastUtil.show(mContext, "对不起，普通用户没有该功能权限！");
                }
                break;
            case R.id.rl_alarm_port_count:  //响应设备
                Intent intent = new Intent(AlarmTaskConfigActivity.this, PortConfigActivity.class);
                String alarmDeviceDetailJson = JSONObject.toJSONString(alarmDeviceDetail);
                intent.putExtra("alarmDeviceDetail", alarmDeviceDetailJson);
                mContext.startActivity(intent);
                break;
        }
    }

    /**
     * 配置报警解除模式
     *
     * @param view
     */
    private void editAlarmTriggerMode(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        CharSequence[] item = {"自动解除", "手动解除"};
        builder.setTitle("解除报警模式")
                .setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alarmDeviceDetail.setTriggerMode(i);
                        EditAlarmDevice.sendCMD(alarmDeviceDetail.getDeviceIp(), alarmDeviceDetail);
                    }
                })
                .show();
    }

    /**
     * 配置报警响应模式
     *
     * @param view
     */
    private void editAlarmResponseMode(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        CharSequence[] item = {"单区报警", "邻区+1报警", "邻区+2报警", "邻区+3报警", "邻区+4报警", "全区报警"};
        builder.setTitle("报警响应模式")
                .setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alarmDeviceDetail.setPortResponseMode(i);
                        EditAlarmDevice.sendCMD(alarmDeviceDetail.getDeviceIp(), alarmDeviceDetail);
                    }
                })
                .show();
    }

    /**
     * 编辑播放模式
     */
    private void editPlayMode(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        CharSequence[] item = {"单曲循环", "单曲播放"};
        builder.setTitle("播放模式")
                .setItems(item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alarmDeviceDetail.setPlayMode(i);
                        EditAlarmDevice.sendCMD(alarmDeviceDetail.getDeviceIp(), alarmDeviceDetail);
                    }
                })
                .show();
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
        if ("editAlarmDeviceResult".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                EditAlarmDeviceResult editAlarmDeviceResult = JSONObject.parseObject(data, EditAlarmDeviceResult.class);
                if (editAlarmDeviceResult.getResult() == 1) {
                    ToastUtil.show(mContext, "配置成功！");
                    initData();
                } else {
                    ToastUtil.show(mContext, "配置成功！");
                }
            }
        }
    }
}
