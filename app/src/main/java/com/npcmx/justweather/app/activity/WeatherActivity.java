package com.npcmx.justweather.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.npc.cmx.justweather.R;
import com.npcmx.justweather.app.service.AutoUpdateService;
import com.npcmx.justweather.app.util.HttpCallbackListener;
import com.npcmx.justweather.app.util.HttpUtil;
import com.npcmx.justweather.app.util.Utility;

/**
 * Created by CMX on 2016/5/14.
 */
public class WeatherActivity extends Activity implements View.OnClickListener {
    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView minTempText;
    private TextView maxTempText;
    private TextView currentDateText;
    private Button switchCity;
    private Button refreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);

        //初始化控件
        weatherInfoLayout = (LinearLayout)findViewById(R.id.weather_info_layout);
        cityNameText = (TextView)findViewById(R.id.city_name);
        publishText = (TextView)findViewById(R.id.pulish_text);
        weatherDespText = (TextView)findViewById(R.id.weather_desp);
        maxTempText = (TextView)findViewById(R.id.temp_max);
        minTempText = (TextView)findViewById(R.id.temp_min);
        currentDateText = (TextView)findViewById(R.id.current_date);
        switchCity = (Button)findViewById(R.id.switch_city);
        refreshWeather = (Button)findViewById(R.id.refresh_weather);
        switchCity.setOnClickListener(this);
        refreshWeather.setOnClickListener(this);

        String cityCode = getIntent().getStringExtra("city_code");
        if (!TextUtils.isEmpty(cityCode)){
            //有城市代号就去查询天气
            publishText.setText("正在同步天气");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            queryWeatherCode(cityCode);
        }else {
            showWeather();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.switch_city:
                Intent intent = new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from_weather_activity",true);
                startActivity(intent);
                finish();
                break;
            case R.id.refresh_weather:
                publishText.setText("Refreshing...");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String cityCode = prefs.getString("city_code","");
                queryWeatherCode(cityCode);
                break;
            default:
                break;

        }
    }

    /**
     * 查询城市天气
     * */
    private void queryWeatherCode(String cityCode){
        String address = "https://api.heweather.com/x3/weather?cityid="+cityCode+"&key=4f8055b25db942269ed64b8ac49a79c0";
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Utility.handleWeatherResponse(WeatherActivity.this,response);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        showWeather();
                    }
                });
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    /**
     * 从本地文件读取天气信息并显示到界面上
     * */
    private void showWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name",""));
        minTempText.setText(prefs.getString("minTemp","")+"℃");
        maxTempText.setText(prefs.getString("maxTemp","")+"℃");
        weatherDespText.setText(prefs.getString("weather_desp",""));
        currentDateText.setText(prefs.getString("current_date",""));
        publishText.setText("今天"+prefs.getString("publish_time","")+"发布");
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, AutoUpdateService.class);
        startService(intent);
    }


}
