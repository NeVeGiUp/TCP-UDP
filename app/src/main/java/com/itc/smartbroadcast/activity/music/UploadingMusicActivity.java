package com.itc.smartbroadcast.activity.music;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.base.BaseActivity;
import com.itc.smartbroadcast.bean.Music;
import com.itc.smartbroadcast.channels.tftp.FileTool;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Ligh on 18-10-22.
 * describe _上传音乐页面
 */

public class UploadingMusicActivity extends BaseActivity {


    @BindView(R.id.textView)
    TextView textView;
    @BindView(R.id.tv_doing_num)
    TextView tvDoingNum;
    @BindView(R.id.cancel_uploading)
    Button cancelUploading;

    private Context mContext;


    @Override
    protected void init() {
        mContext = this;
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);
        Intent intent = getIntent();
        String taskListJson = intent.getStringExtra("checkedMusicListJson");
        String musicFolderName = intent.getStringExtra("MusicFolderName");
        final List<Music> musicList = JSONArray.parseArray(taskListJson, Music.class);

        textView.setText("正在上传到" + musicFolderName + "音乐文件夹");


        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    tftpTest(musicList.get(0).getMusicPath());
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void tftpTest(String filePath) {
        Log.i("test", "tftpTest: " + filePath);
        try {
            File tempFile =new File( filePath.trim());
            //上传到服务器的文件名
            String fileName = tempFile.getName();
            FileTool.upLoadFromProduction("172.16.13.30",69,"1.mp3",filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_uploadingmusic;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) throws Exception {
    }


    @OnClick(R.id.cancel_uploading)
    public void onViewClicked() {
        //点击取消上传
    }
}
