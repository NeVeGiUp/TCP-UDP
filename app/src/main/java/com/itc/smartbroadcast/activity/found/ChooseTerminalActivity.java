package com.itc.smartbroadcast.activity.found;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.adapter.ChooseTerminalAdapter;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
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

public class ChooseTerminalActivity extends AppCompatActivity {
    @BindView(R.id.bt_back_found)
    ImageView btBackFound;
    @BindView(R.id.list_choose_terminal)
    ListView listChooseTerminal;
    @BindView(R.id.tv_save_bind_terminal)
    TextView tvSaveBindTerminal;
    //private MyAdapter<FoundDeviceInfo> FdAdapter = null;
    private List<String> FdData;
    private List<String> MacData;
    private ChooseTerminalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_choose_terminal);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(ChooseTerminalActivity.this, getResources().getColor(R.color.colorMain),0);
        init();
    }


    private void init() {
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
        Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);
        if ("getDeviceList".equals(baseBean.getType())) {
            final String data = baseBean.getData();
            Log.i("终端信息》》》", data);
            if (data != null) {

//                final List<FoundDeviceInfo> infoList = JSONArray.parseArray(data, FoundDeviceInfo.class);
//                FdData.addAll(infoList);
//                FdAdapter = new MyAdapter<FoundDeviceInfo>((ArrayList) FdData, R.layout.list_item_chooseterminal) {
//                    @Override
//                    public void bindView(ViewHolder holder, FoundDeviceInfo obj) {
//                        holder.setText(R.id.cb_terminal_name, obj.getDeviceName());
//                    }
//                };

                ArrayList<FoundDeviceInfo> foundDeviceInfos = JSON.parseObject(data, new TypeReference<ArrayList<FoundDeviceInfo>>() {});
                FdData = new ArrayList<>();
                MacData = new ArrayList<>();
                for (FoundDeviceInfo FoundDeviceInfo : foundDeviceInfos) {
                    String deviceName = FoundDeviceInfo.getDeviceName().toString();
                    String deviceMac = FoundDeviceInfo.getDeviceMac().toString();
                    FdData.add(deviceName);
                    MacData.add(deviceMac);

                }
                ListView list_terminals = (ListView) findViewById(R.id.list_choose_terminal);
                adapter = new ChooseTerminalAdapter(FdData,MacData,ChooseTerminalActivity.this);
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
                List<String> checkTerminal = new ArrayList<>();
                for (int i = 0; i < adapter.getCheckBoxIDList().size(); i++) {
                    String checked = adapter.getCheckBoxIDList().get(i).toString();
                    checkTerminal.add(checked);
                }
                String checkedTerminalMAC = checkTerminal.toString();
                Log.i("已选择的终端MAC",checkedTerminalMAC);
                Intent intent = new Intent(ChooseTerminalActivity.this,CreatePartitionActivity.class);
                intent.putExtra("CheckedTerminal",checkedTerminalMAC);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}