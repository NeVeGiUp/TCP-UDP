package com.itc.smartbroadcast.widget.custom.menu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.itc.smartbroadcast.cache.AppDataCache;
/**
 * Created by zhangzl on 2017/1/5.
 */

public class NetworkConnectChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
// 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
        String action = intent.getAction();
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            //获取联网状态的NetworkInfo对象
            NetworkInfo info = intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (info != null) {
                //如果当前的网络连接成功并且网络连接可用
                if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI
                            || info.getType() == ConnectivityManager.TYPE_MOBILE) {
                        AppDataCache.getInstance().putBoolean("isHasNetwork", true);
                        Log.i("TAG", getConnectionType(info.getType()) + "连上");
                    }
                } else {
                    AppDataCache.getInstance().putBoolean("isHasNetwork", false);
                    Log.i("TAG", getConnectionType(info.getType()) + "断开");
                }
            }
        }
        if (action.equals(
                ConnectivityManager.CONNECTIVITY_ACTION)
                || action.equals(
                "android.net.conn.CONNECTIVITY_CHANGE")) {

            switch (isNetworkAvailable(context)) {
                case 1:
                    Log.e("pds", "-----------networktest---------有线");
                    break;
                case 2:
                    Log.e("pds", "-----------networktest---------无线");
                    break;
                case 0:
                    Log.e("pds", "-----------networktest---------无网络");
//                    EventBus.getDefault().post(new MainErrorEvent(1002));
                    break;
                default:
                    break;
            }
        }

    }
    private String getConnectionType(int type) {
        switch (type){
            case ConnectivityManager.TYPE_MOBILE:
                    return "3G网络数据";
            case ConnectivityManager.TYPE_WIFI:
                return "WIFI网络";
            case ConnectivityManager.TYPE_ETHERNET:
                return "有线网络";
            default:
                return "异常";
        }
    }
    public int isNetworkAvailable(Context context) {
        ConnectivityManager connectMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ethNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        NetworkInfo wifiNetInfo = connectMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (ethNetInfo != null && ethNetInfo.isConnected()) {
            return 1;
        } else if (wifiNetInfo != null && wifiNetInfo.isConnected()) {
            return 2;
        } else {
            return 0;
        }
    }

}

