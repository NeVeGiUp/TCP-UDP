package com.itc.smartbroadcast.activity.found;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.adapter.TerminalAdapter;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditTerminalResult;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.bean.LoginedInfo;
import com.itc.smartbroadcast.bean.TerminalDetailInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.EditTerminalMsg;
import com.itc.smartbroadcast.channels.protocolhandler.GetDeviceList;
import com.itc.smartbroadcast.helper.CustomDialog;
import com.itc.smartbroadcast.helper.MultiLineRadioGroup;
import com.itc.smartbroadcast.util.StringUtil;
import com.itc.smartbroadcast.util.ToastUtil;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * create by youmu on 2018/7
 */
public class NonePartTerminalActivity extends AppCompatActivity {
    @BindView(R.id.bt_back_personal)
    ImageView btBackPersonal;
    @BindView(R.id.list_terminals)
    ListView list_terminals;
    @BindView(R.id.bt_filter)
    Button btFilter;
    @BindView(R.id.tv_all_terminal_filter)
    TextView tvAllTerminalFilter;
    @BindView(R.id.ll_all_terminal_filter)
    LinearLayout llAllTerminalFilter;
    private TerminalAdapter FdAdapter = null;
    private List<FoundDeviceInfo> FdData = null;
    private Context mContext;
    private String filterDeviceType = "全部";
    private String filterDeviceStatus = "全部";
    private BaseBean baseBean;
    private List<FoundDeviceInfo> infoList;
    private List<FoundDeviceInfo> filterList;
    List<FoundDeviceInfo> operableDeviceInfoList;
    List<FoundDeviceInfo> nonePartInfoList;
    private RadioButton radioButton;
    private RadioButton radioButton2;
    private int scrollPos,scrollTop;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        setContentView(R.layout.activity_all_terminals);
        ButterKnife.bind(this);
        mContext = NonePartTerminalActivity.this;
        Drawable drawable = getResources().getDrawable(R.mipmap.common_icon_xiala_default);
        drawable.setBounds(0, 0, 20, 20);
        init();
        btFilter.setCompoundDrawables(null, null, drawable, null);
        StatusBarUtil.setColor(NonePartTerminalActivity.this, getResources().getColor(R.color.colorMain), 0);
        list_terminals.setTextFilterEnabled(true);
        btBackPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPopWindow(view);
            }
        });

    }

    @Override
    protected void onResume() {
        init();
        EventBus.getDefault().register(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }


    private void init() {
        list_terminals .setSelectionFromTop(scrollPos, scrollTop);
        GetDeviceList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
        if (FdAdapter != null) {
            FdAdapter.notifyDataSetChanged();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        Gson gson = new Gson();
        baseBean = gson.fromJson(json, BaseBean.class);
        if ("getDeviceList".equals(baseBean.getType())) {
            String data = baseBean.getData();
            Log.i("设备信息》》》", data);
            if (data != null) {
                FdData = new ArrayList<FoundDeviceInfo>();
                String userJson = AppDataCache.getInstance().getString("loginedMsg");
                LoginedInfo userInfo = JSONObject.parseObject(userJson, LoginedInfo.class);
                infoList = JSONArray.parseArray(data, FoundDeviceInfo.class);
                operableDeviceInfoList = new ArrayList<>();
                if (userInfo.getUserType().equals("00")) {
                    operableDeviceInfoList.addAll(infoList);
                } else {
                    List<String> list = userInfo.getOperableDeviceMacList();
                    for (String mac : list) {
                        for (FoundDeviceInfo device : infoList) {
                            if (mac.equals(device.getDeviceMac())) {
                                operableDeviceInfoList.add(device);
                            }
                        }
                    }
                }
                nonePartInfoList = new ArrayList<>();
                for (FoundDeviceInfo foundDeviceInfo : operableDeviceInfoList){
                    if (foundDeviceInfo.getDeviceZoneMsg().size()==0){
                        nonePartInfoList.add(foundDeviceInfo);
                    }
                }
                for (FoundDeviceInfo foundDeviceInfo : infoList) {
                    switch (foundDeviceInfo.getDeviceMedel()) {
                        case "TX-8623":
                            foundDeviceInfo.setZhDeviceType("报警设备");
                            break;
                        case "TX-8607":
                            foundDeviceInfo.setZhDeviceType("功放设备");
                            break;
                        case "TX-8660":
                            foundDeviceInfo.setZhDeviceType("功放设备");
                            break;
                        case "TX-8660Z":
                            foundDeviceInfo.setZhDeviceType("功放设备");
                            break;
                        case "TX-8605":
                            foundDeviceInfo.setZhDeviceType("面板设备");
                            break;
                        case "TX-8602":
                            foundDeviceInfo.setZhDeviceType("音源设备");
                            break;
                        case "TX-8627":
                            foundDeviceInfo.setZhDeviceType("音源设备");
                            break;
                        case "TX-8628":
                            foundDeviceInfo.setZhDeviceType("音源设备");
                            break;
                        case "TX-8601":
                            foundDeviceInfo.setZhDeviceType("音源设备");
                            break;
                        case "TX-8606":
                            foundDeviceInfo.setZhDeviceType("功放设备");
                            break;
                    }
                }
                if (filterDeviceStatus.equals("全部")&&filterDeviceType.equals("全部")){
                    FdData.addAll(nonePartInfoList);
                    FdAdapter = new TerminalAdapter(NonePartTerminalActivity.this, FdData);
                    list_terminals.setAdapter(FdAdapter);
                    tvAllTerminalFilter.setText("在“全部”设备类型、“全部”终端条件下，共筛选到" + FdData.size() + "台设备");
                }else {
                    filter2();
                }
                list_terminals.setTextFilterEnabled(true);
                FdAdapter.notifyDataSetChanged();

                list_terminals.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        scrollPos = list_terminals.getFirstVisiblePosition();
                        View v1 = list_terminals.getChildAt(0);
                        scrollTop = (v1 == null) ? 0 : v1.getTop();
                    }
                });

                list_terminals.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        FoundDeviceInfo foundDeviceInfo = FdAdapter.getData().get(position);
                        String deviceIP = foundDeviceInfo.getDeviceIp();
                        String deviceMedel = foundDeviceInfo.getDeviceMedel();
                        String deviceStatus = foundDeviceInfo.getDeviceStatus();
                        String deviceVersionMsg = foundDeviceInfo.getDeviceVersionMsg();
                        String deviceMac = foundDeviceInfo.getDeviceMac();
                        String deviceName = foundDeviceInfo.getDeviceName();
                        String deviceVol = foundDeviceInfo.getDeviceVoice() + "";
                        Intent intent5 = new Intent();
                        intent5.setClass(NonePartTerminalActivity.this, DeviceDetailActivity.class);
                        intent5.putExtra("DeviceIP", deviceIP);
                        intent5.putExtra("DeviceMedel", deviceMedel);
                        intent5.putExtra("DeviceStatus", deviceStatus);
                        intent5.putExtra("DeviceVersionMsg", deviceVersionMsg);
                        intent5.putExtra("DeviceVol", deviceVol);
                        intent5.putExtra("DeviceName", deviceName);
                        intent5.putExtra("DeviceMac", deviceMac);
                        NonePartTerminalActivity.this.startActivityForResult(intent5, 5);
                    }
                });
                list_terminals.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                        deleteDevice(position);
                        return true;
                    }
                });
            }
        }
        if ("editTerminalResult".equals(baseBean.getType())) {
            EditTerminalResult editTerminalResult = gson.fromJson(baseBean.getData(), EditTerminalResult.class);
            int[] resultCode = editTerminalResult.getResult();
            if (resultCode[6] == 1) {
                ToastUtil.show(NonePartTerminalActivity.this, "删除成功");
                init();
            } else {
                ToastUtil.show(NonePartTerminalActivity.this, "删除失败");
            }
        }
    }


    private void deleteDevice(final int position) {
        Vibrator vb = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
        vb.vibrate(100);
        CustomDialog dialog = new CustomDialog(NonePartTerminalActivity.this, "提示", "确定要删除当前设备？",
                "取消", "确认", new CustomDialog.OnOkClickListener() {
            @Override
            public void onClick() {

                FoundDeviceInfo foundDeviceInfo = FdAdapter.getData().get(position);
                String deviceIP = foundDeviceInfo.getDeviceIp();
                String deviceMac = foundDeviceInfo.getDeviceMac();
                String deviceStatus = foundDeviceInfo.getDeviceStatus();
                if (!deviceStatus.equals("在线")) {
                    FdData.remove(position);

                    TerminalDetailInfo detailInfo = new TerminalDetailInfo();
                    //mac
                    detailInfo.setTerminalMac(deviceMac);
                    //设备名称
                    detailInfo.setTerminalName("132");
                    //IP获取方式
                    detailInfo.setTerminalIpMode(00);
                    //设备IP
                    detailInfo.setTerminalIp(deviceIP);
                    //设备子网掩码
                    detailInfo.setTerminalSubnet("255.255.255");
                    //设备网关
                    detailInfo.setTerminalGateway("172.16.13.254");
                    //设备音源类型
                    detailInfo.setTerminalSoundCate("00");
                    //设备默音音量
                    detailInfo.setTerminalDefVolume(00);
                    //设备优先级
                    detailInfo.setTerminalPriority("00");
                    //设备音量
                    detailInfo.setTerminalSetVolume(00);
                    //系统旧密码
                    detailInfo.setTerminalOldPsw("123456");
                    //系统新密码
                    detailInfo.setTerminalNewPsw("123456");
                    //host为指定终端IP地址
                    EditTerminalMsg.sendCMD(AppDataCache.getInstance().getString("loginIp"), detailInfo, true);
                    FdAdapter.notifyDataSetChanged();
                } else {
                    ToastUtil.show(mContext, "无法删除在线的设备！");
                }
            }
        });
        dialog.show();

    }


    private void initPopWindow(final View v) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.popupwindow_choose_terminal, null, false);
        //1.构造一个PopupWindow，参数依次是加载的View，宽高
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popWindow.setAnimationStyle(R.anim.anim_pop);  //设置加载动画

        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        popWindow.setBackgroundDrawable(new ColorDrawable(0xFFFFFF));
        popWindow.showAsDropDown(v, 30, 0);
