package com.liujiang.todolist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015/1/5.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public final static String DATABASE_NAME = "todolist.db";
    public final static int DATABASE_VERSION = 1;
    public final static String TABLE_NAME = "task";
    public final static String FIELD_ID = "_id";
    public final static String FIELD_TITLE = "_title";
    public final static String FIELD_ADDRESS = "_address";
    public final static String FIELD_PARTICIPATOR = "_participator";
    public final static String FIELD_START_TIME = "_starttime";
    public final static String FIELD_END_TIME = "_endtime";
    public final static String FIELD_START_ALARM = "_startalarm";
    public final static String FIELD_END_ALARM = "_endalarm";
    public final static String FIELD_REPEAT = "_repeat";
    public final static String FIELD_SUBTASK = "_subtask";
    public final static String FIELD_PROJECT = "_project";
    public final static String FIELD_LABEL = "_lebel";
    public final static String FIELD_IMPORTANCE = "_importance";
    public final static String FIELD_PS = "_postscript";
    public final static String FIELD_STATUS = "_status";


    public static final String DB_NAME = "todolist.db"; //数据库名称
    public static final int version = 1; //数据库版本

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, version);
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //String sql = "create table user(username varchar(20) not null , password varchar(60) not null );";
        String sql = "Create table " + TABLE_NAME + "(" + FIELD_ID
                + " integer primary key autoincrement," + FIELD_TITLE + " varchar(60) not null ,"
                + FIELD_ADDRESS + " varchar(60) ," + FIELD_PARTICIPATOR + " varchar(60) ,"
                + FIELD_START_TIME + " integer ,"+FIELD_END_TIME + " integer ,"
                + FIELD_START_ALARM + " integer ,"+ FIELD_END_ALARM + " integer ,"
                + FIELD_REPEAT + " integer ," + FIELD_SUBTASK + " varchar(60) ,"
                + FIELD_PROJECT + " varchar(60) ," + FIELD_LABEL + " varchar(60) ,"
                + FIELD_IMPORTANCE + " integer ," + FIELD_PS + " varchar(60) ,"
                + FIELD_STATUS + " integer  )";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

}
