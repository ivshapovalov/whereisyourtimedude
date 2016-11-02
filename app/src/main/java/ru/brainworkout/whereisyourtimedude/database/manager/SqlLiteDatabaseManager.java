package ru.brainworkout.whereisyourtimedude.database.manager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ru.brainworkout.whereisyourtimedude.database.entities.Area;
import ru.brainworkout.whereisyourtimedude.database.entities.DetailedPracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.entities.Options;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.PracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.entities.Project;
import ru.brainworkout.whereisyourtimedude.database.entities.User;

public class SqlLiteDatabaseManager extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "wiytd";

    // Tables names
    private static final String TABLE_OPTIONS = "options";
    private static final String TABLE_USERS = "users";
    private static final String TABLE_PRACTICES = "practices";
    private static final String TABLE_PROJECTS = "projects";
    private static final String TABLE_AREAS = "areas";
    private static final String TABLE_PRACTICE_HISTORY = "practice_history";
    private static final String TABLE_DETAILED_PRACTICE_HISTORY = "detailed_practice_history";

    //options
    private static final String KEY_OPTIONS_ID = "options_id";
    private static final String KEY_OPTIONS_ID_USER = "options_id_user";
    private static final String KEY_OPTIONS_RECOVERY_ON_RUN = "options_recovery_on_run";
    private static final String KEY_OPTIONS_DISPLAY_NOTIFICATION_TIMER = "options_display_notification_timer";
    private static final String KEY_OPTIONS_SAVE_INTERVAL = "options_save_interval";
    private static final String KEY_OPTIONS_CHRONO_IS_WORKING = "options_chrono_is_working";

    // Practice
    private static final String KEY_PRACTICE_ID = "practice_id";
    private static final String KEY_PRACTICE_ID_USER = "practice_id_user";
    private static final String KEY_PRACTICE_IS_ACTIVE = "practice_is_active";
    private static final String KEY_PRACTICE_NAME = "practice_name";
    private static final String KEY_PRACTICE_ID_PROJECT = "practice_id_project";

    //  Area
    private static final String KEY_AREA_ID = "area_id";
    private static final String KEY_AREA_ID_USER = "area_id_user";
    private static final String KEY_AREA_NAME = "area_name";
    private static final String KEY_AREA_COLOR = "area_color";

    //  Project
    private static final String KEY_PROJECT_ID = "project_id";
    private static final String KEY_PROJECT_ID_USER = "project_id_user";
    private static final String KEY_PROJECT_ID_AREA = "project_id_area";
    private static final String KEY_PROJECT_NAME = "project_name";

    //  Practice history
    private static final String KEY_PRACTICE_HISTORY_ID = "practice_history_id";
    private static final String KEY_PRACTICE_HISTORY_ID_USER = "practice_history_id_user";
    private static final String KEY_PRACTICE_HISTORY_ID_PRACTICE = "practice_history_id_practice";
    private static final String KEY_PRACTICE_HISTORY_DATE = "practice_history_date";
    private static final String KEY_PRACTICE_HISTORY_DURATION = "practice_history_duration";
    private static final String KEY_PRACTICE_HISTORY_LAST_TIME = "practice_history_last_time";

    //  Detailed practice history
    private static final String KEY_DETAILED_PRACTICE_HISTORY_ID = "detailed_practice_history_id";
    private static final String KEY_DETAILED_PRACTICE_HISTORY_ID_USER = "detailed_practice_history_id_user";
    private static final String KEY_DETAILED_PRACTICE_HISTORY_ID_PRACTICE = "detailed_practice_history_id_practice";
    private static final String KEY_DETAILED_PRACTICE_HISTORY_DATE = "detailed_practice_history_date";
    private static final String KEY_DETAILED_PRACTICE_HISTORY_DURATION = "detailed_practice_history_duration";
    private static final String KEY_DETAILED_PRACTICE_HISTORY_TIME = "detailed_practice_history_last_time";

    //  Users AbstractEntity
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_IS_CURRENT = "user_is_current";

    public SqlLiteDatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public synchronized void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 1 && newVersion == 2) {
            //TODO
            //update1To2(db);

        } else {

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_OPTIONS);

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_AREAS);

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRACTICES);

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRACTICE_HISTORY);

            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DETAILED_PRACTICE_HISTORY);

            onCreate(db);
        }

    }

    public synchronized void update1To2(SQLiteDatabase db) {
        //detailed practice history
        String CREATE_DETAILED_PRACTICE_HISTORY_TABLE = "CREATE TABLE " + TABLE_DETAILED_PRACTICE_HISTORY + "("
                + KEY_DETAILED_PRACTICE_HISTORY_ID + " INTEGER UNIQUE PRIMARY KEY NOT NULL,"
                + KEY_DETAILED_PRACTICE_HISTORY_ID_USER + " INTEGER, "
                + KEY_DETAILED_PRACTICE_HISTORY_ID_PRACTICE + " INTEGER,"
                + KEY_DETAILED_PRACTICE_HISTORY_DATE + " INTEGER,"
                + KEY_DETAILED_PRACTICE_HISTORY_DURATION + " INTEGER,"
                + KEY_DETAILED_PRACTICE_HISTORY_TIME + " INTEGER, "
                + "FOREIGN KEY(" + KEY_DETAILED_PRACTICE_HISTORY_ID_PRACTICE + ") REFERENCES " + TABLE_PRACTICES + "(" + KEY_PRACTICE_ID + "),"
                + "FOREIGN KEY(" + KEY_DETAILED_PRACTICE_HISTORY_ID_USER + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + ")"
                + ")";
        db.execSQL(CREATE_DETAILED_PRACTICE_HISTORY_TABLE);

        String CREATE_DETAILED_PRACTICE_HISTORY_INDEX_PRACTICE_ASC = "CREATE INDEX DETAILED_PRACTICE_HISTORY_PRACTICE_IDX_ASC ON " + TABLE_DETAILED_PRACTICE_HISTORY + " (" + KEY_DETAILED_PRACTICE_HISTORY_ID_PRACTICE + " ASC)";
        db.execSQL(CREATE_DETAILED_PRACTICE_HISTORY_INDEX_PRACTICE_ASC);
        String CREATE_DETAILED_PRACTICE_HISTORY_INDEX_PRACTICE_DESC = "CREATE INDEX DETAILED_PRACTICE_HISTORY_PRACTICE_IDX_DESC ON " + TABLE_DETAILED_PRACTICE_HISTORY + " (" + KEY_DETAILED_PRACTICE_HISTORY_ID_PRACTICE + " DESC)";
        db.execSQL(CREATE_DETAILED_PRACTICE_HISTORY_INDEX_PRACTICE_DESC);

        String CREATE_DETAILED_PRACTICE_HISTORY_INDEX_USER_ASC = "CREATE INDEX DETAILED_PRACTICE_HISTORY_USER_IDX_ASC ON " + TABLE_DETAILED_PRACTICE_HISTORY + " (" + KEY_DETAILED_PRACTICE_HISTORY_ID_USER + " ASC)";
        db.execSQL(CREATE_DETAILED_PRACTICE_HISTORY_INDEX_USER_ASC);
        String CREATE_DETAILED_PRACTICE_HISTORY_INDEX_USER_DESC = "CREATE INDEX DETAILED_PRACTICE_HISTORY_USER_IDX_DESC ON " + TABLE_DETAILED_PRACTICE_HISTORY + " (" + KEY_DETAILED_PRACTICE_HISTORY_ID_USER + " DESC)";
        db.execSQL(CREATE_DETAILED_PRACTICE_HISTORY_INDEX_USER_DESC);

        String CREATE_DETAILED_PRACTICE_HISTORY_INDEX_DATE_ASC = "CREATE INDEX DETAILED_PRACTICE_HISTORY_DATE_IDX_ASC ON " + TABLE_DETAILED_PRACTICE_HISTORY + " (" + KEY_DETAILED_PRACTICE_HISTORY_DATE + " ASC)";
        db.execSQL(CREATE_DETAILED_PRACTICE_HISTORY_INDEX_DATE_ASC);
        String CREATE_DETAILED_PRACTICE_HISTORY_INDEX_DATE_DESC = "CREATE INDEX DETAILED_PRACTICE_HISTORY_DATE_IDX_DESC ON " + TABLE_DETAILED_PRACTICE_HISTORY + " (" + KEY_DETAILED_PRACTICE_HISTORY_DATE + " DESC)";
        db.execSQL(CREATE_DETAILED_PRACTICE_HISTORY_INDEX_DATE_DESC);

        String CREATE_DETAILED_PRACTICE_HISTORY_INDEX_LAST_TIME_ASC = "CREATE INDEX DETAILED_PRACTICE_HISTORY_LAST_TIME_IDX_ASC ON " + TABLE_DETAILED_PRACTICE_HISTORY + " (" + KEY_DETAILED_PRACTICE_HISTORY_TIME + " ASC)";
        db.execSQL(CREATE_DETAILED_PRACTICE_HISTORY_INDEX_LAST_TIME_ASC);
        String CREATE_DETAILED_PRACTICE_HISTORY_INDEX_LAST_TIME_DESC = "CREATE INDEX DETAILED_PRACTICE_HISTORY_LAST_TIME_IDX_DESC ON " + TABLE_DETAILED_PRACTICE_HISTORY + " (" + KEY_DETAILED_PRACTICE_HISTORY_TIME + " DESC)";
        db.execSQL(CREATE_DETAILED_PRACTICE_HISTORY_INDEX_LAST_TIME_DESC);

        String CREATE_DETAILED_PRACTICE_HISTORY_INDEX_USER_AND_PRACTICE_ASC = "CREATE INDEX DETAILED_PRACTICE_HISTORY_USER_AND_PRACTICE_IDX_ASC ON " + TABLE_DETAILED_PRACTICE_HISTORY + " (" + KEY_DETAILED_PRACTICE_HISTORY_ID_USER + " ASC, " + KEY_DETAILED_PRACTICE_HISTORY_ID_PRACTICE + " ASC)";
        db.execSQL(CREATE_DETAILED_PRACTICE_HISTORY_INDEX_USER_AND_PRACTICE_ASC);
        String CREATE_DETAILED_PRACTICE_HISTORY_INDEX_USER_AND_PRACTICE_DESC = "CREATE INDEX DETAILED_PRACTICE_HISTORY_USER_AND_PRACTICE_IDX_DESC ON " + TABLE_DETAILED_PRACTICE_HISTORY + " (" + KEY_DETAILED_PRACTICE_HISTORY_ID_USER + " DESC, " + KEY_DETAILED_PRACTICE_HISTORY_ID_PRACTICE + " DESC)";
        db.execSQL(CREATE_DETAILED_PRACTICE_HISTORY_INDEX_USER_AND_PRACTICE_DESC);

        String INSERT = "insert into " + TABLE_DETAILED_PRACTICE_HISTORY + "("
                + KEY_DETAILED_PRACTICE_HISTORY_ID + ","
                + KEY_DETAILED_PRACTICE_HISTORY_ID_USER + ","
                + KEY_DETAILED_PRACTICE_HISTORY_ID_PRACTICE + ","
                + KEY_DETAILED_PRACTICE_HISTORY_DATE + ","
                + KEY_DETAILED_PRACTICE_HISTORY_DURATION + ","
                + KEY_DETAILED_PRACTICE_HISTORY_TIME + ""
                + ") select "
                + KEY_PRACTICE_HISTORY_ID + ","
                + KEY_PRACTICE_HISTORY_ID_USER + ","
                + KEY_PRACTICE_HISTORY_ID_PRACTICE + ","
                + KEY_PRACTICE_HISTORY_DATE + ","
                + KEY_PRACTICE_HISTORY_DURATION + ","
                + KEY_PRACTICE_HISTORY_LAST_TIME + " AS " + KEY_DETAILED_PRACTICE_HISTORY_TIME + ""
                + " from " + TABLE_PRACTICE_HISTORY;
        db.execSQL(INSERT);
    }

    @Override
    public synchronized void onCreate(SQLiteDatabase db) {

        //options
        String CREATE_OPTIONS_TABLE = "CREATE TABLE " + TABLE_OPTIONS + "("
                + KEY_OPTIONS_ID + " INTEGER UNIQUE PRIMARY KEY NOT NULL,"
                + KEY_OPTIONS_ID_USER + " INTEGER, "
                + KEY_OPTIONS_RECOVERY_ON_RUN + " INTEGER," + KEY_OPTIONS_DISPLAY_NOTIFICATION_TIMER + " INTEGER,"
                + KEY_OPTIONS_SAVE_INTERVAL + " INTEGER," + KEY_OPTIONS_CHRONO_IS_WORKING + " INTEGER" + ")";
        db.execSQL(CREATE_OPTIONS_TABLE);

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
                + ")";
        db.execSQL(CREATE_AREAS_TABLE);


        String CREATE_AREAS_INDEX_USER_ASC = "CREATE INDEX AREAS_USER_IDX_ASC ON " + TABLE_AREAS + " (" + KEY_AREA_ID_USER + " ASC)";
        db.execSQL(CREATE_AREAS_INDEX_USER_ASC);
        String CREATE_AREAS_INDEX_USER_DESC = "CREATE INDEX AREAS_USER_IDX_DESC ON " + TABLE_AREAS + " (" + KEY_AREA_ID_USER + " DESC)";
        db.execSQL(CREATE_AREAS_INDEX_USER_DESC);

        //projects
        String CREATE_PROJECTS_TABLE = "CREATE TABLE " + TABLE_PROJECTS + "("
                + KEY_PROJECT_ID + " INTEGER UNIQUE PRIMARY KEY NOT NULL,"
                + KEY_PROJECT_ID_USER + " INTEGER, "
                + KEY_PROJECT_NAME + " TEXT," + KEY_PROJECT_ID_AREA + " INTEGER,"
                + " FOREIGN KEY(" + KEY_PROJECT_ID_USER + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + ")" + ","
                + " FOREIGN KEY(" + KEY_PROJECT_ID_AREA + ") REFERENCES " + TABLE_AREAS + "(" + KEY_AREA_ID + ")"
                + ")";
        db.execSQL(CREATE_PROJECTS_TABLE);


        String CREATE_PROJECTS_INDEX_USER_ASC = "CREATE INDEX PROJECTS_USER_IDX_ASC ON " + TABLE_PROJECTS + " (" + KEY_PROJECT_ID_USER + " ASC)";
        db.execSQL(CREATE_PROJECTS_INDEX_USER_ASC);
        String CREATE_PROJECTS_INDEX_USER_DESC = "CREATE INDEX PROJECTS_USER_IDX_DESC ON " + TABLE_PROJECTS + " (" + KEY_PROJECT_ID_USER + " DESC)";
        db.execSQL(CREATE_PROJECTS_INDEX_USER_DESC);

        String CREATE_PROJECTS_INDEX_AREA_ASC = "CREATE INDEX PROJECTS_AREA_IDX_ASC ON " + TABLE_PROJECTS + " (" + KEY_PROJECT_ID_AREA + " ASC)";
        db.execSQL(CREATE_PROJECTS_INDEX_AREA_ASC);
        String CREATE_PROJECTS_INDEX_AREA_DESC = "CREATE INDEX PROJECTS_AREA_IDX_DESC ON " + TABLE_PROJECTS + " (" + KEY_PROJECT_ID_AREA + " DESC)";
        db.execSQL(CREATE_PROJECTS_INDEX_AREA_DESC);

        //practises
        String CREATE_PRACTICES_TABLE = "CREATE TABLE " + TABLE_PRACTICES + "("
                + KEY_PRACTICE_ID + " INTEGER UNIQUE PRIMARY KEY NOT NULL,"
                + KEY_PRACTICE_ID_USER + " INTEGER, "
                + KEY_PRACTICE_ID_PROJECT + " INTEGER, "
                + KEY_PRACTICE_IS_ACTIVE + " INTEGER, "
                + KEY_PRACTICE_NAME + " TEXT,"
                + " FOREIGN KEY(" + KEY_PRACTICE_ID_USER + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + ")" + ","
                + " FOREIGN KEY(" + KEY_PRACTICE_ID_PROJECT + ") REFERENCES " + TABLE_PROJECTS + "(" + KEY_PROJECT_ID + ")"
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

        //practice history
        String CREATE_PRACTICE_HISTORY_TABLE = "CREATE TABLE " + TABLE_PRACTICE_HISTORY + "("
                + KEY_PRACTICE_HISTORY_ID + " INTEGER UNIQUE PRIMARY KEY NOT NULL,"
                + KEY_PRACTICE_HISTORY_ID_USER + " INTEGER, "
                + KEY_PRACTICE_HISTORY_ID_PRACTICE + " INTEGER,"
                + KEY_PRACTICE_HISTORY_DATE + " INTEGER,"
                + KEY_PRACTICE_HISTORY_DURATION + " INTEGER,"
                + KEY_PRACTICE_HISTORY_LAST_TIME + " INTEGER, "
                + "FOREIGN KEY(" + KEY_PRACTICE_HISTORY_ID_PRACTICE + ") REFERENCES " + TABLE_PRACTICES + "(" + KEY_PRACTICE_ID + "),"
                + "FOREIGN KEY(" + KEY_PRACTICE_HISTORY_ID_USER + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + ")"
                + ")";
        db.execSQL(CREATE_PRACTICE_HISTORY_TABLE);

        String CREATE_PRACTICE_HISTORY_INDEX_PRACTICE_ASC = "CREATE INDEX PRACTICE_HISTORY_PRACTICE_IDX_ASC ON " + TABLE_PRACTICE_HISTORY + " (" + KEY_PRACTICE_HISTORY_ID_PRACTICE + " ASC)";
        db.execSQL(CREATE_PRACTICE_HISTORY_INDEX_PRACTICE_ASC);
        String CREATE_PRACTICE_HISTORY_INDEX_PRACTICE_DESC = "CREATE INDEX PRACTICE_HISTORY_PRACTICE_IDX_DESC ON " + TABLE_PRACTICE_HISTORY + " (" + KEY_PRACTICE_HISTORY_ID_PRACTICE + " DESC)";
        db.execSQL(CREATE_PRACTICE_HISTORY_INDEX_PRACTICE_DESC);

        String CREATE_PRACTICE_HISTORY_INDEX_USER_ASC = "CREATE INDEX PRACTICE_HISTORY_USER_IDX_ASC ON " + TABLE_PRACTICE_HISTORY + " (" + KEY_PRACTICE_HISTORY_ID_USER + " ASC)";
        db.execSQL(CREATE_PRACTICE_HISTORY_INDEX_USER_ASC);
        String CREATE_PRACTICE_HISTORY_INDEX_USER_DESC = "CREATE INDEX PRACTICE_HISTORY_USER_IDX_DESC ON " + TABLE_PRACTICE_HISTORY + " (" + KEY_PRACTICE_HISTORY_ID_USER + " DESC)";
        db.execSQL(CREATE_PRACTICE_HISTORY_INDEX_USER_DESC);

        String CREATE_PRACTICE_HISTORY_INDEX_DATE_ASC = "CREATE INDEX PRACTICE_HISTORY_DATE_IDX_ASC ON " + TABLE_PRACTICE_HISTORY + " (" + KEY_PRACTICE_HISTORY_DATE + " ASC)";
        db.execSQL(CREATE_PRACTICE_HISTORY_INDEX_DATE_ASC);
        String CREATE_PRACTICE_HISTORY_INDEX_DATE_DESC = "CREATE INDEX PRACTICE_HISTORY_DATE_IDX_DESC ON " + TABLE_PRACTICE_HISTORY + " (" + KEY_PRACTICE_HISTORY_DATE + " DESC)";
        db.execSQL(CREATE_PRACTICE_HISTORY_INDEX_DATE_DESC);

        String CREATE_PRACTICE_HISTORY_INDEX_LAST_TIME_ASC = "CREATE INDEX PRACTICE_HISTORY_LAST_TIME_IDX_ASC ON " + TABLE_PRACTICE_HISTORY + " (" + KEY_PRACTICE_HISTORY_LAST_TIME + " ASC)";
        db.execSQL(CREATE_PRACTICE_HISTORY_INDEX_LAST_TIME_ASC);
        String CREATE_PRACTICE_HISTORY_INDEX_LAST_TIME_DESC = "CREATE INDEX PRACTICE_HISTORY_LAST_TIME_IDX_DESC ON " + TABLE_PRACTICE_HISTORY + " (" + KEY_PRACTICE_HISTORY_LAST_TIME + " DESC)";
        db.execSQL(CREATE_PRACTICE_HISTORY_INDEX_LAST_TIME_DESC);

        String CREATE_PRACTICE_HISTORY_INDEX_USER_AND_PRACTICE_ASC = "CREATE INDEX PRACTICE_HISTORY_USER_AND_PRACTICE_IDX_ASC ON " + TABLE_PRACTICE_HISTORY + " (" + KEY_PRACTICE_HISTORY_ID_USER + " ASC, " + KEY_PRACTICE_HISTORY_ID_PRACTICE + " ASC)";
        db.execSQL(CREATE_PRACTICE_HISTORY_INDEX_USER_AND_PRACTICE_ASC);
        String CREATE_PRACTICE_HISTORY_INDEX_USER_AND_PRACTICE_DESC = "CREATE INDEX PRACTICE_HISTORY_USER_AND_PRACTICE_IDX_DESC ON " + TABLE_PRACTICE_HISTORY + " (" + KEY_PRACTICE_HISTORY_ID_USER + " DESC, " + KEY_PRACTICE_HISTORY_ID_PRACTICE + " DESC)";
        db.execSQL(CREATE_PRACTICE_HISTORY_INDEX_USER_AND_PRACTICE_DESC);

        //detailed practice history
        String CREATE_DETAILED_PRACTICE_HISTORY_TABLE = "CREATE TABLE " + TABLE_DETAILED_PRACTICE_HISTORY + "("
                + KEY_DETAILED_PRACTICE_HISTORY_ID + " INTEGER UNIQUE PRIMARY KEY NOT NULL,"
                + KEY_DETAILED_PRACTICE_HISTORY_ID_USER + " INTEGER, "
                + KEY_DETAILED_PRACTICE_HISTORY_ID_PRACTICE + " INTEGER,"
                + KEY_DETAILED_PRACTICE_HISTORY_DATE + " INTEGER,"
                + KEY_DETAILED_PRACTICE_HISTORY_DURATION + " INTEGER,"
                + KEY_DETAILED_PRACTICE_HISTORY_TIME + " INTEGER, "
                + "FOREIGN KEY(" + KEY_DETAILED_PRACTICE_HISTORY_ID_PRACTICE + ") REFERENCES " + TABLE_PRACTICES + "(" + KEY_PRACTICE_ID + "),"
                + "FOREIGN KEY(" + KEY_DETAILED_PRACTICE_HISTORY_ID_USER + ") REFERENCES " + TABLE_USERS + "(" + KEY_USER_ID + ")"
                + ")";
        db.execSQL(CREATE_DETAILED_PRACTICE_HISTORY_TABLE);

        String CREATE_DETAILED_PRACTICE_HISTORY_INDEX_PRACTICE_ASC = "CREATE INDEX DETAILED_PRACTICE_HISTORY_PRACTICE_IDX_ASC ON " + TABLE_DETAILED_PRACTICE_HISTORY + " (" + KEY_DETAILED_PRACTICE_HISTORY_ID_PRACTICE + " ASC)";
        db.execSQL(CREATE_DETAILED_PRACTICE_HISTORY_INDEX_PRACTICE_ASC);
        String CREATE_DETAILED_PRACTICE_HISTORY_INDEX_PRACTICE_DESC = "CREATE INDEX DETAILED_PRACTICE_HISTORY_PRACTICE_IDX_DESC ON " + TABLE_DETAILED_PRACTICE_HISTORY + " (" + KEY_DETAILED_PRACTICE_HISTORY_ID_PRACTICE + " DESC)";
        db.execSQL(CREATE_DETAILED_PRACTICE_HISTORY_INDEX_PRACTICE_DESC);

        String CREATE_DETAILED_PRACTICE_HISTORY_INDEX_USER_ASC = "CREATE INDEX DETAILED_PRACTICE_HISTORY_USER_IDX_ASC ON " + TABLE_DETAILED_PRACTICE_HISTORY + " (" + KEY_DETAILED_PRACTICE_HISTORY_ID_USER + " ASC)";
        db.execSQL(CREATE_DETAILED_PRACTICE_HISTORY_INDEX_USER_ASC);
        String CREATE_DETAILED_PRACTICE_HISTORY_INDEX_USER_DESC = "CREATE INDEX DETAILED_PRACTICE_HISTORY_USER_IDX_DESC ON " + TABLE_DETAILED_PRACTICE_HISTORY + " (" + KEY_DETAILED_PRACTICE_HISTORY_ID_USER + " DESC)";
        db.execSQL(CREATE_DETAILED_PRACTICE_HISTORY_INDEX_USER_DESC);

        String CREATE_DETAILED_PRACTICE_HISTORY_INDEX_DATE_ASC = "CREATE INDEX DETAILED_PRACTICE_HISTORY_DATE_IDX_ASC ON " + TABLE_DETAILED_PRACTICE_HISTORY + " (" + KEY_DETAILED_PRACTICE_HISTORY_DATE + " ASC)";
        db.execSQL(CREATE_DETAILED_PRACTICE_HISTORY_INDEX_DATE_ASC);
        String CREATE_DETAILED_PRACTICE_HISTORY_INDEX_DATE_DESC = "CREATE INDEX DETAILED_PRACTICE_HISTORY_DATE_IDX_DESC ON " + TABLE_DETAILED_PRACTICE_HISTORY + " (" + KEY_DETAILED_PRACTICE_HISTORY_DATE + " DESC)";
        db.execSQL(CREATE_DETAILED_PRACTICE_HISTORY_INDEX_DATE_DESC);

        String CREATE_DETAILED_PRACTICE_HISTORY_INDEX_LAST_TIME_ASC = "CREATE INDEX DETAILED_PRACTICE_HISTORY_LAST_TIME_IDX_ASC ON " + TABLE_DETAILED_PRACTICE_HISTORY + " (" + KEY_DETAILED_PRACTICE_HISTORY_TIME + " ASC)";
        db.execSQL(CREATE_DETAILED_PRACTICE_HISTORY_INDEX_LAST_TIME_ASC);
        String CREATE_DETAILED_PRACTICE_HISTORY_INDEX_LAST_TIME_DESC = "CREATE INDEX DETAILED_PRACTICE_HISTORY_LAST_TIME_IDX_DESC ON " + TABLE_DETAILED_PRACTICE_HISTORY + " (" + KEY_DETAILED_PRACTICE_HISTORY_TIME + " DESC)";
        db.execSQL(CREATE_DETAILED_PRACTICE_HISTORY_INDEX_LAST_TIME_DESC);

        String CREATE_DETAILED_PRACTICE_HISTORY_INDEX_USER_AND_PRACTICE_ASC = "CREATE INDEX DETAILED_PRACTICE_HISTORY_USER_AND_PRACTICE_IDX_ASC ON " + TABLE_DETAILED_PRACTICE_HISTORY + " (" + KEY_DETAILED_PRACTICE_HISTORY_ID_USER + " ASC, " + KEY_DETAILED_PRACTICE_HISTORY_ID_PRACTICE + " ASC)";
        db.execSQL(CREATE_DETAILED_PRACTICE_HISTORY_INDEX_USER_AND_PRACTICE_ASC);
        String CREATE_DETAILED_PRACTICE_HISTORY_INDEX_USER_AND_PRACTICE_DESC = "CREATE INDEX DETAILED_PRACTICE_HISTORY_USER_AND_PRACTICE_IDX_DESC ON " + TABLE_DETAILED_PRACTICE_HISTORY + " (" + KEY_DETAILED_PRACTICE_HISTORY_ID_USER + " DESC, " + KEY_DETAILED_PRACTICE_HISTORY_ID_PRACTICE + " DESC)";
        db.execSQL(CREATE_DETAILED_PRACTICE_HISTORY_INDEX_USER_AND_PRACTICE_DESC);


    }

    public synchronized void DropDB(SQLiteDatabase db) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OPTIONS);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AREAS);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRACTICES);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRACTICE_HISTORY);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DETAILED_PRACTICE_HISTORY);
    }

    public synchronized void ClearDB(SQLiteDatabase db) {

        db.execSQL("Delete from " + TABLE_OPTIONS);
        db.execSQL("Delete from " + TABLE_AREAS);
        db.execSQL("Delete from " + TABLE_PROJECTS);
        db.execSQL("Delete from " + TABLE_PRACTICES);
        db.execSQL("Delete from " + TABLE_PRACTICE_HISTORY);
        db.execSQL("Delete from " + TABLE_DETAILED_PRACTICE_HISTORY);
        db.execSQL("Delete from  " + TABLE_USERS);
    }

    public synchronized void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_USER_ID, user.getId());
        values.put(KEY_USER_NAME, user.getName());
        values.put(KEY_USER_IS_CURRENT, user.isCurrentUser());

        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public synchronized void addOptions(Options options) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_OPTIONS_ID, options.getId());
        values.put(KEY_OPTIONS_ID_USER, options.getIdUser());
        values.put(KEY_OPTIONS_DISPLAY_NOTIFICATION_TIMER, options.getDisplayNotificationTimerSwitch());
        values.put(KEY_OPTIONS_SAVE_INTERVAL, options.getSaveInterval());
        values.put(KEY_OPTIONS_CHRONO_IS_WORKING, options.getChronoIsWorking());

        db.insert(TABLE_OPTIONS, null, values);
        db.close();
    }

    public synchronized void addArea(Area area) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_AREA_ID, area.getId());
        values.put(KEY_AREA_ID_USER, area.getIdUser());
        values.put(KEY_AREA_NAME, area.getName());
        values.put(KEY_AREA_COLOR, area.getColor());


        db.insert(TABLE_AREAS, null, values);
        db.close();
    }

    public synchronized void addProject(Project project) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PROJECT_ID, project.getId());
        values.put(KEY_PROJECT_ID_USER, project.getIdUser());
        values.put(KEY_PROJECT_NAME, project.getName());
        values.put(KEY_PROJECT_ID_AREA, project.getIdArea());

        db.insert(TABLE_PROJECTS, null, values);
        db.close();
    }

    public synchronized void addPractice(Practice practice) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PRACTICE_ID, practice.getId());
        values.put(KEY_PRACTICE_ID_USER, practice.getIdUser());
        values.put(KEY_PRACTICE_NAME, practice.getName());
        values.put(KEY_PRACTICE_ID_PROJECT, practice.getIdProject());
        values.put(KEY_PRACTICE_IS_ACTIVE, practice.getIsActive());

        db.insert(TABLE_PRACTICES, null, values);
        db.close();
    }

    public synchronized void addPracticeHistory(PracticeHistory practiceHistory) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PRACTICE_HISTORY_ID, practiceHistory.getId());
        values.put(KEY_PRACTICE_HISTORY_ID_USER, practiceHistory.getIdUser());
        values.put(KEY_PRACTICE_HISTORY_ID_PRACTICE, practiceHistory.getIdPractice());
        values.put(KEY_PRACTICE_HISTORY_DATE, practiceHistory.getDate());
        values.put(KEY_PRACTICE_HISTORY_DURATION, practiceHistory.getDuration());
        values.put(KEY_PRACTICE_HISTORY_LAST_TIME, practiceHistory.getLastTime());

        db.insert(TABLE_PRACTICE_HISTORY, null, values);
        db.close();
    }

    public synchronized void addDetailedPracticeHistory(DetailedPracticeHistory detailedPracticeHistory) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DETAILED_PRACTICE_HISTORY_ID, detailedPracticeHistory.getId());
        values.put(KEY_DETAILED_PRACTICE_HISTORY_ID_USER, detailedPracticeHistory.getIdUser());
        values.put(KEY_DETAILED_PRACTICE_HISTORY_ID_PRACTICE, detailedPracticeHistory.getIdPractice());
        values.put(KEY_DETAILED_PRACTICE_HISTORY_DATE, detailedPracticeHistory.getDate());
        values.put(KEY_DETAILED_PRACTICE_HISTORY_DURATION, detailedPracticeHistory.getDuration());
        values.put(KEY_DETAILED_PRACTICE_HISTORY_TIME, detailedPracticeHistory.getTime());

        db.insert(TABLE_DETAILED_PRACTICE_HISTORY, null, values);
        db.close();
    }

    public synchronized boolean containsUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_USER_ID}, KEY_USER_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            return false;
        } else {
            cursor.close();
            db.close();
            return true;
        }
    }

    public synchronized User getUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_USER_ID, KEY_USER_NAME, KEY_USER_IS_CURRENT}, KEY_USER_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        User user = null;
        if (cursor.getCount() == 0) {
            db.close();
            throw new TableDoesNotContainElementException("There is no User with id - " + id);
        } else {
            user = new User.Builder(Integer.parseInt(cursor.getString(0)))
                    .addName(cursor.getString(1))
                    .addIsCurrentUser(Integer.parseInt(cursor.getString(2)))
                    .build();

            cursor.close();
            db.close();
            return user;
        }
    }

    public synchronized boolean containsOptions(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_OPTIONS, new String[]{KEY_OPTIONS_ID}, KEY_OPTIONS_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            cursor.close();
            db.close();
            return false;
        } else {
            cursor.close();
            db.close();
            return true;
        }
    }

    public synchronized Options getOptions(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_OPTIONS, new String[]{KEY_OPTIONS_ID, KEY_OPTIONS_RECOVERY_ON_RUN,
                        KEY_OPTIONS_DISPLAY_NOTIFICATION_TIMER, KEY_OPTIONS_SAVE_INTERVAL, KEY_OPTIONS_CHRONO_IS_WORKING}, KEY_OPTIONS_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Options options = null;
        if (cursor.getCount() == 0) {
            db.close();
            throw new TableDoesNotContainElementException("There is no Options with id - " + id);
        } else {
            options = new Options.Builder(Integer.parseInt(cursor.getString(0)))
                    .addRecoverySwitch(cursor.getInt(1))
                    .addDisplaySwitch(cursor.getInt(2))
                    .addSaveInterval(cursor.getInt(3))
                    .addChronoIsWorking(cursor.getInt(4))
                    .build();

            cursor.close();
            db.close();
            return options;
        }
    }

    public synchronized Options getOptionsOfUser(int id_user) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_OPTIONS, new String[]{KEY_OPTIONS_ID, KEY_OPTIONS_RECOVERY_ON_RUN,
                        KEY_OPTIONS_DISPLAY_NOTIFICATION_TIMER, KEY_OPTIONS_SAVE_INTERVAL, KEY_OPTIONS_CHRONO_IS_WORKING}, KEY_OPTIONS_ID_USER + "=?",
                new String[]{String.valueOf(id_user)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Options options = null;
        if (cursor.getCount() == 0) {
            cursor.close();
            db.close();
            return null;
        } else {
            options = new Options.Builder(Integer.parseInt(cursor.getString(0)))
                    .addRecoverySwitch(cursor.getInt(1))
                    .addDisplaySwitch(cursor.getInt(2))
                    .addSaveInterval(cursor.getInt(3))
                    .addChronoIsWorking(cursor.getInt(4))
                    .build();

            cursor.close();
            db.close();
            return options;
        }
    }

    public synchronized Boolean containsArea(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_AREAS, new String[]{KEY_AREA_ID}, KEY_AREA_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            db.close();
            return false;
        } else {
            cursor.close();
            db.close();
            return true;
        }
    }

    public synchronized Area getArea(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_AREAS, new String[]{KEY_AREA_ID, KEY_AREA_NAME, KEY_AREA_COLOR}, KEY_AREA_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Area area = null;
        if (cursor.getCount() == 0) {
            db.close();
            throw new TableDoesNotContainElementException("There is no Area with id - " + id);
        } else {
            area = new Area.Builder(Integer.parseInt(cursor.getString(0)))
                    .addName(cursor.getString(1))
                    .addColor(cursor.getInt(2)).build();

            cursor.close();
            db.close();
            return area;
        }

    }

    public synchronized boolean containsProject(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PROJECTS, new String[]{KEY_PROJECT_ID}, KEY_PROJECT_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        if (cursor.getCount() == 0) {
            db.close();
            return false;
        } else {
            cursor.close();
            db.close();
            return true;
        }
    }

    public synchronized Project getProject(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PROJECTS, new String[]{KEY_PROJECT_ID, KEY_PROJECT_ID_USER, KEY_PROJECT_NAME, KEY_PROJECT_ID_AREA}, KEY_PROJECT_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Project project = null;
        if (cursor.getCount() == 0) {
            db.close();
            throw new TableDoesNotContainElementException("There is no Project with id - " + id);
        } else {
            project = new Project.Builder(Integer.parseInt(cursor.getString(0)))
                    .addName(cursor.getString(2))
                    .addIdArea(cursor.getInt(3)).build();

            cursor.close();
            db.close();
            return project;

        }
    }

    public synchronized boolean containsPractice(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PRACTICES, new String[]{KEY_PRACTICE_ID}, KEY_PRACTICE_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            db.close();
            return false;
        } else {
            cursor.close();
            db.close();
            return true;
        }

    }

    public synchronized Practice getPractice(int id) {
        SQLiteDatabase db = this.getReadableDatabase();


        Cursor cursor = db.query(TABLE_PRACTICES, new String[]{KEY_PRACTICE_ID, KEY_PRACTICE_NAME,
                        KEY_PRACTICE_ID_PROJECT, KEY_PRACTICE_IS_ACTIVE}, KEY_PRACTICE_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        Practice practice = null;
        if (cursor.getCount() == 0) {
            db.close();
            throw new TableDoesNotContainElementException("There is no Practice with id - " + id);
        } else {
            practice = new Practice.Builder(Integer.parseInt(cursor.getString(0)))
                    .addName(cursor.getString(1))
                    .addIDProject(cursor.getInt(2))
                    .addIsActive(cursor.getInt(3)).build();

            cursor.close();
            db.close();
            return practice;
        }

    }

    public synchronized Boolean containsPracticeHistory(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRACTICE_HISTORY, new String[]{KEY_PRACTICE_HISTORY_ID},
                KEY_PRACTICE_HISTORY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        if (cursor.getCount() == 0) {
            db.close();
            return false;
        } else {
            cursor.close();
            db.close();
            return true;
        }
    }

    public synchronized PracticeHistory getPracticeHistory(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PRACTICE_HISTORY, new String[]{KEY_PRACTICE_HISTORY_ID, KEY_PRACTICE_HISTORY_ID_PRACTICE,
                        KEY_PRACTICE_HISTORY_DATE, KEY_PRACTICE_HISTORY_LAST_TIME, KEY_PRACTICE_HISTORY_DURATION},
                KEY_PRACTICE_HISTORY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        if (cursor.getCount() == 0) {
            db.close();
            throw new TableDoesNotContainElementException("There is no Practice history with id - " + id);
        } else {
            PracticeHistory practiceHistory = new PracticeHistory.Builder(Integer.parseInt(cursor.getString(0)))
                    .addIdPractice(Integer.parseInt(cursor.getString(1)))
                    .addDate(cursor.getLong(2))
                    .addLastTime(cursor.getLong(3))
                    .addDuration(cursor.getLong(4))
                    .build();

            cursor.close();
            db.close();
            return practiceHistory;
        }
    }

    public synchronized Boolean containsDetailedPracticeHistory(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DETAILED_PRACTICE_HISTORY, new String[]{KEY_DETAILED_PRACTICE_HISTORY_ID},
                KEY_DETAILED_PRACTICE_HISTORY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        if (cursor.getCount() == 0) {
            db.close();
            return false;
        } else {
            cursor.close();
            db.close();
            return true;
        }
    }

    public synchronized DetailedPracticeHistory getDetailedPracticeHistory(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_DETAILED_PRACTICE_HISTORY, new String[]{KEY_DETAILED_PRACTICE_HISTORY_ID, KEY_DETAILED_PRACTICE_HISTORY_ID_PRACTICE,
                        KEY_DETAILED_PRACTICE_HISTORY_DATE, KEY_DETAILED_PRACTICE_HISTORY_TIME, KEY_DETAILED_PRACTICE_HISTORY_DURATION},
                KEY_DETAILED_PRACTICE_HISTORY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        if (cursor.getCount() == 0) {
            db.close();
            throw new TableDoesNotContainElementException("There is no Detailed practice history with id - " + id);
        } else {
            DetailedPracticeHistory detailedPracticeHistory = new DetailedPracticeHistory.Builder(Integer.parseInt(cursor.getString(0)))
                    .addIdPractice(Integer.parseInt(cursor.getString(1)))
                    .addDate(cursor.getLong(2))
                    .addTime(cursor.getLong(3))
                    .addDuration(cursor.getLong(4))
                    .build();

            cursor.close();
            db.close();
            return detailedPracticeHistory;
        }
    }

    public synchronized PracticeHistory getPracticeHistory(int id_practice, long date) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PRACTICE_HISTORY, new String[]{KEY_PRACTICE_HISTORY_ID, KEY_PRACTICE_HISTORY_ID_PRACTICE,
                        KEY_PRACTICE_HISTORY_DATE, KEY_PRACTICE_HISTORY_LAST_TIME, KEY_PRACTICE_HISTORY_DURATION},
                KEY_PRACTICE_HISTORY_ID_PRACTICE + "=? AND " + KEY_PRACTICE_HISTORY_DATE + "=?",
                new String[]{String.valueOf(id_practice), String.valueOf(date)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        if (cursor.getCount() == 0) {
            db.close();
            throw new TableDoesNotContainElementException("There is no Practice history with practice_id - " + id_practice
                    + " and date=" + date);
        } else {
            PracticeHistory practiceHistory = new PracticeHistory.Builder(Integer.parseInt(cursor.getString(0)))
                    .addIdPractice(Integer.parseInt(cursor.getString(1)))
                    .addDate(cursor.getLong(2))
                    .addLastTime(cursor.getLong(3))
                    .addDuration(cursor.getLong(4))
                    .build();

            cursor.close();
            db.close();
            return practiceHistory;
        }
    }

    public synchronized void deleteAllUsers() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, null, null);
        db.close();

    }

    public synchronized void deleteAllOptions() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OPTIONS, null, null);
        db.close();

    }

    public synchronized void deleteAllOptionsOfUser(int id_user) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OPTIONS, KEY_OPTIONS_ID_USER + "=?", new String[]{String.valueOf(id_user)});
        db.close();

    }

    public synchronized void deleteAllAreas() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_AREAS, null, null);
        db.close();

    }

    public synchronized void deleteAllAreasOfUser(int id_user) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_AREAS, KEY_AREA_ID_USER + "=?", new String[]{String.valueOf(id_user)});
        db.close();

    }

    public synchronized void deleteAllProjects() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROJECTS, null, null);
        db.close();

    }

    public synchronized void deleteAllProjectsOfUser(int id_user) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROJECTS, KEY_PROJECT_ID_USER + "=?", new String[]{String.valueOf(id_user)});
        db.close();

    }

    public synchronized void deleteAllProjectsOfArea(int id_area) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROJECTS, KEY_PROJECT_ID_AREA + "=?", new String[]{String.valueOf(id_area)});
        db.close();

    }

    public synchronized void deleteAllPractices() {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRACTICES, null, null);
        db.close();

    }

    public synchronized void deleteAllPracticesOfUser(int id_user) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRACTICES, KEY_PRACTICE_ID_USER + "=?", new String[]{String.valueOf(id_user)});
        db.close();

    }

    public synchronized void deleteAllPracticesOfProject(int id_project) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRACTICES, KEY_PRACTICE_ID_PROJECT + "=?", new String[]{String.valueOf(id_project)});
        db.close();

    }

    public synchronized void deleteAllPracticeHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRACTICE_HISTORY, null, null);
        db.close();
    }

    public synchronized void deleteAllPracticeHistoryOfPractice(int id_practice) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRACTICE_HISTORY, KEY_PRACTICE_HISTORY_ID_PRACTICE + "=?", new String[]{String.valueOf(id_practice)});
        db.close();
    }

    public synchronized void deleteAllPracticeHistoryOfUser(int id_user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRACTICE_HISTORY, KEY_PRACTICE_HISTORY_ID_USER + "=?", new String[]{String.valueOf(id_user)});
        db.close();
    }

    public synchronized void deleteAllDetailedPracticeHistory() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DETAILED_PRACTICE_HISTORY, null, null);
        db.close();
    }

    public synchronized void deleteAllDetailedPracticeHistoryOfPractice(int id_practice) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DETAILED_PRACTICE_HISTORY, KEY_DETAILED_PRACTICE_HISTORY_ID_PRACTICE + "=?",
                new String[]{String.valueOf(id_practice)});
        db.close();
    }

    public synchronized void deleteAllDetailedPracticeHistoryOfPracticeAndDate(int id_practice, long date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DETAILED_PRACTICE_HISTORY, KEY_DETAILED_PRACTICE_HISTORY_ID_PRACTICE + "=?"
                + KEY_DETAILED_PRACTICE_HISTORY_DATE + "=?", new String[]{String.valueOf(id_practice), String.valueOf(date)});
        db.close();
    }

    public synchronized void deleteAllDetailedPracticeHistoryOfUser(int id_user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DETAILED_PRACTICE_HISTORY, KEY_DETAILED_PRACTICE_HISTORY_ID_USER + "=?", new String[]{String.valueOf(id_user)});
        db.close();
    }

    public synchronized List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String selectQuery = "SELECT  " + KEY_USER_ID + "," + KEY_USER_NAME + "," + KEY_USER_IS_CURRENT + " FROM " + TABLE_USERS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                User user = new User.Builder(cursor.getInt(0))
                        .addName(cursor.getString(1))
                        .addIsCurrentUser(Integer.parseInt(cursor.getString(2)))
                        .build();
                users.add(user);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return users;
    }

    public synchronized List<Area> getAllAreas() {
        List<Area> areas = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_AREA_ID + "," + KEY_AREA_NAME + "," + KEY_AREA_COLOR + " FROM " + TABLE_AREAS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Area area = new Area.Builder(cursor.getInt(0))
                        .addName(cursor.getString(1))
                        .addColor(cursor.getInt(2))
                        .build();
                areas.add(area);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return areas;
    }

    public synchronized List<Area> getAllAreasOfUser(int id_user) {
        List<Area> areas = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_AREA_ID + "," + KEY_AREA_NAME + "," + KEY_AREA_COLOR + " FROM " + TABLE_AREAS
                + " WHERE " + KEY_AREA_ID_USER + "=" + id_user + " ORDER BY " + KEY_AREA_ID;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Area area = new Area.Builder(cursor.getInt(0))
                        .addName(cursor.getString(1))
                        .addColor(cursor.getInt(2))
                        .build();
                areas.add(area);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return areas;
    }

    public synchronized List<Project> getAllProjects() {
        List<Project> projects = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_PROJECT_ID + "," + KEY_PROJECT_NAME + "," + KEY_PROJECT_ID_AREA + " FROM " + TABLE_PROJECTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Project project = new Project.Builder(cursor.getInt(0))
                        .addName(cursor.getString(1))
                        .addIdArea(cursor.getInt(2))
                        .build();
                projects.add(project);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return projects;
    }

    public synchronized List<Project> getAllProjectsOfUser(int id_user) {
        List<Project> projects = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_PROJECT_ID + "," + KEY_PROJECT_NAME + "," + KEY_PROJECT_ID_AREA + " FROM " + TABLE_PROJECTS
                + " WHERE " + KEY_PROJECT_ID_USER + "=" + id_user + " ORDER BY " + KEY_PROJECT_ID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Project project = new Project.Builder(cursor.getInt(0))
                        .addName(cursor.getString(1))
                        .addIdArea(cursor.getInt(2))
                        .build();
                projects.add(project);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return projects;
    }

    public synchronized List<Project> getAllProjectsOfArea(int id_area) {
        List<Project> projects = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_PROJECT_ID + "," + KEY_PROJECT_NAME + "," + KEY_PROJECT_ID_AREA + " FROM " + TABLE_PROJECTS
                + " WHERE " + KEY_PROJECT_ID_AREA + "=" + id_area + " ORDER BY " + KEY_PROJECT_ID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Project project = new Project.Builder(cursor.getInt(0))
                        .addName(cursor.getString(1))
                        .addIdArea(cursor.getInt(2))
                        .build();
                projects.add(project);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return projects;
    }

    public synchronized List<Practice> getAllPractices() {
        List<Practice> practices = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_PRACTICE_ID + "," + KEY_PRACTICE_NAME + "," + KEY_PRACTICE_ID_PROJECT + "," + KEY_PRACTICE_IS_ACTIVE
                + " FROM " + TABLE_PRACTICES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Practice practice = new Practice.Builder(cursor.getInt(0))
                        .addName(cursor.getString(1))
                        .addIDProject(cursor.getInt(2))
                        .addIsActive(cursor.getInt(3))
                        .build();
                practices.add(practice);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return practices;
    }

    public synchronized List<Practice> getAllActivePractices() {
        List<Practice> practices = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_PRACTICE_ID + "," + KEY_PRACTICE_NAME + "," + KEY_PRACTICE_ID_PROJECT + "," + KEY_PRACTICE_IS_ACTIVE
                + " FROM " + TABLE_PRACTICES
                + " WHERE " + KEY_PRACTICE_IS_ACTIVE + "=1 ORDER BY " + KEY_PRACTICE_ID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Practice practice = new Practice.Builder(cursor.getInt(0))
                        .addName(cursor.getString(1))
                        .addIDProject(cursor.getInt(2))
                        .addIsActive(cursor.getInt(3))
                        .build();
                practices.add(practice);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return practices;
    }

    public synchronized List<Practice> getAllActivePracticesOfUser(int id_user) {
        List<Practice> practices = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_PRACTICE_ID + "," + KEY_PRACTICE_NAME + "," + KEY_PRACTICE_ID_PROJECT + "," + KEY_PRACTICE_IS_ACTIVE
                + " FROM " + TABLE_PRACTICES
                + " WHERE " + KEY_PRACTICE_IS_ACTIVE + "=1 AND " + KEY_PRACTICE_ID_USER + "=" + id_user +
                " ORDER BY " + KEY_PRACTICE_ID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Practice practice = new Practice.Builder(cursor.getInt(0))
                        .addName(cursor.getString(1))
                        .addIDProject(cursor.getInt(2))
                        .addIsActive(cursor.getInt(3))
                        .build();
                practices.add(practice);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return practices;
    }

    public synchronized List<Practice> getAllActivePracticesOfProject(int id_project) {
        List<Practice> practices = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_PRACTICE_ID + "," + KEY_PRACTICE_NAME + "," + KEY_PRACTICE_ID_PROJECT + "," + KEY_PRACTICE_IS_ACTIVE
                + " FROM " + TABLE_PRACTICES
                + " WHERE " + KEY_PRACTICE_IS_ACTIVE + "=1 AND " + KEY_PRACTICE_ID_PROJECT + "=" + id_project +
                " ORDER BY " + KEY_PRACTICE_ID;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Practice practice = new Practice.Builder(cursor.getInt(0))
                        .addName(cursor.getString(1))
                        .addIDProject(cursor.getInt(2))
                        .addIsActive(cursor.getInt(3))
                        .build();
                practices.add(practice);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return practices;
    }

    public synchronized List<PracticeHistory> getAllPracticeHistory() {
        List<PracticeHistory> practiceHistoryList = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_PRACTICE_HISTORY_ID + "," + KEY_PRACTICE_HISTORY_ID_PRACTICE + ","
                + KEY_PRACTICE_HISTORY_DATE + "," + KEY_PRACTICE_HISTORY_LAST_TIME + "," + KEY_PRACTICE_HISTORY_DURATION
                + " FROM " + TABLE_PRACTICE_HISTORY + " ORDER BY " + KEY_PRACTICE_HISTORY_LAST_TIME + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                PracticeHistory practiceHistory = new PracticeHistory.Builder(cursor.getInt(0))
                        .addIdPractice(cursor.getInt(1))
                        .addDate(cursor.getLong(2))
                        .addLastTime(cursor.getLong(3))
                        .addDuration(cursor.getLong(4))
                        .build();
                practiceHistoryList.add(practiceHistory);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return practiceHistoryList;
    }

    public synchronized List<PracticeHistory> getAllPracticeHistoryOfPractice(int id_practice) {
        List<PracticeHistory> practiceHistoryList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT " + KEY_PRACTICE_HISTORY_ID + "," + KEY_PRACTICE_HISTORY_ID_PRACTICE + ","
                + KEY_PRACTICE_HISTORY_DATE + "," + KEY_PRACTICE_HISTORY_LAST_TIME + "," + KEY_PRACTICE_HISTORY_DURATION
                + " FROM " + TABLE_PRACTICE_HISTORY
                + " WHERE " + KEY_PRACTICE_HISTORY_ID_PRACTICE + "=" + id_practice +
                " ORDER BY " + KEY_PRACTICE_HISTORY_LAST_TIME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                PracticeHistory practiceHistory = new PracticeHistory.Builder(cursor.getInt(0))
                        .addIdPractice(cursor.getInt(1))
                        .addDate(cursor.getLong(2))
                        .addLastTime(cursor.getLong(3))
                        .addDuration(cursor.getLong(4))
                        .build();
                practiceHistoryList.add(practiceHistory);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return practiceHistoryList;
    }

    public synchronized List<PracticeHistory> getAllPracticeHistoryOfUser(int id_user) {
        List<PracticeHistory> practiceHistoryList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT " + KEY_PRACTICE_HISTORY_ID + "," + KEY_PRACTICE_HISTORY_ID_PRACTICE + ","
                + KEY_PRACTICE_HISTORY_DATE + "," + KEY_PRACTICE_HISTORY_LAST_TIME + "," + KEY_PRACTICE_HISTORY_DURATION
                + " FROM " + TABLE_PRACTICE_HISTORY
                + " WHERE " + KEY_PRACTICE_HISTORY_ID_USER + "=" + id_user +
                " ORDER BY " + KEY_PRACTICE_HISTORY_LAST_TIME + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                PracticeHistory practiceHistory = new PracticeHistory.Builder(cursor.getInt(0))
                        .addIdPractice(cursor.getInt(1))
                        .addDate(cursor.getLong(2))
                        .addLastTime(cursor.getLong(3))
                        .addDuration(cursor.getLong(4))
                        .build();
                practiceHistoryList.add(practiceHistory);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return practiceHistoryList;
    }

    public synchronized List<PracticeHistory> getAllPracticeAndPracticeHistoryOfUserByDates(int id_user, long dateFrom, long dateTo) {
        dateFrom = dateFrom == 0 ? Long.MIN_VALUE : dateFrom;
        dateTo = dateTo == 0 ? Long.MAX_VALUE : dateTo;
        List<PracticeHistory> practiceHistoryList = new ArrayList<>();

        // Select All Query
        String selectQuery = "select active_practices." + KEY_PRACTICE_ID + ",ifnull(history." + KEY_PRACTICE_HISTORY_ID + ",0),"
                + "ifnull(history." + KEY_PRACTICE_HISTORY_DATE + ",0),ifnull(history." + KEY_PRACTICE_HISTORY_LAST_TIME + ",0),ifnull(history."
                + KEY_PRACTICE_HISTORY_DURATION + ",0)"
                + " FROM (select " + TABLE_PRACTICES + "." + KEY_PRACTICE_ID + " from " + TABLE_PRACTICES
                + " where " + TABLE_PRACTICES + "." + KEY_PRACTICE_IS_ACTIVE + "=1 AND " + TABLE_PRACTICES + "." + KEY_PRACTICE_ID_USER + "=" + id_user
                + ") as active_practices "
                + " left join ( select " + KEY_PRACTICE_HISTORY_ID + "," + KEY_PRACTICE_HISTORY_ID_PRACTICE
                + "," + KEY_PRACTICE_HISTORY_DATE + "," + KEY_PRACTICE_HISTORY_LAST_TIME + "," + KEY_PRACTICE_HISTORY_DURATION
                + " from " + TABLE_PRACTICE_HISTORY
                + " WHERE " + KEY_PRACTICE_HISTORY_DATE + ">= " + dateFrom + " AND " + KEY_PRACTICE_HISTORY_DATE + " <=" + dateTo + ") as history"
                + " on active_practices." + KEY_PRACTICE_ID + "=history." + KEY_PRACTICE_HISTORY_ID_PRACTICE
                + " order by " + KEY_PRACTICE_HISTORY_LAST_TIME + " desc";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int id_practice_count = 0;
        if (cursor.getCount() == 0) {
            return practiceHistoryList;
        }
        if (cursor.moveToFirst()) {
            do {
                int id_practice = cursor.getInt(0);
                int id_practice_history = cursor.getInt(1);
                PracticeHistory.Builder practiceHistoryBuilder;
                if (id_practice_history == 0) {
                    practiceHistoryBuilder = new PracticeHistory.Builder(getPracticeHistoryMaxNumber() + id_practice_count++ + 1);
                    practiceHistoryBuilder.addDate(dateFrom);
                } else {

                    practiceHistoryBuilder = new PracticeHistory.Builder(id_practice_history);
                    practiceHistoryBuilder.addDate(cursor.getLong(2));
                }
                practiceHistoryBuilder.addIdPractice(id_practice)
                        .addLastTime(cursor.getLong(3))
                        .addDuration(cursor.getLong(4))
                        .build();
                PracticeHistory practiceHistory = new PracticeHistory(practiceHistoryBuilder);
                practiceHistoryList.add(practiceHistory);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return practiceHistoryList;
    }

    public synchronized PracticeHistory getLastPracticeHistoryOfUserByDates(int id_user, long dateFrom, long dateTo) {
        dateFrom = dateFrom == 0 ? Long.MIN_VALUE : dateFrom;
        dateTo = dateTo == 0 ? Long.MAX_VALUE : dateTo;

        String selectQuery = "SELECT " + KEY_PRACTICE_HISTORY_ID + "," + KEY_PRACTICE_HISTORY_ID_PRACTICE + ","
                + KEY_PRACTICE_HISTORY_DATE + "," + KEY_PRACTICE_HISTORY_LAST_TIME + "," + KEY_PRACTICE_HISTORY_DURATION
                + " FROM " + TABLE_PRACTICE_HISTORY
                + " WHERE " + TABLE_PRACTICE_HISTORY + "." + KEY_PRACTICE_HISTORY_ID_USER + "=" + id_user
                + " AND " + KEY_PRACTICE_HISTORY_DATE + ">= " + dateFrom + " AND " + KEY_PRACTICE_HISTORY_DATE + " <=" + dateTo
                + " ORDER BY " + KEY_PRACTICE_HISTORY_LAST_TIME + " DESC LIMIT 1";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        PracticeHistory practiceHistory = null;
        if (cursor.moveToFirst()) {

            practiceHistory = new PracticeHistory.Builder(cursor.getInt(0))
                    .addIdPractice(cursor.getInt(1))
                    .addDate(cursor.getLong(2))
                    .addLastTime(cursor.getLong(3))
                    .addDuration(cursor.getLong(4))
                    .build();

        }

        cursor.close();
        db.close();
        return practiceHistory;
    }

    public synchronized List<PracticeHistory> getAllPracticeHistoryByDates(long dateFrom, long dateTo) {
        dateFrom = dateFrom == 0 ? Long.MIN_VALUE : dateFrom;
        dateTo = dateTo == 0 ? Long.MAX_VALUE : dateTo;
        List<PracticeHistory> practiceHistoryList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT " + KEY_PRACTICE_HISTORY_ID + "," + KEY_PRACTICE_HISTORY_ID_PRACTICE + ","
                + KEY_PRACTICE_HISTORY_DATE + "," + KEY_PRACTICE_HISTORY_LAST_TIME + "," + KEY_PRACTICE_HISTORY_DURATION
                + " FROM " + TABLE_PRACTICE_HISTORY
                + " WHERE " + KEY_PRACTICE_HISTORY_DATE + ">= " + dateFrom + "\" AND " + KEY_PRACTICE_HISTORY_DATE + " <=" + dateTo
                + " ORDER BY " + KEY_PRACTICE_HISTORY_LAST_TIME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                PracticeHistory practiceHistory = new PracticeHistory.Builder(cursor.getInt(0))
                        .addIdPractice(cursor.getInt(1))
                        .addDate(cursor.getLong(2))
                        .addLastTime(cursor.getLong(3))
                        .addDuration(cursor.getLong(4))
                        .build();
                practiceHistoryList.add(practiceHistory);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return practiceHistoryList;
    }

    public synchronized List<PracticeHistory> getAllPracticeHistoryOfPracticeByDates(int id_practice, long dateFrom, long dateTo) {
        dateFrom = dateFrom == 0 ? Long.MIN_VALUE : dateFrom;
        dateTo = dateTo == 0 ? Long.MAX_VALUE : dateTo;
        List<PracticeHistory> practiceHistoryList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT " + KEY_PRACTICE_HISTORY_ID + "," + KEY_PRACTICE_HISTORY_ID_PRACTICE + ","
                + KEY_PRACTICE_HISTORY_DATE + "," + KEY_PRACTICE_HISTORY_LAST_TIME + "," + KEY_PRACTICE_HISTORY_DURATION
                + " FROM " + TABLE_PRACTICE_HISTORY
                + " WHERE " + KEY_PRACTICE_HISTORY_ID_PRACTICE + "=" + id_practice
                + KEY_PRACTICE_HISTORY_DATE + ">= " + dateFrom + " AND " + KEY_PRACTICE_HISTORY_DATE + "<=" + dateTo
                + " ORDER BY " + KEY_PRACTICE_HISTORY_LAST_TIME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                PracticeHistory practiceHistory = new PracticeHistory.Builder(cursor.getInt(0))
                        .addIdPractice(cursor.getInt(1))
                        .addDate(cursor.getLong(2))
                        .addLastTime(cursor.getLong(3))
                        .addDuration(cursor.getLong(4))
                        .build();
                practiceHistoryList.add(practiceHistory);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return practiceHistoryList;
    }

    public synchronized List<DetailedPracticeHistory> getAllDetailedPracticeHistory() {
        List<DetailedPracticeHistory> detailedPracticeHistories = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_DETAILED_PRACTICE_HISTORY_ID + "," + KEY_DETAILED_PRACTICE_HISTORY_ID_PRACTICE + ","
                + KEY_DETAILED_PRACTICE_HISTORY_DATE + "," + KEY_DETAILED_PRACTICE_HISTORY_TIME + "," + KEY_DETAILED_PRACTICE_HISTORY_DURATION
                + " FROM " + TABLE_DETAILED_PRACTICE_HISTORY + " ORDER BY " + KEY_DETAILED_PRACTICE_HISTORY_TIME + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                DetailedPracticeHistory detailedPracticeHistory = new DetailedPracticeHistory.Builder(cursor.getInt(0))
                        .addIdPractice(cursor.getInt(1))
                        .addDate(cursor.getLong(2))
                        .addTime(cursor.getLong(3))
                        .addDuration(cursor.getLong(4))
                        .build();
                detailedPracticeHistories.add(detailedPracticeHistory);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return detailedPracticeHistories;
    }

    public synchronized List<DetailedPracticeHistory> getAllDetailedPracticeHistoryOfPractice(int id_practice) {
        List<DetailedPracticeHistory> detailedPracticeHistories = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_DETAILED_PRACTICE_HISTORY_ID + "," + KEY_DETAILED_PRACTICE_HISTORY_ID_PRACTICE + ","
                + KEY_DETAILED_PRACTICE_HISTORY_DATE + "," + KEY_DETAILED_PRACTICE_HISTORY_TIME + "," + KEY_DETAILED_PRACTICE_HISTORY_DURATION
                + " FROM " + TABLE_DETAILED_PRACTICE_HISTORY
                + " WHERE " + KEY_DETAILED_PRACTICE_HISTORY_ID_PRACTICE + "=" + id_practice +
                " ORDER BY " + KEY_DETAILED_PRACTICE_HISTORY_TIME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                DetailedPracticeHistory detailedPracticeHistory = new DetailedPracticeHistory.Builder(cursor.getInt(0))
                        .addIdPractice(cursor.getInt(1))
                        .addDate(cursor.getLong(2))
                        .addTime(cursor.getLong(3))
                        .addDuration(cursor.getLong(4))
                        .build();
                detailedPracticeHistories.add(detailedPracticeHistory);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return detailedPracticeHistories;
    }


    public synchronized List<DetailedPracticeHistory> getAllDetailedPracticeHistoryOfUser(int id_user) {
        List<DetailedPracticeHistory> detailedPracticeHistories = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_DETAILED_PRACTICE_HISTORY_ID + "," + KEY_DETAILED_PRACTICE_HISTORY_ID_PRACTICE + ","
                + KEY_DETAILED_PRACTICE_HISTORY_DATE + "," + KEY_DETAILED_PRACTICE_HISTORY_TIME + "," + KEY_DETAILED_PRACTICE_HISTORY_DURATION
                + " FROM " + TABLE_DETAILED_PRACTICE_HISTORY
                + " WHERE " + KEY_DETAILED_PRACTICE_HISTORY_ID_USER + "=" + id_user +
                " ORDER BY " + KEY_DETAILED_PRACTICE_HISTORY_TIME + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                DetailedPracticeHistory detailedPracticeHistory = new DetailedPracticeHistory.Builder(cursor.getInt(0))
                        .addIdPractice(cursor.getInt(1))
                        .addDate(cursor.getLong(2))
                        .addTime(cursor.getLong(3))
                        .addDuration(cursor.getLong(4))
                        .build();
                detailedPracticeHistories.add(detailedPracticeHistory);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return detailedPracticeHistories;
    }

    public synchronized List<DetailedPracticeHistory> getAllDetailedPracticeHistoryByDates(long dateFrom, long dateTo) {
        dateFrom = dateFrom == 0 ? Long.MIN_VALUE : dateFrom;
        dateTo = dateTo == 0 ? Long.MAX_VALUE : dateTo;
        List<DetailedPracticeHistory> detailedPracticeHistories = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_DETAILED_PRACTICE_HISTORY_ID + "," + KEY_DETAILED_PRACTICE_HISTORY_ID_PRACTICE + ","
                + KEY_DETAILED_PRACTICE_HISTORY_DATE + "," + KEY_DETAILED_PRACTICE_HISTORY_TIME + "," + KEY_DETAILED_PRACTICE_HISTORY_DURATION
                + " FROM " + TABLE_DETAILED_PRACTICE_HISTORY
                + " WHERE " + KEY_DETAILED_PRACTICE_HISTORY_DATE + ">= " + dateFrom + "\" AND " + KEY_DETAILED_PRACTICE_HISTORY_DATE + " <=" + dateTo
                + " ORDER BY " + KEY_DETAILED_PRACTICE_HISTORY_TIME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                DetailedPracticeHistory detailedPracticeHistory = new DetailedPracticeHistory.Builder(cursor.getInt(0))
                        .addIdPractice(cursor.getInt(1))
                        .addDate(cursor.getLong(2))
                        .addTime(cursor.getLong(3))
                        .addDuration(cursor.getLong(4))
                        .build();
                detailedPracticeHistories.add(detailedPracticeHistory);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return detailedPracticeHistories;
    }

    public synchronized List<DetailedPracticeHistory> getAllDetailedPracticeHistoryOfPracticeByDates(int id_practice, long dateFrom, long dateTo) {
        dateFrom = dateFrom == 0 ? Long.MIN_VALUE : dateFrom;
        dateTo = dateTo == 0 ? Long.MAX_VALUE : dateTo;
        List<DetailedPracticeHistory> detailedPracticeHistories = new ArrayList<>();
        String selectQuery = "SELECT " + KEY_DETAILED_PRACTICE_HISTORY_ID + "," + KEY_DETAILED_PRACTICE_HISTORY_ID_PRACTICE + ","
                + KEY_DETAILED_PRACTICE_HISTORY_DATE + "," + KEY_DETAILED_PRACTICE_HISTORY_TIME + "," + KEY_DETAILED_PRACTICE_HISTORY_DURATION
                + " FROM " + TABLE_DETAILED_PRACTICE_HISTORY
                + " WHERE " + KEY_DETAILED_PRACTICE_HISTORY_ID_PRACTICE + "=" + id_practice
                + KEY_DETAILED_PRACTICE_HISTORY_DATE + ">= " + dateFrom + " AND " + KEY_DETAILED_PRACTICE_HISTORY_DATE + "<=" + dateTo
                + " ORDER BY " + KEY_DETAILED_PRACTICE_HISTORY_TIME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                DetailedPracticeHistory detailedPracticeHistory = new DetailedPracticeHistory.Builder(cursor.getInt(0))
                        .addIdPractice(cursor.getInt(1))
                        .addDate(cursor.getLong(2))
                        .addTime(cursor.getLong(3))
                        .addDuration(cursor.getLong(4))
                        .build();
                detailedPracticeHistories.add(detailedPracticeHistory);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return detailedPracticeHistories;
    }

    public synchronized int getUserMaxNumber() {
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

    public synchronized int getAreaMaxNumber() {
        String countQuery = "SELECT  MAX(" + KEY_AREA_ID + ") FROM " + TABLE_AREAS + "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int count = 0;
        if (cursor.getCount() != 0) {
            count = cursor.getInt(0);

        }
        cursor.close();
        db.close();
        return count;
    }

    public synchronized int getOptionsMaxNumber() {
        String countQuery = "SELECT  MAX(" + KEY_OPTIONS_ID + ") FROM " + TABLE_OPTIONS + "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.moveToFirst();
        int count = 0;
        if (cursor.getCount() != 0) {
            count = cursor.getInt(0);

        }
        cursor.close();
        db.close();
        return count;
    }

    public synchronized int getProjectMaxNumber() {
        String countQuery = "SELECT  MAX(" + KEY_PROJECT_ID + ") FROM " + TABLE_PROJECTS + "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        cursor.moveToFirst();
        int count = 0;
        if (cursor.getCount() != 0) {
            count = cursor.getInt(0);

        }
        cursor.close();
        db.close();
        return count;

    }

    public synchronized int getPracticeMaxNumber() {
        String countQuery = "SELECT  MAX(" + KEY_PRACTICE_ID + ") FROM " + TABLE_PRACTICES + "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        cursor.moveToFirst();
        int count = 0;
        if (cursor.getCount() != 0) {
            count = cursor.getInt(0);

        }
        cursor.close();
        db.close();
        return count;

    }

    public synchronized int getPracticeHistoryMaxNumber() {
        String countQuery = "SELECT  MAX(" + KEY_PRACTICE_HISTORY_ID + ") FROM " + TABLE_PRACTICE_HISTORY + "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        cursor.moveToFirst();
        int count = 0;
        if (cursor.getCount() != 0) {
            count = cursor.getInt(0);

        }
        cursor.close();
        db.close();
        return count;
    }

    public synchronized int getDetailedPracticeHistoryMaxNumber() {
        String countQuery = "SELECT  MAX(" + KEY_DETAILED_PRACTICE_HISTORY_ID + ") FROM " + TABLE_DETAILED_PRACTICE_HISTORY + "";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        cursor.moveToFirst();
        int count = 0;
        if (cursor.getCount() != 0) {
            count = cursor.getInt(0);

        }
        cursor.close();
        db.close();
        return count;
    }

    public synchronized int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_NAME, user.getName());
        values.put(KEY_USER_IS_CURRENT, user.isCurrentUser());
        db.close();
        int rows = db.update(TABLE_USERS, values, KEY_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
        return rows;

    }

    public synchronized int updateArea(Area area) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_AREA_NAME, area.getName());
        values.put(KEY_AREA_ID_USER, area.getIdUser());
        values.put(KEY_AREA_COLOR, area.getColor());
        int rows = db.update(TABLE_AREAS, values, KEY_AREA_ID + " = ?",
                new String[]{String.valueOf(area.getId())});
        db.close();
        return rows;
    }

    public synchronized int updateOptions(Options options) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_OPTIONS_RECOVERY_ON_RUN, options.getRecoveryOnRunSwitch());
        values.put(KEY_OPTIONS_DISPLAY_NOTIFICATION_TIMER, options.getDisplayNotificationTimerSwitch());
        values.put(KEY_OPTIONS_SAVE_INTERVAL, options.getSaveInterval());
        values.put(KEY_OPTIONS_CHRONO_IS_WORKING, options.getChronoIsWorking());
        int rows = db.update(TABLE_OPTIONS, values, KEY_OPTIONS_ID + " = ?",
                new String[]{String.valueOf(options.getId())});
        db.close();
        return rows;
    }

    public synchronized int updateProject(Project project) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_PROJECT_NAME, project.getName());
        values.put(KEY_PROJECT_ID_USER, project.getIdUser());
        values.put(KEY_PROJECT_ID_AREA, project.getIdArea());
        int rows = db.update(TABLE_PROJECTS, values, KEY_PROJECT_ID + " = ?",
                new String[]{String.valueOf(project.getId())});
        db.close();
        return rows;
    }

    public synchronized int updatePractice(Practice practice) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PRACTICE_NAME, practice.getName());
        values.put(KEY_PRACTICE_ID_USER, practice.getIdUser());
        values.put(KEY_PRACTICE_ID_PROJECT, practice.getIdProject());
        values.put(KEY_PRACTICE_IS_ACTIVE, practice.getIsActive());
        int rows = db.update(TABLE_PRACTICES, values, KEY_PRACTICE_ID + " = ?",
                new String[]{String.valueOf(practice.getId())});
        db.close();
        return rows;
    }

    public synchronized int updatePracticeHistory(PracticeHistory practiceHistory) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PRACTICE_HISTORY_DATE, practiceHistory.getDate());
        values.put(KEY_PRACTICE_HISTORY_ID_USER, practiceHistory.getIdUser());
        values.put(KEY_PRACTICE_HISTORY_ID_PRACTICE, practiceHistory.getIdPractice());
        values.put(KEY_PRACTICE_HISTORY_DURATION, practiceHistory.getDuration());
        values.put(KEY_PRACTICE_HISTORY_LAST_TIME, practiceHistory.getLastTime());

        int rows = db.update(TABLE_PRACTICE_HISTORY, values, KEY_PRACTICE_HISTORY_ID + " = ?",
                new String[]{String.valueOf(practiceHistory.getId())});
        db.close();
        return rows;
    }

    public synchronized int updateDetailedPracticeHistory(DetailedPracticeHistory detailedPracticeHistory) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DETAILED_PRACTICE_HISTORY_DATE, detailedPracticeHistory.getDate());
        values.put(KEY_DETAILED_PRACTICE_HISTORY_ID_USER, detailedPracticeHistory.getIdUser());
        values.put(KEY_DETAILED_PRACTICE_HISTORY_ID_PRACTICE, detailedPracticeHistory.getIdPractice());
        values.put(KEY_DETAILED_PRACTICE_HISTORY_DURATION, detailedPracticeHistory.getDuration());
        values.put(KEY_DETAILED_PRACTICE_HISTORY_TIME, detailedPracticeHistory.getTime());

        int rows = db.update(TABLE_DETAILED_PRACTICE_HISTORY, values, KEY_DETAILED_PRACTICE_HISTORY_ID + " = ?",
                new String[]{String.valueOf(detailedPracticeHistory.getId())});
        db.close();
        return rows;
    }


    public synchronized void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, KEY_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }


    public synchronized void deleteArea(Area area) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_AREAS, KEY_AREA_ID + " = ?",
                new String[]{String.valueOf(area.getId())});
        db.close();
    }

    public synchronized void deleteOptions(Options options) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_OPTIONS, KEY_OPTIONS_ID + " = ?",
                new String[]{String.valueOf(options.getId())});
        db.close();
    }

    public synchronized void deleteProject(Project project) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PROJECTS, KEY_PROJECT_ID + " = ?",
                new String[]{String.valueOf(project.getId())});
        db.close();
    }

    public synchronized void deletePractice(Practice practice) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRACTICES, KEY_PRACTICE_ID + " = ?",
                new String[]{String.valueOf(practice.getId())});
        db.close();
    }

    public synchronized void deletePracticeHistory(PracticeHistory practiceHistory) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PRACTICE_HISTORY, KEY_PRACTICE_HISTORY_ID + " = ?",
                new String[]{String.valueOf(practiceHistory.getId())});
        db.close();
    }

    public synchronized void deleteDetailedPracticeHistory(DetailedPracticeHistory detailedPracticeHistory) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_DETAILED_PRACTICE_HISTORY, KEY_DETAILED_PRACTICE_HISTORY_ID + " = ?",
                new String[]{String.valueOf(detailedPracticeHistory.getId())});
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
