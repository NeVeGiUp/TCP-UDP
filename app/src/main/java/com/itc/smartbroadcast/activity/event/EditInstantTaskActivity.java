package com.itc.smartbroadcast.activity.event;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bigkoo.pickerview.TimePickerView;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.event.utils.TaskUtils;
import com.itc.smartbroadcast.activity.music.ChooseMusicFolderActivityToTask;
import com.itc.smartbroadcast.base.Base2Activity;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditInstantTaskResult;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.bean.InstantTask;
import com.itc.smartbroadcast.bean.InstantTaskDetail;
import com.itc.smartbroadcast.bean.MusicMsgInfo;
import com.itc.smartbroadcast.bean.OperateInstantTaskResult;
import com.itc.smartbroadcast.bean.TaskDetail;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.EditInstantTask;
import com.itc.smartbroadcast.channels.protocolhandler.GetInstantTaskDetail;
import com.itc.smartbroadcast.channels.protocolhandler.OperateInstantTask;
import com.itc.smartbroadcast.util.StringUtils;
import com.itc.smartbroadcast.util.ToastUtil;
import com.itc.smartbroadcast.widget.custom.CommonProgressDialog;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * create by youmu on 2018/7
 */
public class EditInstantTaskActivity extends Base2Activity {


    public static int REQUEST_MUSIC_CODE = 1;
    public static int REQUEST_PLAY_MODE_CODE = 2;
    public static int REQUEST_DEVICE_CODE = 3;
    public static int REQUEST_VOLUME_CODE = 4;
    public static int REQUEST_WEEK_CODE = 5;
    public static int REQUEST_KEY_CODE = 6;
    public static int REQUEST_SOURCE_DEVICE_CODE = 7;
    public static int REQUEST_PRIORITY_CODE = 8;

