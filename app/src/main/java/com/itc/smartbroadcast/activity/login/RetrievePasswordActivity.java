package com.itc.smartbroadcast.activity.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.CloudRegisterCodeInfo;
import com.itc.smartbroadcast.bean.CloudRegisterInfo;
import com.itc.smartbroadcast.channels.http.CloudProtocolModel;
import com.itc.smartbroadcast.util.CountDownTimerUtils;
import com.itc.smartbroadcast.util.ToastUtil;
import com.itc.smartbroadcast.widget.custom.ClearEditText;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//create by youmu on 2018/12
public class RetrievePasswordActivity extends AppCompatActivity {
    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R.id.et_phone_num)
    ClearEditText etPhoneNum;
    @BindView(R.id.et_pass)
    ClearEditText etPass;
    @BindView(R.id.et_re_pass)
    ClearEditText etRePass;
    @BindView(R.id.bt_get_sms_code)
    Button btGetSmsCode;
    @BindView(R.id.et_sms_code)
    EditText etSmsCode;
    @BindView(R.id.bt_reg)
    Button btReg;
    @BindView(R.id.cloud_reg_title)
    TextView cloudRegTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);
        cloudRegTitle.setText("找回密码");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);
        Log.i("云注册>>>",baseBean.getData());
        switch (baseBean.getType()) {
            case "cloudForgotPassCode":
                BaseBean bean = gson.fromJson(baseBean.getData(),BaseBean.class);
                if (baseBean.getData().contains("failed")){
                    ToastUtil.show(this,"验证码发送失败");
                }else {
                    BaseBean innerData = gson.fromJson(baseBean.getData(), BaseBean.class);
                    if ("success".equals(innerData.getType())) {
                        CloudRegisterCodeInfo cloudAuthorizationInfo = JSON.parseObject(innerData.getData(), CloudRegisterCodeInfo.class);
                        String ss = cloudAuthorizationInfo.getMessage();
                        ToastUtil.show(this, ss);
                    } else {
                        ToastUtil.show(this, "验证码发送失败");
                    }
                }
                break;
            case "cloudForgotPassword":
                BaseBean baseBean1 = gson.fromJson(baseBean.getData(), BaseBean.class);
                if ("success".equals(baseBean1.getType())) {
                    CloudRegisterInfo cloudRegisterInfo = JSON.parseObject(baseBean1.getData(), CloudRegisterInfo.class);
                    ToastUtil.show(this, cloudRegisterInfo.getMessage());
                    if (cloudRegisterInfo.getMessage().equals("修改成功")){
                        finish();
                    }
                } else {

                }
                break;
        }
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

    @OnClick({R.id.rl_back, R.id.bt_get_sms_code, R.id.bt_reg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.bt_get_sms_code:

                String phoneNum = etPhoneNum.getText().toString().trim();
                String PHONE_NUMBER_REG = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";
                Pattern pattern = Pattern.compile(PHONE_NUMBER_REG);
                Matcher matcher = pattern.matcher(phoneNum);
                boolean b = matcher.matches();
                if (!b) {
                    ToastUtil.show(this, "请输入正确的手机号");
                } else {
                    CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(btGetSmsCode, 60*1000, 1000); //倒计时1分钟
                    mCountDownTimerUtils.start();
                    CloudProtocolModel.postForgotPasswordCode(phoneNum, RetrievePasswordActivity.this);
                }
                break;
            case R.id.bt_reg:
                String phone = etPhoneNum.getText().toString().trim();
                String password = etPass.getText().toString().trim();
                String rePass = etRePass.getText().toString().trim();
                String identifyCode = etSmsCode.getText().toString().trim();
                if (password.length() != 6) {
                    ToastUtil.show(this, "请输入等于6位的密码");
                } else if (password.equals(rePass)) {
                    HashMap<String, String> mapData = new HashMap<>();
                    mapData.put("mobile", phone);
                    mapData.put("password", password);
                    mapData.put("confirm_password", rePass);
                    mapData.put("identify_code", identifyCode);
                    CloudProtocolModel.postForgotPassword(mapData, this);
                } else {
                    etPass.setText("");
                    etRePass.setText("");
                    ToastUtil.show(this, "两次输入的密码不一致");
                }
                break;
        }
    }
}
