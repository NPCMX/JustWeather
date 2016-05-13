package com.npcmx.justweather.app.model;

/**
 * Created by CMX on 2016/5/13.
 */
public class County {
    private int id;
    private String countyName;
    private String countyCode;
    private int cityId;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getCountyName(){
        return  countyName;
    }

    public void setCountyName(String countyName){
        this.countyName = countyName;
    }

    public String getCountyCode(){
        return  countyCode;
    }

    public void setCountyCode(String countyCode){
        this.countyCode = countyCode;
    }

    public void setCityId(int cityId){
        this.cityId = cityId;
    }

    public int getCityId(){
        return cityId;
    }
}
