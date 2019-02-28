package com.itc.smartbroadcast.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.activity.found.DeviceDetailActivity;
import com.itc.smartbroadcast.activity.personal.TimeSyncActivity;
import com.itc.smartbroadcast.adapter.TodayTaskListRvAdapter;
import com.itc.smartbroadcast.base.BaseFragment;
import com.itc.smartbroadcast.bean.BaseBean;
import com.itc.smartbroadcast.bean.ConfigureTodayTaskInfo;
import com.itc.smartbroadcast.bean.ConfigureTodayTaskResult;
import com.itc.smartbroadcast.bean.ExecuteTaskDateResult;
import com.itc.smartbroadcast.bean.Scheme;
import com.itc.smartbroadcast.bean.Task;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.channels.protocolhandler.ConfigureTodayTask;
import com.itc.smartbroadcast.channels.protocolhandler.ExecuteTaskDate;
import com.itc.smartbroadcast.channels.protocolhandler.GetSchemeList;
import com.itc.smartbroadcast.channels.protocolhandler.GetTaskList;
import com.itc.smartbroadcast.helper.CustomDialog;
import com.itc.smartbroadcast.popupwindow.ListStrPopWindow;
import com.itc.smartbroadcast.util.ConfigUtils;
import com.itc.smartbroadcast.util.ToastUtil;
import com.itc.smartbroadcast.widget.custom.CommonProgressDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;


/**
 * Created by Ligh on 18-9-12.
 * describe _每日执行任务列表fragment
 */

public class TodayTaskListFragment extends BaseFragment {


    private static final String TAG = "TodayTaskListFragment";

    @BindView(R.id.task_list_rv)
    RecyclerView taskListRv;
    @BindView(R.id.no_data_tv)
    TextView noTaskTv;
    @BindView(R.id.date_tv)
    TextView dateTv;
    @BindView(R.id.switch_date_tv)
    TextView switchDateTv;
    @BindView(R.id.all_task_view_iv)
    ImageView allTaskViewIv;
    @BindView(R.id.all_task_view_tv)
    TextView allTaskViewTv;
    @BindView(R.id.all_task_view)
    RelativeLayout allTaskView;


    private TodayTaskListRvAdapter mRvAdapter;

    private int type;                           //0 周一 1周二 ....
    protected boolean isInit = false;           //视图是否已经初初始化
    protected boolean isLoad = false;           //视图是否需要加载

    private CommonProgressDialog progressDialog;
    public static boolean mIsRefreshing = true;      //recycleview是否在加载数据中


