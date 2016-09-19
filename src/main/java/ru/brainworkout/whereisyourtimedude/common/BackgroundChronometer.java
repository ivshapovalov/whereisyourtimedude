package ru.brainworkout.whereisyourtimedude.common;


public class BackgroundChronometer extends Thread {

    public static final BackgroundChronometer INSTANCE = new BackgroundChronometer();
    private volatile long globalChronometerCount = 0;
    private volatile boolean ticking;

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
        this.ticking =true;

    }

    public boolean isTicking() {
        return ticking;
    }

    @Override
    public void run() {
        ticking =true;
        while(!isInterrupted()) {
            increaseChronometer();
         }
    }

    private void increaseChronometer() {
        while (ticking) {
            try {
                this.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            globalChronometerCount += 1000;
        }
    }
}
