package com.itc.smartbroadcast.activity.music;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.BatchOperateMusicMsgReplyInfo;
import com.itc.smartbroadcast.bean.FileOperationProgressResult;
import com.itc.smartbroadcast.bean.OperateMusicFilesInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.BatchOperateMusicFiles;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * create by youmu on 2018/10
 */

public class BatchDoingActivity extends AppCompatActivity {
    private Button cancel_uploading;
    private String checkedMusic, operation, musicLibName, targetMusicLibName;
    private ArrayList<String> musicArr;
    private LinkedList<String> musicList;
    private TextView tv_doing_num, tv_doing_fail, tvDoingTitle, doingMusic,tvMusicPercent;
    private int failNum, successNum, allNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_batch_doing);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(BatchDoingActivity.this, getResources().getColor(R.color.colorMain), 0);
        init();
    }

    private void init() {
        cancel_uploading = (Button) findViewById(R.id.cancel_uploading);
        tv_doing_num = (TextView) findViewById(R.id.tv_doing_num);
        tv_doing_fail = (TextView) findViewById(R.id.tv_doing_fail);
        tvDoingTitle = (TextView) findViewById(R.id.textView);
        doingMusic = (TextView) findViewById(R.id.doing_music);
        tvMusicPercent = (TextView)findViewById(R.id.tv_music_percent);
        Intent intent = getIntent();
        checkedMusic = intent.getStringExtra("CheckedMusic");
        operation = intent.getStringExtra("Operation");
        musicLibName = intent.getStringExtra("MusicLibName");
        targetMusicLibName = intent.getStringExtra("targetMusicLibName");
        checkedMusic = checkedMusic.replaceAll("\\[", "");
        checkedMusic = checkedMusic.replaceAll("]", "");
        musicArr = new ArrayList(Arrays.asList(checkedMusic.split(",")));
        allNum = musicArr.size();
        musicList = new LinkedList<>(musicArr);
        tv_doing_num.setText("还剩" + musicList.size() + "首歌");
        switch (operation) {
            case "delete":
                batchDelete();
                tvDoingTitle.setText("正在删除" + musicLibName + "中的音乐");
                doingMusic.setText("正在操作的歌曲名：" + musicList.removeFirst());
                cancel_uploading.setText("取消删除");
                break;
            case "move":
                batchMove();
                tvDoingTitle.setText("正在转移" + musicLibName + "中的音乐到" + targetMusicLibName);
                doingMusic.setText("正在操作的歌曲名：" + musicList.removeFirst());
                cancel_uploading.setText("取消转移");
                break;
            case "copy":
                batchCopy();
                tvDoingTitle.setText("正在复制" + musicLibName + "中的音乐到" + targetMusicLibName);
                doingMusic.setText("正在操作的歌曲名：" + musicList.removeFirst());
                cancel_uploading.setText("取消复制");
                break;
        }
        cancel_uploading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BatchDoingActivity.this);
                view = View.inflate(BatchDoingActivity.this, R.layout.dialog_tips, null);
                final TextView tvMsg = (TextView) view.findViewById(R.id.tv_msg);
                final Button btnOk = (Button) view.findViewById(R.id.btn_ok);
                final Button btnNo = (Button) view.findViewById(R.id.btn_no);
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getWindow().setContentView(view);
                tvMsg.setText("确定要取消?");
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OperateMusicFilesInfo operateMusicFilesInfo3 = new OperateMusicFilesInfo();
                        operateMusicFilesInfo3.setOperator("03");
                        operateMusicFilesInfo3.setInitFolderName(musicLibName);
                        operateMusicFilesInfo3.setMusicNameList(musicArr);
                        BatchOperateMusicFiles.sendCMD(AppDataCache.getInstance().getString("loginIp"), operateMusicFilesInfo3);
                        finish();
                    }
                });
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);
        if ("BatchOperateMusicMsgReplyResult".equals(baseBean.getType())) {
            BatchOperateMusicMsgReplyInfo batchOperateMusicMsgReplyInfo = gson.fromJson(baseBean.getData(), BatchOperateMusicMsgReplyInfo.class);
            int result = batchOperateMusicMsgReplyInfo.getFileOperator();
            int batchResult = batchOperateMusicMsgReplyInfo.getBatchOperator();
            int doingnum = musicList.size() + 1;
            if (batchResult == 0) {
                tvMusicPercent.setVisibility(View.INVISIBLE);
                successNum = allNum - failNum;
                doingMusic.setText("操作完毕");
                tv_doing_num.setText("成功" + successNum + "首歌");
                cancel_uploading.setText("确定");
                cancel_uploading.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            } else {
                if (result == 0) {
                    tvMusicPercent.setVisibility(View.VISIBLE);
                    successNum = successNum + 1;
                    doingnum = doingnum - 1;
                    doingMusic.setText("正在操作的曲目：" + musicList.removeFirst());
                    tv_doing_num.setText("成功"+successNum+"首歌，"+"还剩" + doingnum + "首歌");
                    tv_doing_fail.setText("失败" + failNum + "首歌");
                } else if (result == 2) {
                    failNum = failNum + 1;
                    doingMusic.setText("操作失败，目标文件夹已满");
                    tv_doing_num.setVisibility(View.INVISIBLE);
                    tv_doing_fail.setVisibility(View.INVISIBLE);
                    cancel_uploading.setText("确定");
                    cancel_uploading.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                } else {
                    tvMusicPercent.setVisibility(View.VISIBLE);
                    failNum = failNum + 1;
                    doingMusic.setText("正在操作的曲目：" + musicList.removeFirst());
                    tv_doing_num.setText("成功"+successNum+"首歌，"+"还剩" + doingnum + "首歌");
                    tv_doing_fail.setText("失败" + failNum + "首歌");
                }
            }
        }
        if ("FileOperationProgressResult".equals(baseBean.getType())) {
            FileOperationProgressResult fileOperationProgressResult = gson.fromJson(baseBean.getData(), FileOperationProgressResult.class);
            int iSbProgress = fileOperationProgressResult.getProgress();
            tvMusicPercent.setText("（"+iSbProgress+"%）");
        }
    }
