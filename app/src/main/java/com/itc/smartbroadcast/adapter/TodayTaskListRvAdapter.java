package com.itc.smartbroadcast.adapter;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itc.smartbroadcast.R;
import com.itc.smartbroadcast.bean.Task;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.util.SmartBroadCastUtils;

import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 作者 : 李观鸿
 */

public class TodayTaskListRvAdapter extends RecyclerView.Adapter {

    private Context mContext;                                       //上下文
    private int mWeekNum;                                           //周数   0:周一
    private List<Task> mTaskList = new ArrayList<>();               //列表所有任务数据集
    private List<Task> mAllTaskList = new ArrayList<>();            //筛选任务功能，全部任务数据集
    private static final String TAG = "TodayTaskListRvAdapter";

    public TodayTaskListRvAdapter(Context context) {
        this.mContext = context;
    }

    public List<Task> getList() {
        return mTaskList;
    }

    /**
     * 填充列表数据集
     * @param list
     * @param weekNum
     */
    public void setTaskList(List<Task> list, int weekNum) {
        if (list != null) {
            this.mTaskList.clear();
            this.mAllTaskList.clear();
            addList(list);
        }
        this.mWeekNum = weekNum;
    }

    private void addList(List<Task> list) {
        if (list != null) {
            mTaskList.addAll(list);
            mAllTaskList.addAll(list);
            notifyDataSetChanged();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_today_task_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        //绑定数据
        final ItemViewHolder itemHolder = (ItemViewHolder) holder;
        //时间轴头部
        itemHolder.timeAxisStart.setVisibility(position == 0 ? View.GONE : View.VISIBLE);
        //数据对象
        Task taskInfo = mTaskList.get(position);
        //任务名称
        itemHolder.taskNameTv.setText(taskInfo.getTaskName());
        //任务状态
        int taskStatus = taskInfo.getTaskStatus();
        //任务是否中断状态
        int taskTestStatus = taskInfo.getTaskTestStatus();
        //音量
        int taskVolume = taskInfo.getTaskVolume();
        if (128 == taskVolume) {
            itemHolder.taskVolumeTv.setText("音量：默认音量");
        } else {
            itemHolder.taskVolumeTv.setText("音量：" + taskInfo.getTaskVolume());
        }
        //开始时间字符串
        String taskStartTime = taskInfo.getTaskStartDate();
        itemHolder.taskTimeTv.setText(taskStartTime);
        //持续时间 把秒转化为时分秒显示
        String taskDurationTime = SmartBroadCastUtils.secToTime(taskInfo.getTaskContinueDate());
        itemHolder.taskDurationTv.setText("持续时间 " + taskDurationTime);
        //切割字符串
        String[] split = taskStartTime.split(":");
        //开始时间
        Date startDate = new Date();
        startDate.setHours(Integer.parseInt(split[0]));
        startDate.setMinutes(Integer.parseInt(split[1]));
        startDate.setSeconds(Integer.parseInt(split[2]));
        //结束时间
        Date endDate = new Date(startDate.getTime() + taskInfo.getTaskContinueDate() * 1000);
        //当前时间
        final Date nowDate = new Date();
        //时间轴原点颜色 判断任务状态为已进行（灰） 进行中（红） 未进行（浅绿）三种
        //1.开始时间小于系统时间 已进行状态   2.开始时间在系统时间加持续时间之内 进行中状态  3.开始时间大于系统时间 未进行状态
        int taskState = compareTime(startDate, endDate, nowDate);
        //任务按日期重复 指定日期重复
        String[] taskDateDuplicationPattern = taskInfo.getTaskDateDuplicationPattern();
        //任务按周重复 为1表示当天执行
        int[] taskWeekDuplicationPattern = taskInfo.getTaskWeekDuplicationPattern();
        //一天毫秒值
        long oneDayMsec = 24 * 60 * 60 * 1000;
        //当前日期毫秒值
        String executeTaskDate = AppDataCache.getInstance().getString("executeTaskDate");
        long currentDateMsec = date2Msec(executeTaskDate);
        //用来区分状态的当前日期毫秒值
        long currentDateMsecState = date2Msec(executeTaskDate);
        //获取索引值
        int slideIndexCache = Integer.parseInt(AppDataCache.getInstance().getString("slideIndex"));
        //更新滑动后的毫秒值
        long endMsec = currentDateMsec + (oneDayMsec * (mWeekNum - slideIndexCache));
        //任务类型显示，任务编号为255表示定时任务，其他的为打铃任务
        int schemeNum = taskInfo.getSchemeNum();
        if (255 != schemeNum) {
            //此为周模式，打铃任务
            showRingTask(itemHolder, taskState, taskWeekDuplicationPattern[mWeekNum], currentDateMsecState, endMsec, taskStatus);
        } else {
            //此为日期模式，定时任务
            showTimingTask(itemHolder, taskState, taskDateDuplicationPattern, currentDateMsecState, endMsec, taskStatus);
        }
        if (currentDateMsecState == endMsec) {
            //任务状态显示
            if (0 == taskState) {
                //已进行状态,时间轴原点灰色，item背景灰色，字体灰色
                taskCompletedColor(itemHolder, taskInfo);
            } else if (1 == taskState) {
                //进行中状态,时间轴原点红色，item背景浅黄，字体浅黄
                taskExecutionColor(itemHolder, taskStartTime);
                //如果进行中的任务由于定时器原因暂停，那么将此任务状态改为完成中状态
                if (0 == taskTestStatus) {
                    taskCompletedColor(itemHolder, taskInfo);
                }
            } else if (2 == taskState) {
                //未进行状态,时间轴原点浅绿色，item背景白色，字体黑色
                taskUnexecutedColor(itemHolder);
            }
        } else {
            //未进行状态,时间轴原点浅绿色，item背景白色，字体黑色
            taskUnexecutedColor(itemHolder);
        }
        //移除不可见的item
        deleteGoneItem(itemHolder);

    }


    //显示有效的定时任务
    private void showTimingTask(ItemViewHolder itemHolder, int taskState, String[] taskDateDuplicationPattern, long currentDateMsec, long endMsec, int taskStatus) {
        itemHolder.taskTypeTv.setText("定时");
        itemHolder.itemContentLl.setVisibility(View.GONE);

        if (currentDateMsec == endMsec) {
            //定时任务图标，根据任务状态判断给予相应图标，只有进行中的任务才显示选中类型图标
            if (taskState == 1) {
                itemHolder.taskIconIv.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.home_icon_daling_on));
            } else {
                itemHolder.taskIconIv.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.home_icon_daling_default));
            }
        } else {
            itemHolder.taskIconIv.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.home_icon_daling_default));
        }

        //按日期重复显示任务
        for (String date : taskDateDuplicationPattern) {
            //2255-255-255 无效日期
            if (!"2255-255-255".equals(date)) {
                //当天或者滑动后的日期毫秒值等于数据返回的日期毫秒值
                if (1 == taskStatus) {
                    if (endMsec == date2Msec(date)) {
                        itemHolder.itemContentLl.setVisibility(View.VISIBLE);
                    }
                }

            }
        }
    }


    //显示有效的打铃任务
    private void showRingTask(ItemViewHolder itemHolder, int taskState, int weekNum, long currentDateMsecState, long endMsec, int taskStatus) {
        itemHolder.taskTypeTv.setText("打铃");
        itemHolder.itemContentLl.setVisibility(View.GONE);
        //打铃任务图标，根据任务状态判断给予相应图标，只有进行中的任务才显示选中类型图标
        if (currentDateMsecState == endMsec) {
            if (taskState == 1) {
                itemHolder.taskIconIv.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.home_icon_dingshi_on));
            } else {
                itemHolder.taskIconIv.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.home_icon_dingshi_default));
            }
        } else {
            itemHolder.taskIconIv.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.home_icon_dingshi_default));
        }

        if (1 == taskStatus) {
            if (1 == weekNum) {
                itemHolder.itemContentLl.setVisibility(View.VISIBLE);
            }
        }
    }


    //任务未执行字体颜色状态
    private void taskUnexecutedColor(ItemViewHolder itemHolder) {
        int colorGray = mContext.getResources().getColor(R.color.gray_999);
        int colorRed = mContext.getResources().getColor(R.color.colorMain);
        int colorGreen = mContext.getResources().getColor(R.color.color_circle_green);
        itemHolder.taskTypeTv.setTextColor(colorGray);
        itemHolder.taskNameTv.setTextColor(colorRed);
        itemHolder.taskDurationTv.setTextColor(colorGreen);
        itemHolder.taskVolumeTv.setTextColor(colorGray);
        itemHolder.contentRl.setBackground(mContext.getResources().getDrawable(R.drawable.bg_today_task_list_white_item));
        itemHolder.circularIv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.circular_green));
    }


    //任务执行中字体颜色状态
    private void taskExecutionColor(ItemViewHolder itemHolder, String taskStartTime) {
        int colorYellow = mContext.getResources().getColor(R.color.color_execution);
        itemHolder.taskTypeTv.setTextColor(colorYellow);
        itemHolder.taskNameTv.setTextColor(colorYellow);
        itemHolder.taskDurationTv.setTextColor(colorYellow);
        itemHolder.taskVolumeTv.setTextColor(colorYellow);
        itemHolder.taskTimeTv.setText(taskStartTime + "  进行中");
        itemHolder.contentRl.setBackground(mContext.getResources().getDrawable(R.drawable.bg_today_task_list_yellow_item));
        itemHolder.circularIv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.circular_red));
    }


    //任务已执行字体颜色状态
    private void taskCompletedColor(ItemViewHolder itemHolder, Task taskInfo) {
        itemHolder.taskNameTv.setText(taskInfo.getTaskName() + "(已完成)");
        int colorGray = mContext.getResources().getColor(R.color.gray_999);
        itemHolder.taskTypeTv.setTextColor(colorGray);
        itemHolder.taskNameTv.setTextColor(colorGray);
        itemHolder.taskDurationTv.setTextColor(colorGray);
        itemHolder.taskVolumeTv.setTextColor(colorGray);
        itemHolder.taskTimeTv.setText(taskInfo.getTaskStartDate());
        itemHolder.taskIconIv.setImageDrawable(mContext.getResources().getDrawable(R.mipmap.home_icon_dingshi_default));
        itemHolder.contentRl.setBackground(mContext.getResources().getDrawable(R.drawable.bg_today_task_list_gray_item));
        itemHolder.circularIv.setImageDrawable(mContext.getResources().getDrawable(R.drawable.circular_gray));
    }


    //移除不可见的item
    private void deleteGoneItem(final ItemViewHolder itemHolder) {
        if (itemHolder.itemContentLl.getVisibility() == View.GONE) {
            //remove后立马notify.可能会损下性能.
            mTaskList.remove(itemHolder.getAdapterPosition());
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        if (mTaskList == null)
            return 0;
        else
            return mTaskList.size();
    }


    //筛选任务类型并更新列表
    public void setTaskType(int type) {
        switch (type) {
            case 0:
                //全部任务
                ArrayList<Task> allList = new ArrayList<>();
                allList.addAll(mAllTaskList);
                mTaskList.clear();
                mTaskList.addAll(allList);
                notifyDataSetChanged();
                break;
            case 1:
                //打铃任务
                ArrayList<Task> ringList = new ArrayList<>();
                for (Task task :
                        mAllTaskList) {
                    if (255 != task.getSchemeNum()) {
                        ringList.add(task);
                    }
                }
                mTaskList.clear();
                mTaskList.addAll(ringList);
                notifyDataSetChanged();
                break;
            case 2:
                //定时任务
                ArrayList<Task> timeList = new ArrayList<>();
                for (Task task :
                        mAllTaskList) {
                    if (255 == task.getSchemeNum()) {
                        timeList.add(task);
                    }
                }
                mTaskList.clear();
                mTaskList.addAll(timeList);
                notifyDataSetChanged();
                break;
        }

    }


    public class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.time_axis_start)
        View timeAxisStart;
        @BindView(R.id.circular_iv)
        ImageView circularIv;
        @BindView(R.id.task_time_tv)
        TextView taskTimeTv;
        @BindView(R.id.task_icon_iv)
        ImageView taskIconIv;
        @BindView(R.id.task_type_tv)
        TextView taskTypeTv;
        @BindView(R.id.task_name_tv)
        TextView taskNameTv;
        @BindView(R.id.task_duration_tv)
        TextView taskDurationTv;
        @BindView(R.id.task_volume_tv)
        TextView taskVolumeTv;
        @BindView(R.id.content_rl)
        RelativeLayout contentRl;
        @BindView(R.id.item_content_ll)
        LinearLayout itemContentLl;

        public ItemViewHolder(View inflate) {
            super(inflate);
            ButterKnife.bind(this, inflate);
        }
    }


    /**
     * 时间比较  返回值0：已进行  1：进行中  2：未进行
     *
     * @param startTime
     * @param endTime
     * @param nowTime
     * @return
     */
    public int compareTime(Date startTime, Date endTime, Date nowTime) {
        if (nowTime.getTime() > endTime.getTime()) {
            //网络时间 - （开始时间+持续时间）>0 说明任务已经完成了
            return 0;
        } else if (nowTime.getTime() <= endTime.getTime() && nowTime.getTime() >= startTime.getTime()) {
            //网络时间 <= 开始时间 + 持续时间 && 网络时间 >= 开始时间  说明任务正在进行中
            return 1;
        } else if (nowTime.getTime() < startTime.getTime()) {
            //网络时间 < 开始时间 说明任务未进行
            return 2;
        }
        return -1;
    }


    /**
     * 日期时间字符串转换成毫秒
     *
     * @param dateTime 2016-12-31
     * @throws ParseException
     */
    public static long date2Msec(String dateTime) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(dateTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar.getTimeInMillis();
    }


    //获取网络时间
    private String getNetworkTime() {
        URL url = null;//取得资源对象
        String format = "";
        try {
            url = new URL("http://www.baidu.com");
            URLConnection uc = url.openConnection();//生成连接对象
            uc.connect(); //发出连接
            long ld = uc.getDate(); //取得网站日期时间
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(ld);
            format = formatter.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return format;
//        //请求网络时间是耗时操作，放到子线程中进行
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                netTime = getNetTime();
//            }
//        }).start();
    }

}
