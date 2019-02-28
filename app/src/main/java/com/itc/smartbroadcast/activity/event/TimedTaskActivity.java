package com.itc.smartbroadcast.activity.event;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.event.utils.TaskUtils;
import com.itc.smartbroadcast.adapter.child.EventChildTaskAdapter;
import com.itc.smartbroadcast.base.Base2Activity;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditTaskResult;
import com.itc.smartbroadcast.bean.Scheme;
import com.itc.smartbroadcast.bean.Task;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.GetTaskList;
import com.itc.smartbroadcast.popupwindow.MoreWindow;
import com.itc.smartbroadcast.util.DateUtil;
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

/**
 * 定时任务列表页面
 * create by youmu on 2018/7
 */
public class TimedTaskActivity extends Base2Activity {

    @BindView(R.id.iv_timed_task_back)
    ImageView ivTimedTaskBack;
    @BindView(R.id.iv_showWindow)
    ImageView ivShowWindow;
    @BindView(R.id.ll_timed_task_add_task)
    LinearLayout llTimedTaskAddTask;
    @BindView(R.id.rv_timed_task)
    RecyclerView rvTimedTask;
    @BindView(R.id.ll_timed_task_list)
    LinearLayout llTimedTaskList;
    @BindView(R.id.btn_timed_task_create_task)
    Button btnTimedTaskCreateTask;
    @BindView(R.id.ll_timed_task_create_task)
    LinearLayout llTimedTaskCreateTask;
    @BindView(R.id.timedtask_top)
    TabLayout timedtaskTop;
    @BindView(R.id.ll_showWindow)
    LinearLayout llShowWindow;
    @BindView(R.id.all_task_view_tv)
    TextView allTaskViewTv;
    @BindView(R.id.all_task_view_iv)
    ImageView allTaskViewIv;
    @BindView(R.id.all_task_view)
    RelativeLayout allTaskView;

    private MoreWindow mMoreWindow;

