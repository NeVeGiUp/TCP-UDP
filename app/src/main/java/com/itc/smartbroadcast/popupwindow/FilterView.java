package com.itc.smartbroadcast.popupwindow;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itc.smartbroadcast.R;



public class FilterView extends LinearLayout implements View.OnClickListener{

    private int defaulColor = Color.parseColor("#333333");
    private int defaulImage = R.mipmap.common_but_pullup_down;
    private TextView text;
    private ImageView image;
    private boolean isDown;

    private FilterClickListeren listeren;

    public void setListeren(FilterClickListeren listeren) {
        this.listeren = listeren;
    }

    public FilterView(Context context) {
        this(context,null);
    }

    public FilterView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public FilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FilterView);
        float textsize = a.getFloat(R.styleable.FilterView_textsize, 13);
        int textcolor = a.getColor(R.styleable.FilterView_textcolor,defaulColor);
        float imagesize = a.getDimension(R.styleable.FilterView_imagesize,12);
        int  drawable = a.getResourceId(R.styleable.FilterView_imagesrc,defaulImage);
        String str = a.getString(R.styleable.FilterView_text);

        this.setOrientation(LinearLayout.HORIZONTAL);
        this.setGravity(Gravity.CENTER);
        text = new TextView(context);
        text.setTextSize(textsize);
        text.setTextColor(textcolor);
        text.setMaxEms(5);
        text.setMaxLines(1);
        text.setEllipsize(TextUtils.TruncateAt.END);
        text.setText(str);
        this.addView(text);

        image = new ImageView(context);
        image.setImageResource(drawable);
        LayoutParams params = new LayoutParams((int)imagesize,(int)imagesize);
        params.leftMargin = 10;
        image.setLayoutParams(params);
        this.addView(image);

        this.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(!isDown){
            ObjectAnimator obj = ObjectAnimator.ofFloat(image,"rotation",0,180);
            obj.setDuration(200);
            obj.start();
            isDown = true;
        }else{
            ObjectAnimator obj = ObjectAnimator.ofFloat(image,"rotation",180,0);
            obj.setDuration(200);
            obj.start();
            isDown = false;
        }

        if(listeren != null)
            listeren.filterClick(this);
    }


    public void setAnimation(){
        if(!isDown){
            ObjectAnimator obj = ObjectAnimator.ofFloat(image,"rotation",0,180);
            obj.setDuration(200);
            obj.start();
            isDown = true;
        }else{
            ObjectAnimator obj = ObjectAnimator.ofFloat(image,"rotation",180,0);
            obj.setDuration(200);
            obj.start();
            isDown = false;
        }
    }

    public void setText(String str){
        text.setText(str);
    }


    public interface FilterClickListeren{
        void filterClick(View v);
    }


}
