package com.example.lenovo.fourfirenotice.model;

import com.example.lenovo.fourfirenotice.model.db.City;
import com.example.lenovo.fourfirenotice.model.db.County;
import com.example.lenovo.fourfirenotice.model.db.Province;
import com.example.lenovo.fourfirenotice.model.gson.Weather;

import java.util.List;

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
    boolean queryProvinces();
    boolean queryCities();
    boolean queryCouty();
    boolean queryFromInternet(String address, final int level);
    List<String> getProvinceNames();//方便在活动中设置adapter
    List<String> getCityNames();
    List<String> getCountyNames();
    List<County> getCountyList();
    void setSelectProvince(int position);
    void setSelectCity(int position);
}
