package com.itc.smartbroadcast.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.event.ChoosePlayTerminalActivity;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.EditInstantTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class ChooseTerminalAdapterToTask extends BaseAdapter {
    private List<FoundDeviceInfo> deviceList = new ArrayList<>();
    private List<FoundDeviceInfo> deviceListed = new ArrayList<>();
    private Context mContext;
    private List<FoundDeviceInfo> checkBoxIDList = new ArrayList<>();           //存储checkBox的值
    ChoosePlayTerminalActivity choosePlayTerminalActivity;

    Map<Integer, Boolean> isCheck = new HashMap<>();

    //get set
    public List<FoundDeviceInfo> getCheckBoxIDList() {
        return checkBoxIDList;
    }

    public void setCheckBoxIDList(List<FoundDeviceInfo> checkBoxIDList) {
        this.checkBoxIDList = checkBoxIDList;
    }

    public ChooseTerminalAdapterToTask(List<FoundDeviceInfo> foundDeviceInfos, List<FoundDeviceInfo> deviceListed, Context mContext, ChoosePlayTerminalActivity choosePlayTerminalActivity) {
        this.deviceList.clear();
        this.deviceListed.clear();
        this.deviceList.addAll(foundDeviceInfos);
        this.deviceListed.addAll(deviceListed);
        this.mContext = mContext;
        this.choosePlayTerminalActivity = choosePlayTerminalActivity;
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

        if (deviceList.get(position).getDeviceMedel().endsWith("Z")) {
            testViewHolder.iv_dialog.setVisibility(View.VISIBLE);
        } else {
            testViewHolder.iv_dialog.setVisibility(View.GONE);
        }

        Log.i(TAG, "getView: position->" + position);
        testViewHolder.item_checkBox.setChecked(isCheck.get(position));
        testViewHolder.item_checkBox.setTag(position);

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
                    if (deviceList.get(position).getDeviceMedel().endsWith("Z")) {

                        FoundDeviceInfo device = deviceList.get(position);
                        int[] deviceZone = {1, 1, 1, 1, 1, 1, 1, 1};
                        device.setDeviceZone(deviceZone);
                        deviceList.set(position, device);
                    }
                } else {

                    //checkBoxIDList.remove(testViewHolder.item_checkBox.getText().toString());
                    checkBoxIDList.remove(deviceList.get(position));

                }
                if (isCheck.get(position)) {
                    isCheck.put(position, false);
                } else {
                    isCheck.put(position, true);
                }

                choosePlayTerminalActivity.setCheckSize(checkBoxIDList.size());
            }
        });

        testViewHolder.iv_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                View v = View.inflate(mContext, R.layout.dialog_select_terminal, null);
                final TextView tvSelectAll = (TextView) v.findViewById(R.id.tv_select_all);
                final CheckBox cbOne = (CheckBox) v.findViewById(R.id.cb_one);
                final CheckBox cbTwo = (CheckBox) v.findViewById(R.id.cb_two);
                final CheckBox cbThree = (CheckBox) v.findViewById(R.id.cb_three);
                final CheckBox cbFour = (CheckBox) v.findViewById(R.id.cb_four);
                final CheckBox cbFive = (CheckBox) v.findViewById(R.id.cb_five);
                final CheckBox cbSix = (CheckBox) v.findViewById(R.id.cb_six);
                final CheckBox cbSeven = (CheckBox) v.findViewById(R.id.cb_seven);
                final CheckBox cbEight = (CheckBox) v.findViewById(R.id.cb_eight);

                tvSelectAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (tvSelectAll.getText().toString().equals("全选")) {
                            tvSelectAll.setText("全不选");
                            for (int i = 0; i < 8; i++) {
                                switch (i) {
                                    case 0:
                                        cbOne.setChecked(true);
                                        break;
                                    case 1:
                                        cbTwo.setChecked(true);
                                        break;
                                    case 2:
                                        cbThree.setChecked(true);
                                        break;
                                    case 3:
                                        cbFour.setChecked(true);
                                        break;
                                    case 4:
                                        cbFive.setChecked(true);
                                        break;
                                    case 5:
                                        cbSix.setChecked(true);
                                        break;
                                    case 6:
                                        cbSeven.setChecked(true);
                                        break;
                                    case 7:
                                        cbEight.setChecked(true);
                                        break;
                                }
                            }
                        } else {
                            tvSelectAll.setText("全选");
                            for (int i = 0; i < 8; i++) {
                                switch (i) {
                                    case 0:
                                        cbOne.setChecked(false);
                                        break;
                                    case 1:
                                        cbTwo.setChecked(false);
                                        break;
                                    case 2:
                                        cbThree.setChecked(false);
                                        break;
                                    case 3:
                                        cbFour.setChecked(false);
                                        break;
                                    case 4:
                                        cbFive.setChecked(false);
                                        break;
                                    case 5:
                                        cbSix.setChecked(false);
                                        break;
                                    case 6:
                                        cbSeven.setChecked(false);
                                        break;
                                    case 7:
                                        cbEight.setChecked(false);
                                        break;
                                }
                            }

                        }
                    }
                });


                int[] deviceZone = deviceList.get(position).getDeviceZone();
                if (deviceZone != null && deviceZone.length > 0) {
                    for (int i = 0; i < 8; i++) {
                        switch (i) {
                            case 0:
                                if (deviceZone[i] == 1) {
                                    cbOne.setChecked(true);
                                } else {
                                    cbOne.setChecked(false);
                                }
                                break;
                            case 1:
                                if (deviceZone[i] == 1) {
                                    cbTwo.setChecked(true);
                                } else {
                                    cbTwo.setChecked(false);
                                }
                                break;
                            case 2:
                                if (deviceZone[i] == 1) {
                                    cbThree.setChecked(true);
                                } else {
                                    cbThree.setChecked(false);
                                }
                                break;
                            case 3:
                                if (deviceZone[i] == 1) {
                                    cbFour.setChecked(true);
                                } else {
                                    cbFour.setChecked(false);
                                }
                                break;
                            case 4:
                                if (deviceZone[i] == 1) {
                                    cbFive.setChecked(true);
                                } else {
                                    cbFive.setChecked(false);
                                }
                                break;
                            case 5:
                                if (deviceZone[i] == 1) {
                                    cbSix.setChecked(true);
                                } else {
                                    cbSix.setChecked(false);
                                }
                                break;
                            case 6:
                                if (deviceZone[i] == 1) {
                                    cbSeven.setChecked(true);
                                } else {
                                    cbSeven.setChecked(false);
                                }
                                break;
                            case 7:
                                if (deviceZone[i] == 1) {
                                    cbEight.setChecked(true);
                                } else {
                                    cbEight.setChecked(false);
                                }
                                break;
                        }
                    }
                }

                final Button btnOk = (Button) v.findViewById(R.id.btn_ok);
                final Button btnNo = (Button) v.findViewById(R.id.btn_no);
                final android.app.AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getWindow().setContentView(v);
                btnNo.setVisibility(View.VISIBLE);
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        FoundDeviceInfo device = deviceList.get(position);
                        int[] deviceZone = new int[8];
                        if (cbOne.isChecked()) {
                            deviceZone[0] = 1;
                        } else {
                            deviceZone[0] = 0;
                        }
                        if (cbTwo.isChecked()) {
                            deviceZone[1] = 1;
                        } else {
                            deviceZone[1] = 0;
                        }
                        if (cbThree.isChecked()) {
                            deviceZone[2] = 1;
                        } else {
                            deviceZone[2] = 0;
                        }
                        if (cbFour.isChecked()) {
                            deviceZone[3] = 1;
                        } else {
                            deviceZone[3] = 0;
                        }
                        if (cbFive.isChecked()) {
                            deviceZone[4] = 1;
                        } else {
                            deviceZone[4] = 0;
                        }
                        if (cbSix.isChecked()) {
                            deviceZone[5] = 1;
                        } else {
                            deviceZone[5] = 0;
                        }
                        if (cbSeven.isChecked()) {
                            deviceZone[6] = 1;
                        } else {
                            deviceZone[6] = 0;
                        }
                        if (cbEight.isChecked()) {
                            deviceZone[7] = 1;
                        } else {
                            deviceZone[7] = 0;
                        }

                        device.setDeviceZone(deviceZone);
                        deviceList.set(position, device);
                        choosePlayTerminalActivity.setDeviceListed(device);
                        dialog.dismiss();
                    }
                });
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
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
