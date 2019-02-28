package com.itc.smartbroadcast.activity.found;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.bean.FoundDeviceInfo;
import com.itc.smartbroadcast.bean.PowerAmplifierInfo;
import com.itc.smartbroadcast.channels.protocolhandler.EditPowerAmplifierDetail;
import com.jaeger.library.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * create by youmu on 2018/8
 */
public class SoundEffectSettingActivity extends AppCompatActivity {


    @BindView(R.id.bt_back_found)
    ImageView btBackFound;
    @BindView(R.id.bt_save_sound_effect)
    Button btSaveSoundEffect;
    @BindView(R.id.rl_hint_sound_effrct)
    RelativeLayout rlHintSoundEffrct;
    @BindView(R.id.ll_sound_sffect)
    LinearLayout llSoundSffect;
    @BindView(R.id.rl_hint_source_type)
    RelativeLayout rlHintSourceType;
    @BindView(R.id.receiving_volume_channel)
    LinearLayout receivingVolumeChannel;
    @BindView(R.id.sb_terminal_vol)
    SeekBar sbTerminalVol;
    @BindView(R.id.sb_high_gain)
    SeekBar sbHighGain;
    @BindView(R.id.tv_high_gain)
    TextView tvHighGain;
    @BindView(R.id.sb_low_gain)
    SeekBar sbLowGain;
    @BindView(R.id.tv_low_gain)
    TextView tvLowGain;
    @BindView(R.id.tv_now_vol)
    TextView tvNowVol;
    @BindView(R.id.cb_mix_state_s1)
    CheckBox cbMixStateS1;
    @BindView(R.id.cb_mix_state_s2)
    CheckBox cbMixStateS2;
    @BindView(R.id.cb_mix_state_s3)
    CheckBox cbMixStateS3;
    @BindView(R.id.cb_mix_state_s4)
    CheckBox cbMixStateS4;
    @BindView(R.id.cb_mix_state_s5)
    CheckBox cbMixStateS5;
    @BindView(R.id.cb_mix_state_p1)
    CheckBox cbMixStateP1;
    @BindView(R.id.cb_mix_state_p2)
    CheckBox cbMixStateP2;
    @BindView(R.id.cb_mix_state_p3)
    CheckBox cbMixStateP3;
    @BindView(R.id.bt_copy_to_another_device)
    Button btCopyToAnotherDevice;
//    @BindView(R.id.cb_mix_state_p4)
//    CheckBox cbMixStateP4;
//    @BindView(R.id.cb_mix_state_p5)
//    CheckBox cbMixStateP5;
//    @BindView(R.id.cb_mix_state_e1)
//    CheckBox cbMixStateE1;
//    @BindView(R.id.cb_mix_state_e2)
//    CheckBox cbMixStateE2;
//    @BindView(R.id.cb_mix_state_e3)
//    CheckBox cbMixStateE3;
//    @BindView(R.id.cb_mix_state_e4)
//    CheckBox cbMixStateE4;
//    @BindView(R.id.cb_mix_state_e5)
//    CheckBox cbMixStateE5;

