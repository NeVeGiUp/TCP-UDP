package com.itc.smartbroadcast.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.login.ChooseProjectActivity;
import com.itc.smartbroadcast.activity.login.LoginActivity;
import com.itc.smartbroadcast.activity.login.SwitchProjectActivity;
import com.itc.smartbroadcast.activity.personal.AccountManageActivity;
import com.itc.smartbroadcast.activity.personal.AuthorizationActivity;
import com.itc.smartbroadcast.activity.personal.DepolySystemActivity;
import com.itc.smartbroadcast.activity.personal.TimeSyncActivity;
import com.itc.smartbroadcast.activity.personal.TimerInfoActivity;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.helper.CustomDialog;
import com.itc.smartbroadcast.util.ToastUtil;

/**
 * create by youmu on 2018/7
 */

public class PersonalFragment extends Fragment {
    private RelativeLayout btnTimerInfo;
    private RelativeLayout btnAuthorization;
    private RelativeLayout btnTimesync;
    private RelativeLayout btnAccountmanage;
    private RelativeLayout btnSystempass;
    private RelativeLayout btDepolySystem,btSwitchSystem;
    private Button btnExit;
    private TextView tvUserName,tvUserType;
    private ImageView icon1,icon2,icon3,icon4,ivLoginMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmant_personal, container, false);
        ivLoginMode = (ImageView)view.findViewById(R.id.iv_login_mode);
        btSwitchSystem = (RelativeLayout)view.findViewById(R.id.bt_switch_system);
        btnTimerInfo = (RelativeLayout) view.findViewById(R.id.bt_timerinfo);
        if (SmartBroadcastApplication.isCloud){
            ivLoginMode.setImageResource(R.mipmap.login_but_yundenglu_down);
            btSwitchSystem.setVisibility(View.VISIBLE);
        }
        icon1 = (ImageView)view.findViewById(R.id.iv_icon1);
        icon2 = (ImageView)view.findViewById(R.id.iv_icon2);
        icon3 = (ImageView)view.findViewById(R.id.iv_icon3);
        icon4 = (ImageView)view.findViewById(R.id.iv_icon4);
        btDepolySystem = (RelativeLayout)view.findViewById(R.id.bt_system_deploy);
        btDepolySystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),DepolySystemActivity.class));
            }
        });
        btnTimerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TimerInfoActivity.class);
                startActivity(intent);
            }
        });
        btnAuthorization = (RelativeLayout) view.findViewById(R.id.bt_authorization);
        btnAuthorization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AuthorizationActivity.class);
                startActivity(intent);
                //ToastUtil.show(getActivity(),"请等待以后版本更新");
            }
        });
        btnTimesync = (RelativeLayout) view.findViewById(R.id.bt_timesync);
        btnTimesync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TimeSyncActivity.class);
                startActivity(intent);
            }
        });
        btnAccountmanage = (RelativeLayout) view.findViewById(R.id.bt_accountmanage);
        btnAccountmanage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AccountManageActivity.class);
                startActivity(intent);
            }
        });
        btSwitchSystem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(),SwitchProjectActivity.class));
            }
        });
//        btnSystempass = (RelativeLayout) view.findViewById(R.id.bt_systempass);
//        btnSystempass.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), ChangPassActivity.class);
//                startActivity(intent);
//            }
//        });
        btnExit = (Button) view.findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomDialog dialog = new CustomDialog(getActivity(), "提示", "是否要退出当前账户？", "取消", "确认", new CustomDialog.OnOkClickListener() {
                    @Override
                    public void onClick() {
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });
                dialog.show();
            }
        });
        tvUserName = (TextView)view.findViewById(R.id.tv_username_personal);
        tvUserType = (TextView)view.findViewById(R.id.tv_usertype_personal);
        if (!AppDataCache.getInstance().getString("userType").equals("00")){
            btnTimerInfo.setClickable(false);
            btnTimerInfo.setFocusable(false);
            btnAccountmanage.setClickable(false);
            btnAccountmanage.setFocusable(false);
            btnAuthorization.setClickable(false);
            btnAuthorization.setFocusable(false);
            btnTimesync.setClickable(false);
            btnTimesync.setFocusable(false);

            icon1.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_not_interested_black_24dp));
            icon2.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_not_interested_black_24dp));
            icon3.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_not_interested_black_24dp));
            icon4.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_not_interested_black_24dp));

        }
        setUser();
        if (AppDataCache.getInstance().getString("timerRegStatus").equals("00")){

        }
        return view;
    }


    private void setUser(){
        tvUserName.setText(AppDataCache.getInstance().getString("loginUsername"));
        String userType = AppDataCache.getInstance().getString("userType");
        Log.i("userType",userType);
        if (userType.equals("00")){
            tvUserType.setText("管理员");
        }else if(userType.equals("01")){
            tvUserType.setText("普通用户");
        }else if (userType.equals("02")){
            tvUserType.setText("来宾用户");
        }
    }

}