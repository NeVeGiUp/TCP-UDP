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
import com.itc.smartbroadcast.activity.event.ChoosePlayTerminalActivity;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;

import java.util.ArrayList;
import java.util.List;

public class ChooseTerminalAdapterToTask_1 extends BaseAdapter {
    private List<FoundDeviceInfo> deviceList;
    private Context mContext;
    private List<FoundDeviceInfo> checkBoxIDList = new ArrayList<>();           //存储checkBox的值

    //get set
    public List<FoundDeviceInfo> getCheckBoxIDList() {
        return checkBoxIDList;
    }

    public void setCheckBoxIDList(List<FoundDeviceInfo> checkBoxIDList) {
        this.checkBoxIDList = checkBoxIDList;
    }

    public ChooseTerminalAdapterToTask_1(List<FoundDeviceInfo> foundDeviceInfos, Context mContext) {
        this.deviceList = foundDeviceInfos;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return deviceList.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final TestViewHolder testViewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_choose_terminal, null);
            testViewHolder = new TestViewHolder();

            testViewHolder.item_checkBox = (CheckBox) convertView.findViewById(R.id.cb_terminal_name);
            testViewHolder.item_MAC = (TextView) convertView.findViewById(R.id.tv_bind_terminal_mac);

            convertView.setTag(testViewHolder);
        } else {
            testViewHolder = (TestViewHolder) convertView.getTag();
        }

        //设置checkBox的值
        testViewHolder.item_checkBox.setText(deviceList.get(position).getDeviceName());
        testViewHolder.item_MAC.setText(deviceList.get(position).getDeviceMac());

        //获取复选框选中状态改变事件进行增删改
        testViewHolder.item_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                /*
                 * b=选中状态
                 * if b = true 将值添加至checkBoxIDList
                 * if b = false 将值从checkBoxIDList移除
                 * */
                if (b) {

                    //checkBoxIDList.add(testViewHolder.item_checkBox.getText().toString());
                    checkBoxIDList.add(deviceList.get(position));

                } else {

                    //checkBoxIDList.remove(testViewHolder.item_checkBox.getText().toString());
                    checkBoxIDList.remove(deviceList.get(position));

                }

            }
        });
        return convertView;
    }

    static class TestViewHolder {

        CheckBox item_checkBox;
        TextView item_MAC;

    }

}
