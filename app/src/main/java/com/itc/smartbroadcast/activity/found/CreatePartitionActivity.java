package com.itc.smartbroadcast.activity.found;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.event.ChoosePlayTerminalActivity;
import com.itc.smartbroadcast.adapter.MyAdapter;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditPartitionResult;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.bean.FoundPartitionInfo;
import com.itc.smartbroadcast.bean.PartitionInfo;
import com.itc.smartbroadcast.bean.PhysicalPartitionInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.EditPartition;
import com.itc.smartbroadcast.channels.protocolhandler.GetDeviceList;
import com.itc.smartbroadcast.listener.LimitInputTextWatcher;
import com.itc.smartbroadcast.util.CharacterParser;
import com.itc.smartbroadcast.util.CheckEmojiUtils;
import com.itc.smartbroadcast.util.ToastUtil;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * create by youmu on 2018/7
 */
public class CreatePartitionActivity extends AppCompatActivity {
    @BindView(R.id.et_part_name)
    EditText etPartName;
    @BindView(R.id.rl_bind_terminal)
    RelativeLayout rlBindTerminal;
    @BindView(R.id.tv_save_part)
    Button tvSavePart;
    @BindView(R.id.tv_not_bind)
    TextView tvNotBind;

    @BindView(R.id.tv_select_device)
    TextView tvSelectDevice;
    @BindView(R.id.ll_binded_terminal)
    LinearLayout llBindedTerminal;
    @BindView(R.id.bt_edit_devicelist)
    Button btEditDevicelist;
    private static String deviceInfo;
    @BindView(R.id.bt_back)
    RelativeLayout btBack;
    private String partNum;
    private int accId;
    List<FoundDeviceInfo> deviceList = new ArrayList<>();
    private String checkedMac = "";
    public static int REQUEST_DEVICE_CODE = 3;
    List<PhysicalPartitionInfo> physicalPartitionInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createpartition);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(CreatePartitionActivity.this, getResources().getColor(R.color.colorMain), 0);
        llBindedTerminal.setVisibility(View.INVISIBLE);
        etPartName.addTextChangedListener(new LimitInputTextWatcher(etPartName));
        init();
    }

    private void init() {
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rlBindTerminal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (llBindedTerminal.getVisibility() == View.INVISIBLE) {
                    llBindedTerminal.setVisibility(View.VISIBLE);
                } else if (llBindedTerminal.getVisibility() == View.VISIBLE) {
                    llBindedTerminal.setVisibility(View.INVISIBLE);
                }
            }
        });
        btEditDevicelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent();
//                intent.putExtra("CheckMac",checkedMac);
//                intent.setClass(EditPartitionActivity.this, ChooseAmplifierTerminalActivity.java.class);
//                EditPartitionActivity.this.startActivityForResult(intent, 3);
                getDevice();
            }
        });

        tvSavePart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String partName = etPartName.getText().toString().trim();
                if (partName.length() < 2 || CheckEmojiUtils.containsEmoji(partName)) {
                    ToastUtil.show(CreatePartitionActivity.this, "请输入正确的分区名");
                } else {

                    PartitionInfo partition = new PartitionInfo();
                    physicalPartitionInfos = new ArrayList<>();

                    for (FoundDeviceInfo device : deviceList) {
                        PhysicalPartitionInfo physicalPartitionInfo = new PhysicalPartitionInfo();
                        physicalPartitionInfo.setMac(device.getDeviceMac());
                        physicalPartitionInfo.setPhycicalPartition(device.getDeviceZone());
                        physicalPartitionInfos.add(physicalPartitionInfo);
                    }
                    ArrayList<String> macList = new ArrayList<String>(Arrays.asList(checkedMac.split(",")));
                    partition.setPartitionName(partName);
                    partition.setDeviceCount(macList.size());
                    partition.setAccountId(AppDataCache.getInstance().getInt("userNum"));
                    partition.setDeviceMacList(macList);
                    partition.setPhycicalPartitionList(physicalPartitionInfos);
                    Log.i("绑定的终端", macList.toString());
                    if (checkedMac.equals("")) {
                        ToastUtil.show(CreatePartitionActivity.this, "请绑定终端");
                    } else if (partName.equals("")) {
                        ToastUtil.show(CreatePartitionActivity.this, "请输入终端名");
                    } else {
                        EditPartition.sendCMD(AppDataCache.getInstance().getString("loginIp"), partition, 0);
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        finish();
                    }
                }
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        etPartName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etPartName.getText().length() < 1) {
                    tvSavePart.setEnabled(false);
                    tvSavePart.setTextColor(getResources().getColor(R.color.colorGray));
                } else {
                    tvSavePart.setEnabled(true);
                    tvSavePart.setTextColor(getResources().getColor(R.color.colorWhite));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        final Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);
        if ("editPartitionResult".equals(baseBean.getType())) {
            EditPartitionResult editPartitionResult = gson.fromJson(baseBean.getData(), EditPartitionResult.class);
            String operator = editPartitionResult.getOperator();
            int result = editPartitionResult.getResult();
            if (operator.equals("00")) {
                if (result == 0) {
                    ToastUtil.show(CreatePartitionActivity.this, "新建失败");
                } else {
                    ToastUtil.show(CreatePartitionActivity.this, "新建成功");
                }
            }
            if (operator.equals("01")) {
                if (result == 0) {
                    ToastUtil.show(CreatePartitionActivity.this, "删除失败");
                } else {
                    init();
                    ToastUtil.show(CreatePartitionActivity.this, "删除成功");
                }
            }
            if (operator.equals("02")) {
                if (result == 0) {
                    ToastUtil.show(CreatePartitionActivity.this, "修改失败");
                } else {
                    ToastUtil.show(CreatePartitionActivity.this, "修改成功");
                }
            }

        }
    }

    @Override
    protected void onResume() {
        EventBus.getDefault().register(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }


    public void getDevice() {
        Intent intent = new Intent(CreatePartitionActivity.this, ChooseAmplifierTerminalActivity.class);
        Gson gson = new Gson();
        String deviceListJson = gson.toJson(deviceList);
        intent.putExtra("deviceList", deviceListJson);
        startActivityForResult(intent, REQUEST_DEVICE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (3 == requestCode) {       //获取设备
            deviceList.clear();
            String deviceListStr = data.getStringExtra("deviceList");
            if (!deviceListStr.equals("")) {
                deviceList.addAll(JSONArray.parseArray(deviceListStr, FoundDeviceInfo.class));
                String str = "";
                checkedMac = "";
                for (FoundDeviceInfo device : deviceList) {
                    str += device.getDeviceName() + "\n";
                    checkedMac += device.getDeviceMac() + ",";
                }
                tvSelectDevice.setText(str);
                tvNotBind.setVisibility(View.GONE);

            }
        }
    }
}
