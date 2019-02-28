package com.itc.smartbroadcast.activity.event;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.event.utils.TaskUtils;
import com.itc.smartbroadcast.adapter.event.BatchSchemeDetailAdapter;
import com.itc.smartbroadcast.base.Base2Activity;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.BatchEditTaskResult;
import com.itc.smartbroadcast.bean.EditTaskResult;
import com.itc.smartbroadcast.bean.Scheme;
import com.itc.smartbroadcast.bean.Task;
import com.itc.smartbroadcast.bean.TaskDetail;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.BatchEditTask;
import com.itc.smartbroadcast.channels.protocolhandler.EditInstantTask;
import com.itc.smartbroadcast.channels.protocolhandler.EditTask;
import com.itc.smartbroadcast.channels.protocolhandler.GetSchemeList;
import com.itc.smartbroadcast.channels.protocolhandler.GetTaskList;
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

public class BatchManagerRingingTaskActivity extends Base2Activity {

    @BindView(R.id.tv_all_select_event)
    TextView tvAllSelectEvent;
    @BindView(R.id.ll_all_select_event)
    LinearLayout llAllSelectEvent;
    @BindView(R.id.ll_close)
    LinearLayout llClose;
    @BindView(R.id.rv_ringing_task)
    RecyclerView rvRingingTask;
    @BindView(R.id.ll_delete)
    LinearLayout llDelete;
    @BindView(R.id.ll_update)
    LinearLayout llUpdate;
    @BindView(R.id.ll_no_data)
    LinearLayout llNoData;

    private Context mContext;
    private BatchSchemeDetailAdapter batchSchemeDetailAdapter;
    private int scheme_num;
    private List<Task> taskList = new ArrayList<>();
    private List<Scheme> schemeList = new ArrayList<>();
    private Scheme scheme;

    private List<Task> selectTaskList = new ArrayList<>();
    private int position = 0;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            //删除任务
            if (msg.what == 0) {
                Task task = (Task) msg.obj;
                TaskDetail taskDetail = new TaskDetail();
                taskDetail.setTaskNum(task.getTaskNum());
                List<TaskDetail.Device> deviceList = new ArrayList<>();
                List<TaskDetail.Music> musicList = new ArrayList<TaskDetail.Music>();
                taskDetail.setDeviceList(deviceList);
                taskDetail.setMusicList(musicList);
                EditTask.sendCMD(AppDataCache.getInstance().getString("loginIp"), task, taskDetail, 2);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_manager_ringing_task);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);

        initData();
    }

    public void initData() {

        mContext = this;

        Intent intent = getIntent();
        scheme_num = intent.getIntExtra("scheme_num", 99999999);

        rvRingingTask.setLayoutManager(new LinearLayoutManager(this));
        rvRingingTask.setHasFixedSize(true);
        rvRingingTask.setFocusableInTouchMode(false);
        rvRingingTask.requestFocus();

        batchSchemeDetailAdapter = new BatchSchemeDetailAdapter(mContext);
        rvRingingTask.setAdapter(batchSchemeDetailAdapter);
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

        //获取任务列表
        if ("getTaskList".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                //获取方案列表
                GetSchemeList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
                List<Task> taskListAll = JSONArray.parseArray(data, Task.class);
                taskList.clear();
                for (Task task : taskListAll) {
                    if (task.getSchemeNum() == this.scheme_num) {
                        taskList.add(task);
                    }
                }
                //排序
                taskList = TaskUtils.sort(taskList);
                if (taskList.size() > 0) {
                    llNoData.setVisibility(View.GONE);
                    rvRingingTask.setVisibility(View.VISIBLE);
                } else {
                    llNoData.setVisibility(View.VISIBLE);
                    rvRingingTask.setVisibility(View.GONE);
                }
                batchSchemeDetailAdapter.setList(taskList);
            }
        }
        //获取方案列表
        if ("getSchemeList".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                schemeList = JSONArray.parseArray(data, Scheme.class);
                //设置使用中的方案
                for (Scheme scheme : schemeList) {
                    if (scheme.getSchemeNum() == scheme_num) {
                        this.scheme = scheme;
                    }
                }
            }
        }
        if ("editTaskResult".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                EditTaskResult editTaskResult = gson.fromJson(data, EditTaskResult.class);
                if (editTaskResult.getResult() == 0) {
                    ToastUtil.show(mContext, "成功删除" + (position + 1) + "个打铃任务!", Toast.LENGTH_LONG);
                    //获取任务列表
                    GetTaskList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
                    position++;
                } else {
                    ToastUtil.show(mContext, "打铃任务 " + selectTaskList.get(position).getTaskName() + "删除失败!");
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
                    ToastUtil.show(mContext, "批量删除成功！", Toast.LENGTH_LONG);
                } else {
                    ToastUtil.show(mContext, "批量删除失败！", Toast.LENGTH_LONG);
                }
                //获取任务列表
                GetTaskList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
            } else {
                ToastUtil.show(mContext, "批量删除失败!");
            }
        }

    }

    @OnClick({R.id.ll_all_select_event, R.id.ll_close, R.id.ll_delete, R.id.ll_update})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_all_select_event:
                if (tvAllSelectEvent.getText().toString().equals("全选")) {
                    tvAllSelectEvent.setText("反选");
                    batchSchemeDetailAdapter.setCheckAll();
                } else {
                    tvAllSelectEvent.setText("全选");
                    batchSchemeDetailAdapter.setNoCheckAll();
                }
                break;
            case R.id.ll_close:
                BatchManagerRingingTaskActivity.this.finish();
                break;
            case R.id.ll_delete:
                selectTaskList = batchSchemeDetailAdapter.getCheckBoxIDList();
                if (selectTaskList.size() <= 0) {
                    ToastUtil.show(mContext, "请选择需要删除的打铃任务！");
                    break;
                }
                deleteRingingTask(selectTaskList);
                break;
            case R.id.ll_update:
                selectTaskList = batchSchemeDetailAdapter.getCheckBoxIDList();
                if (selectTaskList.size() <= 0) {
                    ToastUtil.show(mContext, "请选择需要编辑的打铃任务！");
                    break;
                }
                String taskListJson = JSONArray.toJSONString(selectTaskList);
                String taskListAllJson = JSONArray.toJSONString(taskList);
                Intent intent = new Intent(mContext, BatchUpdateRingingTaskActivity.class);
                intent.putExtra("taskListJson", taskListJson);
                intent.putExtra("taskListAllJson", taskListAllJson);
                startActivity(intent);
                BatchManagerRingingTaskActivity.this.finish();
                break;
        }
    }

    private void deleteRingingTask(final List<Task> checkTaskList) {


        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View v = View.inflate(mContext, R.layout.dialog_tips, null);
        final TextView tvMsg = (TextView) v.findViewById(R.id.tv_msg);
        final Button btnOk = (Button) v.findViewById(R.id.btn_ok);
        final Button btnNo = (Button) v.findViewById(R.id.btn_no);
        final android.app.AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(v);
        tvMsg.setText("打铃任务将被批量删除，确定删除吗？");
        btnNo.setVisibility(View.VISIBLE);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                selectTaskList = checkTaskList;
                position = 0;
                BatchEditTask.sendCMD(AppDataCache.getInstance().getString("loginIp"), checkTaskList, 1, 0, 0);

//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        for (Task task : checkTaskList) {
//                            Message msg = new Message();
//                            msg.what = 0;
//                            msg.obj = task;
//                            handler.sendMessage(msg);
//                            try {
//                                Thread.sleep(500);
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }).start();

            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}
