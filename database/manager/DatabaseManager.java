package ru.brainworkout.whereisyourtimedude.database.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import ru.brainworkout.whereisyourtimedude.database.entities.Area;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.PracticeTimer;
import ru.brainworkout.whereisyourtimedude.database.entities.Project;
import ru.brainworkout.whereisyourtimedude.database.entities.User;

public class DatabaseManager extends SQLiteOpenHelper {
    // All Static variables

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "wiytd";

    // Tables names
    private static final String TABLE_USERS = "users";
    private static final String TABLE_PRACTICES = "practices";
    private static final String TABLE_PROJECTS = "projects";
    private static final String TABLE_AREAS = "areas";
    private static final String TABLE_PRACTICE_TIMER = "practice_timer";

    // Exercise Columns names
    private static final String KEY_PRACTICE_ID = "practice_id";
    private static final String KEY_PRACTICE_ID_USER = "practice_id_user";
    private static final String KEY_PRACTICE_IS_ACTIVE = "practice_is_active";
    private static final String KEY_PRACTICE_NAME = "practice_name";
    private static final String KEY_PRACTICE_ID_PROJECT= "practice_id_project";

    //  Area Columns names
    private static final String KEY_AREA_ID = "area_id";
    private static final String KEY_AREA_ID_USER = "area_id_user";
    private static final String KEY_AREA_NAME= "area_name";
    private static final String KEY_AREA_COLOR= "area_color";

    //  Project Columns names
    private static final String KEY_PROJECT_ID = "project_id";
    private static final String KEY_PROJECT_ID_USER = "project_id_user";
    private static final String KEY_PROJECT_ID_AREA = "project_id_area";
    private static final String KEY_PROJECT_NAME= "project_name";

    //  Practice timer
    private static final String KEY_PRACTICE_TIMER_ID = "practice_timer_id";
    private static final String KEY_PRACTICE_TIMER_ID_USER = "practice_timer_id_user";
    private static final String KEY_PRACTICE_TIMER_ID_PRACTICE = "practice_timer_id_practice";
    private static final String KEY_PRACTICE_TIMER_DATE = "practice_timer_date";
    private static final String KEY_PRACTICE_TIMER_DURATION= "duration";
    private static final String KEY_PRACTICE_TIMER_LAST_TIME = "practice_timer_last_time";

    //  Users AbstractEntity Columns names
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_IS_CURRENT = "user_is_current";

    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        //users
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_USER_ID + " INTEGER UNIQUE PRIMARY KEY NOT NULL,"
                + KEY_USER_NAME + " TEXT," + KEY_USER_IS_CURRENT + " INTEGER)";
        db.execSQL(CREATE_USERS_TABLE);

        //areas
        String CREATE_AREAS_TABLE = "CREATE TABLE " + TABLE_AREAS + "("
                + KEY_AREA_ID + " INTEGER UNIQUE PRIMARY KEY NOT NULL,"
                + KEY_AREA_ID_USER + " INTEGER, "
                + KEY_AREA_NAME + " TEXT," + KEY_AREA_COLOR + " INTEGER,"
                + " FOREIGN KEY(" + KEY_AREA_ID_USER + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + ")"
                +")";
        db.execSQL(CREATE_AREAS_TABLE);


        String CREATE_AREAS_INDEX_USER_ASC = "CREATE INDEX AREAS_USER_IDX_ASC ON " + TABLE_AREAS + " (" + KEY_AREA_ID_USER + " ASC)";
        db.execSQL(CREATE_AREAS_INDEX_USER_ASC);
        String CREATE_AREAS_INDEX_USER_DESC = "CREATE INDEX AREAS_USER_IDX_DESC ON " + TABLE_AREAS + " (" + KEY_AREA_ID_USER + " DESC)";
        db.execSQL(CREATE_AREAS_INDEX_USER_DESC);

        //projects
        String CREATE_PROJECTS_TABLE = "CREATE TABLE " + TABLE_PROJECTS + "("
                + KEY_PROJECT_ID + " INTEGER UNIQUE PRIMARY KEY NOT NULL,"
                + KEY_PROJECT_ID_USER + " INTEGER, "
                + KEY_PROJECT_NAME + " TEXT," + KEY_PROJECT_ID_AREA+ " INTEGER,"
                + " FOREIGN KEY(" + KEY_PROJECT_ID_USER + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + ")"+","
                + " FOREIGN KEY(" + KEY_PROJECT_ID_AREA + ") REFERENCES " + TABLE_AREAS + "(" + KEY_AREA_ID + ")"
                +")";
        db.execSQL(CREATE_PROJECTS_TABLE);


        String CREATE_PROJECTS_INDEX_USER_ASC = "CREATE INDEX PROJECTS_USER_IDX_ASC ON " + TABLE_PROJECTS + " (" + KEY_PROJECT_ID_USER + " ASC)";
        db.execSQL(CREATE_PROJECTS_INDEX_USER_ASC);
        String CREATE_PROJECTS_INDEX_USER_DESC = "CREATE INDEX PROJECTS_USER_IDX_DESC ON " + TABLE_PROJECTS + " (" + KEY_PROJECT_ID_USER + " DESC)";
        db.execSQL(CREATE_PROJECTS_INDEX_USER_DESC);

        String CREATE_PROJECTS_INDEX_AREA_ASC = "CREATE INDEX PROJECTS_AREA_IDX_ASC ON " + TABLE_PROJECTS + " (" + KEY_PROJECT_ID_AREA + " ASC)";
        db.execSQL(CREATE_PROJECTS_INDEX_AREA_ASC);
        String CREATE_PROJECTS_INDEX_AREA_DESC = "CREATE INDEX PROJECTS_AREA_IDX_DESC ON " + TABLE_PROJECTS + " (" + KEY_PROJECT_ID_AREA + " DESC)";
        db.execSQL(CREATE_PROJECTS_INDEX_AREA_ASC);

