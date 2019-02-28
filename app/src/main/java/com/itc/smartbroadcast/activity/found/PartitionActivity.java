package com.itc.smartbroadcast.activity.found;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.itc.smartbroadcast.R;
import com.jaeger.library.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
/**
 * create by youmu on 2018/7
 */
public class PartitionActivity extends AppCompatActivity {
    @BindView(R.id.bt_back_event)
    ImageView btBackEvent;
    @BindView(R.id.ll_binded_terminal)
    LinearLayout llExecutionDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partition);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(PartitionActivity.this, getResources().getColor(R.color.colorMain));
        btBackEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
