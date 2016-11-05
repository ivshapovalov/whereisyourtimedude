package ru.brainworkout.whereisyourtimedude.common;


import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.apache.log4j.Logger;

import ru.brainworkout.whereisyourtimedude.database.manager.SQLiteDatabaseManager;

import static ru.brainworkout.whereisyourtimedude.common.Session.*;

public class BackgroundChronometerService extends Service {

    private static Logger LOG = Alogger.getLogger(BackgroundChronometerService.class);
    private volatile SQLiteDatabaseManager DB;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        LOG.debug("Background service begin create before super ");
        String message = Common.convertStackTraceToString(Thread.currentThread().getStackTrace());
        LOG.debug(message);
        super.onCreate();
        LOG.debug("Background service begin create after super ");
        message = Common.convertStackTraceToString(Thread.currentThread().getStackTrace());
        LOG.debug(message);
        LOG.debug("Set service of backgroundChronometer ");
        sessionBackgroundChronometer.setService(this);
        DB = sessionBackgroundChronometer.getDB();
        LOG.debug("Get notification of " + sessionBackgroundChronometer.getName());
        Notification notification = sessionBackgroundChronometer.getCurrentNotification(Constants.ACTION.PAUSE_ACTION);
        LOG.debug("Start service foreground ");
        startForeground(SESSION_NOTIFICATION_ID, notification);
        LOG.debug("Background service started successful ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() != null) {
            LOG.debug("Before start command "+intent.getAction() );
            if (intent.getAction().equals(Constants.ACTION.SHOW_NOTIFICATION_TIMER)) {
                LOG.debug("Before start command "+intent.getAction() );
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
            LOG.debug("After start command "+intent.getAction() );

        }
        return START_STICKY;
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
