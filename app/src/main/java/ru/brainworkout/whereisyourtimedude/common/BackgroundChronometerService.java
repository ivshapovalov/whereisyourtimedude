package ru.brainworkout.whereisyourtimedude.common;


import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import org.apache.log4j.Logger;
import static  ru.brainworkout.whereisyourtimedude.common.Session.*;

public class BackgroundChronometerService extends Service {

    private static Logger LOG = Alogger.getLogger(BackgroundChronometerService.class);

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        LOG.debug("Background service begin create before super ");
        String message=Common.convertStackTraceToString( Thread.currentThread().getStackTrace());
        LOG.debug(message);
        super.onCreate();
        LOG.debug("Background service begin create after super ");
        message=Common.convertStackTraceToString( Thread.currentThread().getStackTrace());
        LOG.debug(message);
        LOG.debug("Set service of backgroundChronometer ");
        sessionBackgroundChronometer.setService(this);
        LOG.debug("Get notification of "+sessionBackgroundChronometer.getName());
        Notification notification=sessionBackgroundChronometer.getCurrentNotification(Common.SYMBOL_STOP);
        LOG.debug("Start service foreground ");
        startForeground(SESSION_NOTIFICATION_ID, notification);
//        LOG.debug("Stop service foreground ");
//        stopForeground(true);
        LOG.debug("Background service started successful ");
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        if (intent.getAction().equals("PLAY")) {
//            if (sessionBackgroundChronometer != null) {
//                sessionBackgroundChronometer.resumeTicking();
//            }
//        } else if (intent.getAction().equals("STOP")) {
//            if (sessionBackgroundChronometer != null) {
//                sessionBackgroundChronometer.pauseTicking();
//            }
//        }
//        return -1;
//    }


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