        //practises
        String CREATE_PRACTICES_TABLE = "CREATE TABLE " + TABLE_PRACTICES + "("
                + KEY_PRACTICE_ID + " INTEGER UNIQUE PRIMARY KEY NOT NULL,"
                + KEY_PRACTICE_ID_USER + " INTEGER, "
                + KEY_PRACTICE_ID_PROJECT + " INTEGER, "
                + KEY_PRACTICE_IS_ACTIVE + " INTEGER, "
                + KEY_PRACTICE_NAME + " TEXT,"
                + " FOREIGN KEY(" + KEY_PRACTICE_ID_USER + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + ")"+","
                + " FOREIGN KEY(" + KEY_PRACTICE_ID_PROJECT+ ") REFERENCES " + TABLE_PROJECTS + "(" + KEY_PROJECT_ID + ")"
                + ")";
        db.execSQL(CREATE_PRACTICES_TABLE);

        String CREATE_PRACTICES_INDEX_USER_ASC = "CREATE INDEX PRACTICES_USER_IDX_ASC ON " + TABLE_PRACTICES + " (" + KEY_PRACTICE_ID_USER + " ASC)";
        db.execSQL(CREATE_PRACTICES_INDEX_USER_ASC);
        String CREATE_PRACTICES_INDEX_USER_DESC = "CREATE INDEX PRACTICES_USER_IDX_DESC ON " + TABLE_PRACTICES + " (" + KEY_PRACTICE_ID_USER + " DESC)";
        db.execSQL(CREATE_PRACTICES_INDEX_USER_DESC);

        String CREATE_PRACTICES_INDEX_PROJECT_ASC = "CREATE INDEX PRACTICES_PROJECT_IDX_ASC ON " + TABLE_PRACTICES + " (" + KEY_PRACTICE_ID_PROJECT + " ASC)";
        db.execSQL(CREATE_PRACTICES_INDEX_PROJECT_ASC);
        String CREATE_PRACTICES_INDEX_PROJECT_DESC = "CREATE INDEX PRACTICES_PROJECT_IDX_DESC ON " + TABLE_PRACTICES + " (" + KEY_PRACTICE_ID_PROJECT + " DESC)";
        db.execSQL(CREATE_PRACTICES_INDEX_PROJECT_DESC);

        //practice_timer
        String CREATE_PRACTICE_TIMER_TABLE = "CREATE TABLE " + TABLE_PRACTICE_TIMER + "("
                + KEY_PRACTICE_TIMER_ID + " INTEGER UNIQUE PRIMARY KEY NOT NULL,"
                + KEY_PRACTICE_TIMER_ID_USER + " INTEGER, "
                + KEY_PRACTICE_TIMER_ID_PRACTICE + " INTEGER,"
                + KEY_PRACTICE_TIMER_DATE + " DATETIME,"
                + KEY_PRACTICE_TIMER_DURATION + " INTEGER,"
                + KEY_PRACTICE_TIMER_LAST_TIME + " DATETIME, "
                + "FOREIGN KEY(" + KEY_PRACTICE_TIMER_ID_PRACTICE + ") REFERENCES " + TABLE_PRACTICES + "(" + KEY_PRACTICE_ID + "),"
                + "FOREIGN KEY(" + KEY_PRACTICE_TIMER_ID_USER + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + ")"
                + ")";
        db.execSQL(CREATE_PRACTICE_TIMER_TABLE);

        String CREATE_PRACTICE_TIMER_INDEX_PRACTICE_ASC = "CREATE INDEX PRACTICE_TIMER_PRACTICE_IDX_ASC ON " + TABLE_PRACTICE_TIMER + " (" + KEY_PRACTICE_TIMER_ID_PRACTICE + " ASC)";
        db.execSQL(CREATE_PRACTICE_TIMER_INDEX_PRACTICE_ASC);

        String CREATE_PRACTICE_TIMER_INDEX_PRACTICE_DESC = "CREATE INDEX PRACTICE_TIMER_PRACTICE_IDX_DESC ON " + TABLE_PRACTICE_TIMER + " (" + KEY_PRACTICE_TIMER_ID_PRACTICE + " DESC)";
        db.execSQL(CREATE_PRACTICE_TIMER_INDEX_PRACTICE_DESC);

        String CREATE_PRACTICE_TIMER_INDEX_USER_ASC = "CREATE INDEX PRACTICE_TIMER_USER_IDX_ASC ON " + TABLE_PRACTICE_TIMER + " (" + KEY_PRACTICE_TIMER_ID_USER + " ASC)";
        db.execSQL(CREATE_PRACTICE_TIMER_INDEX_USER_ASC);

        String CREATE_PRACTICE_TIMER_INDEX_USER_DESC = "CREATE INDEX PRACTICE_TIMER_USER_IDX_DESC ON " + TABLE_PRACTICE_TIMER + " (" + KEY_PRACTICE_TIMER_ID_USER + " DESC)";
        db.execSQL(CREATE_PRACTICE_TIMER_INDEX_USER_DESC);


