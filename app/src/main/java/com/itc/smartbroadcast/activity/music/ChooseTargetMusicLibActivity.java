package com.itc.smartbroadcast.activity.music;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.GetMusicFolderList;
import com.itc.smartbroadcast.util.CharacterParser;
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
public class ChooseTargetMusicLibActivity extends AppCompatActivity {


    @BindView(R.id.list_musiccover)
    ListView listMusiccover;
    @BindView(R.id.bt_back)
    ImageView btBack;
    private MyAdapter<MusicFolderInfo> McAdapter = null;
    private List<MusicFolderInfo> McData = null;
    private String editType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_music_folder);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(ChooseTargetMusicLibActivity.this, getResources().getColor(R.color.colorMain));
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        init();
    }

    private void init() {
        GetMusicFolderList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
        Intent intent = getIntent();
        editType = intent.getStringExtra("editType");
    }

        @Override
    protected void onResume() {
        EventBus.getDefault().register(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
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
                Collections.sort(infoList, new Comparator<MusicFolderInfo>() {
                    @Override
                    public int compare(MusicFolderInfo t1, MusicFolderInfo t2) {
                        String s1 = CharacterParser.getInstance().getSelling(t1.getMusicFolderName());
                        String s2 = CharacterParser.getInstance().getSelling(t2.getMusicFolderName());
                        return s1.compareTo(s2);
                    }
                });
                McData.addAll(infoList);

                McAdapter = new MyAdapter<MusicFolderInfo>((ArrayList) McData, R.layout.item_musiccover) {
                    @Override
                    public void bindView(ViewHolder holder, MusicFolderInfo obj) {
                        switch (obj.getOverflowFalg()){
                            case 0:
                                holder.setVisibility(R.id.tv_shouge,View.GONE);
                                break;
                            case 1:
                                holder.setVisibility(R.id.tv_shouge,View.VISIBLE);
                                break;
                        }
                        holder.setText(R.id.tv_musicname, obj.getMusicFolderName());
                        holder.setText(R.id.tv_musicamount, obj.getAllMusicNum() + "");
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
                        Intent intent = new Intent();
                        intent.putExtra("MusicFolderName", musicFolderName);
                        intent.putExtra("editType", editType);
                        ChooseTargetMusicLibActivity.this.setResult(1, intent);
                        ChooseTargetMusicLibActivity.this.finish();
                    }
                });
            }
        }
    }

}
