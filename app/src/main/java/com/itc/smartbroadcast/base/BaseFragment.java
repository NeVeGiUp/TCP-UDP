package com.itc.smartbroadcast.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {

    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        EventBus.getDefault().register(this);
        init();
        super.onResume();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    public abstract void init();


    public abstract int getLayoutId();


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (unbinder != null)
            unbinder.unbind();
    }


    public void goTo(Class<?> to, Bundle bundle) {
        Intent it = new Intent(getActivity(), to);
        it.putExtras(bundle);
        startActivity(it);
    }


    public void goTo(Class<?> to) {
        Intent it = new Intent(getActivity(), to);
        startActivity(it);
    }

    public void scrollToPos(RecyclerView.LayoutManager layoutManager,int lastPosition,int lastOffset){
        LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
        linearManager.scrollToPositionWithOffset(lastPosition, lastOffset);
    }

    public void goToForResult(Class<?> to, int requestCode) {
        Intent it = new Intent(getActivity(), to);
        startActivityForResult(it, requestCode);
    }

    public void goToForResult(Class<?> to, Bundle bundle, int requestCode) {
        Intent it = new Intent(getActivity(), to);
        it.putExtras(bundle);
        startActivityForResult(it, requestCode);
    }

    //获取屏幕高度
    public int getStatusHeight() {
        int statusBarHeight1 = -1;
        //获取status_bar_height资源的ID
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight1;
    }


}