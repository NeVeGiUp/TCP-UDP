package com.itc.smartbroadcast.activity.event;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.bigkoo.pickerview.TimePickerView;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.event.utils.TaskUtils;
import com.itc.smartbroadcast.base.Base2Activity;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.BatchEditTaskResult;
import com.itc.smartbroadcast.bean.EditTaskResult;
import com.itc.smartbroadcast.bean.Task;
import com.itc.smartbroadcast.bean.TaskDetail;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.BatchEditTask;
import com.itc.smartbroadcast.channels.protocolhandler.EditTask;
import com.itc.smartbroadcast.channels.protocolhandler.GetTaskList;
import com.itc.smartbroadcast.util.ToastUtil;
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

public class BatchUpdateRingingTaskActivity extends Base2Activity {


    public static int REQUEST_START_TIME_CODE = 1;
    @BindView(R.id.bt_back_event)
    ImageView btBackEvent;
    @BindView(R.id.tv_edit_scheme_title)
    TextView tvEditSchemeTitle;
    @BindView(R.id.ll_done)
    LinearLayout llDone;
    @BindView(R.id.tv_start_date)
    TextView tvStartDate;
    @BindView(R.id.bt_startdate)
    RelativeLayout btStartdate;
    @BindView(R.id.tv_continue_time)
    TextView tvContinueTime;
    @BindView(R.id.bt_continuedtime)
    RelativeLayout btContinuedtime;

    private Context mContext;

    private List<Task> selectTaskList = new ArrayList<>();
    private List<Task> taskAllList = new ArrayList<>();
    private int position = 0;


    private TimePickerView timePickerViewStart;
    private TimePickerView timePickerViewContinue;

    private SimpleDateFormat formattype = new SimpleDateFormat("HH小时mm分ss秒");
    private SimpleDateFormat formattype2 = new SimpleDateFormat("HH:mm:ss");
    private SimpleDateFormat formattype3 = new SimpleDateFormat("yyyy-MM-dd");

