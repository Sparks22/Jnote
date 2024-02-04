package com.note.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;


/**
 * 作者：Sparks
 * 创建时间：2023/3/2  20:53
 * 描述：TODO
 */
public class CURD {
    private final DataBaseHelper mHelper;

    public CURD(Context context) {
        mHelper = new DataBaseHelper(context);
    }


    public long insert(String title, String content, String tag) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        //设置日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());         //获取系统的当前时
        String finalDate = sdf.format(date);        //格式化日期

        values.put(DataBaseHelper.TIME, finalDate);
        values.put(DataBaseHelper.TITLE, title);
        values.put(DataBaseHelper.CONTENT, content);
        values.put(DataBaseHelper.T, tag);
        long id = db.insert(DataBaseHelper.TABLE_NAME, null, values);
        db.close();
        return id > 0 ? id : 0;
    }

    public long insertNote(Note note) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        //设置日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());         //获取系统的当前时
        String finalDate = sdf.format(date);        //格式化日期

        values.put(DataBaseHelper.TIME, finalDate);
        values.put(DataBaseHelper.TITLE, note.getTitle());
        values.put(DataBaseHelper.CONTENT, note.getContent());
        values.put(DataBaseHelper.T, note.getTag());
        long id = db.insert(DataBaseHelper.TABLE_NAME, null, values);
        db.close();
        return id > 0 ? id : 0;
    }



    public long insertNoteHaveTime(Note note) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DataBaseHelper.TIME, note.getTime());
        values.put(DataBaseHelper.TITLE, note.getTitle());
        values.put(DataBaseHelper.CONTENT, note.getContent());
        values.put(DataBaseHelper.T, note.getTag());
        long id = db.insert(DataBaseHelper.TABLE_NAME, null, values);
        db.close();
        return id > 0 ? id : 0;
    }



    public boolean delete(long id) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int num = db.delete(DataBaseHelper.TABLE_NAME, DataBaseHelper.ID + "=?", new String[]{id + ""});
        db.close();
        return num > 0 ? true : false;
    }

    public boolean deleteAll() {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        int num = db.delete(DataBaseHelper.TABLE_NAME, null, null);
        db.close();
        return num > 0 ? true : false;
    }

    public boolean updata(long id, String title, String content, String tag) {
        SQLiteDatabase db = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        //设置日期格式
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());         //获取系统的当前时
        String finalDate = sdf.format(date);        //格式化日期

       // values.put(DataBaseHelper.TIME, finalDate);
        values.put(DataBaseHelper.TITLE, title);
        values.put(DataBaseHelper.CONTENT, content);
        values.put(DataBaseHelper.T, tag);

        int num = db.update(DataBaseHelper.TABLE_NAME, values, DataBaseHelper.ID + "=?", new String[]{id + ""});


        db.close();
        return num > 0 ? true : false;
    }


    //根据id查找
    public Note findForID(long id) {

/*        哈希表（字典）
        Map<Integer,String> dict = new HashMap<>();
        Map<String,Integer> dict = new HashMap<>();
        dict.put(key,value);无序添加，key重复则覆盖掉；
        dict.get(key);通过key获取对应的value;
        dict.remove(key);删除指定key;
        dict.size();返回数组长度;
        dict.replace(key,value);修改指定键的值;
        dict.keySet();返回包含所有key值的hashSet数组;
        dict.isEmpty();判断字典是否为空;
        dict.containsKey();判断字典是否包含指定key,返回true/false;*/

        Note found = new Note();
        SQLiteDatabase db = mHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + DataBaseHelper.TABLE_NAME + " where _id =?",
                new String[]{id + ""});
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                int c = cursor.getColumnIndex(DataBaseHelper.ID);
                found.setId(cursor.getLong(c));
                c = cursor.getColumnIndex(DataBaseHelper.TITLE);
                found.setTitle(cursor.getString(c));
                c = cursor.getColumnIndex(DataBaseHelper.CONTENT);
                found.setContent(cursor.getString(c));
                c = cursor.getColumnIndex(DataBaseHelper.TIME);
                found.setTime(cursor.getString(c));
                c = cursor.getColumnIndex(DataBaseHelper.T);
                found.setTag(cursor.getString(c));
            }
        }
        cursor.close();
        db.close();
        return found;
    }


    //查询数据,查询表中的所有内容，将查询的内容用note的对象属性进行存储，并将该对象存入集合中。
    public List<Note> queryAll() {

        List<Note> allData = new ArrayList<Note>();
        Note found;
        SQLiteDatabase db = mHelper.getWritableDatabase();
        Cursor cursor = db.query(DataBaseHelper.TABLE_NAME, null,null,null,null,null,null);
        if (cursor!= null) {
            while (cursor.moveToNext()) {
                found = new Note();
                found.setId(cursor.getLong(0));
                found.setTitle(cursor.getString(1));
                found.setContent(cursor.getString(2));
                found.setTime(cursor.getString(3));
                found.setTag(cursor.getString(4));
                allData.add(found);
            }
        }
        cursor.close();
        db.close();
       // Log.d("CURDNoteList",allData.toString());
        Collections.reverse(allData);//倒序
        return allData;
    }

    public List<String> queryAllTag(){
        List<Note> notes = queryAll();
        List<String> taglist = new ArrayList<>();
        Set<String> set = new HashSet<>();
        for (Note note:notes) {
            if (set.add(note.getTag())) {
                taglist.add(note.getTag());
            }
        }
        return taglist;
    }

    public boolean isHadTag(String tag) {
        List<String> tags = queryAllTag();
        for (String t: tags) {
            if(t.equals(tag)){
                return false;
            }
        }
        return true;
    }
    public List<Note> queryAllNoteForTag(String tag){
        List<Note> notes = queryAll();
        List<Note> taglist =new ArrayList<>();
        for (Note note:notes) {
            if (note.getTag().equals(tag)) {
                taglist.add(note);
            }
        }
        return taglist;
    }



}