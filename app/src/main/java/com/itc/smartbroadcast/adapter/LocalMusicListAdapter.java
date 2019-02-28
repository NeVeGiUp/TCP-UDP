package com.itc.smartbroadcast.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.bean.Music;
import com.itc.smartbroadcast.bean.Task;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LocalMusicListAdapter extends RecyclerView.Adapter {


    private Context mContext;
    private List<Music> mMusicList = new ArrayList<>();

    private List<Music> checkBoxIDList = new ArrayList<>();            //存储checkBox的值


    private boolean checkAll = false;

    Map<Integer, Boolean> isCheck = new HashMap<>();


    public LocalMusicListAdapter(Context context) {
        this.mContext = context;
    }



    public void setCheckBoxIDList(List<Music> checkBoxIDList) {
        this.checkBoxIDList = checkBoxIDList;
    }

    //get set
    public List<Music> getCheckBoxIDList() {
        return checkBoxIDList;
    }


    public List<Music> getList() {
        return mMusicList;
    }

    public void setList(List<Music> list) {
        if (list != null) {
            this.mMusicList.clear();
            addList(list);
        }
    }

    private void addList(List<Music> list) {
        if (list != null) {
            mMusicList.addAll(list);

            for (int i = 0; i < mMusicList.size(); i++) {
                isCheck.put(i, false);// 默认所有的checkbox都是没选中
            }

            notifyDataSetChanged();
        }
    }

    public void setCheckAll() {
        checkAll = true;
        for (int i = 0; i < mMusicList.size(); i++) {
            isCheck.put(i, true);// 默认所有的checkbox都是选中
        }
        checkBoxIDList.clear();
        checkBoxIDList.addAll(mMusicList);
        notifyDataSetChanged();
    }

    public void setNoCheckAll() {
        checkAll = false;
        for (int i = 0; i < mMusicList.size(); i++) {
            isCheck.put(i, false);// 默认所有的checkbox都是没选中
        }
        checkBoxIDList.clear();
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item_music, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        //绑定数据
        final ItemViewHolder itemHolder = (ItemViewHolder) holder;
        final Music music = mMusicList.get(position);
        itemHolder.tvMusicListTitle.setText(music.getTitle());
        NumberFormat nf = new DecimalFormat("0.0 ");
        double length = Double.parseDouble(nf.format(music.getLength() / 1024.0 / 1024.0));
        itemHolder.tvMusicFileSize.setText("MP3  " + length + "M");

        itemHolder.cbLocalMusic.setOnCheckedChangeListener(null);//清掉监听器
        itemHolder.cbLocalMusic.setChecked(isCheck.get(position));

//        itemHolder.cbLocalMusic.setChecked(checkAll);
        itemHolder.cbLocalMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    checkBoxIDList.add(music);
                } else {
                    checkBoxIDList.remove(music);
                }

                if (isCheck.get(position)) {
                    isCheck.put(position, false);
                } else {
                    isCheck.put(position, true);
                }


            }
        });
    }

    @Override
    public int getItemCount() {
        if (mMusicList == null)
            return 0;
        else
            return mMusicList.size();
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.cb_local_music)
        CheckBox cbLocalMusic;
        @BindView(R.id.tv_music_list_title)
        TextView tvMusicListTitle;
        @BindView(R.id.tv_music_file_size)
        TextView tvMusicFileSize;

        public ItemViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }
}
