package ru.brainworkout.whereisyourtimedude.common;

import android.content.Context;

import java.util.Calendar;

import ru.brainworkout.whereisyourtimedude.database.entities.PracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;

public class BackgroundChronometer extends Thread {

    private volatile long globalChronometerCount = 0;
    private volatile boolean ticking;
    private volatile PracticeHistory currentPracticeHistory;
    private volatile DatabaseManager DB;


    public BackgroundChronometer() {
        this.setDaemon(true);
    }

    public long getGlobalChronometerCount() {
        return globalChronometerCount;
    }

    public void setGlobalChronometerCount(long globalChronometerCount) {
        this.globalChronometerCount = globalChronometerCount;
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
        return currentPracticeHistory;
    }

    public void setCurrentPracticeHistory(PracticeHistory currentPracticeHistory) {
        this.currentPracticeHistory = currentPracticeHistory;
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

            if ((globalChronometerCount/1000)%Common.SAVE_INTERVAL == 0) {
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
                globalChronometerCount += 1000;
                checkAndChangeDateIfNeeded();
                if ((globalChronometerCount/1000)%Common.SAVE_INTERVAL == 0) {
                    if (ticking) {
                        if (DB != null && currentPracticeHistory != null) {
                            currentPracticeHistory.setDuration(globalChronometerCount);
                            currentPracticeHistory.setLastTime(Calendar.getInstance().getTimeInMillis());
                            currentPracticeHistory.dbSave(DB);
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
            globalChronometerCount = 0;
        }
    }
}

