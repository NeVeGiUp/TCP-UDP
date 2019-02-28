package com.itc.smartbroadcast.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.itc.smartbroadcast.bean.MusicFolderInfo;
import com.itc.smartbroadcast.event.account.MusicLibInfo;
import com.itc.smartbroadcast.helper.ResultOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by youmu on 2018/10.
 */

public class ResultDB {

    /**
     * 数据库名
     */
    public static final String SMARTBROADCAST = "smartBroadCastDB";

    /**
     * 数据库版本
     */
    public static final int VERSION = 1;
    private static ResultDB resultDB;
    private SQLiteDatabase db;

    /**
     * 将构造方法私有化
     */
    private ResultDB(Context context) {
        ResultOpenHelper dbHelper = new ResultOpenHelper(context,
                SMARTBROADCAST, null, 1);
        db = dbHelper.getWritableDatabase();
    }

    /**
     * 获取DB的实例。
     */
    public synchronized static ResultDB getInstance(Context context) {
        if (resultDB == null) {
            resultDB = new ResultDB(context);
        }
        return resultDB;
    }

    /**
     * 删除IP地址
     */
    public void deleteIp(String ip) {
        db.delete("config", "loginIP = ?", new String[]{ip});
    }

    /**
     * 保存登录IP
     */
    public void saveIP(String ip) {
        Cursor cursor = db.query("config", null, null, null, null, null, null);
        if (!ip.equals("")) {
            ContentValues values = new ContentValues();
            values.put("loginIP", ip);
            db.delete("config", "loginIP = ?", new String[]{ip});
            db.insert("config", null, values);
        }
    }

    /**
     * 获取IP
     */
    public List<String> getIp() {
        List<String> list = new ArrayList<String>();
        Cursor cursor = db.query("config", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String result = cursor.getString(cursor.getColumnIndex("loginIP")).toString();
                list.add(result);
            } while (cursor.moveToNext());
        }
        return list;
    }

}


