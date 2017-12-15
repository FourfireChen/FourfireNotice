package com.example.lenovo.fourfirenotice.presenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import com.example.lenovo.fourfirenotice.model.InterModel;
import com.example.lenovo.fourfirenotice.model.Model;
import com.example.lenovo.fourfirenotice.model.TimeComparetor;
import com.example.lenovo.fourfirenotice.model.db.City;
import com.example.lenovo.fourfirenotice.model.db.County;
import com.example.lenovo.fourfirenotice.model.db.Notice;
import com.example.lenovo.fourfirenotice.model.db.Province;
import com.example.lenovo.fourfirenotice.model.gson.Weather;
import com.example.lenovo.fourfirenotice.view.InterView;
import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Created by lenovo on 2017/12/11.
 */

public class Presenter implements InterPresenter
{
    private InterModel MyModel;
    private InterView MyView;
    private SharedPreferences prefs;
    private String lastWeatherId;
    private List<Notice> noticesList = new ArrayList<>();
    private static InterPresenter presenter;

    private Presenter(InterView myView)//私有化构造器，实现单实例
    {
        MyView = myView;
        MyModel = new Model();
        prefs = PreferenceManager.getDefaultSharedPreferences((Context)myView);
        lastWeatherId = prefs.getString("weatherid",null);
    }
    public static InterPresenter getPresenter(InterView myView)//利用缓存的方法，实现单实例
    {
        if(presenter == null)
        {
            return new Presenter(myView);
        }
        else
        {
            return presenter;
        }

    }

    //为view层的listview设置监听器，并初始化省份的显示
    public void iniQuery()
    {
        MyView.getProvinceListView().setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                MyModel.setSelectProvince(position);
                MyModel.queryCities();
            }
        });
        MyView.getCityListview().setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                MyModel.setSelectCity(position);
                MyModel.queryCouty();
            }
        });
        MyView.getCountyListview().setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                requestWeather(MyModel.getCountyList().get(position).getWeather());
            }
        });
        MyModel.queryProvinces();
    }
    /**
     * 以下是对天气信息的处理：
     * 包括：
     * 根据上一次保存的天气ID初始化天气显示
     * 从网络扒取天气信息
     * 发送请求至Interview，更新天气信息
     */
    //发送天气搜索请求到InterModel，从网络扒取信息并根据信息扒取成功与否发送请求到Interview进行交互
    public void requestWeather(final String weatherId)
    {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" +
                weatherId + "&key=455105f8812c4e0c8548b9f52f9e0990";
        MyModel.sendOkHttpRequest(weatherUrl, new Callback()
        {
            @Override
            public void onFailure(Call call, final IOException e)
            {

                MyView.failGetToast();
                String weatherText = prefs.getString("weather",null);
                if(weatherText != null)
                {
                    Weather weather = MyModel.handleWeatherResponse(weatherText);
                    MyView.ViewShowWeatherInfo(weather);
                }
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responseText = response.body().string();
                final Weather weather = MyModel.handleWeatherResponse(responseText);
                if(weather != null && "ok".equals(weather.status))
                {
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences((Context)MyView).edit();
                    editor.putString("weather",responseText);
                    editor.putString("weatherid",weather.basic.weatherId);
                    editor.commit();
                    MyView.ViewShowWeatherInfo(weather);
                }
                else
                {
                    MyView.failGetToast();
                }
            }
        });
    }
    //根据上一次选择的城市，初始化天气信息
    public void iniWeather()
    {
        if(lastWeatherId != null)
        {
            requestWeather(lastWeatherId);
        }
        else
        {
            requestWeather("CN101200101");
        }
    }

    /**
     * 以下是对便签的操作：
     * 包括：
     * 从数据库读取，并初始化便签的数据
     * 将便签按照用户设置的时间进行排序
     * 添加便签
     */
    //从数据库读取并初始化便签的数据组
    public void iniNotice()
    {
        Connector.getDatabase();
        noticesList = DataSupport.findAll(Notice.class);
        if(noticesList.size() == 0)
        {
            Notice notice = new Notice("哇帅哥来了");
            notice.save();
            noticesList.add(notice);
        }
    }
    //根据用户设置的时间对便签进行排序，并发送请求到Interview通知显示便签
    public void sort()
    {
        Collections.sort(noticesList,new TimeComparetor());
        for(Notice notice:noticesList)
            notice.save();
        MyView.updataList(noticesList);
    }
    //添加便签的数量，并通知View显示刷新
    public void add()
    {
        Notice notice = new Notice("");
        notice.save();
        noticesList.add(notice);
        MyView.notifyNoticeChange();
    }

    /**
     * 以下方是为了方便View层对于少量数据的调用
     * 详情请见Interpresenter
     */
    @Override
    public List<Notice> getNoticeList()
    {
        return noticesList;
    }
    @Override
    public List<String> getProvinceNames()
    {
        return MyModel.getProvinceNames();
    }
    @Override
    public List<String> getCityNames()
    {
        return MyModel.getCityNames();
    }
    @Override
    public List<String> getCountyNames()
    {
        return MyModel.getCountyNames();
    }



    @Override
    public void showProgressDia()
    {
        MyView.ViewShowProgressDialog();
    }

    @Override
    public void closeProgressDia()
    {
        MyView.ViewCloseProgressDialog();
    }

    @Override
    public void showFailToast()
    {
        MyView.failGetToast();
    }
}
