package com.npcmx.justweather.app.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.npcmx.justweather.app.db.JustWeatherDB;
import com.npcmx.justweather.app.model.City;
import com.npcmx.justweather.app.model.Province;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by CMX on 2016/5/13.
 */
public class Utility {

    /**
     * 处理服务器返回的城市数据
     * */
    public synchronized static boolean handleCityListResponse(JustWeatherDB justWeatherDB,String response) {
        if (response != null) {
            try {
//                Set<String> set = new HashSet<>();
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
