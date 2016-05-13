package com.npcmx.justweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by CMX on 2016/5/13.
 */
public class JustWeatherOpenHelper extends SQLiteOpenHelper {

    /*Province表建表语句*/
    public static final String CREATE_PROVINCE = "create table Province("
            + "id integer primary key autoincrement,"
            + "province_name text)";
//            + "province_code text)";

    /*City表建表语句*/
    public static final String CREATE_CITY = "create table City("
            + "id integer primary key autoincrement,"
            + "city_name text,"
            + "city_code text,"
            + "province_name text)";

    /*County表建表语句*/
//    public static final String CREATE_COUNTY = "create table City("
//            + "id integer primary key autoincrement,"
//            + "city_name text,"
//            + "city_code text,"
//            + "city_id integer)";

    public JustWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,int version){
        super(context,name,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
       db.execSQL(CREATE_PROVINCE);//创建省份表
        db.execSQL(CREATE_CITY);//创建城市表
//        db.execSQL(CREATE_COUNTY);//创建县城表
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
