package com.itc.smartbroadcast.widget.custom;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.itc.smartbroadcast.R;


/**
 * 自定义progress
 */
public class CommonProgressDialog extends ProgressDialog {
    //    private Context context;
//    private String info;
    private boolean isTouchGone;
    private AnimationDrawable anim;

    /**
     * @param context     上下文对象
     * @param isTouchGone 点击外部是否消失
     */
    public CommonProgressDialog(Context context, boolean isTouchGone) {
        super(context, R.style.Translucent_NoTitle);
//        this.context = context;
        this.isTouchGone = isTouchGone;
    }

    /**
     * progress展示
     *
     * @param context 上下文对象
     */
    public CommonProgressDialog(Context context) {
        super(context, R.style.Translucent_NoTitle);
//        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getWindow().setDimAmount(0f);  //去除透明背景

        setContentView(R.layout.progress_dialog);

        setCanceledOnTouchOutside(isTouchGone);//点击外侧消失属性

        ImageView tv_progress_dialog = (ImageView) findViewById(R.id.tv_progress_dialog);
        anim = (AnimationDrawable) tv_progress_dialog.getDrawable();
    }

    @Override
    public void onStart() {
        super.onStart();
        anim.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {//监听用户回退键，是否强制取消progress
        //按下的如果是BACK  并且只有一次
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 1) {
            this.dismiss();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        anim.stop();
    }
}