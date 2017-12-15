package com.example.lenovo.fourfirenotice.sr;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.lenovo.fourfirenotice.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by lenovo on 2017/12/6.
 */

public class AlarmReceiver extends BroadcastReceiver
{
    //根据用户设置的闹钟时间，发送通知到用户
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Notification notification = new Notification.Builder(context)
                .setAutoCancel(true)
                .setTicker("时间到了")
                .setContentTitle("设置的提醒响了")
                .setContentText("时间到了")
                .setDefaults(Notification.DEFAULT_SOUND)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_bookmark_black_48dp)
                .build();
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1,notification);
    }
}
