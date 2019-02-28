package com.itc.smartbroadcast.adapter.child;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.event.EditTimedTaskActivity;
import com.itc.smartbroadcast.activity.event.utils.TaskUtils;
import com.itc.smartbroadcast.bean.Task;
import com.itc.smartbroadcast.bean.TaskDetail;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.EditScheme;
import com.itc.smartbroadcast.channels.protocolhandler.EditTask;
import com.itc.smartbroadcast.util.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Content :定时任务适配器
 * @Author : lik
 * @Time : 18-9-4 下午5:01
 */
public class EventChildTaskAdapter extends RecyclerView.Adapter {


    private Context mContext;
    private List<Task> mList = new ArrayList<>();

    public EventChildTaskAdapter(Context context) {
        this.mContext = context;
    }

    public List<Task> getList() {
        return mList;
    }

    public void setList(List<Task> list) {
        if (list != null) {
            this.mList.clear();
            addList(list);
        }
    }

    private void addList(List<Task> list) {
        if (list != null) {
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_timed_task, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        //绑定数据
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        final Task task = mList.get(position);

        itemHolder.tvTaskName.setText(task.getTaskName());
        itemHolder.tvTaskStarttime.setText(task.getTaskStartDate());

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");

        Date nowDate = new Date();
        String lastDateStr = DateUtil.getDateLast(task.getTaskDateDuplicationPattern());
        lastDateStr = lastDateStr + " " + task.getTaskStartDate();
        boolean bol = false;
        try {
            Date startDate = sdf.parse(lastDateStr);
            Date lastDate = new Date(startDate.getTime() + (task.getTaskContinueDate() * 1000));

            if ((nowDate.getTime() - lastDate.getTime() <= 0)) {    //未开始
                itemHolder.tvTaskStatus.setText("未开始");
                itemHolder.tvTaskStatus.setTextColor(mContext.getResources().getColor(R.color.color_not_started_text));
                itemHolder.tvTaskStatus.setBackground(mContext.getResources().getDrawable(R.drawable.bg_timeed_task_not_started));
                bol = true;
            } else {
                itemHolder.tvTaskStatus.setText("已结束");
                itemHolder.tvTaskStatus.setTextColor(mContext.getResources().getColor(R.color.color_over_text));
                itemHolder.tvTaskStatus.setBackground(mContext.getResources().getDrawable(R.drawable.bg_timeed_task_end));
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (bol) {
            try {
                Date startDate = sdf2.parse(task.getTaskStartDate());

                String todayStr = sdf1.format(nowDate);
                boolean flagBol = false;
                for (String dateStr : task.getTaskDateDuplicationPattern()) {
                    if (todayStr.equals(dateStr)) {
                        flagBol = true;
                    }
                }

                if ((startDate.getHours() * 60 * 60 + startDate.getMinutes() * 60 + startDate.getSeconds() + task.getTaskContinueDate()) > (24 * 60 * 60)) {
                    if ((nowDate.getHours() * 60 * 60 + nowDate.getMinutes() * 60 + nowDate.getSeconds()) <= (startDate.getHours() * 60 * 60 + startDate.getMinutes() * 60 + startDate.getSeconds() + task.getTaskContinueDate())
                            && ((nowDate.getHours() * 60 * 60 + nowDate.getMinutes() * 60 + nowDate.getSeconds()) >= (startDate.getHours() * 60 * 60 + startDate.getMinutes() * 60 + startDate.getSeconds())
                            || (nowDate.getHours() * 60 * 60 + nowDate.getMinutes() * 60 + nowDate.getSeconds()) <= (startDate.getHours() * 60 * 60 + startDate.getMinutes() * 60 + startDate.getSeconds() + task.getTaskContinueDate()) - 24 * 60 * 60)) {
                        itemHolder.tvTaskStatus.setText("进行中");
                        itemHolder.tvTaskStatus.setTextColor(mContext.getResources().getColor(R.color.color_running_text));
                        itemHolder.tvTaskStatus.setBackground(mContext.getResources().getDrawable(R.drawable.bg_timeed_task_execute));
                    }
                } else {
                    if ((nowDate.getHours() * 60 * 60 + nowDate.getMinutes() * 60 + nowDate.getSeconds()) < (startDate.getHours() * 60 * 60 + startDate.getMinutes() * 60 + startDate.getSeconds() + task.getTaskContinueDate())
                            && (nowDate.getHours() * 60 * 60 + nowDate.getMinutes() * 60 + nowDate.getSeconds()) > (startDate.getHours() * 60 * 60 + startDate.getMinutes() * 60 + startDate.getSeconds())
                            && flagBol) {
                        itemHolder.tvTaskStatus.setText("进行中");
                        itemHolder.tvTaskStatus.setTextColor(mContext.getResources().getColor(R.color.color_running_text));
                        itemHolder.tvTaskStatus.setBackground(mContext.getResources().getDrawable(R.drawable.bg_timeed_task_execute));
                    }
                }


            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (task.getTaskStatus() == 0) {
            itemHolder.tvTaskStatus.setText("已禁止");
            itemHolder.tvTaskStatus.setTextColor(mContext.getResources().getColor(R.color.color_ban_text));
            itemHolder.tvTaskStatus.setBackground(mContext.getResources().getDrawable(R.drawable.bg_timeed_task_prohibit));
        }


        int continueTime = task.getTaskContinueDate();
        int day = continueTime / (24 * 60 * 60);
        int hour = (continueTime / (60 * 60)) % 24;
        int minute = (continueTime / 60) % 60;
        int second = continueTime % 60;
        itemHolder.tvTaskContinueTime.setText("持续  " + day + "天" + hour + "小时" + minute + "分" + second + "秒");
        //itemHolder.ring_taskamout.setText(scheme.getSchemeNum());

        itemHolder.rvTimeTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2018/8/22 点击进入详情
                Intent intent = new Intent(mContext, EditTimedTaskActivity.class);
                Gson gson = new Gson();
                String taskStr = gson.toJson(task);
                intent.putExtra("task", taskStr);
                mContext.startActivity(intent);
            }
        });

        itemHolder.rvTimeTask.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if (TaskUtils.getIsManager()) {
                } else {
                    return false;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                View v = View.inflate(mContext, R.layout.dialog_tips, null);
                final TextView tvMsg = (TextView) v.findViewById(R.id.tv_msg);
                final Button btnOk = (Button) v.findViewById(R.id.btn_ok);
                final Button btnNo = (Button) v.findViewById(R.id.btn_no);
                final android.app.AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getWindow().setContentView(v);
                tvMsg.setText("定时任务将被清除，确定删除吗？");
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


                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mList == null)
            return 0;
        else
            return mList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_task_starttime)
        TextView tvTaskStarttime;
        @BindView(R.id.tv_task_status)
        TextView tvTaskStatus;
        @BindView(R.id.tv_task_name)
        TextView tvTaskName;
        @BindView(R.id.tv_task_continue_time)
        TextView tvTaskContinueTime;
        @BindView(R.id.rv_time_task)
        LinearLayout rvTimeTask;

        public ItemViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }
}
