package com.itc.smartbroadcast.activity.found;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.util.ToastUtil;
import com.jaeger.library.StatusBarUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * create by youmu on 2018/9
 */
public class TerminalGetIPActivity extends AppCompatActivity{


    @BindView(R.id.bt_back)
    ImageView btBack;
    @BindView(R.id.bt_save_ip_mode)
    Button btSaveIpMode;
    @BindView(R.id.rb_dynamic)
    RadioButton rbDynamic;
    @BindView(R.id.rb_static)
    RadioButton rbStatic;
    @BindView(R.id.rg_ipmode)
    RadioGroup rgIpmode;
    @BindView(R.id.et_ip)
    EditText etIp;
    @BindView(R.id.et_mask)
    EditText etMask;
    @BindView(R.id.et_gateway)
    EditText etGateway;
    @BindView(R.id.ll_static_ip)
    LinearLayout llStaticIp;
    private static String firstIpmode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal_getip);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(TerminalGetIPActivity.this, getResources().getColor(R.color.colorMain));
        initOnclick();
        Intent intent = getIntent();
        String terminalIPMode = intent.getStringExtra("TerminalIPMode");
        firstIpmode = intent.getStringExtra("TerminalIPMode");
        String terminalIP = intent.getStringExtra("TerminalIP");
        String terminalGateway = intent.getStringExtra("TerminalGateway");
        String terminalSubnet = intent.getStringExtra("TerminalSubnet");
        etIp.setText(terminalIP);
        etGateway.setText(terminalGateway);
        etMask.setText(terminalSubnet);
        switch (terminalIPMode) {
            case "0":
                rbStatic.setChecked(true);
                etIp.setTextColor(getResources().getColor(R.color.colorBlack));
                etGateway.setTextColor(getResources().getColor(R.color.colorBlack));
                etMask.setTextColor(getResources().getColor(R.color.colorBlack));
                break;
            case "1":
                rbDynamic.setChecked(true);
                etIp.setFocusable(false);
                etGateway.setFocusable(false);
                etMask.setFocusable(false);
                etIp.setTextColor(getResources().getColor(R.color.colorGray));
                etGateway.setTextColor(getResources().getColor(R.color.colorGray));
                etMask.setTextColor(getResources().getColor(R.color.colorGray));
                break;
        }
        rgIpmode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_dynamic:
                        etIp.setFocusable(false);
                        etGateway.setFocusable(false);
                        etMask.setFocusable(false);
                        etIp.setTextColor(getResources().getColor(R.color.colorGray));
                        etGateway.setTextColor(getResources().getColor(R.color.colorGray));
                        etMask.setTextColor(getResources().getColor(R.color.colorGray));
                        break;
                    case R.id.rb_static:
                        etIp.setFocusable(true);
                        etIp.setFocusableInTouchMode(true);
                        etGateway.setFocusable(true);
                        etGateway.setFocusableInTouchMode(true);
                        etMask.setFocusable(true);
                        etMask.setFocusableInTouchMode(true);
                        etIp.setTextColor(getResources().getColor(R.color.colorBlack));
                        etGateway.setTextColor(getResources().getColor(R.color.colorBlack));
                        etMask.setTextColor(getResources().getColor(R.color.colorBlack));
                        break;
                }
            }
        });
    }

    public void initOnclick() {
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent();
                Intent intent = getIntent();
                String terminalIP = intent.getStringExtra("TerminalIP");
                String terminalGateway = intent.getStringExtra("TerminalGateway");
                String terminalSubnet = intent.getStringExtra("TerminalSubnet");
                intent1.putExtra("IPmode", firstIpmode);
                intent1.putExtra("Ip", terminalIP);
                intent1.putExtra("Gateway", terminalGateway);
                intent1.putExtra("Mask", terminalSubnet);
                TerminalGetIPActivity.this.setResult(3, intent1);
                TerminalGetIPActivity.this.finish();
            }
        });
        btSaveIpMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveIpmode();
            }
        });
    }

    private void saveIpmode() {
        String statisticsIp = etIp.getText().toString();
        String staticGateway = etGateway.getText().toString();
        String staticMask = etMask.getText().toString();
        String regex = "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(statisticsIp);
        Matcher matcher2 = pattern.matcher(staticGateway);
        Matcher matcher3 = pattern.matcher(staticMask);

        boolean b = matcher.matches();
        boolean b2 = matcher2.matches();
        boolean b3 = matcher3.matches();

        if (!b) {
            ToastUtil.show(TerminalGetIPActivity.this, "请输入正确的IP地址");
        } else if (!b2) {
            ToastUtil.show(TerminalGetIPActivity.this, "请输入正确的网关");
        } else if (!b3) {
            ToastUtil.show(TerminalGetIPActivity.this, "请输入正确的子网掩码");
        } else if (statisticsIp.equals(staticGateway)) {
            ToastUtil.show(TerminalGetIPActivity.this, "请输入正确的网关或IP地址");
        } else {
            Intent intent1 = new Intent();
            intent1.putExtra("Ip", statisticsIp);
            intent1.putExtra("Gateway", staticGateway);
            intent1.putExtra("Mask", staticMask);
            if (rbDynamic.isChecked()){
                intent1.putExtra("IPmode","1");
            }else if (rbStatic.isChecked()){
                intent1.putExtra("IPmode","0");
            }
            TerminalGetIPActivity.this.setResult(3, intent1);
            TerminalGetIPActivity.this.finish();
        }
    }

    //禁止返回键
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            Intent intent1 = new Intent();
            Intent intent = getIntent();
            String terminalIP = intent.getStringExtra("TerminalIP");
            String terminalGateway = intent.getStringExtra("TerminalGateway");
            String terminalSubnet = intent.getStringExtra("TerminalSubnet");
            intent1.putExtra("IPmode", firstIpmode);
            intent1.putExtra("Ip", terminalIP);
            intent1.putExtra("Gateway", terminalGateway);
            intent1.putExtra("Mask", terminalSubnet);
            TerminalGetIPActivity.this.setResult(3, intent1);
            TerminalGetIPActivity.this.finish();
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

}
