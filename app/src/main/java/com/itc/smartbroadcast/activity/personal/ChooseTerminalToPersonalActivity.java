package com.itc.smartbroadcast.activity.personal;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.adapter.ChooseAmplifierTerminalAdapter;
import com.itc.smartbroadcast.adapter.ChooseTerminalAdapterToTask;
import com.itc.smartbroadcast.base.Base2Activity;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.bean.FoundPartitionInfo;
import com.itc.smartbroadcast.bean.LoginedInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.GetDeviceList;
import com.itc.smartbroadcast.channels.protocolhandler.GetPartitionList;
import com.itc.smartbroadcast.util.ToastUtil;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

//功放设备
public class ChooseTerminalToPersonalActivity extends Base2Activity {
    @BindView(R.id.bt_back_found)
    ImageView btBackFound;
    @BindView(R.id.list_choose_terminal)
    ListView listChooseTerminal;
    @BindView(R.id.tv_save_bind_terminal)
    TextView tvSaveBindTerminal;
    @BindView(R.id.tv_check_size)
    TextView tvCheckSize;
    @BindView(R.id.ll_filter)
    LinearLayout llFilter;

    private String deviceIndex = "0";
    private ChooseTerminalToPersonalAdapter adapter;

    private String deviceListJson = "";

    private List<FoundDeviceInfo> deviceList = null;

    private List<FoundDeviceInfo> adapterList;

    private Context mContext;
    private SimpleAdapter simpleAdapter;

