package com.itc.smartbroadcast.activity.personal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.bean.ActivationRegisterCodeResult;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.SystemRegisterInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.SystemRegister;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;
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
public class AuthorizationActivity extends AppCompatActivity {
    @BindView(R.id.bt_back_personal)
    ImageView btBackPersonal;
    @BindView(R.id.tv_timer_reg_status)
    TextView tvTimerRegStatus;
    @BindView(R.id.tv_timer_mec_code)
    TextView tvTimerMecCode;
    @BindView(R.id.et_reg_code)
    EditText etRegCode;
    @BindView(R.id.tv_reg_date)
    TextView tvRegDate;
    @BindView(R.id.tv_save_reg)
    TextView tvSaveReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);
        init();
    }

    @Override
    public void onResume() {
        EventBus.getDefault().register(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);
        String baseData = baseBean.getData();
        Log.i("激活情况>>>", baseData);
        if ("ActivationRegisterCodeResult".equals(baseBean.getType())) {
            ActivationRegisterCodeResult activationRegisterCodeResult = gson.fromJson(baseData, ActivationRegisterCodeResult.class);
            switch (activationRegisterCodeResult.getResult()) {
                case 0:
                    ToastUtil.show(this, "系统激活成功");
                    break;
                case 1:
                    ToastUtil.show(this, "注册码无效");
                    break;
                case 2:
                    ToastUtil.show(this, "注册码已被使用");
                    break;
            }
        }
    }

    private void init() {
        btBackPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String regStatus = AppDataCache.getInstance().getString("timerRegStatus");
        switch (regStatus) {
            case "00":
                tvTimerRegStatus.setText("未注册");
                break;
            case "01":
                tvTimerRegStatus.setText("已激活");
                break;
            case "02":
                tvTimerRegStatus.setText("已激活");
                break;
        }
        if (regStatus.equals("01") || regStatus.equals("02")) {
//            etRegCode.setFocusable(false);
        }
        tvTimerMecCode.setText(AppDataCache.getInstance().getString("timerMecCode"));
        tvSaveReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String regCodeStr = etRegCode.getText().toString().trim();
                boolean hex = SmartBroadCastUtils.isHex(regCodeStr);
                if (!hex || regCodeStr.length()!=20)
                    ToastUtil.show(AuthorizationActivity.this,"请输入格式正确的注册码");
                else
                    SystemRegister.sendCMD(AppDataCache.getInstance().getString("loginIp"), regCodeStr);

            }
        });
    }
}


