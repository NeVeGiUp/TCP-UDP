package com.itc.smartbroadcast.activity.event;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.adapter.TerminalStatusAdapter;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.CollectorInstantStatus;
import com.itc.smartbroadcast.bean.DeviceControlResult;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.bean.GetInstallTaskEndResult;
import com.itc.smartbroadcast.bean.InstantTask;
import com.itc.smartbroadcast.bean.InstantTaskDetail;
import com.itc.smartbroadcast.bean.OperateInstantTaskResult;
import com.itc.smartbroadcast.bean.TerminalDeviceStatus;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.DeviceControl;
import com.itc.smartbroadcast.channels.protocolhandler.GetInstantTaskDetail;
import com.itc.smartbroadcast.channels.protocolhandler.GetInstantTaskList;
import com.itc.smartbroadcast.channels.protocolhandler.GetTerminalDeviceStatus;
import com.itc.smartbroadcast.channels.protocolhandler.OperateInstantTask;
import com.itc.smartbroadcast.util.ToastUtil;
import com.itc.smartbroadcast.widget.custom.VerticalSeekBar;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.droidsonroids.gif.GifImageView;

/**
 * @Content :音频采集器
 * @Author : lik
 * @Time : 18-9-12 下午5:24
 */
public class CollectorPlayerActivity extends AppCompatActivity {


