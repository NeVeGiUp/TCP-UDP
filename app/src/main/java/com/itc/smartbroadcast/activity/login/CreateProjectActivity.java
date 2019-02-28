package com.itc.smartbroadcast.activity.login;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.channels.http.CloudProtocolModel;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateProjectActivity extends AppCompatActivity {

    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R.id.bt_create_project)
    Button btCreateProject;
    @BindView(R.id.et_machine_code)
    EditText etMachineCode;
    @BindView(R.id.et_project_name)
    EditText etProjectName;
    @BindView(R.id.et_project_owner)
    EditText etProjectOwner;
    @BindView(R.id.et_owner_phone)
    EditText etOwnerPhone;
    @BindView(R.id.et_owner_email)
    EditText etOwnerEmail;
    @BindView(R.id.et_owner_unit)
    EditText etOwnerUnit;
    @BindView(R.id.et_owner_address)
    EditText etOwnerAddress;
    @BindView(R.id.et_remarks)
    EditText etRemarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_project);
        ButterKnife.bind(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);
    }

    @Override
    public void onResume() {
        EventBus.getDefault().register(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }



    private void registerTimer(){
        String machineCode = etMachineCode.getText().toString().trim();
        String projectName = etProjectName.getText().toString().trim();
        String projectOwner = etProjectOwner.getText().toString().trim();
        String ownerPhone = etOwnerPhone.getText().toString().trim();
        String ownerEmail = etOwnerEmail.getText().toString().trim();
        String ownerUnit = etOwnerUnit.getText().toString().trim();
        String ownerAddress = etOwnerAddress.getText().toString().trim();
        String remarks = etRemarks.getText().toString().trim();

        HashMap<String, String> mapData = new HashMap<>();
        mapData.put("name", projectName);
        mapData.put("contact_user", projectOwner);
        mapData.put("contact_telephone", ownerPhone);
        mapData.put("contact_email", ownerEmail);
        mapData.put("contact_company", ownerUnit);
        mapData.put("contact_address", ownerAddress);
        mapData.put("comment", remarks);
        mapData.put("machine_code", machineCode);
        CloudProtocolModel.postCloudAddProject(mapData,this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);
        Log.i("获取项目激活情况>>>", baseBean.getData());
        BaseBean baseBean1 = gson.fromJson(baseBean.getData(), BaseBean.class);

    }

    @OnClick({R.id.rl_back, R.id.bt_create_project})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.bt_create_project:
                registerTimer();
                break;
        }
    }
}
