package com.itc.smartbroadcast.activity.event;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.event.utils.TaskUtils;
import com.itc.smartbroadcast.adapter.event.SchemeDetailAdapter;
import com.itc.smartbroadcast.base.Base2Activity;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditSchemeResult;
import com.itc.smartbroadcast.bean.EditTaskResult;
import com.itc.smartbroadcast.bean.Scheme;
import com.itc.smartbroadcast.bean.Task;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.EditInstantTask;
import com.itc.smartbroadcast.channels.protocolhandler.EditScheme;
import com.itc.smartbroadcast.channels.protocolhandler.GetSchemeList;
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

public class RingingTaskDetailActivity extends Base2Activity {

    //初始化界面信息
    @BindView(R.id.bt_ringing_detail_back)
    ImageView btRingingDetailBack;  //返回按钮
    @BindView(R.id.iv_menu)
    ImageView ivMenu;   //菜单
    @BindView(R.id.tv_ringing_detail_name)
    TextView tvRingingDetailName;   //方案名
    @BindView(R.id.tv_ringing_detail_start_time)
    TextView tvRingingDetailStartTime;    //方案开始时间
    @BindView(R.id.tv_ringing_detail_end_time)
    TextView tvRingingDetailEndTime;    //方案结束时间
    @BindView(R.id.tv_ringing_detail_task_mount)
    TextView tvRingingDetailTaskMount;    //任务总数
    @BindView(R.id.btn_ringing_detail_create_task_head)
    LinearLayout btnRingingDetailCreateTaskHead;    //创建任务按钮头部
    @BindView(R.id.btn_ringing_detail_batch_create_task)
    LinearLayout btnRingingDetailBatchCreateTask;   //批量创建任务
    @BindView(R.id.btn_ringing_detail_create_task)
    LinearLayout btnRingingDetailCreateTask;    //创建任务按钮底部
    @BindView(R.id.ll_ringing_detail_create_task)
    LinearLayout llRingingDetailCreateTask;     //创建任务布局
    @BindView(R.id.rv_ringing_detail_task)
    RecyclerView rvRingingDetailTask;   //任务RV
    @BindView(R.id.ll_ringing_detail_task)
    LinearLayout llRingingDetailTask;   //任务布局
    @BindView(R.id.ll_menu)
    LinearLayout llMenu;
    @BindView(R.id.gong_tv)
    TextView gongTv;
    @BindView(R.id.index_ringingtask_rv)
    RelativeLayout indexRingingtaskRv;
    @BindView(R.id.iv_create_task)
    ImageView ivCreateTask;
    @BindView(R.id.iv_set_task)
    ImageView ivSetTask;

    private Context mContext;
    private List<Task> taskList = new ArrayList<>();
    private List<Scheme> schemeList = new ArrayList<>();
    private Scheme scheme;
    private int scheme_num;

    private SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd");

