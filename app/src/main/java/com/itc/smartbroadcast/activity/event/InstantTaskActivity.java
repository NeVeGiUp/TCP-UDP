package com.itc.smartbroadcast.activity.event;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.event.utils.TaskUtils;
import com.itc.smartbroadcast.adapter.child.EventChildInstantTaskAdapter;
import com.itc.smartbroadcast.base.Base2Activity;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditInstantTaskResult;
import com.itc.smartbroadcast.bean.InstantTask;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.GetInstantTaskList;
import com.itc.smartbroadcast.popupwindow.MoreWindow;
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

public class InstantTaskActivity extends Base2Activity {
    @BindView(R.id.bt_back_event)
    ImageView btBackEvent;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.btn_add_instant_task)
    Button btnAddInstantTask;
    @BindView(R.id.ll_instant_task_no_data)
    LinearLayout llInstantTaskNoData;
    @BindView(R.id.ll_add_instant_task)
    LinearLayout llAddInstantTask;
    @BindView(R.id.rv_instant_task_all)
    RecyclerView rvInstantTaskAll;
    @BindView(R.id.ll_instant_task)
    LinearLayout llInstantTask;

    List<InstantTask> instantTaskList = new ArrayList<>();

    EventChildInstantTaskAdapter eventChildInstantTaskAdapter;
    @BindView(R.id.im_add_instant_task)
    ImageView imAddInstantTask;
    @BindView(R.id.ll_showWindow)
    LinearLayout llShowWindow;

    private MoreWindow mMoreWindow;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instant_task);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);
        initView();
    }

    private void initView() {
        context = this;
        rvInstantTaskAll.setLayoutManager(new LinearLayoutManager(this));
        rvInstantTaskAll.setHasFixedSize(true);
        rvInstantTaskAll.setFocusableInTouchMode(false);
        rvInstantTaskAll.requestFocus();
        eventChildInstantTaskAdapter = new EventChildInstantTaskAdapter(this);
        rvInstantTaskAll.setAdapter(eventChildInstantTaskAdapter);

    }

    @OnClick({R.id.bt_back_event, R.id.btn_add_instant_task, R.id.ll_add_instant_task, R.id.im_add_instant_task, R.id.ll_showWindow})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_back_event:
                finish();
                break;
            case R.id.btn_add_instant_task:
                addInstantTask();
                break;
            case R.id.ll_showWindow:
                addInstantTask();
                break;
            case R.id.ll_add_instant_task:
                addInstantTask();
                break;
            case R.id.im_add_instant_task:
                showMoreWindow(view);
                break;
        }
    }

    private void showMoreWindow(View view) {
        if (null == mMoreWindow) {
            mMoreWindow = new MoreWindow(this);
            mMoreWindow.init();
        }
        mMoreWindow.showMoreWindow(view, 100);
    }

    private void addInstantTask() {
        Intent intent = new Intent(this, CreateInstantTaskActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        //获取即时任务列表
        GetInstantTaskList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
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
        if ("getInstantTaskList".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                AppDataCache.getInstance().putString("getInstantTaskList", data);
                instantTaskList = JSONArray.parseArray(data, InstantTask.class);
                if (instantTaskList.size() > 0) {
                    llInstantTaskNoData.setVisibility(View.GONE);
                    llInstantTask.setVisibility(View.VISIBLE);

                    instantTaskList = TaskUtils.installTaskListOrder(instantTaskList);

                    eventChildInstantTaskAdapter.setList(instantTaskList);
                } else {
                    llInstantTaskNoData.setVisibility(View.VISIBLE);
                    llInstantTask.setVisibility(View.GONE);
                }
            } else {

            }
        }
        if ("editInstantTaskResult".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                EditInstantTaskResult editInstantTaskResult = gson.fromJson(data, EditInstantTaskResult.class);
                if (editInstantTaskResult.getResult() == 1) {
                    ToastUtil.show(context, "操作成功!");
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //获取即时任务列表
                    GetInstantTaskList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
                } else {
                    ToastUtil.show(context, "操作失败，请检查数据以及网络!");
                }
            } else {
                ToastUtil.show(context, "操作失败，请检查数据以及网络!");
            }
        }
    }
}
