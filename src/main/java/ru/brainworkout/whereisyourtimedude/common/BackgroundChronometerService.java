package ru.brainworkout.whereisyourtimedude.common;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Calendar;

import ru.brainworkout.whereisyourtimedude.R;
import ru.brainworkout.whereisyourtimedude.activities.ActivityChrono;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.PracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;

public class BackgroundChronometerService extends Service {

    private final DatabaseManager DB= new DatabaseManager(this);
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        if (Session.sessionBackgroundChronometer == null) {
            Session.sessionBackgroundChronometer=new BackgroundChronometer();
        }
        Session.sessionBackgroundChronometer.setService(this);
        Notification notification=Session.sessionBackgroundChronometer.getCurrentNotification(Common.SYMBOL_STOP);
        startForeground(Session.SESSION_NOTIFICATION_ID, notification);
        stopForeground(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

}