    private static String fristVol;
    @BindView(R.id.ll_high_low)
    LinearLayout llHighLow;
    private String terminalIp;
    private String deviceVol = "";
    List<FoundDeviceInfo> deviceList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_effect_setting);
        ButterKnife.bind(this);
        initOnclick();
        StatusBarUtil.setColor(SoundEffectSettingActivity.this, getResources().getColor(R.color.colorMain), 0);
        //llSoundSffect.setVisibility(View.GONE);
        receivingVolumeChannel.setVisibility(View.GONE);
        getTerminalIntent();
        initSb();
    }


    public void getTerminalIntent() {
        Intent intent = getIntent();
        if (intent == null) {
            finish();
        } else {
            String ss = intent.getStringExtra("TerminalMedel");
            if (ss.equals("TX-8606")) {
                llHighLow.setVisibility(View.GONE);
                btCopyToAnotherDevice.setVisibility(View.GONE);
            }
            //设备音量
            String terminalSetVolume = intent.getStringExtra("TerminalSetVolume");
            fristVol = intent.getStringExtra("TerminalSetVolume");
            tvNowVol.setText(terminalSetVolume);
            sbTerminalVol.setProgress(Integer.parseInt(terminalSetVolume));
            //高音增益
            int terminalHighGain = intent.getIntExtra("TerminalHighGain", 0);
            tvHighGain.setText(terminalHighGain + "");
            sbHighGain.setProgress(terminalHighGain + 10);
            //低音增益
            int terminalLowGain = intent.getIntExtra("TerminalLowGain", 0);
            tvLowGain.setText(terminalLowGain + "");
            sbLowGain.setProgress(terminalLowGain + 10);
            int[] mixingEnableState_s = intent.getIntArrayExtra("MixingEnableState_s");
            int[] mixingEnableState_p = intent.getIntArrayExtra("MixingEnableState_p");
            int[] mixingEnableState_e = intent.getIntArrayExtra("MixingEnableState_e");
            terminalIp = intent.getStringExtra("TerminalIP");


            ArrayList<CheckBox> sCheckBox = new ArrayList<>();
            sCheckBox.add(cbMixStateS1);
            sCheckBox.add(cbMixStateS2);
            sCheckBox.add(cbMixStateS3);
            sCheckBox.add(cbMixStateS4);
            sCheckBox.add(cbMixStateS5);
//            for (int i = 0; i < 5; i++) {
//                sCheckBox.get(i).setChecked(mixingEnableState_s[i] == 1 ? true : false);
//            }

            ArrayList<CheckBox> sCheckBox2 = new ArrayList<>();
            sCheckBox2.add(cbMixStateP1);
            sCheckBox2.add(cbMixStateP2);
            sCheckBox2.add(cbMixStateP3);
//            sCheckBox2.add(cbMixStateP4);
//            sCheckBox2.add(cbMixStateP5);
//            for (int i = 0; i < 3; i++) {
//                sCheckBox2.get(i).setChecked(mixingEnableState_p[i] == 1 ? true : false);
//            }
//            ArrayList<CheckBox> sCheckBox3 = new ArrayList<>();
//            sCheckBox3.add(cbMixStateE1);
//            sCheckBox3.add(cbMixStateE2);
//            sCheckBox3.add(cbMixStateE3);
//            sCheckBox3.add(cbMixStateE4);
//            sCheckBox3.add(cbMixStateE5);
//            for (int i = 0; i < 5; i++) {
//                sCheckBox3.get(i).setChecked(mixingEnableState_e[i] == 1 ? true : false);
//            }
        }
    }


    private void dynamicChangGain() {

        int[] sList = new int[5];
        sList[0] = cbMixStateS1.isChecked() ? 1 : 0;
        sList[1] = cbMixStateS2.isChecked() ? 1 : 0;
        sList[2] = cbMixStateS3.isChecked() ? 1 : 0;
        sList[3] = cbMixStateS4.isChecked() ? 1 : 0;
        sList[4] = cbMixStateS5.isChecked() ? 1 : 0;

        int[] pList = new int[5];
        pList[0] = cbMixStateP1.isChecked() ? 1 : 0;
        pList[1] = cbMixStateP2.isChecked() ? 1 : 0;
        pList[2] = cbMixStateP3.isChecked() ? 1 : 0;
//        pList[3] = cbMixStateP4.isChecked() ? 1:0;
//        pList[4] = cbMixStateP5.isChecked() ? 1:0;
        pList[3] = 1;
        pList[4] = 1;

        int[] eList = new int[5];
//        eList[0] = cbMixStateE1.isChecked() ? 1:0;
//        eList[1] = cbMixStateE2.isChecked() ? 1:0;
//        eList[2] = cbMixStateE3.isChecked() ? 1:0;
//        eList[3] = cbMixStateE4.isChecked() ? 1:0;
//        eList[4] = cbMixStateE5.isChecked() ? 1:0;
        eList[0] = 1;
        eList[1] = 1;
        eList[2] = 1;
        eList[3] = 1;
        eList[4] = 1;


        PowerAmplifierInfo powerAmplifierInfo = new PowerAmplifierInfo();
        int highGain = Integer.parseInt(tvHighGain.getText().toString());
        int lowGain = Integer.parseInt(tvLowGain.getText().toString());
        Log.i("高音增益》》", highGain + "");
        Log.i("低音增益》》", lowGain + "");
        powerAmplifierInfo.setHighGain(highGain);
        powerAmplifierInfo.setLowGain(lowGain);
        powerAmplifierInfo.setVolume(Integer.parseInt(tvNowVol.getText().toString()));
        powerAmplifierInfo.setMixingEnableState_s(sList);
        powerAmplifierInfo.setMixingEnableState_p(pList);
        powerAmplifierInfo.setMixingEnableState_e(eList);
        EditPowerAmplifierDetail.sendCMD(terminalIp, powerAmplifierInfo);

    }

    private void saveSoundEffect() {

        int[] sList = new int[5];
        sList[0] = cbMixStateS1.isChecked() ? 1 : 0;
        sList[1] = cbMixStateS2.isChecked() ? 1 : 0;
        sList[2] = cbMixStateS3.isChecked() ? 1 : 0;
        sList[3] = cbMixStateS4.isChecked() ? 1 : 0;
        sList[4] = cbMixStateS5.isChecked() ? 1 : 0;

        int[] pList = new int[5];
        pList[0] = cbMixStateP1.isChecked() ? 1 : 0;
        pList[1] = cbMixStateP2.isChecked() ? 1 : 0;
        pList[2] = cbMixStateP3.isChecked() ? 1 : 0;
        //        pList[3] = cbMixStateP4.isChecked() ? 1:0;
        //        pList[4] = cbMixStateP5.isChecked() ? 1:0;
        pList[3] = 1;
        pList[4] = 1;

        int[] eList = new int[5];
        //        eList[0] = cbMixStateE1.isChecked() ? 1:0;
        //        eList[1] = cbMixStateE2.isChecked() ? 1:0;
        //        eList[2] = cbMixStateE3.isChecked() ? 1:0;
        //        eList[3] = cbMixStateE4.isChecked() ? 1:0;
        //        eList[4] = cbMixStateE5.isChecked() ? 1:0;
        eList[0] = 1;
        eList[1] = 1;
        eList[2] = 1;
        eList[3] = 1;
        eList[4] = 1;


        PowerAmplifierInfo powerAmplifierInfo = new PowerAmplifierInfo();
        int highGain = Integer.parseInt(tvHighGain.getText().toString());
        int lowGain = Integer.parseInt(tvLowGain.getText().toString());
        powerAmplifierInfo.setHighGain(highGain);
        powerAmplifierInfo.setLowGain(lowGain);
        powerAmplifierInfo.setVolume(Integer.parseInt(tvNowVol.getText().toString()));
        powerAmplifierInfo.setMixingEnableState_s(sList);
        powerAmplifierInfo.setMixingEnableState_p(pList);
        powerAmplifierInfo.setMixingEnableState_e(eList);

        EditPowerAmplifierDetail.sendCMD(terminalIp, powerAmplifierInfo);

    }


    //禁止返回键
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            Intent intent1 = new Intent();
            intent1.putExtra("DeviceVol", fristVol);
            intent1.putExtra("Result", "1");
            SoundEffectSettingActivity.this.setResult(4, intent1);
            SoundEffectSettingActivity.this.finish();
            return true;
        } else {
            return super.dispatchKeyEvent(event);
        }
    }


    public void initSb() {
        sbTerminalVol.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                tvNowVol.setText(progress + "");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sbHighGain.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                progress = progress - 10;
                if (progress % 2 != 0) {
                    progress = progress + 1;
                }
                tvHighGain.setText(progress + "");
                dynamicChangGain();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        sbLowGain.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress2, boolean b) {
                progress2 = progress2 - 10;
                if (progress2 % 2 != 0) {
                    progress2 = progress2 + 1;
                }
                tvLowGain.setText(progress2 + "");
                dynamicChangGain();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }


    public void initOnclick() {
        btBackFound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent();
                intent1.putExtra("DeviceVol", fristVol);
                intent1.putExtra("Result", "1");
                SoundEffectSettingActivity.this.setResult(4, intent1);
                SoundEffectSettingActivity.this.finish();
            }
        });