    private SchemeDetailAdapter schemeDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringing_task_detail);
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

        btnRingingDetailCreateTask.setEnabled(true);
        btnRingingDetailCreateTaskHead.setEnabled(true);
        btnRingingDetailBatchCreateTask.setEnabled(true);

    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    public void initData() {


        mContext = this;

        Intent intent = getIntent();
        scheme_num = intent.getIntExtra("scheme_num", 99999999);

        rvRingingDetailTask.setLayoutManager(new LinearLayoutManager(this));
        rvRingingDetailTask.setHasFixedSize(true);
        rvRingingDetailTask.setFocusableInTouchMode(false);
        rvRingingDetailTask.requestFocus();

        schemeDetailAdapter = new SchemeDetailAdapter(mContext);
        rvRingingDetailTask.setAdapter(schemeDetailAdapter);
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

                tvRingingDetailTaskMount.setText(taskList.size() + "");
                if (taskList.size() > 0) {
                    //隐藏添加任务按钮
                    llRingingDetailCreateTask.setVisibility(View.GONE);
                    //显示任务RV
                    llRingingDetailTask.setVisibility(View.VISIBLE);
                    //显示批量管理任务按钮
                    btnRingingDetailBatchCreateTask.setVisibility(View.VISIBLE);
                    schemeDetailAdapter.setList(taskList);
                } else {
                    //显示添加任务按钮
                    llRingingDetailCreateTask.setVisibility(View.VISIBLE);
                    //隐藏任务RV
                    llRingingDetailTask.setVisibility(View.GONE);
                    //隐藏批量管理任务按钮
                    btnRingingDetailBatchCreateTask.setVisibility(View.GONE);
                }

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
                //设置head信息
                tvRingingDetailName.setText(this.scheme.getSchemeName());
                tvRingingDetailStartTime.setText(this.scheme.getSchemeStartDate());
                tvRingingDetailEndTime.setText(this.scheme.getSchemeEndDate());
                checkDate();
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
                    RingingTaskDetailActivity.this.finish();
                }
            } else {
                ToastUtil.show(mContext, "操作失败，请检查数据以及网络!");
            }
        }

        if ("editTaskResult".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                EditTaskResult editTaskResult = gson.fromJson(data, EditTaskResult.class);
                if (editTaskResult.getResult() == 0) {
                    ToastUtil.show(mContext, "操作成功!");
                    //获取任务列表
                    GetTaskList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
                }
            } else {
                ToastUtil.show(mContext, "操作失败，请检查数据以及网络!");
            }
        }
    }

    private boolean checkDate() {

        Date startDate = null;
        try {
            startDate = sf2.parse(this.scheme.getSchemeStartDate());
            Date endDate = sf2.parse(this.scheme.getSchemeEndDate());

            Date nowDate = new Date();
            String nowStr = sf2.format(nowDate);
            Date now = sf2.parse(nowStr);

            if (endDate.getTime() < now.getTime()) {

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                View view = View.inflate(mContext, R.layout.dialog_tips, null);
                final TextView tvMsg = (TextView) view.findViewById(R.id.tv_msg);
                final Button btnOk = (Button) view.findViewById(R.id.btn_ok);
                final Button btnNo = (Button) view.findViewById(R.id.btn_no);
                final android.app.AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getWindow().setContentView(view);
                tvMsg.setText("当前打铃方案已过期，请检查打铃方案的日期后再执行此方案！");
                btnOk.setText("修改方案日期");
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        Intent intent = new Intent(RingingTaskDetailActivity.this, EditSchemeActivity.class);
                        Gson gson = new Gson();
                        String schemeStr = gson.toJson(scheme);
                        intent.putExtra("scheme", schemeStr);
                        RingingTaskDetailActivity.this.startActivity(intent);
                    }
                });
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                return false;
            } else {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }


    @OnClick({R.id.bt_ringing_detail_back, R.id.iv_menu, R.id.btn_ringing_detail_create_task, R.id.btn_ringing_detail_create_task_head,
            R.id.btn_ringing_detail_batch_create_task, R.id.ll_menu})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_ringing_detail_back:
                finish();
                break;
            case R.id.iv_menu:
                if (TaskUtils.getIsManager()) {
                    showPopwindow();
                } else {
                    ToastUtil.show(mContext, "对不起，普通用户没有该功能权限！");
                }
                break;
            case R.id.ll_menu:
                if (TaskUtils.getIsManager()) {
                    showPopwindow();
                } else {
                    ToastUtil.show(mContext, "对不起，普通用户没有该功能权限！");
                }
                break;
            case R.id.btn_ringing_detail_create_task:
                if (TaskUtils.getIsManager()) {
                    createTask();
                } else {
                    ToastUtil.show(mContext, "对不起，普通用户没有该功能权限！");
                }
                break;
            case R.id.btn_ringing_detail_create_task_head:
                if (TaskUtils.getIsManager()) {
                    createTask();
                } else {
                    ToastUtil.show(mContext, "对不起，普通用户没有该功能权限！");
                }
                break;
            case R.id.btn_ringing_detail_batch_create_task:     //批量管理打铃任务
                if (TaskUtils.getIsManager()) {
                    Intent intent = new Intent(mContext, BatchManagerRingingTaskActivity.class);
                    intent.putExtra("scheme_num", scheme_num);
                    startActivity(intent);

                    btnRingingDetailBatchCreateTask.setEnabled(false);

                } else {
                    ToastUtil.show(mContext, "对不起，普通用户没有该功能权限！");
                }
                break;
        }
    }

    private void createTask() {
        Intent intent = new Intent(this, CreateRingingTaskActivity.class);
        intent.putExtra("scheme_id", scheme_num);
        startActivity(intent);

        btnRingingDetailCreateTask.setEnabled(false);
        btnRingingDetailCreateTaskHead.setEnabled(false);

    }


    /**
     * 显示popupWindow
     */
    private void showPopwindow() {
        final Dialog bottomDialog = new Dialog(this, R.style.BottomDialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop, null);
        bottomDialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.setCanceledOnTouchOutside(true);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();

        Button btnEditScheme = (Button) contentView.findViewById(R.id.btn_edit_scheme);
        final Button btnCopyScheme = (Button) contentView.findViewById(R.id.btn_copy_scheme);
        Button btnExecuteScheme = (Button) contentView.findViewById(R.id.btn_execute_scheme);
        Button btnExit = (Button) contentView.findViewById(R.id.btn_exit);

        //编辑方案
        btnEditScheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RingingTaskDetailActivity.this, EditSchemeActivity.class);
                Gson gson = new Gson();
                String schemeStr = gson.toJson(scheme);
                intent.putExtra("scheme", schemeStr);
                RingingTaskDetailActivity.this.startActivity(intent);
                bottomDialog.dismiss();
            }
        });
        //取消
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomDialog.dismiss();
            }
        });

        //克隆方案
        btnCopyScheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RingingTaskDetailActivity.this, CopySchemeActivity.class);
                Gson gson = new Gson();
                String schemeStr = gson.toJson(scheme);
                String taskListStr = gson.toJson(taskList);
                intent.putExtra("scheme", schemeStr);
                intent.putExtra("taskList", taskListStr);
                RingingTaskDetailActivity.this.startActivity(intent);
                bottomDialog.dismiss();
                RingingTaskDetailActivity.this.finish();
            }
        });

        //启动或停止方案
        if (scheme.getSchemeStatus() == 1) {
            btnExecuteScheme.setText("停止此方案");
        } else {
            btnExecuteScheme.setText("执行此方案");
        }

        //执行方案或停止此方案
        btnExecuteScheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (!checkDate()) {
                    return;
                }

                if (scheme.getSchemeStatus() == 1) {
                    scheme.setSchemeStatus(0);
                    EditScheme.sendCMD(AppDataCache.getInstance().getString("loginIp"), scheme, 1);
                } else {
                    int index = 0;
                    for (Scheme scheme : schemeList) {
                        if (scheme.getSchemeStatus() == 1) {
                            index++;
                        }
                    }
                    if (index < 2) {
                        scheme.setSchemeStatus(1);
                        EditScheme.sendCMD(AppDataCache.getInstance().getString("loginIp"), scheme, 1);
                    } else {

                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        View v = View.inflate(mContext, R.layout.dialog_tips, null);
                        TextView tvMsg = (TextView) v.findViewById(R.id.tv_msg);
                        Button btnOk = (Button) v.findViewById(R.id.btn_ok);
                        Button btnNo = (Button) v.findViewById(R.id.btn_no);

                        final AlertDialog dialog = builder.create();
                        dialog.show();
                        dialog.getWindow().setContentView(v);
                        tvMsg.setText("同一时期最多可执行两套方案，如需执行此方案，请先取消其中一套正在执行的打铃方案。");
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        btnNo.setVisibility(View.GONE);


//                        ToastUtil.show(mContext, "同一时期最多可执行两套方案，如需执行此方案，请先取消其中一套正在执行的打铃方案。");
                    }
                }
                bottomDialog.dismiss();
            }
        });
    }
}
