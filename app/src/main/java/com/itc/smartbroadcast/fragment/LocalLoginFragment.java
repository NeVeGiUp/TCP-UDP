package com.itc.smartbroadcast.fragment;


import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.MainActivity;
import com.itc.smartbroadcast.activity.personal.AuthorizationActivity;
import com.itc.smartbroadcast.activity.personal.PersonalPageActivity;
import com.itc.smartbroadcast.application.SmartBroadcastApplication;
import com.itc.smartbroadcast.bean.AccManageInfo;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.CloudAuthorizationInfo;
import com.itc.smartbroadcast.bean.LoginedInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.http.CloudProtocolModel;
import com.itc.smartbroadcast.channels.protocolhandler.EditAccountManage;
import com.itc.smartbroadcast.channels.protocolhandler.GetLoginedMsg;
import com.itc.smartbroadcast.channels.udp.NettyUdpClient;
import com.itc.smartbroadcast.dao.ResultDB;
import com.itc.smartbroadcast.util.AppUtil;
import com.itc.smartbroadcast.util.StringUtil;
import com.itc.smartbroadcast.util.ToastUtil;
import com.itc.smartbroadcast.widget.custom.ClearEditText;
import com.itc.smartbroadcast.widget.custom.CommonProgressDialog;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


//create by youmu on 2018/12
public class LocalLoginFragment extends Fragment {

    @BindView(R.id.login_ip_et)
    AutoCompleteTextView loginIpEt;
    @BindView(R.id.matv_content)
    MultiAutoCompleteTextView matvContent;
    @BindView(R.id.login_username_et)
    ClearEditText loginUsernameEt;
    @BindView(R.id.login_userpsw_et)
    ClearEditText loginUserpswEt;
    @BindView(R.id.login_up_btn)
    Button loginUpBtn;
    @BindView(R.id.btn_login_cloud)
    Button btnLoginCloud;
    @BindView(R.id.forget_psw_tv)
    TextView forgetPswTv;
    @BindView(R.id.version_tv)
    TextView versionTv;
    Unbinder unbinder;

    private String mLoginIp;
    private String mLoginUsername;
    private String mLoginUserpsw;
    private CommonProgressDialog progressDialog;
    String str = "";

