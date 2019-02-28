package com.itc.smartbroadcast.activity;

import android.content.Context;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.LoginedInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.dao.ResultDB;
import com.itc.smartbroadcast.fragment.EventFragment;
import com.itc.smartbroadcast.fragment.FoundFragment;
import com.itc.smartbroadcast.fragment.MusicFragment;
import com.itc.smartbroadcast.fragment.PersonalFragment;
import com.itc.smartbroadcast.fragment.TodayFragment;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.util.StringUtil;
import com.itc.smartbroadcast.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * create by youmu on 2018/7
 */

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNav;
    private long mExitTime;

    private static Context context;
    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.savedInstanceState = savedInstanceState;

        EventBus.getDefault().register(this);

        init(savedInstanceState);
    }

    private void init(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);

        context = this;
//        StatusBarUtil.setTranslucentForImageViewInFragment(MainActivity.this, 0, null);
        //StatusBarUtil.setColor(MainActivity.this, getResources().getColor(R.color.colorMain),0);
        bottomNav = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNav.setOnNavigationItemSelectedListener(navListener);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new TodayFragment()).commit();
        }

        Intent intent = getIntent();
        String result = intent.getStringExtra("jump");
        if (StringUtil.isEmpty(result)) {

        } else {
            Fragment selectedFragment = null;
            switch (result) {
                case "found":
                    selectedFragment = new FoundFragment();
                    bottomNav.getMenu().getItem(1).setChecked(true);
                    break;
                case "personal":
                    selectedFragment = new PersonalFragment();
                    bottomNav.getMenu().getItem(4).setChecked(true);
                    break;
                case "music":
                    selectedFragment = new MusicFragment();
                    bottomNav.getMenu().getItem(3).setChecked(true);
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selectedFragment).commit();
        }
    }

    public static Context getContent() {
        return context;
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_today:
                            selectedFragment = new TodayFragment();
                            break;
                        case R.id.nav_found:
                            selectedFragment = new FoundFragment();
                            break;
                        case R.id.nav_event:
                            selectedFragment = new EventFragment();
                            break;
                        case R.id.nav_music:
                            selectedFragment = new MusicFragment();
                            break;
                        case R.id.nav_personal:
                            selectedFragment = new PersonalFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();
                    return true;
                }
            };


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);
        //切换任务成功刷新界面
        if ("notifyRefresh".equals(baseBean.getType())) {
            //清除之前获取的周值和日期值
            AppDataCache.getInstance().putString("executeTaskWeek", "");
            AppDataCache.getInstance().putString("executeTaskDate", "");
            init(savedInstanceState);
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Object mHelperUtils;
                Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                System.exit(0);

            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}