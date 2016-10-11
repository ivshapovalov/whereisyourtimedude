package ru.brainworkout.whereisyourtimedude.common;

import android.app.Service;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;


import ru.brainworkout.whereisyourtimedude.database.entities.*;

public class Session {

    public static User sessionCurrentUser;
    public static Project sessionCurrentProject;
    public static Area sessionCurrentArea;
    public static Practice sessionCurrentPractice;
    public static PracticeHistory sessionCurrentPracticeHistory;
    public static final Deque<ConnectionParameters> sessionOpenActivities = new ArrayDeque<>();
    public static volatile BackgroundChronometer sessionBackgroundChronometer =new BackgroundChronometer();
    public static boolean sessionChronometerIsWorking;
    public static final int SESSION_NOTIFICATION_ID=1337;
    public static Options sessionOptions;
    public static int saveInterval;



}