    private List<FoundPartitionInfo> foundPartitionInfoList;
    private static String fristDeviceList;
    private static List<FoundDeviceInfo> fristList;
    private List<FoundDeviceInfo> deviceListed = new ArrayList<>();

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            if (message.what == 0) { //配置选择数量
                int size = (int) message.obj;
                tvCheckSize.setText("已选择:" + size);
                //tvCheckSize.setVisibility(View.INVISIBLE);
            }
            return false;
        }
    });

    public void setCheckSize(int size) {
        List<FoundDeviceInfo> deviceInfos = new ArrayList<>();
        List<FoundDeviceInfo> deviceInfos1 = new ArrayList<>();

        deviceInfos1.addAll(deviceListed);

        List<FoundDeviceInfo> checkBoxIDList = adapter.getCheckBoxIDList();
        List<FoundDeviceInfo> list = adapter.getList();

        for (FoundDeviceInfo device : deviceListed) {
            boolean bol = false;
            for (FoundDeviceInfo device1 : list) {
                if (device.getDeviceMac().equals(device1.getDeviceMac())) {
                    bol = true;
                }
            }
            if (bol) {
                deviceInfos.add(device);
            }
        }

        for (FoundDeviceInfo device : checkBoxIDList) {
            boolean bol = false;
            for (FoundDeviceInfo device1 : deviceInfos) {
                if (device.getDeviceMac().equals(device1.getDeviceMac())) {
                    bol = true;
                }
            }
            if (!bol) {
                deviceListed.add(device);
            }
        }
        for (FoundDeviceInfo device : deviceInfos) {
            boolean bol = false;
            for (FoundDeviceInfo device1 : checkBoxIDList) {
                if (device.getDeviceMac().equals(device1.getDeviceMac())) {
                    bol = true;
                }
            }
            if (!bol) {
                deviceListed.remove(device);
            }
        }

        Message msg = new Message();
        msg.what = 0;
        msg.obj = deviceListed.size();
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

        btBackFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Gson gson = new Gson();
                String deviceStr = gson.toJson(fristList);
                resultPost(deviceStr);
            }
        });

        llFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPopWindow(view);
            }
        });
    }


    private void init() {

        mContext = this;
        deviceIndex = getIntent().getStringExtra("index");
        deviceListJson = getIntent().getStringExtra("deviceList");
        fristDeviceList = getIntent().getStringExtra("deviceList");
        if (deviceListJson != null && !deviceListJson.equals("")) {
            deviceList = JSONArray.parseArray(deviceListJson, FoundDeviceInfo.class);
        }
        if (fristDeviceList != null && !fristDeviceList.equals("")) {
            fristList = JSONArray.parseArray(fristDeviceList, FoundDeviceInfo.class);
        }
        tvCheckSize.setText("已选择:"+fristList.size());
        deviceListed.addAll(deviceList);

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

        if ("getPartitionList".equals(baseBean.getType())) {
            final String data = baseBean.getData();
            if (data != null) {
                foundPartitionInfoList = JSONArray.parseArray(data, FoundPartitionInfo.class);
            }
        }



        if ("getDeviceList".equals(baseBean.getType())) {
            final String data = baseBean.getData();
            Log.i("终端信息》》》", data);

            if (data != null) {
                GetPartitionList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
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
                  //  if ((operableDeviceInfoList.get(i).getDeviceMedel().equals("TX-8660") || operableDeviceInfoList.get(i).getDeviceMedel().equals("TX-8607"))) {
                        foundDeviceInfos1.add(operableDeviceInfoList.get(i));
                   // }
                }
                ListView list_terminals = (ListView) findViewById(R.id.list_choose_terminal);

                adapterList = new ArrayList<>();
                adapterList.addAll(deviceList);
                for (FoundDeviceInfo device : foundDeviceInfos1) {
                    boolean bol = true;
                    for (FoundDeviceInfo device1 : deviceList) {
                        if (device.getDeviceMac().equals(device1.getDeviceMac())) {
                            bol = false;
                        }
                    }
                    if (bol) {
                        adapterList.add(device);
                    }
                }

//                List<FoundDeviceInfo> adapterList1 = new ArrayList<>();
//                for (int i = 0; i < 40; i++) {
//                    FoundDeviceInfo device = new FoundDeviceInfo();
//                    device.setDeviceName("终端设备"+i);
//                    device.setDeviceIp("172.16.13."+i);
//                    device.setDeviceMac("AABBCC0011"+i);
//                    adapterList1.add(device);
//
//                }

                adapter = new ChooseTerminalToPersonalAdapter(adapterList, deviceList, ChooseTerminalToPersonalActivity.this, ChooseTerminalToPersonalActivity.this);
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
//                List<FoundDeviceInfo> checkTerminal = adapter.getCheckBoxIDList();
                if (deviceIndex==null){
                    String deviceStr = gson.toJson(deviceListed);
                    resultPost(deviceStr);
                }else if (deviceIndex.equals("1")){
                    if (deviceListed.size()==1){
                        String deviceStr = gson.toJson(deviceListed);
                        resultPost(deviceStr);
                    }else {
                        ToastUtil.show(ChooseTerminalToPersonalActivity.this,"目前只支持选择一个设备");
                    }
                }
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
            Gson gson = new Gson();
            String deviceStr = gson.toJson(fristList);
            resultPost(deviceStr);
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    private void resultPost(String result) {
        //数据是使用Intent返回
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("deviceList", result);
        //设置返回数据
        ChooseTerminalToPersonalActivity.this.setResult(RESULT_OK, intent);
        //关闭Activity
        ChooseTerminalToPersonalActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    private void initPopWindow(View v) {

        final int[] position1 = {0};

        View view = LayoutInflater.from(mContext).inflate(R.layout.pop_choose_device, null, false);
        //1.构造一个PopupWindow，参数依次是加载的View，宽高
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);

        popWindow.setAnimationStyle(R.anim.anim_pop);  //设置加载动画
        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        popWindow.setBackgroundDrawable(new ColorDrawable(0xFFFFFF));
        popWindow.showAsDropDown(v, 30, 0);
//		mraRatingBar.setEnabled(false);
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = 0.7f;
        getWindow().setAttributes(lp);
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });


        GridView gvDeviceType = (GridView) view.findViewById(R.id.gv_device_type);
        Button btnFilter = (Button) view.findViewById(R.id.btn_filter);


        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popWindow.dismiss();
                Log.i("aaaaabbbbb", "onClick: " + position1[0]);
                if (position1[0] == 0) {
                    adapter.setList(adapterList, deviceListed);
                } else {

                    FoundPartitionInfo foundPartitionInfo = foundPartitionInfoList.get(position1[0] - 1);
                    List<FoundDeviceInfo> deviceInfos = new ArrayList<>();
                    for (FoundDeviceInfo device : adapterList) {
                        ArrayList<Integer> deviceZoneMsg = device.getDeviceZoneMsg();
                        boolean bol = false;
                        for (int zone : deviceZoneMsg) {
                            if (zone == Integer.parseInt(foundPartitionInfo.getPartitionNum())) {
                                bol = true;
                            }
                        }
                        if (bol){
                            deviceInfos.add(device);
                        }

                    }
                    adapter.setList(deviceInfos,deviceListed);
                }


            }
        });


        final String[] deviceTypeItem = new String[foundPartitionInfoList.size() + 1];

        deviceTypeItem[0] = "全部";

        for (int i = 0; i < foundPartitionInfoList.size(); i++) {
            deviceTypeItem[i + 1] = foundPartitionInfoList.get(i).getName();
        }

        List<Map<String, Object>> listItems = new ArrayList<>();
        for (int i = 0; i < deviceTypeItem.length; ++i) {
            Map<String, Object> listItem = new HashMap<>();
            listItem.put("deviceType", deviceTypeItem[i]);
            listItems.add(listItem);
        }
        simpleAdapter = new SimpleAdapter(mContext, listItems, R.layout.item_filter_btn,
                new String[]{"deviceType"}, new int[]{R.id.item_type});
        gvDeviceType.setAdapter(simpleAdapter);
        gvDeviceType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                for (int i = 0; i < adapterView.getCount(); i++) {
                    View v = adapterView.getChildAt(i);
                    if (position == i) {//当前选中的Item改变背景颜色
                        view.setBackgroundResource(R.drawable.bg_select);
                        position1[0] = position;

                    } else {
                        v.setBackgroundResource(R.drawable.bg_whitebtn);
                    }
                }
            }
        });
    }
}