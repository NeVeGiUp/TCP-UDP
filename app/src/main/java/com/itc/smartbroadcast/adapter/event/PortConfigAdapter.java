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
import com.alibaba.fastjson.JSONObject;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.event.AlarmPortConfigActivity;
import com.itc.smartbroadcast.activity.event.AlarmTaskConfigActivity;
import com.itc.smartbroadcast.bean.AlarmDeviceDetail;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * create by lik on 2018/8/23
 */

public class PortConfigAdapter extends RecyclerView.Adapter {


    private Context mContext;
    private List<Integer> portConfig = new ArrayList<>();
    private AlarmDeviceDetail alarmDeviceDetail = new AlarmDeviceDetail();

    public PortConfigAdapter(Context context) {
        this.mContext = context;
    }

    public List<Integer> getList() {
        return portConfig;
    }

    public void setList(int[] portConfig, AlarmDeviceDetail alarmDeviceDetail) {
        this.portConfig.clear();
        for (int config : portConfig) {
            this.portConfig.add(config);
        }
        this.alarmDeviceDetail = alarmDeviceDetail;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_port_config, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        //绑定数据
        ItemViewHolder itemHolder = (ItemViewHolder) holder;

        if (alarmDeviceDetail.getPortNameList().get(position).equals("")) {
            itemHolder.tvPortName.setText("端口" + (position + 1));
        } else {
            itemHolder.tvPortName.setText(alarmDeviceDetail.getPortNameList().get(position));
        }

        if (portConfig.get(position) == 1) {
            itemHolder.tvPortStatus.setText("已配置");
        } else {
            itemHolder.tvPortStatus.setText("未配置");
        }

        itemHolder.llAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, AlarmPortConfigActivity.class);
                int portNum = position + 1;
                intent.putExtra("portNum", portNum);
                String alarmDeviceDetailJson = JSONObject.toJSONString(alarmDeviceDetail);
                intent.putExtra("alarmDeviceDetail", alarmDeviceDetailJson);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return portConfig.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {


        @BindView(R.id.tv_port_name)
        TextView tvPortName;
        @BindView(R.id.tv_port_status)
        TextView tvPortStatus;
        @BindView(R.id.ll_alarm)
        LinearLayout llAlarm;

        public ItemViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }
}
