package com.example.lenovo.fourfirenotice.model.db;

import org.litepal.crud.DataSupport;


/**
 * Created by lenovo on 2017/12/1.
 */

public class Notice extends DataSupport
{
    private short isAlarm;
    private String text;
    private int year,month,day;
    private int hour,minute;
    private int level;

    public Notice(String text)
    {
        this.text = text;
        level = 3;
        year = 2015;
        month = 7;
        day = 29;
        hour = 10;
        minute = 0;
        isAlarm = -1;
    }

    public void setTime(int year, int month, int day, int hour, int minute)
    {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    public void setMonth(int month)
    {
        this.month = month;
    }

    public void setDay(int day)
    {
        this.day = day;
    }

    public void setHour(int hour)
    {
        this.hour = hour;
    }

    public void setMinute(int minute)
    {
        this.minute = minute;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public int getYear()
    {
        return year;
    }

    public int getMonth()
    {
        return month;
    }

    public int getDay()
    {
        return day;
    }

    public int getHour()
    {
        return hour;
    }

    public int getMinute()
    {
        return minute;
    }

    public int getLevel()
    {
        return level;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public short getIsAlarm()
    {
        return isAlarm;
    }

    public void setIsAlarm(short isAlarm)
    {
        this.isAlarm = isAlarm;
    }
}
