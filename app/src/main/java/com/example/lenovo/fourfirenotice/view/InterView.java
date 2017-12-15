package com.example.lenovo.fourfirenotice.view;

import com.example.lenovo.fourfirenotice.model.db.Notice;
import com.example.lenovo.fourfirenotice.model.gson.Weather;
import java.util.List;

/**
 * Created by lenovo on 2017/12/11.
 */

public interface InterView
{
    void ViewShowWeatherInfo(final Weather weather);//将Presenter返回的天气数据显示出来
    void ViewShowProgressDialog();//打开读取的进度条
    void ViewCloseProgressDialog();//关不读取进度条
    void failGetToast();//若扒取信息失败，提示
    MoreListview getProvinceListView();//为了在presenter中设置listview的子项监听器
    MoreListview getCityListview();
    MoreListview getCountyListview();
    void notifyCityChange();//为了方便在presenter中刷新listview
    void notifyCountyChange();
    void notifyNoticeChange();
    void notifyProvinceChange();
    void updataList(List<Notice> noticeList);//提供了强行刷新recycleview的方法
}
