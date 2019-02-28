package com.itc.smartbroadcast.activity.personal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.bean.AccManageInfo;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditAccManageResult;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.EditAccountManage;
import com.itc.smartbroadcast.popupwindow.EditPhoneDialog;
import com.itc.smartbroadcast.popupwindow.PasswordEditDialog;
import com.itc.smartbroadcast.util.CheckEmojiUtils;
import com.itc.smartbroadcast.util.ToastUtil;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateAccountActivity extends AppCompatActivity {

    @BindView(R.id.bt_back_personal)
    RelativeLayout btBackPersonal;
    @BindView(R.id.tv_save_part)
    Button tvSavePart;
    @BindView(R.id.et_part_name)
    EditText etPartName;
    @BindView(R.id.tv_account_type)
    TextView tvAccountType;
    @BindView(R.id.rl_account_type)
    RelativeLayout rlAccountType;
    @BindView(R.id.tv_account_password)
    TextView tvAccountPassword;
    @BindView(R.id.rl_account_pass)
    RelativeLayout rlAccountPass;
    @BindView(R.id.tv_not_bind)
    TextView tvNotBind;
    @BindView(R.id.rl_bind_terminal)
    RelativeLayout rlBindTerminal;
    @BindView(R.id.bt_edit_devicelist)
    Button btEditDevicelist;
    @BindView(R.id.tv_select_device)
    TextView tvSelectDevice;
    @BindView(R.id.ll_binded_terminal)
    LinearLayout llBindedTerminal;


    AlertDialog alertDialog;
    List<FoundDeviceInfo> deviceList = new ArrayList<>();
    @BindView(R.id.tv_phone_number)
    TextView tvPhoneNumber;
    @BindView(R.id.rl_phone_number)
    RelativeLayout rlPhoneNumber;
    private String checkedMac = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);
        if (checkedMac.equals("")) {
            llBindedTerminal.setVisibility(View.INVISIBLE);
        } else {
            llBindedTerminal.setVisibility(View.INVISIBLE);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        etPartName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etPartName.getText().length() < 1) {
                    tvSavePart.setEnabled(false);
                    tvSavePart.setTextColor(getResources().getColor(R.color.colorGray));
                } else {
                    tvSavePart.setEnabled(true);
                    tvSavePart.setTextColor(getResources().getColor(R.color.colorWhite));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        initOnclick();
    }

    private void init() {

    }

    public void initOnclick() {
        rlAccountType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRadioButtonDialog();
            }
        });
        rlAccountPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final PasswordEditDialog editDialog = new PasswordEditDialog(CreateAccountActivity.this);
                editDialog.show();
                editDialog.setOnPosNegClickListener(new PasswordEditDialog.OnPosNegClickListener() {
                    @Override
                    public void posClickListener(String value) {
                        tvAccountPassword.setText(value);
                        editDialog.cancel();
                    }

                    @Override
                    public void negCliclListener(String value) {
                        editDialog.cancel();
                    }
                });
            }
        });
        tvAccountPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final PasswordEditDialog editDialog = new PasswordEditDialog(CreateAccountActivity.this);
                editDialog.show();
                editDialog.setOnPosNegClickListener(new PasswordEditDialog.OnPosNegClickListener() {
                    @Override
                    public void posClickListener(String value) {
                        tvAccountPassword.setText(value);
                        editDialog.cancel();
                    }

                    @Override
                    public void negCliclListener(String value) {
                        editDialog.cancel();
                    }
                });
            }
        });
        rlBindTerminal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDevice();
            }
        });
        btBackPersonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btEditDevicelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDevice();
            }
        });
        tvSavePart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etPartName.getText().toString().trim().length() < 2 || CheckEmojiUtils.containsEmoji(etPartName.getText().toString().trim())) {
                    ToastUtil.show(CreateAccountActivity.this, "请正确输入账户名");
                } else if (tvAccountType.getText().equals("未选择")) {
                    ToastUtil.show(CreateAccountActivity.this, "请选择账号类型");
                } else if (tvAccountPassword.getText().equals("未选择")) {
                    ToastUtil.show(CreateAccountActivity.this, "请输入密码");
                } else if (checkedMac.equals("") && tvAccountType.getText().equals("普通用户")) {
                    ToastUtil.show(CreateAccountActivity.this, "请选择终端");
                } else if (tvAccountPassword.getText().toString().trim().length() != 6) {
                    ToastUtil.show(CreateAccountActivity.this, "请输入正确的密码");
                } else {
                    CreateAccount();
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    finish();
                }
            }
        });
        rlPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditPhoneDialog editDialog = new EditPhoneDialog(CreateAccountActivity.this);
                editDialog.show();
                editDialog.setOnPosNegClickListener(new EditPhoneDialog.OnPosNegClickListener() {
                    @Override
                    public void posClickListener(String value) {
                        tvPhoneNumber.setText(value);
                        editDialog.cancel();
                    }
                    @Override
                    public void negCliclListener(String value) {
                        editDialog.cancel();
                    }
                });
            }
        });
        tvPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditPhoneDialog editDialog = new EditPhoneDialog(CreateAccountActivity.this);
                editDialog.show();
                editDialog.setOnPosNegClickListener(new EditPhoneDialog.OnPosNegClickListener() {
                    @Override
                    public void posClickListener(String value) {
                        tvPhoneNumber.setText(value);
                        editDialog.cancel();
                    }
                    @Override
                    public void negCliclListener(String value) {
                        editDialog.cancel();
                    }
                });
            }
        });
    }


    private void showRadioButtonDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccountActivity.this);
        View view = View.inflate(CreateAccountActivity.this, R.layout.dialog_select_usertype, null);
        builder.setView(view);
        builder.setCancelable(true);
        final AlertDialog dialog = builder.create();
        dialog.show();
        final RadioGroup rgUserType = (RadioGroup) view.findViewById(R.id.rg_select_usertype);
        final RadioButton rbAdmin = (RadioButton) view.findViewById(R.id.rb_admin);
        final RadioButton rbNormal = (RadioButton) view.findViewById(R.id.rb_normal_user);
        final TextView tvCancel = (TextView) view.findViewById(R.id.custom_cancel_dialog_confirm);
        final TextView tvConfirm = (TextView) view.findViewById(R.id.custom_ok_dialog_confirm);
        String userType = tvAccountType.getText().toString();
        if (userType.equals("管理员")) {
            rbAdmin.setChecked(true);
        } else if (userType.equals("普通用户")) {
            rbNormal.setChecked(true);
        }
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbAdmin.isChecked()) {
                    rlBindTerminal.setVisibility(View.GONE);
                    llBindedTerminal.setVisibility(View.GONE);
                    tvAccountType.setText("管理员");
                    dialog.dismiss();
                } else if (rbNormal.isChecked()) {
                    rlBindTerminal.setVisibility(View.VISIBLE);
                    llBindedTerminal.setVisibility(View.VISIBLE);
                    tvAccountType.setText("普通用户");
                    dialog.dismiss();
                } else {
                    ToastUtil.show(CreateAccountActivity.this, "请选择账号类型");
                }
            }
        });
    }

    public void getDevice() {
        Intent intent = new Intent(CreateAccountActivity.this, ChooseTerminalToPersonalActivity.class);
        Gson gson = new Gson();
        String deviceListJson = gson.toJson(deviceList);
        intent.putExtra("deviceList", deviceListJson);
        startActivityForResult(intent, 3);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);
        if ("editAccountManage".equals((baseBean.getType()))) {
            EditAccManageResult editAccManageResult = gson.fromJson(baseBean.getData(), EditAccManageResult.class);
            String isSucceed = editAccManageResult.getConfigureState();
            String operator = editAccManageResult.getAccOperator();
            if (operator.equals("00")) {
                if (isSucceed.equals("00")) {
                    ToastUtil.show(CreateAccountActivity.this, "创建失败");
                } else if (isSucceed.equals("01")) {
                    ToastUtil.show(CreateAccountActivity.this, "创建成功");
                }
            }
            if (operator.equals("01")) {
                if (isSucceed.equals("00")) {
                    ToastUtil.show(CreateAccountActivity.this, "修改失败");
                } else if (isSucceed.equals("01")) {
                    ToastUtil.show(CreateAccountActivity.this, "修改成功");
                }
            }
            if (operator.equals("02")) {
                if (isSucceed.equals("00")) {
                    ToastUtil.show(CreateAccountActivity.this, "删除失败");
                } else if (isSucceed.equals("01")) {
                    ToastUtil.show(CreateAccountActivity.this, "删除成功");
                }
            }
        }
    }

    private void CreateAccount() {
        AccManageInfo manageInfo = new AccManageInfo();
        ArrayList<String> macList = new ArrayList<String>(Arrays.asList(checkedMac.split(",")));
        manageInfo.setAccName(etPartName.getText().toString().trim());

        switch (tvAccountType.getText().toString()) {
            case "管理员":
                manageInfo.setAccAuthority("00");
                break;
            case "普通用户":
                manageInfo.setAccAuthority("01");
                break;
        }
        manageInfo.setUserPhoneNum(tvPhoneNumber.getText().toString().trim());
        manageInfo.setAccPsw(tvAccountPassword.getText().toString().trim());
        manageInfo.setAccDeviceCount(macList.size());
        manageInfo.setAccMacList(macList);
        EditAccountManage.sendCMD(AppDataCache.getInstance().getString("loginIp"), manageInfo, 0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (3 == requestCode) {       //获取设备
            deviceList.clear();
            String deviceListStr = data.getStringExtra("deviceList");
            if (!deviceListStr.equals("")) {
                Log.i("result", "onActivityResult: " + deviceListStr);
                deviceList.addAll(JSONArray.parseArray(deviceListStr, FoundDeviceInfo.class));
                checkedMac = "";
                String str = "";
                for (FoundDeviceInfo device : deviceList) {
                    str += device.getDeviceName() + "\n";
                    checkedMac += device.getDeviceMac() + ",";
                }
                tvSelectDevice.setText(str);
                tvNotBind.setVisibility(View.GONE);
                llBindedTerminal.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}