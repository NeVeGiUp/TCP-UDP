package com.itc.smartbroadcast.activity.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.widget.custom.ClearEditText;
import com.jaeger.library.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordActivity extends AppCompatActivity {

    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R.id.et_phone_num)
    ClearEditText etPhoneNum;
    @BindView(R.id.et_pass)
    ClearEditText etPass;
    @BindView(R.id.et_re_pass)
    ClearEditText etRePass;
    @BindView(R.id.et_sms_code)
    EditText etSmsCode;
    @BindView(R.id.bt_get_sms_code)
    Button btGetSmsCode;
    @BindView(R.id.bt_reg)
    Button btReg;
    @BindView(R.id.cloud_reg_title)
    TextView cloudRegTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        cloudRegTitle.setText("找回密码");

        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);
    }

    @OnClick({R.id.rl_back, R.id.bt_get_sms_code, R.id.bt_reg})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.bt_get_sms_code:
                break;
            case R.id.bt_reg:
                break;
        }
    }
}
