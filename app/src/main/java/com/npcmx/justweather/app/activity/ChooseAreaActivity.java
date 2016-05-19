package com.npcmx.justweather.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
//import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.npc.cmx.justweather.R;
import com.npcmx.justweather.app.db.JustWeatherDB;
import com.npcmx.justweather.app.model.City;
import com.npcmx.justweather.app.model.Province;
import com.npcmx.justweather.app.util.HttpCallbackListener;
import com.npcmx.justweather.app.util.HttpUtil;
import com.npcmx.justweather.app.util.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CMX on 2016/5/13.
 */
public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;

    private ProgressDialog progressDialog;
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private JustWeatherDB justWeatherDB;
    private List<String> dataList = new ArrayList<>();

    /**
     * 省列表
     * */
    private List<Province> provinceList;
    /**
     * 城市列表
     * */
    private List<City> cityList;
    /**
     * 选中的省
     * */
    private Province selectedProvince;
    /**
     * 当前选中的级别
     * */
    private int currentLevel;

    /**
     * 是否从WeatherActivity中跳转过来
     * */
    private boolean isFromWeatherActivtiy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isFromWeatherActivtiy = getIntent().getBooleanExtra("from_weather_activity",false);
        setContentView(R.layout.choose_area);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("city_selected",false)&&!isFromWeatherActivtiy){
            Intent intent = new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        listView = (ListView)findViewById(R.id.list_view);
        titleText = (TextView)findViewById(R.id.title_text);
        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        justWeatherDB = JustWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE){
                    selectedProvince = provinceList.get(position);
                    queryCities();
                }else if (currentLevel == LEVEL_CITY){
                    String cityCode = cityList.get(position).getCityCode();
                    Intent intent = new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("city_code",cityCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();
    }

    /**
     * 查询全国的省，优先从数据库查询，若没有则从服务器查询
     * */
    private void queryProvinces(){
        provinceList = justWeatherDB.loadProvinces();
        if (provinceList.size()>0){
            dataList.clear();
            for (Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel = LEVEL_PROVINCE;
        }else {
            queryFromServer("province");
        }
    }

    /**
     * 查询选中的省内所有的市，优先从数据库查询，若没有则从服务器查询
     * */
    private void queryCities(){
        cityList = justWeatherDB.loadCities(selectedProvince.getProvinceName());
        if (cityList.size()>0){
            dataList.clear();
            for (City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel = LEVEL_CITY;
        }else {
            queryFromServer("city");
        }
    }

    /**
     * 从服务器去查询省、市的数据
     * */
    private void queryFromServer(final String type){
        String address;
        address = "https://api.heweather.com/x3/citylist?search=allchina&key=4f8055b25db942269ed64b8ac49a79c0";
        showProgressDialog();
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result;
                result = Utility.handleCityListResponse(justWeatherDB,response);
                if (result){
                    //通过runOnUiThread()方法回到主线处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("TAGCMX","-------here-----");
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"failed to loading",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度条对话框
     * */
    private void showProgressDialog(){
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("首次加载时间较长，抱歉......");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度条对话框
     * */
    private void closeProgressDialog(){
        if (progressDialog != null){
            progressDialog.dismiss();
        }

    }

    /**
     * 捕获Back键，根据当前级别判断应该返回哪个操作
     * */
    @Override
    public void onBackPressed() {
        if (currentLevel == LEVEL_CITY){
            queryProvinces();
        }else {
            if (isFromWeatherActivtiy){
                Intent  intent = new Intent(this,WeatherActivity.class);
                startActivity(intent);
            }
            finish();
        }
    }
}