    //发送数据到TodayTaskListRvAdapter
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            List<Task> taskArrayList3 = (List<Task>)msg.obj;
            //设置数据和传递指示器的索引
            mRvAdapter.setTaskList(taskArrayList3, type);
        }
    };


    public static Fragment newInstance(Bundle bundle) {
        TodayTaskListFragment fragment = new TodayTaskListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Bundle arguments = getArguments();
//        if (arguments != null) {
//            type = arguments.getInt("type");
//        }
    }


    /**
     * 初始化视图，数据等
     */
    @Override
    public void init() {
        Bundle arguments = getArguments();
        if (arguments != null) {
            type = arguments.getInt("type");
        }
        //初始化Recycleview
        initView();
        isInit = true;
        //初始化的时候去加载数据,解决ViewPager预加载问题
        isCanLoadData();
    }


    private void initView() {
        //切换功能图标显示
        int cacheWeek = Integer.parseInt(AppDataCache.getInstance().getString("executeTaskWeek"));
        //如果定时器返回的执行任务周等于当前页面对应的周，那么应该隐藏切换按钮
        if (cacheWeek == type) {
            switchDateTv.setVisibility(View.GONE);
        } else {
            String userType = AppDataCache.getInstance().getString("userType");
            //管理员权限才显示切换功能
            if ("00".equals(userType)) {
                switchDateTv.setVisibility(View.VISIBLE);
            } else {
                switchDateTv.setVisibility(View.GONE);
            }
        }
        //缓存当天执行任务周索引值
        cacheSlideIndex();
        //显示详细日期时间
        final int[] detailDate = showDetailDate();
        //进度条
        progressDialog = new CommonProgressDialog(getActivity());
        //数据回来才显示
        taskListRv.setVisibility(View.GONE);
        noTaskTv.setVisibility(View.VISIBLE);
        //今日任务列表adapter
        mRvAdapter = new TodayTaskListRvAdapter(getActivity());
        //recycleview配置
        taskListRv.setHasFixedSize(true);
        taskListRv.setLayoutManager(new RecyclerViewNoBugLinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        taskListRv.setAdapter(mRvAdapter);
        //点击全部任务
        allTaskView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAllTaskPop(view);
            }
        });
        //点击切换任务
        switchDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                view = View.inflate(getActivity(), R.layout.switch_dialog_tips, null);
                final TextView tvMsg = (TextView) view.findViewById(R.id.tv_msg);
                final Button btnOk = (Button) view.findViewById(R.id.btn_ok);
                final Button btnNo = (Button) view.findViewById(R.id.btn_no);
                final AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getWindow().setContentView(view);
                SpannableString spannableString = new SpannableString("尊敬的用户：\n\n" + "切换的任务，只在今天执行，当\n" + "次有效，确认复制吗？");
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#888888"));
                spannableString.setSpan(colorSpan, 0, 6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                tvMsg.setText(spannableString);
                btnNo.setVisibility(View.VISIBLE);
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConfigureTodayTaskInfo configureTodayTaskInfo = new ConfigureTodayTaskInfo();
                        configureTodayTaskInfo.setExecuteTaskWeek(type + 1);
                        configureTodayTaskInfo.setExecuteTaskDate(detailDate);
                        ConfigureTodayTask.sendCMD(AppDataCache.getInstance().getString("loginIp"), configureTodayTaskInfo);
                        dialog.dismiss();
                    }
                });
                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });

    }


    private void cacheSlideIndex() {
        //记录当前日期索引值
        int executeTaskWeek = Integer.parseInt(AppDataCache.getInstance().getString("executeTaskWeek"));
        final int slideIndex = executeTaskWeek;
        //将索引值存入缓存中
        if (TextUtils.isEmpty(AppDataCache.getInstance().getString("slideIndex"))) {
            AppDataCache.getInstance().putString("slideIndex", String.valueOf(slideIndex));
            Log.d("TodayFragment", "TodayFragment : 重新赋值：slideIndex");
        }
    }


    //显示全部任务筛选菜单列表
    private void showAllTaskPop(View v) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.list_pop_view, null, false);
        //1.构造一个PopupWindow，参数依次是加载的View，宽高
        final PopupWindow popWindow = new PopupWindow(view,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        popWindow.setAnimationStyle(R.anim.anim_pop);  //设置加载动画
        popWindow.setTouchable(true);
        popWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        popWindow.setBackgroundDrawable(new ColorDrawable(0xFFFFFF));
        popWindow.showAsDropDown(v, 30, 0);
//		mraRatingBar.setEnabled(false);
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
//        lp.alpha = 0.7f;
        getActivity().getWindow().setAttributes(lp);
        popWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1f;
                getActivity().getWindow().setAttributes(lp);
            }
        });

        TextView allTaskTv = (TextView) view.findViewById(R.id.all_task_tv);
        TextView ringTaskTv = (TextView) view.findViewById(R.id.ring_task_tv);
        TextView timingTaskTv = (TextView) view.findViewById(R.id.timing_task_tv);

        allTaskTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRvAdapter.setTaskType(0);
                popWindow.dismiss();
                allTaskViewTv.setText("全部任务");
            }
        });

        ringTaskTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRvAdapter.setTaskType(1);
                popWindow.dismiss();
                allTaskViewTv.setText("打铃任务");
            }
        });

        timingTaskTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRvAdapter.setTaskType(2);
                popWindow.dismiss();
                allTaskViewTv.setText("定时任务");
            }
        });
    }

    ArrayList<Task> taskArrayList = new ArrayList<>();

    /**
     * Eventbus 接收数据
     *
     * @param json
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEventMain(String json) {
        if (json == null)
            return;
        Gson gson = new Gson();

        BaseBean baseBean = gson.fromJson(json, BaseBean.class);
        //任务列表数据
        if ("getTaskList".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                //获取方案列表
                GetSchemeList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
                List<Task> taskList = JSONArray.parseArray(data, Task.class);
                //任务列表按时间递增排序
                if (taskList.size() > 0) {
                    //数据回来才显示列表
                    noTaskTv.setVisibility(View.GONE);
                    taskListRv.setVisibility(View.VISIBLE);
                    taskArrayList.clear();
                    taskArrayList.addAll(taskList);
                }
            }
        }


        //方案列表数据
        if ("getSchemeList".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                final List<Scheme> sechemeList = JSONArray.parseArray(data, Scheme.class);
                final List<Task> taskArrayList2 = new ArrayList<>();
                final List<Task> taskArrayList3 = new ArrayList<>();
                taskArrayList2.clear();
                taskArrayList2.addAll(taskArrayList);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //taskArrayList3 添加打铃任务到
                        for (Scheme schemeInfo : sechemeList) {
                            int schemeStatus = schemeInfo.getSchemeStatus();
                            int schemeNum = schemeInfo.getSchemeNum();
                            for (Task taskInfo : taskArrayList2) {
                                int taskSchemeNum = taskInfo.getSchemeNum();
                                if (taskSchemeNum == schemeNum && schemeStatus != 0) {
                                    taskArrayList3.add(taskInfo);
                                }
                            }
                        }
                        //taskArrayList3 添加定时任务
                        for (Task taskInfo : taskArrayList2) {
                            int taskSchemeNum = taskInfo.getSchemeNum();
                            int taskStatus = taskInfo.getTaskStatus();
                            if (taskSchemeNum == 255 && taskStatus != 0) {
                                taskArrayList3.add(taskInfo);
                            }
                        }
                        //时间排序
                        invertOrderList(taskArrayList3);
                        Message msg = new Message();
                        msg.obj = taskArrayList3;
                        handler.sendMessage(msg);
                    }

                }).start();


            }

        }


        //切换任务结果
        if ("ConfigureTodayTaskResult".equals(baseBean.getType())) {
            String data = baseBean.getData();
            if (data != null) {
                ConfigureTodayTaskResult configureTodayTaskResult = gson.fromJson(data, ConfigureTodayTaskResult.class);
                int result = configureTodayTaskResult.getResult();
                if (1 == result) {
                    //通知界面TodayFragment刷新数据
                    BaseBean bean = new BaseBean();
                    bean.setType("notifyRefresh");
                    bean.setData("");
                    String jsonResult = gson.toJson(bean);
                    EventBus.getDefault().post(jsonResult);
                    init();
                    mRvAdapter.notifyDataSetChanged();
                }

            }
        }

    }

    private int[] showDetailDate() {
        //动态显示日期
        String executeTaskDate = AppDataCache.getInstance().getString("executeTaskDate");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        int[] dateArr = new int[0];
        try {
            //一天毫秒值
            long oneDayMsec = 24 * 60 * 60 * 1000;
            long time = sdf.parse(executeTaskDate).getTime();
            //获取索引值
            int slideIndexCache = Integer.parseInt(AppDataCache.getInstance().getString("slideIndex"));
            //更新滑动后的毫秒值
            time += (oneDayMsec * (type - slideIndexCache));
            //毫秒转日期
            Date date = new Date(time);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            executeTaskDate = format.format(date);
            //日期转周
            String dateToWeek = dateToWeek(executeTaskDate);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            dateTv.setText(year + "年" + (month + 1) + "月" + day + "日, " + dateToWeek);
            dateArr = new int[3];
            dateArr[0] = year - 2000;
            dateArr[1] = month + 1;
            dateArr[2] = day;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateArr;
    }


    @Override
    public int getLayoutId() {
        return R.layout.frag_task_list;
    }


    /**
     * 视图是否已经对用户可见，系统的方法
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isCanLoadData();
    }


    /**
     * 是否可以加载数据
     * 可以加载数据的条件：
     * 1.视图已经初始化
     * 2.视图对用户可见
     */
    private void isCanLoadData() {
        if (!isInit) {
            return;
        }
        if (getUserVisibleHint()) {
            //用户可见时加载
            lazyLoad();
            isLoad = true;
        } else {
            if (isLoad) {
                stopLoad();
            }
        }
    }


    /**
     * 视图销毁的时候讲Fragment是否初始化的状态变为false
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isInit = false;
        isLoad = false;
        Log.d(TAG, "onDestroyView: 执行了");
    }


    protected void lazyLoad() {
        mIsRefreshing = true;
        //发送网络请求获取任务列表
        GetTaskList.sendCMD(AppDataCache.getInstance().getString("loginIp"));
    }


    protected void stopLoad() {
        //Log.d(TAG, "Fragment1" + "已经对用户不可见，可以停止加载数据");
    }


    //解决适配器remove出现越界问题，异常处理
    public class RecyclerViewNoBugLinearLayoutManager extends LinearLayoutManager {
        public RecyclerViewNoBugLinearLayoutManager(Context context) {
            super(context);
        }

        public RecyclerViewNoBugLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
            super(context, orientation, reverseLayout);
        }

        public RecyclerViewNoBugLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
        }

        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        }

        /**
         * 去除动画防止，防止出现recyclerView的bug
         * Created by zhanglin on 2016/11/3.
         */
        @Override
        public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                return super.scrollVerticallyBy(dy, recycler, state);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        }

    }


    /**
     * 将List按照时间倒序排列
     *
     * @param list
     * @return
     */
    private void invertOrderList(List<Task> list) {
        final Format f = new SimpleDateFormat("HH:mm:ss");
        Collections.sort(list, new Comparator<Task>() {

            public int compare(Task o1, Task o2) {
                Date d1;
                Date d2;
                try {
                    d1 = (Date) f.parseObject(o1.getTaskStartDate());
                    d2 = (Date) f.parseObject(o2.getTaskStartDate());
                    return d1.compareTo(d2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }


    /**
     * 日期转周
     *
     * @param datetime
     * @return
     */
    public static String dateToWeek(String datetime) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        String[] weekDays = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        Calendar cal = Calendar.getInstance(); // 获得一个日历
        Date datet = null;
        try {
            datet = f.parse(datetime);
            cal.setTime(datet);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个周中的某天。
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

}
