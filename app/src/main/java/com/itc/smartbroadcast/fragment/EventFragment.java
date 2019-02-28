package com.itc.smartbroadcast.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.event.AlarmTaskActivity;
import com.itc.smartbroadcast.activity.event.InstantTaskActivity;
import com.itc.smartbroadcast.activity.event.ShowRingingTaskActivity;
import com.itc.smartbroadcast.activity.event.TimedTaskActivity;
import com.itc.smartbroadcast.activity.event.utils.TaskUtils;
import com.itc.smartbroadcast.adapter.EventAdapter;
import com.itc.smartbroadcast.base.BaseFragment;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditInstantTaskResult;
import com.itc.smartbroadcast.bean.EditMusicFolderNameResult;
import com.itc.smartbroadcast.bean.EditSchemeResult;
import com.itc.smartbroadcast.bean.EditTaskResult;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.bean.InstantTask;
import com.itc.smartbroadcast.bean.LoginedInfo;
import com.itc.smartbroadcast.bean.Scheme;
import com.itc.smartbroadcast.bean.Task;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.GetAccountList;
import com.itc.smartbroadcast.channels.protocolhandler.GetDeviceList;
import com.itc.smartbroadcast.channels.protocolhandler.GetInstantTaskList;
import com.itc.smartbroadcast.channels.protocolhandler.GetSchemeList;
import com.itc.smartbroadcast.channels.protocolhandler.GetTaskList;
import com.itc.smartbroadcast.event.account.LoginInfo;
import com.itc.smartbroadcast.model.user.UserInfo;
import com.itc.smartbroadcast.popupwindow.MoreWindow;
import com.itc.smartbroadcast.util.ToastUtil;
import com.itc.smartbroadcast.widget.custom.CommonProgressDialog;

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
public class EventFragment extends BaseFragment {

    @BindView(R.id.showWindow)
    ImageView showWindow;
    @BindView(R.id.im_timedtask)
    ImageView im_timedtask;
    @BindView(R.id.im_ringing_task)
    ImageView im_ringing_task;
    @BindView(R.id.im_instanttask)
    ImageView im_instanttask;
    @BindView(R.id.im_alarmtask)
    ImageView im_alarmtask;
    @BindView(R.id.rv_index_event_list)
    RecyclerView rvIndexEventList;
    MoreWindow mMoreWindow;
    @BindView(R.id.tv_scheme_count)
    TextView tvSchemeCount;
    Unbinder unbinder;
    @BindView(R.id.tv_task_count)
    TextView tvTaskCount;
    @BindView(R.id.tv_instant_count)
    TextView tvInstantCount;
    @BindView(R.id.ll_showWindow)
    LinearLayout llShowWindow;
    @BindView(R.id.ll_ringing_task)
    LinearLayout llRingingTask;
    @BindView(R.id.ll_timedtask)
    LinearLayout llTimedtask;
    @BindView(R.id.ll_instanttask)
    LinearLayout llInstanttask;
    @BindView(R.id.ll_alarmtask)
    LinearLayout llAlarmtask;
    @BindView(R.id.tv_alarm_count)
    TextView tvAlarmCount;

    private EventAdapter mEventAdapter;

    private CommonProgressDialog progressDialog;

    private void showMoreWindow(View view) {
        if (null == mMoreWindow) {
            mMoreWindow = new MoreWindow(getActivity());
            mMoreWindow.init();
        }
        mMoreWindow.showMoreWindow(view, 100);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_event;
    }

    @Override
    public void init() {
        initRv();
        if (!AppDataCache.getInstance().getString("userType").equals("00")) {
            showWindow.setVisibility(View.GONE);
        }

        //获取用户列表
        GetAccountList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
    }


    private void initRv() {

        if (TaskUtils.getIsManager()) {
        } else {
            llShowWindow.setVisibility(View.GONE);
        }
        progressDialog = new CommonProgressDialog(getContext());
        mEventAdapter = new EventAdapter(getActivity());
        rvIndexEventList.setHasFixedSize(true);
        rvIndexEventList.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvIndexEventList.setAdapter(mEventAdapter);
    }

