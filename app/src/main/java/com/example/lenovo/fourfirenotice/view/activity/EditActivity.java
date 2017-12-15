package com.example.lenovo.fourfirenotice.view.activity;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.pdf.PdfRenderer;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.lenovo.fourfirenotice.presenter.InterPresenter;
import com.example.lenovo.fourfirenotice.presenter.Presenter;
import com.example.lenovo.fourfirenotice.sr.AlarmReceiver;
import com.example.lenovo.fourfirenotice.R;
import com.example.lenovo.fourfirenotice.model.db.Notice;
import org.litepal.tablemanager.Connector;
import java.util.List;

public class EditActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener
{
    private int years,months,days;
    private int hours,minutes;
    private TextView timepicker;
    private boolean isRemove;
    private EditText editText;
    private List<Notice> noticeList;
    private Notice thisNotice;
    private Button button;
    private Button buttonDate;
    private Button buttonTime;
    private Intent intent;
    private Intent alarmIntent;
    private int position;
    private Switch aSwitch;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private InterPresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Connector.getDatabase();
        presenter = MainActivity.presenter;
        iniRes();
        iniNotice();
        timepicker.setText(this.years + "." + this.months + "." + this.days + "-" + this.hours + ":" + minutes);
        editText.setText(thisNotice.getText());
        button.setOnClickListener(this);
        buttonDate.setOnClickListener(this);
        buttonTime.setOnClickListener(this);
        aSwitch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.delete:
                delete();
                break;
            case R.id.time:
                new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener()
                {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
                    {
                        hours = hourOfDay;
                        minutes = minute;
                        setTime(years,months,days,hours,minutes);
                        if(aSwitch.isChecked())
                        {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(thisNotice.getYear(),thisNotice.getMonth(),thisNotice.getDay(),hourOfDay,minute,0);
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
                        }
                    }
                },hours,minutes,true).show();
                break;
            case R.id.date:
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener()
                {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth)
                    {
                        years = year;
                        months = month + 1;
                        days = dayOfMonth;
                        setTime(years,months,days,hours,minutes);
                        if(aSwitch.isChecked())
                        {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year,month,dayOfMonth,thisNotice.getHour(),thisNotice.getMinute(),0);
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
                        }
                    }
                },years,months,days).show();
                break;
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if(isRemove != true)
        {
            thisNotice.setText(editText.getText().toString());
            thisNotice.save();
        }
    }

    public void iniRes()
    {
        noticeList = presenter.getNoticeList();
        timepicker = (TextView)findViewById(R.id.timechoice);
        buttonDate = (Button)findViewById(R.id.date);
        buttonTime = (Button)findViewById(R.id.time);
        button = (Button)findViewById(R.id.delete);
        aSwitch = (Switch)findViewById(R.id.switchbutton);
        editText = (EditText)findViewById(R.id.edit);
        intent = getIntent();
        position = intent.getIntExtra("position",0);
        thisNotice = noticeList.get(position);
        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmIntent = new Intent(this,AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(EditActivity.this,0,alarmIntent,0);
        if(thisNotice.getIsAlarm() == -1) aSwitch.setChecked(false);
        else    aSwitch.setChecked(true);
    }
    public void iniNotice()
    {
        years = thisNotice.getYear();
        months = thisNotice.getMonth();
        days = thisNotice.getDay();
        hours = thisNotice.getHour();
        minutes = thisNotice.getMinute();
    }
    public void setTime(int year,int month,int day,int hour,int minute)
    {
        timepicker.setText(this.years + "." + this.months + "." + this.days + "-" + this.hours + ":" + minutes);
        thisNotice.setYear(year);
        thisNotice.setMonth(month);
        thisNotice.setDay(day);
        thisNotice.setHour(hour);
        thisNotice.setMinute(minute);
        thisNotice.save();
    }
    public void delete()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确定删除?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                thisNotice.delete();
                noticeList.remove(position);
                isRemove = true;
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {}
        });
        builder.show();
    }
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
    {
        if(isChecked)
        {
            aSwitch.setChecked(true);
            thisNotice.setIsAlarm((short)1);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(thisNotice.getYear(),thisNotice.getMonth() - 1,thisNotice.getDay(),thisNotice.getHour(),thisNotice.getMinute(),0);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
            Toast.makeText(this,"设置成功",Toast.LENGTH_SHORT).show();
        }
        else
        {
            aSwitch.setChecked(false);
            thisNotice.setIsAlarm((short)-1);
            alarmManager.cancel(pendingIntent);
        }
    }
}
