package com.itc.smartbroadcast.activity.personal;

import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.helper.CustomDialog;
import com.jaeger.library.StatusBarUtil;

/**
 * create by youmu on 2018/7
 */

public class ChangPassActivity extends AppCompatActivity {
    private ImageView btnBack;
    private LinearLayout wrong_pass;
    private LinearLayout wrong_pass_double;
    private Button bt_editpass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepass);
        StatusBarUtil.setTransparent(ChangPassActivity.this);
        btnBack = (ImageView)findViewById(R.id.bt_back_personal);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        wrong_pass = (LinearLayout)findViewById(R.id.wrong_pass);
        wrong_pass_double = (LinearLayout)findViewById(R.id.wrong_double_pass);
        bt_editpass = (Button)findViewById(R.id.bt_editpass);
        bt_editpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dialog = new CustomDialog(ChangPassActivity.this, "提示", "设备密码一旦修改，所有终端的设备\n" +
                        "密码将随之更改，确认修改设备密码\n" +
                        "吗？", "取消", "确认", new CustomDialog.OnOkClickListener() {
                    @Override
                    public void onClick() {
                        editpass();
                    }
                });
                dialog.show();
            }
        });
        wrong_pass.setVisibility(View.INVISIBLE);
        wrong_pass_double.setVisibility(View.INVISIBLE);

    }

    private void editpass(){
        EditText et_original_password = (EditText)findViewById(R.id.et_original_password);
        EditText et_new_password = (EditText)findViewById(R.id.et_new_password);
        EditText confrim_pass_again = (EditText)findViewById(R.id.confrim_pass_again);
        String originalPass = et_original_password.getText().toString();
        String newPass = et_new_password.getText().toString();
        String confrimPass = confrim_pass_again.getText().toString();
        if (newPass.equals(confrimPass)){
            wrong_pass_double.setVisibility(View.INVISIBLE);
        }else{
            wrong_pass_double.setVisibility(View.VISIBLE);
        }
    }
}
