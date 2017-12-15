package com.example.lenovo.fourfirenotice.model.db;

import org.litepal.crud.DataSupport;

/**
 * Created by lenovo on 2017/12/7.
 */

public class City extends DataSupport
{
    private int id;
    private String cityName;
    private int cityCode;
    private int provinceID;

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getCityName()
    {
        return cityName;
    }

    public void setCityName(String cityName)
    {
        this.cityName = cityName;
    }

    public int getCityCode()
    {
        return cityCode;
    }

    public void setCityCode(int cityCode)
    {
        this.cityCode = cityCode;
    }

    public int getProvinceID()
    {
        return provinceID;
    }

    public void setProvinceID(int provinceID)
    {
        this.provinceID = provinceID;
    }
}
