package ru.brainworkout.whereisyourtimedude.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;

import java.util.Calendar;

import ru.brainworkout.whereisyourtimedude.activities.ActivityMain;
import ru.brainworkout.whereisyourtimedude.database.entities.PracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;

public class BackgroundChronometer extends Thread {

    private volatile Long globalChronometerCountInSeconds = 0L;
    private volatile Long beginTimeinMillis = 0L;
    private volatile boolean ticking;
    private volatile PracticeHistory currentPracticeHistory;
    private volatile DatabaseManager DB;


    public BackgroundChronometer() {

        this.setName("backgroundChronometer");

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

            if ((globalChronometerCountInSeconds) % Common.SAVE_INTERVAL == 0) {
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
                if (globalChronometerCountInSeconds % Common.SAVE_INTERVAL == 0) {
                    if (ticking) {
                        if (DB != null && currentPracticeHistory != null) {
                            synchronized (currentPracticeHistory) {
                                currentPracticeHistory.setDuration(globalChronometerCountInSeconds);
                                currentPracticeHistory.setLastTime(Calendar.getInstance().getTimeInMillis());
                                currentPracticeHistory.dbSave(DB);
                                // System.out.println(Common.ConvertMillisToStringTime(System.currentTimeMillis()) +": count - " +globalChronometerCountInSeconds+ " :save");
                            }
                        }
                    }
                }
            }
        }
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

