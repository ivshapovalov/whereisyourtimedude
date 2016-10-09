package ru.brainworkout.whereisyourtimedude.common;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;

import ru.brainworkout.whereisyourtimedude.R;
import ru.brainworkout.whereisyourtimedude.activities.ActivityChrono;
import ru.brainworkout.whereisyourtimedude.activities.ActivityMain;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.PracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;

import static ru.brainworkout.whereisyourtimedude.common.Session.*;

public class BackgroundChronometer extends Thread {

    private volatile Long globalChronometerCountInSeconds = 0L;
    private volatile Long beginTimeinMillis = 0L;
    private volatile boolean ticking;
    private volatile PracticeHistory currentPracticeHistory;
    private volatile DatabaseManager DB;
    private Service service;

    public BackgroundChronometer() {


        this.setName("sessionBackgroundChronometer");

    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public long getGlobalChronometerCountInSeconds() {
        synchronized (globalChronometerCountInSeconds) {
            return globalChronometerCountInSeconds;
        }
    }

    public void setGlobalChronometerCountInSeconds(Long globalChronometerCountInSeconds) {
        synchronized (globalChronometerCountInSeconds) {
            this.globalChronometerCountInSeconds = globalChronometerCountInSeconds;
            this.beginTimeinMillis = System.currentTimeMillis() - globalChronometerCountInSeconds * 1_000;
        }
    }

    public void pauseTicking() {
        this.ticking = false;
    }

    public void resumeTicking() {

        this.ticking = true;

    }

    public DatabaseManager getDB() {
        return DB;
    }

    public void setDB(DatabaseManager DB) {
        this.DB = DB;
    }

    public PracticeHistory getCurrentPracticeHistory() {
        synchronized (currentPracticeHistory) {
            return currentPracticeHistory;
        }
    }

    public void setCurrentPracticeHistory(PracticeHistory currentPracticeHistory) {
        synchronized (currentPracticeHistory) {
            this.currentPracticeHistory = currentPracticeHistory;
        }
    }

    public boolean isTicking() {
        return ticking;
    }

    @Override
    public void run() {

        while (!isInterrupted()) {
            tick();
        }
    }

    private void tick() {

        while (!isInterrupted()) {

            if ((globalChronometerCountInSeconds) % Session.saveInterval == 0) {
                if (DB != null && currentPracticeHistory != null) {
                    checkAndChangeDateIfNeeded();
                }
            }
            while (ticking) {
                try {
                    this.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (globalChronometerCountInSeconds) {
                    globalChronometerCountInSeconds = (System.currentTimeMillis() - beginTimeinMillis) / 1_000;
                }
                // System.out.println(Common.ConvertMillisToStringTime(System.currentTimeMillis()) +": count - " +globalChronometerCountInSeconds);
                checkAndChangeDateIfNeeded();
                if (globalChronometerCountInSeconds % Session.saveInterval == 0) {
                    if (ticking) {
                        if (DB != null && currentPracticeHistory != null) {
                            synchronized (currentPracticeHistory) {
                                currentPracticeHistory.setDuration(globalChronometerCountInSeconds);
                                currentPracticeHistory.setLastTime(Calendar.getInstance().getTimeInMillis());
                                currentPracticeHistory.dbSave(DB);
                                // System.out.println(Common.ConvertMillisToStringTime(System.currentTimeMillis()) +": count - " +globalChronometerCountInSeconds+ " :save");
                            }
                            if (ticking) {
                                if (Session.sessionOptions != null && Session.sessionOptions.getDisplaySwitch() == 1) {
                                    updateNotification(Common.SYMBOL_PLAY);
                                } else {
                                    //service.stopForeground(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void updateNotification(String symbol) {

        if (Session.sessionOptions != null && Session.sessionOptions.getDisplaySwitch() == 1) {
            Notification notification = getCurrentNotification(symbol);
            NotificationManager mNotificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(Session.SESSION_NOTIFICATION_ID, notification);
        }
    }

    public Notification getCurrentNotification(String symbol) {

        Intent notificationIntent = new Intent(service, ActivityChrono.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(service, 0,
                notificationIntent, 0);
        int currentPracticeHistoryID = this.getCurrentPracticeHistory().getIdPractice();
        Practice practice = DB.getPractice(currentPracticeHistoryID);
        String practiceName = "WIYTD";
        if (practice != null) {
            practiceName = practice.getName().trim();
        }
        String currentDuration = Common.ConvertMillisToStringWithAllTime(this.getGlobalChronometerCountInSeconds() * 1000);
        Notification notification = new NotificationCompat.Builder(service)
                .setSmallIcon(R.drawable.sand_clock)
                .setContentTitle(practiceName)
                .setContentText(symbol + " " + currentDuration)
                .setContentIntent(pendingIntent).build();
        return notification;
    }


    private void checkAndChangeDateIfNeeded() {
        //current date

        Calendar today = Calendar.getInstance();
        today.clear(Calendar.HOUR);
        today.clear(Calendar.HOUR_OF_DAY);
        today.clear(Calendar.MINUTE);
        today.clear(Calendar.SECOND);
        today.clear(Calendar.MILLISECOND);
        long todayInMillis = today.getTimeInMillis();

        if (currentPracticeHistory.getDate() < todayInMillis) {
            synchronized (currentPracticeHistory) {
                currentPracticeHistory.setDuration(globalChronometerCountInSeconds);
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(currentPracticeHistory.getDate());
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 59);
                currentPracticeHistory.setLastTime(calendar.getTimeInMillis());
                currentPracticeHistory.dbSave(DB);
                //change practice history
                currentPracticeHistory = new PracticeHistory.Builder(DB)
                        .addDate(todayInMillis)
                        .addIdPractice(currentPracticeHistory.getIdPractice())
                        .addLastTime(todayInMillis)
                        .addDuration(0)
                        .build();
            }
            setGlobalChronometerCountInSeconds(0L);
        }
    }
}

