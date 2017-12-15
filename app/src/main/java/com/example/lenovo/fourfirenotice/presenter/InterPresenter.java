package com.example.lenovo.fourfirenotice.presenter;

import com.example.lenovo.fourfirenotice.model.db.City;
import com.example.lenovo.fourfirenotice.model.db.County;
import com.example.lenovo.fourfirenotice.model.db.Notice;
import com.example.lenovo.fourfirenotice.model.db.Province;
import com.example.lenovo.fourfirenotice.view.InterView;

import java.util.List;

/**
 * Created by lenovo on 2017/12/13.
 */

public interface InterPresenter
{
    void iniQuery();//初始化查找选择城市功能
    void iniWeather();//初始化天气数据处理，并设置好之后选择城市后的天气数据响应
    void sort();//将Notice按时间进行排序
    void add();//添加Notice
    List<Notice> getNoticeList();//方便在活动中设置adapter
    void iniNotice();//初始化活动的notice
    List<String> getProvinceNames();//方便在活动中设置adapter
    List<String> getCityNames();
    List<String> getCountyNames();
    void showProgressDia();
    void closeProgressDia();
    void showFailToast();
}
