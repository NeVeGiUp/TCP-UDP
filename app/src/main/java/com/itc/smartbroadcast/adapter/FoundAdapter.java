package com.itc.smartbroadcast.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.found.AllTerminalsActivity;
import com.itc.smartbroadcast.activity.found.NullPartitionActivity;
import com.itc.smartbroadcast.activity.found.PartitionManageActivity;
import com.itc.smartbroadcast.adapter.child.FoundChildDeviceAdapter;
import com.itc.smartbroadcast.adapter.child.FoundChildPartitionAdapter;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.bean.FoundPartitionInfo;
import com.itc.smartbroadcast.cache.AppDataCache;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 作者 : 李观鸿
 */

public class FoundAdapter extends RecyclerView.Adapter {

   // private static final int TYPE_TOP = 0;//顶部
    private static final int TYPE_ZONE = 0;//分区列表
    private static final int TYPE_DEVICE = 1;//设备列表


    private Context context;
    private LayoutInflater inflater;
    private List<FoundDeviceInfo> mDeviceList;  //设备列表数据
    private List<FoundPartitionInfo> mPartitionList;  //设备列表数据


    public FoundAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);

    }

    public void setDeviceList(List<FoundDeviceInfo> deviceList) {
        this.mDeviceList = deviceList;
        notifyDataSetChanged();
    }

    public void setPartitionList(List<FoundPartitionInfo> partitionList) {
        this.mPartitionList = partitionList;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

//        if (viewType == TYPE_TOP) {
//            return new FoundTopViewHolder(inflater.inflate(R.layout.fragment_found_top, parent, false));
//        } else

            if (viewType == TYPE_ZONE) {
            return new FoundZoneViewHolder(inflater.inflate(R.layout.fragment_found_zone, parent, false));
        } else if (viewType == TYPE_DEVICE) {
            return new FoundDeviceViewHolder(inflater.inflate(R.layout.fragment_found_device, parent, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        int viewType = getItemViewType(position);
//        if (viewType == TYPE_TOP) {
//            FoundTopViewHolder topHolder = (FoundTopViewHolder) holder;
//            bindFoundTop(topHolder);
//        } else
            if (viewType == TYPE_ZONE) {
            FoundZoneViewHolder backHolder = (FoundZoneViewHolder) holder;
            bindFoundZone(backHolder);
        } else if (viewType == TYPE_DEVICE) {
            FoundDeviceViewHolder newholder = (FoundDeviceViewHolder) holder;
            bindFoundDevice(newholder);
        }

    }


    //发现分区数据
    private void bindFoundZone(final FoundZoneViewHolder partitionHolder) {
        partitionHolder.mFoundZoneRv.setHasFixedSize(true);
        partitionHolder.mFoundZoneRv.setFocusableInTouchMode(false);  //去除焦点,recycleView嵌套recycleView时会自动滚动
        partitionHolder.mFoundZoneRv.requestFocus();
        partitionHolder.mFoundZoneRv.setLayoutManager(new LinearLayoutManager(context));

        FoundChildPartitionAdapter chidAdapter = new FoundChildPartitionAdapter(context);
        chidAdapter.setList(mPartitionList);    //插入数据
        partitionHolder.mFoundZoneRv.setAdapter(chidAdapter);

        partitionHolder.mFoundZoneMoreTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppDataCache.getInstance().getString("partitinoCount").equals("0")) {
                    Intent intent = new Intent(context, NullPartitionActivity.class);
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, PartitionManageActivity.class);
                    context.startActivity(intent);
                }
            }
        });
    }


    //发现终端数据
    private void bindFoundDevice(FoundDeviceViewHolder deviceHolder) {
        deviceHolder.mFoundDeviceRv.setHasFixedSize(true);
        deviceHolder.mFoundDeviceRv.setFocusableInTouchMode(false);  //去除焦点,recycleView嵌套recycleView时会自动滚动
        deviceHolder.mFoundDeviceRv.requestFocus();
        deviceHolder.mFoundDeviceRv.setLayoutManager(new LinearLayoutManager(context));

        FoundChildDeviceAdapter chidAdapter = new FoundChildDeviceAdapter(context);
        chidAdapter.setList(mDeviceList);    //插入数据
        deviceHolder.mFoundDeviceRv.setAdapter(chidAdapter);

        deviceHolder.mFoundDeviceManageTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AllTerminalsActivity.class);
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
//        if (dataBean == null)
//            return 0;
//        else
        return 2;
    }

    /**
     * 根据postition返回item的类型
     *
     * @param position
     * @return
     */
    public int getItemViewType(int position) {
//        if (position == 0) {
//            return TYPE_TOP;
//        } else

            if (position == 0) {
            return TYPE_ZONE;
        } else if (position == 1) {
            return TYPE_DEVICE;
        }
        return 0;
    }


//    public class FoundTopViewHolder extends RecyclerView.ViewHolder {
//
//        @BindView(R.id.device_count)
//        TextView deviceCount;
//        @BindView(R.id.partition_count_tv)
//        TextView partitionCountTv;
//        @BindView(R.id.wait_patition_count_tv)
//        TextView waitPatitionCountTv;
//        @BindView(R.id.ll_wait_for_part)
//        LinearLayout llWaitForPart;
//        @BindView(R.id.ll_device_num)
//        LinearLayout llDeviceNum;
//        @BindView(R.id.ll_part_num)
//        LinearLayout llPartNum;
//
//        public FoundTopViewHolder(View inflate) {
//            super(inflate);
//            ButterKnife.bind(this, inflate);
//        }
//    }

    class FoundZoneViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.found_zone_rv)
        RecyclerView mFoundZoneRv;
        @BindView(R.id.found_zone_more_tv)
        TextView mFoundZoneMoreTv;

        public FoundZoneViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }

    class FoundDeviceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.found_device_manage_tv)
        TextView mFoundDeviceManageTv;
        @BindView(R.id.found_device_rv)
        RecyclerView mFoundDeviceRv;

        public FoundDeviceViewHolder(View inflate) {
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
