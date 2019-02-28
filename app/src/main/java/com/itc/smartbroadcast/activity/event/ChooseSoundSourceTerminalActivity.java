package com.itc.smartbroadcast.activity.event;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.adapter.ChooseSoundSourceTerminalAdapter;
import com.itc.smartbroadcast.base.Base2Activity;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.bean.LoginedInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.GetDeviceList;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

//功放设备
public class ChooseSoundSourceTerminalActivity extends Base2Activity {
    @BindView(R.id.bt_back_found)
    ImageView btBackFound;
    @BindView(R.id.list_choose_terminal)
    ListView listChooseTerminal;
    @BindView(R.id.tv_save_bind_terminal)
    TextView tvSaveBindTerminal;
    @BindView(R.id.tv_check_size)
    TextView tvCheckSize;
    @BindView(R.id.ll_show)
    LinearLayout llShow;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.bt_filter)
    TextView btFilter;
    @BindView(R.id.ll_filter)
    LinearLayout llFilter;
    private ChooseSoundSourceTerminalAdapter adapter;

    private FoundDeviceInfo foundDeviceInfo = new FoundDeviceInfo();

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == 0) { //配置选择数量
                int size = (int) message.obj;
                tvCheckSize.setText("已选择:" + size);
            }
            return false;
        }
    });

    public void setCheckSize(int size) {
        Message msg = new Message();
        msg.what = 0;
        msg.obj = size;
        handler.sendMessage(msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_choose_terminal);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);
        init();
        llShow.setVisibility(View.GONE);
        tvName.setText("选择音源设备");
        btBackFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = "";
                resultPost(result);
            }
        });
    }


    private void init() {

        String foundDeviceInfoJson = getIntent().getStringExtra("foundDeviceInfo");
        foundDeviceInfo = JSONObject.parseObject(foundDeviceInfoJson, FoundDeviceInfo.class);

        btBackFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        GetDeviceList.sendCMD(AppDataCache.getInstance().getString("loginIp"));

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        final Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);
        if ("getDeviceList".equals(baseBean.getType())) {
            final String data = baseBean.getData();
            Log.i("终端信息》》》", data);
            if (data != null) {

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

                List<FoundDeviceInfo> foundDeviceInfos1 = new ArrayList<>();

                //筛选出功放设备
                for (int i = 0; i < operableDeviceInfoList.size(); i++) {
                    if ((operableDeviceInfoList.get(i).getDeviceMedel().equals("TX-8627") || operableDeviceInfoList.get(i).getDeviceMedel().equals("TX-8628") || operableDeviceInfoList.get(i).getDeviceMedel().equals("TX-8601"))) {
                        foundDeviceInfos1.add(operableDeviceInfoList.get(i));
                    }
                }

                ListView list_terminals = (ListView) findViewById(R.id.list_choose_terminal);


                for (int i = 0; i < foundDeviceInfos1.size(); i++) {
                    if (foundDeviceInfos1.get(i).getDeviceMac().equals(foundDeviceInfo.getDeviceMac())) {
                        foundDeviceInfos1.set(i, foundDeviceInfos1.get(0));
                        foundDeviceInfos1.set(0, foundDeviceInfo);
                    }
                }

                adapter = new ChooseSoundSourceTerminalAdapter(foundDeviceInfos1, foundDeviceInfo, ChooseSoundSourceTerminalActivity.this);
                list_terminals.setAdapter(adapter);
            }
        }
        tvSaveBindTerminal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                 *循环打印选中checkBox的值
                 *通过adapter.getCheckBoxIDList()获取所选checkBox值的集合
                 */
                FoundDeviceInfo checkTerminal = adapter.getCheckBoxIDList();
                String deviceStr = gson.toJson(checkTerminal);
                resultPost(deviceStr);

            }
        });
    }

    /**
     * 监听Back键按下事件,方法2:
     * 注意:
     * 返回值表示:是否能完全处理该事件
     * 在此处返回false,所以会继续传播该事件.
     * 在具体项目中此处的返回值视情况而定.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            String result = "";
            resultPost(result);
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    private void resultPost(String result) {
        //数据是使用Intent返回
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("device", result);
        //设置返回数据
        ChooseSoundSourceTerminalActivity.this.setResult(RESULT_OK, intent);
        //关闭Activity
        ChooseSoundSourceTerminalActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}