    String instantTaskJson = null;
    InstantTask instantTask = new InstantTask();
    @BindView(R.id.bt_back)
    ImageView btBack;
    @BindView(R.id.tv_task_name)
    TextView tvTaskName;
    @BindView(R.id.iv_edit_task)
    ImageView ivEditTask;
    @BindView(R.id.tv_collector_name)
    TextView tvCollectorName;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.sb_volume)
    VerticalSeekBar sbVolume;
    @BindView(R.id.iv_volume)
    ImageView ivVolume;
    @BindView(R.id.tv_volume)
    TextView tvVolume;
    @BindView(R.id.btn_start_task)
    Button btnStartTask;
    @BindView(R.id.ll_terminal_status)
    LinearLayout llTerminalStatus;
    @BindView(R.id.ll_volume_mute)
    LinearLayout llVolumeMute;
    @BindView(R.id.iv_status_stop)
    ImageView ivStatusStop;
    @BindView(R.id.iv_status_start)
    GifImageView ivStatusStart;
    private Context context;

    int isPlay = 0;
    boolean isController = false;
    boolean isOnline = false;

    List<TerminalDeviceStatus> terminalDeviceStatusList = new ArrayList<>();

    CollectorInstantStatus collectorInstantStatus = new CollectorInstantStatus();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置不能横屏，防止生命周期的改变
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_collector_player);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);
        context = this;


        initData();

        ivPlay.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int alpha = ivPlay.getImageAlpha();
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    //重新设置按下时的背景图片
                    ivPlay.setColorFilter(getResources().getColor(R.color.colorMain));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //再修改为抬起时的正常图片
                    ivPlay.setColorFilter(Color.BLACK);
                }
                return false;
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        initStatus(0);
    }

    private void initStatus(int type) {

        int userNum = AppDataCache.getInstance().getInt("userNum");
        String deviceListStr = AppDataCache.getInstance().getString("deviceList");
        List<FoundDeviceInfo> deviceList = JSONArray.parseArray(deviceListStr, FoundDeviceInfo.class);
        FoundDeviceInfo foundDeviceInfo = new FoundDeviceInfo();
        for (FoundDeviceInfo device : deviceList) {
            if (instantTask.getTerminalMac().equals(device.getDeviceMac())) {
                foundDeviceInfo = device;
            }
        }
        isPlay = 0;
        ivPlay.setImageDrawable(getResources().getDrawable(R.mipmap.jishi_but_stop_default));
        ivStatusStop.setVisibility(View.VISIBLE);
        ivStatusStart.setVisibility(View.GONE);

        if (foundDeviceInfo.getDeviceStatus().equals("在线") && instantTask.getStatus() == 1) {
            isController = true;
        } else {
            isController = false;
        }
        if (foundDeviceInfo.getDeviceStatus().equals("在线")) {
            isOnline = true;
        } else {
            isOnline = false;
        }

        if (isController) {
            DeviceControl.sendCMD(userNum, foundDeviceInfo, 18, 1, instantTask.getTaskNum());
        } else {
            if (type == 0)
                ToastUtil.show(context, "当前任务未启动或者该设备处于离线中...");
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }


    @OnClick({R.id.bt_back, R.id.iv_edit_task, R.id.iv_play, R.id.btn_start_task, R.id.ll_terminal_status, R.id.ll_volume_mute})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_back:
                finish();
                break;
            case R.id.iv_edit_task:
                Intent intent = new Intent(context, EditInstantTaskActivity.class);
                intent.putExtra("instantTask", instantTaskJson);
                startActivity(intent);
                finish();
                break;
            case R.id.iv_play:
                if (isController) {
                    play();
                } else {
                    ToastUtil.show(context, "当前任务未启动或者该设备处于离线中...");
                }
                break;
            case R.id.btn_start_task:
                if (isOnline) {
                    taskControl();
                } else {
                    ToastUtil.show(context, "当前设备处于离线中...");
                }
                break;

            case R.id.ll_terminal_status:   //终端状态
                if (isController) {
                    String deviceListStr = AppDataCache.getInstance().getString("deviceList");
                    List<FoundDeviceInfo> deviceList = JSONArray.parseArray(deviceListStr, FoundDeviceInfo.class);
                    FoundDeviceInfo foundDeviceInfo = new FoundDeviceInfo();
                    for (FoundDeviceInfo device : deviceList) {
                        if (instantTask.getTerminalMac().equals(device.getDeviceMac())) {
                            foundDeviceInfo = device;
                        }
                    }
                    GetTerminalDeviceStatus.sendCMD(foundDeviceInfo.getDeviceIp());
                } else {
                    ToastUtil.show(context, "当前任务未启动或者该设备处于离线中...");
                }
                break;
            case R.id.ll_volume_mute:
                setVolume(0);
                ivVolume.setImageDrawable(context.getResources().getDrawable(R.mipmap.jishi_icon_yinliang_close));
                tvVolume.setText("0");
                sbVolume.setProgress(0);
                break;
        }
    }


    private void initData() {
        instantTaskJson = getIntent().getStringExtra("instantTask");
        Gson gson = new Gson();
        instantTask = gson.fromJson(instantTaskJson, InstantTask.class);

        tvTaskName.setText(instantTask.getTaskName());

        ivPlay.setImageDrawable(getResources().getDrawable(R.mipmap.jishi_but_stop_default));
        ivStatusStop.setVisibility(View.VISIBLE);
        ivStatusStart.setVisibility(View.GONE);

        InstantTaskDetail instantTaskDetail = new InstantTaskDetail();
        instantTaskDetail.setTaskNum(instantTask.getTaskNum());
        GetInstantTaskDetail.sendCMD(AppDataCache.getInstance().getString("loginIp"), instantTaskDetail);


        if (instantTask.getStatus() == 0) {
            btnStartTask.setText("启动任务");
        } else {
            btnStartTask.setText("停止任务");
        }


        sbVolume.getProgressDrawable().setColorFilter(context.getResources().getColor(R.color.color_seek_bar), PorterDuff.Mode.SRC_ATOP);//设置进度条颜色、样式

        sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                int position = sbVolume.getProgress();

                if (isController) {
                    setVolume(position);
                    tvVolume.setText(position + "");
                }
                if (position == 0) {
                    ivVolume.setImageDrawable(context.getResources().getDrawable(R.mipmap.jishi_icon_yinliang_close));
                } else {
                    ivVolume.setImageDrawable(context.getResources().getDrawable(R.mipmap.jishi_icon_yinliang_open));
                }
            }
        });
    }

    private void setVolume(int volume) {
        int userNum = AppDataCache.getInstance().getInt("userNum");
        String deviceListStr = AppDataCache.getInstance().getString("deviceList");
        List<FoundDeviceInfo> deviceList = JSONArray.parseArray(deviceListStr, FoundDeviceInfo.class);
        FoundDeviceInfo foundDeviceInfo = new FoundDeviceInfo();
        for (FoundDeviceInfo device : deviceList) {
            if (instantTask.getTerminalMac().equals(device.getDeviceMac())) {
                foundDeviceInfo = device;
            }
        }
        DeviceControl.sendCMD(userNum, foundDeviceInfo, 15, volume, instantTask.getTaskNum());
    }

    private void play() {

        int userNum = AppDataCache.getInstance().getInt("userNum");
        String deviceListStr = AppDataCache.getInstance().getString("deviceList");
        List<FoundDeviceInfo> deviceList = JSONArray.parseArray(deviceListStr, FoundDeviceInfo.class);
        FoundDeviceInfo foundDeviceInfo = new FoundDeviceInfo();
        for (FoundDeviceInfo device : deviceList) {
            if (instantTask.getTerminalMac().equals(device.getDeviceMac())) {
                foundDeviceInfo = device;
            }
        }
        if (isPlay == 0) {    //当前处于停止状态
            //播放FM
            DeviceControl.sendCMD(userNum, foundDeviceInfo, 1, 1, instantTask.getTaskNum());
        } else {
            //暂停FM
            DeviceControl.sendCMD(userNum, foundDeviceInfo, 5, 1, instantTask.getTaskNum());
        }
    }

    /**
     * EventBus数据回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);

        if ("getInstallTaskEnd".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {

                GetInstallTaskEndResult getInstallTaskEndResult = gson.fromJson(data, GetInstallTaskEndResult.class);

                if (getInstallTaskEndResult.getTaskNum() == instantTask.getTaskNum())
                    GetInstantTaskList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
            }
        }


        if ("deviceControlResult".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                DeviceControlResult deviceControlResult = gson.fromJson(data, DeviceControlResult.class);
            } else {
                ToastUtil.show(this, "操作失败，请检查数据以及网络!");
            }
        }

        if ("operateInstantTaskResult".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                OperateInstantTaskResult operateInstantTaskResult = gson.fromJson(data, OperateInstantTaskResult.class);
                if (operateInstantTaskResult.getResult() == 1) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    GetInstantTaskList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
                } else {
                    ToastUtil.show(this, "操作失败，请检查数据以及网络!");
                }
            } else {
                ToastUtil.show(this, "操作失败，请检查数据以及网络!");
            }
        }

        if ("getInstantTaskList".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                AppDataCache.getInstance().putString("getInstantTaskList", data);
                List<InstantTask> instantTaskList = JSONArray.parseArray(data, InstantTask.class);

                for (InstantTask instantTask : instantTaskList) {
                    if (instantTask.getTaskNum() == this.instantTask.getTaskNum()) {
                        this.instantTask = instantTask;
                    }
                }

                if (instantTask.getStatus() == 0) {
                    btnStartTask.setText("启动任务");
                } else {
                    btnStartTask.setText("停止任务");
                }

                initStatus(1);

            } else {

            }
        }


        if ("getCollectorInstantStatus".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                collectorInstantStatus = gson.fromJson(data, CollectorInstantStatus.class);

                if (collectorInstantStatus.getDeviceMac().equals(instantTask.getTerminalMac()) && collectorInstantStatus.getTaskNum() == instantTask.getTaskNum()) {
                    int playStatus = collectorInstantStatus.getPlayStatus();
                    if (playStatus == 2) {      //暂停状态
                        isPlay = 0;
                        ivPlay.setImageDrawable(getResources().getDrawable(R.mipmap.jishi_but_stop_default));
                        ivStatusStop.setVisibility(View.VISIBLE);
                        ivStatusStart.setVisibility(View.GONE);
                    }
                    if (playStatus == 1) {      //播放状态
                        isPlay = 1;
                        ivPlay.setImageDrawable(getResources().getDrawable(R.mipmap.jishi_but_play_default));
                        ivStatusStop.setVisibility(View.GONE);
                        ivStatusStart.setVisibility(View.VISIBLE);
                    }
                    sbVolume.setProgress(collectorInstantStatus.getDeviceVolume());
                    tvVolume.setText(collectorInstantStatus.getDeviceVolume() + "");
                    if (collectorInstantStatus.getDeviceVolume() == 0) {
                        ivVolume.setImageDrawable(context.getResources().getDrawable(R.mipmap.jishi_icon_yinliang_close));
                    } else {
                        ivVolume.setImageDrawable(context.getResources().getDrawable(R.mipmap.jishi_icon_yinliang_open));
                    }
                    if (collectorInstantStatus.getDeviceVolume() == 128) {
                        tvVolume.setText("默认");
                    }
                }
            } else {
                ToastUtil.show(this, "操作失败，请检查数据以及网络!");
            }
        }

        if ("getTerminalDeviceStatusList".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                terminalDeviceStatusList = JSONArray.parseArray(data, TerminalDeviceStatus.class);
                showPopWindowTerminal();
            } else {
                ToastUtil.show(this, "操作失败，请检查数据以及网络!");
            }
        }
    }

    /**
     * showPopWindowTerminal
     */
    private void showPopWindowTerminal() {
        final Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
        final View contentView = LayoutInflater.from(this).inflate(R.layout.popup_terminal_device, null);
        bottomDialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.setCanceledOnTouchOutside(true);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();

        final ListView musicLv = (ListView) contentView.findViewById(R.id.lv_terminal_list);
        TextView tvTaskName = (TextView) contentView.findViewById(R.id.tv_task_name);

        tvTaskName.setText("终端状态");
        TerminalStatusAdapter terminalStatusAdapter = new TerminalStatusAdapter(context, terminalDeviceStatusList);
        musicLv.setAdapter(terminalStatusAdapter);
    }

    //控制任务的启动和停止
    private void taskControl() {
        if (instantTask.getStatus() == 0) {
            instantTask.setStatus(1);
        } else {
            instantTask.setStatus(0);
        }
        int userNum = AppDataCache.getInstance().getInt("userNum");
        OperateInstantTask.sendCMD(AppDataCache.getInstance().getString("loginIp"), instantTask, userNum, instantTask.getStatus());
    }

}
