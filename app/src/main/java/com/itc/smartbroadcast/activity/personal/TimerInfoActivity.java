package com.itc.smartbroadcast.activity.personal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditTerminalResult;
import com.itc.smartbroadcast.bean.TerminalDetailInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.EditTerminalMsg;
import com.itc.smartbroadcast.listener.LimitInputTextWatcher;
import com.itc.smartbroadcast.util.StringUtil;
import com.itc.smartbroadcast.util.ToastUtil;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * create by youmu on 2018/7
 */

public class TimerInfoActivity extends AppCompatActivity {
    @BindView(R.id.et_timer_name)
    EditText etTimerName;
    @BindView(R.id.rl_get_IP)
    RelativeLayout rlGetIP;
    @BindView(R.id.tv_timer_mac)
    TextView tvTimerMac;
    @BindView(R.id.tv_timer_version)
    TextView tvTimerVersion;
    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R.id.bt_save)
    Button btSave;
    @BindView(R.id.tv_timer_ip)
    TextView tvTimerIp;

    private String editedIp;
    private String editedMask;
    private String editedGateway;
    private String editedIpMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timerinfo);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);
        init();
    }

    private void init() {
        editedIp = AppDataCache.getInstance().getString("loginIp");
        editedMask = AppDataCache.getInstance().getString("timerMask");
        editedGateway = AppDataCache.getInstance().getString("timerGateway");
        editedIpMode = AppDataCache.getInstance().getString("ipMode");
        etTimerName.addTextChangedListener(new LimitInputTextWatcher(etTimerName));
        etTimerName.setText(AppDataCache.getInstance().getString("timerName"));
        tvTimerMac.setText(AppDataCache.getInstance().getString("timerMac"));
        tvTimerVersion.setText(AppDataCache.getInstance().getString("timerVersion"));
        tvTimerIp.setText(editedIp);
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        rlGetIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("editedIp", editedIp);
                intent.putExtra("editedMask", editedMask);
                intent.putExtra("editedGateway", editedGateway);
                intent.putExtra("ipMode", editedIpMode);
                intent.setClass(TimerInfoActivity.this, GetIPActivity.class);
                TimerInfoActivity.this.startActivityForResult(intent, 1);
            }
        });
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTimerConfig();
            }
        });
    }

    private void saveTimerConfig() {
        if (StringUtil.isEmpty(etTimerName.getText().toString().trim()) || etTimerName.getText().toString().length() < 2) {
            ToastUtil.show(this, "请输入正确的定时器名称");
        } else {
            TerminalDetailInfo detailInfo = new TerminalDetailInfo();
            //mac
            detailInfo.setTerminalMac(AppDataCache.getInstance().getString("timerMac"));
            //设备名称
            String timerName = etTimerName.getText().toString();
            detailInfo.setTerminalName(timerName);
            //IP获取方式 0为静态获取，1为动态获取
            detailInfo.setTerminalIpMode(Integer.parseInt(editedIpMode));
            //设备IP
            detailInfo.setTerminalIp(editedIp);
            //设备子网掩码
            detailInfo.setTerminalSubnet(editedMask);
            //设备网关
            detailInfo.setTerminalGateway(editedGateway);
            //设备音源类型
            detailInfo.setTerminalSoundCate("10");
            //设备优先级
            detailInfo.setTerminalPriority("00");
            //设备默音音量
            detailInfo.setTerminalDefVolume(20);
            //设备默认音量
            detailInfo.setTerminalSetVolume(100);
            //系统旧密码
            detailInfo.setTerminalOldPsw("123456");
            //系统新密码
            detailInfo.setTerminalNewPsw("123456");
            //host为指定终端IP地址
            EditTerminalMsg.sendCMD(AppDataCache.getInstance().getString("loginIp"), detailInfo, false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        final Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);
        if ("editTerminalResult".equals(baseBean.getType())) {
            EditTerminalResult editTerminalResult = gson.fromJson(baseBean.getData(), EditTerminalResult.class);
            int[] resultCode = editTerminalResult.getResult();
            if (resultCode[0] == 1 && resultCode[1] == 1) {
                ToastUtil.show(TimerInfoActivity.this, "修改成功");
                AppDataCache.getInstance().putString("timerMask", editedMask);//定时器子网掩码
                AppDataCache.getInstance().putString("timerGateway", editedGateway);//定时器网关
                AppDataCache.getInstance().putString("loginIp", editedIp);//定时器地址
                AppDataCache.getInstance().putString("timerName", etTimerName.getText().toString());//定时器名称
                finish();
            } else {
                ToastUtil.show(TimerInfoActivity.this, "修改失败！请检查网络或数据");
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (data == null) {
                    break;
                } else {
                    editedIp = data.getStringExtra("Ip");
                    editedMask = data.getStringExtra("Mask");
                    editedGateway = data.getStringExtra("Gateway");
                    editedIpMode = data.getStringExtra("IpMode");
                    tvTimerIp.setText(editedIp);
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}