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

import org.apache.log4j.Logger;

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
    private static Logger LOG = ALogger.getLogger(BackgroundChronometerService.class);

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        LOG.debug("Background service begin create ");
        super.onCreate();
        if (Session.sessionBackgroundChronometer == null) {
            LOG.debug("Background service create new chronometer");
            Session.sessionBackgroundChronometer=new BackgroundChronometer(this);
        }
        LOG.debug("Set service of backgroundChronometer ");
        Session.sessionBackgroundChronometer.setService(this);
        LOG.debug("Get notification of "+Session.sessionBackgroundChronometer.getName());
        Notification notification=Session.sessionBackgroundChronometer.getCurrentNotification(Common.SYMBOL_STOP);
        LOG.debug("Start service foreground ");
        startForeground(Session.SESSION_NOTIFICATION_ID, notification);
        LOG.debug("Stop service foreground ");
        stopForeground(true);
        LOG.debug("Background service started successful ");

    }

    @Override
    public void onDestroy() {
        LOG.error("Background service stopped ");
        super.onDestroy();

    }

    @Override
    public void onLowMemory() {
        LOG.error("Background service low memory ");
        super.onLowMemory();
    }

    @Override
    public void onRebind(Intent intent) {
        LOG.error("onRebind");
        super.onRebind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        LOG.error("onTaskRemoved");

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LOG.error("onUnbind");

        return super.onUnbind(intent);
    }
}