        String CREATE_PRACTICE_TIMER_INDEX_USER_AND_PRACTICE_ASC = "CREATE INDEX PRACTICE_TIMER_USER_AND_PRACTICE_IDX_ASC ON " + TABLE_PRACTICE_TIMER + " (" + KEY_PRACTICE_TIMER_ID_USER + " ASC, " + KEY_PRACTICE_TIMER_ID_PRACTICE + " ASC)";
        db.execSQL(CREATE_PRACTICE_TIMER_INDEX_USER_AND_PRACTICE_ASC);
        String CREATE_PRACTICE_TIMER_INDEX_USER_AND_PRACTICE_DESC = "CREATE INDEX PRACTICE_TIMER_USER_AND_PRACTICE_IDX_DESC ON " + TABLE_PRACTICE_TIMER + " (" + KEY_PRACTICE_TIMER_ID_USER + " DESC, " + KEY_PRACTICE_TIMER_ID_PRACTICE + " DESC)";
        db.execSQL(CREATE_PRACTICE_TIMER_INDEX_USER_AND_PRACTICE_DESC);

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AREAS);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRACTICES);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRACTICE_TIMER);

        // Create tables again
        onCreate(db);
    }

    public void DeleteDB(SQLiteDatabase db) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AREAS);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRACTICES);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRACTICE_TIMER);
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, user.getID());
        values.put(KEY_USER_NAME, user.getName());
        values.put(KEY_USER_IS_CURRENT, user.isCurrentUser());

        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public void addArea(Area area) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_AREA_ID, area.getID());
        values.put(KEY_AREA_ID_USER, area.getIdUser());
        values.put(KEY_AREA_NAME, area.getName());
        values.put(KEY_AREA_COLOR, area.getColor());

        // Inserting Row
        db.insert(TABLE_AREAS, null, values);
        db.close(); // Closing database connection
    }

    public void addProject(Project project) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PROJECT_ID, project.getID());
        values.put(KEY_PROJECT_ID_USER, project.getIdUser());
        values.put(KEY_PROJECT_NAME, project.getName());
        values.put(KEY_PROJECT_ID_AREA, project.getAreaID());

        // Inserting Row
        db.insert(TABLE_PROJECTS, null, values);
        db.close(); // Closing database connection
    }

    public void addPractice(Practice practice) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PRACTICE_ID, practice.getID());
        values.put(KEY_PRACTICE_ID_USER, practice.getIdUser());
        values.put(KEY_PRACTICE_NAME, practice.getName());
        values.put(KEY_PRACTICE_ID_PROJECT, practice.getProjectID());
        values.put(KEY_PRACTICE_IS_ACTIVE, practice.getIsActive());
        // Inserting Row
        db.insert(TABLE_PRACTICES, null, values);
        db.close(); // Closing database connection
    }
