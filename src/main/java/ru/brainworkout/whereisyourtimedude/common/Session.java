package ru.brainworkout.whereisyourtimedude.common;


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

}
