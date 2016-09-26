package ru.brainworkout.whereisyourtimedude.common;

import android.app.Service;
import android.content.Context;
import android.os.PowerManager;

import java.util.Calendar;

import ru.brainworkout.whereisyourtimedude.database.entities.PracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;

public class BackgroundChronometer extends Thread {

    private volatile Long globalChronometerCount = 0L;
    private volatile boolean ticking;
    private volatile PracticeHistory currentPracticeHistory;
    private volatile DatabaseManager DB;


    public BackgroundChronometer() {
        //this.setDaemon(true);
        this.setPriority(Thread.MAX_PRIORITY);
    }

    public long getGlobalChronometerCount() {
        synchronized (globalChronometerCount) {
            return globalChronometerCount;
        }
    }

    public void setGlobalChronometerCount(Long globalChronometerCount) {
        synchronized (globalChronometerCount) {
            this.globalChronometerCount = globalChronometerCount;
        }
    }


    private void increaseGlobalChronometerCount(int i) {
        synchronized (globalChronometerCount) {
            globalChronometerCount+=1000;
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

            if ((globalChronometerCount / 1000) % Common.SAVE_INTERVAL == 0) {
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
                increaseGlobalChronometerCount(1000);
                checkAndChangeDateIfNeeded();
                if ((globalChronometerCount / 1000) % Common.SAVE_INTERVAL == 0) {
                    if (ticking) {
                        if (DB != null && currentPracticeHistory != null) {
                            synchronized (currentPracticeHistory) {
                                currentPracticeHistory.setDuration(globalChronometerCount);
                                currentPracticeHistory.setLastTime(Calendar.getInstance().getTimeInMillis());
                                currentPracticeHistory.dbSave(DB);
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
                currentPracticeHistory.setDuration(globalChronometerCount);
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
            setGlobalChronometerCount(0L);
        }
    }
}

