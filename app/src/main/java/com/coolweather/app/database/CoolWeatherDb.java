package com.coolweather.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.coolweather.app.model.City;
import com.coolweather.app.model.Count;
import com.coolweather.app.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by duanbiwei on 2015/9/20.
 */
public class CoolWeatherDb {
    public static final String DB_NAME = "cool_weather";
    public static final int VERSION_CODE = 1;
    private static CoolWeatherDb coolWeatherDb;
    private SQLiteDatabase db;

    private CoolWeatherDb(Context context) {
        CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context, DB_NAME, null, VERSION_CODE);
        db = dbHelper.getWritableDatabase();
    }

    public synchronized static CoolWeatherDb getInstance(Context context) {
        if (coolWeatherDb == null) {
            coolWeatherDb = new CoolWeatherDb(context);
        }
        return coolWeatherDb;
    }

    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues values = new ContentValues();
            values.put("province_name", province.getProvinceName());
            values.put("province_code", province.getProvinceCode());
            db.insert("Province", null, values);
        }
    }

    public List<Province> loadProvince() {
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = db
                .query("Province", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                list.add(province);
            }while (cursor.moveToNext());
        }
        return list;
    }

    public void saveCity(City city) {
        if (city != null) {
            ContentValues values = new ContentValues();
            values.put("city_code", city.getCityCode());
            values.put("city_name", city.getCityName());
            values.put("province_id", city.getProvinceId());
            db.insert("City",null, values);
        }
    }

    public List<City> loadCities(int provinceId) {
        List<City> list = new ArrayList<City>();
        Cursor cursor = db.query("City", null, "province_id = ?",
                new String [] { String.valueOf(provinceId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setProvinceId(cursor.getInt(cursor.getColumnIndex("province_id")));
                list.add(city);
            }while (cursor.moveToNext());
        }
        return list;
    }

    public void saveCount(Count count) {
        if (count != null) {
            ContentValues values = new ContentValues();
            values.put("count_name", count.getCountName());
            values.put("count_code", count.getCountCode());
            values.put("city_id", count.getCityId());
            db.insert("Count", null, values);
        }
    }

    public List<Count> loadCounts(int cityId) {
        List<Count> list = new ArrayList<Count>();
        Cursor cursor = db.query("Count", null, "city_id = ?",
                new String [] { String.valueOf(cityId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                Count count = new Count();
                count.setId(cursor.getInt(cursor.getColumnIndex("id")));
                count.setCountName(cursor.getString(cursor.getColumnIndex("count_name")));
                count.setCountCode(cursor.getString(cursor.getColumnIndex("count_code")));
                count.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
                list.add(count);
            }while (cursor.moveToNext());
        }
        return list;
    }
}
