package com.itc.smartbroadcast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.bean.TerminalDeviceStatus;
import com.itc.smartbroadcast.cache.AppDataCache;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lik on 18-9-28.
 */

public class TerminalStatusAdapter extends BaseAdapter {


    List<TerminalDeviceStatus> dataList = new ArrayList<>();
    Context content;

    public void setList(List<TerminalDeviceStatus> terminalDeviceStatusList) {
        this.dataList.clear();
        this.dataList.addAll(terminalDeviceStatusList);
        notifyDataSetChanged();
    }


    public List<TerminalDeviceStatus> getList() {
        return dataList;
    }

    public TerminalStatusAdapter(Context content, List<TerminalDeviceStatus> terminalDeviceStatusList) {
        this.content = content;
        this.dataList.clear();
        this.dataList.addAll(terminalDeviceStatusList);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View view = null;
        if (convertView == null) {
            view = LayoutInflater.from(content).inflate(R.layout.item_terminal_status_list, null);
        } else {
            view = convertView;
        }
        TextView terminalName = (TextView) view.findViewById(R.id.tv_terminal_name);
        TextView passStatus = (TextView) view.findViewById(R.id.tv_pass_status);
        TextView playStatus = (TextView) view.findViewById(R.id.tv_play_status);
        TerminalDeviceStatus terminalDeviceStatus = dataList.get(position);

        String deviceListStr = AppDataCache.getInstance().getString("deviceList");
        List<FoundDeviceInfo> deviceList = JSONArray.parseArray(deviceListStr, FoundDeviceInfo.class);
        FoundDeviceInfo foundDeviceInfo = new FoundDeviceInfo();
        for (FoundDeviceInfo device : deviceList) {
            if (terminalDeviceStatus.getTargetMac().equals(device.getDeviceMac())) {
                foundDeviceInfo = device;
            }
        }
        terminalName.setText(foundDeviceInfo.getDeviceName());
        int[] channelStatus = terminalDeviceStatus.getChannelStatus();
        String passStr = "";

        if(terminalDeviceStatus.getPriority() == 0){
            passStr = "普通";
        }
        if(terminalDeviceStatus.getPriority() == 1){
            passStr = "高级";
        }
        if(terminalDeviceStatus.getPriority() == 2){
            passStr = "重要";
        }
        if(terminalDeviceStatus.getPriority() == 3){
            passStr = "紧急";
        }
//        for (int i = 0; i < 5; i++) {
//            if (channelStatus[i] == 1) {
//                if(terminalDeviceStatus.getPriority() == 0){
//                    passStr = "普通";
//                }
//                if(terminalDeviceStatus.getPriority() == 1){
//                    passStr = "高级";
//                }
//                if(terminalDeviceStatus.getPriority() == 2){
//                    passStr = "重要";
//                }
//                if(terminalDeviceStatus.getPriority() == 3){
//                    passStr = "紧急";
//                }
//
//            }
//        }
//        if(!passStr.equals("")){
//            passStr = passStr.substring(0, passStr.length() - 1);
//        }

        passStatus.setText(passStr);

        switch (terminalDeviceStatus.getPlayStatus()) {
            case 0:
                playStatus.setText("（执行中）");
                playStatus.setTextColor(content.getResources().getColor(R.color.color_status_online));
                break;
            case 1:
                playStatus.setText("（忙碌）");
                playStatus.setTextColor(content.getResources().getColor(R.color.color_status_ml));
                break;
            case 2:
                playStatus.setText("（离线）");
                playStatus.setTextColor(content.getResources().getColor(R.color.color_status_lx));
                break;
        }
        return view;
    }
}
