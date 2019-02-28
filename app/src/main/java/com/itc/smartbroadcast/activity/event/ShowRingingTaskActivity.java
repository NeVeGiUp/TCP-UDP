package com.itc.smartbroadcast.activity.event;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.event.utils.TaskUtils;
import com.itc.smartbroadcast.adapter.MyAdapter;
import com.itc.smartbroadcast.adapter.child.EventChildSchemeAdapter;
import com.itc.smartbroadcast.base.Base2Activity;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditSchemeResult;
import com.itc.smartbroadcast.bean.RingingTask;
import com.itc.smartbroadcast.bean.Scheme;
import com.itc.smartbroadcast.bean.Task;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.GetSchemeList;
import com.itc.smartbroadcast.channels.protocolhandler.GetTaskList;
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

public class ShowRingingTaskActivity extends Base2Activity implements View.OnClickListener {

    @BindView(R.id.bt_back_event)
    ImageView btBackEvent;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.rv_ringing_task_use)
    RecyclerView rvRingingTaskUse;
    @BindView(R.id.ll_add_ringing_task)
    LinearLayout llAddRingingTask;
    @BindView(R.id.rv_ringing_task_all)
    RecyclerView rvRingingTaskAll;
    @BindView(R.id.iv_showWindow)
    ImageView ivShowWindow;
    @BindView(R.id.ll_showWindow)
    LinearLayout llShowWindow;


    private Context mContext;
    private MyAdapter<RingingTask> RtAdapter = null;
    private List<RingingTask> RtData = null;
    private List<Task> taskList = null;

    private MoreWindow mMoreWindow;

    EventChildSchemeAdapter eventChildSchemeAdapter;
    EventChildSchemeAdapter eventChildSchemeAdapterToUse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_show_ringing_task);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);
        mContext = this;
        initData();

        llAddRingingTask.setOnClickListener(this);
        ivShowWindow.setOnClickListener(this);
        llShowWindow.setOnClickListener(this);
        btBackEvent.setOnClickListener(this);
    }

    public void initData() {

        rvRingingTaskUse.setLayoutManager(new LinearLayoutManager(this));
        rvRingingTaskUse.setHasFixedSize(true);
        rvRingingTaskUse.setFocusableInTouchMode(false);
        rvRingingTaskUse.requestFocus();
        rvRingingTaskAll.setLayoutManager(new LinearLayoutManager(this));
        rvRingingTaskAll.setHasFixedSize(true);
        rvRingingTaskAll.setFocusableInTouchMode(false);
        rvRingingTaskAll.requestFocus();

        eventChildSchemeAdapter = new EventChildSchemeAdapter(this);
        rvRingingTaskAll.setAdapter(eventChildSchemeAdapter);
        eventChildSchemeAdapterToUse = new EventChildSchemeAdapter(this);
        rvRingingTaskUse.setAdapter(eventChildSchemeAdapterToUse);

        rvRingingTaskUse.setOnClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        //注册EventBus
        EventBus.getDefault().register(this);
        //获取任务列表
        GetTaskList.sendCMD(AppDataCache.getInstance().getString("loginIp"));

        llAddRingingTask.setEnabled(true);

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
                //获取方案列表
                GetSchemeList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
                taskList = JSONArray.parseArray(data, Task.class);
            }
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
                eventChildSchemeAdapterToUse.setList(schemeListToUse, taskList);

                //方案排序
                schemeList = TaskUtils.schemeListOrder(schemeList);
                //设置所有方案
                eventChildSchemeAdapter.setList(schemeList, taskList);
            }
        }

        if ("editSchemeResult".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                EditSchemeResult editSchemeResult = gson.fromJson(data, EditSchemeResult.class);
                if (editSchemeResult.getResult() == 1) {
                    ToastUtil.show(mContext, "操作成功!");
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //获取任务列表
                    GetTaskList.sendCMD(AppDataCache.getInstance().getString("loginIp"));

                }
            } else {
                ToastUtil.show(mContext, "操作失败，请检查数据以及网络!");
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_add_ringing_task:      //添加打铃方案
                if (TaskUtils.getIsManager()) {
                    Intent ringingTaskIntent = new Intent(mContext, CreateSchemeActivity.class);
                    mContext.startActivity(ringingTaskIntent);
                    llAddRingingTask.setEnabled(false);
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
            case R.id.bt_back_event:
                this.finish();
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

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }
}
