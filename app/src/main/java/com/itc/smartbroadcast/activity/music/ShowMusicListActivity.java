package com.itc.smartbroadcast.activity.music;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.adapter.MyAdapter;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditMusicFolderNameInfo;
import com.itc.smartbroadcast.bean.EditMusicFolderNameResult;
import com.itc.smartbroadcast.bean.MusicMsgInfo;
import com.itc.smartbroadcast.bean.OperateMusicFilesInfo;
import com.itc.smartbroadcast.bean.OperateMusicFilesResult;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.EditMusicFolderName;
import com.itc.smartbroadcast.channels.protocolhandler.GetMusicList;
import com.itc.smartbroadcast.channels.protocolhandler.OperateMusicFiles;
import com.itc.smartbroadcast.popupwindow.EditMusicLibDialog;
import com.itc.smartbroadcast.util.CharacterParser;
import com.itc.smartbroadcast.util.SecToTime;
import com.itc.smartbroadcast.util.ToastUtil;
import com.itc.smartbroadcast.widget.custom.CommonProgressDialog;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * create by youmu on 2018/7
 */

public class ShowMusicListActivity extends AppCompatActivity {
    @BindView(R.id.music_topbar)
    RelativeLayout musicTopbar;
    @BindView(R.id.tv_musiclist_name)
    TextView tvMusiclistName;
    @BindView(R.id.ll_musiclist_top)
    RelativeLayout llMusiclistTop;
    @BindView(R.id.list_music_list)
    ListView listMusicList;
    Activity mContext;
    @BindView(R.id.tv_music_num)
    TextView tvMusicNum;
    @BindView(R.id.ll_import_music)
    LinearLayout llImportMusic;
    @BindView(R.id.rl_importmusic)
    RelativeLayout rlImportmusic;
    @BindView(R.id.rl_edit_musicfolder)
    RelativeLayout rlEditMusicfolder;
    @BindView(R.id.rl_importmusic_btn)
    RelativeLayout rlImportmusicBtn;
    @BindView(R.id.rl_batch_edit)
    RelativeLayout rlBatchEdit;
    @BindView(R.id.bt_back)
    RelativeLayout btBack;
    @BindView(R.id.music_cover)
    ImageView musicCover;
    private MyAdapter<MusicMsgInfo> MmAdapter;
    private List<MusicMsgInfo> MmData;
    private String editName;
    private String musicFolderName;
    private String musicName;
    private List<MusicMsgInfo> infoList;
    private CommonProgressDialog progressDialog;
    private Dialog bottomDialog;
    private EditMusicLibDialog editDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showmusiclist);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        llImportMusic.setVisibility(View.GONE);
        StatusBarUtil.setTransparent(this);
        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // 虚拟导航键
        window.setNavigationBarColor(Color.BLACK);

    }


    private void init() {
        if (!AppDataCache.getInstance().getString("userType").equals("00")) {
            rlEditMusicfolder.setEnabled(false);
            rlBatchEdit.setEnabled(false);
        }

        final Intent intent = getIntent();
        musicFolderName = intent.getStringExtra("MusicFolderName");
        String musicNum = intent.getStringExtra("MusicNum");



        String s = CharacterParser.getInstance().getSelling(musicFolderName).substring(0, 1).toLowerCase();
        if ("hoy".contains(s)){
            musicCover.setImageResource(R.mipmap.music_fengmian_fengjing_small);
        }else if ("sbk".contains(s)){
            musicCover.setImageResource(R.mipmap.music_fengmian_fgangq_small);
        }else if ("wz7".contains(s)){
            musicCover.setImageResource(R.mipmap.music_fengmian_huanghun_small);
        }else if ("j0e".contains(s)){
            musicCover.setImageResource(R.mipmap.music_fengmian_jiedao_small);
        }else if ("5trn".contains(s)){
            musicCover.setImageResource(R.mipmap.music_fengmian_meishi_small);
        }else if ("d2pv".contains(s)){
            musicCover.setImageResource(R.mipmap.music_fengmian_moren_small);
        }else if ("1qau".contains(s)){
            musicCover.setImageResource(R.mipmap.music_fengmian_shama_small);
        }else if ("683f".contains(s)){
            musicCover.setImageResource(R.mipmap.music_fengmian_yundong_small);
        }else if ("4cl9".contains(s)){
            musicCover.setImageResource(R.mipmap.music_fengmian_zhuqiuchang_small);
        }else if ("xgmi".contains(s)){
            musicCover.setImageResource(R.mipmap.music_fengmian_haiyang_small);
        }else {
            musicCover.setImageResource(R.mipmap.music_fengmian_moren_small);
        }




        tvMusiclistName.setText(musicFolderName);
        tvMusicNum.setText("共" + musicNum + "首歌曲");
        if (musicNum.equals("0")) {
            llMusiclistTop.setVisibility(View.GONE);
            llImportMusic.setVisibility(View.VISIBLE);
        }

        GetMusicList.sendCMD(AppDataCache.getInstance().getString("loginIp"), musicFolderName);


        rlEditMusicfolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editDialog = new EditMusicLibDialog(ShowMusicListActivity.this);
                editDialog.show();
                editDialog.setOnPosNegClickListener(new EditMusicLibDialog.OnPosNegClickListener() {
                    @Override
                    public void posClickListener(String value) {
                        if (value.length() < 2) {
                            ToastUtil.show(ShowMusicListActivity.this, "请输入正确的文件夹名");
                        } else {
                            progressDialog = new CommonProgressDialog(ShowMusicListActivity.this);
                            progressDialog.show();
                            EditMusicFolderNameInfo musicFolderNameInfo2 = new EditMusicFolderNameInfo();
                            musicFolderName = tvMusiclistName.getText().toString();
                            musicFolderNameInfo2.setOperator("02");
                            musicFolderNameInfo2.setFolderName(musicFolderName);
                            musicFolderNameInfo2.setUpdateFolderName(value);
                            EditMusicFolderName.sendCMD(AppDataCache.getInstance().getString("loginIp"), musicFolderNameInfo2);
                            editName = value;
                        }
                    }

                    @Override
                    public void negCliclListener(String value) {
                        editDialog.cancel();
                    }
                });
            }
        });

        rlBatchEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ShowMusicListActivity.this, BatchOperationMusicActivity.class);
                intent1.putExtra("MusicFolderName", musicFolderName);
                startActivity(intent1);
            }
        });
    }


    private void init2() {
        GetMusicList.sendCMD(AppDataCache.getInstance().getString("loginIp"), musicFolderName);

        if (infoList.size() == 0) {
            llMusiclistTop.setVisibility(View.GONE);
            llImportMusic.setVisibility(View.VISIBLE);
        } else {
            tvMusicNum.setText("共" + infoList.size() + "首歌曲");
            if (infoList == null) {
                llMusiclistTop.setVisibility(View.GONE);
                llImportMusic.setVisibility(View.VISIBLE);
            }
        }
    }

    @OnClick(R.id.rl_importmusic)
    public void onViewClicked() {
        Intent intent = new Intent(this, ImportMusicActivity.class);
        intent.putExtra("MusicFolderName", musicFolderName);
        startActivity(intent);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);
        if ("getMusicList".equals(baseBean.getType())) {
            String data = baseBean.getData();
            Log.i("音乐列表》》》", data);
            if (data != null) {
                MmData = new ArrayList<MusicMsgInfo>();
                infoList = JSONArray.parseArray(data, MusicMsgInfo.class);
                tvMusicNum.setText("共" + infoList.size() + "首歌曲");
                MmData.addAll(infoList);
                if (infoList == null) {
                    llMusiclistTop.setVisibility(View.GONE);
                    llImportMusic.setVisibility(View.VISIBLE);
                }
                MmAdapter = new MyAdapter<MusicMsgInfo>((ArrayList) MmData, R.layout.item_musiclist) {
                    @Override
                    public void bindView(ViewHolder holder, MusicMsgInfo obj) {
                        holder.setText(R.id.tv_music_title, obj.getMusicName());
                        String musicTime = SecToTime.secToTime(obj.getMusicTime());
                        holder.setText(R.id.tv_music_duration, musicTime);
                    }
                };
                listMusicList.setAdapter(MmAdapter);
                listMusicList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (AppDataCache.getInstance().getString("userType").equals("00")) {
                            editMusic(position);
                        } else {
                            ToastUtil.show(ShowMusicListActivity.this, "普通用户无法对音乐进行操作");
                        }
                    }
                });
            }
        }
        if ("EditMusicFolderNameResult".equals(baseBean.getType())) {
            EditMusicFolderNameResult partitionInfo = gson.fromJson(baseBean.getData(), EditMusicFolderNameResult.class);
            int isSucceed = partitionInfo.getResult();
            if (isSucceed == 0) {
                ToastUtil.show(ShowMusicListActivity.this, "修改失败");
                editDialog.cancel();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            } else if (isSucceed == 1) {
                ToastUtil.show(ShowMusicListActivity.this, "修改成功");
                editDialog.cancel();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                tvMusiclistName.setText(editName);
                String s = CharacterParser.getInstance().getSelling(musicFolderName).substring(0, 1).toLowerCase();
                if ("hoy".contains(s)){
                    musicCover.setImageResource(R.mipmap.music_fengmian_fengjing_small);
                }else if ("sbk".contains(s)){
                    musicCover.setImageResource(R.mipmap.music_fengmian_fgangq_small);
                }else if ("wz7".contains(s)){
                    musicCover.setImageResource(R.mipmap.music_fengmian_huanghun_small);
                }else if ("j0e".contains(s)){
                    musicCover.setImageResource(R.mipmap.music_fengmian_jiedao_small);
                }else if ("5trn".contains(s)){
                    musicCover.setImageResource(R.mipmap.music_fengmian_meishi_small);
                }else if ("d2pv".contains(s)){
                    musicCover.setImageResource(R.mipmap.music_fengmian_moren_small);
                }else if ("1qau".contains(s)){
                    musicCover.setImageResource(R.mipmap.music_fengmian_shama_small);
                }else if ("683f".contains(s)){
                    musicCover.setImageResource(R.mipmap.music_fengmian_yundong_small);
                }else if ("4cl9".contains(s)){
                    musicCover.setImageResource(R.mipmap.music_fengmian_zhuqiuchang_small);
                }else if ("xgmi".contains(s)){
                    musicCover.setImageResource(R.mipmap.music_fengmian_haiyang_small);
                }else {
                    musicCover.setImageResource(R.mipmap.music_fengmian_moren_small);
                }
            }
        }
        if ("OperateMusicFilesResult".equals(baseBean.getType())) {
            OperateMusicFilesResult operateMusicFilesResult = gson.fromJson(baseBean.getData(), OperateMusicFilesResult.class);
            int isSucceed = operateMusicFilesResult.getResult();
            if (isSucceed == 0) {
                ToastUtil.show(ShowMusicListActivity.this, "失败");
                bottomDialog.dismiss();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            } else if (isSucceed == 1) {
                init2();
                ToastUtil.show(ShowMusicListActivity.this, "成功");
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                bottomDialog.dismiss();
                MmAdapter.notifyDataSetChanged();
            } else if (isSucceed == 4) {
                ToastUtil.show(ShowMusicListActivity.this, "操作失败，目标文件夹已满");
                bottomDialog.dismiss();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        }
    }


    private void editMusic(final int position) {
        bottomDialog = new Dialog(this, R.style.BottomDialog);
        View contentView = LayoutInflater.from(this).inflate(R.layout.dialog_musicoperate, null);
        bottomDialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.setCanceledOnTouchOutside(true);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();


        final TextView tvMusicName = (TextView) contentView.findViewById(R.id.music_name);
        final TextView tvMusicFolderSource = (TextView) contentView.findViewById(R.id.music_folder_source);
        final ImageView imBottomDelete = (ImageView) contentView.findViewById(R.id.bottom_delete);
        final ImageView imBottomTrans = (ImageView) contentView.findViewById(R.id.bottom_trans);
        final ImageView imBottomCopy = (ImageView) contentView.findViewById(R.id.bottom_copy);
        final TextView tvCancelBottom = (TextView) contentView.findViewById(R.id.tv_cancel_bottom);
        final MusicMsgInfo musicMsgInfo = infoList.get(position);
        musicName = musicMsgInfo.getMusicName();
        tvMusicName.setText(musicName);
        tvMusicFolderSource.setText("来自文件夹《" + musicFolderName + "》");

        imBottomDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OperateMusicFilesInfo operateMusicFilesInfo3 = new OperateMusicFilesInfo();
                operateMusicFilesInfo3.setOperator("02");
                operateMusicFilesInfo3.setInitFolderName(musicFolderName);
                operateMusicFilesInfo3.setMusicName(musicName);
                OperateMusicFiles.sendCMD(AppDataCache.getInstance().getString("loginIp"), operateMusicFilesInfo3);
                progressDialog = new CommonProgressDialog(ShowMusicListActivity.this);  //登录进度条
                progressDialog.show();
            }
        });
        imBottomTrans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent1 = new Intent(ShowMusicListActivity.this, ChooseTargetMusicLibToBatchOperationActivity.class);
                intent1.putExtra("Operation", "move");
                intent1.putExtra("MusicLibName", musicFolderName);
                intent1.putExtra("CheckedMusic", musicName);
                startActivity(intent1);

            }
        });
        imBottomCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent2 = new Intent(ShowMusicListActivity.this, ChooseTargetMusicLibToBatchOperationActivity.class);
                intent2.putExtra("Operation", "copy");
                intent2.putExtra("MusicLibName", musicFolderName);
                intent2.putExtra("CheckedMusic", musicName);
                startActivity(intent2);

            }
        });
        tvCancelBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.dismiss();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (data == null) {
                    break;
                } else {
                    String selectedMusicLib = data.getStringExtra("MusicFolderName");
                    String editType = data.getStringExtra("editType");
                    switch (editType) {
                        case "copy":
                            progressDialog = new CommonProgressDialog(ShowMusicListActivity.this);
                            progressDialog.show();
                            OperateMusicFilesInfo operateMusicFilesInfo = new OperateMusicFilesInfo();
                            operateMusicFilesInfo.setOperator("00");
                            operateMusicFilesInfo.setInitFolderName(musicFolderName);
                            operateMusicFilesInfo.setMusicName(musicName);
                            operateMusicFilesInfo.setTargetFolderName(selectedMusicLib);
                            OperateMusicFiles.sendCMD(AppDataCache.getInstance().getString("loginIp"), operateMusicFilesInfo);

                            break;
                        case "move": {
                            progressDialog = new CommonProgressDialog(ShowMusicListActivity.this);
                            progressDialog.show();
                            OperateMusicFilesInfo operateMusicFilesInfo1 = new OperateMusicFilesInfo();
                            operateMusicFilesInfo1.setOperator("01");
                            operateMusicFilesInfo1.setInitFolderName(musicFolderName);
                            operateMusicFilesInfo1.setMusicName(musicName);
                            operateMusicFilesInfo1.setTargetFolderName(selectedMusicLib);
                            OperateMusicFiles.sendCMD(AppDataCache.getInstance().getString("loginIp"), operateMusicFilesInfo1);
                            break;
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onResume() {
        init();
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
        //EventBus.getDefault().register(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        //EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


}
