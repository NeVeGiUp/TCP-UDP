package com.itc.smartbroadcast.activity.found;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.personal.ChooseTerminalToPersonalActivity;
import com.itc.smartbroadcast.activity.personal.CreateAccountActivity;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.ControlPanelInfo;
import com.itc.smartbroadcast.bean.EditControlPanelResult;
import com.itc.smartbroadcast.bean.EditSoundSourceTypeResult;
import com.itc.smartbroadcast.bean.EditTerminalResult;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.bean.GetSoundSourceTypeResult;
import com.itc.smartbroadcast.bean.PowerAmplifierInfo;
import com.itc.smartbroadcast.bean.TerminalDetailInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.EditCollectorSoundSourceType;
import com.itc.smartbroadcast.channels.protocolhandler.EditControlPanel;
import com.itc.smartbroadcast.channels.protocolhandler.EditTerminalMsg;
import com.itc.smartbroadcast.channels.protocolhandler.GetCollectorSoundSourceType;
import com.itc.smartbroadcast.channels.protocolhandler.GetControlPanelDetail;
import com.itc.smartbroadcast.channels.protocolhandler.GetDeviceList;
import com.itc.smartbroadcast.channels.protocolhandler.GetPowerAmplifierDetail;
import com.itc.smartbroadcast.channels.protocolhandler.GetTerminalDetail;
import com.itc.smartbroadcast.listener.LimitInputTextWatcher;
import com.itc.smartbroadcast.util.CheckEmojiUtils;
import com.itc.smartbroadcast.util.StringUtil;
import com.itc.smartbroadcast.util.ToastUtil;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * create by youmu
 */
public class DeviceDetailActivity extends AppCompatActivity {


    @BindView(R.id.bt_save_terminal_info)
    Button btSaveTerminalInfo;
    @BindView(R.id.et_device_name)
    EditText etDeviceName;
    @BindView(R.id.tv_terminal_IP)
    TextView tvTerminalIP;
    @BindView(R.id.tv_gateway_hint)
    TextView tvGatewayHint;
    @BindView(R.id.tv_subnet_hint)
    TextView tvSubnetHint;
    @BindView(R.id.tv_setVol_hint)
    TextView tvSetVolHint;
    @BindView(R.id.rl_terminal_IP)
    RelativeLayout rlTerminalIP;
    @BindView(R.id.tv_terminal_sound_cate)
    TextView tvTerminalSoundCate;
    @BindView(R.id.rl_source_type)
    RelativeLayout rlSourceType;
    @BindView(R.id.tv_terminal_priority)
    TextView tvTerminalPriority;
    @BindView(R.id.rl_default_source)
    RelativeLayout rlDefaultSource;
    @BindView(R.id.rl_sound_effect)
    RelativeLayout rlSoundEffect;
    @BindView(R.id.iv_terminalicon)
    ImageView ivTerminalicon;
    @BindView(R.id.iv_terminal_icon)
    ImageView ivTerminalIcon;
    @BindView(R.id.tv_terminal_medel)
    TextView tvTerminalMedel;
    @BindView(R.id.tv_terminal_ver)
    TextView tvTerminalVer;
    @BindView(R.id.tv_terminal_mac)
    TextView tvTerminalMac;
    @BindView(R.id.terminal_deviceStatus)
    TextView terminalDeviceStatus;
    @BindView(R.id.rl_bind_device)
    RelativeLayout rlBindDevice;
    @BindView(R.id.hintGateWay)
    TextView hintGateWay;
    @BindView(R.id.hintMask)
    TextView hintMask;
    @BindView(R.id.bt_back)
    RelativeLayout btBack;
    @BindView(R.id.tv_collector_source_type)
    TextView tvCollectorSourceType;
    @BindView(R.id.rl_collector_source_type)
    RelativeLayout rlCollectorSourceType;
    @BindView(R.id.ll_binded_terminal)
    LinearLayout llBindedTerminal;
    @BindView(R.id.bt_edit_devicelist)
    Button btEditDevicelist;
    @BindView(R.id.tv_select_device)
    TextView tvSelectDevice;

