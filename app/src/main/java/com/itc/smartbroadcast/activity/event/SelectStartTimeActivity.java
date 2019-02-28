package com.itc.smartbroadcast.activity.event;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.base.Base2Activity;
import com.jaeger.library.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectStartTimeActivity extends Base2Activity {

    @BindView(R.id.bt_back)
    ImageView btBack;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.et_time)
    EditText etTime;
    @BindView(R.id.rb_advance)
    RadioButton rbAdvance;
    @BindView(R.id.rb_postpone)
    RadioButton rbPostpone;
    @BindView(R.id.rg_source_type)
    RadioGroup rgSourceType;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_start_time);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);
        context = this;
        initView();

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = "0";
                resultPost(result);
            }
        });

    }

    /**
     * 监听Back键按下事件,方法2:
     * 注意:
     * 返回值表示:是否能完全处理该事件
     * 在此处返回false,所以会继续传播该事件.
     * 在具体项目中此处的返回值视情况而定.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            String result = "0";
            resultPost(result);
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void resultPost(String startTime) {
        //数据是使用Intent返回
        Intent intent = new Intent();

        int start = 0;
        if (rbAdvance.isChecked()) {
            start = 0 - (Integer.parseInt(startTime));
        }else{
            start = 0 + (Integer.parseInt(startTime));
        }

        //把返回数据存入Intent
        intent.putExtra("startTimeDis", start);
        //设置返回数据
        this.setResult(RESULT_OK, intent);
        //关闭Activity
        this.finish();
    }

    private void initView() {

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectStartTimeActivity.this.finish();
            }
        });

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultPost(etTime.getText().toString().trim());
            }
        });

    }
}
