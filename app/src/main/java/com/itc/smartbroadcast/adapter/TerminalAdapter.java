package com.itc.smartbroadcast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class TerminalAdapter extends BaseAdapter{
    Context context;
    List<FoundDeviceInfo> data; //这个数据是会改变的，所以要有个变量来备份一下原始数据

    public TerminalAdapter(Context context, List<FoundDeviceInfo> data) {
        this.context = context;
        this.data = data;
    }

    public List<FoundDeviceInfo> getData(){
        return data;
    }



    @Override
    public int getCount() {
        if (data!=null){
            return data.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_index_terminals,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.tvTerminalName = (TextView) convertView.findViewById(R.id.terminal_name);
            viewHolder.tvTerminalIp = (TextView) convertView.findViewById(R.id.terminal_IP);
            viewHolder.tvTerminalStuatus = (TextView) convertView.findViewById(R.id.terminal_stuatus);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        switch (data.get(position).getDeviceStatus()){
            case "在线":
                viewHolder.tvTerminalStuatus.setBackground(context.getResources().getDrawable(R.drawable.bg_online));
                viewHolder.tvTerminalStuatus.setTextColor(context.getResources().getColor(R.color.onlineFontColor));
                break;
            case "锁定":
                viewHolder.tvTerminalStuatus.setBackgroundColor(context.getResources().getColor(R.color.clockColor));
                viewHolder.tvTerminalStuatus.setTextColor(context.getResources().getColor(R.color.clockFontColor));
                break;
            case "离线":
                viewHolder.tvTerminalStuatus.setBackground(context.getResources().getDrawable(R.drawable.bg_offline));
                viewHolder.tvTerminalStuatus.setTextColor(context.getResources().getColor(R.color.gray_999));
                break;
            case "故障":
                viewHolder.tvTerminalStuatus.setBackgroundColor(context.getResources().getColor(R.color.clockColor));
                viewHolder.tvTerminalStuatus.setTextColor(context.getResources().getColor(R.color.clockFontColor));
                break;
            case "空载":
                viewHolder.tvTerminalStuatus.setBackgroundColor(context.getResources().getColor(R.color.clockColor));
                viewHolder.tvTerminalStuatus.setTextColor(context.getResources().getColor(R.color.clockFontColor));
                break;
        }
        viewHolder.tvTerminalName.setText((data.get(position).getDeviceName()));
        viewHolder.tvTerminalIp.setText((data.get(position).getDeviceIp()));
        viewHolder.tvTerminalStuatus.setText((data.get(position).getDeviceStatus()));
        return convertView;
    }

    static class ViewHolder {

        TextView tvTerminalName;
        TextView tvTerminalIp;
        TextView tvTerminalStuatus;
    }


}