package com.itc.smartbroadcast.activity.event;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.TimePickerView;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.base.Base2Activity;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.EditSchemeResult;
import com.itc.smartbroadcast.bean.Scheme;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.EditScheme;
import com.itc.smartbroadcast.util.StringUtils;
import com.itc.smartbroadcast.util.ToastUtil;
import com.itc.smartbroadcast.widget.custom.ClearEditText;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CreateSchemeActivity extends Base2Activity {
    private ImageView btBackEvent;
    private TextView tvAddringover, tvStartDate, tvEndDate;
    private ClearEditText etTaskName;
    private RelativeLayout btStartDate, btEndDate;

    private TimePickerView timePickerView, timePickerView1;
    private String mTaskName = "", mStartTime = "", mEndTime = "", mSchemeStartTime = "", mSchemeEndTime = "";
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日");
    private SimpleDateFormat sf2 = new SimpleDateFormat("yyyy-MM-dd");


    private boolean isUpdate = false;
    private String taskName = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addringingtask);
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorMain), 0);
        initView();
    }


    private void initView() {
        btBackEvent = (ImageView) findViewById(R.id.bt_back_event);
        tvAddringover = (TextView) findViewById(R.id.tv_addringover);
        etTaskName = (ClearEditText) findViewById(R.id.et_taskname);
        tvStartDate = (TextView) findViewById(R.id.tv_start_date);
        btStartDate = (RelativeLayout) findViewById(R.id.bt_startdate);
        tvEndDate = (TextView) findViewById(R.id.tv_enddate);
        btEndDate = (RelativeLayout) findViewById(R.id.bt_enddate);

        btBackEvent.setOnClickListener(new ButtonListener());
        tvAddringover.setOnClickListener(new ButtonListener());
        btStartDate.setOnClickListener(new ButtonListener());
        btEndDate.setOnClickListener(new ButtonListener());

    }

    @Override
    protected void onResume() {
        EventBus.getDefault().register(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);
        super.onPause();
    }


    //打铃任务开始时间
    private void showStartDate() {
        if (timePickerView == null) {
            timePickerView = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
            timePickerView.setTitle("");
            timePickerView.setRange(2000, 2025);
            timePickerView.setTime(new Date());
            timePickerView.setCyclic(false);
            timePickerView.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date) {
                    mStartTime = sf.format(date);
                    mSchemeStartTime = sf2.format(date);
                    tvStartDate.setText(mStartTime);
                }
            });
        }
        if (!timePickerView.isShowing()) {
            timePickerView.show();
        } else {
            timePickerView.dismiss();
        }
    }

    //打铃任务结束时间
    private void showEndDate() {
        if (timePickerView1 == null) {
            timePickerView1 = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
            timePickerView1.setTitle("");
            timePickerView1.setRange(2000, 2025);
            timePickerView1.setTime(new Date());
            timePickerView1.setCyclic(false);
            timePickerView1.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date) {
                    mEndTime = sf.format(date);
                    mSchemeEndTime = sf2.format(date);
                    tvEndDate.setText(mEndTime);
                }
            });
        }
        if (!timePickerView1.isShowing()) {
            timePickerView1.show();
        } else {
            timePickerView1.dismiss();
        }

    }

    /**
     * 添加打铃任务发送UDP命令
     */
    private void addRingingScheme() {

        mTaskName = etTaskName.getText().toString().trim();
        if (!StringUtils.checkName(mTaskName)) {
            ToastUtil.show(CreateSchemeActivity.this, "方案名称必须是汉字、字母和数字并且在2到15个字符之间！", Toast.LENGTH_SHORT);
            return;
        }
        if (mSchemeStartTime == null || mSchemeStartTime.equals("")) {
            ToastUtil.show(CreateSchemeActivity.this, "开始时间不能为空！", Toast.LENGTH_SHORT);
            return;
        }
        if (mSchemeEndTime == null || mSchemeEndTime.equals("")) {
            ToastUtil.show(CreateSchemeActivity.this, "结束时间不能为空！", Toast.LENGTH_SHORT);
            return;
        }

        try {
            Date startDate = sf2.parse(mSchemeStartTime);
            Date endDate = sf2.parse(mSchemeEndTime);
            if (endDate.getTime() < startDate.getTime()) {
                ToastUtil.show(CreateSchemeActivity.this, "结束时间不能小于开始时间！", Toast.LENGTH_SHORT);
                return;
            }

            Date nowDate = new Date();
            String nowStr = sf2.format(nowDate);
            Date now = sf2.parse(nowStr);

            if (startDate.getTime() < now.getTime()) {
                ToastUtil.show(CreateSchemeActivity.this, "方案开始时间必须为今天以及以后的时间！", Toast.LENGTH_SHORT);
                return;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        Scheme scheme = new Scheme();
        scheme.setSchemeName(mTaskName);
        scheme.setSchemeStartDate(mSchemeStartTime);
        scheme.setSchemeEndDate(mSchemeEndTime);
        tvAddringover.setEnabled(false);
        EditScheme.sendCMD(AppDataCache.getInstance().getString("loginIp"), scheme, 0);
    }

    /**
     * EventBus数据回调
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        Gson gson = new Gson();
        BaseBean baseBean = gson.fromJson(json, BaseBean.class);

        if ("editSchemeResult".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                EditSchemeResult editSchemeResult = gson.fromJson(data, EditSchemeResult.class);
                if (editSchemeResult.getResult() == 1) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ToastUtil.show(this, "方案创建成功!");
                    this.finish();
                } else {
                    ToastUtil.show(this, "方案创建失败,方案数目达到最大值，请删除部分方案重试!");
                }
            } else {
                ToastUtil.show(this, "操作失败，请检查数据以及网络!");
            }
        }
    }

    /**
     * 监听Back键按下事件,方法2:
     * 注意:
     * 返回值表示:是否能完全处理该事件
     * 在此处返回false,所以会继续传播该事件.
     * 在具体项目中此处的返回值视情况而定.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            back();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }

    }

    private void back() {


        String taskName = etTaskName.getText().toString().trim();

        if (!isUpdate &&(taskName.equals(""))){
            finish();
            return;
        }


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = View.inflate(this, R.layout.dialog_tips, null);
        final TextView tvMsg = (TextView) v.findViewById(R.id.tv_msg);
        final Button btnOk = (Button) v.findViewById(R.id.btn_ok);
        final Button btnNo = (Button) v.findViewById(R.id.btn_no);
        final android.app.AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(v);
        tvMsg.setText("信息未保存，确定退出吗？");
        btnNo.setVisibility(View.VISIBLE);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private class ButtonListener implements View.OnClickListener {
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_back_event:
                    back();
                    break;
                case R.id.tv_addringover:

                    addRingingScheme();
                    break;
                case R.id.bt_startdate:
                    showStartDate();
                    isUpdate = true;
                    break;
                case R.id.bt_enddate:
                    showEndDate();
                    isUpdate = true;
                    break;
            }
        }
    }
}