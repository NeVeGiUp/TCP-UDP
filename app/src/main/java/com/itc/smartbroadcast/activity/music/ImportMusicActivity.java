package com.itc.smartbroadcast.activity.music;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.event.BatchManagerRingingTaskActivity;
import com.itc.smartbroadcast.activity.event.BatchUpdateRingingTaskActivity;
import com.itc.smartbroadcast.adapter.LocalMusicListAdapter;
import com.itc.smartbroadcast.base.BaseActivity;
import com.itc.smartbroadcast.bean.Music;
import com.itc.smartbroadcast.util.ToastUtil;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class ImportMusicActivity extends BaseActivity {


    @BindView(R.id.batch_select_tv)
    TextView batchSelectTv;
    @BindView(R.id.tv_close)
    TextView tvClose;
    @BindView(R.id.topbap_select_music)
    RelativeLayout topbapSelectMusic;
    @BindView(R.id.music_search)
    EditText musicSearch;
    @BindView(R.id.et_search_music)
    RelativeLayout etSearchMusic;
    @BindView(R.id.all_music_size)
    TextView allMusicSize;
    @BindView(R.id.music_rv)
    RecyclerView musicRv;
    @BindView(R.id.ll_no_data)
    LinearLayout llNoData;
    @BindView(R.id.bottom_import_music_ll)
    LinearLayout bottomImportMusicLl;

    private Handler mHandler = new Handler();
    private ArrayList<Music> mMediaLists = new ArrayList<>();
    private LocalMusicListAdapter mLocalMusicListAdapter;
    private String musicFolderName;


    @Override
    protected void init() {
        StatusBarUtil.setColor(ImportMusicActivity.this, getResources().getColor(R.color.colorMain));
        Intent intent = getIntent();
        if (intent != null) {
            musicFolderName = intent.getStringExtra("MusicFolderName");
        }
        initRv();
        asyncQueryMedia();

    }

    private void initRv() {
        musicRv.setLayoutManager(new LinearLayoutManager(this));
        musicRv.setHasFixedSize(true);
        musicRv.setFocusableInTouchMode(false);
        musicRv.requestFocus();

        mLocalMusicListAdapter = new LocalMusicListAdapter(this);
        musicRv.setAdapter(mLocalMusicListAdapter);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_importmusic;
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    public void asyncQueryMedia() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mMediaLists.clear();
                queryMusic(Environment.getExternalStorageDirectory() + File.separator);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        //设置数据
                        if (mMediaLists.size() > 0) {
                            llNoData.setVisibility(View.GONE);
                            musicRv.setVisibility(View.VISIBLE);
                            allMusicSize.setText("共"+mMediaLists.size()+"首歌, "+"当前选定"+mLocalMusicListAdapter.getCheckBoxIDList().size()+"首");
                            mLocalMusicListAdapter.setList(mMediaLists);
                        } else {
                            llNoData.setVisibility(View.VISIBLE);
                            musicRv.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }).start();
    }


    /**
     * 获取目录下的歌曲
     *
     * @param dirName
     */
    public void queryMusic(String dirName) {
        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                MediaStore.Audio.Media.DATA + " like ?",
                new String[]{dirName + "%"},
                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        if (cursor == null) return;
        Music music;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            // 如果不是音乐
            String isMusic = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.IS_MUSIC));
            if (isMusic != null && isMusic.equals("")) continue;
            String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));
            String artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
            if (isRepeat(title, artist)) continue;
            music = new Music();
            music.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
            music.setTitle(title);
            music.setArtist(artist);
            String musicPath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
            music.setMusicPath(musicPath);
            music.setPlayLength(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
            music.setLength(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)));
            music.setImage(getAlbumImage(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID))));

            //后缀名为MP3的才能添加进来
            if (musicPath.endsWith("mp3") || musicPath.endsWith("MP3"))
                mMediaLists.add(music);
        }

        cursor.close();
    }


    /**
     * 根据音乐名称和艺术家来判断是否重复包含了
     *
     * @param title
     * @param artist
     * @return
     */
    private boolean isRepeat(String title, String artist) {
        for (Music music : mMediaLists) {
            if (title.equals(music.getTitle()) && artist.equals(music.getArtist())) {
                return true;
            }
        }
        return false;
    }


    /**
     * 根据歌曲id获取图片
     *
     * @param albumId
     * @return
     */
    private String getAlbumImage(int albumId) {
        String result = "";
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(
                    Uri.parse("content://media/external/audio/albums/"
                            + albumId), new String[]{"album_art"}, null,
                    null, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); ) {
                result = cursor.getString(0);
                break;
            }
        } catch (Exception e) {
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
        return null == result ? null : result;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
    }


    @OnClick({R.id.batch_select_tv, R.id.tv_close, R.id.bottom_import_music_ll})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.batch_select_tv:
                if (batchSelectTv.getText().toString().equals("全选")) {
                    batchSelectTv.setText("反选");
                    mLocalMusicListAdapter.setCheckAll();
                } else {
                    batchSelectTv.setText("全选");
                    mLocalMusicListAdapter.setNoCheckAll();
                }
                break;
            case R.id.tv_close:
                finish();
                break;
            case R.id.bottom_import_music_ll:
                List<Music> checkMusicList = mLocalMusicListAdapter.getCheckBoxIDList();
                if (checkMusicList.size() <= 0) {
                    ToastUtil.show(this, "请选择需要上传的本地音乐！");
                    break;
                }
                String taskListJson = JSONArray.toJSONString(checkMusicList);
                Intent intent = new Intent(this, UploadingMusicActivity.class);
                intent.putExtra("checkedMusicListJson", taskListJson);
                intent.putExtra("MusicFolderName", musicFolderName);
                startActivity(intent);
                break;
        }
    }

}
