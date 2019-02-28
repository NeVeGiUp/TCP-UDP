package com.itc.smartbroadcast.adapter.event;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.bean.CDMusic;
import com.itc.smartbroadcast.bean.CDMusicList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lik on 18-9-19.
 */

public class MusicAdapter extends BaseAdapter {

    List<CDMusic> musicList = new ArrayList<>();
    Context content;

    int nowMusicNum = 999999;

    public void setList(CDMusicList music, int nowMusicNum) {
        this.musicList.clear();
        this.musicList.addAll(music.getMusicNameList());
        this.nowMusicNum = nowMusicNum;
        notifyDataSetChanged();
    }

    public List<CDMusic> getList() {
        return musicList;
    }

    public MusicAdapter(Context content, CDMusicList music, int nowMusicNum) {
        this.content = content;
        this.musicList.clear();
        this.nowMusicNum = nowMusicNum;
        if (music.getMusicNameList() != null && music.getMusicNameList().size() > 0)
            this.musicList.addAll(music.getMusicNameList());
    }

    @Override
    public int getCount() {
        return musicList.size();
    }

    @Override
    public Object getItem(int i) {
        return musicList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        View view = null;
        if (convertView == null) {
            view = LayoutInflater.from(content).inflate(R.layout.item_music_list, null);
        } else {
            view = convertView;
        }
        TextView tvMusicName = (TextView) view.findViewById(R.id.tv_music_name);
        String musicName = musicList.get(i).getNum() + "." + musicList.get(i).getMusicName();
        if (musicName == null || musicName.equals("")) {
            musicName = "暂无歌曲名";
        }
        tvMusicName.setText(musicName);
        if (nowMusicNum == musicList.get(i).getNum()){
            tvMusicName.setTextColor(content.getResources().getColor(R.color.colorMain));
        }else{
            tvMusicName.setTextColor(Color.BLACK);
        }

        return view;
    }


}
