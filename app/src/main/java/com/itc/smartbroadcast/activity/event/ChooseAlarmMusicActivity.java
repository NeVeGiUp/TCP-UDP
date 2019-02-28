package com.itc.smartbroadcast.activity.event;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.adapter.ChooseAlarmMusicAdapter;
import com.itc.smartbroadcast.adapter.ChooseSoundSourceTerminalAdapter;
import com.itc.smartbroadcast.base.Base2Activity;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.bean.MusicMsgInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.GetAlarmMusicList;
import com.itc.smartbroadcast.channels.protocolhandler.GetDeviceList;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

//选择报警音乐
public class ChooseAlarmMusicActivity extends Base2Activity {
    @BindView(R.id.bt_back_found)
    ImageView btBackFound;
    @BindView(R.id.list_choose_terminal)
    ListView listChooseTerminal;
    @BindView(R.id.tv_save_bind_terminal)
    TextView tvSaveBindTerminal;
    @BindView(R.id.tv_check_size)
    TextView tvCheckSize;
    private ChooseAlarmMusicAdapter adapter;

    String ip = "";
    String selectMusic = "";


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
        setContentView(R.layout.activity_choose_alarm_music);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);
        init();
        btBackFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = "";
                resultPost(result);
            }
        });
    }


    private void init() {

        ip = getIntent().getStringExtra("ip");
        selectMusic = getIntent().getStringExtra("selectMusic");
        btBackFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        GetAlarmMusicList.sendCMD(ip);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        final Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);
        if ("getAlarmMusicList".equals(baseBean.getType())) {
            final String data = baseBean.getData();
            if (data != null) {
                List<MusicMsgInfo> musicList = JSONArray.parseArray(data, MusicMsgInfo.class);
                ListView list_terminals = (ListView) findViewById(R.id.list_choose_terminal);

                for (int i = 0; i < musicList.size(); i++) {
                    if (musicList.get(i).getMusicName().equals(selectMusic)){
                        MusicMsgInfo musicMsgInfo = new MusicMsgInfo();
                        musicMsgInfo.setMusicName(musicList.get(i).getMusicName());
                        musicMsgInfo.setMusicFolderName(musicList.get(i).getMusicFolderName());
                        musicMsgInfo.setMusicTime(musicList.get(i).getMusicTime());
                        musicList.set(i,musicList.get(0));
                        musicList.set(0,musicMsgInfo);
                    }
                }

                adapter = new ChooseAlarmMusicAdapter(musicList, selectMusic, ChooseAlarmMusicActivity.this);
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
                MusicMsgInfo musicMsgInfo = adapter.getCheckBoxIDList();
                String deviceStr = gson.toJson(musicMsgInfo);
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
        intent.putExtra("music", result);
        //设置返回数据
        ChooseAlarmMusicActivity.this.setResult(RESULT_OK, intent);
        //关闭Activity
        ChooseAlarmMusicActivity.this.finish();
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}