    private Context mContext;
    private List<Task> taskList = new ArrayList<>();
    private List<Task> useList = new ArrayList<>();
    EventChildTaskAdapter eventChildTaskAdapter;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timedtask);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);
        initData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //注册EventBus
        EventBus.getDefault().register(this);
        //获取任务列表
        GetTaskList.sendCMD(AppDataCache.getInstance().getString("loginIp"));

        llTimedTaskAddTask.setEnabled(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }


    public void initData() {

        mContext = this;
        rvTimedTask.setLayoutManager(new LinearLayoutManager(this));
        rvTimedTask.setHasFixedSize(true);
        rvTimedTask.setFocusableInTouchMode(false);
        rvTimedTask.requestFocus();
        eventChildTaskAdapter = new EventChildTaskAdapter(mContext);
        rvTimedTask.setAdapter(eventChildTaskAdapter);

        llTimedTaskCreateTask.setVisibility(View.VISIBLE);
        llTimedTaskList.setVisibility(View.GONE);
        allTaskView.setVisibility(View.GONE);

        timedtaskTop.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectTab(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    public void selectTab(int position) {
        switch (position) {
            case 0:
                useList.clear();
                useList.addAll(taskList);
                if (useList.size() > 0) {
                    llTimedTaskCreateTask.setVisibility(View.GONE);
                    llTimedTaskList.setVisibility(View.VISIBLE);
                    eventChildTaskAdapter.setList(useList);
                } else {
                    llTimedTaskCreateTask.setVisibility(View.VISIBLE);
                    llTimedTaskList.setVisibility(View.GONE);
                }
                allTaskView.setVisibility(View.GONE);
                break;
            case 1:
                useList.clear();
                for (Task task : taskList) {
                    if (task.getTaskStatus() == 0) {
                        continue;
                    }
                    if (isRunning(task)) {
                        continue;
                    }
                    if (isNoBegin(task)) {
                        useList.add(task);
                    }
                }
                if (useList.size() > 0) {
                    llTimedTaskCreateTask.setVisibility(View.GONE);
                    llTimedTaskList.setVisibility(View.VISIBLE);
                    eventChildTaskAdapter.setList(useList);
                } else {
                    llTimedTaskCreateTask.setVisibility(View.VISIBLE);
                    llTimedTaskList.setVisibility(View.GONE);
                }
                allTaskView.setVisibility(View.VISIBLE);
                break;


            case 2:
                useList.clear();

                for (Task task : taskList) {

                    if (task.getTaskStatus() == 0) {
                        continue;
                    }
                    if (isRunning(task)) {
                        useList.add(task);
                    }
                }
                if (useList.size() > 0) {
                    llTimedTaskCreateTask.setVisibility(View.GONE);
                    llTimedTaskList.setVisibility(View.VISIBLE);
                    eventChildTaskAdapter.setList(useList);
                } else {
                    llTimedTaskCreateTask.setVisibility(View.VISIBLE);
                    llTimedTaskList.setVisibility(View.GONE);
                }
                allTaskView.setVisibility(View.GONE);
                break;
            case 3:
                useList.clear();
                for (Task task : taskList) {
                    if (task.getTaskStatus() == 0) {
                        continue;
                    }
                    if (!isNoBegin(task)) {
                        useList.add(task);
                    }
                }
                if (useList.size() > 0) {
                    llTimedTaskCreateTask.setVisibility(View.GONE);
                    llTimedTaskList.setVisibility(View.VISIBLE);
                    eventChildTaskAdapter.setList(useList);
                } else {
                    llTimedTaskCreateTask.setVisibility(View.VISIBLE);
                    llTimedTaskList.setVisibility(View.GONE);
                }
                allTaskView.setVisibility(View.GONE);
                break;
        }
    }

    public boolean isStart(Task task) {
        if (task.getTaskStatus() == 1) {
            return true;
        } else {
            return false;
        }

    }


    public boolean isRunning(Task task) {

        boolean bol = false;
        Date nowDate = new Date();

        if (isNoBegin(task)) {
            try {
                Date startDate = sdf2.parse(task.getTaskStartDate());

                String todayStr = sdf1.format(nowDate);
                boolean flagBol = false;
                for (String dateStr : task.getTaskDateDuplicationPattern()) {
                    if (todayStr.equals(dateStr)) {
                        flagBol = true;
                    }
                }

                int startS = (startDate.getHours() * 60 * 60 + startDate.getMinutes() * 60 + startDate.getSeconds() + task.getTaskContinueDate());

                if (startS > (24 * 60 * 60)) {
                    if ((nowDate.getHours() * 60 * 60 + nowDate.getMinutes() * 60 + nowDate.getSeconds()) <= (startDate.getHours() * 60 * 60 + startDate.getMinutes() * 60 + startDate.getSeconds() + task.getTaskContinueDate())
                            && ((nowDate.getHours() * 60 * 60 + nowDate.getMinutes() * 60 + nowDate.getSeconds()) >= (startDate.getHours() * 60 * 60 + startDate.getMinutes() * 60 + startDate.getSeconds())
                            || (nowDate.getHours() * 60 * 60 + nowDate.getMinutes() * 60 + nowDate.getSeconds()) <= (startDate.getHours() * 60 * 60 + startDate.getMinutes() * 60 + startDate.getSeconds() + task.getTaskContinueDate()) - 24 * 60 * 60)) {
                        return true;
                    }
                } else {
                    if ((nowDate.getHours() * 60 * 60 + nowDate.getMinutes() * 60 + nowDate.getSeconds()) < (startDate.getHours() * 60 * 60 + startDate.getMinutes() * 60 + startDate.getSeconds() + task.getTaskContinueDate())
                            && (nowDate.getHours() * 60 * 60 + nowDate.getMinutes() * 60 + nowDate.getSeconds()) > (startDate.getHours() * 60 * 60 + startDate.getMinutes() * 60 + startDate.getSeconds())
                            && flagBol) {
                        return true;
                    } else {
                        return false;
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return bol;
    }

    public boolean isNoBegin(Task task) {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");

        boolean bol = false;
        Date nowDate = new Date();
        String lastDateStr = DateUtil.getDateLast(task.getTaskDateDuplicationPattern());
        lastDateStr = lastDateStr + " " + task.getTaskStartDate();
        try {
            Date startDate = sdf.parse(lastDateStr);
            Date lastDate = new Date(startDate.getTime() + (task.getTaskContinueDate() * 1000));

            if ((nowDate.getTime() - lastDate.getTime() <= 0)) {    //未开始
                bol = true;
            } else {
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return bol;
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

        if ("getTaskList".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                taskList.clear();
                List<Task> list = JSONArray.parseArray(data, Task.class);

                for (Task task : list) {
                    if (task.getSchemeNum() == 255) {
                        taskList.add(task);
                    }
                }
                //排序
                taskList = TaskUtils.sort(taskList);
                useList.clear();
                useList.addAll(taskList);
                if (taskList.size() > 0) {
                    llTimedTaskCreateTask.setVisibility(View.GONE);
                    llTimedTaskList.setVisibility(View.VISIBLE);
                    eventChildTaskAdapter.setList(useList);
                } else {
                    llTimedTaskCreateTask.setVisibility(View.VISIBLE);
                    llTimedTaskList.setVisibility(View.GONE);
                }
            }
            selectTab(timedtaskTop.getSelectedTabPosition());
        }

        if ("getSchemeList".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                List<Scheme> schemeList = JSONArray.parseArray(data, Scheme.class);
                //设置使用中的方案
                List<Scheme> schemeListToUse = new ArrayList<>();
                for (Scheme scheme : schemeList) {
                    if (scheme.getSchemeStatus() == 1) {
                        schemeListToUse.add(scheme);
                    }
                }
            }
        }

        if ("editTaskResult".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                EditTaskResult editTaskResult = gson.fromJson(data, EditTaskResult.class);
                if (editTaskResult.getResult() == 0) {
                    ToastUtil.show(mContext, "操作成功!");
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //获取任务列表
                    GetTaskList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
                } else {
                    ToastUtil.show(mContext, "操作失败!");
                }
            } else {
                ToastUtil.show(mContext, "操作失败，请检查数据以及网络!");
            }
        }
    }


    @OnClick({R.id.iv_timed_task_back, R.id.btn_timed_task_create_task, R.id.ll_timed_task_create_task, R.id.iv_showWindow, R.id.ll_showWindow, R.id.all_task_view_tv, R.id.ll_timed_task_add_task})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_timed_task_back:
                finish();
                break;
            case R.id.ll_timed_task_add_task:
                if (TaskUtils.getIsManager()) {
                    createTask();
                } else {
                    ToastUtil.show(mContext, "对不起，普通用户没有该功能权限！");
                }
                break;
            case R.id.btn_timed_task_create_task:
                if (TaskUtils.getIsManager()) {
                    createTask();
                } else {
                    ToastUtil.show(mContext, "对不起，普通用户没有该功能权限！");
                }
                break;
            case R.id.iv_showWindow:
                showMoreWindow(view);
                break;
            case R.id.ll_showWindow:
                showMoreWindow(view);
                break;
            case R.id.all_task_view_tv:
                showAllTaskPop(view);
                break;
        }
    }

    //显示全部任务筛选菜单列表
    private void showAllTaskPop(View v) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_pop_view, null, false);
        //1.构造一个PopupWindow，参数依次是加载的View，宽高
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popWindow.setAnimationStyle(R.anim.anim_pop);  //设置加载动画

        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        popWindow.setBackgroundDrawable(new ColorDrawable(0xFFFFFF));
        popWindow.showAsDropDown(v, 30, 0);
//		mraRatingBar.setEnabled(false);
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
//        lp.alpha = 0.7f;
        getActivity().getWindow().setAttributes(lp);
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1f;
                getActivity().getWindow().setAttributes(lp);
            }
        });

        TextView allTaskTv = (TextView) view.findViewById(R.id.all_task_tv);
        TextView ringTaskTv = (TextView) view.findViewById(R.id.ring_task_tv);
        TextView timingTaskTv = (TextView) view.findViewById(R.id.timing_task_tv);
        allTaskTv.setText("查看全部");
        ringTaskTv.setText("只看今日");
        timingTaskTv.setText("查看明天");

        allTaskTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindow.dismiss();
                allTaskViewTv.setText("查看全部");
                eventChildTaskAdapter.setList(useList);
            }
        });

        ringTaskTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindow.dismiss();
                allTaskViewTv.setText("只看今日");

                List<Task> taskList = new ArrayList<>();
                Date todayDate = new Date();
                String todayStr = sdf1.format(todayDate);
                for (Task task : useList) {
                    boolean bol = false;
                    for (String date : task.getTaskDateDuplicationPattern()) {
                        if (date.equals(todayStr)) {
                            bol = true;
                        }
                    }
                    if (bol) {
                        taskList.add(task);
                    }
                }
                eventChildTaskAdapter.setList(taskList);
            }
        });

        timingTaskTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindow.dismiss();
                allTaskViewTv.setText("查看明天");

                List<Task> taskList = new ArrayList<>();
                Date todayDate = new Date();
                Date tomorrowDate = new Date(todayDate.getTime() + (24 * 60 * 60 * 1000));
                String tomorrowStr = sdf1.format(tomorrowDate);
                for (Task task : useList) {
                    boolean bol = false;
                    for (String date : task.getTaskDateDuplicationPattern()) {
                        if (date.equals(tomorrowStr)) {
                            bol = true;
                        }
                    }
                    if (bol) {
                        taskList.add(task);
                    }
                }
                eventChildTaskAdapter.setList(taskList);
            }
        });
    }

    public TimedTaskActivity getActivity() {
        return this;
    }


    private void showMoreWindow(View view) {
        if (null == mMoreWindow) {
            mMoreWindow = new MoreWindow(this);
            mMoreWindow.init();
        }
        mMoreWindow.showMoreWindow(view, 100);
    }


    public void createTask() {
        llTimedTaskAddTask.setEnabled(false);
        Intent intent = new Intent(mContext, CreateTimedTaskActivity.class);
        startActivity(intent);


    }

}
