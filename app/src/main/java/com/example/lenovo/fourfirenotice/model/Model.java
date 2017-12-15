package com.example.lenovo.fourfirenotice.model;

import android.text.TextUtils;
import com.example.lenovo.fourfirenotice.model.db.City;
import com.example.lenovo.fourfirenotice.model.db.County;
import com.example.lenovo.fourfirenotice.model.db.Province;
import com.example.lenovo.fourfirenotice.model.gson.Weather;
import com.example.lenovo.fourfirenotice.presenter.InterPresenter;
import com.example.lenovo.fourfirenotice.view.activity.MainActivity;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by lenovo on 2017/12/11.
 */

public class Model implements InterModel
{
    boolean isResultForInternet;
    private Province selectProvince;
    private City selectCity;
    public final int LEVEL_PROVINCE = 1;
    public final int LEVEL_CITY = 2;
    public final int LEVEL_COUNTY = 3;
    private static List<Province> provinceList;
    private static List<City> cityList;
    private static List<County> countyList;
    private List<String> provinceNames = new ArrayList<>();
    private List<String> cityNames = new ArrayList<>();
    private List<String> countyNames = new ArrayList<>();
    /**
     * 以下是对网络操作的方法
     * 详情见InterModel
     */
    @Override
    public boolean handleProvinceResponse(String response)
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

    @Override
    public boolean handleCityResponse(String response, int provinceID)
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

    @Override
    public boolean handleCountyResponse(String response, int cityId)
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

    @Override
    public Weather handleWeatherResponse(String response)
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

    @Override
    public void sendOkHttpRequest(String address, Callback callback)
    {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * 以下一部分是对地区的处理
     * 包括：
     * 从数据库中读取省份信息
     * 若不存在，则从网络上读取省份信息
     * 为省份，城市，街区三个listview设置监听器
     * 从网络扒取相应地区信息并显示
     */
    //查询省份信息，并请求InterView显示在Listview上
    public boolean queryProvinces()
    {
        provinceList = DataSupport.findAll(Province.class);
        if(provinceList.size() > 0)
        {
            provinceNames.clear();
            for(Province province : provinceList)
            {
                provinceNames.add(province.getProvinceName());
            }
            return true;
        }
        else
        {
            String address = "http://guolin.tech/api/china";
            boolean isResult = queryFromInternet(address,LEVEL_PROVINCE);
            return isResult;
        }
    }
    //同上
    public boolean queryCities()
    {
        cityList = DataSupport.where("provinceid = ?",String.valueOf(selectProvince.getId())).find(City.class);
        if(cityList.size() > 0)
        {
            cityNames.clear();
            for(City city : cityList)
            {
                cityNames.add(city.getCityName());
            }
            return true;
        }
        else
        {
            int provinceCode = selectProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            boolean isResult = queryFromInternet(address,LEVEL_CITY);
            return isResult;
        }
    }
    //同上
    public boolean queryCouty()
    {
        countyList = DataSupport.where("cityid = ?",String.valueOf(selectCity.getId())).find(County.class);
        if(countyList.size() > 0)
        {
            countyNames.clear();
            for(County county : countyList)
            {
                countyNames.add(county.getCountyName());
            }
            return true;
        }
        else
        {
            int provinceCode = selectProvince.getProvinceCode();
            int cityCode = selectCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            boolean isResult = queryFromInternet(address,LEVEL_COUNTY);
            return isResult;
        }
    }
    //发送地区搜索请求到InterModel,从网络扒取信息并根据返回结果发送请求到Interview实现交互
    public boolean queryFromInternet(String address, final int level)
    {
        final InterPresenter presenter = MainActivity.presenter;
        presenter.showProgressDia();
        sendOkHttpRequest(address, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                presenter.closeProgressDia();
                presenter.showFailToast();
                isResultForInternet = false;
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                String responseText = response.body().string();
                boolean result = false;
                if(level == LEVEL_PROVINCE)
                {
                    result = handleProvinceResponse(responseText);
                }
                else if(level == LEVEL_CITY)
                {
                    result = handleCityResponse(responseText,selectProvince.getId());
                }
                else if(level == LEVEL_COUNTY)
                {
                    result = handleCountyResponse(responseText,selectCity.getId());
                }
                if(result)
                {
                    presenter.closeProgressDia();
                    if(level == LEVEL_PROVINCE)
                    {
                        queryProvinces();
                        isResultForInternet = true;
                    }
                    else if(level == LEVEL_CITY)
                    {
                        queryCities();
                        isResultForInternet = true;
                    }
                    else if(level == LEVEL_COUNTY)
                    {
                        queryCouty();
                        isResultForInternet = true;
                    }
                }
                else    isResultForInternet = false;
            }
        });
        return isResultForInternet;
    }

    @Override
    public List<String> getProvinceNames()
    {
        return provinceNames;
    }

    @Override
    public List<String> getCityNames()
    {
        return cityNames;
    }

    @Override
    public List<String> getCountyNames()
    {
        return countyNames;
    }

    @Override
    public List<County> getCountyList()
    {
        return countyList;
    }

    public void setSelectProvince(int position)
    {
        this.selectProvince = provinceList.get(position);
    }

    public void setSelectCity(int position)
    {
        this.selectCity = cityList.get(position);
    }
}