//        rlHintSoundEffrct.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (llSoundSffect.getVisibility() == View.VISIBLE) {
//                    llSoundSffect.setVisibility(View.GONE);
//                } else {
//                    llSoundSffect.setVisibility(View.VISIBLE);
//                }
//            }
//        });
//        rlHintSourceType.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (receivingVolumeChannel.getVisibility() == View.VISIBLE) {
//                    receivingVolumeChannel.setVisibility(View.GONE);
//                } else {
//                    receivingVolumeChannel.setVisibility(View.VISIBLE);
//                }
//            }
//        });
        btSaveSoundEffect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSoundEffect();
                Intent intent1 = new Intent();
                deviceVol = tvNowVol.getText().toString();
                intent1.putExtra("DeviceVol", deviceVol);
                intent1.putExtra("Result", "2");
                SoundEffectSettingActivity.this.setResult(4, intent1);
                SoundEffectSettingActivity.this.finish();
            }
        });
        btCopyToAnotherDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SoundEffectSettingActivity.this, CopyToAnotherDeviceActivity.class);
                int highGain = Integer.parseInt(tvHighGain.getText().toString());
                int lowGain = Integer.parseInt(tvLowGain.getText().toString());
                int vol = Integer.parseInt(tvNowVol.getText().toString());
                intent.putExtra("highGain", highGain);
                intent.putExtra("lowGain", lowGain);
                intent.putExtra("vol", vol);
                Gson gson = new Gson();
                String deviceListJson = gson.toJson(deviceList);
                intent.putExtra("deviceList", deviceListJson);
                startActivity(intent);
            }
        });
    }
}
