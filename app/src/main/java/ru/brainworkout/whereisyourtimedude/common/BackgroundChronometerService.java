package ru.brainworkout.whereisyourtimedude.common;


import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import ru.brainworkout.whereisyourtimedude.database.manager.SQLiteDatabaseManager;

import static ru.brainworkout.whereisyourtimedude.common.Session.*;

public class BackgroundChronometerService extends Service {

    private volatile SQLiteDatabaseManager DB;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        super.onCreate();
        sessionBackgroundChronometer.setService(this);
        DB = sessionBackgroundChronometer.getDB();
        Notification notification = sessionBackgroundChronometer.getCurrentNotification(Constants.ACTION.PAUSE_ACTION);
        startForeground(SESSION_NOTIFICATION_ID, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null) {
            if (intent.getAction().equals(Constants.ACTION.SHOW_NOTIFICATION_TIMER)) {
                sessionBackgroundChronometer.setService(this);
                if (Session.sessionBackgroundChronometer != null && Session.sessionBackgroundChronometer.getService() != null) {
                    Session.sessionOptions.setDisplayNotificationTimerSwitch(1);
                    Session.sessionOptions.dbSave(DB);
                    Notification notification = sessionBackgroundChronometer.getCurrentNotification(Constants.ACTION.SHOW_NOTIFICATION_TIMER);
                    startForeground(SESSION_NOTIFICATION_ID, notification);
                }
            } else if (intent.getAction().equals(Constants.ACTION.SHOW_NOTIFICATION_INFO)) {
                if (Session.sessionBackgroundChronometer != null && Session.sessionBackgroundChronometer.getService() != null) {
                    Session.sessionOptions.setDisplayNotificationTimerSwitch(0);
                    Session.sessionOptions.dbSave(DB);
                    Session.sessionBackgroundChronometer.freezeNotification();
                }
            } else if (intent.getAction().equals(Constants.ACTION.PLAY_ACTION)) {
                if (sessionBackgroundChronometer != null) {
                    sessionBackgroundChronometer.setGlobalChronometerCount(sessionBackgroundChronometer.getGlobalChronometerCount());
                    sessionBackgroundChronometer.resumeTicking();
                }
            } else if (intent.getAction().equals(Constants.ACTION.PAUSE_ACTION)) {
                if (sessionBackgroundChronometer != null) {
                    sessionBackgroundChronometer.updateNotification(Constants.ACTION.PAUSE_ACTION);
                    sessionBackgroundChronometer.pauseTicking();
                }
            }
        }
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

}
