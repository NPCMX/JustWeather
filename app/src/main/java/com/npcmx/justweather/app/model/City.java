package com.npcmx.justweather.app.model;

/**
 * Created by CMX on 2016/5/13.
 */
public class City {
    private int id;
    private String cityName;
    private String cityCode;
    private String provinceName;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getCityName(){
        return  cityName;
    }

    public void setCityName(String cityName){
        this.cityName = cityName;
    }

    public String getCityCode(){
        return cityCode;
    }

    public void setCityCode(String cityCode){
        this.cityCode = cityCode;
    }

    public void setProvinceName(String provinceName){
        this.provinceName = provinceName;
    }

    public String getProvinceName(){
        return provinceName;
    }
}
