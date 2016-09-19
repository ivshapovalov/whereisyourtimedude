package ru.brainworkout.whereisyourtimedude.common;


public class BackgroundChronometer extends Thread {

    public static BackgroundChronometer INSTANCE = new BackgroundChronometer();
    private volatile long globalChronometerCount = 0;
    private volatile boolean ticking;

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
            }


        }
        }
    }

