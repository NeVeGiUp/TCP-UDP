package com.itc.smartbroadcast.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.found.AllTerminalsActivity;
import com.itc.smartbroadcast.activity.found.NonePartTerminalActivity;
import com.itc.smartbroadcast.activity.found.PartitionManageActivity;
import com.itc.smartbroadcast.adapter.FoundAdapter;
import com.itc.smartbroadcast.base.BaseFragment;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditMusicFolderNameResult;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.bean.FoundPartitionInfo;
import com.itc.smartbroadcast.bean.ListRefreshResult;
import com.itc.smartbroadcast.bean.LoginedInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.GetDeviceList;
import com.itc.smartbroadcast.channels.protocolhandler.GetPartitionList;
import com.itc.smartbroadcast.popupwindow.MoreWindow;
import com.itc.smartbroadcast.util.ToastUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * create by youmu on 2018/7
 */

public class FoundFragment extends BaseFragment {

    @BindView(R.id.showWindow)
    ImageView showWindow;
    @BindView(R.id.rv_device_list)
    RecyclerView rvDeviceList;

    MoreWindow mMoreWindow;
    @BindView(R.id.back_to_top)
    RelativeLayout backToTop;

    @BindView(R.id.device_count)
    TextView deviceCount;
    @BindView(R.id.partition_count_tv)
    TextView partitionCountTv;
    @BindView(R.id.wait_patition_count_tv)
    TextView waitPatitionCountTv;
    @BindView(R.id.ll_wait_for_part)
    LinearLayout llWaitForPart;
    @BindView(R.id.ll_device_num)
    LinearLayout llDeviceNum;
    @BindView(R.id.ll_part_num)
    LinearLayout llPartNum;

    Unbinder unbinder;
    private FoundAdapter mFoundAdapter;

    List<FoundDeviceInfo> deviceInfoList;
    List<FoundPartitionInfo> partInfoList;
    List<FoundDeviceInfo> operableDeviceInfoList;
    List<FoundPartitionInfo> userInfoList;

    private int lastOffset = 0;
    private int lastPosition = 0;

