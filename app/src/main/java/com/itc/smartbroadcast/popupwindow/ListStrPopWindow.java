package com.itc.smartbroadcast.popupwindow;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.adapter.CommonAdapter;
import com.itc.smartbroadcast.listener.PopItemClickListener;
import com.itc.smartbroadcast.widget.custom.ViewHolder;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by lhh
 * DATE 2017/4/17.
 */

public class ListStrPopWindow extends BasePopupWindow {

    public ListView listView;
    private Context context;

    private List<String> datas = new ArrayList<>();
    private CommonAdapter<String> adapter;
    private int mType;//1 我的收藏中下拉框

    public ListStrPopWindow(Context context) {
        init(context);
    }



    public void setListener(PopItemClickListener listener) {
        this.listener = listener;
    }

//    public void setType(int mType){
//        this.mType = mType;
//        Window dialogWindow = getWindow();
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
//        dialogWindow.setGravity(Gravity.TOP);
//        dialogWindow.setBackgroundDrawableResource(R.color.transparent);
//
//
//        lp.width = DensityUtil.getScreenWidth(context); // 高度设置为屏幕
//        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        if(mType == 1)
//            lp.y = DensityUtil.dip2px(context,92)+ DensityUtil.getStatusHeight();
//        dialogWindow.setAttributes(lp);
//    }


    /***
     *
     * @param context
     */

    public void init(final Context context) {
        this.context = context;


        LayoutInflater inflater = LayoutInflater.from(context);
        View conentView = inflater.inflate(R.layout.list_pop_view, null);
//        conentView.setBackgroundResource(background);

        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
//        Window dialogWindow = getWindow();
//        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
////        dialogWindow.setWindowAnimations(R.style.popupIntoAnima);
//        dialogWindow.setGravity(Gravity.TOP);
//        dialogWindow.setBackgroundDrawableResource(R.color.transparent);


//        lp.width = DensityUtil.getScreenWidth(context); // 高度设置为屏幕
//        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        if(mType == 1)
//            lp.y = DensityUtil.dip2px(context,90)+ DensityUtil.getStatusHeight();
//        else
//            lp.y = DensityUtil.dip2px(context,86)+ DensityUtil.getStatusHeight();
//        dialogWindow.setAttributes(lp);




        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态

        // 实例化一个ColorDrawable颜色为透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);

        conentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListStrPopWindow.this.dismiss();
            }
        });


//        listView = (ListView) conentView.findViewById(R.id.listpop_listview);
//        listView.setAdapter(adapter = new CommonAdapter<String>(context, datas ,R.layout.listpop_itemview) {
//            @Override
//            public void convert(ViewHolder helper, String item, int position) {
//                helper.setText(R.id.listpop_itemview_tv,datas.get(position));
//
//            }
//        });
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                ListStrPopWindow.this.dismiss();
//                if(listener != null)
//                    listener.itemClick(datas.get(position),position);
//            }
//        });

    }

    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            showAsDropDown(parent);
        } else {
            this.dismiss();
        }
    }

    public List<String> getDatas() {
        return datas;
    }

    public void setDatas(List<String> datas) {
        this.datas.clear();
        this.datas.addAll(datas);
        adapter.notifyDataSetChanged();
    }

    private PopItemClickListener listener;



}
