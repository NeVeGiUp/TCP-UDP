package com.itc.smartbroadcast.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.itc.smartbroadcast.R;

import java.util.List;


public class MainPageAdapter extends FragmentPagerAdapter {
    //添加fragment的集合
    private List<Fragment> mFragmentList;
    //添加标题的集合
    private String tabTitles[]=new String[]{"局域网登录","云登录"};
    private Context mContext;

    public MainPageAdapter(FragmentManager fm, List<Fragment> fragmentList, Context mContext) {
        super(fm);
        mFragmentList = fragmentList;
        this.mContext=mContext;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public View getCustomView(int position){
        View view= LayoutInflater.from(mContext).inflate(R.layout.tabitem_cloud_login,null);
        ImageView iv= (ImageView) view.findViewById(R.id.iv_tab_icon);
        TextView tv= (TextView) view.findViewById(R.id.tv_tab);
        switch (position){
            case 0:
                iv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.selector_local_login));
                tv.setText("局域网登录");
                break;
            case 1:
                iv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.selector_could_login));
                tv.setText("云登录");
                break;
        }
        return view;
    }

}