package com.itc.smartbroadcast.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.MainActivity;
import com.itc.smartbroadcast.adapter.MyAdapter;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.CloudProjectListInfo;
import com.itc.smartbroadcast.bean.LoginedInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.http.CloudProtocolModel;
import com.itc.smartbroadcast.channels.protocolhandler.GetLoginedMsg;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.dao.ResultDB;
import com.itc.smartbroadcast.util.StringUtil;
import com.itc.smartbroadcast.util.ToastUtil;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SwitchProjectActivity extends AppCompatActivity {

    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R.id.rl_create_project)
    RelativeLayout rlCreateProject;
    @BindView(R.id.list_choose_project)
    ListView listChooseProject;


    String projectIp;

    private MyAdapter<CloudProjectListInfo.DataBeanX.DataBean> cloudProjectAdapter = null;
    private List<CloudProjectListInfo.DataBeanX.DataBean> cloudProjectData = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_project);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);
        CloudProtocolModel.getCloudProjectList(this);
    }

    @Override
    public void onResume() {
        EventBus.getDefault().register(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);
        Log.i("获取项目列表页面json>>>>", json);
        BaseBean baseBean1 = gson.fromJson(baseBean.getData(), BaseBean.class);
        if ("cloudgetProjectList".equals(baseBean.getType())) {
            if ("success".equals(baseBean1.getType())) {
                Log.i("项目列表>>>", baseBean1.getData());
                CloudProjectListInfo cloudProjectListInfo = gson.fromJson(baseBean1.getData(), CloudProjectListInfo.class);
                final CloudProjectListInfo.DataBeanX dataBeans = cloudProjectListInfo.getData();
                final List<CloudProjectListInfo.DataBeanX.DataBean> dataBeans1 = dataBeans.getData_();
                cloudProjectData = new ArrayList<>();
                cloudProjectData.addAll(dataBeans1);
                cloudProjectAdapter = new MyAdapter<CloudProjectListInfo.DataBeanX.DataBean>((ArrayList) cloudProjectData, R.layout.item_choose_project) {
                    @Override
                    public void bindView(ViewHolder holder, CloudProjectListInfo.DataBeanX.DataBean obj) {
                        holder.setText(R.id.tv_project_name, obj.getName());
                        holder.setText(R.id.tv_project_mac, obj.getMac());
                    }
                };
                listChooseProject.setAdapter(cloudProjectAdapter);
                listChooseProject.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        CloudProjectListInfo.DataBeanX.DataBean dataBean = dataBeans1.get(position);
                        //List<CloudProjectListInfo.DataBeanX.DataBean> ss = dataBean.getData_();
                        String projectName = dataBean.getName();
                        String projectMac = dataBean.getMac();
                        String projectMacCode = dataBean.getMachine_code();
                        int projectId = dataBean.getId();
                        projectIp = dataBean.getIp();
                        if (projectMac.replaceAll("-","").equals(SmartBroadcastApplication.cloudMacAddress)){
                            ToastUtil.show(SwitchProjectActivity.this,"已处于当前项目，无法切换！");
                        }else {
                            SmartBroadcastApplication.isCloud = true;
                            SmartBroadcastApplication.cloudMacAddress = projectMac.replaceAll("-", "");
                            GetLoginedMsg.sendCloudCMD(projectIp, AppDataCache.getInstance().getString("userPhoneNum"), 0);
                        }
                    }
                });
            }
        }else {
            ToastUtil.show(this,"获取项目列表失败");
        }

        if ("getLoginedMsg".equals(baseBean.getType())) {
            LoginedInfo loginedInfo = gson.fromJson(baseBean.getData(), LoginedInfo.class);
            //登录状态
            String loginState = loginedInfo.getLoginState();
            if ("00".equals(loginState)) {
                // 登录成功后缓存用户输入信息
                ResultDB.getInstance(this).saveIP(projectIp);
                AppDataCache.getInstance().putString("loginedMsg", baseBean.getData());
                AppDataCache.getInstance().putString("loginIp", StringUtil.isEmpty(projectIp) ? "" : projectIp);
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
                //启动心跳包
                NettyUdpClient.sendHeartBeatCMD(AppDataCache.getInstance().getString("loginIp"), AppDataCache.getInstance().getInt("userNum"));
                Intent it = new Intent(this, MainActivity.class);
                startActivity(it);
                finish();
            } else if ("01".equals(loginState)) {
                ToastUtil.show(this, "找不到指定账户");
            } else {
                ToastUtil.show(this, "密码错误");
            }
        }

    }


    @OnClick({R.id.rl_back, R.id.rl_create_project})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.rl_create_project:
                startActivity(new Intent(this,CreateProjectActivity.class));
                break;
        }
    }
}
