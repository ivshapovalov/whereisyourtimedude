package ru.brainworkout.whereisyourtimedude.common;

import android.content.Context;

import java.util.Calendar;

import ru.brainworkout.whereisyourtimedude.database.entities.PracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;

public class BackgroundChronometer extends Thread {

    private volatile long globalChronometerCount = 0;
    private volatile boolean ticking;
    private volatile PracticeHistory currentPracticeHistory;
    private volatile Context context;


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

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
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
        ticking = true;
        while (!isInterrupted()) {
            tick();
        }
    }

    private void tick() {

        while (!isInterrupted()) {
            while (ticking) {
                try {
                    this.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                globalChronometerCount += 1000;
               if (globalChronometerCount% Common.SAVE_INTERVAL ==0){
                if (context!=null && currentPracticeHistory!=null) {
                    DatabaseManager DB = new DatabaseManager(context);
                    currentPracticeHistory.setDuration(globalChronometerCount);
                    currentPracticeHistory.setLastTime(Calendar.getInstance().getTimeInMillis());
                    currentPracticeHistory.dbSave(DB);
                }

                }
            }


        }
        }
    }

