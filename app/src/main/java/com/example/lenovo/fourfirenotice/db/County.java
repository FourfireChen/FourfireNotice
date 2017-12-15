package com.example.lenovo.fourfirenotice.db;

import org.litepal.crud.DataSupport;

/**
 * Created by lenovo on 2017/12/7.
 */

public class County extends DataSupport
{
    private int id;
    private String countyName;
    private String weather;
    private int cityId;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getCountyName()
    {
        return countyName;
    }

    public void setCountyName(String countyName)
    {
        this.countyName = countyName;
    }

    public String getWeather()
    {
        return weather;
    }

    public void setWeather(String weather)
    {
        this.weather = weather;
    }

    public int getCityId()
    {
        return cityId;
    }

    public void setCityId(int cityId)
    {
        this.cityId = cityId;
    }
}
