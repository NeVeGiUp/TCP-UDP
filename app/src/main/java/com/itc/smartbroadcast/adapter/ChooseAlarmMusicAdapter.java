package com.itc.smartbroadcast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.bean.MusicMsgInfo;

import java.util.List;

public class ChooseAlarmMusicAdapter extends BaseAdapter {
    private List<MusicMsgInfo> musicList;
    private Context mContext;
    private MusicMsgInfo checkFou = new MusicMsgInfo();        //存储checkBox的值
    private int layoutPosition;
    private String selectMusic;

    //get set
    public MusicMsgInfo getCheckBoxIDList() {
        return checkFou;
    }

    public void setCheckBoxIDList(MusicMsgInfo checkBoxIDList) {
        this.checkFou = checkBoxIDList;
    }

    public ChooseAlarmMusicAdapter(List<MusicMsgInfo> musicList, String selectMusic, Context mContext) {
        this.musicList = musicList;
        this.selectMusic = selectMusic;
        this.mContext = mContext;
        for (int i = 0; i < musicList.size(); i++){
            if (musicList.get(i).getMusicName().equals(selectMusic)){
                layoutPosition = i;
            }
        }

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

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_choose_sound_source_terminal, null);
            testViewHolder = new TestViewHolder();

            testViewHolder.item_radio = (RadioButton) convertView.findViewById(R.id.cb_terminal_name);
            testViewHolder.item_MAC = (TextView) convertView.findViewById(R.id.tv_bind_terminal_mac);

            convertView.setTag(testViewHolder);
        } else {
            testViewHolder = (TestViewHolder) convertView.getTag();
        }

        //设置checkBox的值
        testViewHolder.item_radio.setText(musicList.get(position).getMusicName());
        testViewHolder.item_MAC.setText(musicList.get(position).getMusicName());

        //获取复选框选中状态改变事件进行增删改
        testViewHolder.item_radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取当前点击的位置
                layoutPosition = position;
                notifyDataSetChanged();

            }
        });

        //更改状态
        if (position == layoutPosition) {
            testViewHolder.item_radio.setChecked(true);
            checkFou = musicList.get(position);
        } else {
            testViewHolder.item_radio.setChecked(false);
        }
        return convertView;
    }

    static class TestViewHolder {

        RadioButton item_radio;
        TextView item_MAC;

    }

}
