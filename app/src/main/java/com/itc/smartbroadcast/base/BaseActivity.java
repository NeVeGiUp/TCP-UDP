package com.itc.smartbroadcast.base;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Activity基类
 */
public abstract class BaseActivity extends Base2Activity {

    private Unbinder bind;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置不能横屏，防止生命周期的改变
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        this.setContentView(getLayoutId());
        bind = ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected abstract void init();

    protected abstract int getLayoutId();



    @Override
    protected void onDestroy() {
        bind.unbind();
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    public void goTo(Class<?> to, Bundle bundle) {
        Intent it = new Intent(this, to);
        it.putExtras(bundle);
        startActivity(it);
    }


    public void goTo(Class<?> to) {
        Intent it = new Intent(this, to);
        startActivity(it);
    }

    public void goToForResult(Class<?> to, int requestCode) {
        Intent it = new Intent(this, to);
        startActivityForResult(it, requestCode);
    }

    public void goToForResult(Class<?> to, Bundle bundle, int requestCode) {
        Intent it = new Intent(this, to);
        it.putExtras(bundle);
        startActivityForResult(it, requestCode);

    }

}