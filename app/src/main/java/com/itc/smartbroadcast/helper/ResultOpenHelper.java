package com.itc.smartbroadcast.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by youmu on 2018/10.
 */
public class ResultOpenHelper extends SQLiteOpenHelper {
    Context mContext;

    /**
     *  建表语句
     */
    public static final String DATAIP = "create table config("
            + "id integer primary key autoincrement, "
            + "loginIP text)";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATAIP);
    }

    public ResultOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists config");
        onCreate(db);
    }
}
