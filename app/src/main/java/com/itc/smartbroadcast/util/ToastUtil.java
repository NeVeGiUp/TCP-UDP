package com.itc.smartbroadcast.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import com.itc.smartbroadcast.R;

/**
 * Created by pengds on 2017/8/18.
 * 自定义平板吐司
 */

public class ToastUtil {
    private static Toast toast;

    private ToastUtil(Context context, String text, int duration){
        TextView textView = new TextView(context);
        textView.setBackgroundResource(R.drawable.toast_bg);
        textView.setGravity(Gravity.CENTER);
        textView.setWidth(30000);
        textView.setAlpha(1f);
        textView.setPadding(ScreenUtil.dip2px(20),ScreenUtil.dip2px(10),ScreenUtil.dip2px(20),ScreenUtil.dip2px(10));
        if(AppUtil.isIPad(context))
            textView.setTextSize(ScreenUtil.sp2px(context, 15f));
        textView.setTextColor(context.getResources().getColor(R.color.color_toast_text));
        textView.setText(text);
        if(toast == null){
            toast = new Toast(context);
            toast.setDuration(duration);
            toast.setGravity(Gravity.TOP, 0, 200);
        }
        toast.setView(textView);
        toast.show();
    }

    public static ToastUtil show(Context context, String text, int duration) {
        return new ToastUtil(context, text, duration);
    }
    public static ToastUtil show(Context context, int resId, int duration) {
        return new ToastUtil(context, context.getString(resId), duration);
    }
    public static ToastUtil show(Context context, int resId) {//默认是长吐司
        return new ToastUtil(context, context.getString(resId), Toast.LENGTH_LONG);
    }
    public static ToastUtil show(Context context, String text) {
        return new ToastUtil(context, text, Toast.LENGTH_SHORT);
    }
//    public void setGravity(int gravity, int xOffset, int yOffset) {
//        if (toast != null) {
//            toast.setGravity(gravity, xOffset, yOffset);
//        }
//    }

}
