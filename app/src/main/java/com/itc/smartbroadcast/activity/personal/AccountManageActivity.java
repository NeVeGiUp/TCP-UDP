package com.itc.smartbroadcast.activity.personal;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.adapter.MyAdapter;
import com.itc.smartbroadcast.bean.AccManageInfo;
import com.itc.smartbroadcast.bean.AccountListInfo;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditAccManageResult;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.bean.MusicFolderInfo;
import com.itc.smartbroadcast.bean.OperatorDeviceListInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.EditAccountManage;
import com.itc.smartbroadcast.channels.protocolhandler.EditPartition;
import com.itc.smartbroadcast.channels.protocolhandler.GetAccountList;
import com.itc.smartbroadcast.channels.protocolhandler.GetDeviceList;
import com.itc.smartbroadcast.channels.protocolhandler.GetOperatorDeviceList;
import com.itc.smartbroadcast.helper.CustomDialog;
import com.itc.smartbroadcast.util.CharacterParser;
import com.itc.smartbroadcast.util.StringUtil;
import com.itc.smartbroadcast.util.ToastUtil;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * create by youmu on 2018/7
 */

public class AccountManageActivity extends AppCompatActivity {
    @BindView(R.id.bt_back_personal)
    RelativeLayout btBack;
    @BindView(R.id.showWindow)
    RelativeLayout btAddAccount;
    @BindView(R.id.ll_all_view)
    LinearLayout llAllView;
    @BindView(R.id.list_accountmanage)
    ListView listAccountmanage;
    private List<String> deviceMacList;
    private Context mContext;
    private MyAdapter<AccountListInfo.AccountDataInner> AmAdapter = null;
    private List<AccountListInfo.AccountDataInner> AmData = null;
    private BaseBean baseBean;
    List<FoundDeviceInfo> deviceInfoList;
    private String accName, accType, accPsw,accPhone;
    private int accNum;
    List<OperatorDeviceListInfo> macList;
    private OperatorDeviceListInfo operatorDeviceListInfo;
    private List<AccountListInfo.AccountDataInner> infoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountmanage);
        ButterKnife.bind(this);
        //EventBus.getDefault().register(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);
        mContext = AccountManageActivity.this;
        listAccountmanage.setClickable(true);
        listAccountmanage.setFocusable(true);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                for (int i=0;i<25;i++){
//                    AccManageInfo manageInfo = new AccManageInfo();
//                    ArrayList<String> macList = new ArrayList<>();
//                    macList.add("000000");
//                    manageInfo.setAccName(i+"");
//                    manageInfo.setAccAuthority("00");
//                    manageInfo.setAccPsw("123456");
//                    manageInfo.setAccDeviceCount(1);
//                    manageInfo.setAccMacList(macList);
//                    manageInfo.setUserPhoneNum("11011011011");
//                    EditAccountManage.sendCMD(AppDataCache.getInstance().getString("loginIp"), manageInfo, 0);
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
                if (infoList == null){
                    Intent intent = new Intent(AccountManageActivity.this, CreateAccountActivity.class);
                    startActivity(intent);
                }else if (infoList.size()>29){
                    ToastUtil.show(AccountManageActivity.this,"不能创建更多账户！");
                }else {
                    Intent intent = new Intent(AccountManageActivity.this, CreateAccountActivity.class);
                    startActivity(intent);
                }
            }
        });
    }


    private void init() {
        GetAccountList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        Gson gson = new Gson();
        baseBean = gson.fromJson(json, BaseBean.class);
        if ("getAccountList".equals(baseBean.getType())) {
            String data = baseBean.getData();
            Log.i("用户信息》》》", data);
            if (data != null) {
                infoList = JSONArray.parseArray(data, AccountListInfo.AccountDataInner.class);
                for (AccountListInfo.AccountDataInner acc : infoList){
                    System.out.println(acc.getAccountName()+"  "+acc.getAccountNum());
                }
                infoList.remove(0);
                Collections.sort(infoList, new Comparator<AccountListInfo.AccountDataInner>() {
                    @Override
                    public int compare(AccountListInfo.AccountDataInner t1, AccountListInfo.AccountDataInner t2) {
                        String s1 = CharacterParser.getInstance().getSelling(t1.getAccountName());
                        String s2 = CharacterParser.getInstance().getSelling(t2.getAccountName());
                        return s1.compareTo(s2);
                    }
                });
                Log.i("data>>>", data);
                AmData = new ArrayList<>();
                AmData.addAll(infoList);
                AmAdapter = new MyAdapter<AccountListInfo.AccountDataInner>((ArrayList) AmData, R.layout.item_account) {
                    @Override
                    public void bindView(ViewHolder holder, AccountListInfo.AccountDataInner obj) {
                        holder.setText(R.id.tv_username, obj.getAccountName());
                        switch (obj.getAccountType()) {
                            case "00":
                                holder.setText(R.id.tv_usertype, "管理员");
                                break;
                            case "01":
                                holder.setText(R.id.tv_usertype, "普通用户");
                                break;
                        }
                        switch (obj.getAccountType()) {
                            case "00":
                                holder.setText(R.id.tv_terminal_amount, "所有");
                                break;
                            case "01":
                                holder.setText(R.id.tv_terminal_amount, obj.getAccountDeviceTotal() + "");
                                break;
                        }

                    }
                };
                listAccountmanage.setAdapter(AmAdapter);
                listAccountmanage.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        listAccountmanage.setClickable(false);
                        listAccountmanage.setFocusable(false);
                        AccountListInfo.AccountDataInner accManageInfo = infoList.get(position);
                        accName = accManageInfo.getAccountName();
                        accType = accManageInfo.getAccountType();
                        accPsw = accManageInfo.getAccountPsw();
                        accNum = accManageInfo.getAccountNum();
                        accPhone = accManageInfo.getAccountPhoneNum();
                        GetOperatorDeviceList.sendCMD(AppDataCache.getInstance().getString("loginIp"), accNum);
                    }
                });
                listAccountmanage.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                        deleteAcc(position);
                        return true;
                    }
                });
            }
        }
        if ("getDeviceList".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                deviceInfoList = JSONArray.parseArray(data, FoundDeviceInfo.class);
                List<FoundDeviceInfo> deviceList = new ArrayList<>();
                for (String macList3 : deviceMacList) {
                    operatorDeviceListInfo.setAccDeviceList(deviceList);
                    for (FoundDeviceInfo foundDeviceInfo3 : deviceInfoList) {
                        if (macList3.contains(foundDeviceInfo3.getDeviceMac())) {
                            operatorDeviceListInfo.getAccDeviceList().add(foundDeviceInfo3);
                        }
                    }
                }
                Intent intent = new Intent(AccountManageActivity.this, EditPartition.class);
                intent.setClass(AccountManageActivity.this, EditAccountActivity.class);
                List<FoundDeviceInfo> deviceOfPart = operatorDeviceListInfo.getAccDeviceList();
                String sDeviceOfPart = gson.toJson(deviceOfPart);
                Log.i("DeviceOfPart>>>", sDeviceOfPart);
                if (StringUtil.isEmpty(sDeviceOfPart)){
                }else {
                    intent.putExtra("AccDevice", sDeviceOfPart);
                }
                intent.putExtra("AccNum", accNum);
                intent.putExtra("AccName", accName);
                intent.putExtra("AccType", accType);
                intent.putExtra("AccPsw", accPsw);
                intent.putExtra("AccPhone",accPhone);
                startActivity(intent);
            }
        }
        if ("getOperatorDeviceList".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                GetDeviceList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
                String ss = data;
                data = "[" + data;
                data = data + "]";
                macList = JSONArray.parseArray(data, OperatorDeviceListInfo.class);
                operatorDeviceListInfo = JSON.parseObject(ss, OperatorDeviceListInfo.class);
                deviceMacList = operatorDeviceListInfo.getOperableDeviceMacList();
            }
        }
        if ("editAccountManage".equals((baseBean.getType()))) {
            EditAccManageResult editAccManageResult = gson.fromJson(baseBean.getData(), EditAccManageResult.class);
            String isSucceed = editAccManageResult.getConfigureState();
            String operator = editAccManageResult.getAccOperator();
            if (operator.equals("00")) {
                if (isSucceed.equals("00")) {
                    ToastUtil.show(AccountManageActivity.this, "创建失败");
                } else if (isSucceed.equals("01")) {
                    ToastUtil.show(AccountManageActivity.this, "创建成功");
                }
            }
            if (operator.equals("01")) {
                if (isSucceed.equals("00")) {
                    ToastUtil.show(AccountManageActivity.this, "修改失败");
                } else if (isSucceed.equals("01")) {
                    ToastUtil.show(AccountManageActivity.this, "修改成功");
                }
            }
            if (operator.equals("02")) {
                if (isSucceed.equals("00")) {
                    ToastUtil.show(AccountManageActivity.this, "删除失败");
                } else if (isSucceed.equals("01")) {
                    ToastUtil.show(AccountManageActivity.this, "删除成功");
                    init();
                }
            }
        }
    }

    private void deleteAcc(final int position) {
        Vibrator vb = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
        vb.vibrate(100);
        CustomDialog dialog = new CustomDialog(AccountManageActivity.this, "提示", "确定要删除当前账号？",
                "取消", "确认", new CustomDialog.OnOkClickListener() {
            @Override
            public void onClick() {
                AccountListInfo.AccountDataInner accManageInfo = infoList.get(position);
                AccManageInfo manageInfo3 = new AccManageInfo();
                ArrayList<String> macList3 = new ArrayList<>();
                int accNum = accManageInfo.getAccountNum();
                macList3.add("42-4c-45-00-cc-01");
                manageInfo3.setAccNum(accNum);
                Log.i("删除的账户ID",accNum+""+position);
                manageInfo3.setAccName("lghandroid");
                manageInfo3.setAccAuthority("01");
                manageInfo3.setAccPsw("123456");
                manageInfo3.setUserPhoneNum("");
                manageInfo3.setAccDeviceCount(1);
                manageInfo3.setAccMacList(macList3);
                EditAccountManage.sendCMD(AppDataCache.getInstance().getString("loginIp"), manageInfo3, 2);
                AmAdapter.notifyDataSetChanged();
            }
        });
        dialog.show();
    }

    @Override
    protected void onResume() {
        init();
        EventBus.getDefault().register(this);
        super.onResume();
    }


    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        //EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}