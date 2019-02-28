package com.itc.smartbroadcast.activity.event;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class PlayModeActivity extends Base2Activity {
    @BindView(R.id.bt_back_event)
    ImageView btBackEvent;
    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.tv_selected)
    TextView tvSelected;
    @BindView(R.id.bt_loopplay)
    RadioButton btLoopplay;
    @BindView(R.id.bt_randomplay)
    RadioButton btRandomplay;
    @BindView(R.id.bt_orderplay)
    RadioButton btOrderplay;
    @BindView(R.id.rg_playmode)
    RadioGroup rgPlaymode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playmode);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain),0);


        String playMode = getIntent().getStringExtra("playMode");

        switch (playMode) {
            case "顺序播放":
                btOrderplay.setChecked(true);
                break;
            case "循环播放":
                btLoopplay.setChecked(true);
                break;
            case "随机播放":
                btRandomplay.setChecked(true);
                break;
            default:
                btLoopplay.setChecked(true);
                break;
        }


        tvSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (rgPlaymode.getCheckedRadioButtonId()) {
                    case R.id.bt_loopplay:
                        resultPost("循环播放");
                        break;
                    case R.id.bt_randomplay:
                        resultPost("随机播放");
                        break;
                    case R.id.bt_orderplay:
                        resultPost("顺序播放");
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

    public void resultPost(String playMode) {
        //数据是使用Intent返回
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("playMode", playMode);
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
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }

}
