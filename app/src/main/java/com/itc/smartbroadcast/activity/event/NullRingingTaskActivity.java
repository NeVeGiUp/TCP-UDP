package com.itc.smartbroadcast.activity.event;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.base.Base2Activity;
import com.jaeger.library.StatusBarUtil;

public class NullRingingTaskActivity extends Base2Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_null_ringingtask);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain),0);
        init();
    }

    private void init(){
        ImageView btnBack = (ImageView)findViewById(R.id.bt_back_event);
        Button btnAddRingingTask = (Button) findViewById(R.id.add_ringingtask_over);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnAddRingingTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NullRingingTaskActivity.this, CreateSchemeActivity.class);
                startActivity(i);
            }
        });
    }

}
