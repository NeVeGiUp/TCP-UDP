package com.itc.smartbroadcast.activity.personal;

import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.found.CreatePartitionActivity;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.PartitionInfo;
import com.itc.smartbroadcast.bean.SynTimeResult;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.SynchronizationTime;
import com.itc.smartbroadcast.helper.CustomDialog;
import com.itc.smartbroadcast.util.ConfigUtils;
import com.itc.smartbroadcast.util.ToastUtil;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * create by youmu on 2018/7
 */

public class TimeSyncActivity extends AppCompatActivity {
    private Button bt_synctime;
    TextView localTime1;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            this.update();
            handler.postDelayed(this, 1000);
        }
        void update(){
            localTime1 = (TextView)findViewById(R.id.getLocalTime);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            localTime1.setText(simpleDateFormat.format(date));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timesync);
        EventBus.getDefault().register(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);
        localTime1 = (TextView)findViewById(R.id.getLocalTime);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        localTime1.setText(simpleDateFormat.format(date));
        handler.postDelayed(runnable, 1000);
        bt_synctime = (Button)findViewById(R.id.bt_synctime);
        bt_synctime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialog dialog = new CustomDialog(TimeSyncActivity.this, "提示", "同步手机时间为系统时间后，当前\n" +
                        "系统时间无法恢复，确认同步吗？", "取消", "确认", new CustomDialog.OnOkClickListener() {
                    @Override
                    public void onClick() {
                        initDate();
                    }
                });
                dialog.show();
            }
        });
        ImageView btnBack = (ImageView) findViewById(R.id.bt_back_personal);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initDate(){
        Calendar c = Calendar.getInstance();
        boolean isFirstSunday = (c.getFirstDayOfWeek() == Calendar.SUNDAY);
        int mYear = c.get(Calendar.YEAR); // 获取当前年份
        if (mYear<2000||mYear>2255){
            mYear = 2000;
        }else {
            mYear = mYear-2000;
        }
        int mMonth = c.get(Calendar.MONTH) + 1;// 获取当前月份
        int mDay = c.get(Calendar.DAY_OF_MONTH);// 获取当日期
        int mWeek = c.get(Calendar.DAY_OF_WEEK);// 获取当前日期的周
        if(isFirstSunday){
            mWeek = mWeek - 1;
            if(mWeek == 0){
                mWeek = 7;
            }
        }
        int mHour = c.get(Calendar.HOUR_OF_DAY);//时
        int mMinute = c.get(Calendar.MINUTE);//分
        int mSecond = c.get(Calendar.SECOND);//秒
        System.out.println("时间》》》"+mYear+" "+mMonth+" "+mDay+" "+mWeek+" "+mHour+" "+mMinute+" "+mSecond);
        int[] synDate = new int[7];
        synDate[0] = mYear;  //年
        synDate[1] = mMonth;     //月
        synDate[2] = mDay;    //日
        synDate[3] = mHour;    //时
        synDate[4] = mMinute;     //分
        synDate[5] = mSecond;     //秒
        synDate[6] = mWeek;     //周
        SynchronizationTime.sendCMD(AppDataCache.getInstance().getString("loginIp"), "00", synDate);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        final Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);
        if ("SynchronizationTime".equals(baseBean.getType())) {
            SynTimeResult synTimeResult = gson.fromJson(baseBean.getData(), SynTimeResult.class);
            int isSucceed = synTimeResult.getResult();
            if (isSucceed == 0) {
                ToastUtil.show(TimeSyncActivity.this, "同步时间失败");
            } else if (isSucceed == 1) {
                ToastUtil.show(TimeSyncActivity.this, "同步时间成功");
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(runnable); //停止刷新
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


}