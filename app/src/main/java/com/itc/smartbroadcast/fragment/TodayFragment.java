package com.itc.smartbroadcast.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.MainActivity;
import com.itc.smartbroadcast.base.BaseFragment;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditMusicFolderNameResult;
import com.itc.smartbroadcast.bean.ExecuteTaskDateResult;
import com.itc.smartbroadcast.bean.LoginedInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.ExecuteTaskDate;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.popupwindow.MoreWindow;
import com.itc.smartbroadcast.util.ToastUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Calendar;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * create by youmu on 2018/7
 */

public class TodayFragment extends BaseFragment {

    MoreWindow mMoreWindow;                 //右上角更多自定义view

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.no_data_tv)
    TextView noData_tv;
    @BindView(R.id.showWindow)
    ImageView showWindow;
    Unbinder unbinder;

    private TodayViewPagerAdapter mTodayViewPagerAdapter;



    @SuppressLint("ResourceType")
    @Override
    public void init() {

        //透明状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        if (!AppDataCache.getInstance().getString("userType").equals("00")){
            showWindow.setVisibility(View.GONE);
        }
        //清除日期索引缓存
        AppDataCache.getInstance().putString("slideIndex", "");
        //获取定时器执行周几任务
        initExecuteDate();
        //初始化TabLayout，ViewPager
        initView();

    }

    private void initExecuteDate() {
        ExecuteTaskDate.sendCMD(AppDataCache.getInstance().getString("loginIp"));
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void initView() {

        noData_tv.setVisibility(View.VISIBLE);
        tabLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.GONE);
        line.setVisibility(View.GONE);

        //初始化ViewPagerAdapter
        mTodayViewPagerAdapter = new TodayViewPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(mTodayViewPagerAdapter);

        //TabLayout关联ViewPager
        tabLayout.setupWithViewPager(viewPager);
        viewPager.clearOnPageChangeListeners();

        //Viewpager的监听
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        //通过反射方式自定义indicator为滑块，带动画（需求：滑块只填充文字，现滑块处于文字与图片之间(只有一个view时正常，两个view会居中)，若解决则选择此方式）,现在用的是字体选择器模式代替
//        LinearLayout indicator_view = (LinearLayout) tabLayout.getChildAt(0);
//        indicator_view.setBackgroundDrawable(new ProxyDrawable(indicator_view));

        //indicator字体变化监听
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getCustomView() != null) {
                    TextView textView = (TextView) tab.getCustomView().findViewById(R.id.tabtext);
                    textView.setTextColor(getResources().getColor(R.color.colorWhite));
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab.getCustomView() != null) {
                    TextView textView = (TextView) tab.getCustomView().findViewById(R.id.tabtext);
                    textView.setTextColor(getResources().getColor(R.color.bg_def_indicator));
                }

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }


    @Override
    public int getLayoutId() {
        return R.layout.fragment_today;
    }


    /**
     * Eventbus 接收数据
     *
     * @param json
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {

        if (json == null)
            return;

        Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);

        //今日执行周几任务
        if ("ExecuteTaskDateResult".equals(baseBean.getType())) {
            initView();   //解决执行中状态叠加问题
            String data = baseBean.getData();
            if (data != null) {

                noData_tv.setVisibility(View.GONE);
                tabLayout.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.VISIBLE);
                line.setVisibility(View.VISIBLE);

                ExecuteTaskDateResult executeTaskDateResult = gson.fromJson(data, ExecuteTaskDateResult.class);

                //执行星期 1表示星期一，7为星期天
                int executeTaskWeek = executeTaskDateResult.getExecuteTaskWeek() - 1;

                if (executeTaskWeek >= 0) {

                    //执行日期
                    String executeTaskDate = executeTaskDateResult.getExecuteTaskDate();

                    //设置默认选中的viewpager
                    viewPager.setCurrentItem(executeTaskWeek);

                    //设置默认选中的tab
                    tabLayout.getTabAt(executeTaskWeek).select();

                    //indicator下标添加图片
                    for (int i = 0; i < tabLayout.getTabCount(); i++) {
                        tabLayout.getTabAt(i).setCustomView(mTodayViewPagerAdapter.getTabView(i, executeTaskWeek));
                    }

                    //缓存星期值
                    AppDataCache.getInstance().putString("executeTaskWeek", String.valueOf(executeTaskWeek));
                    //缓存日期值
                    AppDataCache.getInstance().putString("executeTaskDate", String.valueOf(executeTaskDate));
                }

            }
        }
        if ("EditMusicFolderNameResult".equals(baseBean.getType())) {
            EditMusicFolderNameResult partitionInfo = gson.fromJson(baseBean.getData(), EditMusicFolderNameResult.class);
            int isSucceed = partitionInfo.getResult();
            if (isSucceed == 0) {
                ToastUtil.show(getActivity(), "失败");
            } else if (isSucceed == 1) {
                ToastUtil.show(getActivity(), "成功");
                init();
            }
        }
        if ("getLoginedMsg".equals(baseBean.getType())) {
            LoginedInfo loginedInfo = gson.fromJson(baseBean.getData(), LoginedInfo.class);
            //登录状态
            String loginState = loginedInfo.getLoginState();
            if ("00".equals(loginState)) {
                // 登录成功后缓存用户输入信息
                AppDataCache.getInstance().putString("loginedMsg", baseBean.getData());
                AppDataCache.getInstance().putString("loginUsername", loginedInfo.getUserName());
                AppDataCache.getInstance().putString("loginPsw", loginedInfo.getSystemPsw());
                AppDataCache.getInstance().putInt("userNum", loginedInfo.getUserNum());
                AppDataCache.getInstance().putString("userType", loginedInfo.getUserType());
                AppDataCache.getInstance().putString("timerMask", loginedInfo.getSubnetMask());//定时器子网掩码
                AppDataCache.getInstance().putString("timerGateway", loginedInfo.getGateway());//定时器网关
                AppDataCache.getInstance().putString("timerMac", loginedInfo.getDeviceMac());//定时器MAC
                AppDataCache.getInstance().putString("timerName", loginedInfo.getHostName());//定时器名称
                AppDataCache.getInstance().putString("timerRegStatus", loginedInfo.getRegisterState());//定时器注册状态 00：未注册 01：有限注册 02：永久注册
                AppDataCache.getInstance().putString("timerMecCode", loginedInfo.getDeviceMechanicalCode());//定时器机械码
                AppDataCache.getInstance().putString("timerVersion", loginedInfo.getHostVersion());//设备版本
                AppDataCache.getInstance().putString("ipMode", loginedInfo.getIpAcquisitionMode());//IP获取方式
                AppDataCache.getInstance().putString("userPhoneNum",loginedInfo.getUserPhoneNum());//用户手机号
                //启动心跳包
                NettyUdpClient.sendHeartBeatCMD(AppDataCache.getInstance().getString("loginIp"), AppDataCache.getInstance().getInt("userNum"));
            } else if ("01".equals(loginState)) {
                ToastUtil.show(getActivity(), "找不到指定账户");
            } else {
                ToastUtil.show(getActivity(), "密码错误");
            }
        }

    }


    @OnClick({R.id.showWindow, R.id.no_data_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.showWindow:         //右上角点击更多
                showMoreWindow(view);
                break;
            case R.id.no_data_tv:         //无数据点击重试
                init();
                break;
            default:
                break;
        }
    }


    //右上角点击展示更多选择view
    private void showMoreWindow(View view) {
        if (null == mMoreWindow) {
            mMoreWindow = new MoreWindow(getActivity());
            mMoreWindow.init();
        }
        mMoreWindow.showMoreWindow(view, 100);
    }


    //获取当前日期
    public static String getCurrentTime(String type) {
        //Calendar类获取当前日期
        final Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        String year = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
        String month = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
        String week = String.valueOf(c.get(Calendar.DAY_OF_WEEK));

        if ("date".equals(type)) {
            if ("1".equals(week)) {
                week = "天";
            } else if ("2".equals(week)) {
                week = "一";
            } else if ("3".equals(week)) {
                week = "二";
            } else if ("4".equals(week)) {
                week = "三";
            } else if ("5".equals(week)) {
                week = "四";
            } else if ("6".equals(week)) {
                week = "五";
            } else if ("7".equals(week)) {
                week = "六";
            }
            return year + "年" + month + "月" + day + "日" + ", 星期" + week;
        } else if ("week".equals(type)) {
            return week;
        }
        return "";
    }


    //ViewPager适配器
    class TodayViewPagerAdapter extends FragmentPagerAdapter {
        public final String[] names = new String[]{"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

        public TodayViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    Bundle bundle = new Bundle();
                    bundle.putInt("type", 0);
                    return TodayTaskListFragment.newInstance(bundle);
                case 1:
                    Bundle bundle2 = new Bundle();
                    bundle2.putInt("type", 1);
                    return TodayTaskListFragment.newInstance(bundle2);
                case 2:
                    Bundle bundle3 = new Bundle();
                    bundle3.putInt("type", 2);
                    return TodayTaskListFragment.newInstance(bundle3);
                case 3:
                    Bundle bundle4 = new Bundle();
                    bundle4.putInt("type", 3);
                    return TodayTaskListFragment.newInstance(bundle4);

                case 4:
                    Bundle bundle5 = new Bundle();
                    bundle5.putInt("type", 4);
                    return TodayTaskListFragment.newInstance(bundle5);

                case 5:
                    Bundle bundle6 = new Bundle();
                    bundle6.putInt("type", 5);
                    return TodayTaskListFragment.newInstance(bundle6);

                case 6:
                    Bundle bundle7 = new Bundle();
                    bundle7.putInt("type", 6);
                    return TodayTaskListFragment.newInstance(bundle7);
            }
            return null;
        }

        @Override
        public int getCount() {
            return names.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return names[position];
        }

        //设置执行中状态图标
        public View getTabView(int position, int executeWeek) {

            View v = LayoutInflater.from(getActivity()).inflate(R.layout.tablayout_indicator, null);
            ImageView iv = (ImageView) v.findViewById(R.id.tabicon);
            TextView tv = (TextView) v.findViewById(R.id.tabtext);
            tv.setText(names[position]);

            //第一个indicator字体设置背景
            if (position == executeWeek) {
                tv.setTextColor(v.getResources().getColor(R.color.colorWhite));
            } else {
                tv.setTextColor(v.getResources().getColor(R.color.bg_def_indicator));
            }

            //根据时间改变执行中的日期状态
            if (position == executeWeek) {
                iv.setImageDrawable(getActivity().getResources().getDrawable(R.mipmap.excution));
            } else {
            }

            return v;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //清除日期索引缓存
        AppDataCache.getInstance().putString("slideIndex", "");
        Log.d("TodayFragment", "TodayFragment : onDestroy()清除缓存值：slideIndex");
    }
}