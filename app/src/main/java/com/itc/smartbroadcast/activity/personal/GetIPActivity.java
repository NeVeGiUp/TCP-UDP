package com.itc.smartbroadcast.activity.personal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.util.ToastUtil;
import com.jaeger.library.StatusBarUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GetIPActivity extends AppCompatActivity {

    @BindView(R.id.et_ip)
    EditText etIp;
    @BindView(R.id.et_mask)
    EditText etMask;
    @BindView(R.id.et_gateway)
    EditText etGateway;
    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R.id.bt_save)
    Button btSave;
    @BindView(R.id.rb_dynamic)
    RadioButton rbDynamic;
    @BindView(R.id.rb_static)
    RadioButton rbStatic;
    @BindView(R.id.rg_ipmode)
    RadioGroup rgIpmode;
    @BindView(R.id.ll_static_ip)
    LinearLayout llStaticIp;

    private String firstIp, fristGateway, fristMask,fristMode;
    private String ipMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getip);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(GetIPActivity.this, getResources().getColor(R.color.colorMain), 0);
        init();
    }

    private void init() {
        rlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.putExtra("Ip", firstIp);
                intent1.putExtra("Gateway", fristGateway);
                intent1.putExtra("Mask", fristMask);
                intent1.putExtra("IpMode",fristMode);
                GetIPActivity.this.setResult(1, intent1);
                GetIPActivity.this.finish();
            }
        });
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editIp();
            }
        });
        Intent intent = getIntent();
        String ip = intent.getStringExtra("editedIp");
        String mask = intent.getStringExtra("editedMask");
        String gateway = intent.getStringExtra("editedGateway");
        firstIp = intent.getStringExtra("editedIp");
        fristMask = intent.getStringExtra("editedMask");
        fristGateway = intent.getStringExtra("editedGateway");
        fristMode = intent.getStringExtra("ipMode");
        etIp.setText(ip);
        etMask.setText(mask);
        etGateway.setText(gateway);
        ipMode = intent.getStringExtra("ipMode");

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
        switch (ipMode){
            case "00":
                rbStatic.setChecked(true);
                etIp.setTextColor(getResources().getColor(R.color.colorBlack));
                etGateway.setTextColor(getResources().getColor(R.color.colorBlack));
                etMask.setTextColor(getResources().getColor(R.color.colorBlack));
                break;
            case "01":
                rbDynamic.setChecked(true);
                etIp.setFocusable(false);
                etGateway.setFocusable(false);
                etMask.setFocusable(false);
                etIp.setTextColor(getResources().getColor(R.color.colorGray));
                etGateway.setTextColor(getResources().getColor(R.color.colorGray));
                etMask.setTextColor(getResources().getColor(R.color.colorGray));
                break;
        }
    }


    private void editIp() {
        String editedIp = etIp.getText().toString();
        String editedMask = etMask.getText().toString();
        String editedGateway = etGateway.getText().toString();

        String regex = "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(editedIp);
        Matcher matcher2 = pattern.matcher(editedMask);
        Matcher matcher3 = pattern.matcher(editedGateway);

        boolean b = matcher.matches();
        boolean b2 = matcher2.matches();
        boolean b3 = matcher3.matches();

        if (!b) {
            ToastUtil.show(GetIPActivity.this, "请输入正确的IP地址");
        } else if (!b2) {
            ToastUtil.show(GetIPActivity.this, "请输入正确的网关");
        } else if (!b3) {
            ToastUtil.show(GetIPActivity.this, "请输入正确的子网掩码");
        } else if (editedIp.equals(editedGateway)) {
            ToastUtil.show(GetIPActivity.this, "请输入正确的网关或IP地址");
        } else {
            Intent intent1 = new Intent();
            intent1.putExtra("Ip", editedIp);
            intent1.putExtra("Gateway", editedGateway);
            intent1.putExtra("Mask", editedMask);
            if (rbDynamic.isChecked()){
                intent1.putExtra("IPmode","01");
            }else if (rbStatic.isChecked()){
                intent1.putExtra("IPmode","00");
            }
            intent1.putExtra("IpMode",ipMode);
            GetIPActivity.this.setResult(1, intent1);
            GetIPActivity.this.finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent1 = new Intent();
        intent1.putExtra("Ip", firstIp);
        intent1.putExtra("Gateway", fristGateway);
        intent1.putExtra("Mask", fristMask);
        intent1.putExtra("IpMode",fristMode);
        GetIPActivity.this.setResult(1, intent1);
        GetIPActivity.this.finish();
        super.onBackPressed();
    }
}
