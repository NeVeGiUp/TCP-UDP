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
import com.bigkoo.pickerview.TimePickerView;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.event.utils.TaskUtils;
import com.itc.smartbroadcast.activity.music.ChooseMusicFolderActivityToTask;
import com.itc.smartbroadcast.base.Base2Activity;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditTaskDetailResult;
import com.itc.smartbroadcast.bean.EditTaskResult;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.bean.MusicMsgInfo;
import com.itc.smartbroadcast.bean.Task;
import com.itc.smartbroadcast.bean.TaskDetail;
import com.itc.smartbroadcast.bean.TaskOperationResult;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.EditTask;
import com.itc.smartbroadcast.channels.protocolhandler.EditTaskDetail;
import com.itc.smartbroadcast.channels.protocolhandler.GetTaskDetail;
import com.itc.smartbroadcast.channels.protocolhandler.TaskOperation;
import com.itc.smartbroadcast.util.DateUtil;
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

import static com.itc.smartbroadcast.activity.event.CreateTimedTaskActivity.REQUEST_VOLUME_CODE;

/**
 * create by youmu on 2018/7
 */
public class EditTimedTaskActivity extends Base2Activity {

    @BindView(R.id.bt_back_event)
    ImageView btBackEvent;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.tv_complete)
    TextView tv_complete;
    @BindView(R.id.tv_continue_time)
    TextView tvContinueTime;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.bt_starttime)
    RelativeLayout btStarttime;
    @BindView(R.id.bt_continuedtime)
    RelativeLayout btContinuedtime;
    @BindView(R.id.bt_executiondate)
    RelativeLayout btExecutiondate;
    @BindView(R.id.ll_execution_date)
    LinearLayout llExecutionDate;
    @BindView(R.id.bt_selectmusic)
    RelativeLayout btSelectmusic;
    @BindView(R.id.ll_selected_music)
    LinearLayout llSelectedMusic;
    @BindView(R.id.tv_playmode)
    TextView tvPlaymode;
    @BindView(R.id.bt_playmode)
    RelativeLayout btPlaymode;
    @BindView(R.id.bt_selectterminal)
    RelativeLayout btSelectterminal;
    @BindView(R.id.ll_selected_terminal)
    LinearLayout llSelectedTerminal;
    @BindView(R.id.bt_volume)
    RelativeLayout btVolume;
    @BindView(R.id.et_task_name)
    EditText etTaskName;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.btn_get_date)
    Button btnGetDate;
    @BindView(R.id.btn_select_music)
    Button btnSelectMusic;
    @BindView(R.id.tv_select_music)
    TextView tvSelectMusic;

    public static int REQUEST_MUSIC_CODE = 1;
    public static int REQUEST_PLAY_MODE_CODE = 2;
    public static int REQUEST_DEVICE_CODE = 3;
    public static int REQUEST_PRIORITY_CODE = 6;

    private final static int COMPLETE = 5;
    private final static long SLEEP_TIME = 300;
    @BindView(R.id.tv_select_device)
    TextView tvSelectDevice;
    @BindView(R.id.btn_get_device)
    Button btnGetDevice;
    @BindView(R.id.tv_volume)
    TextView tvVolume;
    @BindView(R.id.btn_task_control)
    Button btnTaskControl;
    @BindView(R.id.tv_select_music_size)
    TextView tvSelectMusicSize;
    @BindView(R.id.tv_select_terminal_size)
    TextView tvSelectTerminalSize;
    @BindView(R.id.btn_clear_date)
    Button btnClearDate;
    @BindView(R.id.tv_priority)
    TextView tvPriority;
    @BindView(R.id.bt_priority)
    RelativeLayout btPriority;
    @BindView(R.id.btn_clear_music)
    Button btnClearMusic;
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

    private List<String> taskDateDuplicationPatternList = new ArrayList<>();

    Task task = new Task();

    List<MusicMsgInfo> musicList = new ArrayList<>();
    List<FoundDeviceInfo> deviceList = new ArrayList<>();

    private boolean isUpdate = false;
    private String taskName = "";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case COMPLETE:
                    tv_complete.setEnabled(true);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createtimedtask);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);
        //默认不开启键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        context = this;
        initData();
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

        if ("editTaskResult".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                EditTaskResult editTaskResult = gson.fromJson(data, EditTaskResult.class);
                if (editTaskResult.getResult() == 0) {
                    ToastUtil.show(this, "任务编辑成功!");
                }
                if (editTaskResult.getResult() == 1) {
                    ToastUtil.show(this, "任务已满!");
                }
                if (editTaskResult.getResult() == 2) {
                    ToastUtil.show(this, "基本信息配置失败!");
                }
                if (editTaskResult.getResult() == 4) {
                    ToastUtil.show(this, "设备列表配置失败!");
                }
                if (editTaskResult.getResult() == 8) {
                    ToastUtil.show(this, "音乐列表配置失败!");
                }
                this.finish();
            } else {
                ToastUtil.show(this, "操作失败，请检查数据以及网络!");
            }
        }

        if ("taskOperationResult".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                TaskOperationResult taskOperationResult = gson.fromJson(data, TaskOperationResult.class);
                if (taskOperationResult.getResult() == 1) {
                    ToastUtil.show(this, "任务操作成功!");
                }else{
                    ToastUtil.show(this, "任务操作失败!");
                }
                this.finish();
            } else {
                ToastUtil.show(this, "任务操作失败!");
            }
        }


        if ("getTaskDetail".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                TaskDetail taskDetail = gson.fromJson(data, TaskDetail.class);

                //获取设备列表
                deviceList.clear();
                String deviceListStr = "";
                for (TaskDetail.Device device : taskDetail.getDeviceList()) {
                    String deviceListJson = AppDataCache.getInstance().getString("deviceList");
                    List<FoundDeviceInfo> deviceList = JSONArray.parseArray(deviceListJson, FoundDeviceInfo.class);
                    FoundDeviceInfo foundDeviceInfo = new FoundDeviceInfo();

                    for (FoundDeviceInfo deviceInfo : deviceList) {
                        if (device.getDeviceMac().equals(deviceInfo.getDeviceMac())) {
                            foundDeviceInfo = deviceInfo;
                            foundDeviceInfo.setDeviceZone(device.getDeviceZoneMsg());
                        }
                    }

                    foundDeviceInfo.setDeviceMac(device.getDeviceMac());
                    if (foundDeviceInfo.getDeviceName() == null||foundDeviceInfo.getDeviceName().equals("")){
                        deviceListStr += foundDeviceInfo.getDeviceMac() + "(该设备已被删除)\n";
                    }else{
                        deviceListStr += foundDeviceInfo.getDeviceName() + "\n";
                    }


                    this.deviceList.add(foundDeviceInfo);
                }
                tvSelectDevice.setText(deviceListStr);


                //获取音乐列表

                musicList.clear();
                String musicListStr = "";
                for (TaskDetail.Music music : taskDetail.getMusicList()) {
                    musicListStr += music.getMusicName() + "\n";
                    MusicMsgInfo musicMsgInfo = new MusicMsgInfo();
                    musicMsgInfo.setMusicFolderName(music.getMusicPath());
                    musicMsgInfo.setMusicName(music.getMusicName());
                    musicList.add(musicMsgInfo);
                }
                tvSelectMusic.setText(musicListStr);

                tvSelectMusicSize.setText(musicList.size() + "");
                tvSelectTerminalSize.setText(deviceList.size() + "");
            }
        }
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



        if (TaskUtils.getIsManager()) {
        } else {
            finish();
            return;
        }

        String taskName = etTaskName.getText().toString().trim();

        if (!isUpdate &&(taskName.equals(this.taskName))){
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


    @OnClick({R.id.bt_back_event, R.id.bt_starttime, R.id.bt_continuedtime, R.id.bt_executiondate, R.id.bt_playmode, R.id.tv_complete, R.id.btn_get_date,
            R.id.bt_selectmusic, R.id.btn_select_music, R.id.bt_selectterminal, R.id.btn_get_device, R.id.btn_task_control, R.id.bt_volume,
            R.id.btn_clear_date, R.id.bt_priority, R.id.btn_clear_music, R.id.btn_clear_device})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_back_event:
                back();
                break;
            case R.id.bt_starttime:
                showStartTime();
                isUpdate = true;
                break;
            case R.id.bt_continuedtime:
                showContinueTime();
                isUpdate = true;
                break;
            case R.id.bt_executiondate:
                if (llExecutionDate.getVisibility() == View.VISIBLE) {
                    llExecutionDate.setVisibility(View.GONE);
                } else {
                    llExecutionDate.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.bt_playmode:
                getPlayMode();
                isUpdate = true;
                break;

            case R.id.bt_priority:
                getPriority();
                isUpdate = true;
                break;
            case R.id.tv_complete:
                complete();
                break;
            case R.id.btn_get_date:
                if (taskDateDuplicationPatternList.size() >= 10) {
                    ToastUtil.show(context, "最多添加10个执行时间!");
                } else {
                    getDate();
                }
                isUpdate = true;
                break;
            case R.id.btn_clear_date:
                taskDateDuplicationPatternList.clear();
                String str = "";
                for (String st : taskDateDuplicationPatternList) {
                    str += st + "\n";
                }
                tvDate.setText(str);
                isUpdate = true;
                break;
            case R.id.btn_clear_music:
                musicList.clear();
                String musicStr = "";
                tvSelectMusic.setText(musicStr);
                tvSelectMusicSize.setText("0");
                isUpdate = true;
                break;
            case R.id.btn_clear_device:
                deviceList.clear();
                String deviceStr = "";
                tvSelectDevice.setText(deviceStr);
                tvSelectTerminalSize.setText("0");
                isUpdate = true;
                break;
            case R.id.bt_selectmusic:
                if (llSelectedMusic.getVisibility() == View.VISIBLE) {
                    llSelectedMusic.setVisibility(View.GONE);
                } else {
                    llSelectedMusic.setVisibility(View.VISIBLE);
                }

                break;
            case R.id.btn_select_music:
                getMusic();
                isUpdate = true;
                break;
            case R.id.btn_get_device:
                getDevice();
                isUpdate = true;
                break;

            case R.id.btn_task_control:     //禁止或启动任务
                taskControl();
                isUpdate = true;
                break;

            case R.id.bt_volume:
                getVolume();
                isUpdate = true;
                break;
            case R.id.bt_selectterminal:
                if (llSelectedTerminal.getVisibility() == View.VISIBLE) {
                    llSelectedTerminal.setVisibility(View.GONE);
                } else {
                    llSelectedTerminal.setVisibility(View.VISIBLE);
                }
                break;
        }
    }

    private void taskControl() {
        if (task.getTaskStatus() == 0) {
            task.setTaskStatus(1);
        } else {
            task.setTaskStatus(0);
        }
        TaskOperation.sendCMD(AppDataCache.getInstance().getString("loginIp"), task, task.getTaskStatus());
    }

    public void getMusic() {
        Intent intent = new Intent(context, ChooseMusicFolderActivityToTask.class);
        Gson gson = new Gson();
        String musicListJson = gson.toJson(musicList);
        intent.putExtra("music", musicListJson);
        startActivityForResult(intent, REQUEST_MUSIC_CODE);
    }


    public void getPlayMode() {
        Intent intent = new Intent(context, PlayModeActivity.class);
        intent.putExtra("playMode", tvPlaymode.getText().toString());
        startActivityForResult(intent, REQUEST_PLAY_MODE_CODE);
    }

    /**
     * 获取优先级
     */
    public void getPriority() {
        Intent intent = new Intent(context, SelectPriorityActivity.class);
        intent.putExtra("priority", tvPriority.getText().toString());
        startActivityForResult(intent, REQUEST_PRIORITY_CODE);
    }

    public void getDevice() {
        Intent intent = new Intent(context, ChoosePlayTerminalActivity.class);
        Gson gson = new Gson();
        String deviceListJson = gson.toJson(deviceList);
        intent.putExtra("deviceList", deviceListJson);
        startActivityForResult(intent, REQUEST_DEVICE_CODE);
    }

    public void getVolume() {
        Intent intent = new Intent(context, SelectVolumeActivity.class);
        intent.putExtra("volume", tvVolume.getText().toString().trim());
        startActivityForResult(intent, REQUEST_VOLUME_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_MUSIC_CODE == requestCode) {        //获取音乐

            String musicResultListStr = data.getStringExtra("music_result_list");
            if (!"".equals(musicResultListStr)) {
                musicList.clear();
                Log.i("result", "onActivityResult: " + musicResultListStr);
                musicList.addAll(JSONArray.parseArray(musicResultListStr, MusicMsgInfo.class));
                String str = "";
                for (MusicMsgInfo music : musicList) {
                    str += music.getMusicName() + "\n";
                }
                tvSelectMusic.setText(str);
            }
        }
        if (REQUEST_PLAY_MODE_CODE == requestCode) {       //获取播放模式
            String playMode = data.getStringExtra("playMode");
            if (!playMode.equals("")) {
                tvPlaymode.setText(playMode);
            }

        }

        if (REQUEST_PRIORITY_CODE == requestCode) {       //获取优先级
            String priority = data.getStringExtra("priority");
            if (!priority.equals("")) {
                tvPriority.setText(priority);
            }
        }

        if (REQUEST_VOLUME_CODE == requestCode) {       //获取音量
            String volume = data.getStringExtra("volume");
            if (volume.equals("") || volume.equals("0")) {
                tvVolume.setText("");
            } else {
                tvVolume.setText(volume);
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
        }
        tvSelectMusicSize.setText(musicList.size() + "");
        tvSelectTerminalSize.setText(deviceList.size() + "");

    }

    public void complete() {

        String taskName = etTaskName.getText().toString().trim();
        if (!StringUtils.checkName(taskName)) {
            ToastUtil.show(context, "任务名称必须是汉字、字母和数字并且在2到15个字符之间！", Toast.LENGTH_SHORT);
            return;
        }
        String startTime = tvTime.getText().toString().trim();
        if (startTime == null || startTime.equals("")) {
            ToastUtil.show(context, "请选择开始时间！");
            return;
        }
        String continueTime = tvContinueTime.getText().toString().trim();
        if (continueTime == null || continueTime.equals("")) {
            ToastUtil.show(context, "请选择持续时间！");
            return;
        }
        if (continueTime.equals("00:00:00")) {
            ToastUtil.show(context, "持续时间不能为0！");
            return;
        }

        if (musicList == null || musicList.size() == 0) {
            ToastUtil.show(context, "请选择音乐！");
            return;
        }
        if (deviceList == null || deviceList.size() == 0) {
            ToastUtil.show(context, "请选择终端！");
            return;
        }

        int h = Integer.parseInt(continueTime.split(":")[0]);
        int m = Integer.parseInt(continueTime.split(":")[1]);
        int s = Integer.parseInt(continueTime.split(":")[2]);
        int continueDate = (h * 60 * 60) + (m * 60) + s;

        task.setSchemeNum(255);
        task.setTaskName(taskName);
        task.setTaskStartDate(startTime);
        task.setTaskContinueDate(continueDate);

        int[] week = {0, 0, 0, 0, 0, 0, 0};
        task.setTaskWeekDuplicationPattern(week);
        String date[] = new String[taskDateDuplicationPatternList.size()];
        for (int i = 0; i < taskDateDuplicationPatternList.size(); i++) {
            date[i] = taskDateDuplicationPatternList.get(i);
        }
        task.setTaskDateDuplicationPattern(date);
        int playMode = 0;
        switch (tvPlaymode.getText().toString().trim()) {
            case "顺序播放":
                playMode = 0;
                break;
            case "循环播放":
                playMode = 1;
                break;
            case "随机播放":
                playMode = 2;
                break;
            default:
                playMode = 1;
                break;
        }
        task.setTaskPlayMode(playMode);

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
        task.setTaskPriority(priority);

        task.setTaskPlayTotal(musicList.size());
        String volume = tvVolume.getText().toString().trim();
        if (volume == null || volume.equals("")) {
            volume = "128";
        }
        task.setTaskVolume(Integer.parseInt(volume));

        TaskDetail taskDetail = new TaskDetail();
        taskDetail.setTaskNum(task.getTaskNum());
        List<TaskDetail.Device> deviceList = new ArrayList<>();
        for (int k = 0; k < this.deviceList.size(); k++) {
            TaskDetail.Device device = new TaskDetail.Device();
            if (this.deviceList.get(k).getDeviceZone() == null || this.deviceList.get(k).getDeviceZone().length == 0) {
                int[] zone = new int[8];
                device.setDeviceZoneMsg(zone);
            } else {
                device.setDeviceZoneMsg(this.deviceList.get(k).getDeviceZone());
            }

            device.setDeviceMac(this.deviceList.get(k).getDeviceMac());
            deviceList.add(device);
        }

        List<TaskDetail.Music> musicList = new ArrayList<TaskDetail.Music>();
        for (int k = 0; k < this.musicList.size(); k++) {
            TaskDetail.Music music = new TaskDetail.Music();
            music.setMusicName(this.musicList.get(k).getMusicName());
            music.setMusicPath(this.musicList.get(k).getMusicFolderName());
            musicList.add(music);
        }
        taskDetail.setDeviceList(deviceList);
        taskDetail.setMusicList(musicList);
        tv_complete.setEnabled(false);
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
        EditTask.sendCMD(AppDataCache.getInstance().getString("loginIp"), task, taskDetail, 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void initData() {
        llExecutionDate.setVisibility(View.GONE);
        llSelectedMusic.setVisibility(View.GONE);
        llSelectedTerminal.setVisibility(View.GONE);
        btnTaskControl.setVisibility(View.VISIBLE);
        tv_complete.setText("编辑保存");

        //权限加入
        if (TaskUtils.getIsManager()) {

        } else {
            etTaskName.setEnabled(false);
            btStarttime.setEnabled(false);
            btContinuedtime.setEnabled(false);

            btnClearDate.setVisibility(View.GONE);
            btnGetDate.setVisibility(View.GONE);

            btnSelectMusic.setVisibility(View.GONE);
            btPlaymode.setEnabled(false);
            btnGetDevice.setVisibility(View.GONE);
            btVolume.setEnabled(false);

            btnTaskControl.setVisibility(View.GONE);
            tv_complete.setVisibility(View.GONE);

            btnClearMusic.setVisibility(View.GONE);
            btnClearDevice.setVisibility(View.GONE);
            ToastUtil.show(context, "温馨提示：普通用户不能编辑信息！");
        }


        Intent intent = getIntent();
        String taskStr = intent.getStringExtra("task");
        Gson gson = new Gson();
        task = gson.fromJson(taskStr, Task.class);
        task.setTaskDateDuplicationPattern(DateUtil.getDate(task.getTaskDateDuplicationPattern()));
        etTaskName.setText(task.getTaskName());

        taskName = etTaskName.getText().toString().trim();

        //持续时间
        int continueTime = task.getTaskContinueDate();
        int hour = (continueTime / (60 * 60)) % 24;
        int minute = (continueTime / 60) % 60;
        int second = continueTime % 60;
        tvContinueTime.setText(hour + ":" + minute + ":" + second);//持续时间
        //开始时间
        tvTime.setText(task.getTaskStartDate());
        //持续时间
        for (String str : task.getTaskDateDuplicationPattern()) {
            taskDateDuplicationPatternList.add(str);
        }
        String str = "";
        for (String st : taskDateDuplicationPatternList) {
            str += st + "\n";
        }
        tvDate.setText(str);
        //播放模式
        String playMode = "";
        switch (task.getTaskPlayMode()) {
            case 0:
                playMode = "顺序播放";
                break;
            case 1:
                playMode = "循环播放";
                break;
            case 2:
                playMode = "随机播放";
                break;
        }
        tvPlaymode.setText(playMode);

        //播放模式
        String priority = "";
        switch (task.getTaskPriority()) {
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

        //音量
        if (task.getTaskVolume() == 128) {
            tvVolume.setText("");
        } else {
            tvVolume.setText(task.getTaskVolume() + "");
        }


        int status = task.getTaskStatus();
        if (status == 0) {
            btnTaskControl.setText("启动任务");
        } else {
            btnTaskControl.setText("禁止任务");
        }

        GetTaskDetail.sendCMD(AppDataCache.getInstance().getString("loginIp"), task);


    }


    //定时任务开始时间
    private void showStartTime() {
        if (timePickerViewStart == null) {
            timePickerViewStart = new TimePickerView(this, TimePickerView.Type.HOURS_MINS);
            timePickerViewStart.setTitle("");
            timePickerViewStart.setRange(0, 23);
            if (tvTime.getText().toString().equals("")) {
                timePickerViewStart.setTime(new Date());
            } else {
                try {
                    timePickerViewStart.setTime(formattype2.parse(tvTime.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            timePickerViewStart.setCyclic(false);
            timePickerViewStart.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date) {
                    String formattime = formattype2.format(date);
                    tvTime.setText(formattime);
                }
            });
        }
        if (!timePickerViewStart.isShowing()) {
            timePickerViewStart.show();
        } else {
            timePickerViewStart.dismiss();
        }
    }

    //定时任务持续时间
    private void showContinueTime() {
        if (timePickerViewContinue == null) {
            timePickerViewContinue = new TimePickerView(this, TimePickerView.Type.HOURS_MINS);
            timePickerViewContinue.setTitle("");
            timePickerViewContinue.setRange(0, 23);
            try {
                if (tvContinueTime.getText().toString().equals("")) {
                    timePickerViewContinue.setTime(formattype2.parse("00:00:00"));
                } else {
                    timePickerViewContinue.setTime(formattype2.parse(tvContinueTime.getText().toString()));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            timePickerViewContinue.setCyclic(false);
            timePickerViewContinue.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date) {
                    String formattime = formattype2.format(date);
                    tvContinueTime.setText(formattime);
                }
            });
        }
        if (!timePickerViewContinue.isShowing()) {
            timePickerViewContinue.show();
        } else {
            timePickerViewContinue.dismiss();
        }
    }

    //获取重复日期
    private void getDate() {
        if (timePickerView == null) {
            timePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
            timePickerView.setTitle("");
            timePickerView.setRange(2000, 2025);
            timePickerView.setTime(new Date());
            timePickerView.setCyclic(false);
            timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date) {
                    String formattime = formattype3.format(date);
                    boolean isHave = false;
                    for (String st : taskDateDuplicationPatternList) {
                        if (st.equals(formattime)) {
                            isHave = true;
                        }
                    }


                    if (!isHave) {
                        taskDateDuplicationPatternList.add(formattime);
                        String str = "";
                        for (String st : taskDateDuplicationPatternList) {
                            str += st + "\n";
                        }
                        tvDate.setText(str);
                    } else {
                        ToastUtil.show(context, "该日期已经添加，请重选！", Toast.LENGTH_SHORT);
                    }
                }
            });
        }
        if (!timePickerView.isShowing()) {
            timePickerView.show();
        } else {
            timePickerView.dismiss();
        }
    }

    public void goTo(Class<?> to) {
        Intent it = new Intent(this, to);
        startActivity(it);
    }


}