//    @Override
//    protected void onResume() {
//        EventBus.getDefault().register(this);
//        super.onResume();
//    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    //批量删除
    private void batchDelete() {
        OperateMusicFilesInfo operateMusicFilesInfo3 = new OperateMusicFilesInfo();
        operateMusicFilesInfo3.setOperator("02");
        operateMusicFilesInfo3.setMusicTotal(musicArr.size());
        operateMusicFilesInfo3.setInitFolderName(musicLibName);
        operateMusicFilesInfo3.setMusicNameList(musicArr);
        operateMusicFilesInfo3.setMusicTotal(musicArr.size());
        BatchOperateMusicFiles.sendCMD(AppDataCache.getInstance().getString("loginIp"), operateMusicFilesInfo3);
    }

    //批量转移
    private void batchMove() {
        OperateMusicFilesInfo operateMusicFilesInfo2 = new OperateMusicFilesInfo();
        operateMusicFilesInfo2.setOperator("01");
        operateMusicFilesInfo2.setMusicTotal(musicArr.size());
        operateMusicFilesInfo2.setInitFolderName(musicLibName);
        operateMusicFilesInfo2.setMusicNameList(musicArr);
        operateMusicFilesInfo2.setTargetFolderName(targetMusicLibName);
        operateMusicFilesInfo2.setMusicTotal(musicArr.size());
        BatchOperateMusicFiles.sendCMD(AppDataCache.getInstance().getString("loginIp"), operateMusicFilesInfo2);
    }

    //批量复制
    private void batchCopy() {
        OperateMusicFilesInfo operateMusicFilesInfo = new OperateMusicFilesInfo();
        operateMusicFilesInfo.setOperator("00");
        operateMusicFilesInfo.setMusicTotal(musicArr.size());
        operateMusicFilesInfo.setInitFolderName(musicLibName);
        operateMusicFilesInfo.setMusicNameList(musicArr);
        operateMusicFilesInfo.setMusicTotal(musicArr.size());
        operateMusicFilesInfo.setTargetFolderName(targetMusicLibName);
        BatchOperateMusicFiles.sendCMD(AppDataCache.getInstance().getString("loginIp"), operateMusicFilesInfo);
    }

    //禁用返回键
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (cancel_uploading.getText().toString().contains("取消")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(BatchDoingActivity.this);
                View view = View.inflate(BatchDoingActivity.this, R.layout.dialog_tips, null);
                final TextView tvMsg = (TextView) view.findViewById(R.id.tv_msg);
                final Button btnOk = (Button) view.findViewById(R.id.btn_ok);
                final Button btnNo = (Button) view.findViewById(R.id.btn_no);
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getWindow().setContentView(view);
                tvMsg.setText("确定要取消?");
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OperateMusicFilesInfo operateMusicFilesInfo3 = new OperateMusicFilesInfo();
                        operateMusicFilesInfo3.setOperator("03");
                        operateMusicFilesInfo3.setInitFolderName(musicLibName);
                        operateMusicFilesInfo3.setMusicNameList(musicArr);
                        BatchOperateMusicFiles.sendCMD(AppDataCache.getInstance().getString("loginIp"), operateMusicFilesInfo3);
                        finish();
                    }
                });
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            } else {
                finish();
            }
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