//		mraRatingBar.setEnabled(false);
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        final MultiLineRadioGroup mrg = (MultiLineRadioGroup)view.findViewById(R.id.mrg);
        final MultiLineRadioGroup mrg2 = (MultiLineRadioGroup)view.findViewById(R.id.mrg2);

        switch (filterDeviceType){
            case "全部":
                radioButton = (RadioButton) view.findViewById(R.id.rb_all_type);
                radioButton.setChecked(true);
                break;
            case "音源设备":
                radioButton = (RadioButton) view.findViewById(R.id.rb_source_device);
                radioButton.setChecked(true);
                break;
            case "面板设备":
                radioButton = (RadioButton) view.findViewById(R.id.rb_panel_device);
                radioButton.setChecked(true);
                break;
            case "功放设备":
                radioButton = (RadioButton) view.findViewById(R.id.rb_amplifier_device);
                radioButton.setChecked(true);
                break;
            case "报警设备":
                radioButton = (RadioButton) view.findViewById(R.id.rb_alarm_device);
                radioButton.setChecked(true);
                break;
            case "未知设备":
                radioButton = (RadioButton) view.findViewById(R.id.rb_unknow_device);
                radioButton.setChecked(true);
                break;
        }
        switch (filterDeviceStatus){
            case "全部":
                radioButton2 = (RadioButton) view.findViewById(R.id.rb_all_status);
                radioButton2.setChecked(true);
                break;
            case "在线":
                radioButton2 = (RadioButton) view.findViewById(R.id.rb_online);
                radioButton2.setChecked(true);
                break;
            case "离线":
                radioButton2 = (RadioButton) view.findViewById(R.id.rb_offline);
                radioButton2.setChecked(true);
                break;
            case "忙碌":
                radioButton2 = (RadioButton) view.findViewById(R.id.rb_busy);
                radioButton2.setChecked(true);
                break;
            case "锁定":
                radioButton2 = (RadioButton) view.findViewById(R.id.rb_clock);
                radioButton2.setChecked(true);
                break;
            case "故障":
                radioButton2 = (RadioButton) view.findViewById(R.id.rb_error);
                radioButton2.setChecked(true);
                break;
        }
        Button btnFilter = (Button) view.findViewById(R.id.filter);
        Button btnReset = (Button) view.findViewById(R.id.bt_reset);
        mrg.setOnCheckedChangeListener(new MultiLineRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MultiLineRadioGroup group, int checkedId) {
                radioButton = (RadioButton)view.findViewById(checkedId);
            }
        });
        mrg2.setOnCheckedChangeListener(new MultiLineRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(MultiLineRadioGroup group, int checkedId) {
                radioButton2 = (RadioButton)view.findViewById(checkedId);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mrg.clearCheck();
                mrg2.clearCheck();
            }
        });
        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioButton == null || radioButton2 == null) {
                    popWindow.dismiss();
                } else {
                    if (operableDeviceInfoList == null) {
                        ToastUtil.show(NonePartTerminalActivity.this, "没有可筛选的终端");
                    } else {
                        filterDeviceType = radioButton.getText().toString();
                        filterDeviceStatus = radioButton2.getText().toString();
                        if (filterDeviceType.equals("全部") && filterDeviceStatus.equals("全部")) {
                            init();
                            popWindow.dismiss();
                        } else if (StringUtil.isEmpty(filterDeviceStatus) && StringUtil.isEmpty(filterDeviceType)) {
                            init();
                            popWindow.dismiss();
                        } else if (filterDeviceType.equals("全部") && StringUtil.isEmpty(filterDeviceStatus)) {
                            init();
                            popWindow.dismiss();
                        } else if (filterDeviceStatus.equals("全部") && StringUtil.isEmpty(filterDeviceType)) {
                            init();
                            popWindow.dismiss();
                        } else if (filterDeviceStatus.equals("全部") || StringUtil.isEmpty(filterDeviceStatus)) {
                            filterList = new ArrayList<>();
                            if (operableDeviceInfoList == null) {
                                ToastUtil.show(NonePartTerminalActivity.this, "没有可筛选的终端");
                            } else {
                                for (FoundDeviceInfo foundDeviceInfo : nonePartInfoList) {
                                    if (filterDeviceType.equals(foundDeviceInfo.getZhDeviceType())) {
                                        filterList.add(foundDeviceInfo);
                                    }
                                    FdData.clear();
                                    FdData.addAll(filterList);
                                    FdAdapter = new TerminalAdapter(NonePartTerminalActivity.this, FdData);
                                    list_terminals.setAdapter(FdAdapter);
                                    tvAllTerminalFilter.setText("在“" + filterDeviceType + "”设备类型、“" + "全部" + "”终端条件下，共筛选到" + filterList.size() + "台设备");
                                    popWindow.dismiss();
                                }
                            }
                        } else if (filterDeviceType.equals("全部") || StringUtil.isEmpty(filterDeviceType)) {
                            if (operableDeviceInfoList == null) {
                                ToastUtil.show(NonePartTerminalActivity.this, "没有可筛选的终端");
                            } else {
                                filterList = new ArrayList<>();
                                for (FoundDeviceInfo foundDeviceInfo : nonePartInfoList) {
                                    if (foundDeviceInfo.getDeviceStatus().equals(filterDeviceStatus)) {
                                        filterList.add(foundDeviceInfo);
                                    }
                                    FdData.clear();
                                    FdData.addAll(filterList);
                                    FdAdapter = new TerminalAdapter(NonePartTerminalActivity.this, FdData);
                                    list_terminals.setAdapter(FdAdapter);
                                    tvAllTerminalFilter.setText("在“" + "全部" + "”设备类型、“" + filterDeviceStatus + "”终端条件下，共筛选到" + filterList.size() + "台设备");
                                    popWindow.dismiss();
                                }
                            }
                        } else {
                            filterList = new ArrayList<>();
                            if (operableDeviceInfoList == null) {
                                ToastUtil.show(NonePartTerminalActivity.this, "没有可筛选的终端");
                            } else {
                                for (FoundDeviceInfo foundDeviceInfo : nonePartInfoList) {
                                    if (filterDeviceType.equals(foundDeviceInfo.getZhDeviceType()) && foundDeviceInfo.getDeviceStatus().equals(filterDeviceStatus)) {
                                        filterList.add(foundDeviceInfo);
                                    }
                                    FdData.clear();
                                    FdData.addAll(filterList);
                                    FdAdapter = new TerminalAdapter(NonePartTerminalActivity.this, FdData);
                                    list_terminals.setAdapter(FdAdapter);
                                    tvAllTerminalFilter.setText("在“" + filterDeviceType + "”设备类型、“" + filterDeviceStatus + "”终端条件下，共筛选到" + filterList.size() + "台设备");
                                    popWindow.dismiss();
                                }
                            }
                        }
                    }
                }
            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case 5:
