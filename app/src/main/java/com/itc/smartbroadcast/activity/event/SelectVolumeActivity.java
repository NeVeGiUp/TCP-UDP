package com.itc.smartbroadcast.activity.event;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.base.Base2Activity;
import com.jaeger.library.StatusBarUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectVolumeActivity extends Base2Activity {

    @BindView(R.id.bt_back)
    ImageView btBack;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.et_select_volume)
    EditText etSelectVolume;

    private Context context;

    String volume;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_volume);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);
        context = this;
        initView();

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = volume;
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
            String result = volume;
            resultPost(result);
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    public void resultPost(String volume) {

        hintKeyBoard();
        //数据是使用Intent返回
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("volume", volume);
        //设置返回数据
        this.setResult(RESULT_OK, intent);
        //关闭Activity
        this.finish();
    }

    private void initView() {


        volume = getIntent().getStringExtra("volume");
        etSelectVolume.setText(volume);



        etSelectVolume.addTextChangedListener(new TextWatcher() {
            private String outStr = ""; //这个值存储输入超过两位数时候显示的内容

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String edit = s.toString();

                if (edit.length() > 0) {
                    if (Integer.parseInt(edit) == 0) {
                        outStr = "0";
                    }
                }
                if (edit.length() == 2 && Integer.parseInt(edit) >= 10) {
                    outStr = edit;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String words = s.toString();
                //首先内容进行非空判断，空内容（""和null）不处理
                if (!TextUtils.isEmpty(words)) {
                    //1-100的正则验证
                    Pattern p = Pattern.compile("^(100|[1-9]\\d|\\d|0)$");
                    Matcher m = p.matcher(words);
                    if (m.find() || ("").equals(words)) {
                        //这个时候输入的是合法范围内的值
                    } else {
                        if (words.length() > 2 && !(Integer.parseInt(words) == 0)) {
                            //若输入不合规，且长度超过2位，继续输入只显示之前存储的outStr
                            etSelectVolume.setText(outStr);
                            //重置输入框内容后默认光标位置会回到索引0的地方，要改变光标位置
                            etSelectVolume.setSelection(2);
                        }
                        if (words.length() >= 1 && (Integer.parseInt(words.substring(0, 1)) == 0)) {
                            etSelectVolume.setText("0");
                            etSelectVolume.setSelection(1);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //这里的处理是不让输入0开头的值
                String words = s.toString();
                //首先内容进行非空判断，空内容（""和null）不处理
                if (!TextUtils.isEmpty(words)) {
                    if (Integer.parseInt(s.toString()) < 0) {
                        etSelectVolume.setText("");
                    }
                }
            }

        });

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SelectVolumeActivity.this.finish();
            }
        });

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resultPost(etSelectVolume.getText().toString().trim());
            }
        });

    }


    public void hintKeyBoard() {
        //拿到InputMethodManager
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //如果window上view获取焦点 && view不为空
        if (imm.isActive() && getCurrentFocus() != null) {
            //拿到view的token 不为空
            if (getCurrentFocus().getWindowToken() != null) {
                //表示软键盘窗口总是隐藏，除非开始时以SHOW_FORCED显示。
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
