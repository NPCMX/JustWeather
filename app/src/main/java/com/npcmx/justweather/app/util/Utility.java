package com.npcmx.justweather.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.alibaba.fastjson.JSON;
import com.npcmx.justweather.app.db.JustWeatherDB;
import com.npcmx.justweather.app.model.City;
import com.npcmx.justweather.app.model.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by CMX on 2016/5/13.
 */
public class Utility {

    /**
     * 处理服务器返回的城市数据（实测耗时过久）
     * 使用android自带的JsonObject时速度非常慢！十分慢！
     * 使用fastJson的普通解析时，速度得到明显提升但是依旧需要等待很久
     * 下次试着使用fastJson的其他解析手段
     * */


    public synchronized static boolean handleCityListResponse(JustWeatherDB justWeatherDB,String response) {
        if (response != null) {
            try {
                Pakage pakage = JSON.parseObject(response,Pakage.class);
                List<Pakage.City_Info> cityInfos = pakage.getCity_info();
                Set<String> set = new HashSet<>();
                for (Pakage.City_Info cityInfo:cityInfos) {
                    String id = cityInfo.getId();
                    String cityName = cityInfo.getCity();
                    String provName = cityInfo.getProv();
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


//    public synchronized static boolean handleCityListResponse(JustWeatherDB justWeatherDB,String response) {
//        if (response != null) {
//            try {
//
//                JSONObject jsonObject1 = new JSONObject(response);
//                JSONArray jsonArray = jsonObject1.getJSONArray("city_info");
//                Set<String> set = new HashSet<>();
//                for (int i = 0; i <= jsonArray.length(); i++) {
//                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                    String id = jsonObject.getString("id");
//                    String cityName = jsonObject.getString("city");
//                    String provName = jsonObject.getString("prov");
//                    Province province = new Province();
//                    City city = new City();
//                    if (!set.contains(provName)) {
//                        province.setProvinceName(provName);
//                        set.add(provName);
//                        justWeatherDB.saveProvince(province);
//                    }
//                    city.setCityCode(id);
//                    city.setCityName(cityName);
//                    city.setProvinceName(provName);
//                    justWeatherDB.saveCity(city);
//            }
//               return true;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        return false;
//    }

    /**
     * 解析服务器返回的天气JSON数据，并保存到本地
     * */
    public static void handleWeatherResponse(Context context,String response){
        try {
            //简直爆炸
            JSONArray jsonArray = new JSONObject(response).getJSONArray("HeWeather data service 3.0");
            JSONObject jsonObject = jsonArray.getJSONObject(0);

            JSONArray jsonArrayDaily = jsonObject.getJSONArray("daily_forecast");

            JSONObject jsonObjecDate = jsonArrayDaily.getJSONObject(0);

            JSONObject jsonObjectDesp = jsonObjecDate.getJSONObject("cond");
            String weatherDespDay = jsonObjectDesp.getString("txt_d");
            String weatherDespNig = jsonObjectDesp.getString("txt_n");

            JSONObject jsonObjectTmp = jsonObjecDate.getJSONObject("tmp");
            String minTemp = jsonObjectTmp.getString("min");
            String maxTemp = jsonObjectTmp.getString("max");

            JSONObject jsonObjectCity = jsonObject.getJSONObject("basic");
            String cityName = jsonObjectCity.getString("city");
            String weatherCode = jsonObjectCity.getString("id");

            JSONObject jsonObjectPublish = jsonObjectCity.getJSONObject("update");
            String publishTime = jsonObjectPublish.getString("loc");

            JSONObject jsonObjectNow = jsonObject.getJSONObject("now");
//            JSONObject jsonObjectNew = jsonObjectNow.getJSONObject("cond");
            String nowTem = jsonObjectNow.getString("tmp");
//            String nowFeel = jsonObjectNew.getString("fl");
            String nowHum = jsonObjectNow.getString("hum");

            JSONObject jsonObjectSug = jsonObject.getJSONObject("suggestion");
            JSONObject jsonObjectSug1 = jsonObjectSug.getJSONObject("comf");
            JSONObject jsonObjectSug2 = jsonObjectSug.getJSONObject("drsg");
            JSONObject jsonObjectSug3 = jsonObjectSug.getJSONObject("uv");
            String suggestion = "  今天天气"+jsonObjectSug1.getString("brf")
                    +","+jsonObjectSug1.getString("txt")
                    +","+jsonObjectSug2.getString("txt")
                    +"\n\n    紫外线强度"+jsonObjectSug3.getString("brf")
                    +","+jsonObjectSug3.getString("txt");

            saveWeatherInfo(context,cityName,weatherCode,minTemp,maxTemp,weatherDespDay,weatherDespNig,
                    publishTime,nowTem,nowHum,suggestion);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将所有天气信息存储到SharePreferences文件中
     * */
    public static void saveWeatherInfo(Context context,String cityName,String weatherCode,String minTemp,
                                       String maxTemp,String weatherDespDay,String weatherDespNig,String publishTime,
                                        String nowTem,String nowHum,String suggestion){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);

        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("city_code",weatherCode);
        editor.putString("minTemp",minTemp);
        editor.putString("maxTemp",maxTemp);
        editor.putString("weather_desp_day",weatherDespDay);
        editor.putString("weather_desp_night",weatherDespNig);
        editor.putString("publish_time",publishTime);
        editor.putString("current_date",sdf.format(new Date()));
        editor.putString("now_temp",nowTem);
        editor.putString("now_Hum",nowHum);
        editor.putString("suggestion",suggestion);
        editor.commit();
    }
}

class Pakage{

    private List<City_Info> city_info = new ArrayList<>();
    private String status;

    public List<City_Info> getCity_info() {
        return city_info;
    }

    public void setCity_info(List<City_Info> city_info) {
        this.city_info = city_info;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void addCityInfo(City_Info cityInfo){
        city_info.add(cityInfo);
    }

    class City_Info {

        private String city;
        private String cnty;
        private String id;
        private String lat;
        private String lon;
        private String prov;

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getCnty() {
            return cnty;
        }

        public void setCnty(String cnty) {
            this.cnty = cnty;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLon() {
            return lon;
        }

        public void setLon(String lon) {
            this.lon = lon;
        }

        public String getProv() {
            return prov;
        }

        public void setProv(String prov) {
            this.prov = prov;
        }
    }
}