    int startTimeDis = 0;


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            //修改任务
            if (msg.what == 0) {
                Task task = (Task) msg.obj;
                TaskDetail taskDetail = new TaskDetail();
                taskDetail.setTaskNum(task.getTaskNum());
                List<TaskDetail.Device> deviceList = new ArrayList<>();
                List<TaskDetail.Music> musicList = new ArrayList<TaskDetail.Music>();
                taskDetail.setDeviceList(deviceList);
                taskDetail.setMusicList(musicList);
                EditTask.sendCMD(AppDataCache.getInstance().getString("loginIp"), task, taskDetail, 1);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_update_ringing_task);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);
        initData();
    }

    public void initData() {
        mContext = this;
        Intent intent = getIntent();
        String taskListJson = intent.getStringExtra("taskListJson");
        String taskListAllJson = intent.getStringExtra("taskListAllJson");
        selectTaskList = JSONArray.parseArray(taskListJson, Task.class);
        taskAllList = JSONArray.parseArray(taskListAllJson, Task.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //注册EventBus
        EventBus.getDefault().register(this);
        //获取任务列表
        GetTaskList.sendCMD(AppDataCache.getInstance().getString("loginIp"));

    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
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
                    ToastUtil.show(mContext, "成功编辑" + (position + 1) + "个打铃任务!", Toast.LENGTH_LONG);
                    //获取任务列表
                    GetTaskList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
                    position++;
                    if (position == selectTaskList.size()) {
                        BatchUpdateRingingTaskActivity.this.finish();
                    }
                } else {
                    ToastUtil.show(mContext, "打铃任务 " + selectTaskList.get(position).getTaskName() + "编辑失败!");
                }

            } else {
                ToastUtil.show(mContext, "操作失败，请检查数据以及网络!");
            }
        }
        if ("batchEditTaskResult".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                BatchEditTaskResult batchEditTaskResult = gson.fromJson(data, BatchEditTaskResult.class);
                if (batchEditTaskResult.getResult() == 1) {
                    ToastUtil.show(mContext, "批量编辑成功！", Toast.LENGTH_LONG);
                    BatchUpdateRingingTaskActivity.this.finish();

                } else {
                    ToastUtil.show(mContext, "批量编辑失败！", Toast.LENGTH_LONG);
                }
                //获取任务列表
                GetTaskList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
            } else {
                ToastUtil.show(mContext, "批量编辑失败!");
            }
        }

    }

    private void checkDate() {

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = View.inflate(mContext, R.layout.dialog_tips, null);
        final TextView tvMsg = (TextView) view.findViewById(R.id.tv_msg);
        final Button btnOk = (Button) view.findViewById(R.id.btn_ok);
        final Button btnNo = (Button) view.findViewById(R.id.btn_no);
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(view);
        tvMsg.setText("任务执行时间冲突，请调整任务时间。");
        btnNo.setVisibility(View.GONE);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


    private void updateRingingTask() {

        final String continueTime = tvContinueTime.getText().toString().trim();

        if (continueTime.equals("00:00:00")) {
            ToastUtil.show(mContext, "持续时间不能为0！");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View v = View.inflate(mContext, R.layout.dialog_tips, null);
        final TextView tvMsg = (TextView) v.findViewById(R.id.tv_msg);
        final Button btnOk = (Button) v.findViewById(R.id.btn_ok);
        final Button btnNo = (Button) v.findViewById(R.id.btn_no);
        final android.app.AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(v);
        tvMsg.setText("打铃任务将被批量修改，确定修改吗？");
        btnNo.setVisibility(View.VISIBLE);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                position = 0;


                List<Task> taskList = taskAllList;
                List<Task> comTaskList = new ArrayList<>();


                for (Task task : selectTaskList) {


                    if (!(continueTime == null || continueTime.equals(""))) {
                        int h = Integer.parseInt(continueTime.split(":")[0]);
                        int m = Integer.parseInt(continueTime.split(":")[1]);
                        int s = Integer.parseInt(continueTime.split(":")[2]);
                        int continueDate = (h * 60 * 60) + (m * 60) + s;
                        task.setTaskContinueDate(continueDate);
                    }
                    try {
                        Date startDate = formattype2.parse(task.getTaskStartDate());
                        String StartTime = formattype2.format(startDate.getTime() + (startTimeDis * 1000));
                        task.setTaskStartDate(StartTime);

                        for (int i = 0; i < taskList.size(); i++) {
                            if (task.getTaskNum() == taskList.get(i).getTaskNum()) {
                                taskList.set(i, task);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                for (Task task : taskList) {
                    comTaskList.add(task);
                }

                for (Task task : selectTaskList) {
                    for (int i = 0; i < comTaskList.size(); i++) {
                        if (comTaskList.get(i).getTaskNum() == task.getTaskNum()) {
                            comTaskList.remove(i);
                        }
                    }
                    if (!TaskUtils.checkTaskTime(comTaskList, task)) {
                        checkDate();
                        return;
                    }
                    for (Task task1 : taskList) {
                        comTaskList.add(task1);
                    }
                }

                int continueDate = 0;
                if (!(continueTime == null || continueTime.equals(""))) {
                    int h = Integer.parseInt(continueTime.split(":")[0]);
                    int m = Integer.parseInt(continueTime.split(":")[1]);
                    int s = Integer.parseInt(continueTime.split(":")[2]);
                    continueDate = (h * 60 * 60) + (m * 60) + s;
                }


                BatchEditTask.sendCMD(AppDataCache.getInstance().getString("loginIp"), selectTaskList, 0, continueDate, startTimeDis);

//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        for (Task task : selectTaskList) {
//                            Message msg = new Message();
//                            msg.what = 0;
//                            msg.obj = task;
//                            handler.sendMessage(msg);
//                            try {
//                                Thread.sleep(400);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }).start();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    @OnClick({R.id.bt_back_event, R.id.bt_startdate, R.id.bt_continuedtime, R.id.ll_done})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_back_event:
                BatchUpdateRingingTaskActivity.this.finish();
                break;
            case R.id.bt_startdate:
                getStartTime();
                break;
            case R.id.bt_continuedtime:
                showContinueTime();
                break;
            case R.id.ll_done:
                updateRingingTask();
                break;
        }
    }


    public void getStartTime() {
        Intent intent = new Intent(mContext, SelectStartTimeActivity.class);
        startActivityForResult(intent, REQUEST_START_TIME_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_START_TIME_CODE == requestCode) {        //开始时间

            startTimeDis = data.getIntExtra("startTimeDis", 0);

            if (startTimeDis > 0) {
                tvStartDate.setText("延后" + startTimeDis + "秒");
            } else {
                tvStartDate.setText("提前" + (-startTimeDis) + "秒");
            }
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
}
