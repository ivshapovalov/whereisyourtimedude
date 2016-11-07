package ru.brainworkout.whereisyourtimedude.common;

import android.os.Build;
import android.os.Environment;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;

import de.mindpipe.android.logging.log4j.LogConfigurator;

public class Alogger {
    public static org.apache.log4j.Logger getLogger(Class clazz) {
        final LogConfigurator logConfigurator = new LogConfigurator();
//        if (Build.MODEL.contains("ME173")) {
//            logConfigurator.setFileName(Environment.getExternalStorageDirectory().toString() + File.separator + "Android/data/com.dropbox.android/files/scratch/android/timer_"+ Build.MODEL+".log");
//        } else {
            logConfigurator.setFileName(Environment.getExternalStorageDirectory().toString() + File.separator + "timer_"+ Build.MODEL+".log");
            //logConfigurator.setFileName(Environment.getExternalStorageDirectory().toString() + File.separator + "Android/data/com.dropbox.android/files/u56524148/scratch/android/timer_"+ Build.MODEL+".log");
        //}
        //logConfigurator.setFileName(Environment.getExternalStorageDirectory().toString() + File.separator + "Android/data/com.yandex.disk/files/disk/timer_"+ Build.MODEL+".log");
       //logConfigurator.setFileName(Environment.getExternalStorageDirectory().toString() + File.separator + "Android/data/com.dropbox.android/files/u56524148/scratch/android/timer.log");
       //logConfigurator.setFileName(Environment.getExternalStorageDirectory().toString() + File.separator + "log/timer.log");
        logConfigurator.setRootLevel(Level.ALL);
        logConfigurator.setLevel("org.apache", Level.ALL);
        logConfigurator.setUseFileAppender(true);
        logConfigurator.setFilePattern("%d %-5p [%c{2}]-[%L] %m%n");
        logConfigurator.setMaxFileSize(1024 * 1024 * 5);
        logConfigurator.setImmediateFlush(true);
        logConfigurator.configure();
        Logger log = Logger.getLogger(clazz);
        return log;
    }
}