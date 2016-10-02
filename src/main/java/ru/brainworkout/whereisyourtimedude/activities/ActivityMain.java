package ru.brainworkout.whereisyourtimedude.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ru.brainworkout.whereisyourtimedude.R;

import static ru.brainworkout.whereisyourtimedude.common.Common.*;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionCurrentUser;

import ru.brainworkout.whereisyourtimedude.common.BackgroundChronometer;
import ru.brainworkout.whereisyourtimedude.common.Common;
import ru.brainworkout.whereisyourtimedude.common.Session;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.PracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.entities.User;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;


public class ActivityMain extends AppCompatActivity {

    private SharedPreferences mSettings;
    public static final String APP_PREFERENCES = "mysettings";
    public static final String APP_PREFERENCES_SAVE_INTERVAL = "save_interval";
    public static final String APP_PREFERENCES_CHRONO_IS_WORKING = "chrono_is_working";

    private static final int MAX_VERTICAL_BUTTON_COUNT = 10;
    private final DatabaseManager DB = new DatabaseManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPreferencesFromFile();

        showElementsOnScreen();

        defineCurrentUser();

        //resumeChronoIfWorking();

    }

    private void resumeChronoIfWorking() {
        if (Session.sessionChronometerIsWorking) {
            if (Session.sessionBackgroundChronometer != null &&
                    Session.sessionBackgroundChronometer.isAlive() ||
                    Session.sessionBackgroundChronometer.isTicking()) {
            } else {
                synchronized (Session.sessionBackgroundChronometer) {
                    if (Session.sessionBackgroundChronometer == null) {
                        resumeBackgroundChronometer();
                    }
                }
            }
        }
    }

    private void resumeBackgroundChronometer() {
        PracticeHistory resumedPracticeHistory = DB.getLastPracticeHistoryOfUserByDates(Session.sessionCurrentUser.getID(), 0, System.currentTimeMillis());
        if (resumedPracticeHistory != null) {
            Calendar today = Calendar.getInstance();
            today.clear(Calendar.HOUR);
            today.clear(Calendar.HOUR_OF_DAY);
            today.clear(Calendar.MINUTE);
            today.clear(Calendar.SECOND);
            today.clear(Calendar.MILLISECOND);
            long todayInMillis = today.getTimeInMillis();
            long duration = 0;
            long resumedPracticeHistoryDateInMillis = resumedPracticeHistory.getDate();
            if (resumedPracticeHistoryDateInMillis < todayInMillis) {
                Calendar calendarEndOfDay = Calendar.getInstance();
                synchronized (resumedPracticeHistory) {
                    //end resumed practice
                    calendarEndOfDay.setTimeInMillis(resumedPracticeHistory.getDate());
                    calendarEndOfDay.set(Calendar.HOUR_OF_DAY, 23);
                    calendarEndOfDay.set(Calendar.MINUTE, 59);
                    calendarEndOfDay.set(Calendar.SECOND, 59);
                    calendarEndOfDay.set(Calendar.MILLISECOND, 59);
                    long beginTimeinMillis = resumedPracticeHistory.getLastTime() - resumedPracticeHistory.getDuration() * 1_000;
                    long resumedDuration = (calendarEndOfDay.getTimeInMillis() - beginTimeinMillis) / 1_000;
                    resumedPracticeHistory.setLastTime(calendarEndOfDay.getTimeInMillis());
                    resumedPracticeHistory.setDuration(resumedDuration);
                    resumedPracticeHistory.dbSave(DB);
                }

                //check every day between begin date and today
                Calendar calendarBeginOfDay = Calendar.getInstance();
                calendarBeginOfDay.setTimeInMillis(resumedPracticeHistoryDateInMillis);
                calendarBeginOfDay.add(Calendar.DAY_OF_MONTH, 1);
                long nextDayBeginInMillis = calendarBeginOfDay.getTimeInMillis();
                calendarEndOfDay.add(Calendar.DAY_OF_MONTH, 1);
                long nextDayEndInMillis = calendarEndOfDay.getTimeInMillis();
                while (nextDayBeginInMillis != todayInMillis) {

                    PracticeHistory newDayPracticeHistory = new PracticeHistory.Builder(DB)
                            .addDate(nextDayBeginInMillis)
                            .addIdPractice(resumedPracticeHistory.getIdPractice())
                            .addLastTime(nextDayEndInMillis)
                            .addDuration(20 * 60 * 60)
                            .build();
                    calendarBeginOfDay.add(Calendar.DAY_OF_MONTH, 1);
                    nextDayBeginInMillis = calendarBeginOfDay.getTimeInMillis();
                    calendarEndOfDay.add(Calendar.DAY_OF_MONTH, 1);
                    nextDayEndInMillis = calendarEndOfDay.getTimeInMillis();
                    newDayPracticeHistory.dbSave(DB);
                }

                duration = (System.currentTimeMillis() - todayInMillis) / 1_000;
                resumedPracticeHistory = new PracticeHistory.Builder(DB)
                        .addDate(todayInMillis)
                        .addIdPractice(resumedPracticeHistory.getIdPractice())
                        .addLastTime(System.currentTimeMillis())
                        .addDuration(duration)
                        .build();

            } else {

                long beginTimeinMillis = resumedPracticeHistory.getLastTime() - resumedPracticeHistory.getDuration() * 1_000;
                duration = (System.currentTimeMillis() - beginTimeinMillis) / 1_000;
            }
            Session.sessionBackgroundChronometer = new BackgroundChronometer();
            Session.sessionBackgroundChronometer.setCurrentPracticeHistory(resumedPracticeHistory);
            Session.sessionBackgroundChronometer.setDB(DB);
            Session.sessionBackgroundChronometer.setGlobalChronometerCountInSeconds(duration);
            Session.sessionBackgroundChronometer.start();
            Session.sessionBackgroundChronometer.resumeTicking();
        }
    }

    private void getPreferencesFromFile() {
        mSettings = getSharedPreferences(ActivityMain.APP_PREFERENCES, Context.MODE_PRIVATE);

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_SAVE_INTERVAL)) {
            Common.SAVE_INTERVAL = mSettings.getInt(ActivityMain.APP_PREFERENCES_SAVE_INTERVAL, 10);
        } else {
            Common.SAVE_INTERVAL = 10;
        }

        //Session.sessionChronometerIsWorking=true;
        if (mSettings.contains(ActivityMain.APP_PREFERENCES_CHRONO_IS_WORKING)) {
            Session.sessionChronometerIsWorking = mSettings.getBoolean(ActivityMain.APP_PREFERENCES_CHRONO_IS_WORKING, false);
        } else {
            Session.sessionChronometerIsWorking = false;
        }


    }

    private void showElementsOnScreen() {

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        int mHeight = displaymetrics.heightPixels / MAX_VERTICAL_BUTTON_COUNT;
        for (int i = 0; i <= MAX_VERTICAL_BUTTON_COUNT; i++) {
            int btID = getResources().getIdentifier("btMain" + String.valueOf(i), "id", getPackageName());
            Button btName = (Button) findViewById(btID);
            if (btName != null) {
                btName.setHeight(mHeight);
            }
        }

    }

    private void defineCurrentUser() {

        if (sessionCurrentUser == null) {
            List<User> userList = DB.getAllUsers();
            if (userList.size() == 1) {
                User currentUser = userList.get(0);
                sessionCurrentUser = currentUser;
                currentUser.setIsCurrentUser(1);
                currentUser.dbSave(DB);
            } else {
                //ищем активного
                for (User user : userList
                        ) {
                    if (user.isCurrentUser() == 1) {
                        sessionCurrentUser = user;
                        break;
                    }
                }
                isUserDefined();
            }
        }
    }

    public void btUsers_onClick(final View view) {

        Intent intent = new Intent(ActivityMain.this, ActivityUsersList.class);
        startActivity(intent);

    }

    public void btTest_onClick(final View view) {

        Intent intent = new Intent(ActivityMain.this, ActivityDateTimePickerDialog.class);
        startActivity(intent);

    }

    public void btAreas_onClick(final View view) {

        if (isDBNotEmpty() && isUserDefined()) {
            Intent intent = new Intent(ActivityMain.this, ActivityAreasList.class);
            startActivity(intent);
        }

    }

    public void btProjects_onClick(final View view) {
        if (isDBNotEmpty() && isUserDefined()) {
            Intent intent = new Intent(ActivityMain.this, ActivityProjectsList.class);
            startActivity(intent);
        }

    }

    public void btPractices_onClick(final View view) {

        if (isDBNotEmpty() && isUserDefined()) {
            Intent intent = new Intent(ActivityMain.this, ActivityPracticesList.class);
            startActivity(intent);
        }

    }

    public void btPracticeHistory_onClick(final View view) {

        if (isDBNotEmpty() && isUserDefined()) {
            //DB.update(DB.getReadableDatabase());
            Intent intent = new Intent(ActivityMain.this, ActivityPracticeHistoryList.class);
            startActivity(intent);
        }

    }

    public void btChronometer_onClick(final View view) {
        Intent intent = new Intent(ActivityMain.this, ActivityChrono.class);
        startActivity(intent);

    }


    private boolean isUserDefined() {
        if (sessionCurrentUser == null) {
            Toast toast = Toast.makeText(ActivityMain.this,
                    "Не выбран пользатель. Создайте пользователя и сделайте его активным!", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }

    private boolean isDBNotEmpty() {

        List<Practice> list = new ArrayList<Practice>();
        if (sessionCurrentUser == null) {
            //list = DB.getAllActiveExercises();
        } else {
            list = DB.getAllActivePracticesOfUser(sessionCurrentUser.getID());
        }
        if (list.size() == 0) {
            Toast toast = Toast.makeText(ActivityMain.this,
                    "Отсутствуют активные занятия. Заполните список занятий!", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        } else {
            return true;

        }
    }

    public void btTools_onClick(final View view) {

        blink(view, this);
        Intent intent = new Intent(ActivityMain.this, ActivityTools.class);
        startActivity(intent);


    }

    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setMessage("Вы действительно хотите покинуть программу?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                }).setNegativeButton("Нет", null).show();

    }
}
