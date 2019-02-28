package com.itc.smartbroadcast.activity.personal;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.found.ChooseAmplifierTerminalActivity;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class ChooseTerminalToPersonalAdapter extends BaseAdapter {
    private List<FoundDeviceInfo> deviceList = new ArrayList<>();
    private List<FoundDeviceInfo> deviceListed = new ArrayList<>();
    private Context mContext;
    private List<FoundDeviceInfo> checkBoxIDList = new ArrayList<>();           //存储checkBox的值
    ChooseTerminalToPersonalActivity chooseTerminalToPersonalActivity;

    Map<Integer, Boolean> isCheck = new HashMap<>();

    //get set
    public List<FoundDeviceInfo> getCheckBoxIDList() {
        return checkBoxIDList;
    }

    public void setCheckBoxIDList(List<FoundDeviceInfo> checkBoxIDList) {
        this.checkBoxIDList = checkBoxIDList;
    }

    public ChooseTerminalToPersonalAdapter(List<FoundDeviceInfo> foundDeviceInfos, List<FoundDeviceInfo> deviceListed, Context mContext, ChooseTerminalToPersonalActivity chooseTerminalToPersonalActivity) {
        this.deviceList.clear();
        this.deviceListed.clear();
        this.deviceList.addAll(foundDeviceInfos);
        this.deviceListed.addAll(deviceListed);
        this.mContext = mContext;
        this.chooseTerminalToPersonalActivity = chooseTerminalToPersonalActivity;
        initCheck();
    }

    public void setList(List<FoundDeviceInfo> foundDeviceInfos, List<FoundDeviceInfo> deviceListed) {

        this.deviceList.clear();
        this.deviceListed.clear();
        this.deviceList.addAll(foundDeviceInfos);
        this.deviceListed.addAll(deviceListed);
        initCheck();
        notifyDataSetChanged();
    }

    public List<FoundDeviceInfo> getList() {

        return this.deviceList;
    }

    public void initCheck() {

        isCheck.clear();
        checkBoxIDList.clear();

        for (int position = 0; position < deviceList.size(); position++) {
            for (FoundDeviceInfo deviceInfo : deviceListed) {
                if (deviceList.get(position).getDeviceIp().equals(deviceInfo.getDeviceIp())) {
                    isCheck.put(position, true);
                    int size = 0;
                    for (FoundDeviceInfo checkDevice : checkBoxIDList) {
                        if (checkDevice.getDeviceIp().equals(deviceInfo.getDeviceIp())) {
                        } else {
                            size++;
                        }
                    }
                    if (checkBoxIDList.size() == size) {
                        checkBoxIDList.add(deviceList.get(position));
                    }
                    break;
                }
            }
        }
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
        if (true) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_choose_terminal, null);
            testViewHolder = new TestViewHolder();

            testViewHolder.item_checkBox = (CheckBox) convertView.findViewById(R.id.cb_terminal_name);
            testViewHolder.item_MAC = (TextView) convertView.findViewById(R.id.tv_bind_terminal_mac);
testViewHolder.iv_dialog = (Button) convertView.findViewById(R.id.iv_dialog);

            convertView.setTag(testViewHolder);
        } else {
            testViewHolder = (TestViewHolder) convertView.getTag();
        }
        if (isCheck.get(position) == null) {
            isCheck.put(position, false);
        }

        testViewHolder.item_checkBox.setChecked(isCheck.get(position));
        testViewHolder.item_checkBox.setTag(position);
        testViewHolder.iv_dialog.setVisibility(View.INVISIBLE);

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
                if (isCheck.get(position)) {
                    isCheck.put(position, false);
                } else {
                    isCheck.put(position, true);
                }

                chooseTerminalToPersonalActivity.setCheckSize(checkBoxIDList.size());
            }
        });
        return convertView;
    }

    static class TestViewHolder {
        CheckBox item_checkBox;
        TextView item_MAC;
        Button iv_dialog;
    }

}
