package com.itc.smartbroadcast.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import com.itc.smartbroadcast.bean.Music;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service {

    private MediaPlayer mPlayer;
    private ArrayList<Music> musicPathLists;
    private int currentPos;

    public interface CallBack {
        boolean isPlayerMusic();
        int callTotalDate();
        int callCurrentTime();
        void iSeekTo(int m_second);
        void isPlayPre();
        void isPlayNext();
        boolean isPlayering();
    }

    public class MyBinder extends Binder implements CallBack {

        @Override
        public boolean isPlayerMusic() {
            return playerMusic();
        }

        @Override
        public int callTotalDate() {
            if (mPlayer != null) {
                return mPlayer.getDuration();
            } else {
                return 0;
            }
        }

        @Override
        public int callCurrentTime() {
            if (mPlayer != null) {
                return mPlayer.getCurrentPosition();
            } else {
                return 0;
            }
        }

        @Override
        public void iSeekTo(int m_second) {
            if (mPlayer != null) {
                mPlayer.seekTo(m_second);
            }
        }

        @Override
        public void isPlayPre() {
            if (--currentPos < 0) {
                currentPos = 0;
            }
            initMusic();
            playerMusic();
        }

        @Override
        public void isPlayNext() {
            if (++currentPos > musicPathLists.size() - 1) {
                currentPos = musicPathLists.size() - 1;
            }
            initMusic();
            playerMusic();
        }

        @Override
        public boolean isPlayering() {
            if(mPlayer.isPlaying()){
                return true;
            }else{
                return false;
            }
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mPlayer = new MediaPlayer();
    }

    private void initMusic() {
        // 根路径
        //      String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/tmd.mp3";
        mPlayer.reset();
        try {
            mPlayer.setDataSource(musicPathLists.get(currentPos).getMusicPath());
            mPlayer.prepare();

            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    currentPos++;
                    if (currentPos >= musicPathLists.size()) {
                        currentPos = 0;
                    }
                    //       mp.start();
                    initMusic();
                    playerMusic();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean playerMusic() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            return false;
        } else {
            mPlayer.start();
            return true;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        musicPathLists = intent.getParcelableArrayListExtra("MUSIC_LIST");
        currentPos = intent.getIntExtra("CURRENT_POSITION", -1);

        initMusic();

        playerMusic();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer.release();
        }
    }
}
