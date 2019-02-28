package com.itc.smartbroadcast.util;

import android.widget.ListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @Content :
 * @Author : lik
 * @Time : 18-9-7 下午1:59
 */
public class DateUtil {

    /**
     * 获取当前日期是周几<br>
     *
     * @param dt
     * @return 当前日期是周几
     */
    public static int getWeekOfDate(Date dt) {
        int[] weekDays = {6, 0, 1, 2, 3, 4, 5};
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 获取最后一天的日期
     * @param dateArr
     * @return
     */
    public static String getDateLast(String[] dateArr) {
        String lastDateStr = "2001-01-01";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (String date : dateArr) {
            int day = Integer.parseInt(date.split("-")[2]);
            int month = Integer.parseInt(date.split("-")[1]);
            int year = Integer.parseInt(date.split("-")[0]);
            if ((day <= 31 && day > 0) && (month > 0 && month <= 12) && (year > 2001 && year < 2050)) {
                try {
                    Date lastDate = sdf.parse(lastDateStr);
                    Date nDate = sdf.parse(date);
                    if (nDate.getTime() > lastDate.getTime()) {
                        lastDateStr = sdf.format(nDate);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        return lastDateStr;
    }

    /**
     * 判断当天是否执行任务
     * @param dateArr
     * @return
     */
    public static boolean isToday(String[] dateArr) {

        boolean bol = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String todayDateStr = sdf.format(new Date());
        for (String date : dateArr) {
            if (todayDateStr.equals(date)){
                bol = true;
            }
        }

        return bol;
    }

    public static String[] getDate(String[] dateArr) {
        List<String> dates = new ArrayList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        for (String date : dateArr) {
            int day = Integer.parseInt(date.split("-")[2]);
            int month = Integer.parseInt(date.split("-")[1]);
            int year = Integer.parseInt(date.split("-")[0]);
            if ((day <= 31 && day > 0) && (month > 0 && month <= 12) && (year > 2001 && year < 2050)) {
                dates.add(date);
            }
        }

        String[] dateRe = new String[dates.size()];
        int i = 0;
        for (String str : dates) {
            dateRe[i] = str;
            i++;
        }
        return dateRe;
    }

}
