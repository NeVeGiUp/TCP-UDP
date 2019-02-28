package com.itc.smartbroadcast.activity.login;


import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.adapter.MainPageAdapter;
import com.itc.smartbroadcast.channels.http.CloudProtocolModel;
import com.itc.smartbroadcast.fragment.CloudLoginFragment;
import com.itc.smartbroadcast.fragment.LocalLoginFragment;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

//create by youmu on 2018/12
public class LoginActivity extends AppCompatActivity {


    @BindView(R.id.tl_login_mode)
    TabLayout tlLoginMode;
    @BindView(R.id.vp_login)
    ViewPager vpLogin;

    private List<Fragment> mFragments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.setStatusBarColor(getResources().getColor(android.R.color.white));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorWhite),0);
        ButterKnife.bind(this);

        mFragments = new ArrayList<>();
        LocalLoginFragment localLoginFragment = new LocalLoginFragment();
        CloudLoginFragment cloudLoginFragment = new CloudLoginFragment();
        mFragments.add(localLoginFragment);
        mFragments.add(cloudLoginFragment);

        MainPageAdapter mainPageAdapter = new MainPageAdapter(getSupportFragmentManager(),mFragments,this);
        vpLogin.setAdapter(mainPageAdapter);
        tlLoginMode.setupWithViewPager(vpLogin);

        LinearLayout linearLayout = (LinearLayout) tlLoginMode.getChildAt(0);
        linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        linearLayout.setDividerPadding(52); // 设置分割线的pandding
        linearLayout.setDividerDrawable(ContextCompat.getDrawable(this, R.drawable.sp_exposure_select));


        for (int i = 0; i < 2; i++) {
            TabLayout.Tab tab = tlLoginMode.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(mainPageAdapter.getCustomView(i));
            }
            if (i == 0) {
                ((ImageView) tab.getCustomView().findViewById(R.id.iv_tab_icon)).setSelected(true);
                ((TextView) tab.getCustomView().findViewById(R.id.tv_tab)).setSelected(true);
            }
        }

        tlLoginMode.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ((ImageView) tab.getCustomView().findViewById(R.id.iv_tab_icon)).setSelected(true);
                ((TextView) tab.getCustomView().findViewById(R.id.tv_tab)).setSelected(true);
                vpLogin.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ((ImageView) tab.getCustomView().findViewById(R.id.iv_tab_icon)).setSelected(false);
                ((TextView) tab.getCustomView().findViewById(R.id.tv_tab)).setSelected(false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//                System.exit(0);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

}
