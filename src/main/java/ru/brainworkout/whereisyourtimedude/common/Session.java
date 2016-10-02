package ru.brainworkout.whereisyourtimedude.common;

import java.util.Stack;


import ru.brainworkout.whereisyourtimedude.database.entities.Area;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.PracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.entities.Project;
import ru.brainworkout.whereisyourtimedude.database.entities.User;

public class Session {

    public static User sessionCurrentUser;
    public static Project sessionCurrentProject;
    public static Area sessionCurrentArea;
    public static Practice sessionCurrentPractice;
    public static PracticeHistory sessionCurrentPracticeHistory;
    public static final Stack<ConnectionParameters> sesionOpenActivities = new Stack<>();
    public static BackgroundChronometer sessionBackgroundChronometer =new BackgroundChronometer();
    public static boolean sessionChronometerIsWorking;
}
