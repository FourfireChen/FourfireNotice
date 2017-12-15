package com.example.lenovo.fourfirenotice.model;

import com.example.lenovo.fourfirenotice.model.db.Notice;

import java.util.Comparator;

/**
 * Created by lenovo on 2017/12/2.
 */

public class TimeComparetor implements Comparator
{
    @Override
    public int compare(Object o1, Object o2)
    {
        Notice notice1 = (Notice)o1;
        Notice notice2 = (Notice)o2;
        if(notice1.getYear() > notice2.getYear())
            return 1;
        else  if(notice1.getYear() < notice2.getYear())
            return -1;
        else if (notice1.getMonth() > notice2.getMonth())
            return 1;
        else if (notice1.getMonth() < notice2.getMonth())
            return -1;
        else if (notice1.getDay() > notice2.getDay())
            return 1;
        else if (notice1.getDay() < notice2.getDay())
            return -1;
        else if (notice1.getHour() > notice2.getHour())
            return 1;
        else if (notice1.getHour() < notice2.getHour())
            return -1;
        else if (notice1.getMinute() > notice2.getMinute())
            return 1;
        else if (notice1.getMinute() < notice2.getMinute())
            return -1;
        else
            return 0;
    }

    @Override
    public boolean equals(Object obj)
    {
        return true;
    }
}