    /**
     * 数据回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);

        if ("getSchemeList".equals(baseBean.getType())) {

            String data = baseBean.getData();
            if (data != null) {
                //获取定时任务列表
                GetTaskList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
                AppDataCache.getInstance().putString("getSchemeList", data);
                List<Scheme> schemeList = JSONArray.parseArray(data, Scheme.class);

                //设置打铃方案总数
                tvSchemeCount.setText(schemeList.size() + "套");

                //方案排序
                schemeList = TaskUtils.schemeListOrder(schemeList);

                mEventAdapter.setSchemeDataList(schemeList);   //填充数据
            } else {

            }
        }

        if ("getTaskList".equals(baseBean.getType())) {

            String data = baseBean.getData();
            if (data != null) {

                //获取即时任务列表
                GetInstantTaskList.sendCMD(AppDataCache.getInstance().getString("loginIp"));

                AppDataCache.getInstance().putString("getTaskList", data);
                List<Task> taskListAll = JSONArray.parseArray(data, Task.class);
                List<Task> taskList = new ArrayList<>();
                for (Task task : taskListAll) {
                    if (task.getSchemeNum() == 255) {
                        taskList.add(task);
                    }
                }
                //设置打铃方案总数
                tvTaskCount.setText(taskList.size() + "项");

                //排序
                taskList = TaskUtils.sort(taskList);

                mEventAdapter.setTaskDataList(taskListAll, taskList);   //填充数据
            } else {

            }
        }
        if ("getInstantTaskList".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                //获取设备列表
                GetDeviceList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
                AppDataCache.getInstance().putString("getInstantTaskList", data);
                List<InstantTask> instantTaskList = JSONArray.parseArray(data, InstantTask.class);
                //设置打铃方案总数
                tvInstantCount.setText(instantTaskList.size() + "项");

                instantTaskList = TaskUtils.installTaskListOrder(instantTaskList);

                mEventAdapter.setInstantTaskDataList(instantTaskList);   //填充数据
            } else {

            }
        }

        if ("getDeviceList".equals(baseBean.getType())) {
            final String data = baseBean.getData();
            if (data != null) {
                AppDataCache.getInstance().putString("deviceList", data);
            }

            List<FoundDeviceInfo> deviceInfoList = JSONArray.parseArray(data, FoundDeviceInfo.class);

            List<FoundDeviceInfo> operableDeviceInfoList = new ArrayList();
            String userJson = AppDataCache.getInstance().getString("loginedMsg");
            LoginedInfo userInfo = JSONObject.parseObject(userJson, LoginedInfo.class);
            if (userInfo.getUserType().equals("00")) {
                operableDeviceInfoList.addAll(deviceInfoList);
            } else {
                List<String> list = userInfo.getOperableDeviceMacList();
                for (String mac : list) {
                    for (FoundDeviceInfo device : deviceInfoList) {
                        if (mac.equals(device.getDeviceMac())) {
                            operableDeviceInfoList.add(device);
                        }
                    }
                }
            }

            AppDataCache.getInstance().putString("operableDeviceInfoList", JSONObject.toJSONString(operableDeviceInfoList));
            int alarmTaskSize = 0;
            for (FoundDeviceInfo deviceInfo : deviceInfoList) {
                if ("TX-8623".equals(deviceInfo.getDeviceMedel()) && "在线".equals(deviceInfo.getDeviceStatus())) {
                    alarmTaskSize++;
                }
            }
            //设置报警任务总数
            tvAlarmCount.setText(alarmTaskSize + "项");
        }


        if ("getAccountList".equals(baseBean.getType())) {
            final String data = baseBean.getData();
            //获取方案列表
            GetSchemeList.sendCMD(AppDataCache.getInstance().getString("loginIp"));

            if (data != null) {
                AppDataCache.getInstance().putString("accountList", data);
            }
        }

        if ("editTaskResult".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                EditTaskResult editTaskResult = gson.fromJson(data, EditTaskResult.class);
                if (editTaskResult.getResult() == 0) {
                    ToastUtil.show(getContext(), "操作成功!");
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    GetSchemeList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
                } else {
                    ToastUtil.show(getContext(), "操作失败!");
                }
            } else {
                ToastUtil.show(getContext(), "操作失败!");
            }
        }

        if ("editSchemeResult".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                EditSchemeResult editSchemeResult = gson.fromJson(data, EditSchemeResult.class);
                if (editSchemeResult.getResult() == 1) {
                    ToastUtil.show(getContext(), "操作成功!");
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    GetSchemeList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
                } else {
                    ToastUtil.show(getContext(), "操作失败，请检查数据以及网络!");
                }
            } else {
                ToastUtil.show(getContext(), "操作失败，请检查数据以及网络!");
            }
        }
        if ("editInstantTaskResult".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                EditInstantTaskResult editInstantTaskResult = gson.fromJson(data, EditInstantTaskResult.class);
                if (editInstantTaskResult.getResult() == 1) {
                    ToastUtil.show(getContext(), "操作成功!");
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    GetSchemeList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
                } else {
                    ToastUtil.show(getContext(), "操作失败，请检查数据以及网络!");
                }
            } else {
                ToastUtil.show(getContext(), "操作失败，请检查数据以及网络!");
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.timing_task) {

        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick({R.id.showWindow, R.id.ll_showWindow, R.id.ll_timedtask, R.id.ll_ringing_task, R.id.ll_instanttask, R.id.ll_alarmtask})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.showWindow:
                showMoreWindow(view);
                break;
            case R.id.ll_showWindow:
                showMoreWindow(view);
                break;
            case R.id.ll_timedtask:
                Intent intent = new Intent(getActivity(), TimedTaskActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_ringing_task:
                Intent intent2 = new Intent(getActivity(), ShowRingingTaskActivity.class);
                startActivity(intent2);
                break;
            case R.id.ll_instanttask:
                Intent intent3 = new Intent(getActivity(), InstantTaskActivity.class);
                startActivity(intent3);
                break;
            case R.id.ll_alarmtask:     //报警任务
                Intent intent4 = new Intent(getActivity(), AlarmTaskActivity.class);
                startActivity(intent4);
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