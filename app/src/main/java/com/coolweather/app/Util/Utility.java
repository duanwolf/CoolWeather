package com.coolweather.app.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coolweather.app.database.CoolWeatherDb;
import com.coolweather.app.model.City;
import com.coolweather.app.model.Count;
import com.coolweather.app.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by duanbiwei on 2015/9/20.
 */
public class Utility {
    public synchronized static boolean handleProvincesResponse(CoolWeatherDb coolWeatherDb,
                                                               String response) {
        if (!TextUtils.isEmpty(response)) {
            String[] allValues = response.split(",");
            if (allValues != null && allValues.length > 0) {
                for (String p : allValues) {
                    String [] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    coolWeatherDb.saveProvince(province);
                }
            }
            return true;
        }
        return false;
    }

    public static boolean handleCitiesResponse(CoolWeatherDb db, String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            String [] allCities = response.split(",");
            if (allCities != null && allCities.length > 0) {
                for (String c : allCities) {
                    String [] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    db.saveCity(city);
                }
            }
            return true;
        }
        return false;
    }

    public static boolean handleCountiesResponse(CoolWeatherDb db, String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            String [] allCounties = response.split(",");
            if (allCounties != null && allCounties.length > 0) {
                for(String c : allCounties) {
                    String [] array = c.split("\\|");
                    Count count = new Count();
                    count.setCountCode(array[0]);
                    count.setCountName(array[1]);
                    count.setCityId(cityId);
                    db.saveCount(count);
                }
            }
            return true;
        }
        return false;
    }

    public static void handleWeatherResponse(Context context, String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
            String cityName = weatherInfo.getString("city");
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");
            saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, weatherDesp, publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void saveWeatherInfo(Context context, String cityName, String weatherCode, String
                                       temp1, String temp2, String weatherDesp, String publishTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();

        editor.putBoolean("city_selected", true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
        editor.putString("current_date", sdf.format(new Date()));
        editor.commit();
    }
}
