package ru.ivan.whereisyourtimedude.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ru.ivan.whereisyourtimedude.R;

import static ru.ivan.whereisyourtimedude.common.Common.*;
import static ru.ivan.whereisyourtimedude.common.Session.*;

import ru.ivan.whereisyourtimedude.common.BackgroundChronometer;
import ru.ivan.whereisyourtimedude.common.BackgroundChronometerService;

import ru.ivan.whereisyourtimedude.common.Session;
import ru.ivan.whereisyourtimedude.database.entities.Options;
import ru.ivan.whereisyourtimedude.database.entities.Practice;
import ru.ivan.whereisyourtimedude.database.entities.PracticeHistory;


public class ActivityMain extends AbstractActivity {

    private static final int MAX_VERTICAL_BUTTON_COUNT = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getOptionsFromDB();
        showElementsOnScreen();
        resumeChronoIfWorking();

    }

    public void btTest_onClick(View view) {
        if (Session.sessionBackgroundChronometer != null && Session.sessionBackgroundChronometer.getService() != null) {
            sessionBackgroundChronometer.getService().stopForeground(true);
            sessionBackgroundChronometer.getService().stopSelf();
        }
    }

    private void resumeChronoIfWorking() {
        if (sessionOptions != null && sessionOptions.getRecoveryOnRunSwitch() == 1) {
            if (sessionBackgroundChronometer != null &&
                    sessionBackgroundChronometer.isAlive() &&
                    sessionBackgroundChronometer.isTicking()) {
            } else {
                if (sessionBackgroundChronometer == null) {
                    sessionBackgroundChronometer = new BackgroundChronometer();
                }

                synchronized (sessionBackgroundChronometer) {
                    resumeBackgroundChronometer();
                }
            }
        }
    }

    private void resumeBackgroundChronometer() {
        PracticeHistory resumedPracticeHistory = DB.getLastPracticeHistoryOfUserByDates(Session.sessionCurrentUser.getId(), 0, System.currentTimeMillis());
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
                            .addPractice(resumedPracticeHistory.getPractice())
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
                        .addPractice(resumedPracticeHistory.getPractice())
                        .addLastTime(System.currentTimeMillis())
                        .addDuration(duration)
                        .build();

            } else {

                long beginTimeinMillis = resumedPracticeHistory.getLastTime() - resumedPracticeHistory.getDuration() * 1_000;
                duration = (System.currentTimeMillis() - beginTimeinMillis) / 1_000;
            }
            Session.sessionBackgroundChronometer = new BackgroundChronometer();
            Session.sessionBackgroundChronometer.setCurrentPracticeHistory(resumedPracticeHistory);

            Intent backgroundServiceIntent = new Intent(this, BackgroundChronometerService.class);
            startService(backgroundServiceIntent);
            Session.sessionBackgroundChronometer.setDB(DB);
            Session.sessionBackgroundChronometer.setGlobalChronometerCount(duration);
            Session.sessionBackgroundChronometer.start();
            if (sessionChronometerIsWorking) {
                Session.sessionBackgroundChronometer.resumeTicking();
            }
        }
    }

    private void getOptionsFromDB() {
        Options options = null;
        if (Session.sessionCurrentUser != null) {
            options = DB.getOptionsOfUser(Session.sessionCurrentUser.getId());
            if (options == null) {
                options = new Options.Builder(DB).addRecoverySwitch(0)
                        .addDisplaySwitch(0)
                        .addSaveInterval(1)
                        .addChronoIsWorking(0)
                        .build();
                options.dbSave(DB);
            }
            Session.sessionOptions = options;
            if (options.getRecoveryOnRunSwitch() == 1) {
                sessionChronometerIsWorking = options.getChronoIsWorking() == 1 ? true : false;
            }
            Session.saveInterval = options.getSaveInterval();
        }
    }

    private void showElementsOnScreen() {

//        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
//        int mHeight = displaymetrics.heightPixels / MAX_VERTICAL_BUTTON_COUNT;
//        for (int i = 1; i <= MAX_VERTICAL_BUTTON_COUNT; i++) {
//            int btID = getResources().getIdentifier("btMain" + String.valueOf(i), "id", getPackageName());
//            Button btName = (Button) findViewById(btID);
//            if (btName != null) {
//                btName.setHeight(mHeight);
//            }
//        }
    }


    public void btUsers_onClick(final View view) {

        Intent intent = new Intent(ActivityMain.this, ActivityUsersList.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void btAreas_onClick(final View view) {

        if (isUserDefined()) {
            Intent intent = new Intent(ActivityMain.this, ActivityAreasList.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void btProjects_onClick(final View view) {
        if (isUserDefined()) {
            Intent intent = new Intent(ActivityMain.this, ActivityProjectsList.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void btPractices_onClick(final View view) {
        if (isUserDefined()) {
            Intent intent = new Intent(ActivityMain.this, ActivityPracticesList.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void btPracticeHistory_onClick(final View view) {
        if (isUserDefined()) {
            Intent intent = new Intent(ActivityMain.this, ActivityPracticeHistoryList.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void btDetailedPracticeHistory_onClick(final View view) {
        if (isUserDefined()) {
            Intent intent = new Intent(ActivityMain.this, ActivityDetailedPracticeHistoryList.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void btChronometer_onClick(final View view) {
        if (isUserDefined()) {
            Intent intent = new Intent(ActivityMain.this, ActivityChrono.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    private boolean isDBNotEmpty() {

        List<Practice> list = new ArrayList<Practice>();
        if (sessionCurrentUser == null) {
        } else {
            list = DB.getAllActivePracticesOfUser(sessionCurrentUser.getId());
        }
        if (list.size() == 0) {
            Toast toast = Toast.makeText(ActivityMain.this,
                    "There are no active practices. Complete list of practices!", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        } else {
            return true;
        }
    }

    public void btTools_onClick(final View view) {
        blink(view, this);
        Intent intent = new Intent(ActivityMain.this, ActivityTools.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Do you want to exit the program?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        if (sessionBackgroundChronometer != null
                                && sessionBackgroundChronometer.getService() != null) {
                            sessionBackgroundChronometer.getService().stopForeground(true);
                            sessionBackgroundChronometer.getService().stopSelf();
                            sessionBackgroundChronometer.interrupt();
                        }
                    }
                }).setNegativeButton("No", null).show();
    }
}
