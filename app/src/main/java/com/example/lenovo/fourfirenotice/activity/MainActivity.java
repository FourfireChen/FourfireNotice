package com.example.lenovo.fourfirenotice.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.lenovo.fourfirenotice.db.City;
import com.example.lenovo.fourfirenotice.db.County;
import com.example.lenovo.fourfirenotice.db.Province;
import com.example.lenovo.fourfirenotice.gson.Weather;
import com.example.lenovo.fourfirenotice.net.HttpUtil;
import com.example.lenovo.fourfirenotice.net.Utility;
import com.example.lenovo.fourfirenotice.tools.ChooseAdapter;
import com.example.lenovo.fourfirenotice.tools.MyAdapter;
import com.example.lenovo.fourfirenotice.R;
import com.example.lenovo.fourfirenotice.tools.TimeComparetor;
import com.example.lenovo.fourfirenotice.db.Notice;
import com.example.lenovo.fourfirenotice.view.MoreListview;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    public static List<Notice> noticeList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ImageView imageView;
    private Button button;
    private TextView cityName;
    private TextView temperature;
    private TextView pm25;
    private TextView txt;
    private TextView updateTime;
    private TextView cityNameDrawer;
    private TextView temperatureDrawer;
    private TextView pm25Drawer;
    private TextView txtDrawer;
    private TextView updateTimeDrawer;
    private MyAdapter myAdapter;
    private android.support.v7.widget.Toolbar toolbar;
    private String lastWeatherId;
    private SharedPreferences prefs;
    private static List<Province> provinceList;
    private static List<City> cityList;
    private static List<County> countyList;
    private List<String> provinceNames = new ArrayList<>();
    private List<String> cityNames = new ArrayList<>();
    private List<String> countyNames = new ArrayList<>();
    private static Province selectProvince;
    private static City selectCity;
    private MoreListview provinceListView;
    private MoreListview cityListview;
    private MoreListview countyListview;
    private ChooseAdapter provinceAdapter;
    private ChooseAdapter cityAdapter;
    private ChooseAdapter coutyAdapter;
    private ProgressDialog progressDialog;
    public static final int LEVEL_PROVINCE = 1;
    public static final int LEVEL_CITY = 2;
    public static final int LEVEL_COUNTY = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Connector.getDatabase();
        iniRes();
        iniNotice();
        myAdapter = new MyAdapter(noticeList);
        recyclerView.setAdapter(myAdapter);
        button.setOnClickListener(this);
        iniWeather();
        iniQuery();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        iniNotice();
        Collections.sort(noticeList,new TimeComparetor());
        for(Notice notice:noticeList)
            notice.save();
        myAdapter.updataList(noticeList);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.addbtn:
                Notice notice = new Notice("");
                notice.save();
                noticeList.add(notice);
                myAdapter.updataList(noticeList);
                break;
        }

    }

    public void iniNotice()
    {
        noticeList = DataSupport.findAll(Notice.class);
        if(noticeList.size() == 0)
        {
            Notice notice = new Notice("哇帅哥来了");
            notice.save();
            noticeList.add(notice);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.fortime:
                Collections.sort(noticeList,new TimeComparetor());
                for(Notice notice:noticeList)
                    notice.save();
                myAdapter.updataList(noticeList);
            case R.id.deletemore:
                myAdapter.showDelete();
                break;
            case R.id.forlevel:
                Toast.makeText(this,"优先级的功能没做，感觉和时间排序一样",Toast.LENGTH_LONG).show();
                break;
            case R.id.weather:
                DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawyout);
                drawerLayout.openDrawer(Gravity.START);
//                startActivityForResult(weatherChoice,1);
                break;
        }
        return true;
    }

    public void iniRes()
    {
        toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.toolbarlayout);
        imageView = (ImageView)findViewById(R.id.image);
        setSupportActionBar(toolbar);
        button = (Button)findViewById(R.id.addbtn);
        temperature = (TextView)findViewById(R.id.temp);
        pm25 = (TextView)findViewById(R.id.pm25);
        updateTime = (TextView)findViewById(R.id.updatetime);
        cityName = (TextView)findViewById(R.id.cityname);
        txt = (TextView)findViewById(R.id.txt);
        temperatureDrawer = (TextView)findViewById(R.id.temp2);
        pm25Drawer = (TextView)findViewById(R.id.pm252);
        updateTimeDrawer = (TextView)findViewById(R.id.updatetime2);
        cityNameDrawer = (TextView)findViewById(R.id.cityname2);
        txtDrawer = (TextView)findViewById(R.id.txt2);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        collapsingToolbarLayout.setTitle("四火便签");
        Glide.with(this).load(R.drawable.background).into(imageView);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        lastWeatherId = prefs.getString("weatherid",null);
        provinceListView = (MoreListview)MainActivity.this.findViewById(R.id.prolistmain);
        cityListview = (MoreListview)MainActivity.this.findViewById(R.id.citylistmain);
        countyListview = (MoreListview)MainActivity.this.findViewById(R.id.countylistmain);
        cityAdapter = new ChooseAdapter(MainActivity.this,R.layout.city_item,cityNames);
        provinceAdapter = new ChooseAdapter(MainActivity.this,R.layout.city_item,provinceNames);
        coutyAdapter = new ChooseAdapter(MainActivity.this,R.layout.city_item,countyNames);
        provinceListView.setAdapter(provinceAdapter);
        cityListview.setAdapter(cityAdapter);
        countyListview.setAdapter(coutyAdapter);
    }

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

    public void showWeatherInfo(Weather weather)
    {
        String name = weather.basic.cityName;
        String time = "更新时间:" + weather.basic.update.updateTime.split(" ")[1];
        String temp = weather.now.temperature + "℃";
        String weatherTxt = weather.now.more.info;
        String pm252 = "pm2.5指数:" + weather.aqi.city.pm25;
        temperature.setText(temp);
        updateTime.setText(time);
        cityName.setText(name);
        txt.setText(weatherTxt);
        pm25.setText(pm252);
        temperatureDrawer.setText(temp);
        updateTimeDrawer.setText(time);
        cityNameDrawer.setText(name);
        txtDrawer.setText(weatherTxt);
        pm25Drawer.setText(pm252);
    }

    public void requestWeather(final String weatherId)
    {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" +
                weatherId + "&key=455105f8812c4e0c8548b9f52f9e0990";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback()
        {
            @Override
            public void onFailure(Call call, final IOException e)
            {

                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Toast.makeText(MainActivity.this,"没拿到信息,你网络有毛病",Toast.LENGTH_SHORT).show();
                        String weatherText = prefs.getString("weather",null);
                        if(weatherText != null)
                        {
                            Weather weather = Utility.handleWeatherResponse(weatherText);
                            showWeatherInfo(weather);
                        }
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if(weather != null && "ok".equals(weather.status))
                        {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                            editor.putString("weather",responseText);
                            editor.putString("weatherid",weather.basic.weatherId);
                            editor.commit();
                            showWeatherInfo(weather);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,"获取天气信息失败",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public void queryProvinces()
    {
        provinceList = DataSupport.findAll(Province.class);
        if(provinceList.size() > 0)
        {
            provinceNames.clear();
            for(Province province : provinceList)
            {
                provinceNames.add(province.getProvinceName());
            }
            provinceAdapter.notifyDataSetChanged();
        }
        else
        {
            String address = "http://guolin.tech/api/china";
            queryFromInternet(address,LEVEL_PROVINCE);
        }
    }

    public void queryCities()
    {
        cityList = DataSupport.where("provinceid = ?",String.valueOf(selectProvince.getId())).find(City.class);
        if(cityList.size() > 0)
        {
            cityNames.clear();
            for(City city : cityList)
            {
                cityNames.add(city.getCityName());
            }
            cityAdapter.notifyDataSetChanged();

        }
        else
        {
            int provinceCode = selectProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromInternet(address,LEVEL_CITY);
        }
    }

    public void queryCouty()
    {
        countyList = DataSupport.where("cityid = ?",String.valueOf(selectCity.getId())).find(County.class);
        if(countyList.size() > 0)
        {
            countyNames.clear();
            for(County county : countyList)
            {
                countyNames.add(county.getCountyName());
            }
            coutyAdapter.notifyDataSetChanged();
        }
        else
        {
            int provinceCode = selectProvince.getProvinceCode();
            int cityCode = selectCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromInternet(address,LEVEL_COUNTY);
        }
    }


    public void queryFromInternet(String address, final int level)
    {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        closeProgressDialog();
                        Toast.makeText(MainActivity.this,"没扒到",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                String responseText = response.body().string();
                boolean result = false;
                if(level == LEVEL_PROVINCE)
                {
                    result = Utility.handleProvinceResponse(responseText);
                }
                else if(level == LEVEL_CITY)
                {
                    result = Utility.handleCityResponse(responseText,selectProvince.getId());
                }
                else if(level == LEVEL_COUNTY)
                {
                    result = Utility.handleCountyResponse(responseText,selectCity.getId());
                }
                if(result)
                {
                    runOnUiThread(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            closeProgressDialog();
                            if(level == LEVEL_PROVINCE)
                            {
                                queryProvinces();
                            }
                            else if(level == LEVEL_CITY)
                            {
                                queryCities();
                            }
                            else if(level == LEVEL_COUNTY)
                            {
                                queryCouty();
                            }
                        }
                    });
                }
            }
        });
    }

    public void showProgressDialog()
    {
        if(progressDialog == null)
        {
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("正在努力扒取信息");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    public void closeProgressDialog()
    {
        if(progressDialog != null)
        {
            progressDialog.dismiss();
        }
    }

    public void iniQuery()
    {
        provinceListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                selectProvince = provinceList.get(position);
                queryCities();
            }
        });
        cityListview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                selectCity = cityList.get(position);
                queryCouty();
            }
        });
        countyListview.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                requestWeather(countyList.get(position).getWeather());
            }
        });
        queryProvinces();
    }
}
