package com.itc.smartbroadcast.adapter.child;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.event.RingingTaskDetailActivity;
import com.itc.smartbroadcast.activity.event.utils.TaskUtils;
import com.itc.smartbroadcast.bean.Scheme;
import com.itc.smartbroadcast.bean.Task;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.EditInstantTask;
import com.itc.smartbroadcast.channels.protocolhandler.EditScheme;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * create by youmu on 2018/8/23
 */

public class EventChildSchemeAdapter extends RecyclerView.Adapter {


    private Context mContext;
    private List<Scheme> mList = new ArrayList<>();
    private List<Task> mTaskList = new ArrayList<>();

    public EventChildSchemeAdapter(Context context) {
        this.mContext = context;
    }

    public List<Scheme> getList() {
        return mList;
    }

    public void setList(List<Scheme> list, List<Task> mTaskList) {
        if (list != null) {
            this.mList.clear();
            this.mTaskList.clear();
            addList(list, mTaskList);
        }
    }

    private void addList(List<Scheme> list, List<Task> mTaskList) {
        this.mTaskList = mTaskList;
        if (list != null) {
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item_ringingtask, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        //绑定数据
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        Scheme scheme = mList.get(position);
        itemHolder.ring_plannane.setText(scheme.getSchemeName());
        itemHolder.ring_starttime.setText(scheme.getSchemeStartDate());
        itemHolder.ring_overtime.setText(scheme.getSchemeEndDate());
        //itemHolder.ring_taskamout.setText(scheme.getSchemeNum());

        if (scheme.getSchemeStatus() == 0) {
            itemHolder.tvRingStatus.setText("已禁止");
            itemHolder.tvRingStatus.setTextColor(mContext.getResources().getColor(R.color.color_ban_text));
            itemHolder.tvRingStatus.setBackground(mContext.getResources().getDrawable(R.drawable.bg_timeed_task_prohibit));
        } else {
            itemHolder.tvRingStatus.setText("生效");
            itemHolder.tvRingStatus.setTextColor(mContext.getResources().getColor(R.color.color_running_text));
            itemHolder.tvRingStatus.setBackground(mContext.getResources().getDrawable(R.drawable.bg_timeed_task_execute));
        }

        int mount = 0;
        if (mTaskList != null && mTaskList.size() > 0) {
            for (Task task : mTaskList) {
                if (mList.get(position).getSchemeNum() == task.getSchemeNum()) {
                    mount++;
                }
            }
        }
        itemHolder.tv_ring_task_amount.setText(mount + "");
        itemHolder.ringingtask_rv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 2018/8/22 点击进入详情
                Intent intent = new Intent(mContext, RingingTaskDetailActivity.class);
                intent.putExtra("scheme_num", mList.get(position).getSchemeNum());
                mContext.startActivity(intent);
            }
        });

        itemHolder.ringingtask_rv.setOnLongClickListener(new View.OnLongClickListener() {
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
                tvMsg.setText("方案及方案内的所有任务都将被清除，确定删除吗？");
                btnNo.setVisibility(View.VISIBLE);
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        EditScheme.sendCMD(AppDataCache.getInstance().getString("loginIp"), mList.get(position), 2);
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
        @BindView(R.id.tv_ring_planname)
        TextView ring_plannane;
        @BindView(R.id.tv_ring_starttime)
        TextView ring_starttime;
        @BindView(R.id.tv_ring_overtime)
        TextView ring_overtime;
        @BindView(R.id.tv_ring_task_amount)
        TextView tv_ring_task_amount;
        @BindView(R.id.index_ringingtask_rv)
        RelativeLayout ringingtask_rv;
        @BindView(R.id.tv_ring_status)
        TextView tvRingStatus;

        public ItemViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }
}
