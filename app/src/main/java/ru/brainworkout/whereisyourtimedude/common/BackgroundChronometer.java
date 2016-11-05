package ru.brainworkout.whereisyourtimedude.common;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import org.apache.log4j.Logger;

import java.util.Calendar;

import ru.brainworkout.whereisyourtimedude.R;
import ru.brainworkout.whereisyourtimedude.activities.ActivityChrono;
import ru.brainworkout.whereisyourtimedude.database.entities.Area;
import ru.brainworkout.whereisyourtimedude.database.entities.DetailedPracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.PracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.entities.Project;
import ru.brainworkout.whereisyourtimedude.database.manager.SQLiteDatabaseManager;

import static ru.brainworkout.whereisyourtimedude.common.Session.*;


public class BackgroundChronometer extends Thread {

    private static Logger LOG = Alogger.getLogger(BackgroundChronometer.class);
    private volatile Long globalChronometerCountInSeconds = 0L;
    private volatile Long beginTimeinMillis = 0L;
    private volatile boolean ticking;
    private volatile PracticeHistory currentPracticeHistory;
    private volatile DetailedPracticeHistory currentDetailedPracticeHistory;
    private volatile SQLiteDatabaseManager DB;
    private volatile Service service;
    private volatile boolean notificationStatusIsPlay;

    public BackgroundChronometer() {
        LOG.debug("Before new thread created");
        String message = Common.convertStackTraceToString(Thread.currentThread().getStackTrace());
        LOG.debug(message);
        this.setName("ThreadChrono-" + Calendar.getInstance().getTimeInMillis());
        LOG.debug(this.getName() + " created");
    }

    public BackgroundChronometer(Service service) {

        this.service = service;
    }

    @Override
    protected void finalize() throws Throwable {
        LOG.debug(this.getName() + " finalizes ");
        super.finalize();
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
        //LOG.debug(this.getName() + " set service + " + service.toString());
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
        if (currentDetailedPracticeHistory != null) {
            synchronized (currentDetailedPracticeHistory) {
                currentDetailedPracticeHistory.dbSave(DB);
            }
        }
        this.ticking = false;
        setAndSaveChronometerState(false);
        LOG.debug(this.getName() + " paused");
    }

    public void resumeTicking() {

        createNewDetailedPracticeHistory();
        this.ticking = true;
        setAndSaveChronometerState(true);
        LOG.debug(this.getName() + " resumed");

    }

    private void createNewDetailedPracticeHistory() {
        if (currentPracticeHistory != null) {
            synchronized (this) {
                currentDetailedPracticeHistory = new DetailedPracticeHistory.Builder(DB.getDetailedPracticeHistoryMaxNumber() + 1)
                        .addIdPractice(currentPracticeHistory.getIdPractice())
                        .addDate(currentPracticeHistory.getDate())
                        .addTime(Calendar.getInstance().getTimeInMillis())
                        .build();
            }
        }
    }

    private void setAndSaveChronometerState(boolean state) {
        Session.sessionOptions.setChronoIsWorking(state ? 1 : 0);
        Session.sessionOptions.dbSave(DB);
    }

    public SQLiteDatabaseManager getDB() {
        return DB;
    }

