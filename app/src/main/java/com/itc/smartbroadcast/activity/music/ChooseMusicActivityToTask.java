package com.itc.smartbroadcast.activity.music;

import android.content.Context;
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
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.adapter.ChooseMusicAdapter;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.Music;
import com.itc.smartbroadcast.bean.MusicMsgInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.GetMusicList;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ChooseMusicActivityToTask extends AppCompatActivity {

    @BindView(R.id.bt_back)
    ImageView btBack;
    @BindView(R.id.tv_save_music)
    TextView tvSaveMusic;
    @BindView(R.id.list_choose_music)
    ListView listChooseMusic;
    @BindView(R.id.tv_check_size)
    TextView tvCheckSize;
    private List<String> McData;
    private ChooseMusicAdapter adapter;
    private static String checkedMusic;

    String musicListJson;

    List<MusicMsgInfo> musicMsgInfoList = new ArrayList<>();

    List<MusicMsgInfo> musicMsgInfos = new ArrayList<>();

    public ChooseMusicActivityToTask chooseMusicActivityToTask;

    private Context context;

    String musicFolderName = "";

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
        context = this;
        chooseMusicActivityToTask = this;
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_choose_music);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(ChooseMusicActivityToTask.this, getResources().getColor(R.color.colorMain));
        init();
    }

    private void init() {
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent = getIntent();
        musicFolderName = intent.getStringExtra("MusicFolderName");
        musicListJson = intent.getStringExtra("musicList");

        if (musicListJson != null && !musicListJson.equals("")) {
            musicMsgInfoList = JSONArray.parseArray(musicListJson, MusicMsgInfo.class);
        }

        GetMusicList.sendCMD(AppDataCache.getInstance().getString("loginIp"), musicFolderName);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        final Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);
        if ("getMusicList".equals(baseBean.getType())) {
            final String data = baseBean.getData();
            Log.i("音乐》》》", data);
            if (data != null) {
                musicMsgInfos = JSONArray.parseArray(data, MusicMsgInfo.class);
                List<MusicMsgInfo> adapterList = new ArrayList<>();

                for (MusicMsgInfo music1 : musicMsgInfoList) {
                    if (musicFolderName.equals(music1.getMusicFolderName())) {
                        adapterList.add(music1);
                    }
                }

                for (MusicMsgInfo music : musicMsgInfos) {
                    boolean bol = true;
                    for (MusicMsgInfo music1 : musicMsgInfoList) {
                        if (music.getMusicName().equals(music1.getMusicName()) && musicFolderName.equals(music1.getMusicFolderName())) {
                            bol = false;
                        }
                    }
                    if (bol) {
                        adapterList.add(music);
                    }
                }
                adapter = new ChooseMusicAdapter(adapterList, musicMsgInfoList, ChooseMusicActivityToTask.this, chooseMusicActivityToTask);
                listChooseMusic.setAdapter(adapter);
            }
        }
        tvSaveMusic.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                /*
                 *循环打印选中checkBox的值
                 *通过adapter.getCheckBoxIDList()获取所选checkBox值的集合
                 */
                List<MusicMsgInfo> checkMusic = adapter.getCheckBoxIDList();

                int size = musicMsgInfoList.size();

                List<MusicMsgInfo> temp = new ArrayList<>();

                if (musicMsgInfoList != null && musicMsgInfoList.size() > 0 && musicMsgInfos != null && musicMsgInfos.size() > 0)
                    for (int i = 0; i < size; i++) {
                        if (!musicMsgInfoList.get(i).getMusicFolderName().equals(musicMsgInfos.get(0).getMusicFolderName())) {
                            temp.add(musicMsgInfoList.get(i));
                        }
                    }

                temp.addAll(checkMusic);

                String result = gson.toJson(temp);

                resultPost(result);
            }
        });
    }


    public void resultPost(String result) {
        //数据是使用Intent返回
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("music_result_list", result);
        //设置返回数据
        this.setResult(RESULT_OK, intent);
        //关闭Activity
        this.finish();
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


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}