//                filterDeviceType = data.getStringExtra("FilterDeviceType");
//                filterDeviceStatus = data.getStringExtra("FilterDeviceStatus");
//                break;
//        }
//    }


    private void filter2() {
        if (operableDeviceInfoList == null) {
            ToastUtil.show(NonePartTerminalActivity.this, "没有可筛选的终端");
        } else {
            if (filterDeviceType.equals("全部") && filterDeviceStatus.equals("全部")) {
                init();
            } else if (StringUtil.isEmpty(filterDeviceStatus) && StringUtil.isEmpty(filterDeviceType)) {
                init();
            } else if (filterDeviceType.equals("全部") && StringUtil.isEmpty(filterDeviceStatus)) {
                init();
            } else if (filterDeviceStatus.equals("全部") && StringUtil.isEmpty(filterDeviceType)) {
                init();
            } else if (filterDeviceStatus.equals("全部") || StringUtil.isEmpty(filterDeviceStatus)) {
                filterList = new ArrayList<>();
                for (FoundDeviceInfo foundDeviceInfo : nonePartInfoList) {
                    if (filterDeviceType.equals(foundDeviceInfo.getZhDeviceType())) {
                        filterList.add(foundDeviceInfo);
                    }
                    FdData.clear();
                    FdData.addAll(filterList);
                    FdAdapter = new TerminalAdapter(NonePartTerminalActivity.this, FdData);
                    list_terminals.setAdapter(FdAdapter);
                    tvAllTerminalFilter.setText("在“" + filterDeviceType + "”设备类型、“" + "全部" + "”终端条件下，共筛选到" + filterList.size() + "台设备");
                }
            } else if (filterDeviceType.equals("全部") || StringUtil.isEmpty(filterDeviceType)) {
                filterList = new ArrayList<>();
                for (FoundDeviceInfo foundDeviceInfo : nonePartInfoList) {
                    if (foundDeviceInfo.getDeviceStatus().equals(filterDeviceStatus)) {
                        filterList.add(foundDeviceInfo);
                    }
                    FdData.clear();
                    FdData.addAll(filterList);
                    FdAdapter = new TerminalAdapter(NonePartTerminalActivity.this, FdData);
                    list_terminals.setAdapter(FdAdapter);
                    tvAllTerminalFilter.setText("在“" + "全部" + "”设备类型、“" + filterDeviceStatus + "”终端条件下，共筛选到" + filterList.size() + "台设备");
                }
            } else {
                filterList = new ArrayList<>();
                for (FoundDeviceInfo foundDeviceInfo : nonePartInfoList) {
                    if (filterDeviceType.equals(foundDeviceInfo.getZhDeviceType()) && foundDeviceInfo.getDeviceStatus().equals(filterDeviceStatus)) {
                        filterList.add(foundDeviceInfo);
                    }
                    FdData.clear();
                    FdData.addAll(filterList);
                    FdAdapter = new TerminalAdapter(NonePartTerminalActivity.this, FdData);
                    list_terminals.setAdapter(FdAdapter);
                    tvAllTerminalFilter.setText("在“" + filterDeviceType + "”设备类型、“" + filterDeviceStatus + "”终端条件下，共筛选到" + filterList.size() + "台设备");
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