    private List<String> ipList;
    private String[] listData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_login, container, false);
        unbinder = ButterKnife.bind(this, view);

        ipList = ResultDB.getInstance(getActivity()).getIp();
        listData = ipList.toArray(new String[ipList.size()]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_dropdown_item_1line, listData);
        loginIpEt.setAdapter(adapter);

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_dropdown_item_1line, listData);
        matvContent.setAdapter(adapter);
        matvContent.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());

        initEdit();
        initview();



        return view;
    }

    private void initview() {
        loginIpEt.setText(AppDataCache.getInstance().getString("loginIp"));
        loginUsernameEt.setText(AppDataCache.getInstance().getString("loginUsername"));
        loginUserpswEt.setText(AppDataCache.getInstance().getString("loginPsw"));
    }

    private void initEdit() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        loginIpEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (loginUsernameEt.getText().length() < 2 || loginUserpswEt.getText().length() < 6 || loginIpEt.getText().length() < 7) {
//                    loginUpBtn.setEnabled(false);
                    loginUpBtn.setBackground(getResources().getDrawable(R.drawable.bg_login_unclick_btn));
                } else {
                    loginUpBtn.setEnabled(true);
                    loginUpBtn.setBackground(getResources().getDrawable(R.drawable.loginout));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        loginUsernameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (loginUsernameEt.getText().length() < 2 || loginUserpswEt.getText().length() < 6 || loginIpEt.getText().length() < 7) {
//                    loginUpBtn.setEnabled(false);
                    loginUpBtn.setBackground(getResources().getDrawable(R.drawable.bg_login_unclick_btn));
                } else {
                    loginUpBtn.setEnabled(true);
                    loginUpBtn.setBackground(getResources().getDrawable(R.drawable.loginout));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        loginUserpswEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (loginUserpswEt.getText().length() < 6 || loginUsernameEt.getText().length() < 2 || loginIpEt.getText().length() < 7) {
                    loginUpBtn.setBackground(getResources().getDrawable(R.drawable.bg_login_unclick_btn));
                } else {
                    loginUpBtn.setEnabled(true);
                    loginUpBtn.setBackground(getResources().getDrawable(R.drawable.loginout));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    @OnClick({R.id.login_up_btn, R.id.forget_psw_tv, R.id.version_tv, R.id.btn_login_cloud})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.login_up_btn:
                try {
                    SmartBroadcastApplication.isCloud = false;
                    login();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.forget_psw_tv:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View v = View.inflate(getActivity(), R.layout.dialog_tips, null);
                final TextView tvMsg = (TextView) v.findViewById(R.id.tv_msg);
                final Button btnOk = (Button) v.findViewById(R.id.btn_ok);
                final Button btnNo = (Button) v.findViewById(R.id.btn_no);
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getWindow().setContentView(v);
                tvMsg.setText("请联系管理员修改密码");
                btnNo.setVisibility(View.VISIBLE);
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
                break;
            case R.id.version_tv:
                //startActivity(new Intent(this,TestActivity.class));
//                startActivity(new Intent(this,LoginActivity.class));
                break;
            case R.id.btn_login_cloud:
                SmartBroadcastApplication.isCloud = true;
                try {
                    login();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    //登录
    private void login() throws Exception {

        if (!AppUtil.isNetworkAvailable(getActivity())) {//当前页面判断是否有网络
            Toast.makeText(getActivity(), "网络未连接，请检查网络", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!checkLogin()) return;
        //登录进度条 服务器无响应15s后去除dialog，或者重发登录请求
        progressDialog = new CommonProgressDialog(getActivity());  //登录进度条
//        progressDialog.show();
        //发送登录请求
        GetLoginedMsg.sendCMD(mLoginIp, mLoginUsername, mLoginUserpsw, "00");
        //登录时长计时器
//        handler.postDelayed(runnable, 0);
    }

    private boolean checkLogin() {
        mLoginIp = loginIpEt.getText().toString().trim();
        mLoginUsername = loginUsernameEt.getText().toString().trim();
        mLoginUserpsw = loginUserpswEt.getText().toString().trim();
        // 检查服务器ip
        if (StringUtil.isEmpty(mLoginIp)) {
            Toast.makeText(getActivity(), R.string.login_input_ip_is_empty, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (!AppUtil.ipCheck(mLoginIp)) {
                Toast.makeText(getActivity(), R.string.login_input_ok_ip, Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (TextUtils.isEmpty(mLoginUsername)) {
            Toast.makeText(getActivity(), "账号为空，请先填写账号!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mLoginUsername.length() < 2) {
            Toast.makeText(getActivity(), "请输入2-15位用户名", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(mLoginUserpsw)) {
            Toast.makeText(getActivity(), "密码为空，请先填写密码!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mLoginUserpsw.length() != 6) {
            Toast.makeText(getActivity(), "请输入6位密码!", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * 登录成功数据回调
     **/
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        if (json == null)
            return;

        Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);
        if ("getLoginedMsg".equals(baseBean.getType())) {
            LoginedInfo loginedInfo = gson.fromJson(baseBean.getData(), LoginedInfo.class);
            //登录状态
            String loginState = loginedInfo.getLoginState();
            if ("00".equals(loginState)) {
                // 登录成功后缓存用户输入信息
                if (loginedInfo.getRegisterState().equals("00")){
                    ResultDB.getInstance(getActivity()).saveIP(mLoginIp);
                    AppDataCache.getInstance().putString("loginedMsg", baseBean.getData());
                    AppDataCache.getInstance().putString("loginIp", StringUtil.isEmpty(mLoginIp) ? "" : mLoginIp);
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
                    Intent it = new Intent(getActivity(), MainActivity.class);
//                    Intent it = new Intent(getActivity(), AuthorizationActivity.class);
                    startActivity(it);
                }else {
                    ResultDB.getInstance(getActivity()).saveIP(mLoginIp);
                    AppDataCache.getInstance().putString("loginedMsg", baseBean.getData());
                    AppDataCache.getInstance().putString("loginIp", StringUtil.isEmpty(mLoginIp) ? "" : mLoginIp);
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
                    Intent it = new Intent(getActivity(), MainActivity.class);
                    startActivity(it);
                    Objects.requireNonNull(getActivity()).finish();
                }
            } else if ("01".equals(loginState)) {
                ToastUtil.show(getActivity(), "找不到指定账户");
            } else {
                ToastUtil.show(getActivity(), "密码错误");
            }
        }


        //云授权
        if ("cloudAuthorization".equals(baseBean.getType())) {
            BaseBean innerData = gson.fromJson(baseBean.getData(), BaseBean.class);
            if ("success".equals(innerData.getType())) {
                ToastUtil.show(getActivity(), "云授权成功");
                //具体数据
                CloudAuthorizationInfo cloudAuthorizationInfo = JSON.parseObject(innerData.getData(), CloudAuthorizationInfo.class);
            } else {
                ToastUtil.show(getActivity(), "云授权失败");
            }
        }
    }

    //关闭handle
    final Handler handlerStop = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    handler.removeCallbacks(runnable);
                    recLen = 0;
                    break;
            }
            super.handleMessage(msg);
        }

    };

    private int recLen = 0;
    Handler handler = new Handler();

    //开始计时
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            recLen++;
            handler.postDelayed(this, 1000);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //五秒后定时器无数据返回则提示登录失败
                    if (recLen == 6) {
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        ToastUtil.show(getActivity(), "登录失败！");
                        Message message = new Message();
                        message.what = 1;
                        handlerStop.sendMessage(message);
                    }
                }
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
