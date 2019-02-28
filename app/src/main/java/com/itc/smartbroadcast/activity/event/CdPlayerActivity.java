package com.itc.smartbroadcast.activity.event;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.adapter.TerminalStatusAdapter;
import com.itc.smartbroadcast.adapter.event.MusicAdapter;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.CDInstantStatus;
import com.itc.smartbroadcast.bean.CDMusic;
import com.itc.smartbroadcast.bean.CDMusicList;
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
import com.itc.smartbroadcast.util.SizeUtils;
import com.itc.smartbroadcast.util.ToastUtil;
import com.itc.smartbroadcast.widget.custom.VerticalSeekBar;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Content :
 * @Author : lik
 * @Time : 18-9-12 下午5:24
 */
public class CdPlayerActivity extends AppCompatActivity {

    @BindView(R.id.bt_back)
    ImageView btBack;
    @BindView(R.id.tv_task_name)
    TextView tvTaskName;
    @BindView(R.id.iv_edit_task)
    ImageView ivEditTask;
    @BindView(R.id.tv_music_name)
    TextView tvMusicName;
    @BindView(R.id.sb_cd)
    SeekBar sbCd;
    @BindView(R.id.tv_now_time)
    TextView tvNowTime;
    @BindView(R.id.tv_end_time)
    TextView tvEndTime;
    @BindView(R.id.iv_prev)
    ImageView ivProv;
    @BindView(R.id.iv_play)
    ImageView ivPlay;
    @BindView(R.id.iv_next)
    ImageView ivNext;
    @BindView(R.id.sb_volume)
    VerticalSeekBar sbVolume;
    @BindView(R.id.iv_menu)
    ImageView ivMenu;
    @BindView(R.id.ll_edit_task)
    LinearLayout llEditTask;
    @BindView(R.id.ll_menu)
    LinearLayout llMenu;
    @BindView(R.id.iv_record)
    ImageView ivRecord;
    @BindView(R.id.iv_pointer)
    ImageView ivPointer;
    @BindView(R.id.iv_volume)
    ImageView ivVolume;
    @BindView(R.id.tv_volume)
    TextView tvVolume;
    @BindView(R.id.ll_music_list)
    LinearLayout llMusicList;
    @BindView(R.id.btn_start_task)
    Button btnStartTask;
    @BindView(R.id.iv_play_mode)
    ImageView ivPlayMode;
    @BindView(R.id.tv_play_mode)
    TextView tvPlayMode;
    @BindView(R.id.ll_play_mode)
    LinearLayout llPlayMode;
    @BindView(R.id.iv_device_type)
    ImageView ivDeviceType;
    @BindView(R.id.tv_device_type)
    TextView tvDeviceType;
    @BindView(R.id.ll_device_type)
    LinearLayout llDeviceType;
    @BindView(R.id.ll_terminal_status)
    LinearLayout llTerminalStatus;
    @BindView(R.id.tv_music_list)
    TextView tvMusicList;
    @BindView(R.id.tv_terminal_status)
    TextView tvTerminalStatus;
    @BindView(R.id.tv_loading)
    TextView tvLoading;
    @BindView(R.id.ll_volume_mute)
    LinearLayout llVolumeMute;
    @BindView(R.id.tv_loading_execute)
    TextView tvLoadingExecute;
    private Context context;

    int cdStatus = 255;
    Button btnNext;
    Button btnPrev;


    private final static int UPDATE_TASK = 1;
    private final static int LAST_MUSIC = 2;
    private final static int NEXT_MUSIC = 3;
    private final static int LAST_PAGE = 4;
    private final static int NEXT_PAGE = 5;
    //线程睡眠时间
    private final static long SLEEP_TIME = 300;
    String instantTaskJson = null;
    InstantTask instantTask = new InstantTask();

    TextView tvMusicSize;

    int isPlay = 0;

    String nowDate = "";

    CDInstantStatus CDInstantStatus = new CDInstantStatus();

    Date playDate;

    boolean isController = false;
    boolean isOnline = false;

    List<FoundDeviceInfo> deviceListAll;

    CDMusicList music = new CDMusicList();

    List<TerminalDeviceStatus> terminalDeviceStatusList;

    MusicAdapter musicAdapter = null;

    int saveDeviceType = 255;

    int nowMusicNum = 99999;

