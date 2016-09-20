package ru.brainworkout.whereisyourtimedude.common;


import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.TreeSet;

import ru.brainworkout.whereisyourtimedude.database.entities.Area;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.PracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.entities.Project;
import ru.brainworkout.whereisyourtimedude.database.entities.User;

import ru.brainworkout.whereisyourtimedude.common.BackgroundChronometer.*;

public class Session {

    public static User sessionUser;
    public static Project currentProject;
    public static Area currentArea;
    public static Practice currentPractice;
    public static PracticeHistory currentPracticeHistory;
    public static final Stack<ConnectionParameters> openActivities= new Stack<>();
    public static BackgroundChronometer backgroundChronometer=new BackgroundChronometer();

    static {
       // backgroundChronometer.start();
    }


}