    private int deviceNum,partNum,unPartNum;
    @Override
    public void init() {



        initRv();
        GetPartitionList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
        if (!AppDataCache.getInstance().getString("userType").equals("00")){
            showWindow.setVisibility(View.GONE);
        }

//        if (!TextUtils.isEmpty(AppDataCache.getInstance().getString("deviceCount"))) {
//            deviceCount.setText(AppDataCache.getInstance().getString("deviceCount"));
//        }
//        //分区数量
//        if (!TextUtils.isEmpty(AppDataCache.getInstance().getString("partitinoCount"))) {
//            partitionCountTv.setText(AppDataCache.getInstance().getString("partitinoCount"));
//        }
//        String s = AppDataCache.getInstance().getString("waitPartitionCount");
//        //待分区设备数量
//        if (!TextUtils.isEmpty(AppDataCache.getInstance().getString("waitPartitionCount"))) {
//            waitPatitionCountTv.setText(AppDataCache.getInstance().getString("waitPartitionCount"));
//        }

        llDeviceNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(),AllTerminalsActivity.class);
                startActivity(intent);
            }
        });
        llPartNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(),PartitionManageActivity.class);
                startActivity(intent);
            }
        });
        llWaitForPart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(),NonePartTerminalActivity.class);
                startActivity(intent);
            }
        });

    }


    private void refreshRv(){
        GetPartitionList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
//        mFoundAdapter.setDeviceList(operableDeviceInfoList);
        rvDeviceList.notifyAll();
    }
    private void initRv() {

//        rvDeviceList.setVisibility(View.GONE);
        mFoundAdapter = new FoundAdapter(getActivity());
        rvDeviceList.setHasFixedSize(true);
        rvDeviceList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvDeviceList.setAdapter(mFoundAdapter);
//        rvDeviceList.getItemAnimator().setChangeDuration(0); //取消rv的动画

    }

    /**
     * 发现页面数据回调
     *
     * @param json
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;

        Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);
        if ("getDeviceList".equals(baseBean.getType())) {   //设备列表数据
            String data = baseBean.getData();
            if (data != null) {
                deviceInfoList = JSONArray.parseArray(data, FoundDeviceInfo.class);

                String userJson = AppDataCache.getInstance().getString("loginedMsg");
                LoginedInfo userInfo = JSONObject.parseObject(userJson, LoginedInfo.class);
                deviceInfoList = JSONArray.parseArray(data, FoundDeviceInfo.class);
                operableDeviceInfoList = new ArrayList<>();
                if (userInfo.getUserType().equals("00")) {
                    operableDeviceInfoList.addAll(deviceInfoList);
                    deviceNum = operableDeviceInfoList.size();
                } else {
                    List<String> list = userInfo.getOperableDeviceMacList();
                    deviceNum = 0;
                    for (String mac : list) {
                        for (FoundDeviceInfo device : deviceInfoList) {
                            if (mac.equals(device.getDeviceMac())) {
                                operableDeviceInfoList.add(device);
                                deviceNum++;
                            }
                        }
                    }
                }
                mFoundAdapter.setDeviceList(operableDeviceInfoList);

                if(rvDeviceList.getLayoutManager() != null && lastPosition >= 0) {
                    Log.i("postiton&offset:",lastPosition+"   "+lastOffset);
                    RecyclerView.LayoutManager layoutManager = rvDeviceList.getLayoutManager();
                    scrollToPos(layoutManager,lastPosition,lastOffset);
                }

                rvDeviceList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if(recyclerView.getLayoutManager() != null) {
                            LinearLayoutManager layoutManager = (LinearLayoutManager) rvDeviceList.getLayoutManager();
                            View topView = layoutManager.getChildAt(0);
                            if(topView != null) {
                                //获取与该view的顶部的偏移量
                                lastOffset = topView.getTop();
                                //得到该View的数组位置
                                lastPosition = layoutManager.getPosition(topView);
                            }
                        }
                    }
                });

                for (FoundPartitionInfo foundPartitionInfo : partInfoList) {
                    foundPartitionInfo.setDeviceInfoList(new ArrayList<FoundDeviceInfo>());
                    for (FoundDeviceInfo foundDeviceInfo : deviceInfoList) {
                        if(foundDeviceInfo.getDeviceZoneMsg().contains(Integer.valueOf(foundPartitionInfo.getPartitionNum()))) {
                            foundPartitionInfo.getDeviceInfoList().add(foundDeviceInfo);
                        }
                    }
                }
                unPartNum = 0;
                for (FoundDeviceInfo foundDeviceInfo :operableDeviceInfoList){
                    if (foundDeviceInfo.getDeviceZoneMsg().size() == 0){
                        unPartNum++;
                    }
                }
                deviceCount.setText(deviceNum+"");
                waitPatitionCountTv.setText(unPartNum+"");
            }
        }

        if ("getPartitionList".equals(baseBean.getType())) {  //已分区列表数据
            GetDeviceList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
            String data = baseBean.getData();
            if (data != null) {
                partInfoList = JSONArray.parseArray(data, FoundPartitionInfo.class);
                partNum = partInfoList.size();
                if (AppDataCache.getInstance().getString("userType").equals("00")) {
                    mFoundAdapter.setPartitionList(partInfoList);
                    partitionCountTv.setText(partNum+"");
                }else {
                    userInfoList = new ArrayList<>();
                    partNum = 0;
                    for (FoundPartitionInfo foundPartitionInfo :partInfoList){
                        if (foundPartitionInfo.getAccountId() == AppDataCache.getInstance().getInt("userNum") ){
                            userInfoList.add(foundPartitionInfo);
                            partNum++;
                        }
                    }
                    mFoundAdapter.setPartitionList(userInfoList);
                    partitionCountTv.setText(partNum+"");
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
        if ("listRefresh".equals(baseBean.getType())){
            ListRefreshResult listRefreshResult = gson.fromJson(baseBean.getData(),ListRefreshResult.class);
            int result = listRefreshResult.getResult();
            switch (result){
                case 0:
                    refreshRv();
                    break;
                case 1:
                    refreshRv();
                    break;
            }

        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_found;
    }

    private void showMoreWindow(View view) {
        if (null == mMoreWindow) {
            mMoreWindow = new MoreWindow(getActivity());
            mMoreWindow.init();
        }
        mMoreWindow.showMoreWindow(view, 100);
    }


    @OnClick({R.id.showWindow, R.id.back_to_top})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.showWindow:
                showMoreWindow(view);
                break;
            case R.id.back_to_top:
//                rvDeviceList.scrollToPosition(0);
                RecyclerView.LayoutManager layoutManager = rvDeviceList.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    linearManager.scrollToPositionWithOffset(0, 0);
//                    linearManager.setStackFromEnd(true);
                }
                break;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View rootView = super.onCreateView(inflater, container, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}