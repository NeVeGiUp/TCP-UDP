package com.itc.smartbroadcast.adapter.event;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.event.AlarmTaskConfigActivity;
import com.itc.smartbroadcast.bean.AlarmDeviceDetail;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * create by youmu on 2018/8/23
 */

public class AlarmAdapter extends RecyclerView.Adapter {


    private Context mContext;
    private List<AlarmDeviceDetail> mTaskList = new ArrayList<>();

    public AlarmAdapter(Context context) {
        this.mContext = context;
    }

    public List<AlarmDeviceDetail> getList() {
        return mTaskList;
    }

    public void setList(List<AlarmDeviceDetail> list) {
        if (list != null) {
            this.mTaskList.clear();
            addList(list);
        }
    }

    private void addList(List<AlarmDeviceDetail> list) {
        if (list != null) {
            mTaskList.addAll(list);
            notifyDataSetChanged();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_alarm, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        //绑定数据
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        final AlarmDeviceDetail alarmDeviceDetail = mTaskList.get(position);
        itemHolder.tvAlarmName.setText(alarmDeviceDetail.getDeviceName());
        if (alarmDeviceDetail.getTriggerMode() == 255) {
            itemHolder.ivTriggerMode.setVisibility(View.GONE);
            itemHolder.tvTriggerMode.setText("加载中...");
        } else {
            itemHolder.ivTriggerMode.setVisibility(View.VISIBLE);
            if (alarmDeviceDetail.getTriggerMode() == 0) {
                itemHolder.tvTriggerMode.setText("自动解除报警");
                itemHolder.ivTriggerMode.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.jishi_icon_zidong_default));
            }
            if (alarmDeviceDetail.getTriggerMode() == 1) {
                itemHolder.tvTriggerMode.setText("手动解除报警");
                itemHolder.ivTriggerMode.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.jishi_icon_shoudong_default));
            }

        }
        if (alarmDeviceDetail.getPortResponseMode() == 255) {
            itemHolder.tvPortResponseMode.setText("加载中...");
        } else {

            switch (alarmDeviceDetail.getPortResponseMode()) {
                case 0:
                    itemHolder.tvPortResponseMode.setText("单区报警");
                    break;
                case 1:
                    itemHolder.tvPortResponseMode.setText("邻区+1报警");
                    break;
                case 2:
                    itemHolder.tvPortResponseMode.setText("邻区+2报警");
                    break;
                case 3:
                    itemHolder.tvPortResponseMode.setText("邻区+3报警");
                    break;
                case 4:
                    itemHolder.tvPortResponseMode.setText("邻区+4报警");
                    break;
                case 5:
                    itemHolder.tvPortResponseMode.setText("全区报警");
                    break;
            }
        }

        itemHolder.llAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AlarmTaskConfigActivity.class);
                String alarmDeviceDetailJson = JSON.toJSONString(alarmDeviceDetail);
                intent.putExtra("alarmDeviceDetail", alarmDeviceDetailJson);
                mContext.startActivity(intent);
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

        @BindView(R.id.tv_alarm_name)
        TextView tvAlarmName;
        @BindView(R.id.iv_trigger_mode)
        ImageView ivTriggerMode;
        @BindView(R.id.tv_trigger_mode)
        TextView tvTriggerMode;
        @BindView(R.id.tv_port_response_mode)
        TextView tvPortResponseMode;
        @BindView(R.id.ll_alarm)
        LinearLayout llAlarm;

        public ItemViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }
}
