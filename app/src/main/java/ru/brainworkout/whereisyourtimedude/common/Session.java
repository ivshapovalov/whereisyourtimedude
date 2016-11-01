package ru.brainworkout.whereisyourtimedude.common;

import org.apache.log4j.Logger;

import java.util.ArrayDeque;
import java.util.Deque;

import ru.brainworkout.whereisyourtimedude.database.entities.*;

public class Session {
    private static Logger LOG = Alogger.getLogger(Session.class);

    static {
        LOG.debug("Session static start");
        String message = Common.convertStackTraceToString(Thread.currentThread().getStackTrace());
        LOG.debug(message);
    }

    public static User sessionCurrentUser;
    public static Project sessionCurrentProject;
    public static Area sessionCurrentArea;
    public static Practice sessionCurrentPractice;
    public static PracticeHistory sessionCurrentPracticeHistory;
    public static DetailedPracticeHistory sessionCurrentDetailedPracticeHistory;

    public static final Deque<ConnectionParameters> sessionOpenActivities = new ArrayDeque<>();
    public static BackgroundChronometer sessionBackgroundChronometer;
    public static boolean sessionChronometerIsWorking;
    public static final int SESSION_NOTIFICATION_ID = 1337;
    public static Options sessionOptions;
    public static int saveInterval;
    static {
        LOG.debug("Session static end");
        String message = Common.convertStackTraceToString(Thread.currentThread().getStackTrace());
        LOG.debug(message);
    }

}