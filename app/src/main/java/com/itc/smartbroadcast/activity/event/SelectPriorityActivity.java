package com.itc.smartbroadcast.activity.event;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.base.Base2Activity;
import com.jaeger.library.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectPriorityActivity extends Base2Activity {
    @BindView(R.id.bt_back_event)
    ImageView btBackEvent;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.tv_selected)
    TextView tvSelected;
    @BindView(R.id.rg_playmode)
    RadioGroup rgPlaymode;
    @BindView(R.id.bt_ordinary)
    RadioButton btOrdinary;
    @BindView(R.id.bt_senior)
    RadioButton btSenior;
    @BindView(R.id.bt_important)
    RadioButton btImportant;
    @BindView(R.id.bt_urgent)
    RadioButton btUrgent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_priority);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);
        String priority = getIntent().getStringExtra("priority");

        switch (priority) {
            case "普通":
                btOrdinary.setChecked(true);
                break;
            case "高级":
                btSenior.setChecked(true);
                break;
            case "重要":
                btImportant.setChecked(true);
                break;
            case "紧急":
                btUrgent.setChecked(true);
                break;
            default:
                btSenior.setChecked(true);
                break;
        }


        tvSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (rgPlaymode.getCheckedRadioButtonId()) {
                    case R.id.bt_ordinary:
                        resultPost("普通");
                        break;
                    case R.id.bt_senior:
                        resultPost("高级");
                        break;
                    case R.id.bt_important:
                        resultPost("重要");
                        break;
                    case R.id.bt_urgent:
                        resultPost("紧急");
                        break;
                }
            }
        });

        btBackEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = "";
                resultPost(result);
            }
        });

    }

    public void resultPost(String priority) {
        //数据是使用Intent返回
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("priority", priority);
        //设置返回数据
        this.setResult(RESULT_OK, intent);
        //关闭Activity
        this.finish();
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
            String result = "";
            resultPost(result);
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

}
