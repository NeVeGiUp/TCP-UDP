package com.itc.smartbroadcast.activity.event;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.base.Base2Activity;
import com.jaeger.library.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectWeekActivity extends Base2Activity {

    @BindView(R.id.bt_back)
    ImageView btBack;
    @BindView(R.id.tv_save)
    TextView tvSave;
    @BindView(R.id.cb_one)
    CheckBox cbOne;
    @BindView(R.id.cb_two)
    CheckBox cbTwo;
    @BindView(R.id.cb_three)
    CheckBox cbThree;
    @BindView(R.id.cb_four)
    CheckBox cbFour;
    @BindView(R.id.cb_five)
    CheckBox cbFive;
    @BindView(R.id.cb_six)
    CheckBox cbSix;
    @BindView(R.id.cb_seven)
    CheckBox cbSeven;
    @BindView(R.id.bt_select_all)
    TextView btSelectAll;
    @BindView(R.id.bt_select_reverse)
    TextView btSelectReverse;

    private Context context;

    private int[] WeekDuplicationPatternList = new int[8];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_week);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);

        context = this;

        String weekStr = getIntent().getStringExtra("week");
        String[] weeks = weekStr.split("-");
        int index = 0;
        for (String str : weeks) {
            WeekDuplicationPatternList[index] = Integer.parseInt(str);
            index++;
        }
        int i = 0;
        for (int we : WeekDuplicationPatternList) {
            if (we == 1) {
                switch (i) {
                    case 0:
                        cbOne.setChecked(true);
                        break;
                    case 1:
                        cbTwo.setChecked(true);
                        break;
                    case 2:
                        cbThree.setChecked(true);
                        break;
                    case 3:
                        cbFour.setChecked(true);
                        break;
                    case 4:
                        cbFive.setChecked(true);
                        break;
                    case 5:
                        cbSix.setChecked(true);
                        break;
                    case 6:
                        cbSeven.setChecked(true);
                        break;
                }
            }
            i++;
        }

        btSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btSelectAll.setVisibility(View.GONE);
                btSelectReverse.setVisibility(View.VISIBLE);
                cbOne.setChecked(true);
                cbTwo.setChecked(true);
                cbThree.setChecked(true);
                cbFour.setChecked(true);
                cbFive.setChecked(true);
                cbSix.setChecked(true);
                cbSeven.setChecked(true);
            }
        });
        btSelectReverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btSelectAll.setVisibility(View.VISIBLE);
                btSelectReverse.setVisibility(View.GONE);
                if (cbOne.isChecked()){
                    cbOne.setChecked(false);
                }else{
                    cbOne.setChecked(true);
                }
                if (cbTwo.isChecked()){
                    cbTwo.setChecked(false);
                }else{
                    cbTwo.setChecked(true);
                }
                if (cbThree.isChecked()){
                    cbThree.setChecked(false);
                }else{
                    cbThree.setChecked(true);
                }
                if (cbFour.isChecked()){
                    cbFour.setChecked(false);
                }else{
                    cbFour.setChecked(true);
                }
                if (cbFive.isChecked()){
                    cbFive.setChecked(false);
                }else{
                    cbFive.setChecked(true);
                }
                if (cbSix.isChecked()){
                    cbSix.setChecked(false);
                }else{
                    cbSix.setChecked(true);
                }
                if (cbSeven.isChecked()){
                    cbSeven.setChecked(false);
                }else{
                    cbSeven.setChecked(true);
                }
            }
        });




        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String result = "";
                if (cbOne.isChecked()) {
                    result += "1-";
                } else {
                    result += "0-";
                }
                if (cbTwo.isChecked()) {
                    result += "1-";
                } else {
                    result += "0-";
                }
                if (cbThree.isChecked()) {
                    result += "1-";
                } else {
                    result += "0-";
                }
                if (cbFour.isChecked()) {
                    result += "1-";
                } else {
                    result += "0-";
                }
                if (cbFive.isChecked()) {
                    result += "1-";
                } else {
                    result += "0-";
                }
                if (cbSix.isChecked()) {
                    result += "1-";
                } else {
                    result += "0-";
                }
                if (cbSeven.isChecked()) {
                    result += "1";
                } else {
                    result += "0";
                }
                resultPost(result);
            }
        });
    }

    @OnClick({R.id.bt_back, R.id.tv_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_back:
                String result = "";
                resultPost(result);
                break;
            case R.id.tv_save:
                break;
        }
    }

    public void resultPost(String week) {
        //数据是使用Intent返回
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("week", week);
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
