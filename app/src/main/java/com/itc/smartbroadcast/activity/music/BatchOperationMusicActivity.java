package com.itc.smartbroadcast.activity.music;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.adapter.BatchOperationMusicAdapter;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.MusicMsgInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.GetMusicList;
import com.itc.smartbroadcast.util.ToastUtil;
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
 * create by youmu on 2018/9
 */
public class BatchOperationMusicActivity extends AppCompatActivity {
    @BindView(R.id.bt_all_unclick)
    Button btAllUnclick;
    @BindView(R.id.bt_save)
    Button btSave;
    @BindView(R.id.list_choose_music)
    ListView listChooseMusic;
    @BindView(R.id.ll_batch_delete)
    LinearLayout llBatchDelete;
    @BindView(R.id.ll_batch_move)
    LinearLayout llBatchMove;
    @BindView(R.id.ll_batch_copy)
    LinearLayout llBatchCopy;
    private List<String> MusicNamedata;
    private BatchOperationMusicAdapter adapter;
    String musicListJson, musicFolderName;

    List<MusicMsgInfo> musicMsgInfoList = new ArrayList<>();

    List<MusicMsgInfo> musicMsgInfos = new ArrayList<>();

    public BatchOperationMusicActivity batchOperationMusicActivity;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_music_batch_operation);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(BatchOperationMusicActivity.this, getResources().getColor(R.color.colorMain));
        init();
    }

    private void init() {
        Intent intent = getIntent();
        musicFolderName = intent.getStringExtra("MusicFolderName");
        musicListJson = intent.getStringExtra("musicList");

        if (musicListJson != null && !musicListJson.equals("")) {
            musicMsgInfoList = JSONArray.parseArray(musicListJson, MusicMsgInfo.class);
        }

        GetMusicList.sendCMD(AppDataCache.getInstance().getString("loginIp"), musicFolderName);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        final Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);
        if ("getMusicList".equals(baseBean.getType())) {
            final String data = baseBean.getData();
            Log.i("音乐》》》", data);
            if (data != null) {
                ArrayList<MusicMsgInfo> musicMsgInfos = JSON.parseObject(data, new TypeReference<ArrayList<MusicMsgInfo>>() {
                });
                MusicNamedata = new ArrayList<>();
                for (MusicMsgInfo musicMsgInfo : musicMsgInfos) {
                    String musicName = musicMsgInfo.getMusicName().toString();
                    MusicNamedata.add(musicName);
                }
                adapter = new BatchOperationMusicAdapter(MusicNamedata, BatchOperationMusicActivity.this);
                listChooseMusic.setAdapter(adapter);
            }
        }
    }


    public void resultPost(String result) {
        //数据是使用Intent返回
        Intent intent = new Intent();
        //把返回数据存入Intent
        intent.putExtra("music_result_list", result);
        //设置返回数据
        this.setResult(RESULT_OK, intent);
        //关闭Activity
        this.finish();
    }

    /**
     * 监听Back键按下事件,方法2:
     * 注意:
     * 返回值表示:是否能完全处理该事件
     * 在此处返回false,所以会继续传播该事件.
     * 在具体项目中此处的返回值视情况而定.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            String result = "";
            resultPost(result);
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

//    @Override
//    protected void onPause(){
//        EventBus.getDefault().unregister(this);
//        super.onPause();
//    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @OnClick({R.id.bt_all_unclick, R.id.bt_save, R.id.ll_batch_delete, R.id.ll_batch_move, R.id.ll_batch_copy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_all_unclick:
                if (btAllUnclick.getText().toString().endsWith("全选")){
                    btAllUnclick.setText("全不选");
                    adapter.setCheckAll();
                }else {
                    btAllUnclick.setText("全选");
                    adapter.setNoCheckAll();
                }
                break;
            case R.id.bt_save:
                String result = "";
                resultPost(result);
                break;
            case R.id.ll_batch_delete:
                List<String> checkMusic = new ArrayList<>();
                for (int i = 0; i < adapter.getCheckBoxIDList().size(); i++) {
                    String checked = adapter.getCheckBoxIDList().get(i).toString();
                    checkMusic.add(checked);
                }
                if (checkMusic.size() == 0){
                    ToastUtil.show(BatchOperationMusicActivity.this,"请至少选择一首音乐");
                }else {
                    String checkedMusic = checkMusic.toString();
                    Log.i("已选择的音乐名MAC", checkedMusic);
                    Intent intent = new Intent(BatchOperationMusicActivity.this, BatchDoingActivity.class);
                    intent.putExtra("Operation", "delete");
                    intent.putExtra("MusicLibName", musicFolderName);
                    intent.putExtra("CheckedMusic", checkedMusic);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.ll_batch_move:

                List<String> checkMusic1 = new ArrayList<>();
                for (int i = 0; i < adapter.getCheckBoxIDList().size(); i++) {
                    String checked = adapter.getCheckBoxIDList().get(i).toString();
                    checkMusic1.add(checked);
                }
                if (checkMusic1.size() == 0){
                    ToastUtil.show(BatchOperationMusicActivity.this,"请至少选择一首音乐");
                }else {
                    String checkedMusic1 = checkMusic1.toString();
                    Log.i("已选择的音乐名MAC", checkedMusic1);
                    Intent intent1 = new Intent(BatchOperationMusicActivity.this, ChooseTargetMusicLibToBatchOperationActivity.class);
                    intent1.putExtra("Operation", "move");
                    intent1.putExtra("MusicLibName", musicFolderName);
                    intent1.putExtra("CheckedMusic", checkedMusic1);
                    startActivity(intent1);
                    finish();
                }
                break;
            case R.id.ll_batch_copy:
                List<String> checkMusic2 = new ArrayList<>();
                for (int i = 0; i < adapter.getCheckBoxIDList().size(); i++) {
                    String checked = adapter.getCheckBoxIDList().get(i).toString();
                    checkMusic2.add(checked);
                }
                if (checkMusic2.size() == 0){
                    ToastUtil.show(BatchOperationMusicActivity.this,"请至少选择一首音乐");
                }else {
                    String checkedMusic2 = checkMusic2.toString();
                    Log.i("已选择的音乐名MAC", checkedMusic2);
                    Intent intent2 = new Intent(BatchOperationMusicActivity.this, ChooseTargetMusicLibToBatchOperationActivity.class);
                    intent2.putExtra("Operation", "copy");
                    intent2.putExtra("MusicLibName", musicFolderName);
                    intent2.putExtra("CheckedMusic", checkedMusic2);
                    startActivity(intent2);
                    finish();
                }
                break;
        }
    }
}