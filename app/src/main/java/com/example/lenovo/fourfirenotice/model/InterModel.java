package com.example.lenovo.fourfirenotice.model;

import com.example.lenovo.fourfirenotice.model.gson.Weather;

/**
 * Created by lenovo on 2017/12/11.
 */

public interface InterModel
{
    boolean handleProvinceResponse(String response);//处理从网络返回的省份信息，并写入数据库
    boolean handleCityResponse(String response,int provinceID);//处理从网络返回的城市信息，并写入数据库
    boolean handleCountyResponse(String response,int cityId);//处理从网络返回的街区信息，并写入数据库
    Weather handleWeatherResponse(String response);//处理从网络扒取的天气信息，并封装在weather对象中返回
    void sendOkHttpRequest(String address,okhttp3.Callback callback);//发送网络请求
}
