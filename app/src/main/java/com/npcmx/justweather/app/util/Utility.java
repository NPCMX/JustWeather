package com.npcmx.justweather.app.util;

//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.npcmx.justweather.app.db.JustWeatherDB;
import com.npcmx.justweather.app.model.City;
import com.npcmx.justweather.app.model.Province;

import org.json.JSONArray;
//import org.json.JSONException;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
//import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by CMX on 2016/5/13.
 */
public class Utility {

    /**
     * 处理服务器返回的城市数据（实测耗时过久）
     * */
    public synchronized static boolean handleCityListResponse(JustWeatherDB justWeatherDB,String response) {
        if (response != null) {
            try {
                JSONObject jsonObject1 = new JSONObject(response);
                JSONArray jsonArray = jsonObject1.getJSONArray("city_info");
                Set<String> set = new HashSet<>();
                for (int i = 0; i <= jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String id = jsonObject.getString("id");
                    String cityName = jsonObject.getString("city");
                    String provName = jsonObject.getString("prov");
                    Province province = new Province();
                    City city = new City();
                    if (!set.contains(provName)) {
                        province.setProvinceName(provName);
                        set.add(provName);
                        justWeatherDB.saveProvince(province);
                    }
                    city.setCityCode(id);
                    city.setCityName(cityName);
                    city.setProvinceName(provName);
                    justWeatherDB.saveCity(city);
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析服务器返回的天气JSON数据，并保存到本地
     * */
    public static void handleWeatherResponse(Context context,String response){
        try {
            //简直爆炸
            JSONArray jsonArray = new JSONObject(response).getJSONArray("HeWeather data service 3.0");
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            JSONObject jsonObjectCity = jsonObject.getJSONObject("basic");
            JSONObject jsonObjectPublish = jsonObjectCity.getJSONObject("update");
            JSONArray jsonArrayDaily = jsonObject.getJSONArray("daily_forecast");
            JSONObject jsonObjecDate = jsonArrayDaily.getJSONObject(0);
            JSONObject jsonObjectDesp = jsonObjecDate.getJSONObject("cond");
            JSONObject jsonObjectTmp = jsonObjecDate.getJSONObject("tmp");
            String cityName = jsonObjectCity.getString("city");
            String weatherCode = jsonObjectCity.getString("id");
            String minTemp = jsonObjectTmp.getString("min");
            String maxTemp = jsonObjectTmp.getString("max");
            String weatherDesp = jsonObjectDesp.getString("txt_d");
            String publishTime = jsonObjectPublish.getString("loc");
            saveWeatherInfo(context,cityName,weatherCode,minTemp,maxTemp,weatherDesp,publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将所有天气信息存储到SharePreferences文件中
     * */
    public static void saveWeatherInfo(Context context,String cityName,String weatherCode,String minTemp,
                                       String maxTemp,String wetherDesp,String publishTime){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("city_code",weatherCode);
        editor.putString("minTemp",minTemp);
        editor.putString("maxTemp",maxTemp);
        editor.putString("weather_desp",wetherDesp);
        editor.putString("publish_time",publishTime);
        editor.putString("current_date",sdf.format(new Date()));
        editor.commit();
    }



/**
 * 尝试使用Gson解析，解析时间依旧太久
 * */
//    Set<String> set = new HashSet<>();
//                Gson gson = new Gson();
//                JsonBean jsonBean = gson.fromJson(response,JsonBean.class);
//                List<Info> infoList = jsonBean.getCity_info();
//                for (Info data:infoList){
//                    City city = new City();
//                    Province province = new Province();
//                    if (!set.contains(data.getProv())){
//                        province.setProvinceName(data.getProv());
//                        justWeatherDB.saveProvince(province);
//                        set.add(data.getProv());
//                    }
//                    city.setCityName(data.getCity());
//                    city.setProvinceName(data.getProv());
//                    city.setCityCode(data.getId());
//                    justWeatherDB.saveCity(city);
//                }
//                List<myData> dataList = gson.fromJson(response,new TypeToken<List<Data>>(){}.getType());
//                for (myData data:dataList){
//                    City city = new City();
//                    Province province = new Province();
//                    if (!set.contains(data.getProv())){
//                        province.setProvinceName(data.getProv());
//                        justWeatherDB.saveProvince(province);
//                    }
//                    set.add(data.getProv());
//                    city.setCityName(data.getCity());
//                    city.setProvinceName(data.getProv());
//                    city.setCityCode(data.getId());
//                    justWeatherDB.saveCity(city);
//                }
//    class JsonBean{
//       private List<Info> city_info;
//        public List<Info> getCity_info(){
//            return city_info;
//        }
//    }
//
//    class Info{
//        private String id;
//        private String city;
//        private String prov;
//
//        public String getId(){
//            return id;
//        }
//        public void setId(String id){
//            this.id = id;
//        }
//        public String getCity(){
//            return city;
//        }
//        public void setCity(String city){
//            this.city = city;
//        }
//        public String getProv(){
//            return prov;
//        }
//        public void setProv(String prov){
//            this.prov = prov;
//        }

    }
