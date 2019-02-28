package com.itc.smartbroadcast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.music.ChooseMusicActivityToTask;
import com.itc.smartbroadcast.bean.MusicMsgInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseMusicAdapter extends BaseAdapter {
    private List<MusicMsgInfo> musicList;
    private List<MusicMsgInfo> musicListed = new ArrayList<>();
    private Context mContext;
    private List<MusicMsgInfo> checkBoxIDList = new ArrayList<>();            //存储checkBox的值
    private ChooseMusicActivityToTask chooseMusicActivityToTask;

    Map<Integer, Boolean> isCheck = new HashMap<>();

    //get set
    public List<MusicMsgInfo> getCheckBoxIDList() {
        return checkBoxIDList;
    }

    public void setCheckBoxIDList(List<MusicMsgInfo> musicList) {
        this.checkBoxIDList = checkBoxIDList;
    }

    public ChooseMusicAdapter(List<MusicMsgInfo> musicList, List<MusicMsgInfo> musicMsgInfoList, Context mContext, ChooseMusicActivityToTask chooseMusicActivityToTask) {
        this.musicList = musicList;
        if (musicMsgInfoList != null && musicList.size() > 0)
            this.musicListed.addAll(musicMsgInfoList);
        this.mContext = mContext;
        this.chooseMusicActivityToTask = chooseMusicActivityToTask;
    }

    @Override
    public int getCount() {
        return musicList.size();
    }

    @Override
    public Object getItem(int position) {
        return musicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final TestViewHolder testViewHolder;

        convertView = null;

//        if (convertView == null) {
        if (true) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_choose_music, null);
            testViewHolder = new TestViewHolder();

            testViewHolder.item_checkBox = (CheckBox) convertView.findViewById(R.id.cb_music_name);
            convertView.setTag(testViewHolder);
        } else {
            testViewHolder = (TestViewHolder) convertView.getTag();
        }

        if (isCheck.get(position) == null) {
            isCheck.put(position, false);
        }

        if (musicListed != null && musicListed.size() > 0)
            for (MusicMsgInfo musicInfo : musicListed) {
                if (musicList.get(position).getMusicName().equals(musicInfo.getMusicName()) && musicList.get(position).getMusicFolderName().equals(musicInfo.getMusicFolderName())) {
                    isCheck.put(position, true);
                    int size = 0;
                    for (MusicMsgInfo checkMusic : checkBoxIDList) {
                        if (checkMusic.getMusicName().equals(musicInfo.getMusicName()) && checkMusic.getMusicFolderName().equals(musicInfo.getMusicFolderName())) {
                        } else {
                            size++;
                        }
                    }
                    if (checkBoxIDList.size() == size) {
                        checkBoxIDList.add(musicList.get(position));
                    }
                    break;
                }
            }
        chooseMusicActivityToTask.setCheckSize(checkBoxIDList.size());
        testViewHolder.item_checkBox.setChecked(isCheck.get(position));
        //设置checkBox的值
        testViewHolder.item_checkBox.setText(musicList.get(position).getMusicName());

        testViewHolder.item_checkBox.setTag(position);

        //获取复选框选中状态改变事件进行增删改
        testViewHolder.item_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                /*
                 * b=选中状态
                 * if b = true 将值添加至checkBoxIDList
                 * if b = false 将值从checkBoxIDList移除
                 * */
                if (checkBoxIDList.size() >= 20) {
                    if (b) {
                        testViewHolder.item_checkBox.setChecked(false);
                    } else {
                        testViewHolder.item_checkBox.setChecked(true);
                    }
                } else {
                    if (b) {
                        checkBoxIDList.add(musicList.get(position));
                    } else {
                        checkBoxIDList.remove(musicList.get(position));
                    }
                    if (isCheck.get(position)) {
                        isCheck.put(position, false);
                    } else {
                        isCheck.put(position, true);
                    }
                }
                chooseMusicActivityToTask.setCheckSize(checkBoxIDList.size());
            }
        });
        return convertView;
    }

    static class TestViewHolder {

        CheckBox item_checkBox;

    }

}
