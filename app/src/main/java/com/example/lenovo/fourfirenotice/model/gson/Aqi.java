package com.example.lenovo.fourfirenotice.model.gson;

/**
 * Created by lenovo on 2017/12/7.
 */

public class Aqi
{
    public AQICity city;
    public class AQICity
    {
        public String aqi;
        public String pm25;
    }
}