    public void setDB(SQLiteDatabaseManager DB) {
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

    public DetailedPracticeHistory getCurrentDetailedPracticeHistory() {
        synchronized (currentDetailedPracticeHistory) {
            return currentDetailedPracticeHistory;
        }
    }

    public void setCurrentDetailedPracticeHistory(DetailedPracticeHistory currentDetailedPracticeHistory) {
        synchronized (currentDetailedPracticeHistory) {
            this.currentDetailedPracticeHistory = currentDetailedPracticeHistory;
        }
    }

    public boolean isTicking() {
        return ticking;
    }

    @Override
    public void run() {
        setAndSaveChronometerState(true);
        LOG.debug(this.getName() + " run");
        while (!isInterrupted()) {
            tick();
        }
        LOG.error(this.getName() + " stopped ");
        sessionBackgroundChronometer = null;
    }

    @Override
    public void interrupt() {
        LOG.error(this.getName() + " interrupted ");
        pauseTicking();
        super.interrupt();
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
                // System.out.println(Common.convertMillisToStringTime(System.currentTimeMillis()) +": count - " +globalChronometerCountInSeconds);
                checkAndChangeDateIfNeeded();
                if (globalChronometerCountInSeconds % saveInterval == 0) {
                    if (ticking) {
                        if (DB != null && currentPracticeHistory != null) {
                            synchronized (currentPracticeHistory) {
                                currentPracticeHistory.setDuration(globalChronometerCountInSeconds);
                                currentPracticeHistory.setLastTime(Calendar.getInstance().getTimeInMillis());
                                currentPracticeHistory.dbSave(DB);
                            }
                            synchronized (currentDetailedPracticeHistory) {
                                if (currentDetailedPracticeHistory.getTime() == 0L) {
                                    currentDetailedPracticeHistory.setTime(Calendar.getInstance().getTimeInMillis());
                                    currentDetailedPracticeHistory.setDuration(0);
                                } else {
                                    currentDetailedPracticeHistory.setDuration((Calendar.getInstance().getTimeInMillis()
                                    - currentDetailedPracticeHistory.getTime())/1000);
                                }
                                currentDetailedPracticeHistory.dbSave(DB);
                            }
                            if (ticking) {
                                if (sessionOptions != null) {

                                    if (service != null) {
                                        //if (sessionOptions.getDisplayNotificationTimerSwitch() == 1) {
                                        writeMemoryInLog();
                                        updateNotification(Constants.ACTION.PLAY_ACTION);
                                        // }
                                    }

                                } else {
                                    LOG.error(this.getName() + " sessionOptions==null");
                                    //service.stopForeground(true);
                                }
                            }
                        }
                    }
                }
            }
        }
        LOG.debug(this.getName() + " out of tick");

    }

    private void writeMemoryInLog() {
        long freeSize = 0L;
        long totalSize = 0L;
        long usedSize = -1L;
        try {
            Runtime info = Runtime.getRuntime();
            freeSize = info.freeMemory() / 1024;
            totalSize = info.totalMemory() / 1024;
            usedSize = totalSize - freeSize;
        } catch (Exception e) {
            e.printStackTrace();
        }

        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) service.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        long availableKbs = mi.availMem / 1024;

        LOG.info(this.getName() + "-Tick. Total free " + availableKbs + "Kb. Free app memory in heap " + freeSize + "Kb. Used memory in heap " + usedSize + "Kb");

    }

    public void updateNotification(String symbol) {
        if (sessionOptions != null) {
            Notification notification = getCurrentNotification(symbol);
            NotificationManager mNotificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(SESSION_NOTIFICATION_ID, notification);
        }
    }

    public Notification getCurrentNotification(String symbol) {

        try {
            Intent notigicationIntent = new Intent(service, ActivityChrono.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(service, 0,
                    notigicationIntent, 0);
            String practiceName = "WIYTD";
            String projectName = "";
            String areaName = "";
            int areaColor = Color.WHITE;
            if (currentPracticeHistory != null) {
                int currentPracticeID = this.getCurrentPracticeHistory().getIdPractice();
                if (DB.containsPractice(currentPracticeID)) {
                    Practice practice = DB.getPractice(currentPracticeID);
                    if (practice != null) {
                        practiceName = practice.getName().trim();
                        if (DB.containsProject(practice.getIdProject())) {
                            Project project = DB.getProject(practice.getIdProject());
                            if (project != null) {
                                projectName = project.getName();
                                if (DB.containsArea(project.getIdArea())) {
                                    Area area = DB.getArea(project.getIdArea());
                                    if (area != null) {
                                        areaName = area.getName();
                                        areaColor = area.getColor();
                                    }
                                }
                            }
                        }
                    }
                }
            }

            String currentDuration = Common.convertMillisToStringWithAllTime(this.getGlobalChronometerCountInSeconds() * 1000);


//            int iconPause = 0;

//            //What happen when you will click on button
//            Intent actionIntentPlay = new Intent(service, BackgroundChronometerService.class);
//            Intent actionIntentPause = new Intent(service, BackgroundChronometerService.class);
//
//            actionIntentPlay.setAction("PLAY");
//            iconPlay = R.drawable.ic_play;
//
//            actionIntentPause.setAction("PAUSE");
//            iconPause = R.drawable.ic_pause;
//
//
//            PendingIntent pendingIntentPlay = PendingIntent.getService(service, 0, actionIntentPlay, 0);
//            PendingIntent pendingIntentPause = PendingIntent.getService(service, 0, actionIntentPause, 0);

//            Intent actionIntent = new Intent(service, BackgroundChronometerService.class);
//            if (symbol.equals(Constants.ACTION.PLAY_ACTION)) {
//                actionIntent.setAction("PAUSE");
//                iconPlayPause = R.drawable.ic_pause;
//            } else if (symbol.equals(Constants.ACTION.PAUSE_ACTION)) {
//                currentDuration = currentDuration.concat(" (paused)");
//                actionIntent.setAction("PLAY");
//                iconPlayPause = R.drawable.ic_play;
//            }

//            PendingIntent pendingIntentPlayPause = PendingIntent.getService(service, 0, actionIntent, 0);
//
//            NotificationCompat.Action actionPlayPause = new NotificationCompat.Action.Builder(iconPlayPause, currentDuration, pendingIntentPlayPause).build();

//            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                notification = new NotificationCompat.Builder(service)
//                        .setContentTitle(practiceName)
//                        .setContentText(projectName + " - " + areaName)
//                        .setContentIntent(pendingIntent)
//                        .setSmallIcon(R.mipmap.sand_clock, 0)
//                        .addAction(actionPlayPause)
//                        .build();
//            } else {
//                 notification = new NotificationCompat.Builder(service)
//                        .setContentTitle(practiceName)
//                        .setContentText(currentDuration)
//                        .setContentIntent(pendingIntent)
//                        .setSmallIcon(R.mipmap.sand_clock, 0)
//                        .build();
            //}
            Notification notification;

            RemoteViews views = new RemoteViews(service.getPackageName(),
                    R.layout.status_bar);
            views.setViewVisibility(R.id.status_bar_icon, View.VISIBLE);

            Intent statusBarIntent = new Intent(service, ActivityChrono.class);
            statusBarIntent.setAction(Constants.ACTION.CHRONO_ACTION);
            statusBarIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent statusBarPendingIntent = PendingIntent.getActivity(service, 0,
                    statusBarIntent, 0);

            Intent intentPlayPause = new Intent(service, BackgroundChronometerService.class);

            int iconPlayPause = 0;

            if (symbol.equals(Constants.ACTION.PAUSE_ACTION)) {
                notificationStatusIsPlay = false;
            } else if (symbol.equals(Constants.ACTION.PLAY_ACTION)) {
                notificationStatusIsPlay = true;
            }

            if (!notificationStatusIsPlay) {
                currentDuration = currentDuration.concat(" (paused)");
                intentPlayPause.setAction(Constants.ACTION.PLAY_ACTION);
                iconPlayPause = android.R.drawable.ic_media_play;
                practiceName = Common.SYMBOL_STOP + " " + practiceName;
            } else {
                intentPlayPause.setAction(Constants.ACTION.PAUSE_ACTION);
                iconPlayPause = android.R.drawable.ic_media_pause;
                practiceName = Common.SYMBOL_PLAY + " " + practiceName;
            }

            PendingIntent pPlayPauseIntent = PendingIntent.getService(service, 0,
                    intentPlayPause, 0);

            int iconTimerInfo = 0;

            Intent intentTimerInfoNotification = new Intent(service, BackgroundChronometerService.class);

            String message = currentDuration;
            if (Session.sessionOptions.getDisplayNotificationTimerSwitch() == 1) {
                intentTimerInfoNotification.setAction(Constants.ACTION.SHOW_NOTIFICATION_INFO);
                iconTimerInfo = android.R.drawable.ic_dialog_info;
            } else {
                intentTimerInfoNotification.setAction(Constants.ACTION.SHOW_NOTIFICATION_TIMER);
                iconTimerInfo = android.R.drawable.ic_menu_recent_history;
                message = projectName + " - " + areaName;

            }
            PendingIntent pShowHideIntent = PendingIntent.getService(service, 0,
                    intentTimerInfoNotification, 0);

            views.setOnClickPendingIntent(R.id.status_bar_play_pause, pPlayPauseIntent);
            views.setOnClickPendingIntent(R.id.status_bar_timer_info, pShowHideIntent);

            views.setImageViewResource(R.id.status_bar_play_pause,
                    iconPlayPause);
            views.setImageViewResource(R.id.status_bar_timer_info,
                    iconTimerInfo);
            views.setTextViewText(R.id.status_bar_practice_name, practiceName);

            views.setTextViewText(R.id.status_bar_duration, message);

            notification = new NotificationCompat.Builder(service).build();
            notification.contentView = views;
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            notification.icon = R.mipmap.sand_clock;
            notification.contentIntent = statusBarPendingIntent;

            return notification;

        } catch (NullPointerException e) {
            LOG.error(this.getName() + "-" + e.getMessage(), e);
            throw e;
        }
    }

    private void checkAndChangeDateIfNeeded() {

        Calendar today = Calendar.getInstance();
        today.clear(Calendar.HOUR);
        today.clear(Calendar.HOUR_OF_DAY);
        today.clear(Calendar.MINUTE);
        today.clear(Calendar.SECOND);
        today.clear(Calendar.MILLISECOND);
        long todayInMillis = today.getTimeInMillis();

        if (currentPracticeHistory.getDate() < todayInMillis) {

            synchronized (currentPracticeHistory) {
                if (isTicking()) {
                    currentPracticeHistory.setDuration(globalChronometerCountInSeconds);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(currentPracticeHistory.getDate());
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    calendar.set(Calendar.MILLISECOND, 59);
                    currentPracticeHistory.setLastTime(calendar.getTimeInMillis());
                    currentPracticeHistory.dbSave(DB);
                }
                //change practice history
                currentPracticeHistory = new PracticeHistory.Builder(DB)
                        .addDate(todayInMillis)
                        .addIdPractice(currentPracticeHistory.getIdPractice())
                        .addLastTime(todayInMillis)
                        .addDuration(0)
                        .build();
            }
            synchronized (currentDetailedPracticeHistory) {
                if (isTicking()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(currentDetailedPracticeHistory.getDate());
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    calendar.set(Calendar.MILLISECOND, 59);
                    currentDetailedPracticeHistory.setDuration((calendar.getTimeInMillis()
                            - currentDetailedPracticeHistory.getTime())/1000);
                    currentDetailedPracticeHistory.dbSave(DB);
                }
                //change practice history
                currentDetailedPracticeHistory = new DetailedPracticeHistory.Builder(DB)
                        .addDate(todayInMillis)
                        .addIdPractice(currentPracticeHistory.getIdPractice())
                        .addTime(todayInMillis)
                        .addDuration(0)
                        .build();

            }
            setGlobalChronometerCountInSeconds(0L);
            if (service != null) {
                if (isTicking()) {
                    updateNotification(Constants.ACTION.PLAY_ACTION);
                } else {
                    updateNotification(Constants.ACTION.PAUSE_ACTION);
                }
            }
        }

    }

    public void freezeNotification() {
        if (sessionOptions != null) {
            Notification notification = getCurrentNotification(Constants.ACTION.FREEZE_ACTION);
            NotificationManager mNotificationManager = (NotificationManager) service.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(SESSION_NOTIFICATION_ID, notification);
        }
    }
}