    private ListView sourceTypeList;
    private String terminalPriority, deviceIp, terminalGateway, terminalSubnet, deviceStatus, ssCollectorSourceType;
    private int terminalSetVolume, terminalIPMode, terminalHighGain, terminalLowGain;
    private int[] mixingEnableState_s, mixingEnableState_p, mixingEnableState_e;
    private String[] collectorSourceType;
    private RadioOnClick OnClick = new RadioOnClick(0);
    private String filterDeviceType, filterDeviceStatus;
    private int checkPoint = 0;
    List<FoundDeviceInfo> deviceList = new ArrayList<>();
    private String checkedMac = "";
    private List<String> macList;
    List<FoundDeviceInfo> deviceInfoList;
    private ControlPanelInfo.DeviceMsgInner deviceMsgInner2;
    private String sDeviceOfPart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(DeviceDetailActivity.this, getResources().getColor(R.color.colorMain), 0);
        EventBus.getDefault().register(this);
        collectorSourceType = new String[]{"普通音源", "话筒音源"};
        rlDefaultSource.setVisibility(View.GONE);
        rlSoundEffect.setVisibility(View.GONE);
        rlSourceType.setVisibility(View.GONE);
        rlBindDevice.setVisibility(View.GONE);
        rlCollectorSourceType.setVisibility(View.GONE);
        // 去除除了a-z  A-Z与0-9和中文的其他符号
        etDeviceName.addTextChangedListener(new LimitInputTextWatcher(etDeviceName));
        init();

