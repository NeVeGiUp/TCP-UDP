package com.itc.smartbroadcast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.music.BatchOperationMusicActivity;
import com.itc.smartbroadcast.activity.music.ChooseMusicActivityToTask;
import com.itc.smartbroadcast.bean.MusicMsgInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchOperationMusicAdapter extends BaseAdapter {
    private List<String> musicList;
    private Context mContext;
    private List<String> checkBoxIDList = new ArrayList<>();            //存储checkBox的值
    private BatchOperationMusicActivity batchOperationMusicActivity;
    Map<Integer, Boolean> isCheck = new HashMap<>();
    private boolean checkAll = false;
    //get set
    public List<String> getCheckBoxIDList() {
        return checkBoxIDList;
    }

    public void setCheckBoxIDList(List<MusicMsgInfo> musicList) {
        this.checkBoxIDList = checkBoxIDList;
    }

    public BatchOperationMusicAdapter(List<String> musicList, Context mContext) {
        this.musicList = musicList;
        this.mContext = mContext;
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


    public void setCheckAll() {
        checkAll = true;
        for (int i = 0; i < musicList.size(); i++) {
            isCheck.put(i, true);// 默认所有的checkbox都是选中
        }
        checkBoxIDList.clear();
        checkBoxIDList.addAll(musicList);
        notifyDataSetChanged();
    }

    public void setNoCheckAll() {
        checkAll = false;
        for (int i = 0; i < musicList.size(); i++) {
            isCheck.put(i, false);// 默认所有的checkbox都是没选中
        }
        checkBoxIDList.clear();
        notifyDataSetChanged();
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

        testViewHolder.item_checkBox.setChecked(isCheck.get(position));

        //设置checkBox的值
        testViewHolder.item_checkBox.setText(musicList.get(position).toString());

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
                if (checkBoxIDList.size() >= 100) {
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
            }
        });
        return convertView;
    }

    static class TestViewHolder {

        CheckBox item_checkBox;

    }

}
