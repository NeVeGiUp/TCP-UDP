package com.itc.smartbroadcast.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.event.CreateSchemeActivity;
import com.itc.smartbroadcast.activity.event.CreateInstantTaskActivity;
import com.itc.smartbroadcast.activity.event.CreateTimedTaskActivity;
import com.itc.smartbroadcast.activity.event.InstantTaskActivity;
import com.itc.smartbroadcast.activity.event.ShowRingingTaskActivity;
import com.itc.smartbroadcast.activity.event.TimedTaskActivity;
import com.itc.smartbroadcast.adapter.child.EventChildSchemeAdapter;
import com.itc.smartbroadcast.adapter.child.EventChildInstantTaskAdapter;
import com.itc.smartbroadcast.adapter.child.EventChildTaskAdapter;
import com.itc.smartbroadcast.bean.InstantTask;
import com.itc.smartbroadcast.bean.Scheme;
import com.itc.smartbroadcast.bean.Task;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EventAdapter extends RecyclerView.Adapter {


    private static final int TYPE_RINGING = 0;//打铃任务
    private static final int TYPE_TIMED = 1;//定时任务
    private static final int TYPE_INSTANT = 2;//即时任务
    //private static final int TYPE_ALARm = 3;//报警任务

    private Context context;
    private LayoutInflater inflater;
    private List<Scheme> mSchemeList;
    private List<Task> mTaskListAll;
    private List<Task> mTaskList;
    private List<InstantTask> mInstantTaskList;

    public boolean isSetScheme = false;
    public boolean isSetTask = false;
    public boolean isInstantTask = false;

    public EventAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    //设置方案数据
    public void setSchemeDataList(List<Scheme> schemeList) {
        this.mSchemeList = schemeList;
        isSetScheme = true;
        notifyDataSetChanged();
    }

    //设置定时任务数据
    public void setTaskDataList(List<Task> taskListAll, List<Task> taskList) {
        this.mTaskListAll = taskListAll;
        this.mTaskList = taskList;
        isSetTask = true;
        notifyDataSetChanged();
    }

    //设置即时任务数据
    public void setInstantTaskDataList(List<InstantTask> instantTaskList) {
        this.mInstantTaskList = instantTaskList;
        isInstantTask = true;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_TIMED) {
            return new TimedViewHolder(inflater.inflate(R.layout.fragment_timed_task, parent, false));
        } else if (viewType == TYPE_RINGING) {
            return new RingingViewHolder(inflater.inflate(R.layout.fragment_ringing_task, parent, false));
        } else if (viewType == TYPE_INSTANT) {
            return new InstantViewHolder(inflater.inflate(R.layout.fragment_instant_task, parent, false));
        }
        return null;
    }

    /**
     * 根据postition返回item的类型
     *
     * @param position
     * @return
     */
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_RINGING;
        } else if (position == 1) {
            return TYPE_TIMED;
        } else if (position == 2) {
            return TYPE_INSTANT;
        }
        return 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);

        if (viewType == TYPE_TIMED) {         //定时任务
            TimedViewHolder timedTaskHolder = (TimedViewHolder) holder;
            bindTimedTask(timedTaskHolder);
        } else if (viewType == TYPE_RINGING) {  //打铃任务
            RingingViewHolder ringingTaskHolder = (RingingViewHolder) holder;
            bindRingingTask(ringingTaskHolder);
        } else if (viewType == TYPE_INSTANT) {  //即时任务
            InstantViewHolder instantTaskHolder = (InstantViewHolder) holder;
            bindInstantTask(instantTaskHolder);
        }
    }

    //在首页显示定时任务
    private void bindTimedTask(TimedViewHolder timedTaskHolder) {
        timedTaskHolder.mTimedTaskRv.setHasFixedSize(true);
        timedTaskHolder.mTimedTaskRv.setFocusableInTouchMode(false);  //去除焦点,recycleView嵌套recycleView时会自动滚动
        timedTaskHolder.mTimedTaskRv.requestFocus();
        timedTaskHolder.mTimedTaskRv.setLayoutManager(new LinearLayoutManager(context));
        EventChildTaskAdapter taskAdapter = new EventChildTaskAdapter(context);

        List<Task> mList = new ArrayList<>();
        //隐藏添加打铃方案按钮
        if (mTaskList != null && mTaskList.size() > 0) {
            timedTaskHolder.mAddTaskLl.setVisibility(View.GONE);
            if (mTaskList.size() > 3) {
                for (int i = 0; i < 3; i++) {
                    mList.add(mTaskList.get(i));
                }
            } else {
                mList.addAll(mTaskList);
            }
        } else {
            if (isSetTask)
                timedTaskHolder.mAddTaskLl.setVisibility(View.VISIBLE);
        }

        taskAdapter.setList(mList);    //插入数据
        timedTaskHolder.mTimedTaskRv.setAdapter(taskAdapter);
        timedTaskHolder.mAddTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CreateTimedTaskActivity.class);
                context.startActivity(intent);
            }
        });

        timedTaskHolder.tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TimedTaskActivity.class);
                context.startActivity(intent);
            }
        });

    }

    //在首页显示打铃任务
    private void bindRingingTask(RingingViewHolder ringingViewHolder) {
        ringingViewHolder.mRingingRv.setHasFixedSize(true);
        ringingViewHolder.mRingingRv.setFocusableInTouchMode(false);  //去除焦点,recycleView嵌套recycleView时会自动滚动
        ringingViewHolder.mRingingRv.requestFocus();
        ringingViewHolder.mRingingRv.setLayoutManager(new LinearLayoutManager(context));
        EventChildSchemeAdapter chidCateAdapter = new EventChildSchemeAdapter(context);

        List<Scheme> mList = new ArrayList<>();
        //隐藏添加打铃方案按钮
        if (mSchemeList != null && mSchemeList.size() > 0) {
            ringingViewHolder.mAddRingingLl.setVisibility(View.GONE);

            if (mSchemeList != null && mSchemeList.size() > 0 && mSchemeList.size() > 3) {
                for (int i = 0; i < 3; i++) {
                    mList.add(mSchemeList.get(i));
                }
            } else {
                mList.addAll(mSchemeList);
            }
        } else {
            if (isSetScheme)
                ringingViewHolder.mAddRingingLl.setVisibility(View.VISIBLE);
        }

        chidCateAdapter.setList(mList, mTaskListAll);    //插入数据

        ringingViewHolder.mRingingRv.setAdapter(chidCateAdapter);

        //添加打铃方案事件
        ringingViewHolder.mAddRingingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTo(context, CreateSchemeActivity.class);
            }
        });
        ringingViewHolder.tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goTo(context, ShowRingingTaskActivity.class);
            }
        });
    }

    //在首页显示即时任务
    private void bindInstantTask(InstantViewHolder instantViewHolder) {
        instantViewHolder.mInstantRv.setHasFixedSize(true);
        instantViewHolder.mInstantRv.setFocusableInTouchMode(false);  //去除焦点,recycleView嵌套recycleView时会自动滚动
        instantViewHolder.mInstantRv.requestFocus();
        instantViewHolder.mInstantRv.setLayoutManager(new LinearLayoutManager(context));
        EventChildInstantTaskAdapter instantTaskAdapter = new EventChildInstantTaskAdapter(context);

        List<InstantTask> mList = new ArrayList<>();
        //隐藏添加打铃方案按钮
        if (mInstantTaskList != null && mInstantTaskList.size() > 0) {
            instantViewHolder.mAddInstantTaskLl.setVisibility(View.GONE);
            if (mInstantTaskList.size() > 3) {
                for (int i = 0; i < 3; i++) {
                    mList.add(mInstantTaskList.get(i));
                }
            } else {
                mList.addAll(mInstantTaskList);
            }
        } else {
            if (isInstantTask)
                instantViewHolder.mAddInstantTaskLl.setVisibility(View.VISIBLE);
        }


        instantTaskAdapter.setList(mList);    //插入数据

        instantViewHolder.mInstantRv.setAdapter(instantTaskAdapter);

        instantViewHolder.mAddInstantTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CreateInstantTaskActivity.class);
                context.startActivity(intent);
            }
        });

        instantViewHolder.tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, InstantTaskActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 3;
    }


    /**
     * 定时任务
     */
    public class TimedViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.list_index_timedtask)
        RecyclerView mTimedTaskRv;
        @BindView(R.id.ll_add_task)
        LinearLayout mAddTaskLl;
        @BindView(R.id.btn_add_task)
        Button mAddTaskBtn;
        @BindView(R.id.tv_more)
        TextView tvMore;

        public TimedViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }

    /**
     * 打铃方案
     */
    class RingingViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.list_index_ringingtask)
        RecyclerView mRingingRv;
        @BindView(R.id.ll_add_ringing)
        LinearLayout mAddRingingLl;
        @BindView(R.id.btn_add_ringing)
        Button mAddRingingBtn;
        @BindView(R.id.tv_more)
        TextView tvMore;

        public RingingViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }

    /**
     * 即时任务
     */
    public class InstantViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.list_index_instanttask)
        RecyclerView mInstantRv;
        @BindView(R.id.ll_add_instant_task)
        LinearLayout mAddInstantTaskLl;
        @BindView(R.id.btn_add_instant_task)
        Button mAddInstantTaskBtn;
        @BindView(R.id.tv_more)
        TextView tvMore;

        public InstantViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }

    public void goTo(Context context, Class<?> to) {
        Intent in = new Intent();
        in.setClass(context, to);
        context.startActivity(in);

    }

    public void goTo(Context context, Class<?> to, Bundle bundle) {
        Intent in = new Intent();
        in.setClass(context, to);
        in.putExtras(bundle);
        context.startActivity(in);

    }
}