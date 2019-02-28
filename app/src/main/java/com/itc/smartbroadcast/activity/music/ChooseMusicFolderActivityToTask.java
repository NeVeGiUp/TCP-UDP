package com.itc.smartbroadcast.activity.music;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.adapter.MyAdapter;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.MusicFolderInfo;
import com.itc.smartbroadcast.bean.MusicMsgInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.GetMusicFolderList;
import com.itc.smartbroadcast.util.CharacterParser;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.itc.smartbroadcast.activity.event.CreateTimedTaskActivity.REQUEST_MUSIC_CODE;

/**
 * create by youmu on 2018/7
 */

public class ChooseMusicFolderActivityToTask extends AppCompatActivity {

    @BindView(R.id.bt_back)
    ImageView btBack;
    @BindView(R.id.list_musiccover)
    ListView listMusiccover;
    private MyAdapter<MusicFolderInfo> McAdapter = null;
    private List<MusicFolderInfo> McData = null;

    String musicListJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_show_music_folder);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(ChooseMusicFolderActivityToTask.this, getResources().getColor(R.color.colorMain));
        init();

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String result = "";
                resultPost(result);
            }
        });
    }

    private void init() {
        musicListJson = getIntent().getStringExtra("music");

        GetMusicFolderList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);
        if ("getMusicFolderList".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                McData = new ArrayList<MusicFolderInfo>();

                final List<MusicFolderInfo> infoList = JSONArray.parseArray(data, MusicFolderInfo.class);

                McData.addAll(infoList);

                McAdapter = new MyAdapter<MusicFolderInfo>((ArrayList) McData, R.layout.item_musiccover) {
                    @Override
                    public void bindView(ViewHolder holder, MusicFolderInfo obj) {

                        holder.setVisibility(R.id.ll_chose,View.VISIBLE);

                        holder.setText(R.id.tv_musicname, obj.getMusicFolderName());
                        holder.setText(R.id.tv_musicamount, obj.getAllMusicNum() + "");


                        List<MusicMsgInfo> musicFolderInfoList = JSONArray.parseArray(musicListJson, MusicMsgInfo.class);
                        int size = 0;
                        if (musicFolderInfoList != null && musicFolderInfoList.size() > 0)
                            for (MusicMsgInfo musicMsgInfo : musicFolderInfoList) {
                                if (musicMsgInfo.getMusicFolderName().equals(obj.getMusicFolderName())) {
                                    size++;
                                }
                            }

                        holder.setText(R.id.tv_music_size, size + "");
                        String s = CharacterParser.getInstance().getSelling(obj.getMusicFolderName()).substring(0,1).toLowerCase();
                        if ("hoy".contains(s)){
                            holder.setImageResource(R.id.tm_musiccover,R.mipmap.music_fengmian_fengjing_small);
                        }else if ("sbk".contains(s)){
                            holder.setImageResource(R.id.tm_musiccover,R.mipmap.music_fengmian_fgangq_small);
                        }else if ("wz7".contains(s)){
                            holder.setImageResource(R.id.tm_musiccover,R.mipmap.music_fengmian_huanghun_small);
                        }else if ("j0e".contains(s)){
                            holder.setImageResource(R.id.tm_musiccover,R.mipmap.music_fengmian_jiedao_small);
                        }else if ("5trn".contains(s)){
                            holder.setImageResource(R.id.tm_musiccover,R.mipmap.music_fengmian_meishi_small);
                        }else if ("d2pv".contains(s)){
                            holder.setImageResource(R.id.tm_musiccover,R.mipmap.music_fengmian_moren_small);
                        }else if ("1qau".contains(s)){
                            holder.setImageResource(R.id.tm_musiccover,R.mipmap.music_fengmian_shama_small);
                        }else if ("683f".contains(s)){
                            holder.setImageResource(R.id.tm_musiccover,R.mipmap.music_fengmian_yundong_small);
                        }else if ("4cl9".contains(s)){
                            holder.setImageResource(R.id.tm_musiccover,R.mipmap.music_fengmian_zhuqiuchang_small);
                        }else if ("xgmi".contains(s)){
                            holder.setImageResource(R.id.tm_musiccover,R.mipmap.music_fengmian_haiyang_small);
                        }else {
                            holder.setImageResource(R.id.tm_musiccover,R.mipmap.music_fengmian_moren_small);
                        }
                    }
                };
                listMusiccover.setAdapter(McAdapter);
                listMusiccover.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MusicFolderInfo musicFolderInfo = infoList.get(position);
                        String musicFolderName = musicFolderInfo.getMusicFolderName();
                        Intent intent = new Intent(ChooseMusicFolderActivityToTask.this, ChooseMusicActivityToTask.class);
                        intent.putExtra("MusicFolderName", musicFolderName);
                        intent.putExtra("musicList", musicListJson);
                        startActivityForResult(intent, REQUEST_MUSIC_CODE);
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (REQUEST_MUSIC_CODE == requestCode && resultCode == RESULT_OK) {
            String musicResultListStr = data.getStringExtra("music_result_list");
            resultPost(musicResultListStr);
        }

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
        intent.putExtra("music_result_list", result);
        //设置返回数据
        this.setResult(RESULT_OK, intent);
        //关闭Activity
        this.finish();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
