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

import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.found.EditPartitionActivity;
import com.itc.smartbroadcast.activity.found.PartitionManageActivity;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.bean.FoundPartitionInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作者 : 李观鸿
 */

public class FoundChildPartitionAdapter extends RecyclerView.Adapter {


    private Context mContext;

    public FoundChildPartitionAdapter(Context context) {
        this.mContext = context;
    }

    public List<FoundPartitionInfo> getList() {
        return mList;
    }

    public void setList(List<FoundPartitionInfo> list) {
        if (list != null) {
            this.mList.clear();
            addList(list);
        }
    }

    private void addList(List<FoundPartitionInfo> list) {
        if (list != null) {
            mList.addAll(list);
            notifyDataSetChanged();
        }
    }

    private List<FoundPartitionInfo> mList = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_found_partition, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        ItemViewHolder itemHolder = (ItemViewHolder) holder;
        //分区列表只显示3个item，点击更多显示所有分区，把mList传进分区管理页面就行了
        if (position >= 3) {
            itemHolder.partitionContentLl.setVisibility(View.GONE);
        } else {
            //绑定数据
            FoundPartitionInfo partitionInfo = mList.get(position);
            itemHolder.partitinoNameTv.setText(partitionInfo.getName());
            if (partitionInfo.getDeviceInfoList() == null){
                itemHolder.partitinoDeviceTv.setText("0个终端");
            }else {
                itemHolder.partitinoDeviceTv.setText(partitionInfo.getDeviceInfoList().size()+"个终端");
            }
            itemHolder.partitionContentRl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FoundPartitionInfo foundPartitionInfo = mList.get(position);
                    Intent intent = new Intent();
                    intent.setClass(mContext,EditPartitionActivity.class);
                    List<FoundDeviceInfo> deviceOfPart = foundPartitionInfo.getDeviceInfoList();
                    Gson gson = new Gson();
                    String  sDeviceOfPart = gson.toJson(deviceOfPart);
                    String partName = foundPartitionInfo.getName();
                    String partNum = foundPartitionInfo.getPartitionNum();
                    String accId = foundPartitionInfo.getAccountId()+"";
                    intent.putExtra("AccId",accId);
                    intent.putExtra("PartNum",partNum);
                    intent.putExtra("PartName",partName);
                    intent.putExtra("PartInfo",sDeviceOfPart);
                    mContext.startActivity(intent);
                }
            });
        }


    }

    @Override
    public int getItemCount() {
        if (mList == null)
            return 0;
        else
            return mList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.partitino_name_tv)
        TextView partitinoNameTv;
        @BindView(R.id.partitino_device_tv)
        TextView partitinoDeviceTv;
        @BindView(R.id.partition_content_rl)
        RelativeLayout partitionContentRl;
        @BindView(R.id.partition_content_ll)
        LinearLayout partitionContentLl;

        public ItemViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }
}
