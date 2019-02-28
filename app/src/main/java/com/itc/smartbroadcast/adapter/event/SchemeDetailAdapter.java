package com.itc.smartbroadcast.adapter.event;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.event.CopyRingingTaskActivity;
import com.itc.smartbroadcast.activity.event.EditRingingTaskActivity;
import com.itc.smartbroadcast.activity.event.utils.TaskUtils;
import com.itc.smartbroadcast.bean.Task;
import com.itc.smartbroadcast.bean.TaskDetail;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.EditTask;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * create by youmu on 2018/8/23
 */

public class SchemeDetailAdapter extends RecyclerView.Adapter {


    private Context mContext;
    private List<Task> mTaskList = new ArrayList<>();

    public SchemeDetailAdapter(Context context) {
        this.mContext = context;
    }

    public List<Task> getList() {
        return mTaskList;
    }

    public void setList(List<Task> list) {
        if (list != null) {
            this.mTaskList.clear();
            addList(list);
        }
    }

    private void addList(List<Task> list) {
        if (list != null) {
            mTaskList.addAll(list);
            notifyDataSetChanged();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_ringing_task_detail, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        //绑定数据
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        final Task task = mTaskList.get(position);
        itemHolder.tvTaskName.setText(task.getTaskName());

        if (task.getTaskStatus() == 0) {
            itemHolder.tvTaskStatus.setText("已禁止");
            itemHolder.tvTaskStatus.setTextColor(mContext.getResources().getColor(R.color.color_ban_text));
            itemHolder.tvTaskStatus.setBackground(mContext.getResources().getDrawable(R.drawable.bg_timeed_task_prohibit));
        } else {
            itemHolder.tvTaskStatus.setText("生效");
            itemHolder.tvTaskStatus.setTextColor(mContext.getResources().getColor(R.color.color_running_text));
            itemHolder.tvTaskStatus.setBackground(mContext.getResources().getDrawable(R.drawable.bg_timeed_task_execute));
        }


        int[] week = task.getTaskWeekDuplicationPattern();
        String taskCycleStr = "执行周期：  ";
        for (int i = 0; i < 7; i++) {
            if (week[i] == 1) {
                switch (i + 1) {
                    case 1:
                        taskCycleStr += "周一  ";
                        break;
                    case 2:
                        taskCycleStr += "周二  ";
                        break;
                    case 3:
                        taskCycleStr += "周三  ";
                        break;
                    case 4:
                        taskCycleStr += "周四  ";
                        break;
                    case 5:
                        taskCycleStr += "周五  ";
                        break;
                    case 6:
                        taskCycleStr += "周六  ";
                        break;
                    case 7:
                        taskCycleStr += "周日";
                        break;
                }
            }
        }
        itemHolder.tvTaskCycle.setText(taskCycleStr);
        String startTime = task.getTaskStartDate();
        int continueTime = task.getTaskContinueDate();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
        String endTime = "";
        try {
            Date startDate = sdf.parse("2001-01-01 " + startTime);
            Date endDate = new Date(startDate.getTime() + (continueTime * 1000));
            endTime = sdf.format(endDate).split(" ")[1];
            String executeTimeStr = "执行时间：  " + startTime + " - " + endTime + "";
            itemHolder.tvTaskExecuteTime.setText(executeTimeStr);

            List<Task> list = new ArrayList<>();
            for (int i = 0; i < position; i++) {
                list.add(mTaskList.get(i));
            }

            if (TaskUtils.checkTaskTime(list, task)) {
                itemHolder.tvTaskExecuteTime.setTextColor(mContext.getResources().getColor(R.color.color_font));
                itemHolder.ivWarn.setVisibility(View.GONE);
            } else {
                itemHolder.tvTaskExecuteTime.setTextColor(mContext.getResources().getColor(R.color.color_execution));
                itemHolder.ivWarn.setVisibility(View.VISIBLE);
            }

            String continueTimeStr = SmartBroadCastUtils.secToTime(continueTime);
            itemHolder.tvTaskContinueTime.setText("持续  " + continueTimeStr);
            int volume = task.getTaskVolume();
            if (volume == 128) {
                itemHolder.tvTaskVolume.setText("音量  默认音量");
            } else {
                itemHolder.tvTaskVolume.setText("音量  " + volume);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        itemHolder.rvTimeTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, EditRingingTaskActivity.class);
                Gson gson = new Gson();
                String taskStr = gson.toJson(task);
                intent.putExtra("task", taskStr);
                mContext.startActivity(intent);
            }
        });

        itemHolder.rvTimeTask.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                if (TaskUtils.getIsManager()) {
                } else {
                    return false;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                View view = View.inflate(mContext, R.layout.dialog_scheme_detail, null);
                final TextView tvCopy = (TextView) view.findViewById(R.id.tv_copy);
                final TextView tvDelete = (TextView) view.findViewById(R.id.tv_delete);
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getWindow().setContentView(view);
                tvCopy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        copyTask(task);
                    }
                });
                tvDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        deleteTask(task);
                    }
                });
                return false;
            }
        });

    }

    private void copyTask(Task task) {
        Intent intent = new Intent(mContext, CopyRingingTaskActivity.class);
        Gson gson = new Gson();
        String taskStr = gson.toJson(task);
        intent.putExtra("task", taskStr);
        mContext.startActivity(intent);
    }


    public void deleteTask(final Task task) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = View.inflate(mContext, R.layout.dialog_tips, null);
        final TextView tvMsg = (TextView) view.findViewById(R.id.tv_msg);
        final Button btnOk = (Button) view.findViewById(R.id.btn_ok);
        final Button btnNo = (Button) view.findViewById(R.id.btn_no);
        final android.app.AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(view);
        tvMsg.setText("打铃任务将被清除，确定删除吗？");
        btnNo.setVisibility(View.VISIBLE);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                TaskDetail taskDetail = new TaskDetail();
                taskDetail.setTaskNum(task.getTaskNum());
                List<TaskDetail.Device> deviceList = new ArrayList<>();
                List<TaskDetail.Music> musicList = new ArrayList<TaskDetail.Music>();
                taskDetail.setDeviceList(deviceList);
                taskDetail.setMusicList(musicList);
                EditTask.sendCMD(AppDataCache.getInstance().getString("loginIp"), task, taskDetail, 2);
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }


    @Override
    public int getItemCount() {
        if (mTaskList == null)
            return 0;
        else
            return mTaskList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_task_name)
        TextView tvTaskName;
        @BindView(R.id.tv_task_status)
        TextView tvTaskStatus;
        @BindView(R.id.tv_task_cycle)
        TextView tvTaskCycle;
        @BindView(R.id.tv_task_execute_time)
        TextView tvTaskExecuteTime;
        @BindView(R.id.tv_task_continue_time)
        TextView tvTaskContinueTime;
        @BindView(R.id.tv_task_volume)
        TextView tvTaskVolume;
        @BindView(R.id.rv_time_task)
        LinearLayout rvTimeTask;
        @BindView(R.id.iv_warn)
        ImageView ivWarn;

        public ItemViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }
}
