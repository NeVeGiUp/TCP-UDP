package com.itc.smartbroadcast.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.music.ShowMusicListActivity;
import com.itc.smartbroadcast.adapter.MyAdapter;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditMusicFolderNameInfo;
import com.itc.smartbroadcast.bean.EditMusicFolderNameResult;
import com.itc.smartbroadcast.bean.MusicFolderInfo;
import com.itc.smartbroadcast.bean.TimerStatusQueryResult;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.EditMusicFolderName;
import com.itc.smartbroadcast.channels.protocolhandler.GetMusicFolderList;
import com.itc.smartbroadcast.channels.protocolhandler.TimerStatusQuery;
import com.itc.smartbroadcast.dao.ResultDB;
import com.itc.smartbroadcast.helper.CustomDialog;
import com.itc.smartbroadcast.popupwindow.EditDialog;
import com.itc.smartbroadcast.popupwindow.MoreWindow;
import com.itc.smartbroadcast.util.CharacterParser;
import com.itc.smartbroadcast.util.ToastUtil;
import com.itc.smartbroadcast.widget.custom.CommonProgressDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * create by youmu on 2018/7
 */

public class MusicFragment extends Fragment {
    MoreWindow mMoreWindow;
    private ImageView showWindow;
    private RelativeLayout btnCreateFolder;
    private Context mContext;
    private ListView list_musiccover;
    private MyAdapter<MusicFolderInfo> McAdapter= null;
    private List<MusicFolderInfo> McData = null;
    private List<MusicFolderInfo> infoList;
    private TextView tvMusicInfo,tvMusicInfo2;
    private int musicInfoNum,musicLibInfoNum;
    private RelativeLayout rlCreateMusicLib;
    private CommonProgressDialog progressDialog;
    private Boolean b = true;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume(){
        init();
        EventBus.getDefault().register(this);
        super.onResume();
    }

