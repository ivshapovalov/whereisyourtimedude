package ru.brainworkout.whereisyourtimedude.common;


import java.util.ArrayDeque;
import java.util.Queue;
import java.util.TreeSet;

import ru.brainworkout.whereisyourtimedude.database.entities.Area;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.PracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.entities.Project;
import ru.brainworkout.whereisyourtimedude.database.entities.User;

public class Session {

    public static User sessionUser;
    public static Project currentProject;
    public static Area currentArea;
    public static Practice currentPractice;
    public static PracticeHistory currentPracticeHistory;

    public static Queue<Boolean> queueIsNew= new ArrayDeque<Boolean>();
    public static Queue<Boolean> queueForChoice= new ArrayDeque<Boolean>();


}
