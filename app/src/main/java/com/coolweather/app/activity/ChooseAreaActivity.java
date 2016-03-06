package com.coolweather.app.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.Util.HttpUtil;
import com.coolweather.app.Util.Utility;
import com.coolweather.app.database.CoolWeatherDb;
import com.coolweather.app.model.City;
import com.coolweather.app.model.Count;
import com.coolweather.app.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by duanbiwei on 2015/9/20.
 */
public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;;
    public static final int LEVEL_COUNTY = 2;

    private TextView titleText;
    private ListView listView;
    private ProgressDialog progressDialog;

    private ArrayAdapter<String> adapter;
    private CoolWeatherDb coolWeatherDb;
    private List<String> dataList = new ArrayList<>();

    private List<Province> provincesList;
    private List<City> cityList;
    private List<Count> countList;

    private Province selectedProvince;
    private City selectedCity;
    private Count selectedCount;

    private int level;
    private boolean isFromWeatherActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFromWeatherActivity = getIntent().getBooleanExtra("from_weather_activity", false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("city_selected", false) && !isFromWeatherActivity) {
            Intent i = new Intent(this, WeatherActivity.class);
            startActivity(i);
            finish();
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);
        titleText = (TextView) findViewById(R.id.title_text);
        listView = (ListView) findViewById(R.id.list_view);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        coolWeatherDb = CoolWeatherDb.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (level == LEVEL_PROVINCE) {
                    selectedProvince = provincesList.get(position);
                    queryCities();
                }else if (level == LEVEL_CITY) {
                    selectedCity = cityList.get(position);
                    queryCounties();
                } else if (level == LEVEL_COUNTY) {
                    String countyCode = countList.get(position).getCountCode();
                    Intent i = new Intent(ChooseAreaActivity.this, WeatherActivity.class);
                    i.putExtra("county_code", countyCode);
                    startActivity(i);
                    finish();
                }
            }
        });
        queryProvinces();
    }

    private void queryProvinces() {
        provincesList = coolWeatherDb.loadProvince();
        if (provincesList.size() > 0) {
            dataList.clear();
            for (Province province : provincesList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            level = LEVEL_PROVINCE;
        } else {
            queryFromServer(null, "province");
        }
    }

    private void queryCities() {
        cityList = coolWeatherDb.loadCities(selectedProvince.getId());
        if (cityList.size() > 0) {
            dataList.clear();
            for (City c : cityList) {
                dataList.add(c.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());

            level = LEVEL_CITY;
        } else {
            queryFromServer(selectedProvince.getProvinceCode(), "city");
        }
    }

    private void queryCounties() {
        countList = coolWeatherDb.loadCounts(selectedCity.getId());
        if (countList.size() > 0) {
            dataList.clear();
            for (Count c : countList) {
                dataList.add(c.getCountName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedCity.getCityName());
            level = LEVEL_COUNTY;
        } else {
            queryFromServer(selectedCity.getCityCode(), "count");
        }
    }

    private void queryFromServer(final String code, final String type) {
        String adress;
        if (!TextUtils.isEmpty(code)) {
            adress = "http://www.weather.com.cn/data/list3/city" + code + ".xml";
        } else {
            adress = "http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();

        HttpUtil.sendHttpRequest(adress, new HttpUtil.HttpCallBackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.handleProvincesResponse(coolWeatherDb, response);
                } else if ("city".equals(type)) {
                    result = Utility.handleCitiesResponse(coolWeatherDb, response, selectedProvince.getId());
                } else if ("count".equals(type)) {
                    result = Utility.handleCountiesResponse(coolWeatherDb, response, selectedCity.getId());
                }
                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {
                                queryCities();
                            } else if ("count".equals(type)) {
                                queryCounties();
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
                        Toast.makeText(ChooseAreaActivity.this, "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载....");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    @Override
    public void onBackPressed() {
        if (level == LEVEL_COUNTY) {
            queryCities();
        } else if (level == LEVEL_CITY) {
            queryProvinces();
        } else {
            if (isFromWeatherActivity) {
                Intent i = new Intent(this, WeatherActivity.class);
                startActivity(i);
            }
            finish();
        }
    }
}