    Animation anim;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_TASK:        //更新定时器
                    String dateFormat = dateFormat((String) msg.obj);
                    String nowTime = (String) msg.obj;
                    String allTime = "00:00:00";
                    if (CDInstantStatus.getNowMusicTime() != null) {
                        allTime = CDInstantStatus.getNowMusicTime();
                    }
                    int used = Integer.parseInt(nowTime.split(":")[0]) * 60 * 60 + Integer.parseInt(nowTime.split(":")[1]) * 60 + Integer.parseInt(nowTime.split(":")[2]);
                    int all = Integer.parseInt(allTime.split(":")[0]) * 60 * 60 + Integer.parseInt(allTime.split(":")[1]) * 60 + Integer.parseInt(allTime.split(":")[2]);
                    int progress = (int) (((double) used / (double) all) * 100);
                    sbCd.setProgress(progress);
                    if (progress >= 100) {
                        tvNowTime.setText(tvEndTime.getText().toString().trim());
                    } else {
                        tvNowTime.setText(dateFormat);
                    }
                    break;
                case LAST_MUSIC:
                    ivProv.setEnabled(true);
                    break;
                case NEXT_MUSIC:
                    ivNext.setEnabled(true);
                    break;
                case LAST_PAGE:
                    btnPrev.setEnabled(true);
                    break;
                case NEXT_PAGE:
                    btnNext.setEnabled(true);
                    break;
            }
        }
    };

    /**
     * 时间格式化
     *
     * @param obj
     * @return
     */
    private String dateFormat(String obj) {
        String[] arr = obj.split(":");
        String h = arr[0].length() < 2 ? ("0" + arr[0]) : (arr[0]);
        String m = arr[1].length() < 2 ? ("0" + arr[1]) : (arr[1]);
        String s = arr[2].length() < 2 ? ("0" + arr[2]) : (arr[2]);
        String re = h + ":" + m + ":" + s;
        return re;
    }

    Thread thread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置不能横屏，防止生命周期的改变
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_cd_player);
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
        ivProv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int alpha = ivProv.getImageAlpha();
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    //重新设置按下时的背景图片
                    ivProv.setColorFilter(getResources().getColor(R.color.colorMain));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //再修改为抬起时的正常图片
                    ivProv.setColorFilter(Color.BLACK);
                }
                return false;
            }
        });
        ivNext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int alpha = ivNext.getImageAlpha();
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    //重新设置按下时的背景图片
                    ivNext.setColorFilter(getResources().getColor(R.color.colorMain));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //再修改为抬起时的正常图片
                    ivNext.setColorFilter(Color.BLACK);
                }
                return false;
            }
        });

        llDeviceType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    //重新设置按下时的背景图片
                    tvDeviceType.setTextColor(context.getResources().getColor(R.color.colorMain));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //再修改为抬起时的正常图片
                    tvDeviceType.setTextColor(Color.GRAY);
                }

                return false;
            }
        });
        llPlayMode.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    //重新设置按下时的背景图片
                    tvPlayMode.setTextColor(context.getResources().getColor(R.color.colorMain));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //再修改为抬起时的正常图片
                    tvPlayMode.setTextColor(Color.GRAY);
                }

                return false;
            }
        });
        llMusicList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    //重新设置按下时的背景图片
                    tvMusicList.setTextColor(context.getResources().getColor(R.color.colorMain));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //再修改为抬起时的正常图片
                    tvMusicList.setTextColor(Color.GRAY);
                }

                return false;
            }
        });

        llTerminalStatus.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    //重新设置按下时的背景图片
                    tvTerminalStatus.setTextColor(context.getResources().getColor(R.color.colorMain));
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    //再修改为抬起时的正常图片
                    tvTerminalStatus.setTextColor(Color.GRAY);
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

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Message msg = new Message();
                    msg.what = 1;
                    if (CDInstantStatus != null) {
                        if (isPlay == 0) {
                            String obj = tvNowTime.getText().toString().trim();
                            msg.obj = obj;
                            handler.sendMessage(msg);
                        } else {
                            String startTime = CDInstantStatus.getNowTime();
                            Date nowDate = new Date();
                            String[] startTimeArr = startTime.split(":");
                            Date startDate = new Date();
                            if (startTimeArr.length == 3) {
                                startDate = new Date(playDate.getTime() - ((Integer.parseInt(startTimeArr[0]) * 60 * 60 + Integer.parseInt(startTimeArr[1]) * 60 + Integer.parseInt(startTimeArr[2])) * 1000));
                            }

                            int continueTime = (int) ((nowDate.getTime() - startDate.getTime()) / 1000);
                            int h = continueTime / (60 * 60);
                            int m = (continueTime / 60) % 60;
                            int s = continueTime % 60;
                            String obj = h + ":" + m + ":" + s;
                            msg.obj = obj;
                            handler.sendMessage(msg);
                        }
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
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
        stopRotate();

        isController = foundDeviceInfo.getDeviceStatus().equals("在线") && instantTask.getStatus() == 1;
        isOnline = foundDeviceInfo.getDeviceStatus().equals("在线");

        if (isController) {
            DeviceControl.sendCMD(userNum, foundDeviceInfo, 18, 1, instantTask.getTaskNum());
        } else {
            if (type == 0) {
                Log.i("initStatus", "initStatus: " + "当前任务未启动或者该设备处于离线中...");
                ToastUtil.show(context, "当前任务未启动或者该设备处于离线中...");
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    private void initData() {

        deviceListAll = new ArrayList<>();
        music = new CDMusicList();
        terminalDeviceStatusList = new ArrayList<>();

        instantTaskJson = getIntent().getStringExtra("instantTask");
        Gson gson = new Gson();
        instantTask = gson.fromJson(instantTaskJson, InstantTask.class);

        tvTaskName.setText(instantTask.getTaskName());
        sbCd.setProgress(0);

        ivPlay.setImageDrawable(getResources().getDrawable(R.mipmap.jishi_but_stop_default));
        stopRotate();


        anim = AnimationUtils.loadAnimation(this, R.anim.round_rotate);
        ivPointer.setPivotX(SizeUtils.dp2px(12));
        ivPointer.setPivotY(SizeUtils.dp2px(12));//支点在图片中心
        ivPointer.setRotation(-40);


        tvNowTime.setText("00:00:00");
        tvEndTime.setText("00:00:00");

        if (instantTask.getStatus() == 0) {
            btnStartTask.setText("启动任务");
        } else {
            btnStartTask.setText("停止任务");
        }


        InstantTaskDetail instantTaskDetail = new InstantTaskDetail();
        instantTaskDetail.setTaskNum(instantTask.getTaskNum());
        GetInstantTaskDetail.sendCMD(AppDataCache.getInstance().getString("loginIp"), instantTaskDetail);
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
        sbCd.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean b) {


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                int position = sbCd.getProgress();
                if (isController) {

                    String allTime = "00:00:00";
                    if (CDInstantStatus.getNowMusicTime() != null) {
                        allTime = CDInstantStatus.getNowMusicTime();
                    }
                    int all = Integer.parseInt(allTime.split(":")[0]) * 60 * 60 + Integer.parseInt(allTime.split(":")[1]) * 60 + Integer.parseInt(allTime.split(":")[2]);

                    int progress = (int) ((position / 100.0) * all);

                    String nowTime = progress / (60 * 60) + ":" + progress / 60 % 60 + ":" + progress % 60;

                    playDate = new Date();
                    CDInstantStatus.setNowTime(nowTime);
                    setMusicProgress(progress);
                }
            }
        });

    }


    @OnClick({R.id.bt_back, R.id.iv_edit_task, R.id.iv_prev, R.id.iv_play, R.id.iv_next, R.id.iv_menu, R.id.ll_edit_task, R.id.ll_menu, R.id.ll_music_list,
            R.id.btn_start_task, R.id.ll_play_mode, R.id.ll_device_type, R.id.ll_terminal_status, R.id.ll_volume_mute})
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
            case R.id.ll_edit_task:
                Intent intent1 = new Intent(context, EditInstantTaskActivity.class);
                intent1.putExtra("instantTask", instantTaskJson);
                startActivity(intent1);
                finish();
                break;

            case R.id.iv_prev:
                if (isController) {
                    prev();
                    ivProv.setEnabled(false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(SLEEP_TIME);
                                Message msg = new Message();
                                msg.what = LAST_MUSIC;
                                handler.sendMessage(msg);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    ToastUtil.show(context, "当前任务未启动或者该设备处于离线中...");
                }
                break;
            case R.id.iv_play:
                if (isController) {
                    if (CDInstantStatus.getMusicSize() == 0) {
                        ToastUtil.show(context, "当前暂无可播放歌曲！");
                    }
                    play();
                } else {
                    ToastUtil.show(context, "当前任务未启动或者该设备处于离线中...");
                }

                break;
            case R.id.iv_next:
                if (isController) {
                    next();
                    ivNext.setEnabled(false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(SLEEP_TIME);
                                Message msg = new Message();
                                msg.what = NEXT_MUSIC;
                                handler.sendMessage(msg);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    ToastUtil.show(context, "当前任务未启动或者该设备处于离线中...");
                }
                break;
            case R.id.iv_menu:
                if (isController) {
                    showPopWindow();
                } else {
                    ToastUtil.show(context, "当前任务未启动或者该设备处于离线中...");
                }

                break;
            case R.id.ll_menu:
                if (isController) {
                    showPopWindow();
                } else {
                    ToastUtil.show(context, "当前任务未启动或者该设备处于离线中...");
                }
                break;
            case R.id.ll_music_list:
                if (isController) {
                    showPopWindow();
                } else {
                    ToastUtil.show(context, "当前任务未启动或者该设备处于离线中...");
                }
                break;
            case R.id.btn_start_task:       //启动/停止即时任务
                if (isOnline) {
                    taskControl();
                } else {
                    ToastUtil.show(context, "当前设备处于离线中...");
                }
                break;
            case R.id.ll_play_mode:         //播放模式切换

                if (isController) {
                    playModelController();
                } else {
                    ToastUtil.show(context, "当前任务未启动或者该设备处于离线中...");
                }
                break;
            case R.id.ll_device_type:         //介质切换

                if (isController) {
                    tvMusicName.setText("");
                    deviceTypeController();
                } else {
                    ToastUtil.show(context, "当前任务未启动或者该设备处于离线中...");
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

    /**
     * 存储介质切换
     */
    private void deviceTypeController() {

        int userNum = AppDataCache.getInstance().getInt("userNum");
        String deviceListStr = AppDataCache.getInstance().getString("deviceList");
        List<FoundDeviceInfo> deviceList = JSONArray.parseArray(deviceListStr, FoundDeviceInfo.class);
        FoundDeviceInfo foundDeviceInfo = new FoundDeviceInfo();
        for (FoundDeviceInfo device : deviceList) {
            if (instantTask.getTerminalMac().equals(device.getDeviceMac())) {
                foundDeviceInfo = device;
            }
        }
        DeviceControl.sendCMD(userNum, foundDeviceInfo, 6, 1, instantTask.getTaskNum());
    }

    /**
     * 播放模式切换
     */
    private void playModelController() {

        int userNum = AppDataCache.getInstance().getInt("userNum");
        String deviceListStr = AppDataCache.getInstance().getString("deviceList");
        List<FoundDeviceInfo> deviceList = JSONArray.parseArray(deviceListStr, FoundDeviceInfo.class);
        FoundDeviceInfo foundDeviceInfo = new FoundDeviceInfo();
        for (FoundDeviceInfo device : deviceList) {
            if (instantTask.getTerminalMac().equals(device.getDeviceMac())) {
                foundDeviceInfo = device;
            }
        }

        switch (CDInstantStatus.getPlayModel()) {

            case 0:     //顺序播放切单曲循环
                DeviceControl.sendCMD(userNum, foundDeviceInfo, 8, 1, instantTask.getTaskNum());
                break;
            case 1:     //单曲循环切全部播放
                DeviceControl.sendCMD(userNum, foundDeviceInfo, 9, 1, instantTask.getTaskNum());
                break;
            case 2:     //全部播放切单曲播放
                DeviceControl.sendCMD(userNum, foundDeviceInfo, 10, 1, instantTask.getTaskNum());
                break;
            case 4:     //单曲播放切顺序播放
                DeviceControl.sendCMD(userNum, foundDeviceInfo, 7, 1, instantTask.getTaskNum());
                break;
        }

    }

    private void next() {
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
        stopRotate();
        tvLoadingExecute.setText("请稍等....");
        tvLoadingExecute.setVisibility(View.VISIBLE);
        DeviceControl.sendCMD(userNum, foundDeviceInfo, 4, 1, instantTask.getTaskNum());
    }

    private void prev() {

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
        stopRotate();

        tvLoadingExecute.setText("请稍等....");
        tvLoadingExecute.setVisibility(View.VISIBLE);


        DeviceControl.sendCMD(userNum, foundDeviceInfo, 3, 1, instantTask.getTaskNum());

    }

    /**
     * 下一页
     */
    private void nextPage() {
        int userNum = AppDataCache.getInstance().getInt("userNum");
        String deviceListStr = AppDataCache.getInstance().getString("deviceList");
        List<FoundDeviceInfo> deviceList = JSONArray.parseArray(deviceListStr, FoundDeviceInfo.class);
        FoundDeviceInfo foundDeviceInfo = new FoundDeviceInfo();
        for (FoundDeviceInfo device : deviceList) {
            if (instantTask.getTerminalMac().equals(device.getDeviceMac())) {
                foundDeviceInfo = device;
            }
        }
        DeviceControl.sendCMD(userNum, foundDeviceInfo, 12, 1, instantTask.getTaskNum());

    }

    /**
     * 上一页
     */
    private void prevPage() {
        int userNum = AppDataCache.getInstance().getInt("userNum");
        String deviceListStr = AppDataCache.getInstance().getString("deviceList");
        List<FoundDeviceInfo> deviceList = JSONArray.parseArray(deviceListStr, FoundDeviceInfo.class);
        FoundDeviceInfo foundDeviceInfo = new FoundDeviceInfo();
        for (FoundDeviceInfo device : deviceList) {
            if (instantTask.getTerminalMac().equals(device.getDeviceMac())) {
                foundDeviceInfo = device;
            }
        }
        DeviceControl.sendCMD(userNum, foundDeviceInfo, 11, 1, instantTask.getTaskNum());

    }

    /**
     * 播放指定歌曲
     */
    private void playMusicById(int musicNum) {
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
        stopRotate();
        tvLoadingExecute.setText("请稍等....");
        tvLoadingExecute.setVisibility(View.VISIBLE);

        DeviceControl.sendCMD(userNum, foundDeviceInfo, 13, musicNum, instantTask.getTaskNum());

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

    private void setMusicProgress(int progress) {
        int userNum = AppDataCache.getInstance().getInt("userNum");
        String deviceListStr = AppDataCache.getInstance().getString("deviceList");
        List<FoundDeviceInfo> deviceList = JSONArray.parseArray(deviceListStr, FoundDeviceInfo.class);
        FoundDeviceInfo foundDeviceInfo = new FoundDeviceInfo();
        for (FoundDeviceInfo device : deviceList) {
            if (instantTask.getTerminalMac().equals(device.getDeviceMac())) {
                foundDeviceInfo = device;
            }
        }
        DeviceControl.sendCMD(userNum, foundDeviceInfo, 19, progress, instantTask.getTaskNum());
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

        tvLoadingExecute.setText("请稍等....");
        tvLoadingExecute.setVisibility(View.VISIBLE);

        if (isPlay == 0) {    //当前处于停止状态
            //播放CD
            DeviceControl.sendCMD(userNum, foundDeviceInfo, 1, 1, instantTask.getTaskNum());
        } else {
            //暂停CD
            DeviceControl.sendCMD(userNum, foundDeviceInfo, 2, 1, instantTask.getTaskNum());
        }
    }

    /**
     * 图片开始旋转
     */
    private void rotate() {
        if (ivRecord.getAnimation() == null) {
            LinearInterpolator lir = new LinearInterpolator();
            anim.setInterpolator(lir);
            ivRecord.startAnimation(anim);
            ivPointer.setPivotX(SizeUtils.dp2px(12));
            ivPointer.setPivotY(SizeUtils.dp2px(12));//支点在图片中心
            ivPointer.setRotation(0);
        }
    }

    /**
     * 停止动画
     */
    private void stopRotate() {
        ivRecord.clearAnimation();
        ivPointer.setPivotX(SizeUtils.dp2px(12));
        ivPointer.setPivotY(SizeUtils.dp2px(12));//支点在图片中心
        ivPointer.setRotation(-40);
    }


    /**
     * 终端状态查询PopWindow
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


    /**
     * 显示音乐列表的popupWindow
     */
    private void showPopWindow() {
        final Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
        final View contentView = LayoutInflater.from(this).inflate(R.layout.popup_cd_player, null);
        bottomDialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.setCanceledOnTouchOutside(true);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();

        btnNext = (Button) contentView.findViewById(R.id.btn_next);
        btnPrev = (Button) contentView.findViewById(R.id.btn_prev);
        final ListView musicLv = (ListView) contentView.findViewById(R.id.lv_cd_music);
        TextView tvTaskName = (TextView) contentView.findViewById(R.id.tv_task_name);
        tvMusicSize = (TextView) contentView.findViewById(R.id.tv_music_size);
        TextView tvNoData = (TextView) contentView.findViewById(R.id.tv_no_data);

        tvTaskName.setText(instantTask.getTaskName());
        tvMusicSize.setText("总曲目：" + CDInstantStatus.getMusicSize());
        musicAdapter = new MusicAdapter(context, music, nowMusicNum);
        musicLv.setAdapter(musicAdapter);

        final List<CDMusic> list = musicAdapter.getList();
        musicLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (list.get(i) == null) {
                    ToastUtil.show(context, "当前音乐不存在！");
                } else {
                    playMusicById(list.get(i).getNum());
                    bottomDialog.dismiss();
                }
            }
        });

        //下一页
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextPage();
                btnNext.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(SLEEP_TIME);
                            Message msg = new Message();
                            msg.what = NEXT_PAGE;
                            handler.sendMessage(msg);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        //上一页
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevPage();
                btnPrev.setEnabled(false);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(SLEEP_TIME);
                            Message msg = new Message();
                            msg.what = LAST_PAGE;
                            handler.sendMessage(msg);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
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

        /**
         * 获取即时任务结束标志
         */
        if ("getInstallTaskEnd".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {

                GetInstallTaskEndResult getInstallTaskEndResult = gson.fromJson(data, GetInstallTaskEndResult.class);

                if (getInstallTaskEndResult.getTaskNum() == instantTask.getTaskNum())
                    GetInstantTaskList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
            }
        }

        /**
         * 设备控制状态回复
         */
        if ("deviceControlResult".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                DeviceControlResult deviceControlResult = gson.fromJson(data, DeviceControlResult.class);

                if (deviceControlResult.getResult() == 1) {
                } else if (deviceControlResult.getResult() == 2) {
                    ToastUtil.show(this, "操作失败，无可用终端!");
                } else {
                    ToastUtil.show(this, "操作失败，请检查数据以及网络!");
                }
            } else {
                ToastUtil.show(this, "操作失败，请检查数据以及网络!");
            }
        }

        /**
         * 操作即时任务结果
         */
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

        /**
         * 获取即时任务列表
         */
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
                    stopRotate();
                    btnStartTask.setText("启动任务");
                } else {
                    btnStartTask.setText("停止任务");
                }
                initStatus(1);
            } else {

            }
        }

        /**
         * 获取即时任务详情
         */
        if ("instantTaskDetail".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                String deviceListStr = AppDataCache.getInstance().getString("deviceList");
                List<FoundDeviceInfo> deviceList = JSONArray.parseArray(deviceListStr, FoundDeviceInfo.class);
                InstantTaskDetail instantTaskDetail = gson.fromJson(data, InstantTaskDetail.class);
                for (InstantTaskDetail.Device inDevice : instantTaskDetail.getDevicesList()) {
                    for (FoundDeviceInfo device : deviceList) {
                        if (inDevice.getDeviceMac().equals(device.getDeviceMac())) {
                            this.deviceListAll.add(device);
                        }
                    }
                }
                String str = "";
                for (FoundDeviceInfo device : this.deviceListAll) {
                    str += device.getDeviceName() + "\n";
                }
            }
        }

        /**
         * 获取CD机推送状态
         */
        if ("getCdInstantStatus".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                CDInstantStatus = gson.fromJson(data, CDInstantStatus.class);
                if (CDInstantStatus.getDeviceMac().equals(instantTask.getTerminalMac()) && CDInstantStatus.getTaskNum() == instantTask.getTaskNum()) {
                    updateCdStatus();
                }
            } else {
                ToastUtil.show(this, "操作失败，请检查数据以及网络!");
            }
        }
        if ("getCDMusicList".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {

                CDMusicList cdMusicList = gson.fromJson(data, CDMusicList.class);
                if (cdMusicList.getTaskNum() == instantTask.getTaskNum()) {
                    music = cdMusicList;
                    if (musicAdapter != null)
                        musicAdapter.setList(music, nowMusicNum);
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


    public void updateCdStatus(){



        tvLoadingExecute.setVisibility(View.GONE);

        if (tvMusicSize != null) {
            tvMusicSize.setText("总曲目：" + CDInstantStatus.getMusicSize());
        }
        int playStatus = CDInstantStatus.getPlayStatus();
        if (playStatus == 2) {      //暂停状态
            stopRotate();
            isPlay = 0;
            ivPlay.setImageDrawable(getResources().getDrawable(R.mipmap.jishi_but_stop_default));
        }
        if (playStatus == 1) {      //播放状态
            if (ivRecord.isOpaque()) {

            } else {
                rotate();
            }

            isPlay = 1;
            playDate = new Date();
            ivPlay.setImageDrawable(getResources().getDrawable(R.mipmap.jishi_but_play_default));
        }
        if (playStatus == 5) {      //停止状态
            stopRotate();
            isPlay = 0;
            ivPlay.setImageDrawable(getResources().getDrawable(R.mipmap.jishi_but_stop_default));
        }
        if (cdStatus != CDInstantStatus.getCdStatus()) {
            if (CDInstantStatus.getCdStatus() == 5) {
                tvLoading.setText("加载音乐中...");
                tvLoading.setVisibility(View.VISIBLE);
            }
            if (CDInstantStatus.getCdStatus() == 6) {
                tvLoading.setText("加载音乐完成...");
                tvLoading.setVisibility(View.GONE);
                ToastUtil.show(context, "加载音乐完成...", Toast.LENGTH_SHORT);
            }

            cdStatus = CDInstantStatus.getCdStatus();
        }


        if (saveDeviceType != CDInstantStatus.getSaveDeviceType()) {
            saveDeviceType = CDInstantStatus.getSaveDeviceType();
        }
        sbVolume.setProgress(CDInstantStatus.getDeviceVolume());
        tvVolume.setText(CDInstantStatus.getDeviceVolume() + "");
        if (CDInstantStatus.getDeviceVolume() == 0) {
            ivVolume.setImageDrawable(context.getResources().getDrawable(R.mipmap.jishi_icon_yinliang_close));
        } else {
            ivVolume.setImageDrawable(context.getResources().getDrawable(R.mipmap.jishi_icon_yinliang_open));
        }
        if (CDInstantStatus.getDeviceVolume() == 128) {
            tvVolume.setText("默认");
        }

        String musicName = CDInstantStatus.getMusicName();
        if (musicName == null || "".equals(musicName)) {
            tvMusicName.setText(CDInstantStatus.getNowMusicNum() + "." + "暂无歌曲名");
        } else {
            tvMusicName.setText(CDInstantStatus.getNowMusicNum() + "." + musicName);
        }
        nowMusicNum = CDInstantStatus.getNowMusicNum();
        switch (CDInstantStatus.getPlayModel()) {
            case 0:
                ivPlayMode.setImageDrawable(context.getResources().getDrawable(R.mipmap.jishi_but_shunxubofang_default));
                tvPlayMode.setText("顺序播放");
                break;
            case 1:
                ivPlayMode.setImageDrawable(context.getResources().getDrawable(R.mipmap.jishi_but_danquxunhuan_default));
                tvPlayMode.setText("单曲循环");
                break;
            case 2:
                ivPlayMode.setImageDrawable(context.getResources().getDrawable(R.mipmap.jishi_but_quanbuxunhuan_default));
                tvPlayMode.setText("全部循环");
                break;
            case 4:
                ivPlayMode.setImageDrawable(context.getResources().getDrawable(R.mipmap.jishi_but_danqubofang_default));
                tvPlayMode.setText("单曲播放");
                break;
        }
        switch (CDInstantStatus.getSaveDeviceType()) {
            case 0:
                ivDeviceType.setImageDrawable(context.getResources().getDrawable(R.mipmap.jishi_but_cd_default));
                tvDeviceType.setText("光盘");
                break;
            case 1:
                ivDeviceType.setImageDrawable(context.getResources().getDrawable(R.mipmap.jishi_but_usb_default));
                tvDeviceType.setText("U盘");
                break;
            case 2:
                ivDeviceType.setImageDrawable(context.getResources().getDrawable(R.mipmap.jishi_but_sd_default));
                tvDeviceType.setText("SD卡");
                break;
        }
        if (musicAdapter != null)
            musicAdapter.setList(music, nowMusicNum);
        String h = CDInstantStatus.getNowMusicTime().split(":")[0];
        String m = CDInstantStatus.getNowMusicTime().split(":")[1];
        String s = CDInstantStatus.getNowMusicTime().split(":")[2];
        String dateFormat = dateFormat(h + ":" + m + ":" + s);
        tvEndTime.setText(dateFormat);

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
