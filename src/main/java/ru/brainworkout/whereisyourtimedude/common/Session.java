package ru.brainworkout.whereisyourtimedude.common;

import android.app.Service;

import java.util.Stack;


import ru.brainworkout.whereisyourtimedude.database.entities.*;

public class Session {

    public static User sessionCurrentUser;
    public static Project sessionCurrentProject;
    public static Area sessionCurrentArea;
    public static Practice sessionCurrentPractice;
    public static PracticeHistory sessionCurrentPracticeHistory;
    public static final Stack<ConnectionParameters> sessionOpenActivities = new Stack<>();
    public static BackgroundChronometer sessionBackgroundChronometer =new BackgroundChronometer();
    public static boolean sessionChronometerIsWorking;
    public static final int SESSION_NOTIFICATION_ID=1337;


}
