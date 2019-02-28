package com.itc.smartbroadcast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepolySystemAdapter extends BaseAdapter {
    private List<FoundDeviceInfo> macList;
    private List<FoundDeviceInfo> macListed = new ArrayList<>();
    private Context mContext;
    private List<FoundDeviceInfo> checkBoxIDList = new ArrayList<>();
    Map<Integer, Boolean> isCheck = new HashMap<>();


    public DepolySystemAdapter(List<FoundDeviceInfo> macList,Context mContext) {
        this.macList = macList;
        this.mContext = mContext;
    }


    public List<FoundDeviceInfo> getCheckBoxIDList() {
        return checkBoxIDList;
    }

    public void setCheckBoxIDList(List<FoundDeviceInfo> macList) {
        this.checkBoxIDList = checkBoxIDList;
    }

    @Override
    public int getCount() {
        return macList.size();
    }

    @Override
    public Object getItem(int position) {
        return macList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final TestViewHolder testViewHolder;

        if (true) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_system_depoly, null);
            testViewHolder = new TestViewHolder();

            testViewHolder.item_checkBox = (CheckBox) convertView.findViewById(R.id.cb_select_device);
            testViewHolder.item_device_name = (TextView) convertView.findViewById(R.id.tv_device_name);
            testViewHolder.item_org_ip = (TextView) convertView.findViewById(R.id.tv_target_ip);

            convertView.setTag(testViewHolder);
        } else {
            testViewHolder = (TestViewHolder) convertView.getTag();
        }

        if (isCheck.get(position) == null) {
            isCheck.put(position, false);
        }

        testViewHolder.item_checkBox.setChecked(isCheck.get(position));
        //testViewHolder.item_checkBox.setText(macList.get(position).getDeviceMac());
        testViewHolder.item_device_name.setText(macList.get(position).getDeviceName());
        testViewHolder.item_org_ip.setText("绑定主机:"+macList.get(position).getDeviceIp());

        testViewHolder.item_checkBox.setTag(position);

        testViewHolder.item_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean b) {
                if (b) {
                    checkBoxIDList.add(macList.get(position));
                } else {
                    checkBoxIDList.remove(macList.get(position));
                }
                if (isCheck.get(position)) {
                    isCheck.put(position, false);
                } else {
                    isCheck.put(position, true);
                }
            }
        });

        return convertView;
    }


    static class TestViewHolder {

        CheckBox item_checkBox;
        TextView item_device_name;
        TextView item_org_ip;

    }
}
