package com.itc.smartbroadcast.helper;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.itc.smartbroadcast.R;


public class CustomDialog extends Dialog{
    private Context context;
    private String title;
    private String content;
    public OnOkClickListener mOkListener;
    public OncencleClickListener mCancelListener;
    private String okStr = "确定";
    private String cancelStr = "取消";
    private boolean isCancleTouch = false;
    private boolean isFinish = false;
    private TextView custom_okcancel_dialog_content;

    /**
     * 常用dialog重载
     *
     * @param context     上下文对象，切记不要传全局上下文对象
     * @param title       标题信息（传null则隐藏标题）
     * @param content     提示信息
     * @param mOkListener ok的监听回调
     */
    public CustomDialog(Context context, String title, String content, OnOkClickListener mOkListener) {
        super(context);
        this.context = context;
        this.title = title;
        this.content = content;
        this.mOkListener = mOkListener;
    }

    /**
     *
     * @param context 上下文对象，切记不要传全局上下文对象
     * @param title  标题信息（传null则隐藏标题）
     * @param content 提示内容
     * @param leftString 左边的 忽视点击按钮 （可传null，隐藏掉此按钮）
     * @param rightString 右边的 逻辑处理按钮
     * @param mRightListener 右边的逻辑处理按钮
     */
    public CustomDialog(Context context, String title, String content, String leftString, String rightString, OnOkClickListener mRightListener) {
        super(context);
        this.context = context;
        this.title = title;
        this.content = content;
        this.cancelStr = leftString;
        this.okStr = rightString;
        this.mOkListener = mRightListener;
    }

    /**
     *
     * @param context 上下文对象，切记不要传全局上下文对象
     * @param title  标题信息（传null则隐藏标题）
     * @param content 提示内容
     * @param leftString 左边的 忽视点击按钮 （可传null，隐藏掉此按钮）
     * @param mLeftListener 左边的逻辑处理按钮
     * @param rightString 右边的 逻辑处理按钮
     * @param mRightListener 右边的逻辑处理按钮
     */
    public CustomDialog(Context context, String title, String content, String leftString,OncencleClickListener mLeftListener, String rightString, OnOkClickListener mRightListener) {
        super(context);
        this.context = context;
        this.title = title;
        this.content = content;
        this.cancelStr = leftString;
        this.okStr = rightString;
        this.mOkListener = mRightListener;
        this.mCancelListener = mLeftListener;
    }
    /**
     * 常用dialog重载
     *
     * @param context       上下文对象，切记不要传全局上下文对象
     * @param title         标题信息
     * @param content       提示信息
     * @param mOkListener   ok的监听回调
     * @param isCancleTouch 可点击周围关闭dialog  true是可关闭
     * @param isFinish      在点击取消后是否需要关闭当前页面  true是会关闭当前页
     */
    public CustomDialog(Context context, String title, String content, OnOkClickListener mOkListener, boolean isCancleTouch, boolean isFinish) {
        super(context);
        this.context = context;
        this.title = title;
        this.content = content;
        this.mOkListener = mOkListener;
        this.isCancleTouch = isCancleTouch;
        this.isFinish = isFinish;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_layout);
        TextView custom_okcancel_dialog_title = (TextView) findViewById(R.id.custom_okcancel_dialog_title);
        if(title == null){
            custom_okcancel_dialog_title.setVisibility(View.GONE);
        }else{
            custom_okcancel_dialog_title.setText(title);
        }
        TextView custom_ok_dialog_confirm = (TextView) findViewById(R.id.custom_ok_dialog_confirm);
        custom_okcancel_dialog_content = (TextView) findViewById(R.id.custom_okcancel_dialog_content);
        custom_okcancel_dialog_content.setText(content);
        if(okStr == null)
            okStr = "确定";
        custom_ok_dialog_confirm.setText(okStr);
        custom_ok_dialog_confirm.setOnClickListener(OnOkClickListener);
        TextView custom_cancel_dialog_confirm = (TextView) findViewById(R.id.custom_cancel_dialog_confirm);
        if (cancelStr == null) {
            findViewById(R.id.custom_cancel_dialog_line).setVisibility(View.GONE);
            custom_cancel_dialog_confirm.setVisibility(View.GONE);
        }else{
            custom_cancel_dialog_confirm.setOnClickListener(OnCancelClickListener);
            custom_cancel_dialog_confirm.setText(cancelStr);
        }

        this.setCanceledOnTouchOutside(isCancleTouch);
    }

    private View.OnClickListener OnCancelClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            CustomDialog.this.dismiss();
            isFinishActivity();
            if (mCancelListener != null){
                mCancelListener.onClick();
            }
        }
    };

    private View.OnClickListener OnOkClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            cancel();
            if (mOkListener != null) {
                mOkListener.onClick();
            }
        }
    };

    public interface OnOkClickListener {
        void onClick();
    }
    public interface OncencleClickListener {
        void onClick();
    }


    public void setContentStr(String str) {
        custom_okcancel_dialog_content.setText(str);
        custom_okcancel_dialog_content.postInvalidate();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            isFinishActivity();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void isFinishActivity() {
        if (isFinish)
            ((Activity) context).finish();
    }

}