    @BindView(R.id.bt_back_event)
    ImageView btBackEvent;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.tv_complete)
    TextView tvComplete;
    @BindView(R.id.et_task_name)
    EditText etTaskName;
    @BindView(R.id.tv_continued_time)
    TextView tvContinuedTime;
    @BindView(R.id.bt_continued_time)
    RelativeLayout btContinuedTime;
    @BindView(R.id.tv_remote_control_key)
    TextView tvRemoteControlKey;
    @BindView(R.id.bt_remote_control_key)
    RelativeLayout btRemoteControlKey;
    @BindView(R.id.tv_sound_source)
    TextView tvSoundSource;
    @BindView(R.id.bt_sound_source)
    RelativeLayout btSoundSource;
    @BindView(R.id.bt_select_terminal)
    RelativeLayout btSelectTerminal;
    @BindView(R.id.btn_get_device)
    Button btnGetDevice;
    @BindView(R.id.tv_select_device)
    TextView tvSelectDevice;
    @BindView(R.id.ll_selected_terminal)
    LinearLayout llSelectedTerminal;
    @BindView(R.id.tv_volume)
    TextView tvVolume;
    @BindView(R.id.bt_volume)
    RelativeLayout btVolume;
    @BindView(R.id.btn_task_control)
    Button btnTaskControl;
    @BindView(R.id.tv_select_terminal)
    TextView tvSelectTerminal;
    @BindView(R.id.tv_priority)
    TextView tvPriority;
    @BindView(R.id.bt_priority)
    RelativeLayout btPriority;
    @BindView(R.id.btn_clear_device)
    Button btnClearDevice;

    private Context context;
    private TimePickerView timePickerViewStart;
    private TimePickerView timePickerViewContinue;
    private SimpleDateFormat formattype = new SimpleDateFormat("HH小时mm分ss秒");
    private SimpleDateFormat formattype2 = new SimpleDateFormat("HH:mm:ss");
    private SimpleDateFormat formattype3 = new SimpleDateFormat("yyyy-MM-dd");
    private CommonProgressDialog progressDialog;

    private TimePickerView timePickerView;

    private ArrayList<String> taskDateDuplicationPatternList = new ArrayList<>();
    private int[] WeekDuplicationPatternList = new int[7];

    List<MusicMsgInfo> musicList = new ArrayList<>();
    List<FoundDeviceInfo> deviceList = new ArrayList<>();
    FoundDeviceInfo foundDeviceInfo = new FoundDeviceInfo();

    String instantTaskJson = null;
    InstantTask instantTask = new InstantTask();

    int controlStatus = 1;


    private boolean isUpdate = false;
    private String taskName = "";

    private Handler controlHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1 && controlStatus == 1) {
                ToastUtil.show(context, "连接超时，该设备不在线！");
            }
        }
    };

    Thread controlThread;

    private final static int COMPLETE = 5;
    private final static long SLEEP_TIME = 300;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case COMPLETE:
                    tvComplete.setEnabled(true);
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_instant_task);
        ButterKnife.bind(this);
        StatusBarUtil.setTransparent(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);
        //默认不开启键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        EventBus.getDefault().register(this);
        context = this;
        initView();
        initData();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void initData() {
        instantTaskJson = getIntent().getStringExtra("instantTask");
        Gson gson = new Gson();
        instantTask = gson.fromJson(instantTaskJson, InstantTask.class);


        int userNum = instantTask.getAccountNum();
        int selfNum = TaskUtils.getUserNum();
        if (TaskUtils.getIsManager()) {
        } else {
            if (userNum != selfNum) {

                etTaskName.setEnabled(false);
                btContinuedTime.setEnabled(false);
                btRemoteControlKey.setEnabled(false);
                btSoundSource.setEnabled(false);
                btnGetDevice.setVisibility(View.GONE);
                btVolume.setEnabled(false);
                btnTaskControl.setVisibility(View.GONE);
                tvComplete.setVisibility(View.GONE);
                btnClearDevice.setVisibility(View.GONE);

                ToastUtil.show(context, "温馨提示：该用户没有编辑本条任务的权限！");
            }
        }

        etTaskName.setText(instantTask.getTaskName());

        taskName = instantTask.getTaskName();

        //持续时间
        int continueTime = instantTask.getContinueDate();
        int hour = (continueTime / (60 * 60)) % 24;
        int minute = (continueTime / 60) % 60;
        int second = continueTime % 60;
        tvContinuedTime.setText(hour + ":" + minute + ":" + second);
        tvRemoteControlKey.setText(instantTask.getRemoteControlKeyInfo() + "");
        String deviceListStr = AppDataCache.getInstance().getString("deviceList");
        List<FoundDeviceInfo> deviceList = JSONArray.parseArray(deviceListStr, FoundDeviceInfo.class);
        for (FoundDeviceInfo device : deviceList) {
            if (instantTask.getTerminalMac().equals(device.getDeviceMac())) {
                foundDeviceInfo = device;
            }
        }
        tvSoundSource.setText(foundDeviceInfo.getDeviceName());

        //播放模式
        String priority = "";
        switch (instantTask.getPriority()) {
            case 0:
                priority = "普通";
                break;
            case 16:
                priority = "高级";
                break;
            case 32:
                priority = "重要";
                break;
            case 48:
                priority = "紧急";
                break;
        }
        tvPriority.setText(priority);

        if (instantTask.getVolume() == 128) {
            tvVolume.setText("");
        } else {
            tvVolume.setText(instantTask.getVolume() + "");
        }

        if (instantTask.getStatus() == 1) {
            btnTaskControl.setText("停止任务");
        } else {
            btnTaskControl.setText("启动任务");
        }

        taskName = etTaskName.getText().toString().trim();

        InstantTaskDetail instantTaskDetail = new InstantTaskDetail();
        instantTaskDetail.setTaskNum(instantTask.getTaskNum());
        GetInstantTaskDetail.sendCMD(AppDataCache.getInstance().getString("loginIp"), instantTaskDetail);
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

        if ("editInstantTaskResult".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                EditInstantTaskResult editInstantTaskResult = gson.fromJson(data, EditInstantTaskResult.class);
                if (editInstantTaskResult.getResult() == 1) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ToastUtil.show(this, "操作成功!");
                    this.finish();
                } else {
                    ToastUtil.show(this, "操作失败，请检查数据以及网络!");
                }
            } else {
                ToastUtil.show(this, "操作失败，请检查数据以及网络!");
            }
        }
        if ("operateInstantTaskResult".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {

                controlStatus = 0;
                OperateInstantTaskResult operateInstantTaskResult = gson.fromJson(data, OperateInstantTaskResult.class);
                if (operateInstantTaskResult.getResult() == 1) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ToastUtil.show(this, "操作成功!");

                    this.finish();
                } else {
                    ToastUtil.show(this, "操作失败，请检查数据以及网络!");
                }
            } else {
                ToastUtil.show(this, "操作失败，请检查数据以及网络!");
            }
        }
        //获取即时任务详情
        if ("instantTaskDetail".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                String deviceListJson = AppDataCache.getInstance().getString("deviceList");
                List<FoundDeviceInfo> deviceList = JSONArray.parseArray(deviceListJson, FoundDeviceInfo.class);
                InstantTaskDetail instantTaskDetail = gson.fromJson(data, InstantTaskDetail.class);
                //获取设备列表
//                deviceList.clear();
                String deviceListStr = "";
                for (InstantTaskDetail.Device device : instantTaskDetail.getDevicesList()) {

                    FoundDeviceInfo foundDeviceInfo = new FoundDeviceInfo();
                    for (FoundDeviceInfo deviceInfo : deviceList) {
                        if (device.getDeviceMac().equals(deviceInfo.getDeviceMac())) {
                            foundDeviceInfo = deviceInfo;
                            foundDeviceInfo.setDeviceZone(device.getDeviceZoneMsg());
                        }
                    }

                    foundDeviceInfo.setDeviceMac(device.getDeviceMac());
                    if (foundDeviceInfo.getDeviceName() == null || foundDeviceInfo.getDeviceName().equals("")) {
                        deviceListStr += foundDeviceInfo.getDeviceMac() + "(该设备已被删除)\n";
                    } else {
                        deviceListStr += foundDeviceInfo.getDeviceName() + "\n";
                    }

                    this.deviceList.add(foundDeviceInfo);
                }

                tvSelectTerminal.setText(this.deviceList.size() + "");
                tvSelectDevice.setText(deviceListStr);
            }
        }
    }


    public void getMusic() {
        Intent intent = new Intent(context, ChooseMusicFolderActivityToTask.class);
        startActivityForResult(intent, REQUEST_MUSIC_CODE);
    }

    public void getPlayMode() {
        Intent intent = new Intent(context, PlayModeActivity.class);
        startActivityForResult(intent, REQUEST_PLAY_MODE_CODE);
    }

    public void getDevice() {
        Intent intent = new Intent(context, ChoosePlayTerminalActivity.class);
        Gson gson = new Gson();
        String deviceListJson = gson.toJson(deviceList);
        intent.putExtra("deviceList", deviceListJson);
        startActivityForResult(intent, REQUEST_DEVICE_CODE);
    }

    public void getSoundSourceDevice() {
        Intent intent = new Intent(context, ChooseSoundSourceTerminalActivity.class);
        intent.putExtra("foundDeviceInfo", JSONObject.toJSONString(foundDeviceInfo));
        startActivityForResult(intent, REQUEST_SOURCE_DEVICE_CODE);
    }

    /**
     * 获取优先级
     */
    public void getPriority() {
        Intent intent = new Intent(context, SelectPriorityActivityToInstantTask.class);
        intent.putExtra("priority", tvPriority.getText().toString());
        startActivityForResult(intent, REQUEST_PRIORITY_CODE);
    }

    public void getVolume() {
        Intent intent = new Intent(context, SelectVolumeActivity.class);
        intent.putExtra("volume", tvVolume.getText().toString().trim());
        startActivityForResult(intent, REQUEST_VOLUME_CODE);
    }

    public void getKey() {
        Intent intent = new Intent(context, SelectRemoteControlKeyActivity.class);
        startActivityForResult(intent, REQUEST_KEY_CODE);
    }

    //获取重复日期
    private void getWeekDate() {
        Intent intent = new Intent(context, SelectWeekActivity.class);
        startActivityForResult(intent, REQUEST_WEEK_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_VOLUME_CODE == requestCode) {       //获取音量
            String volume = data.getStringExtra("volume");
            if (volume.equals("") || volume.equals("0")) {
                tvVolume.setText("");
            } else {
                tvVolume.setText(volume);
            }
        }
        if (REQUEST_KEY_CODE == requestCode) {       //获取遥控按键
            String key = data.getStringExtra("key");
            if (!key.equals("")) {
                tvRemoteControlKey.setText(key);
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
                tvSelectDevice.setText(str);
            }
            tvSelectTerminal.setText(deviceList.size() + "");
        }

        if (REQUEST_PRIORITY_CODE == requestCode) {       //获取优先级
            String priority = data.getStringExtra("priority");
            if (!priority.equals("")) {
                tvPriority.setText(priority);
            }
        }

        if (REQUEST_SOURCE_DEVICE_CODE == requestCode) {       //获取音源设备
            String deviceStr = data.getStringExtra("device");
            if (!deviceStr.equals("")) {
                Gson gson = new Gson();
                foundDeviceInfo = gson.fromJson(deviceStr, FoundDeviceInfo.class);
                tvSoundSource.setText(foundDeviceInfo.getDeviceName());
            }
        }
    }

    public void complete() {

        String taskName = etTaskName.getText().toString().trim();
        if (!StringUtils.checkName(taskName)) {
            ToastUtil.show(context, "任务名称必须是汉字、字母和数字并且在2到15个字符之间！", Toast.LENGTH_SHORT);
            return;
        }

        instantTask.setAccountNum(TaskUtils.getUserNum());

        //设置即时任务名称
        instantTask.setTaskName(taskName);
        //设置持续时间
        String continueTime = tvContinuedTime.getText().toString().trim();
        if (continueTime == null || continueTime.equals("")) {
            ToastUtil.show(context, "请选择持续时间！");
            return;
        }
        if (continueTime.equals("00:00:00")) {
            ToastUtil.show(context, "持续时间不能为0！");
            return;
        }
        int h = Integer.parseInt(continueTime.split(":")[0]);
        int m = Integer.parseInt(continueTime.split(":")[1]);
        int s = Integer.parseInt(continueTime.split(":")[2]);
        int continueDate = (h * 60 * 60) + (m * 60) + s;
        instantTask.setContinueDate(continueDate);

        //设置遥控按键
        String key = "255";
        if (key == null || key.equals("")) {
            ToastUtil.show(context, "请填写遥控按键！");
            return;
        }
        instantTask.setRemoteControlKeyInfo(Integer.parseInt(key));
        //设置音源设备
        if (foundDeviceInfo.getDeviceMac() == null || foundDeviceInfo.getDeviceMac().equals("")) {
            ToastUtil.show(context, "请选择音源设备！");
            return;
        }

        if (deviceList == null || deviceList.size() == 0) {
            ToastUtil.show(context, "请选择终端！");
            return;
        }

        instantTask.setTerminalMac(foundDeviceInfo.getDeviceMac());
        //设置音量
        String volume = tvVolume.getText().toString().trim();
        //设置任务优先级
        int priority = 16;
        switch (tvPriority.getText().toString().trim()) {
            case "普通":
                priority = 0;
                break;
            case "高级":
                priority = 16;
                break;
            case "重要":
                priority = 32;
                break;
            case "紧急":
                priority = 48;
                break;
            default:
                priority = 16;
                break;
        }
        instantTask.setPriority(priority);

        List<InstantTaskDetail.Device> deviceList = new ArrayList<>();
        for (FoundDeviceInfo foundDeviceInfo2 : this.deviceList) {
            InstantTaskDetail.Device device = new InstantTaskDetail.Device();
            if (foundDeviceInfo2.getDeviceZone() == null || foundDeviceInfo2.getDeviceZone().length == 0) {
                int[] zone = new int[8];
                device.setDeviceZoneMsg(zone);
            } else {
                device.setDeviceZoneMsg(foundDeviceInfo2.getDeviceZone());
            }
            device.setDeviceMac(foundDeviceInfo2.getDeviceMac());
            deviceList.add(device);
        }
        InstantTaskDetail instantTaskDetail = new InstantTaskDetail();
        instantTaskDetail.setDevicesList(deviceList);
        if (volume == null || volume.equals("")) {
            volume = "128";
        }
        instantTask.setVolume(Integer.parseInt(volume));

        tvComplete.setEnabled(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(SLEEP_TIME);
                    Message msg = new Message();
                    msg.what = COMPLETE;
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        EditInstantTask.sendCMD(AppDataCache.getInstance().getString("loginIp"), instantTask, instantTaskDetail, 1);
    }


    private void taskControl() {

        instantTask.setAccountNum(TaskUtils.getUserNum());
        if (instantTask.getStatus() == 0) {
            instantTask.setStatus(1);
        } else {
            instantTask.setStatus(0);
        }
        int userNum = TaskUtils.getUserNum();
        OperateInstantTask.sendCMD(AppDataCache.getInstance().getString("loginIp"), instantTask, userNum, instantTask.getStatus());
        controlThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    Message msg = new Message();
                    msg.what = 1;
                    controlHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        controlThread.start();
    }


    private void initView() {

        llSelectedTerminal.setVisibility(View.GONE);
        btnTaskControl.setVisibility(View.VISIBLE);
    }


    //即时任务持续时间
    private void showContinueTime() {
        if (timePickerViewContinue == null) {
            timePickerViewContinue = new TimePickerView(this, TimePickerView.Type.HOURS_MINS);
            timePickerViewContinue.setTitle("");
            timePickerViewContinue.setRange(0, 23);
            try {
                if (tvContinuedTime.getText().toString().equals("")) {
                    timePickerViewContinue.setTime(formattype2.parse("00:00:00"));
                } else {
                    timePickerViewContinue.setTime(formattype2.parse(tvContinuedTime.getText().toString()));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            timePickerViewContinue.setCyclic(false);
            timePickerViewContinue.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date) {
                    String formattime = formattype2.format(date);
                    tvContinuedTime.setText(formattime);
                }
            });
        }
        if (!timePickerViewContinue.isShowing()) {
            timePickerViewContinue.show();
        } else {
            timePickerViewContinue.dismiss();
        }
    }


    public void goTo(Class<?> to) {
        Intent it = new Intent(this, to);
        startActivity(it);
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


        int userNum = instantTask.getAccountNum();
        int selfNum = TaskUtils.getUserNum();
        if (TaskUtils.getIsManager()) {
        } else {
            if (userNum != selfNum) {
                finish();
                return;
            }
        }

        String taskName = etTaskName.getText().toString().trim();

        if (!isUpdate && (taskName.equals(this.taskName))) {
            finish();
            return;
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = View.inflate(this, R.layout.dialog_tips, null);
        final TextView tvMsg = (TextView) v.findViewById(R.id.tv_msg);
        final Button btnOk = (Button) v.findViewById(R.id.btn_ok);
        final Button btnNo = (Button) v.findViewById(R.id.btn_no);
        final android.app.AlertDialog dialog = builder.create();
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


    @OnClick({R.id.bt_back_event, R.id.tv_complete, R.id.bt_continued_time, R.id.bt_remote_control_key, R.id.bt_sound_source, R.id.bt_select_terminal,
            R.id.btn_get_device, R.id.bt_volume, R.id.btn_task_control, R.id.bt_priority, R.id.btn_clear_device})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_back_event:
                back();
                break;
            case R.id.btn_clear_device:
                deviceList.clear();
                String deviceStr = "";
                tvSelectDevice.setText(deviceStr);
                tvSelectTerminal.setText("0");
                isUpdate = true;
                break;
            case R.id.tv_complete:
                complete();
                break;
            case R.id.bt_continued_time:
                showContinueTime();
                isUpdate = true;
                break;
            case R.id.bt_remote_control_key:
                getKey();
                isUpdate = true;
                break;
            case R.id.bt_priority:
                if (TaskUtils.getIsManager()) {
                    getPriority();
                } else {
                    ToastUtil.show(context, "普通用户只能使用默认普通!");
                }
                isUpdate = true;
                break;
            case R.id.bt_sound_source:
                getSoundSourceDevice();
                isUpdate = true;
                break;
            case R.id.bt_select_terminal:
                if (llSelectedTerminal.getVisibility() == View.VISIBLE) {
                    llSelectedTerminal.setVisibility(View.GONE);
                } else {
                    llSelectedTerminal.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_get_device:
                getDevice();
                isUpdate = true;
                break;
            case R.id.bt_volume:
                getVolume();
                isUpdate = true;
                break;
            case R.id.btn_task_control:
                taskControl();
                isUpdate = true;
                break;
        }
    }


}
