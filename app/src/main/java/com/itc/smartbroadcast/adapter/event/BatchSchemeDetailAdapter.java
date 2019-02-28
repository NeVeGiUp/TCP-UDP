package com.itc.smartbroadcast.adapter.event;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.bean.MusicMsgInfo;
import com.itc.smartbroadcast.bean.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * create by youmu on 2018/8/23
 */

public class BatchSchemeDetailAdapter extends RecyclerView.Adapter {


    private Context mContext;
    private List<Task> mTaskList = new ArrayList<>();

    private List<Task> checkBoxIDList = new ArrayList<>();            //存储checkBox的值

    private boolean checkAll = false;
    private boolean checkNo = false;

    Map<Integer, Boolean> isCheck = new HashMap<>();


    public BatchSchemeDetailAdapter(Context context) {
        this.mContext = context;
    }

    //get set
    public List<Task> getCheckBoxIDList() {
        return checkBoxIDList;
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
            for (int i = 0; i < mTaskList.size(); i++) {
                isCheck.put(i, false);// 默认所有的checkbox都是没选中
            }
            notifyDataSetChanged();
        }
    }

    public void setCheckAll() {
        checkAll = true;
        checkNo = false;
        for (int i = 0; i < mTaskList.size(); i++) {
            isCheck.put(i, true);// 默认所有的checkbox都是没选中
        }
        checkBoxIDList.clear();
        checkBoxIDList.addAll(mTaskList);
        notifyDataSetChanged();
    }

    public void setNoCheckAll() {
        checkAll = false;
        checkNo = true;
        for (int i = 0; i < mTaskList.size(); i++) {
            isCheck.put(i, false);// 默认所有的checkbox都是没选中
        }
        checkBoxIDList.clear();
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_batch_ringing_task_detail, parent, false));
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
        String endTime = "";
        try {
            Date startDate = sdf.parse("2001-01-01 " + startTime);
            Date endDate = new Date(startDate.getTime() + (continueTime * 1000));
            endTime = sdf.format(endDate).split(" ")[1];
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String executeTimeStr = "执行时间：  " + startTime + " - " + endTime + "";
        itemHolder.tvTaskExecuteTime.setText(executeTimeStr);

        int hour = (continueTime / (60 * 60)) % 24;
        int minute = (continueTime / 60) % 60;
        int second = continueTime % 60;
        String continueTimeStr = hour + ":" + minute + ":" + second;
        itemHolder.tvTaskContinueTime.setText("持续  " + continueTimeStr);
        int volume = task.getTaskVolume();
        itemHolder.tvTaskVolume.setText("音量  " + volume);


        itemHolder.cbRingingTask.setOnCheckedChangeListener(null);//清掉监听器
        itemHolder.cbRingingTask.setChecked(isCheck.get(position));

        itemHolder.cbRingingTask.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    checkBoxIDList.add(task);
                } else {
                    checkBoxIDList.remove(task);
                }
                if (isCheck.get(position)) {
                    isCheck.put(position, false);
                } else {
                    isCheck.put(position, true);
                }
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
        @BindView(R.id.cb_ringing_task)
        CheckBox cbRingingTask;

        public ItemViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }
}
