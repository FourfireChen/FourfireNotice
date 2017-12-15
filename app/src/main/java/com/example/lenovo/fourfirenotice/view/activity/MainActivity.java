package com.example.lenovo.fourfirenotice.view.activity;

import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.lenovo.fourfirenotice.model.gson.Weather;
import com.example.lenovo.fourfirenotice.presenter.InterPresenter;
import com.example.lenovo.fourfirenotice.presenter.Presenter;
import com.example.lenovo.fourfirenotice.view.InterView;
import com.example.lenovo.fourfirenotice.view.tools.ChooseAdapter;
import com.example.lenovo.fourfirenotice.view.tools.MyAdapter;
import com.example.lenovo.fourfirenotice.R;
import com.example.lenovo.fourfirenotice.model.db.Notice;
import com.example.lenovo.fourfirenotice.view.MoreListview;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,InterView
{
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
    private MoreListview provinceListView;
    private MoreListview cityListview;
    private MoreListview countyListview;
    private ChooseAdapter provinceAdapter;
    private ChooseAdapter cityAdapter;
    private ChooseAdapter coutyAdapter;
    private ProgressDialog progressDialog;
    public static InterPresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = Presenter.getPresenter(this);
        presenter.iniNotice();
        iniRes();
        button.setOnClickListener(this);
        presenter.iniQuery();
        presenter.iniWeather();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        presenter.sort();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.addbtn:
                presenter.add();
                break;
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
            case R.id.deletemore:
                myAdapter.showDelete();
                break;
            case R.id.forlevel:
                Toast.makeText(this,"优先级的功能没做，感觉和时间排序一样",Toast.LENGTH_LONG).show();
                break;
            case R.id.weather:
                DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawyout);
                drawerLayout.openDrawer(Gravity.START);
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
        provinceListView = (MoreListview)MainActivity.this.findViewById(R.id.prolistmain);
        cityListview = (MoreListview)MainActivity.this.findViewById(R.id.citylistmain);
        countyListview = (MoreListview)MainActivity.this.findViewById(R.id.countylistmain);
        cityAdapter = new ChooseAdapter(MainActivity.this,R.layout.city_item,presenter.getCityNames());
        provinceAdapter = new ChooseAdapter(MainActivity.this,R.layout.city_item,presenter.getProvinceNames());
        coutyAdapter = new ChooseAdapter(MainActivity.this,R.layout.city_item,presenter.getCountyNames());
        provinceListView.setAdapter(provinceAdapter);
        cityListview.setAdapter(cityAdapter);
        countyListview.setAdapter(coutyAdapter);
        myAdapter = new MyAdapter(presenter.getNoticeList());
        recyclerView.setAdapter(myAdapter);
    }

    //重写的view层的方法
    //以下都是重写的view层方法

    public void ViewShowWeatherInfo(final Weather weather)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
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
        });
    }

    public void ViewShowProgressDialog()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if(progressDialog == null)
                {
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setMessage("正在努力扒取信息");
                    progressDialog.setCanceledOnTouchOutside(false);
                }
                progressDialog.show();
            }
        });
    }

    public void ViewCloseProgressDialog()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                if(progressDialog != null)
                {
                    progressDialog.dismiss();
                }
            }
        });
    }

    @Override
    public void failGetToast()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Toast.makeText(MainActivity.this,"没扒到",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public MoreListview getProvinceListView()
    {
        return provinceListView;
    }

    public MoreListview getCityListview()
    {
        return cityListview;
    }

    public MoreListview getCountyListview()
    {
        return countyListview;
    }

    @Override
    public void notifyCityChange()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                cityAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void notifyCountyChange()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                coutyAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void notifyNoticeChange()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                myAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void notifyProvinceChange()
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                provinceAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void updataList(final List<Notice> noticeList)
    {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                myAdapter.updataList(noticeList);
            }
        });
    }
}
