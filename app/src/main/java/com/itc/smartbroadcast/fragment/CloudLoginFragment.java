package com.itc.smartbroadcast.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.login.ChooseProjectActivity;
import com.itc.smartbroadcast.activity.login.RegisterActivity;
import com.itc.smartbroadcast.activity.login.RetrievePasswordActivity;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.CloudLoginInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.http.CloudProtocolModel;
import com.itc.smartbroadcast.util.ToastUtil;
import com.itc.smartbroadcast.widget.custom.ClearEditText;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


//create by youmu on 2018/12
public class CloudLoginFragment extends Fragment {


    @BindView(R.id.et_Phone)
    ClearEditText etPhone;
    @BindView(R.id.et_password)
    ClearEditText etPassword;
    @BindView(R.id.bt_login)
    Button btLogin;
    @BindView(R.id.tv_reg)
    TextView tvReg;
    @BindView(R.id.forget_psw_tv)
    TextView forgetPswTv;
    Unbinder unbinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cloud_login, container, false);
        unbinder = ButterKnife.bind(this, view);
        //CloudProtocolModel.getCloudProjectList(getActivity());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        EventBus.getDefault().register(this);

        etPhone.setText(AppDataCache.getInstance().getString("userPhoneNum"));
        etPassword.setText(AppDataCache.getInstance().getString("userPass"));

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
        String baseData =  baseBean.getData();
        Log.i("云登录页面json>>>>", json.replaceAll("/",""));
        Log.i("云登录>>>", baseData);
        switch (baseBean.getType()) {
            case "cloudLogin":
                if (baseData.contains("failed")) {
                    ToastUtil.show(getContext(), "登录失败");
                } else {
                    BaseBean baseBean1 = gson.fromJson(baseData, BaseBean.class);
                    if (!"success".equals(baseBean1.getType())) {
                        ToastUtil.show(getContext(), "登录失败");
                    } else {
                        Log.i("登录信息>>>",baseBean1.getData());
                        CloudLoginInfo cloudLoginInfo = JSON.parseObject(baseBean1.getData(),CloudLoginInfo.class);
                        CloudLoginInfo.Data data = cloudLoginInfo.getData();
                        CloudLoginInfo.Data.UserInfo userInfo = data.getUserInfo();
                        if (cloudLoginInfo.getStatus().equals("success")){
                            AppDataCache.getInstance().putString("cloudLogined",data.getToken());
                            AppDataCache.getInstance().putString("userPhoneNum",userInfo.getMobile());
                            AppDataCache.getInstance().putString("userPass",etPassword.getText().toString().trim());
                            Log.i("用户信息>>>",userInfo.getEmail()+" "+userInfo.getMobile()+" "+ userInfo.getUsername()+" "+userInfo.getId());
                            startActivity(new Intent(getActivity(),ChooseProjectActivity.class));
                        }else {
                            ToastUtil.show(getActivity(),cloudLoginInfo.getMessage());
                        }

                    }
                }
                break;
        }
    }


    @OnClick({R.id.bt_login, R.id.tv_reg, R.id.forget_psw_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_login:
                CloudProtocolModel.getCloudLoginedMsg(etPhone.getText().toString(),etPassword.getText().toString(),getContext());
                break;
            case R.id.tv_reg:
                startActivity(new Intent(getActivity(),RegisterActivity.class));
                break;
            case R.id.forget_psw_tv:
                startActivity(new Intent(getActivity(),RetrievePasswordActivity.class));
                break;
        }
    }
}
