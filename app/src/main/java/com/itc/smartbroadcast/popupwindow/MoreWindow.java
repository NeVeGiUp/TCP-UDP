package com.itc.smartbroadcast.popupwindow;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.support.v4.app.Fragment;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.event.CreateSchemeActivity;
import com.itc.smartbroadcast.activity.event.CreateInstantTaskActivity;
import com.itc.smartbroadcast.activity.event.CreateTimedTaskActivity;
import com.itc.smartbroadcast.activity.found.CreatePartitionActivity;
import com.itc.smartbroadcast.activity.personal.CreateAccountActivity;
import com.itc.smartbroadcast.bean.EditMusicFolderNameInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.EditMusicFolderName;

public class MoreWindow extends PopupWindow implements OnClickListener{

    private String TAG = MoreWindow.class.getSimpleName();
    Activity mContext;
    private int mWidth;
    private int mHeight;
    private int statusBarHeight ;
    private Bitmap mBitmap= null;
    private Bitmap overlay = null;

    private Handler mHandler = new Handler();

    public MoreWindow(Activity context) {
        mContext = context;
    }

    public void init() {
        Rect frame = new Rect();
        mContext.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        statusBarHeight = frame.top;
        DisplayMetrics metrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay()
                .getMetrics(metrics);
        mWidth = metrics.widthPixels;
        mHeight = metrics.heightPixels;

        setWidth(mWidth);
        setHeight(mHeight);
    }

    private Bitmap blur() {
        if (null != overlay) {
            return overlay;
        }
        long startMs = System.currentTimeMillis();

        View view = mContext.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        mBitmap = view.getDrawingCache();

        float scaleFactor = 8;
        float radius = 10;
        int width = mBitmap.getWidth();
        int height =  mBitmap.getHeight();

        overlay = Bitmap.createBitmap((int) (width / scaleFactor),(int) (height / scaleFactor),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(overlay);
        canvas.scale(1 / scaleFactor, 1 / scaleFactor);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(mBitmap, 0, 0, paint);

        overlay = FastBlur.doBlur(overlay, (int) radius, true);
        Log.i(TAG, "blur time is:"+(System.currentTimeMillis() - startMs));
        return overlay;
    }

    private Animation showAnimation1(final View view,int fromY ,int toY) {
        AnimationSet set = new AnimationSet(true);
        TranslateAnimation go = new TranslateAnimation(0, 0, fromY, toY);
        go.setDuration(300);
        TranslateAnimation go1 = new TranslateAnimation(0, 0, -10, 2);
        go1.setDuration(100);
        go1.setStartOffset(250);
        set.addAnimation(go1);
        set.addAnimation(go);

        set.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationStart(Animation animation) {

            }

        });
        return set;
    }


    public void showMoreWindow(View anchor,int bottomMargin){
        final RelativeLayout layout = (RelativeLayout)LayoutInflater.from(mContext).inflate(R.layout.popup_show, null);
        setContentView(layout);

        ImageView close= (ImageView)layout.findViewById(R.id.center_music_window_close);
        android.widget.RelativeLayout.LayoutParams params =new android.widget.RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
//        params.bottomMargin = bottomMargin;
//        params.addRule(RelativeLayout.BELOW, R.id.partition);
//        params.addRule(RelativeLayout.RIGHT_OF, R.id.music_folder);
//        params.topMargin = 200;
//        params.leftMargin = 18;
        close.setLayoutParams(params);
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShowing()) {
                    closeAnimation(layout);
                }
            }
        });

        showAnimation(layout);
        setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), blur()));
        setOutsideTouchable(true);
        setFocusable(true);
        showAtLocation(anchor, Gravity.BOTTOM, 0, statusBarHeight);
    }

    private void showAnimation(ViewGroup layout){
        for(int i=0;i<layout.getChildCount();i++){
            final View child = layout.getChildAt(i);
            if(child.getId() == R.id.center_music_window_close){
                continue;
            }
            child.setOnClickListener(this);
            child.setVisibility(View.INVISIBLE);
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    child.setVisibility(View.VISIBLE);
                    ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 600, 0);
                    fadeAnim.setDuration(300);
                    KickBackAnimator kickAnimator = new KickBackAnimator();
                    kickAnimator.setDuration(150);
                    fadeAnim.setEvaluator(kickAnimator);
                    fadeAnim.start();
                }
            }, i * 50);
        }

    }

    private void closeAnimation(ViewGroup layout){
        for(int i=0;i<layout.getChildCount();i++){
            final View child = layout.getChildAt(i);
            if(child.getId() == R.id.center_music_window_close){
                continue;
            }
            child.setOnClickListener(this);
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    child.setVisibility(View.VISIBLE);
                    ValueAnimator fadeAnim = ObjectAnimator.ofFloat(child, "translationY", 0, 600);
                    fadeAnim.setDuration(200);
                    KickBackAnimator kickAnimator = new KickBackAnimator();
                    kickAnimator.setDuration(100);
                    fadeAnim.setEvaluator(kickAnimator);
                    fadeAnim.start();
                    fadeAnim.addListener(new AnimatorListener() {

                        @Override
                        public void onAnimationStart(Animator animation) {


                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {


                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            child.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {


                        }
                    });
                }
            }, (layout.getChildCount()-i-1) * 30);

            if(child.getId() == R.id.timing_task){
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        dismiss();
                    }
                }, (layout.getChildCount()-i) * 30 + 80);
            }
        }

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.timing_task:
                Intent intent = new Intent(mContext, CreateTimedTaskActivity.class);
                mContext.startActivity(intent);
                dismiss();
                break;
            case R.id.instant_task:
                Intent instantTaskIntent = new Intent(mContext, CreateInstantTaskActivity.class);
                mContext.startActivity(instantTaskIntent);
                dismiss();
                break;
            case R.id.ringing_task:
                Intent ringingTaskIntent = new Intent(mContext, CreateSchemeActivity.class);
                mContext.startActivity(ringingTaskIntent);
                dismiss();
                break;
            case R.id.music_folder:
                dismiss();
                final EditDialog editDialog = new EditDialog(mContext);
                editDialog.show();
                editDialog.setOnPosNegClickListener(new EditDialog.OnPosNegClickListener() {
                    @Override
                    public void posClickListener(String value) {
                        EditMusicFolderNameInfo musicFolderNameInfo2 = new EditMusicFolderNameInfo();
                        musicFolderNameInfo2.setOperator("00");
                        musicFolderNameInfo2.setFolderName(value);
                        EditMusicFolderName.sendCMD(AppDataCache.getInstance().getString("loginIp"), musicFolderNameInfo2);
                    }
                    @Override
                    public void negCliclListener(String value) {
                        editDialog.cancel();
                    }
                });
                break;
            case R.id.partition:
                Intent intent2 = new Intent(mContext, CreatePartitionActivity.class);
                mContext.startActivity(intent2);
                dismiss();
                break;
            case R.id.account:
                Intent intent1 = new Intent(mContext, CreateAccountActivity.class);
                mContext.startActivity(intent1);
                dismiss();
                break;

            default:
                break;
        }
    }

    public void destroy() {
        if (null != overlay) {
            overlay.recycle();
            overlay = null;
            System.gc();
        }
        if (null != mBitmap) {
            mBitmap.recycle();
            mBitmap = null;
            System.gc();
        }
    }

}