        setInVisibility();
    }

    //根据设备类型显示对应的选项
    private void setInVisibility() {
        Intent intent = getIntent();
        String deviceIP = intent.getStringExtra("DeviceIP");
        String deviceMedel = intent.getStringExtra("DeviceMedel");
        filterDeviceType = intent.getStringExtra("FlierDeviceType");
        filterDeviceStatus = intent.getStringExtra("FlierDeviceStauts");
        switch (deviceMedel) {
            //报警设备
            //报警终端
            case "TX-8623":
                //rlSourceType.setVisibility(View.VISIBLE);
                //rlDefaultSource.setVisibility(View.VISIBLE);
                rlSourceType.setFocusable(false);
                rlSourceType.setClickable(false);
                rlDefaultSource.setFocusable(false);
                rlDefaultSource.setClickable(false);
                GetTerminalDetail.sendCMD(deviceIP);
                break;
            //功放设备
            //网络功放
            case "TX-8660":
                rlSoundEffect.setVisibility(View.VISIBLE);
                GetPowerAmplifierDetail.sendCMD(deviceIP);
                GetTerminalDetail.sendCMD(deviceIP);
                break;
            //有源音箱
            case "TX-8607":
                rlSoundEffect.setVisibility(View.VISIBLE);
                GetPowerAmplifierDetail.sendCMD(deviceIP);
                GetTerminalDetail.sendCMD(deviceIP);
                break;
                //网络解码终端
            case "TX-8606":
                rlSoundEffect.setVisibility(View.VISIBLE);
                GetPowerAmplifierDetail.sendCMD(deviceIP);
                GetTerminalDetail.sendCMD(deviceIP);
                break;
            //面板设备
            //控制面板
            case "TX-8605":
                rlBindDevice.setVisibility(View.VISIBLE);
                GetTerminalDetail.sendCMD(deviceIP);
                GetControlPanelDetail.sendCMD(deviceIP);
                break;
            //发送/音源设备
            //寻呼话筒
            case "TX-8602":
//                rlSourceType.setVisibility(View.VISIBLE);
//                rlDefaultSource.setVisibility(View.VISIBLE);
                GetTerminalDetail.sendCMD(deviceIP);
                break;
            //CD机
            case "TX-8627":
//                rlSourceType.setVisibility(View.VISIBLE);
//                rlDefaultSource.setVisibility(View.VISIBLE);
                GetTerminalDetail.sendCMD(deviceIP);
                break;
            //FM/AM调谐器
            case "TX-8628":
//                rlSourceType.setVisibility(View.VISIBLE);
//                rlDefaultSource.setVisibility(View.VISIBLE);
                GetTerminalDetail.sendCMD(deviceIP);
                break;
            //网络采集器
            case "TX-8601":
//                rlSourceType.setVisibility(View.VISIBLE);
//                rlDefaultSource.setVisibility(View.VISIBLE);
                rlCollectorSourceType.setVisibility(View.VISIBLE);
                GetCollectorSoundSourceType.sendCMD(deviceIP);
                GetTerminalDetail.sendCMD(deviceIP);
                break;
        }
        if (deviceMedel.contains("Z")) {
            rlSoundEffect.setVisibility(View.VISIBLE);
            GetPowerAmplifierDetail.sendCMD(deviceIP);
            GetTerminalDetail.sendCMD(deviceIP);
        }
    }


    private void init() {
        Intent intent = getIntent();
        //设备IP
        deviceIp = intent.getStringExtra("DeviceIP");
        tvTerminalIP.setText(deviceIp);
        //设备型号
        String deviceMedel = intent.getStringExtra("DeviceMedel");
        tvTerminalMedel.setText(deviceMedel);
        //设备状态
        deviceStatus = intent.getStringExtra("DeviceStatus");
        terminalDeviceStatus.setText(deviceStatus);
        if (!AppDataCache.getInstance().getString("userType").equals("00")) {
            rlSourceType.setFocusable(false);
            rlSourceType.setClickable(false);
            rlTerminalIP.setFocusable(false);
            rlTerminalIP.setClickable(false);
            rlDefaultSource.setFocusable(false);
            rlDefaultSource.setClickable(false);
            rlBindDevice.setFocusable(false);
            rlBindDevice.setClickable(false);
            rlSoundEffect.setFocusable(false);
            rlSoundEffect.setClickable(false);
            btSaveTerminalInfo.setVisibility(View.INVISIBLE);
            ToastUtil.show(DeviceDetailActivity.this, "普通用户无法对终端进行操作");
            btBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }
        if (deviceStatus.equals("离线")) {
            rlSourceType.setFocusable(false);
            rlSourceType.setClickable(false);
            rlTerminalIP.setFocusable(false);
            rlTerminalIP.setClickable(false);
            rlDefaultSource.setFocusable(false);
            rlDefaultSource.setClickable(false);
            rlBindDevice.setFocusable(false);
            rlBindDevice.setClickable(false);
            rlSoundEffect.setFocusable(false);
            rlSoundEffect.setClickable(false);
            ToastUtil.show(DeviceDetailActivity.this, "当前设备已离线");
        }
        //设备版本
        String deviceVersionMsg = intent.getStringExtra("DeviceVersionMsg");
        tvTerminalVer.setText(deviceVersionMsg);
        //设备名
        String deviceName = intent.getStringExtra("DeviceName");
        etDeviceName.setText(deviceName);
        //设备MAC
        String deviceMac = intent.getStringExtra("DeviceMac");
        tvTerminalMac.setText(deviceMac);
        //设备音量
        terminalSetVolume = Integer.parseInt(intent.getStringExtra("DeviceVol"));
    }

    private void refreshData() {
        saveTerminalInfo();
        GetPowerAmplifierDetail.sendCMD(deviceIp);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);
        if ("getTerminalDetail".equals(baseBean.getType())) {
            final String data = baseBean.getData();
            if (data != null) {
                Log.i("音源设备详情》》》", data);
                ArrayList<TerminalDetailInfo> terminalDetailInfos = JSON.parseObject(data, new TypeReference<ArrayList<TerminalDetailInfo>>() {
                });
                for (TerminalDetailInfo terminalDetailInfo : terminalDetailInfos) {
                    //网关
                    terminalGateway = terminalDetailInfo.getTerminalGateway();
                    hintGateWay.setText(terminalGateway);
                    //Ip模式
                    terminalIPMode = terminalDetailInfo.getTerminalIpMode();
                    //子网掩码
                    terminalSubnet = terminalDetailInfo.getTerminalSubnet();
                    hintMask.setText(terminalSubnet);
                    //默音权限（隐藏）
                    terminalPriority = terminalDetailInfo.getTerminalPriority();
                    if ("00".equals(terminalPriority)) {
                        tvTerminalPriority.setText("关闭");
                    } else {
                        //默认音量
                        String terminalDefVolume = terminalDetailInfo.getTerminalDefVolume() + "";
                        tvTerminalPriority.setText(terminalDefVolume + "%");
                    }
                    //音源类型
                    String terminalSoundCate = terminalDetailInfo.getTerminalSoundCate();
                    tvTerminalSoundCate.setText(terminalSoundCate);
                }
            }
        }
        if ("getPowerAmplifierDetail".equals(baseBean.getType())) {
            final String data = baseBean.getData();
            if (data != null) {
                Log.i("功放设备详情》》》", data);
                ArrayList<PowerAmplifierInfo> powerAmplifierInfos = JSON.parseObject(data, new TypeReference<ArrayList<PowerAmplifierInfo>>() {
                });
                for (PowerAmplifierInfo PowerAmplifierInfo : powerAmplifierInfos) {
                    //高音增益
                    terminalHighGain = PowerAmplifierInfo.getHighGain();
                    //低音增益
                    terminalLowGain = PowerAmplifierInfo.getLowGain();
                    //S0-S5类型混音使能状态
                    mixingEnableState_s = PowerAmplifierInfo.getMixingEnableState_s();
                    //P0-P5类型混音使能状态
                    mixingEnableState_p = PowerAmplifierInfo.getMixingEnableState_p();
                    //E0-E5类型混音使能状态
                    mixingEnableState_e = PowerAmplifierInfo.getMixingEnableState_e();
                }
            }
        }
        if ("editTerminalResult".equals(baseBean.getType())) {
            EditTerminalResult editTerminalResult = gson.fromJson(baseBean.getData(), EditTerminalResult.class);
            int[] resultCode = editTerminalResult.getResult();
            if (resultCode[0] == 1 && resultCode[1] == 1) {
                ToastUtil.show(DeviceDetailActivity.this, "修改成功");
            } else {
                ToastUtil.show(DeviceDetailActivity.this, "修改失败！请检查网络或数据");
            }
        }
        if ("getCollectorSoundSourceTypeResult".equals(baseBean.getType())) {
            GetSoundSourceTypeResult getSoundSourceTypeResult = gson.fromJson(baseBean.getData(), GetSoundSourceTypeResult.class);
            if (getSoundSourceTypeResult.getResult() == 1) {
                tvCollectorSourceType.setText("话筒音源");
                ssCollectorSourceType = "话筒音源";
                OnClick = new RadioOnClick(1);
            } else if (getSoundSourceTypeResult.getResult() == 0) {
                tvCollectorSourceType.setText("普通音源");
                ssCollectorSourceType = "普通音源";
                OnClick = new RadioOnClick(0);
            }
        }
        if ("editSoundSourceTypeResult".equals(baseBean.getType())) {
            EditSoundSourceTypeResult editSoundSourceTypeResult = gson.fromJson(baseBean.getData(), EditSoundSourceTypeResult.class);
            if (editSoundSourceTypeResult.getResult() == 1) {
                ToastUtil.show(DeviceDetailActivity.this, "修改音源类型成功");
            } else if (editSoundSourceTypeResult.getResult() == 0) {
                ToastUtil.show(DeviceDetailActivity.this, "修改音源类型失败");
            } else {
                ToastUtil.show(DeviceDetailActivity.this, "修改音源类型失败");
            }
        }
        if ("getControlPanelDetail".equals(baseBean.getType())){
            GetDeviceList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
            String data = baseBean.getData();
            Log.i("面板绑定设备》》》", data);
            ControlPanelInfo deviceMsgInner = JSON.parseObject(data,ControlPanelInfo.class);
            List<ControlPanelInfo.DeviceMsgInner> ss = deviceMsgInner.getDeviceMsgList();
            String deviceOfPanel = gson.toJson(ss);
            Log.i("deviceOfPanel",deviceOfPanel+" /n  "+ss);
            deviceOfPanel = deviceOfPanel.replaceAll("\\[","").replaceAll("\\]","");
            deviceMsgInner2 = JSON.parseObject(deviceOfPanel,ControlPanelInfo.DeviceMsgInner.class);
            macList = new ArrayList<>();

            for (int i=0;i<ss.size();i++){
               macList.add(ss.get(i).getBindDeviceMac());
            }
        }
        if ("getDeviceList".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                deviceInfoList = JSONArray.parseArray(data, FoundDeviceInfo.class);
                List<FoundDeviceInfo> deviceList2 = new ArrayList<>();
                for (int i=0;i<macList.size();i++) {
                    deviceMsgInner2.setPanelDeviceList(deviceList2);
                    for (FoundDeviceInfo fdSs : deviceInfoList) {
                        if (macList.get(i).equals(fdSs.getDeviceMac())) {
                            deviceMsgInner2.getPanelDeviceList().add(fdSs);
                        }
                    }
                }
                String str = "";
                deviceList = deviceMsgInner2.getPanelDeviceList();
                sDeviceOfPart = gson.toJson(deviceList);
                Log.i("de>>>>",sDeviceOfPart);
                for (FoundDeviceInfo ss : deviceList){
                    str += ss.getDeviceName() + "\n";
                }
                tvSelectDevice.setText(str);
            }
        }
        if ("editControlPanelResult".equals(baseBean.getType())){
            EditControlPanelResult configureTargetHostResult = gson.fromJson(baseBean.getData(),EditControlPanelResult.class);
            if (configureTargetHostResult.getResult()==1){
                ToastUtil.show(this,"绑定成功");
            }else {
                ToastUtil.show(this,"绑定失败");
            }
        }
    }

    @OnClick({R.id.bt_save_terminal_info, R.id.rl_source_type, R.id.rl_default_source, R.id.bt_back,
            R.id.rl_terminal_IP, R.id.rl_sound_effect, R.id.rl_collector_source_type, R.id.rl_bind_device,R.id.bt_edit_devicelist})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_save_terminal_info:
                checkPoint = 1;
                saveTerminalInfo2();
                break;
            case R.id.rl_source_type:
                checkPoint = 1;
                String terminalSoundCate2 = tvTerminalSoundCate.getText() + "";
                Intent source1 = new Intent();
                source1.setClass(DeviceDetailActivity.this, SourceTypeActivity.class);
                source1.putExtra("SourceType", terminalSoundCate2);
                DeviceDetailActivity.this.startActivityForResult(source1, 1);
                break;
            case R.id.rl_default_source:
                checkPoint = 1;
                String deviceDefVol = tvTerminalPriority.getText().toString();
                Intent priority = new Intent();
                priority.setClass(DeviceDetailActivity.this, DefaultSourceSettingActivity.class);
                priority.putExtra("DevicePriority", terminalPriority);
                priority.putExtra("DevicedefVol", deviceDefVol.replaceAll("%", ""));
                DeviceDetailActivity.this.startActivityForResult(priority, 2);
                break;
            case R.id.bt_back:
                if (checkPoint == 1) {
                    if (!AppDataCache.getInstance().getString("userType").equals("00")) {
                        finish();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        View v = View.inflate(this, R.layout.dialog_tips, null);
                        final TextView tvMsg = (TextView) v.findViewById(R.id.tv_msg);
                        final Button btnOk = (Button) v.findViewById(R.id.btn_ok);
                        final Button btnNo = (Button) v.findViewById(R.id.btn_no);
                        final AlertDialog dialog = builder.create();
                        dialog.show();
                        dialog.getWindow().setContentView(v);
                        tvMsg.setText("信息未保存，确定退出吗？");
                        btnNo.setVisibility(View.VISIBLE);
                        btnOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                            }
                        });
                        btnNo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });
                    }
                } else {
                    finish();
                }
                break;
            case R.id.rl_terminal_IP:
                checkPoint = 1;
                Intent ipconfig = new Intent();
                String terminalIp = tvTerminalIP.getText().toString();
                String IpMode = terminalIPMode + "";
                ipconfig.setClass(DeviceDetailActivity.this, TerminalGetIPActivity.class);
                ipconfig.putExtra("TerminalIPMode", IpMode);//IP获取模式
                ipconfig.putExtra("TerminalIP", terminalIp);//IP
                ipconfig.putExtra("TerminalGateway", terminalGateway);//网关
                ipconfig.putExtra("TerminalSubnet", terminalSubnet);//子网掩码
                DeviceDetailActivity.this.startActivityForResult(ipconfig, 3);
                break;
            case R.id.rl_sound_effect:
                checkPoint = 1;
                Intent effect = new Intent();
                effect.setClass(DeviceDetailActivity.this, SoundEffectSettingActivity.class);
                String ssVol = terminalSetVolume + "";
                effect.putExtra("TerminalSetVolume", ssVol);
                effect.putExtra("TerminalHighGain", terminalHighGain);
                effect.putExtra("TerminalLowGain", terminalLowGain);
                effect.putExtra("MixingEnableState_s", mixingEnableState_s);
                effect.putExtra("MixingEnableState_p", mixingEnableState_p);
                effect.putExtra("MixingEnableState_e", mixingEnableState_e);
