package ru.brainworkout.whereisyourtimedude.common;


public class BackgroundChronometer extends Thread {

    public static final BackgroundChronometer INSTANCE = new BackgroundChronometer();
    private volatile long globalChronometerCount = 0;
    private volatile boolean running;

    public long getGlobalChronometerCount() {
        return globalChronometerCount;
    }

    public void setGlobalChronometerCount(long globalChronometerCount) {
        this.globalChronometerCount = globalChronometerCount;
    }

    public void pause() {
        this.running = false;

    }
    public void resumepause() {
        this.running=true;

    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void run() {
        running=true;
        while(true) {
            if (running) {
                IncreaseChronometer();
            }
            else {
                DontIncreaseChronometer();
            }
        }
    }

    private void IncreaseChronometer() {
        while (running) {
            try {
                this.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            globalChronometerCount += 1000;
        }
    }

    private void DontIncreaseChronometer() {
        while (!running) {
            try {
                this.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }




}
