package com.itc.smartbroadcast.activity.event.utils;

import com.alibaba.fastjson.JSONObject;
import com.itc.smartbroadcast.bean.InstantTask;
import com.itc.smartbroadcast.bean.LoginedInfo;
import com.itc.smartbroadcast.bean.Scheme;
import com.itc.smartbroadcast.bean.Task;
import com.itc.smartbroadcast.cache.AppDataCache;
import com.itc.smartbroadcast.util.ThreadUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lik on 18-9-21.
 */

public class TaskUtils {

    public static List<Task> sort(List<Task> taskList) {

        for (int i = 0; i < taskList.size(); i++) {
            for (int j = i; j < taskList.size(); j++) {

                int start1 = Integer.parseInt(taskList.get(i).getTaskStartDate().split(":")[0]) * 60 * 60 + Integer.parseInt(taskList.get(i).getTaskStartDate().split(":")[1]) * 60 + Integer.parseInt(taskList.get(i).getTaskStartDate().split(":")[2]);
                int start2 = Integer.parseInt(taskList.get(j).getTaskStartDate().split(":")[0]) * 60 * 60 + Integer.parseInt(taskList.get(j).getTaskStartDate().split(":")[1]) * 60 + Integer.parseInt(taskList.get(j).getTaskStartDate().split(":")[2]);
                if (start1 > start2) {
                    Task task = taskList.get(i);
                    taskList.set(i, taskList.get(j));
                    taskList.set(j, task);
                }
            }
        }
        return taskList;
    }

    public static boolean getIsManager() {
        boolean bol = false;
        String userJson = AppDataCache.getInstance().getString("loginedMsg");
        LoginedInfo userInfo = JSONObject.parseObject(userJson, LoginedInfo.class);
        if (userInfo.getUserType().equals("00")) {
            bol = true;
        } else {
            bol = false;
        }
        return bol;
    }

    /**
     * 获取用户编号
     *
     * @return
     */
    public static int getUserNum() {
        String userJson = AppDataCache.getInstance().getString("loginedMsg");
        LoginedInfo userInfo = JSONObject.parseObject(userJson, LoginedInfo.class);
        return userInfo.getUserNum();
    }


    /**
     * 检查任务执行时间是否冲突
     *
     * @param taskList
     * @param task
     * @return
     */
    public static boolean checkTaskTime(List<Task> taskList, Task task) {

        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
        boolean bol1 = true;
        try {
            for (int i = 0; i < taskList.size(); i++) {
                Task taskAfter = taskList.get(i);
                boolean bol = false;
                for (int j = 0; j < 7; j++) {
                    if (taskAfter.getTaskWeekDuplicationPattern()[j] == 1 && task.getTaskWeekDuplicationPattern()[j] == 1) {
                        bol = true;
                    }
                }
                if (bol) {
                    long afterDate = sdf1.parse(taskAfter.getTaskStartDate()).getTime() + (taskAfter.getTaskContinueDate() * 1000);
                    long date = sdf1.parse(task.getTaskStartDate()).getTime();

                    long afterDate1 = sdf1.parse(task.getTaskStartDate()).getTime() + (task.getTaskContinueDate() * 1000);
                    long date1 = sdf1.parse(taskAfter.getTaskStartDate()).getTime();

                    if (afterDate > date && afterDate1 > date1) {
                        bol1 = false;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bol1;

    }



    /**
     * 检查任务执行时间是否冲突
     *
     * @param taskList
     * @param task
     * @return
     */
    public static boolean checkTaskTimeToUpdate(List<Task> taskList, Task task) {

        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
        boolean bol1 = true;
        try {
            for (int i = 0; i < taskList.size(); i++) {
                Task taskAfter = taskList.get(i);
                boolean bol = false;
                for (int j = 0; j < 7; j++) {
                    if (taskAfter.getTaskWeekDuplicationPattern()[j] == 1 && task.getTaskWeekDuplicationPattern()[j] == 1) {
                        bol = true;
                    }
                }
                if (bol) {
                    long afterDate = sdf1.parse(taskAfter.getTaskStartDate()).getTime() + (taskAfter.getTaskContinueDate() * 1000);
                    long date = sdf1.parse(task.getTaskStartDate()).getTime();

                    long afterDate1 = sdf1.parse(task.getTaskStartDate()).getTime() + (task.getTaskContinueDate() * 1000);
                    long date1 = sdf1.parse(taskAfter.getTaskStartDate()).getTime();

                    if (afterDate > date && afterDate1 > date1) {
                        bol1 = false;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bol1;

    }

    /**
     * 及时任务排序
     * @param instantTaskList
     * @return
     */

    public static List<InstantTask> installTaskListOrder(List<InstantTask> instantTaskList) {

        List<InstantTask> instantTasks = new ArrayList<>();
        for (InstantTask instantTask : instantTaskList) {
            if (instantTask.getStatus() == 1){
                instantTasks.add(instantTask);
            }
        }
        for (InstantTask instantTask : instantTaskList) {
            if (instantTask.getStatus() == 2){
                instantTasks.add(instantTask);
            }
        }
        for (InstantTask instantTask : instantTaskList) {
            if (instantTask.getStatus() == 0){
                instantTasks.add(instantTask);
            }
        }
        return instantTasks;
    }
    /**
     * 打铃方案排序
     * @param schemeList
     * @return
     */

    public static List<Scheme> schemeListOrder( List<Scheme> schemeList) {

        List<Scheme> schemes = new ArrayList<>();
        for (Scheme scheme : schemeList) {
            if (scheme.getSchemeStatus() == 1){
                schemes.add(scheme);
            }
        }
        for (Scheme scheme : schemeList) {
            if (scheme.getSchemeStatus() == 0){
                schemes.add(scheme);
            }
        }
        return schemes;
    }


}
