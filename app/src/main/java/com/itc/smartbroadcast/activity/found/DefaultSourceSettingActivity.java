package com.itc.smartbroadcast.activity.found;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.util.StringUtil;
import com.itc.smartbroadcast.util.ToastUtil;
import com.jaeger.library.StatusBarUtil;
import com.kyleduo.switchbutton.SwitchButton;

import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * create by youmu on 2018/7
 */
public class DefaultSourceSettingActivity extends AppCompatActivity {
    @BindView(R.id.bt_back_event)
    ImageView btBackEvent;
    @BindView(R.id.source_switchButton)
    SwitchButton sourceSwitchButton;
    @BindView(R.id.rb_percent_0)
    RadioButton rbPercent0;
    @BindView(R.id.rb_percent_10)
    RadioButton rbPercent10;
    @BindView(R.id.rb_percent_20)
    RadioButton rbPercent20;
    @BindView(R.id.rb_percent_30)
    RadioButton rbPercent30;
    @BindView(R.id.rb_percent_40)
    RadioButton rbPercent40;
    @BindView(R.id.rb_percent_50)
    RadioButton rbPercent50;
    @BindView(R.id.rb_percent_60)
    RadioButton rbPercent60;
    @BindView(R.id.rb_percent_70)
    RadioButton rbPercent70;
    @BindView(R.id.rb_percent_80)
    RadioButton rbPercent80;
    @BindView(R.id.rb_percent_90)
    RadioButton rbPercent90;
    @BindView(R.id.rb_percent_100)
    RadioButton rbPercent100;
    @BindView(R.id.rg_default_source)
    RadioGroup rgDefaultSource;
    @BindView(R.id.rl_visible_source_setting)
    RelativeLayout rlVisibleSourceSetting;
    @BindView(R.id.bt_save_default_vol)
    Button btSaveDefaultVol;
    private static String devicedefVol;
    private static String fristPriority;
    private String devicePriority;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_source_setting);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(DefaultSourceSettingActivity.this, getResources().getColor(R.color.colorMain),0);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        if (intent != null) {
            devicePriority = intent.getStringExtra("DevicePriority");
            fristPriority = intent.getStringExtra("DevicePriority");
            devicedefVol = intent.getStringExtra("DevicedefVol");
        }else {
            devicePriority = "00";
            devicedefVol = "关闭";
        }
        if (devicePriority.equals("80")){
            sourceSwitchButton.setChecked(true);
            rlVisibleSourceSetting.setVisibility(View.VISIBLE);
        }
        switch (devicedefVol) {
            case "关闭":
                sourceSwitchButton.setChecked(false);
                rlVisibleSourceSetting.setVisibility(View.GONE);
                break;
            case "0":
                rbPercent0.setChecked(true);
                break;
            case "10":
                rbPercent10.setChecked(true);
                break;
            case "20":
                rbPercent20.setChecked(true);
                break;
            case "30":
                rbPercent30.setChecked(true);
                break;
            case "40":
                rbPercent40.setChecked(true);
                break;
            case "50":
                rbPercent50.setChecked(true);
                break;
            case "60":
                rbPercent60.setChecked(true);
                break;
            case "70":
                rbPercent70.setChecked(true);
                break;
            case "80":
                rbPercent80.setChecked(true);
                break;
            case "90":
                rbPercent90.setChecked(true);
                break;
            case "100":
                rbPercent100.setChecked(true);
                break;
        }
        btBackEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                if (devicePriority.equals("关闭")){
                    intent1.putExtra("SelectedDafaultVol", devicedefVol);
                }else {
                    intent1.putExtra("SelectedDafaultVol", devicedefVol+"%");
                }
                intent1.putExtra("DevicePriority",fristPriority);
                DefaultSourceSettingActivity.this.setResult(2, intent1);
                DefaultSourceSettingActivity.this.finish();
            }
        });
        sourceSwitchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    devicePriority = "80";
                    rlVisibleSourceSetting.setVisibility(View.VISIBLE);
                }else {
                    devicePriority = "00";
                    rlVisibleSourceSetting.setVisibility(View.GONE);
                }
            }
        });
//        rgDefaultSource.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                RadioButton rbDefaultVol = (RadioButton) findViewById(checkedId);
//                selectVol = rbDefaultVol.getText()+"";
//            }
//        });
        btSaveDefaultVol.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (devicePriority.equals("00")){
                    Intent intent2 = new Intent();
                    intent2.putExtra("SelectedDafaultVol", "关闭");
                    intent2.putExtra("DevicePriority",devicePriority);
                    DefaultSourceSettingActivity.this.setResult(2, intent2);
                    DefaultSourceSettingActivity.this.finish();
                }else if (rgDefaultSource.getCheckedRadioButtonId() == -1){
                    ToastUtil.show(DefaultSourceSettingActivity.this,"请选择默音等级!");
                }else if (devicePriority.equals("80")){
                    Intent intent2 = new Intent();
                    if (rbPercent0.isChecked()){
                        intent2.putExtra("SelectedDafaultVol", "0%");
                    }
                    if (rbPercent10.isChecked()){
                        intent2.putExtra("SelectedDafaultVol", "10%");
                    }
                    if (rbPercent20.isChecked()){
                        intent2.putExtra("SelectedDafaultVol", "20%");
                    }
                    if (rbPercent30.isChecked()){
                        intent2.putExtra("SelectedDafaultVol", "30%");
                    }
                    if (rbPercent40.isChecked()){
                        intent2.putExtra("SelectedDafaultVol", "40%");
                    }
                    if (rbPercent50.isChecked()){
                        intent2.putExtra("SelectedDafaultVol", "50%");
                    }
                    if (rbPercent60.isChecked()){
                        intent2.putExtra("SelectedDafaultVol", "60%");
                    }
                    if (rbPercent70.isChecked()){
                        intent2.putExtra("SelectedDafaultVol", "70%");
                    }
                    if (rbPercent80.isChecked()){
                        intent2.putExtra("SelectedDafaultVol", "80%");
                    }
                    if (rbPercent90.isChecked()){
                        intent2.putExtra("SelectedDafaultVol", "90%");
                    }
                    if (rbPercent100.isChecked()){
                        intent2.putExtra("SelectedDafaultVol", "100%");
                    }
                    intent2.putExtra("DevicePriority",devicePriority);
                    DefaultSourceSettingActivity.this.setResult(2, intent2);
                    DefaultSourceSettingActivity.this.finish();
                }
            }
        });
    }

    //禁止返回键
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
            Intent intent1 = new Intent();
            if (devicePriority.equals("关闭")){
                intent1.putExtra("SelectedDafaultVol", devicedefVol);
            }else {
                intent1.putExtra("SelectedDafaultVol", devicedefVol+"%");
            }
            intent1.putExtra("DevicePriority",fristPriority);
            DefaultSourceSettingActivity.this.setResult(2, intent1);
            DefaultSourceSettingActivity.this.finish();
            return true;
        }else {
            return super.dispatchKeyEvent(event);
        }
    }
}
