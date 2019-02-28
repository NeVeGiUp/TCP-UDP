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

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.event.CdPlayerActivity;
import com.itc.smartbroadcast.activity.event.CollectorPlayerActivity;
import com.itc.smartbroadcast.activity.event.EditInstantTaskActivity;
import com.itc.smartbroadcast.activity.event.FmPlayerActivity;
import com.itc.smartbroadcast.activity.event.utils.TaskUtils;
import com.itc.smartbroadcast.bean.AccountListInfo;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.bean.InstantTask;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.EditInstantTask;
import com.itc.smartbroadcast.channels.protocolhandler.EditTask;
import com.itc.smartbroadcast.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Content :即时任务适配器
 * @Author : lik
 * @Time : 18-9-4 下午5:01
 */
public class EventChildInstantTaskAdapter extends RecyclerView.Adapter {


    private Context mContext;
    private List<InstantTask> mList = new ArrayList<>();

    public EventChildInstantTaskAdapter(Context context) {
        this.mContext = context;
    }

    public List<InstantTask> getList() {
        return mList;
    }

    public void setList(List<InstantTask> list) {
        if (list != null) {
            this.mList.clear();
            addList(list);
        }
    }

    private void addList(List<InstantTask> list) {
        if (list != null) {
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_instant_task, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        //绑定数据
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        final InstantTask task = mList.get(position);
        itemHolder.tvInstantTaskName.setText(task.getTaskName());

        List<AccountListInfo.AccountDataInner> accountListInfoList = new ArrayList<>();

        String accountList = AppDataCache.getInstance().getString("accountList");
        if (!accountList.equals("")) {
            accountListInfoList = JSONArray.parseArray(accountList, AccountListInfo.AccountDataInner.class);
        }

        for (AccountListInfo.AccountDataInner user : accountListInfoList) {
            if (user.getAccountNum() == task.getAccountNum()) {
                itemHolder.tvInstantUser.setText(user.getAccountName());
            }
        }


        int continueTime = task.getContinueDate();
        int day = continueTime / (24 * 60 * 60);
        int hour = (continueTime / (60 * 60)) % 24;
        int minute = (continueTime / 60) % 60;
        int second = continueTime % 60;
        itemHolder.tvInstantTaskContinueTime.setText("持续  " + day + "天" + hour + "小时" + minute + "分" + second + "秒");

        if (task.getStatus() == 1) {
            itemHolder.tvTaskStatus.setVisibility(View.VISIBLE);
            itemHolder.tvTaskStatus.setText("执行中");
            itemHolder.tvTaskStatus.setBackground(mContext.getResources().getDrawable(R.drawable.bg_online));
            itemHolder.tvTaskStatus.setTextColor(mContext.getResources().getColor(R.color.color_running_text));
        } else if (task.getStatus() == 2) {
            itemHolder.tvTaskStatus.setVisibility(View.VISIBLE);
            itemHolder.tvTaskStatus.setText("异常");
            itemHolder.tvTaskStatus.setBackground(mContext.getResources().getDrawable(R.drawable.bg_instant_execute));
            itemHolder.tvTaskStatus.setTextColor(mContext.getResources().getColor(R.color.task_exception));
        } else {
            itemHolder.tvTaskStatus.setVisibility(View.GONE);
        }
        itemHolder.tvInstantTaskVolume.setText("音量  " + task.getVolume());


        itemHolder.btnEditTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                String instantTaskJson = gson.toJson(task);
                Intent intent = new Intent(mContext, EditInstantTaskActivity.class);
                intent.putExtra("instantTask", instantTaskJson);
                mContext.startActivity(intent);
            }
        });


        itemHolder.rvInstantTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2018/8/22 点击进入详情

                int userNum = task.getAccountNum();
                int selfNum = TaskUtils.getUserNum();
                if (TaskUtils.getIsManager()) {
                } else {
                    if (userNum != selfNum) {
                        ToastUtil.show(mContext, "对不起，您对该任务没有控制权限!");
                        return;
                    }
                }
                List<FoundDeviceInfo> deviceList = JSONArray.parseArray(AppDataCache.getInstance().getString("deviceList"), FoundDeviceInfo.class);
                for (FoundDeviceInfo device : deviceList) {
                    if (task.getTerminalMac().equals(device.getDeviceMac())) {
                        Intent intent;
                        Gson gson = new Gson();
                        String instantTaskJson = gson.toJson(task);
                        switch (device.getDeviceMedel()) {
                            case "TX-8627":     //CD机
                                intent = new Intent(mContext, CdPlayerActivity.class);
                                intent.putExtra("instantTask", instantTaskJson);
                                mContext.startActivity(intent);
                                break;
                            case "TX-8628":     //FM机
                                intent = new Intent(mContext, FmPlayerActivity.class);
                                intent.putExtra("instantTask", instantTaskJson);
                                mContext.startActivity(intent);
                                break;
                            case "TX-8601":     //网络采集器
                                intent = new Intent(mContext, CollectorPlayerActivity.class);
                                intent.putExtra("instantTask", instantTaskJson);
                                mContext.startActivity(intent);
                                break;
                        }
                    }
                }
            }
        });

        itemHolder.rvInstantTask.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {


                int userNum = task.getAccountNum();
                int selfNum = TaskUtils.getUserNum();
                if (TaskUtils.getIsManager()) {
                } else {
                    if (userNum != selfNum) {
                        ToastUtil.show(mContext, "对不起，您对该任务没有控制权限!");
                        return false;
                    }
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                View v = View.inflate(mContext, R.layout.dialog_tips, null);
                final TextView tvMsg = (TextView) v.findViewById(R.id.tv_msg);
                final Button btnOk = (Button) v.findViewById(R.id.btn_ok);
                final Button btnNo = (Button) v.findViewById(R.id.btn_no);
                final android.app.AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getWindow().setContentView(v);
                tvMsg.setText("即时任务将被清除，确定删除吗？");
                btnNo.setVisibility(View.VISIBLE);
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        EditInstantTask.sendCMD(AppDataCache.getInstance().getString("loginIp"), task, null, 2);
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

        @BindView(R.id.tv_instant_task_name)
        TextView tvInstantTaskName;
        @BindView(R.id.tv_instant_task_continue_time)
        TextView tvInstantTaskContinueTime;
        @BindView(R.id.rv_instant_task)
        LinearLayout rvInstantTask;
        @BindView(R.id.tv_task_status)
        TextView tvTaskStatus;
        @BindView(R.id.tv_instant_task_volume)
        TextView tvInstantTaskVolume;
        @BindView(R.id.tv_instant_user)
        TextView tvInstantUser;
        @BindView(R.id.btn_edit_task)
        Button btnEditTask;

        public ItemViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }
}
