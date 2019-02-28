package com.itc.smartbroadcast.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.itc.smartbroadcast.widget.custom.CommonProgressDialog;

/**
 * content:
 * author:lik
 * date: 18-10-18 上午10:01
 */
public abstract class Base2Activity extends AppCompatActivity {

    private static Context mContext = null;
    public static CommonProgressDialog progressDialog = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置不能横屏，防止生命周期的改变
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog = new CommonProgressDialog(this);  //登录进度条
        progressDialog.setTitle("获取数据中...");
    }

    @Override
    protected void onPause() {
        super.onPause();
        progressDialog = null;
    }

    public static Context getContext(){

        return mContext;
    }

    public static CommonProgressDialog getCommonProgressDialog(){
        return progressDialog;
    }
}
