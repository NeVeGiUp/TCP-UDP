package com.itc.smartbroadcast.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.itc.smartbroadcast.R;

import java.util.ArrayList;
import java.util.List;

public class ChooseTerminalAdapter extends BaseAdapter {
    private List<String> macList;
    private List<String> stringList;
    private Context mContext;
    private List<String> checkBoxIDList;            //存储checkBox的值

    //get set
    public List<String> getCheckBoxIDList() {
        return checkBoxIDList;
    }
    public void setCheckBoxIDList(List<String> checkBoxIDList) {
        this.checkBoxIDList = checkBoxIDList;
    }
    public ChooseTerminalAdapter(List<String> stringList, List<String> macList, Context mContext) {
        this.stringList = stringList;
        this.macList = macList;
        this.mContext = mContext;
        checkBoxIDList= new ArrayList<>();
    }

    @Override
    public int getCount() {
        return stringList.size();
    }

    @Override
    public Object getItem(int position) {
        return stringList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final TestViewHolder testViewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_chooseterminal, null);
            testViewHolder = new TestViewHolder();

            testViewHolder.item_checkBox = (CheckBox) convertView.findViewById(R.id.cb_terminal_name);
            testViewHolder.item_MAC = (TextView) convertView.findViewById(R.id.tv_bind_terminal_mac);

            convertView.setTag(testViewHolder);
        } else {
            testViewHolder = (TestViewHolder) convertView.getTag();
        }

        //设置checkBox的值
        testViewHolder.item_checkBox.setText(stringList.get(position).toString());
        testViewHolder.item_MAC.setText(macList.get(position).toString());

        //获取复选框选中状态改变事件进行增删改
        testViewHolder.item_checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                /*
                 * b=选中状态
                 * if b = true 将值添加至checkBoxIDList
                 * if b = false 将值从checkBoxIDList移除
                 * */
                if (b) {

                    //checkBoxIDList.add(testViewHolder.item_checkBox.getText().toString());
                    checkBoxIDList.add(testViewHolder.item_MAC.getText().toString());

                } else {

                    //checkBoxIDList.remove(testViewHolder.item_checkBox.getText().toString());
                    checkBoxIDList.remove(testViewHolder.item_MAC.getText().toString());

                }
            }
        });
        return convertView;
    }

    static class TestViewHolder {

        CheckBox item_checkBox;
        TextView item_MAC;

    }

}