effect.putExtra("TerminalMedel",tvTerminalMedel.getText().toString());
                String terminalIp2 = tvTerminalIP.getText().toString();
                effect.putExtra("TerminalIP", terminalIp2);
                DeviceDetailActivity.this.startActivityForResult(effect, 4);
                break;
            case R.id.rl_collector_source_type:
                checkPoint = 1;
                android.support.v7.app.AlertDialog ad = new android.support.v7.app.AlertDialog.Builder(DeviceDetailActivity.this)
                        .setTitle(getResources().getString(R.string.plese_select_type_source))
                        .setSingleChoiceItems(collectorSourceType, OnClick.getIndex(), OnClick)
                        .create();
                sourceTypeList = ad.getListView();
                ad.show();
                break;
            case R.id.rl_bind_device:
                if (llBindedTerminal.getVisibility() == View.GONE) {
                    llBindedTerminal.setVisibility(View.VISIBLE);
                } else if (llBindedTerminal.getVisibility() == View.VISIBLE) {
                    llBindedTerminal.setVisibility(View.GONE);
                }
                break;
            case R.id.bt_edit_devicelist:
                getDevice();
                break;
        }

    }
    public void getDevice() {
        Intent intent = new Intent(DeviceDetailActivity.this, ChooseTerminalToPersonalActivity.class);
        Gson gson = new Gson();
        String deviceListJson = gson.toJson(deviceList);
        intent.putExtra("index","1");
        intent.putExtra("deviceList", deviceListJson);
        startActivityForResult(intent, 6);
    }

    class RadioOnClick implements DialogInterface.OnClickListener {
        private int index;

        public RadioOnClick(int index) {
            this.index = index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int getIndex() {
            return index;
        }

        public void onClick(DialogInterface dialog, int i) {
            if (tvCollectorSourceType.getText().toString().equals("普通音源")) {

                index = 0;
            } else if (tvCollectorSourceType.getText().toString().equals("话筒音源")) {

                index = 1;
            }
            setIndex(i);
            EditCollectorSoundSourceType.sendCMD(deviceIp, index);
            tvCollectorSourceType.setText(collectorSourceType[index]);
            ssCollectorSourceType = index + "";
            dialog.dismiss();

        }
    }

    private void saveTerminalInfo() {
        if (etDeviceName.getText().toString().trim().length() < 2 || CheckEmojiUtils.containsEmoji(etDeviceName.getText().toString().trim())) {
            ToastUtil.show(this, "请输入正确的终端名称");
        } else if (deviceStatus.equals("离线")||deviceStatus.equals("未知")) {
            ToastUtil.show(DeviceDetailActivity.this, "非在线设备无法保存配置");
        } else {
            TerminalDetailInfo detailInfo = new TerminalDetailInfo();
            //mac
            String terminalMac = tvTerminalMac.getText().toString();
            detailInfo.setTerminalMac(terminalMac);
            //设备名称
            String terminalName = etDeviceName.getText().toString().trim();
            detailInfo.setTerminalName(terminalName);
            //IP获取方式
            detailInfo.setTerminalIpMode(terminalIPMode);
            //设备IP
            String deviceEditedIp = tvTerminalIP.getText().toString();
            detailInfo.setTerminalIp(deviceEditedIp);
            //设备子网掩码
            String deviceMask = hintMask.getText().toString();
            if (StringUtil.isEmpty(deviceMask)) {
                detailInfo.setTerminalSubnet("255.255.255.0");
            } else {
                detailInfo.setTerminalSubnet(deviceMask);
            }
            //设备网关
            String deviceGataway = hintGateWay.getText().toString();
            if (StringUtil.isEmpty(deviceGataway)) {
                detailInfo.setTerminalGateway("172.16.13.254");
            } else {
                detailInfo.setTerminalGateway(deviceGataway);
            }

            //设备音源类型
            String termianlSoundCate = tvTerminalSoundCate.getText().toString();
            detailInfo.setTerminalSoundCate(termianlSoundCate);
            //设备默音音量
            String ssTerVol = tvTerminalPriority.getText().toString().replaceAll("%", "");
            if (ssTerVol.equals("关闭")) {
                detailInfo.setTerminalDefVolume(00);
            } else {
                int terminalDefVolume = Integer.parseInt(ssTerVol);
                detailInfo.setTerminalDefVolume(terminalDefVolume);
            }
            //设备优先级
            if (terminalPriority == null) {
                detailInfo.setTerminalPriority("00");
            } else {
                detailInfo.setTerminalPriority(terminalPriority);
            }
            //设备音量
            detailInfo.setTerminalSetVolume(terminalSetVolume);
            //系统旧密码
            detailInfo.setTerminalOldPsw("123456");
            //系统新密码
            detailInfo.setTerminalNewPsw("123456");
            //host为指定终端IP地址
            EditTerminalMsg.sendCMD(deviceIp, detailInfo, false);
        }
    }

    private void saveTerminalInfo2() {
        if (etDeviceName.getText().toString().trim().length() < 2 || CheckEmojiUtils.containsEmoji(etDeviceName.getText().toString().trim())) {
            ToastUtil.show(this, "请输入正确的终端名称");
        }else if (deviceStatus.equals("离线")||deviceStatus.equals("未知")) {
            ToastUtil.show(DeviceDetailActivity.this, "非在线设备无法保存配置");
        } else {
            TerminalDetailInfo detailInfo = new TerminalDetailInfo();
            //mac
            String terminalMac = tvTerminalMac.getText().toString();
            detailInfo.setTerminalMac(terminalMac);
            //设备名称
            String terminalName = etDeviceName.getText().toString().trim();
            detailInfo.setTerminalName(terminalName);
            //IP获取方式
            detailInfo.setTerminalIpMode(terminalIPMode);
            //设备IP
            String deviceEditedIp = tvTerminalIP.getText().toString();
            detailInfo.setTerminalIp(deviceEditedIp);
            //设备子网掩码
            String deviceMask = hintMask.getText().toString();
            if (StringUtil.isEmpty(deviceMask)) {
                detailInfo.setTerminalSubnet("255.255.255.0");
            } else {
                detailInfo.setTerminalSubnet(deviceMask);
            }
            //设备网关
            String deviceGataway = hintGateWay.getText().toString();
            if (StringUtil.isEmpty(deviceGataway)) {
                detailInfo.setTerminalGateway("172.16.13.254");
            } else {
                detailInfo.setTerminalGateway(deviceGataway);
            }

            //设备音源类型
            String termianlSoundCate = tvTerminalSoundCate.getText().toString();
            detailInfo.setTerminalSoundCate(termianlSoundCate);
            //设备默音音量
            String ssTerVol = tvTerminalPriority.getText().toString().replaceAll("%", "");
            if (ssTerVol.equals("关闭")) {
                detailInfo.setTerminalDefVolume(00);
            } else {
                int terminalDefVolume = Integer.parseInt(ssTerVol);
                detailInfo.setTerminalDefVolume(terminalDefVolume);
            }
            //设备优先级
            if (terminalPriority == null) {
                detailInfo.setTerminalPriority("00");
            } else {
                detailInfo.setTerminalPriority(terminalPriority);
            }
            //设备音量
            detailInfo.setTerminalSetVolume(terminalSetVolume);
            //系统旧密码
            detailInfo.setTerminalOldPsw("123456");
            //系统新密码
            detailInfo.setTerminalNewPsw("123456");
            //host为指定终端IP地址
            EditTerminalMsg.sendCMD(deviceIp, detailInfo, false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode != RESULT_OK) {
//            return;
//        }
        switch (requestCode) {
            case 1:
                String selectedSourceType = data.getStringExtra("SelectedSourceType");
                tvTerminalSoundCate.setText(selectedSourceType);
                break;
            case 2:
                String selectedDefaultVol = data.getStringExtra("SelectedDafaultVol");
                terminalPriority = data.getStringExtra("DevicePriority");
                tvTerminalPriority.setText(selectedDefaultVol);
                break;
            case 3:
                String terminalIp = data.getStringExtra("Ip");
                terminalGateway = data.getStringExtra("Gateway");
                terminalSubnet = data.getStringExtra("Mask");
                tvTerminalIP.setText(terminalIp);
                String ssIPmode = data.getStringExtra("IPmode");
                terminalIPMode = Integer.parseInt(ssIPmode);
                break;
            case 4:
                String refreshAble = data.getStringExtra("Result");
                if (refreshAble.equals("1")) {
                }
                if (refreshAble.equals("2")) {
                    terminalSetVolume = Integer.parseInt(data.getStringExtra("DeviceVol"));
                    refreshData();
                }
                break;
            case 6:
                deviceList.clear();
                String deviceListStr = data.getStringExtra("deviceList");
                if (!deviceListStr.equals("")) {
                    Log.i("result", "onActivityResult: " + deviceListStr);
                    deviceList.addAll(JSONArray.parseArray(deviceListStr, FoundDeviceInfo.class));
                    String str = "";
                    checkedMac = "";
                    List<ControlPanelInfo.DeviceMsgInner> deviceMsgInnerList = new ArrayList<>();
                    ControlPanelInfo.DeviceMsgInner deviceMsgInners = new ControlPanelInfo.DeviceMsgInner();
                    for (FoundDeviceInfo device : deviceList) {
                        str += device.getDeviceName() + "\n";
                        checkedMac += device.getDeviceMac() + ",";
                        deviceMsgInners.setBindDeviceMac(device.getDeviceMac());
                        deviceMsgInners.setBindDeviceIp(device.getDeviceIp());
                    }
                    deviceMsgInnerList.add(deviceMsgInners);

                    ControlPanelInfo controlPanelInfo = new ControlPanelInfo();
                    controlPanelInfo.setBingDeviceCount(deviceList.size());
                    controlPanelInfo.setDeviceMsgList(deviceMsgInnerList);
                    EditControlPanel.sendCMD(deviceIp,controlPanelInfo);

                    tvSelectDevice.setText(str);
                    llBindedTerminal.setVisibility(View.VISIBLE);
                }
                break;
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (checkPoint == 1) {
                if (!AppDataCache.getInstance().getString("userType").equals("00")) {
                    finish();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    View v = View.inflate(this, R.layout.dialog_tips, null);
                    final TextView tvMsg = (TextView) v.findViewById(R.id.tv_msg);
                    final Button btnOk = (Button) v.findViewById(R.id.btn_ok);
                    final Button btnNo = (Button) v.findViewById(R.id.btn_no);
                    final AlertDialog dialog = builder.create();
                    dialog.show();
                    dialog.getWindow().setContentView(v);
                    tvMsg.setText("信息未保存，确定退出吗？");
                    btnNo.setVisibility(View.VISIBLE);
                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                    btnNo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                }
            } else {
                finish();
            }

            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}