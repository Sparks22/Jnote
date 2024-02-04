package com.note.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

/**
 * 作者：Sparks
 * 创建时间：2023/2/25  19:42
 * 描述：TODO
 */
public class DataBaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "notes.db";
    public static final String TABLE_NAME = "notebase";
    public static final String TAG_TABLE_NAME = "tagbase";
    public static final String ID = "_id";
    public static final String TITLE = "title";
    public static final String CONTENT = "content";
    public static final String TIME = "time";
    public static final String T = "tag";
    public static final String MODE = "mode";
    public static final int DATABASE_VERSION = 1;




    public DataBaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlstr = "CREATE TABLE "+ TABLE_NAME
                + "("
                + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + TITLE +" TEXT NOT NULL,"
                + CONTENT + " TEXT NOT NULL,"
                + TIME + " TEXT NOT NULL,"
                + T +" TEXT NOT NULL,"
                + MODE + " INTEGER DEFAULT 1)";

       // String sqlTag = "create table "+TAG_TABLE_NAME+"(_id INTEGER PRIMARY KEY AUTOINCREMENT,"+T+" TEXT NOT NULL)";
        db.execSQL(sqlstr);
       // db.execSQL(sqlTag);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        //禁用WAL
        db.disableWriteAheadLogging();
    }


}