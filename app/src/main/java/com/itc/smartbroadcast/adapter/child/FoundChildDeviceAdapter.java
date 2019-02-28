package com.itc.smartbroadcast.adapter.child;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.found.AllTerminalsActivity;
import com.itc.smartbroadcast.activity.found.DeviceDetailActivity;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.cache.AppDataCache;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作者 : 李观鸿
 */

public class FoundChildDeviceAdapter extends RecyclerView.Adapter {


    private Context mContext;

    public FoundChildDeviceAdapter(Context context) {
        this.mContext = context;
    }

    public List<FoundDeviceInfo> getList() {
        return mList;
    }

    public void setList(List<FoundDeviceInfo> list) {
        if (list != null) {
            this.mList.clear();
            addList(list);
        }
    }

    private void addList(List<FoundDeviceInfo> list) {
        if (list != null) {
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    private List<FoundDeviceInfo> mList = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_found_device, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        //绑定数据
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        final FoundDeviceInfo deviceInfo = mList.get(position);
        itemHolder.deviceName.setText(deviceInfo.getDeviceName());
        itemHolder.deviceIp.setText(deviceInfo.getDeviceIp());
        itemHolder.deviceStatus.setText(deviceInfo.getDeviceStatus());
        switch (deviceInfo.getDeviceStatus()){
            case "在线":
                itemHolder.deviceStatus.setBackground(mContext.getResources().getDrawable(R.drawable.bg_online));
                itemHolder.deviceStatus.setTextColor(mContext.getResources().getColor(R.color.onlineFontColor));
                break;
            case "离线":
                itemHolder.deviceStatus.setBackground(mContext.getResources().getDrawable(R.drawable.bg_offline));
                itemHolder.deviceStatus.setTextColor(mContext.getResources().getColor(R.color.gray_999));
                break;
            case "锁定":
                itemHolder.deviceStatus.setBackgroundColor(mContext.getResources().getColor(R.color.clockColor));
                itemHolder.deviceStatus.setTextColor(mContext.getResources().getColor(R.color.clockFontColor));
                break;
            case "故障":
                itemHolder.deviceStatus.setBackgroundColor(mContext.getResources().getColor(R.color.clockColor));
                itemHolder.deviceStatus.setTextColor(mContext.getResources().getColor(R.color.clockFontColor));
                break;
            case "空载":
                itemHolder.deviceStatus.setBackgroundColor(mContext.getResources().getColor(R.color.clockColor));
                itemHolder.deviceStatus.setTextColor(mContext.getResources().getColor(R.color.clockFontColor));
                break;
        }
        itemHolder.deviceContentRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //List<FoundDeviceInfo> deviceList = JSONArray.parseArray(AppDataCache.getInstance().getString("deviceList"), FoundDeviceInfo.class);
                //for (FoundDeviceInfo device : deviceList) {
                    //if (deviceInfo.getDeviceMac().equals(device.getDeviceMac())){
                        String deviceIP = deviceInfo.getDeviceIp();
                        String deviceMedel = deviceInfo.getDeviceMedel();
                        String deviceStatus = deviceInfo.getDeviceStatus();
                        String deviceVersionMsg = deviceInfo.getDeviceVersionMsg();
                        String deviceMac = deviceInfo.getDeviceMac();
                        String deviceName = deviceInfo.getDeviceName();
                        String deviceVol = deviceInfo.getDeviceVoice()+"";
                        Intent intent5 = new Intent();
                        intent5.setClass(mContext, DeviceDetailActivity.class);
                        intent5.putExtra("DeviceIP", deviceIP);
                        intent5.putExtra("DeviceMedel", deviceMedel);
                        intent5.putExtra("DeviceStatus", deviceStatus);
                        intent5.putExtra("DeviceVersionMsg", deviceVersionMsg);
                        intent5.putExtra("DeviceVol",deviceVol);
                        intent5.putExtra("DeviceName", deviceName);
                        intent5.putExtra("DeviceMac", deviceMac);
                        mContext.startActivity(intent5);
                   // }
              //  }
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

        @BindView(R.id.device_name)
        TextView deviceName;
        @BindView(R.id.device_ip)
        TextView deviceIp;
        @BindView(R.id.device_status)
        TextView deviceStatus;
        @BindView(R.id.device_content_rl)
        LinearLayout deviceContentRl;

        public ItemViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }
}
