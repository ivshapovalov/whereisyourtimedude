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
    public static final Deque<Project> sessionProjectSequence = new ArrayDeque<>();;
    public static final Deque<Area> sessionAreaSequence =new ArrayDeque<>();
    public static final Deque<Practice> sessionPracticeSequence =new ArrayDeque<>();
    public static final Deque<PracticeHistory> sessionPracticeHistorySequence = new ArrayDeque<>();
    public static final Deque<DetailedPracticeHistory> sessionDetailedPracticeHistorySequence =new ArrayDeque<>();

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

    public  static void clearAllSessionSequences(){
        sessionAreaSequence.clear();
        sessionProjectSequence.clear();
        sessionPracticeSequence.clear();
        sessionPracticeHistorySequence.clear();
        sessionDetailedPracticeHistorySequence.clear();
    }
}