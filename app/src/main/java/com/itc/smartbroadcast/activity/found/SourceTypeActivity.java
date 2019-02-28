package com.itc.smartbroadcast.activity.found;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.itc.smartbroadcast.R;
import com.jaeger.library.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * create by youmu on 2018/9
 */
public class SourceTypeActivity extends AppCompatActivity {
    @BindView(R.id.bt_back_event)
    ImageView btBackEvent;
    @BindView(R.id.rb_source_s1)
    RadioButton rbSourceS1;
    @BindView(R.id.rb_source_s2)
    RadioButton rbSourceS2;
    @BindView(R.id.rb_source_s3)
    RadioButton rbSourceS3;
    @BindView(R.id.rb_source_s4)
    RadioButton rbSourceS4;
    @BindView(R.id.rb_source_s5)
    RadioButton rbSourceS5;
    @BindView(R.id.rb_source_p1)
    RadioButton rbSourceP1;
    @BindView(R.id.rb_source_p2)
    RadioButton rbSourceP2;
    @BindView(R.id.rb_source_e1)
    RadioButton rbSourceE1;
    @BindView(R.id.rg_source_type)
    RadioGroup rgSourceType;
    @BindView(R.id.bt_save_source_type)
    Button btSaveSourceType;
    @BindView(R.id.rb_source_p3)
    RadioButton rbSourceP3;
    @BindView(R.id.rb_source_e2)
    RadioButton rbSourceE2;
    @BindView(R.id.rb_source_e3)
    RadioButton rbSourceE3;
    @BindView(R.id.tv_e1)
    TextView tvE1;
    @BindView(R.id.tv_e2)
    TextView tvE2;
    @BindView(R.id.tv_e3)
    TextView tvE3;
    private String sourceType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_source_type);
        StatusBarUtil.setColor(SourceTypeActivity.this, getResources().getColor(R.color.colorMain));
        ButterKnife.bind(this);
        rbSourceE1.setVisibility(View.INVISIBLE);
        rbSourceE2.setVisibility(View.INVISIBLE);
        rbSourceE3.setVisibility(View.INVISIBLE);
        tvE1.setVisibility(View.INVISIBLE);
        tvE2.setVisibility(View.INVISIBLE);
        tvE3.setVisibility(View.INVISIBLE);


        final Intent intent = getIntent();
        sourceType = intent.getStringExtra("SourceType");
        switch (sourceType) {
            case "S1":
                rbSourceS1.setChecked(true);
                break;
            case "S2":
                rbSourceS2.setChecked(true);
                break;
            case "S3":
                rbSourceS3.setChecked(true);
                break;
            case "S4":
                rbSourceS4.setChecked(true);
                break;
            case "S5":
                rbSourceS5.setChecked(true);
                break;
            case "P1":
                rbSourceP1.setChecked(true);
                break;
            case "P2":
                rbSourceP2.setChecked(true);
                break;
            case "P3":
                rbSourceP3.setChecked(true);
            case "E1":
                rbSourceE1.setChecked(true);
                break;
            case "E2":
                rbSourceE2.setChecked(true);
                break;
            case "E3":
                rbSourceE3.setChecked(true);
                break;
        }

        btBackEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.putExtra("SelectedSourceType", sourceType);
                SourceTypeActivity.this.setResult(1, intent1);
                SourceTypeActivity.this.finish();
            }
        });

//        rgSourceType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                RadioButton rbSourceType = (RadioButton) findViewById(checkedId);
//            }
//        });
        btSaveSourceType.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent();
                if (rbSourceS1.isChecked()) {
                    intent1.putExtra("SelectedSourceType", "S1");
                }
                if (rbSourceS2.isChecked()) {
                    intent1.putExtra("SelectedSourceType", "S2");
                }
                if (rbSourceS3.isChecked()) {
                    intent1.putExtra("SelectedSourceType", "S3");
                }
                if (rbSourceS4.isChecked()) {
                    intent1.putExtra("SelectedSourceType", "S4");
                }
                if (rbSourceS5.isChecked()) {
                    intent1.putExtra("SelectedSourceType", "S5");
                }
                if (rbSourceP1.isChecked()) {
                    intent1.putExtra("SelectedSourceType", "P1");
                }
                if (rbSourceP2.isChecked()) {
                    intent1.putExtra("SelectedSourceType", "P2");
                }
                if (rbSourceP3.isChecked()) {
                    intent1.putExtra("SelectedSourceType", "P3");
                }
                if (rbSourceE1.isChecked()) {
                    intent1.putExtra("SelectedSourceType", "E1");
                }
                if (rbSourceE2.isChecked()) {
                    intent1.putExtra("SelectedSourceType", "E2");
                }
                if (rbSourceE3.isChecked()) {
                    intent1.putExtra("SelectedSourceType", "E3");
                }
                SourceTypeActivity.this.setResult(1, intent1);
                SourceTypeActivity.this.finish();
            }
        });
    }

    //禁止返回键
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            Intent intent1 = new Intent();

            intent1.putExtra("SelectedSourceType", sourceType);
            SourceTypeActivity.this.setResult(1, intent1);
            SourceTypeActivity.this.finish();
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }

}
