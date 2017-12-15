package com.example.lenovo.fourfirenotice.net;

import android.text.TextUtils;
import android.view.TextureView;

import com.example.lenovo.fourfirenotice.db.City;
import com.example.lenovo.fourfirenotice.db.County;
import com.example.lenovo.fourfirenotice.db.Province;
import com.example.lenovo.fourfirenotice.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by lenovo on 2017/12/7.
 */

public class Utility
{
    public static boolean handleProvinceResponse(String response)
    {
        if(!TextUtils.isEmpty(response))
        {
            try
            {
                JSONArray allProvinces = new JSONArray(response);
                for(int i = 0;i < allProvinces.length();i++)
                {
                    JSONObject provinceObj = allProvinces.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(provinceObj.getString("name"));
                    province.setProvinceCode(provinceObj.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
    return false;
    }

    public static boolean handleCityResponse(String response,int provinceID)
    {
        if(!TextUtils.isEmpty(response))
        {
            try
            {
                JSONArray allCities = new JSONArray(response);
                for(int i = 0;i < allCities.length();i++)
                {
                    JSONObject cityObj = allCities.getJSONObject(i);
                    City city = new City();
                    city.setCityName(cityObj.getString("name"));
                    city.setCityCode(cityObj.getInt("id"));
                    city.setProvinceID(provinceID);
                    city.save();
                }
                return true;
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean handleCountyResponse(String response,int cityId)
    {
        if(!TextUtils.isEmpty(response))
        {
            try
            {
                JSONArray allCounties = new JSONArray(response);
                for(int i = 0;i < allCounties.length();i++)
                {
                    JSONObject countyObj = allCounties.getJSONObject(i);
                    County county = new County();
                    county.setCountyName(countyObj.getString("name"));
                    county.setCityId(cityId);
                    county.setWeather(countyObj.getString("weather_id"));
                    county.save();
                }
                return true;
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static Weather handleWeatherResponse(String response)
    {
        try
        {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherContent,Weather.class);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