//TODO тут остановился
    public void addPracticeTimer(PracticeTimer practiceTimer) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PRACTICE_TIMER_ID, practiceTimer.getID());
        values.put(KEY_PRACTICE_TIMER_ID_USER, practiceTimer.getIdUser());
        values.put(KEY_PRACTICE_TIMER_ID_PRACTICE, practiceTimer.getPracticeID());
        values.put(KEY_PRACTICE_TIMER_DATE, practiceTimer.getDate());
        values.put(KEY_PRACTICE_TIMER_DURATION, practiceTimer.getDuration());
        values.put(KEY_PRACTICE_TIMER_LAST_TIME, practiceTimer.getLastTime());

        // Inserting Row
        db.insert(TABLE_PROJECTS, null, values);
        db.close(); // Closing database connection
    }


    public User getUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_USER_ID, KEY_USER_NAME, KEY_USER_IS_CURRENT}, KEY_USER_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        User user = null;
        if (cursor.getCount() == 0) {
            throw new TableDoesNotContainElementException("There is no User with id - " + id);
        } else {
            user = new User.Builder(Integer.parseInt(cursor.getString(0)))
                    .addName(cursor.getString(1))
                    .addIsCurrentUser(Integer.parseInt(cursor.getString(2)))
                    .build();

            cursor.close();
            return user;
        }
    }

    public WeightChangeCalendar getWeightChangeCalendar(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_WEIGHT_CHANGE_CALENDAR, new String[]{KEY_WEIGHT_CHANGE_CALENDAR_ID,
                        KEY_WEIGHT_CHANGE_CALENDAR_ID_USER, KEY_WEIGHT_CHANGE_CALENDAR_DAY, KEY_WEIGHT_CHANGE_CALENDAR_WEIGHT},
                KEY_WEIGHT_CHANGE_CALENDAR_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        WeightChangeCalendar weightChangeCalendar = null;
        if (cursor.getCount() == 0) {
            throw new TableDoesNotContainElementException("There is no User with id - " + id);
        } else {
            weightChangeCalendar = new WeightChangeCalendar.Builder(Integer.parseInt(cursor.getString(0)))
                    .addDay(cursor.getString(2))
                    .addWeight(Integer.parseInt(cursor.getString(3)))
                    .build();

            cursor.close();
            return weightChangeCalendar;
        }
    }

    public Exercise getExercise(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_EXERCISES, new String[]{KEY_EXERCISE_ID, KEY_EXERCISE_ID_USER, KEY_EXERCISE_IS_ACTIVE, KEY_EXERCISE_NAME,
                        KEY_EXERCISE_EXPLANATION, KEY_EXERCISE_VOLUME_DEFAULT, KEY_EXERCISE_PICTURE_NAME}, KEY_EXERCISE_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Exercise exercise = null;
        if (cursor.getCount() == 0) {
            throw new TableDoesNotContainElementException("There is no Exercise with id - " + id);
        } else {
            exercise = new Exercise.Builder(Integer.parseInt(cursor.getString(0)))
                    .addIsActive(Integer.parseInt(cursor.getString(2)))
                    .addName(cursor.getString(3))
                    .addExplanation(cursor.getString(4))
                    .addVolumeDefault(cursor.getString(5))
                    .addPicture(cursor.getString(6)).build();

            cursor.close();
            return exercise;
        }
    }

    public Practice getTraining(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TRAININGS, new String[]{KEY_TRAINING_ID, KEY_TRAINING_DAY}, KEY_TRAINING_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            throw new TableDoesNotContainElementException("There is no Practice with id - " + id);
        } else {
            Practice practice = new Practice.Builder(Integer.parseInt(cursor.getString(0))).addDay(cursor.getString(1)).build();
            cursor.close();
            return practice;
        }


    }

    public TrainingContent getTrainingContent(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TRAINING_CONTENT, new String[]{KEY_TRAINING_CONTENT_ID, KEY_TRAINING_CONTENT_ID_EXERCISE, KEY_TRAINING_CONTENT_ID_TRAINING, KEY_TRAINING_CONTENT_VOLUME, KEY_TRAINING_CONTENT_WEIGHT, KEY_TRAINING_CONTENT_COMMENT}, KEY_TRAINING_CONTENT_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            throw new TableDoesNotContainElementException("There is no TrainingContent with id - " + id);
        } else {
            TrainingContent trainingContent = new TrainingContent.Builder(Integer.parseInt(cursor.getString(0)))
                    .addExerciseId(Integer.parseInt(cursor.getString(1)))
                    .addTrainingId(Integer.parseInt(cursor.getString(2)))
                    .addVolume(cursor.getString(3))
                    .addWeight(Integer.parseInt(cursor.getString(4)))
                    .addComment(cursor.getString(5))
                    .build();

            cursor.close();
            return trainingContent;
        }
    }

    public TrainingContent getTrainingContent(int id, int exercise_id, int training_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TRAINING_CONTENT, new String[]{KEY_TRAINING_CONTENT_ID, KEY_TRAINING_CONTENT_ID_EXERCISE, KEY_TRAINING_CONTENT_ID_TRAINING, KEY_TRAINING_CONTENT_VOLUME, KEY_TRAINING_CONTENT_WEIGHT, KEY_TRAINING_CONTENT_COMMENT}, KEY_TRAINING_CONTENT_ID + "=? AND " + KEY_TRAINING_CONTENT_ID_EXERCISE + "=? AND " + KEY_TRAINING_CONTENT_ID_TRAINING + "=?",
                new String[]{String.valueOf(id), String.valueOf(exercise_id), String.valueOf(training_id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            throw new TableDoesNotContainElementException("There is no TrainingContent with id - " + id);
        } else {
            TrainingContent trainingContent = new TrainingContent.Builder(Integer.parseInt(cursor.getString(0)))
                    .addExerciseId(Integer.parseInt(cursor.getString(1)))
                    .addTrainingId(Integer.parseInt(cursor.getString(2)))
                    .addVolume(cursor.getString(3))
                    .addWeight(Integer.parseInt(cursor.getString(4)))
                    .addComment(cursor.getString(5))
                    .build();
            cursor.close();
            return trainingContent;
        }
    }

    public TrainingContent getTrainingContent(int exercise_id, int training_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        TrainingContent trainingContent;
        Cursor cursor = db.query(TABLE_TRAINING_CONTENT, new String[]{KEY_TRAINING_CONTENT_ID, KEY_TRAINING_CONTENT_ID_EXERCISE, KEY_TRAINING_CONTENT_ID_TRAINING, KEY_TRAINING_CONTENT_VOLUME, KEY_TRAINING_CONTENT_WEIGHT, KEY_TRAINING_CONTENT_COMMENT}, KEY_TRAINING_CONTENT_ID_EXERCISE + "=? AND " + KEY_TRAINING_CONTENT_ID_TRAINING + "=?",
                new String[]{String.valueOf(exercise_id), String.valueOf(training_id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            throw new TableDoesNotContainElementException("There is no TrainingContent with Exercise_id - " + exercise_id + " and Training_id " + training_id);
        } else {
            trainingContent = new TrainingContent.Builder(Integer.parseInt(cursor.getString(0)))
                    .addExerciseId(Integer.parseInt(cursor.getString(1)))
                    .addTrainingId(Integer.parseInt(cursor.getString(2)))
                    .addVolume(cursor.getString(3))
                    .addWeight(Integer.parseInt(cursor.getString(4)))
                    .addComment(cursor.getString(5))
                    .build();
            cursor.close();
            return trainingContent;
        }
    }

    public void deleteAllUsers() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, null, null);

    }

    public void deleteAllWeightChangeCalendar() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WEIGHT_CHANGE_CALENDAR, null, null);

    }

    public void deleteAllWeightChangeCalendarOfUser(int user_id) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_WEIGHT_CHANGE_CALENDAR, KEY_WEIGHT_CHANGE_CALENDAR_ID_USER + "=?", new String[]{String.valueOf(user_id)});

    }

    public void deleteAllExercises() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXERCISES, null, null);

    }

    public void deleteAllExercisesOfUser(int user_id) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_EXERCISES, KEY_EXERCISE_ID_USER + "=?", new String[]{String.valueOf(user_id)});

    }

    public void deleteAllTrainings() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRAININGS, null, null);

    }

    public void deleteAllTrainingsOfUser(int user_id) {

        SQLiteDatabase db = this.getWritableDatabase();
        String whereArgs[] = {String.valueOf(user_id)};
        db.delete(TABLE_TRAININGS, KEY_TRAINING_ID_USER + "=?", new String[]{String.valueOf(user_id)});

    }

    public void deleteAllTrainingContent() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRAINING_CONTENT, null, null);

    }

    public void deleteAllTrainingContentOfUser(int user_id) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRAINING_CONTENT, KEY_TRAINING_CONTENT_ID_USER + "=?", new String[]{String.valueOf(user_id)});

    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_USERS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                User user = new User.Builder(cursor.getInt(0))
                        .addName(cursor.getString(1))
                        .addIsCurrentUser(Integer.parseInt(cursor.getString(2)))
                        .build();
                userList.add(user);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return userList;
    }

    public List<WeightChangeCalendar> getAllWeightChangeCalendar() {
        List<WeightChangeCalendar> weightChangeCalendarList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_WEIGHT_CHANGE_CALENDAR;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                WeightChangeCalendar weightChangeCalendar = new WeightChangeCalendar.Builder(cursor.getInt(0))
                        .addDay(cursor.getString(2))
                        .addWeight(Integer.parseInt(cursor.getString(3)))
                        .build();
                weightChangeCalendarList.add(weightChangeCalendar);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return weightChangeCalendarList;
    }

    public List<WeightChangeCalendar> getAllWeightChangeCalendarOfUser(int user_id) {
        List<WeightChangeCalendar> weightChangeCalendarList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_WEIGHT_CHANGE_CALENDAR + " WHERE "
                + KEY_WEIGHT_CHANGE_CALENDAR_ID_USER + "=" + user_id + " ORDER BY " + KEY_WEIGHT_CHANGE_CALENDAR_DAY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                WeightChangeCalendar weightChangeCalendar = new WeightChangeCalendar.Builder(cursor.getInt(0))
                        .addDay(cursor.getString(2))
                        .addWeight(Integer.parseInt(cursor.getString(3)))
                        .build();
                weightChangeCalendarList.add(weightChangeCalendar);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return weightChangeCalendarList;
    }

    public List<Exercise> getAllExercises() {
        List<Exercise> exerciseList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_EXERCISES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Exercise exercise = new Exercise.Builder(cursor.getInt(0))
                        .addIsActive(Integer.parseInt(cursor.getString(2)))
                        .addName(cursor.getString(3))
                        .addExplanation(cursor.getString(4))
                        .addVolumeDefault(cursor.getString(5))
                        .addPicture(cursor.getString(6)).build();
                exerciseList.add(exercise);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return exerciseList;
    }

    public List<Exercise> getAllExercisesOfUser(int user_id) {
        List<Exercise> exerciseList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_EXERCISES + " WHERE " + KEY_EXERCISE_ID_USER + "="
                + user_id + " ORDER BY " + KEY_EXERCISE_ID;
        ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {

                Exercise exercise = new Exercise.Builder(cursor.getInt(0))
                        .addIsActive(Integer.parseInt(cursor.getString(2)))
                        .addName(cursor.getString(3))
                        .addExplanation(cursor.getString(4))
                        .addVolumeDefault(cursor.getString(5))
                        .addPicture(cursor.getString(6)).build();
                exerciseList.add(exercise);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return exerciseList;
    }

    public List<Exercise> getAllActiveExercises() {
        List<Exercise> exerciseList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_EXERCISES + " WHERE " + KEY_EXERCISE_IS_ACTIVE + " = 1" + " ORDER BY " + KEY_EXERCISE_ID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                Exercise exercise = new Exercise.Builder(cursor.getInt(0))
                        .addIsActive(Integer.parseInt(cursor.getString(2)))
                        .addName(cursor.getString(3))
                        .addExplanation(cursor.getString(4))
                        .addVolumeDefault(cursor.getString(5))
                        .addPicture(cursor.getString(6)).build();
                exerciseList.add(exercise);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return exerciseList;
    }

    public List<Exercise> getAllActiveExercisesOfUser(int user_id) {
        List<Exercise> exerciseList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_EXERCISES + " WHERE " + KEY_EXERCISE_IS_ACTIVE + " = 1 AND "
                + KEY_EXERCISE_ID_USER + "=" + user_id + " ORDER BY " + KEY_EXERCISE_ID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                Exercise exercise = new Exercise.Builder(cursor.getInt(0))
                        .addIsActive(Integer.parseInt(cursor.getString(2)))
                        .addName(cursor.getString(3))
                        .addExplanation(cursor.getString(4))
                        .addVolumeDefault(cursor.getString(5))
                        .addPicture(cursor.getString(6)).build();
                exerciseList.add(exercise);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return exerciseList;
    }

    public List<Exercise> getExercisesByDates(String mDateFrom, String mDateTo) {

        mDateFrom = "".equals(mDateFrom) ? "0000-00-00" : mDateFrom;
        mDateTo = "".equals(mDateTo) ? "9999-99-99" : mDateTo;
        List<Exercise> exerciseList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT " + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_EXERCISE + "," + TABLE_EXERCISES + "." + KEY_EXERCISE_NAME + "," + TABLE_EXERCISES + "." + KEY_EXERCISE_VOLUME_DEFAULT + " FROM "
                + TABLE_TRAININGS + "," + TABLE_EXERCISES + "," + TABLE_TRAINING_CONTENT
                + " WHERE " + TABLE_EXERCISES + "." + KEY_EXERCISE_ID + "=" + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_EXERCISE
                + " AND " + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_TRAINING + "=" + TABLE_TRAININGS + "." + KEY_TRAINING_ID
                + " AND " + KEY_TRAINING_DAY + ">= \"" + mDateFrom + "\" AND " + KEY_TRAINING_DAY + "<=\"" + mDateTo
                + "\" GROUP BY (" + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_EXERCISE + ")" + " ORDER BY " + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_EXERCISE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {

                Exercise exercise = new Exercise.Builder(cursor.getInt(0))
                        .addName(cursor.getString(1))
                        .addVolumeDefault(cursor.getString(2))
                        .build();
                exerciseList.add(exercise);

            } while (cursor.moveToNext());
        }

        cursor.close();
        return exerciseList;
    }

    public List<Exercise> getExercisesOfUserByDates(int user_id, String mDateFrom, String mDateTo) {

        mDateFrom = "".equals(mDateFrom) ? "0000-00-00" : mDateFrom;
        mDateTo = "".equals(mDateTo) ? "9999-99-99" : mDateTo;
        List<Exercise> exerciseList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT " + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_EXERCISE + "," + TABLE_EXERCISES + "." + KEY_EXERCISE_NAME + "," + TABLE_EXERCISES + "." + KEY_EXERCISE_VOLUME_DEFAULT + " FROM "
                + TABLE_TRAININGS + "," + TABLE_EXERCISES + "," + TABLE_TRAINING_CONTENT
                + "WHERE " + KEY_EXERCISE_ID_USER + "=" + user_id + " AND "
                + TABLE_EXERCISES + "." + KEY_EXERCISE_ID + "=" + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_EXERCISE
                + " AND " + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_TRAINING + "=" + TABLE_TRAININGS + "." + KEY_TRAINING_ID
                + " AND " + KEY_TRAINING_DAY + ">= \"" + mDateFrom + "\" AND " + KEY_TRAINING_DAY + "<=\"" + mDateTo
                + "\" GROUP BY (" + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_EXERCISE + ")" + " ORDER BY " + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_EXERCISE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                Exercise exercise = new Exercise.Builder(cursor.getInt(0))
                        .addName(cursor.getString(1))
                        .addVolumeDefault(cursor.getString(2))
                        .build();
                exerciseList.add(exercise);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return exerciseList;
    }

    public List<WeightChangeCalendar> getWeightChangeCalendarOfUserByDates(int user_id, String mDateFrom, String mDateTo) {

        mDateFrom = "".equals(mDateFrom) ? "0000-00-00" : mDateFrom;
        mDateTo = "".equals(mDateTo) ? "9999-99-99" : mDateTo;
        List<WeightChangeCalendar> weightChangeCalendarList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT " + TABLE_WEIGHT_CHANGE_CALENDAR + "." + KEY_WEIGHT_CHANGE_CALENDAR_ID + ","
                + TABLE_WEIGHT_CHANGE_CALENDAR + "." + KEY_WEIGHT_CHANGE_CALENDAR_ID_USER + ","
                + TABLE_WEIGHT_CHANGE_CALENDAR + "." + KEY_WEIGHT_CHANGE_CALENDAR_DAY + ","
                + TABLE_WEIGHT_CHANGE_CALENDAR + "." + KEY_WEIGHT_CHANGE_CALENDAR_WEIGHT
                + " FROM "+ TABLE_WEIGHT_CHANGE_CALENDAR
                + "WHERE " + KEY_WEIGHT_CHANGE_CALENDAR_ID_USER + "=" + user_id + " AND "
                + TABLE_WEIGHT_CHANGE_CALENDAR + "." + KEY_WEIGHT_CHANGE_CALENDAR_DAY + ">= \"" + mDateFrom + "\" AND "
                + TABLE_WEIGHT_CHANGE_CALENDAR + "." + KEY_WEIGHT_CHANGE_CALENDAR_DAY + "<=\"" + mDateTo
                + " ORDER BY "
                + TABLE_WEIGHT_CHANGE_CALENDAR + "." + KEY_WEIGHT_CHANGE_CALENDAR_DAY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                WeightChangeCalendar weightChangeCalendar = new WeightChangeCalendar.Builder(cursor.getInt(0))
                        .addDay(cursor.getString(2))
                        .addWeight(Integer.parseInt(cursor.getString(3)))
                        .build();
                weightChangeCalendarList.add(weightChangeCalendar);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return weightChangeCalendarList;
    }

    public List<Practice> getAllTrainings() {
        List<Practice> trainingsList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TRAININGS + " ORDER BY " + KEY_TRAINING_DAY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                Practice practice = new Practice.Builder(cursor.getInt(0))
                        .addDay(cursor.getString(2))
                        .build();
                trainingsList.add(practice);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return trainingsList;
    }

    public List<Practice> getAllTrainingsOfUser(int user_id) {
        List<Practice> trainingsList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_TRAININGS + " WHERE " + KEY_TRAINING_ID_USER + "=" + user_id + " ORDER BY " + KEY_TRAINING_DAY;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                Practice practice = new Practice.Builder(cursor.getInt(0))
                        .addDay(cursor.getString(2))
                        .build();
                trainingsList.add(practice);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return trainingsList;
    }

    public List<Practice> getTrainingsByDates(String mDateFrom, String mDateTo) {

        mDateFrom = "".equals(mDateFrom) ? "0000-00-00" : mDateFrom;
        mDateTo = "".equals(mDateTo) ? "9999-99-99" : mDateTo;

        List<Practice> trainingsList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  " + TABLE_TRAININGS + "." + KEY_TRAINING_ID + "," + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + " FROM " + TABLE_TRAININGS + " WHERE "
                + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + ">= \"" + mDateFrom + "\" AND " + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + "<=\"" + mDateTo
                + "\" ORDER BY " + KEY_TRAINING_DAY;


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                Practice practice = new Practice.Builder(cursor.getInt(0))
                        .addDay(cursor.getString(1))
                        .build();
                trainingsList.add(practice);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return trainingsList;
    }

    public List<Practice> getTrainingsOfUserByDates(int user_id, String mDateFrom, String mDateTo) {

        mDateFrom = "".equals(mDateFrom) ? "0000-00-00" : mDateFrom;
        mDateTo = "".equals(mDateTo) ? "9999-99-99" : mDateTo;

        List<Practice> trainingsList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  " + TABLE_TRAININGS + "." + KEY_TRAINING_ID + "," + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + " FROM " + TABLE_TRAININGS + " WHERE "
                + TABLE_TRAININGS + "." + KEY_TRAINING_ID_USER + "=" + user_id
                + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + ">= \"" + mDateFrom + "\" AND " + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + "<=\"" + mDateTo
                + "\" ORDER BY " + KEY_TRAINING_ID;


        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                Practice practice = new Practice.Builder(cursor.getInt(0))
                        .addDay(cursor.getString(1))
                        .build();
                trainingsList.add(practice);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return trainingsList;
    }

    public List<Practice> getLastTrainingsByDates(String mDateTo) {

        mDateTo = "".equals(mDateTo) ? "9999-99-99" : mDateTo;

        List<Practice> trainingsList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  " + TABLE_TRAININGS + "." + KEY_TRAINING_ID + "," + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + " FROM " + TABLE_TRAININGS + " WHERE "
                + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + " IN (SELECT MAX(" + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + ") FROM " + TABLE_TRAININGS
                + " WHERE " + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + "<\"" + mDateTo + "\" ) ORDER BY " + KEY_TRAINING_ID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Practice practice = new Practice.Builder(cursor.getInt(0))
                        .addDay(cursor.getString(1))
                        .build();
                trainingsList.add(practice);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return trainingsList;
    }

    public List<Practice> getLastTrainingsOfUserByDates(int user_id, String mDateTo) {

        mDateTo = "".equals(mDateTo) ? "9999-99-99" : mDateTo;

        List<Practice> trainingsList = new ArrayList<>();
        String selectQuery = "SELECT  " + TABLE_TRAININGS + "." + KEY_TRAINING_ID + "," + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + " FROM " + TABLE_TRAININGS + " WHERE "
                + TABLE_TRAININGS + "." + KEY_TRAINING_ID_USER + "=" + user_id
                + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + " IN (SELECT MAX(" + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + ") FROM " + TABLE_TRAININGS
                + " WHERE " + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + "<\"" + mDateTo + "\" ) ORDER BY " + KEY_TRAINING_ID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Practice practice = new Practice.Builder(cursor.getInt(0))
                        .addDay(cursor.getString(1))
                        .build();

                trainingsList.add(practice);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return trainingsList;
    }

    public List<TrainingContent> getLastExerciseNotNullVolume(String mDateTo, int exercise_id) {

        mDateTo = "".equals(mDateTo) ? "9999-99-99" : mDateTo;

        List<TrainingContent> trainingsContentList = new ArrayList<>();
        String selectQuery = "SELECT " + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + "," + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID + ","
                + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_VOLUME + "," + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_WEIGHT
                + " FROM (SELECT " + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_TRAINING + "," + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID
                + "," + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_VOLUME + "," + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_WEIGHT + " FROM "
                + TABLE_TRAINING_CONTENT + " WHERE " + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_VOLUME + " <>\"\" AND "
                + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_VOLUME + " <>\"0\" AND "
                + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_EXERCISE + " = " + exercise_id + ") AS " + TABLE_TRAINING_CONTENT
                + " LEFT JOIN (SELECT " + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + "," + TABLE_TRAININGS + "." + KEY_TRAINING_ID + " FROM "
                + TABLE_TRAININGS + " ) AS " + TABLE_TRAININGS
                + " ON " + TABLE_TRAININGS + "." + KEY_TRAINING_ID + "=" + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_TRAINING
                + " WHERE " + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + "<\"" + mDateTo + "\"" + " ORDER BY " + KEY_TRAINING_DAY + " desc limit 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(1) != 0 & cursor.getString(2) != null) {
                    TrainingContent trainingContent = new TrainingContent.Builder(cursor.getInt(1))
                            .addVolume(cursor.getString(2))
                            .addWeight(cursor.getInt(3))
                            .build();

                    trainingsContentList.add(trainingContent);
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        return trainingsContentList;
    }

    public List<TrainingContent> getLastExerciseNotNullVolumeAndWeightOfUser(int user_id, String mDateTo, int exercise_id) {

        mDateTo = "".equals(mDateTo) ? "9999-99-99" : mDateTo;

        List<TrainingContent> trainingsContentList = new ArrayList<>();
        String selectQuery = "SELECT " + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + "," + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID + ","
                + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_VOLUME + "," + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_WEIGHT
                + " FROM (SELECT " + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_TRAINING + "," + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID
                + "," + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_VOLUME + "," + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_WEIGHT + " FROM "
                + TABLE_TRAINING_CONTENT + " WHERE " + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_VOLUME + " <>\"\" AND "
                + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_VOLUME + " <>\"0\" AND "
                + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_EXERCISE + " = " + exercise_id + ") AS " + TABLE_TRAINING_CONTENT
                + " LEFT JOIN (SELECT " + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + "," + TABLE_TRAININGS + "." + KEY_TRAINING_ID + " FROM "
                + TABLE_TRAININGS + " WHERE " + TABLE_TRAININGS + "." + KEY_TRAINING_ID_USER + "=" + user_id + ") AS " + TABLE_TRAININGS
                + " ON " + TABLE_TRAININGS + "." + KEY_TRAINING_ID + "=" + TABLE_TRAINING_CONTENT + "." + KEY_TRAINING_CONTENT_ID_TRAINING
                + " WHERE " + TABLE_TRAININGS + "." + KEY_TRAINING_DAY + "<\"" + mDateTo + "\"" + " ORDER BY " + KEY_TRAINING_DAY + " desc limit 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(1) != 0 & cursor.getString(2) != null) {
                    TrainingContent trainingContent = new TrainingContent.Builder(cursor.getInt(1))
                            .addVolume(cursor.getString(2))
                            .addWeight(cursor.getInt(3))
                            .build();

                    trainingsContentList.add(trainingContent);
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return trainingsContentList;
    }

        public List<WeightChangeCalendar> getWeightOfUserFromWeightCalendar(int user_id, String mDateTo) {

            mDateTo = "".equals(mDateTo) ? "9999-99-99" : mDateTo;

            List<WeightChangeCalendar> weightChangeCalendarList = new ArrayList<>();
            String selectQuery = "SELECT " + TABLE_WEIGHT_CHANGE_CALENDAR + "." + KEY_WEIGHT_CHANGE_CALENDAR_ID + ","
            +TABLE_WEIGHT_CHANGE_CALENDAR + "." + KEY_WEIGHT_CHANGE_CALENDAR_DAY + ","
                    + TABLE_WEIGHT_CHANGE_CALENDAR + "." + KEY_WEIGHT_CHANGE_CALENDAR_WEIGHT
                    + " FROM " + TABLE_WEIGHT_CHANGE_CALENDAR  + " WHERE "
                    + TABLE_WEIGHT_CHANGE_CALENDAR + "." + KEY_WEIGHT_CHANGE_CALENDAR_ID_USER + " =" + user_id
                    + " AND " + TABLE_WEIGHT_CHANGE_CALENDAR + "." + KEY_WEIGHT_CHANGE_CALENDAR_DAY + "<=\"" + mDateTo + "\"" + " ORDER BY " + KEY_WEIGHT_CHANGE_CALENDAR_DAY + " desc limit 1";

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    if (cursor.getInt(2) != 0 & cursor.getString(1) != null) {

                        WeightChangeCalendar weightChangeCalendar=new WeightChangeCalendar.Builder(cursor.getInt(0))
                                .addDay(cursor.getString(1)).addWeight(cursor.getInt(2)).build();
                        weightChangeCalendarList.add(weightChangeCalendar);
                    }
                } while (cursor.moveToNext());
            }

            cursor.close();
            return weightChangeCalendarList;
        }



    public List<TrainingContent> getAllTrainingContentOfTraining(int training_id) {
        List<TrainingContent> trainingContentList = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_TRAINING_CONTENT + " WHERE " + KEY_TRAINING_CONTENT_ID_TRAINING
                + "=" + training_id + " ORDER BY " + KEY_TRAINING_CONTENT_ID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                TrainingContent trainingContent = new TrainingContent.Builder(cursor.getInt(0))
                        .addExerciseId(cursor.getInt(2))
                        .addTrainingId(cursor.getInt(3))
                        .addVolume(cursor.getString(4))
                        .addWeight(cursor.getInt(5))
                        .addComment(cursor.getString(6))
                        .build();

                trainingContentList.add(trainingContent);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return trainingContentList;
    }

    public int getUsersCount() {
        String countQuery = "SELECT  * FROM " + TABLE_USERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int max = cursor.getCount();
        cursor.close();

        // return count
        return max;
    }

    public int getUserMaxNumber() {
        String countQuery = "SELECT  MAX(" + KEY_USER_ID + ") FROM " + TABLE_USERS + "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            return cursor.getInt(0);
        } else {
            cursor.close();
            return 0;
        }

    }

    public int getWeightChangeCalendarMaxNumber() {
        String countQuery = "SELECT  MAX(" + KEY_WEIGHT_CHANGE_CALENDAR_ID + ") FROM " + TABLE_WEIGHT_CHANGE_CALENDAR + "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            return cursor.getInt(0);
        } else {
            cursor.close();
            return 0;
        }

    }

    public int getExerciseMaxNumber() {
        String countQuery = "SELECT  MAX(" + KEY_EXERCISE_ID + ") FROM " + TABLE_EXERCISES + "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            return cursor.getInt(0);
        } else {
            cursor.close();
            return 0;
        }

    }

    public int getTrainingMaxNumber() {
        String countQuery = "SELECT  MAX(" + KEY_TRAINING_ID + ") FROM " + TABLE_TRAININGS + "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            return cursor.getInt(0);
        } else {
            cursor.close();
            return 0;
        }

    }

    public int getTrainingContentMaxNumber() {
        String countQuery = "SELECT  MAX(" + KEY_TRAINING_CONTENT_ID + ") FROM " + TABLE_TRAINING_CONTENT + "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            return cursor.getInt(0);
        } else {
            cursor.close();
            return 0;
        }

    }

    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, user.getName());
        values.put(KEY_USER_IS_CURRENT, user.isCurrentUser());
        return db.update(TABLE_USERS, values, KEY_USER_ID + " = ?",
                new String[]{String.valueOf(user.getID())});
    }

    public int updateWeightChangeCalendar(WeightChangeCalendar weightChangeCalendar) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_WEIGHT_CHANGE_CALENDAR_DAY, weightChangeCalendar.getDayString());
        values.put(KEY_WEIGHT_CHANGE_CALENDAR_ID_USER, weightChangeCalendar.getIdUser());
        values.put(KEY_WEIGHT_CHANGE_CALENDAR_WEIGHT, weightChangeCalendar.getWeight());

        // updating row
        return db.update(TABLE_WEIGHT_CHANGE_CALENDAR, values, KEY_WEIGHT_CHANGE_CALENDAR_ID + " = ?",
                new String[]{String.valueOf(weightChangeCalendar.getID())});
    }

    public int updateExercise(Exercise exercise) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EXERCISE_IS_ACTIVE, exercise.getIsActive());
        values.put(KEY_EXERCISE_ID_USER, exercise.getIdUser());
        values.put(KEY_EXERCISE_NAME, exercise.getName());
        values.put(KEY_EXERCISE_EXPLANATION, exercise.getExplanation());
        values.put(KEY_EXERCISE_VOLUME_DEFAULT, exercise.getVolumeDefault());
        values.put(KEY_EXERCISE_PICTURE_NAME, exercise.getPicture());

        // updating row
        return db.update(TABLE_EXERCISES, values, KEY_EXERCISE_ID + " = ?",
                new String[]{String.valueOf(exercise.getID())});
    }

    public int updateTraining(Practice practice) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        String sDate = "";
        if (practice.getDay() != null) {
            sDate = dateformat.format(practice.getDay());
        }
        values.put(KEY_TRAINING_ID_USER, practice.getIdUser());
        values.put(KEY_TRAINING_DAY, sDate);

        // updating row
        return db.update(TABLE_TRAININGS, values, KEY_TRAINING_ID + " = ?",
                new String[]{String.valueOf(practice.getID())});
    }

    public int updateTrainingContent(TrainingContent trainingContent) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TRAINING_CONTENT_ID_USER, trainingContent.getIdUser());
        values.put(KEY_TRAINING_CONTENT_ID_EXERCISE, trainingContent.getIdExercise());
        values.put(KEY_TRAINING_CONTENT_ID_TRAINING, trainingContent.getIdTraining());
        values.put(KEY_TRAINING_CONTENT_VOLUME, trainingContent.getVolume());
        values.put(KEY_TRAINING_CONTENT_WEIGHT, trainingContent.getWeight());
        values.put(KEY_TRAINING_CONTENT_COMMENT, trainingContent.getComment());

        // updating row
        return db.update(TABLE_TRAINING_CONTENT, values, KEY_TRAINING_CONTENT_ID + " = ?",
                new String[]{String.valueOf(trainingContent.getID())});
    }

    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, KEY_USER_ID + " = ?",
                new String[]{String.valueOf(user.getID())});
        db.close();
    }

    public void deleteWeightChangeCalendar(WeightChangeCalendar weightChangeCalendar) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WEIGHT_CHANGE_CALENDAR, KEY_WEIGHT_CHANGE_CALENDAR_ID + " = ?",
                new String[]{String.valueOf(weightChangeCalendar.getID())});
        db.close();
    }

    public void deleteExercise(Exercise exercise) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXERCISES, KEY_EXERCISE_ID + " = ?",
                new String[]{String.valueOf(exercise.getID())});
        db.close();
    }

    public void deleteTraining(Practice practice) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRAININGS, KEY_TRAINING_ID + " = ?",
                new String[]{String.valueOf(practice.getID())});
        db.close();
    }

    public void deleteTrainingContent(TrainingContent trainingContent) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRAINING_CONTENT, KEY_TRAINING_CONTENT_ID + " = ?",
                new String[]{String.valueOf(trainingContent.getID())});
        db.close();
    }

    public void deleteTrainingContentOfTraining(int id_traning) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TRAINING_CONTENT, KEY_TRAINING_CONTENT_ID_TRAINING + " = ?",
                new String[]{String.valueOf(id_traning)});
        db.close();
    }


    public ArrayList<Cursor> getData(String Query) {
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[]{"mesage"};
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<>(2);
        MatrixCursor Cursor2 = new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);


        try {
            String maxQuery = Query;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);


            //add value to cursor2
            Cursor2.addRow(new Object[]{"Success"});

            alc.set(1, Cursor2);
            if (null != c && c.getCount() > 0) {


                alc.set(0, c);
                c.moveToFirst();

                return alc;
            }
            return alc;
        } catch (Exception sqlEx) {
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[]{"" + sqlEx.getMessage()});
            alc.set(1, Cursor2);
            return alc;

        }


    }
}