    @Override
    public void onPause(){
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        showWindow = (ImageView)view.findViewById(R.id.showWindow);
        if (!AppDataCache.getInstance().getString("userType").equals("00")){
            showWindow.setVisibility(View.GONE);
        }
        showWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMoreWindow(v);
            }
        });
        btnCreateFolder = (RelativeLayout)view.findViewById(R.id.rl_importmusic_btn);
        tvMusicInfo = (TextView)view.findViewById(R.id.tv_music_info);
        tvMusicInfo2 = (TextView)view.findViewById(R.id.tv_musicinfo);
        rlCreateMusicLib = (RelativeLayout)view.findViewById(R.id.rl_create_musiclib);
        if (!AppDataCache.getInstance().getString("userType").equals("00")){
            rlCreateMusicLib.setVisibility(View.GONE);
        }
        TimerStatusQuery.sendCMD(AppDataCache.getInstance().getString("loginIp"),AppDataCache.getInstance().getInt("userNum"));
        btnCreateFolder.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(final View v) {
                if (musicLibInfoNum > 49) {
                    ToastUtil.show(getActivity(), "不允许创建更多音乐文件夹");
                } else {
                    final EditDialog editDialog = new EditDialog(getActivity());
                    editDialog.show();
                    editDialog.setOnPosNegClickListener(new EditDialog.OnPosNegClickListener() {
                        @Override
                        public void posClickListener(String value) {
                            if (infoList == null) {

                            } else {
                                for (MusicFolderInfo musicFolderInfo : infoList) {
                                    if (musicFolderInfo.getMusicFolderName().equals(value)) {
                                        ToastUtil.show(getActivity(), "已有同名文件夹，请重新输入");
                                        b = false;
                                        break;
                                    } else {
                                        b = true;
                                    }
                                }
                                if (b) {
                                    if (value.length() < 2) {
                                        ToastUtil.show(getActivity(), "请输入两位以上的文件夹名");
                                    } else {
                                        EditMusicFolderNameInfo musicFolderNameInfo2 = new EditMusicFolderNameInfo();
                                        Random random = new Random();
                                        int r = random.nextInt(5) % (5 + 1);
                                        musicFolderNameInfo2.setOperator("00");
                                        musicFolderNameInfo2.setFolderName(value);
                                        EditMusicFolderName.sendCMD(AppDataCache.getInstance().getString("loginIp"), musicFolderNameInfo2);
                                        editDialog.dismiss();
                                        progressDialog = new CommonProgressDialog(getActivity());  //登录进度条
                                        progressDialog.show();
                                    }
                                }
                            }
                        }
                        @Override
                        public void negCliclListener(String value) {
                            editDialog.cancel();
                        }
                    });
                }
            }
        });
        list_musiccover = (ListView)view.findViewById(R.id.list_musiccover);
        mContext = getActivity();
        return view;
    }

    private void init(){
        TimerStatusQuery.sendCMD(AppDataCache.getInstance().getString("loginIp"),AppDataCache.getInstance().getInt("userNum"));
    }

    private void showMoreWindow(View view) {
        if (null == mMoreWindow) {
            mMoreWindow = new MoreWindow(getActivity());
            mMoreWindow.init();
        }
        mMoreWindow.showMoreWindow(view,100);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);
        if ("TimerStatusQueryResult".equals(baseBean.getType())){
            TimerStatusQueryResult timerStatusQueryResult = gson.fromJson(baseBean.getData(), TimerStatusQueryResult.class);
            int sdResult = timerStatusQueryResult.getSdcardStatus();
            if (sdResult == 1){
                tvMusicInfo2.setText("SD卡已拔出");
                btnCreateFolder.setClickable(false);
                btnCreateFolder.setFocusable(false);
            }else {
                GetMusicFolderList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
            }
        }
        if ("getMusicFolderList".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                McData = new ArrayList<MusicFolderInfo>();
                infoList = JSONArray.parseArray(data, MusicFolderInfo.class);
                musicInfoNum = 0;
                for (MusicFolderInfo musicFolderInfo : infoList){
                    musicInfoNum += musicFolderInfo.getAllMusicNum();

                }
                musicLibInfoNum = infoList.size();
                tvMusicInfo.setText("当前共创建"+musicLibInfoNum+"个文件夹，收录"+musicInfoNum+"首歌曲");

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
                        holder.setText(R.id.tv_musicname, obj.getMusicFolderName().trim().replaceAll("\\uffff",""));
                        holder.setText(R.id.tv_musicamount, obj.getAllMusicNum()+"");
                        holder.setVisibility(R.id.tv_music_size,View.INVISIBLE);
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
                list_musiccover.setAdapter(McAdapter);
                list_musiccover.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        MusicFolderInfo musicFolderInfo = infoList.get(position);
                        String musicFolderName = musicFolderInfo.getMusicFolderName().replaceAll("\\uffff","");
                        String musicNum = musicFolderInfo.getAllMusicNum()+"";
                        Intent intent = new Intent(getActivity(),ShowMusicListActivity.class);
                        intent.putExtra("MusicFolderName",musicFolderName);
                        intent.putExtra("MusicNum",musicNum);
                        startActivity(intent);
                    }
                });
                list_musiccover.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                        if (AppDataCache.getInstance().getString("userType").equals("00")){
                            deleteMusicLib(position);
                        }else {
                            ToastUtil.show(getActivity(),"普通用户无法对音乐库进行操作");
                        }
                        return true;
                    }
                });
            }
        }
        if ("EditMusicFolderNameResult".equals(baseBean.getType())) {
            EditMusicFolderNameResult partitionInfo = gson.fromJson(baseBean.getData(), EditMusicFolderNameResult.class);
            int isSucceed = partitionInfo.getResult();
            if (isSucceed == 0) {
                progressDialog.dismiss();
                ToastUtil.show(getActivity(), "失败");
            } else if (isSucceed == 1) {
                progressDialog.dismiss();
                ToastUtil.show(getActivity(), "成功");
                init();
            }
        }
    }

    private void deleteMusicLib(final int position){
        CustomDialog dialog = new CustomDialog(getActivity(), "提示", "确定要删除当前音乐库？一旦删除，目前进行中的任务将无法执行",
                "取消", "确认", new CustomDialog.OnOkClickListener() {
            @Override
            public void onClick() {
                MusicFolderInfo musicFolderInfo = infoList.get(position);
                String musicFolderName = musicFolderInfo.getMusicFolderName();
                EditMusicFolderNameInfo musicFolderNameInfo = new EditMusicFolderNameInfo();
                musicFolderNameInfo.setOperator("01");
                musicFolderNameInfo.setFolderName(musicFolderName);
                EditMusicFolderName.sendCMD(AppDataCache.getInstance().getString("loginIp"), musicFolderNameInfo);
                progressDialog = new CommonProgressDialog(getActivity());  //登录进度条
                progressDialog.show();
            }
        });
        dialog.show();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}