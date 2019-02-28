package com.itc.smartbroadcast.activity.personal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.util.ToastUtil;
import com.jaeger.library.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PersonalPageActivity extends AppCompatActivity {

    @BindView(R.id.iv_login_mode)
    ImageView ivLoginMode;
    @BindView(R.id.about_avatar)
    ImageView aboutAvatar;
    @BindView(R.id.tv_username_personal)
    TextView tvUsernamePersonal;
    @BindView(R.id.tv_usertype_personal)
    TextView tvUsertypePersonal;
    @BindView(R.id.iv_icon0)
    ImageView ivIcon0;
    @BindView(R.id.bt_switch_system)
    RelativeLayout btSwitchSystem;
    @BindView(R.id.iv_icon1)
    ImageView ivIcon1;
    @BindView(R.id.bt_timerinfo)
    RelativeLayout btTimerinfo;
    @BindView(R.id.iv_icon2)
    ImageView ivIcon2;
    @BindView(R.id.bt_authorization)
    RelativeLayout btAuthorization;
    @BindView(R.id.iv_icon3)
    ImageView ivIcon3;
    @BindView(R.id.bt_timesync)
    RelativeLayout btTimesync;
    @BindView(R.id.iv_icon4)
    ImageView ivIcon4;
    @BindView(R.id.bt_accountmanage)
    RelativeLayout btAccountmanage;
    @BindView(R.id.iv_icon5)
    ImageView ivIcon5;
    @BindView(R.id.bt_system_deploy)
    RelativeLayout btSystemDeploy;
    @BindView(R.id.btn_exit)
    Button btnExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragmant_personal);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);
        ivIcon1.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_not_interested_black_24dp));
        ivIcon3.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_not_interested_black_24dp));
        ivIcon4.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_not_interested_black_24dp));
        ivIcon5.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_not_interested_black_24dp));
//        ToastUtil.show(this,"当前是未注册状态");
    }

    @OnClick({R.id.bt_switch_system, R.id.bt_timerinfo, R.id.bt_authorization, R.id.bt_timesync, R.id.bt_accountmanage, R.id.bt_system_deploy, R.id.btn_exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_switch_system:
                ToastUtil.show(this,"当前是未注册状态");
                break;
            case R.id.bt_timerinfo:
                ToastUtil.show(this,"当前是未注册状态");
                break;
            case R.id.bt_authorization:
                startActivity(new Intent(this,AuthorizationActivity.class));
                break;
            case R.id.bt_timesync:
                ToastUtil.show(this,"当前是未注册状态");
                break;
            case R.id.bt_accountmanage:
                ToastUtil.show(this,"当前是未注册状态");
                break;
            case R.id.bt_system_deploy:
                ToastUtil.show(this,"当前是未注册状态");
                break;
            case R.id.btn_exit:
                finish();
                break;
        }
    }
}
