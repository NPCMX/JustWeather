package com.npcmx.justweather.app.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.npcmx.justweather.app.model.City;
import com.npcmx.justweather.app.model.County;
import com.npcmx.justweather.app.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CMX on 2016/5/13.
 */
public class JustWeatherDB {

    /**数据库名*/
    public static final String DB_NAME = "just_weather";

    /**数据库版本*/
    public static final int VERSION = 1;

    //单例模式是全局只产生一个justWeatherDB
    private static JustWeatherDB justWeatherDB;
    private SQLiteDatabase db;

    //配合单例模式，将构造方法私有化
    private JustWeatherDB(Context context){
        //创建数据库，会调用JUstWeatherOpenHelper中的建表语句
        //注意区分数据库和数据表单
        JustWeatherOpenHelper dbHelper = new JustWeatherOpenHelper(context,DB_NAME,null,VERSION);
        db = dbHelper.getWritableDatabase();
    }

    /**获取JustWeatherDB的单例*/
    /*synchronized Java语言的关键字，
    * 当它用来修饰一个方法或者一个代码块的时候，
    * 能够保证在同一时刻最多只有一个线程执行该段代码。*/
    public synchronized static JustWeatherDB getInstance(Context context){
        if (justWeatherDB == null){
            justWeatherDB = new JustWeatherDB(context);
        }
        return justWeatherDB;
    }
    
    /**将Province实例储存到数据库*/
    public void saveProvince(Province province){
        if (province!=null){
            //使用ContentValue组装省份表的数据，储存到表中
            ContentValues values = new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            db.insert("Province",null,values);
        }
    }
    
    /**从数据库中读取全国所有的省份信息*/
    public List<Province> loadProvinces(){
        List<Province> list = new ArrayList<>();
        
        //需要遍历全表，所以查找命令只传入表名就好
        Cursor cursor = db.query("Province",null,null,null,null,null,null);
        //必须执行cursor.moveToFirst()，否则cursor不会进入表中，会返回空
        if (cursor.moveToFirst()){
            do{
                //使用表中的数据组装N个省份的实例对象
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                //把组装好的省份实例添加到List当中
                list.add(province);
            }while(cursor.moveToNext());
        }
        return list;
    }

    /**把City实例储存到数据库*/
    public void saveCity(City city){
        if (city!=null){
            //使用ContentValue组装省份表的数据，储存到表中
            ContentValues values = new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            db.insert("City",null,values);
        }
    }

    /**从数据库中读取某省份所有的城市信息*/
    public List<City> loadCities(int provinceId){
        List<City> list = new ArrayList<>();

        //需要限定查找某一省份的城市
        Cursor cursor = db.query("City",null,"province_id = ?",new String[]{String.valueOf(provinceId)},null,null,null);
        //必须执行cursor.moveToFirst()，否则cursor不会进入表中，会返回空
        if (cursor.moveToFirst()){
            do{
                //使用表中的数据组装N个城市的实例对象
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                //把组装好的城市实例添加到List当中
                list.add(city);
            }while(cursor.moveToNext());
        }
        return list;
    }

    /**把County实例储存到数据库*/
    public void saveCounty(County county){
        if (county!=null){
            //使用ContentValue组装省份表的数据，储存到表中
            ContentValues values = new ContentValues();
            values.put("county_name",county.getCountyName());
            values.put("county_code",county.getCountyCode());
            db.insert("County",null,values);
        }
    }

    /**从数据库中读取某城市所有县城的信息*/
    public List<County> loadCounty(int cityId){
        List<County> list = new ArrayList<>();

        //需要限定查找某一城市的县城
        Cursor cursor = db.query("County",null,"city_id = ?",new String[]{String.valueOf(cityId)},null,null,null);
        //必须执行cursor.moveToFirst()，否则cursor不会进入表中，会返回空
        if (cursor.moveToFirst()){
            do{
                //使用表中的数据组装N个县城的实例对象
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                //把组装好的县城实例添加到List当中
                list.add(county);
            }while(cursor.moveToNext());
        }
        return list;
    }
}
