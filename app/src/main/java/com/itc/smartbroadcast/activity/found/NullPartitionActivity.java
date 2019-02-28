package com.itc.smartbroadcast.activity.found;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.itc.smartbroadcast.R;
import com.jaeger.library.StatusBarUtil;
/**
 * create by youmu on 2018/7
 */
public class NullPartitionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_null_partition);
        StatusBarUtil.setColor(NullPartitionActivity.this, getResources().getColor(R.color.colorMain));
        ImageView btnBack = (ImageView)findViewById(R.id.bt_back_found);
        Button btnNewPart = (Button)findViewById(R.id.add_instanttask_over);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnNewPart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NullPartitionActivity.this,CreatePartitionActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
