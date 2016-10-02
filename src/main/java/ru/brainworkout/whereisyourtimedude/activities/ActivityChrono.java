package ru.brainworkout.whereisyourtimedude.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import ru.brainworkout.whereisyourtimedude.R;
import ru.brainworkout.whereisyourtimedude.common.BackgroundChronometer;
import ru.brainworkout.whereisyourtimedude.common.Session;
import ru.brainworkout.whereisyourtimedude.database.entities.Area;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.PracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.entities.Project;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

import java.util.List;

import static ru.brainworkout.whereisyourtimedude.common.Common.*;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionBackgroundChronometer;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionCurrentUser;


public class ActivityChrono extends AppCompatActivity {

    private SharedPreferences mSettings;
    private final DatabaseManager DB = new DatabaseManager(this);
    private static PracticeHistory currentPracticeHistory;
    private static List<PracticeHistory> practices = new ArrayList<>();
    private static long currentDateInMillis;

    private Chronometer mChronometer;
    private Chronometer mChronometerEternity;
    private boolean mChronometerIsWorking = false;
    private long localChronometerCountInSeconds = 0;
    private long elapsedMillis;

    private TableLayout tableHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chrono);

        int tableLayout = getResources().getIdentifier("tablePractices", "id", getPackageName());
        tableHistory = (TableLayout) findViewById(tableLayout);

        mSettings = getSharedPreferences(ActivityMain.APP_PREFERENCES, Context.MODE_PRIVATE);

        mChronometer = (Chronometer) findViewById(R.id.mChronometer);
        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                elapsedMillis = SystemClock.elapsedRealtime()
                        - mChronometer.getBase();

                if (elapsedMillis > 1000) {


                    changeTimer();

                }
            }
        });

        mChronometerEternity = (Chronometer) findViewById(R.id.mChronometerEternity);
        mChronometerEternity.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedMillis = SystemClock.elapsedRealtime()
                        - mChronometerEternity.getBase();

                if (elapsedMillis > 1000) {


                    if (currentPracticeHistory.getDate() < sessionBackgroundChronometer.getCurrentPracticeHistory().getDate()) {

                        autoChangeDay(sessionBackgroundChronometer.getCurrentPracticeHistory().getDate());
                    }

                }
            }
        });
        mChronometerEternity.start();

        Intent intent = getIntent();

        currentDateInMillis = intent.getLongExtra("CurrentDateInMillis", 0);

        if (currentDateInMillis == 0) {

            init();
        } else {
            defineNewDayPractice(currentDateInMillis);
        }


    }

    private void init() {

        Calendar calendar = Calendar.getInstance();
        calendar.clear(Calendar.HOUR);
        calendar.clear(Calendar.HOUR_OF_DAY);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        currentDateInMillis = calendar.getTimeInMillis();
//        calendar.add(Calendar.DAY_OF_YEAR,1);
//        long endOfDayInMillis=calendar.getTimeInMillis();
        updatePractices(currentDateInMillis);
        //Collections.sort(practices, new PracticeHistoryComparatorByLastTime());

        if (practices.isEmpty()) {
            return;
        }

        currentPracticeHistory = practices.get(0);


        if (Session.sessionBackgroundChronometer.isAlive()) {

            if (Session.sessionBackgroundChronometer.isTicking()) {
                localChronometerCountInSeconds = Session.sessionBackgroundChronometer.getGlobalChronometerCountInSeconds();
                rowCurrentWork_onClick(new TextView(this));
            } else {
                localChronometerCountInSeconds = currentPracticeHistory.getDuration();
                Session.sessionBackgroundChronometer.setGlobalChronometerCountInSeconds(localChronometerCountInSeconds);
            }


        } else {
            Session.sessionBackgroundChronometer = new BackgroundChronometer();
            Session.sessionBackgroundChronometer.setCurrentPracticeHistory(currentPracticeHistory);
            Session.sessionBackgroundChronometer.setDB(DB);
            Session.sessionBackgroundChronometer.start();
            Session.sessionBackgroundChronometer.pauseTicking();
            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean(ActivityMain.APP_PREFERENCES_CHRONO_IS_WORKING, false);
            editor.apply();
            localChronometerCountInSeconds = currentPracticeHistory.getDuration();
            Session.sessionBackgroundChronometer.setGlobalChronometerCountInSeconds(localChronometerCountInSeconds);

        }

        updateAllRows();


    }

    public void tvDate_onClick(View view) {

//        blink(view, this);
//        stopTimer();
//
//        Intent intent = new Intent(ActivityChrono.this, ActivityCalendarView.class);
//        intent.putExtra("CurrentActivity", "ActivityChrono");
//        intent.putExtra("CurrentDateInMillis", currentDateInMillis);
//        currentDateInMillis = 0;
//        startActivity(intent);

    }

    private void autoChangeDay(Long newDateInMillis) {
        mChronometerIsWorking = false;

        updatePractices(newDateInMillis);

        if (practices.isEmpty()) {
            return;
        }
        currentPracticeHistory = practices.get(0);
        currentDateInMillis = newDateInMillis;

        if (Session.sessionBackgroundChronometer.isAlive()) {

            if (Session.sessionBackgroundChronometer.isTicking()) {
                localChronometerCountInSeconds = Session.sessionBackgroundChronometer.getGlobalChronometerCountInSeconds();
                rowCurrentWork_onClick(new TextView(this));
            } else {
                localChronometerCountInSeconds = currentPracticeHistory.getDuration();
                Session.sessionBackgroundChronometer.setGlobalChronometerCountInSeconds(localChronometerCountInSeconds);
            }
        }
        updateAllRows();
    }

    private void defineNewDayPractice(Long date) {
        if (mChronometerIsWorking) {
            stopTimer();
        }
        updatePractices(date);

        if (practices.isEmpty()) {
            return;
        }
        currentPracticeHistory = practices.get(0);
        currentDateInMillis = date;

        if (Session.sessionBackgroundChronometer.isAlive()) {

            if (Session.sessionBackgroundChronometer.isTicking()) {
                localChronometerCountInSeconds = Session.sessionBackgroundChronometer.getGlobalChronometerCountInSeconds();
                rowCurrentWork_onClick(new TextView(this));
            } else {
                localChronometerCountInSeconds = currentPracticeHistory.getDuration();
                Session.sessionBackgroundChronometer.setGlobalChronometerCountInSeconds(localChronometerCountInSeconds);
            }
            Session.sessionBackgroundChronometer.setCurrentPracticeHistory(currentPracticeHistory);

        } else {
            Session.sessionBackgroundChronometer.start();
            Session.sessionBackgroundChronometer.pauseTicking();
            Session.sessionBackgroundChronometer.setCurrentPracticeHistory(currentPracticeHistory);
            Session.sessionBackgroundChronometer.setDB(DB);
            localChronometerCountInSeconds = currentPracticeHistory.getDuration();
            Session.sessionBackgroundChronometer.setGlobalChronometerCountInSeconds(localChronometerCountInSeconds);

            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean(ActivityMain.APP_PREFERENCES_CHRONO_IS_WORKING, false);
            editor.apply();

        }
        updateAllRows();

    }

    //
    private void stopTimer() {

        if (mChronometerIsWorking) {
            currentPracticeHistory.setLastTime(Calendar.getInstance().getTimeInMillis());
            currentPracticeHistory.setDuration(sessionBackgroundChronometer.getGlobalChronometerCountInSeconds());
            mChronometer.stop();
            Session.sessionBackgroundChronometer.pauseTicking();
            mChronometerIsWorking = false;

            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean(ActivityMain.APP_PREFERENCES_CHRONO_IS_WORKING, false);
            editor.apply();
        } else {
        }
        currentPracticeHistory.dbSave(DB);

    }

    private void changeTimer() {


        if (currentPracticeHistory.getDate() < sessionBackgroundChronometer.getCurrentPracticeHistory().getDate()) {


        } else {

            localChronometerCountInSeconds = sessionBackgroundChronometer.getGlobalChronometerCountInSeconds();
            currentPracticeHistory.setDuration(localChronometerCountInSeconds);
            currentPracticeHistory.setLastTime(Calendar.getInstance().getTimeInMillis());
        }


        int tvTimerID = getResources().getIdentifier("tvCurrentWorkTime", "id", getPackageName());
        TextView tvTimer = (TextView) findViewById(tvTimerID);

        String strTime = ConvertMillisToStringWithAllTime(localChronometerCountInSeconds * 1000);
        String txt = String.valueOf(strTime);
        tvTimer.setText(txt);


    }


    private String addingZeros(String s, int length) {
        for (int i = s.length(); i < length; i++) {
            s = "0" + s;
        }
        return s;
    }

    private void rowWork_onClick(TableRow view) {

        blink(view, this);
        stopTimer();

        int id_practice_history = view.getId();
        if (DB.containsPracticeHistory(id_practice_history)) {
            currentPracticeHistory = DB.getPracticeHistory(id_practice_history);
        } else {
            int index = practices.indexOf(new PracticeHistory.Builder(id_practice_history).build());
            currentPracticeHistory = practices.get(index);
        }
        currentPracticeHistory.setLastTime(Calendar.getInstance().getTimeInMillis());
        currentPracticeHistory.dbSave(DB);

        localChronometerCountInSeconds = currentPracticeHistory.getDuration();
        Session.sessionBackgroundChronometer.setGlobalChronometerCountInSeconds(localChronometerCountInSeconds);
        Session.sessionBackgroundChronometer.setCurrentPracticeHistory(currentPracticeHistory);
        Session.sessionBackgroundChronometer.setDB(DB);
        Session.sessionBackgroundChronometer.resumeTicking();
        mChronometer.setBase(SystemClock.elapsedRealtime() - localChronometerCountInSeconds);
        mChronometerIsWorking = true;
        mChronometer.start();

        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(ActivityMain.APP_PREFERENCES_CHRONO_IS_WORKING, true);
        editor.apply();

        updatePractices(currentDateInMillis);
        updateAllRows();
    }

    private void updatePractices(long date) {

        practices = DB.getAllPracticeAndPracticeHistoryOfUserByDates(sessionCurrentUser.getID(), date, date);

    }

    public void rowCurrentWork_onClick(View view) {
        blink(view, this);
        if (!mChronometerIsWorking) {

            if (Session.sessionBackgroundChronometer.isAlive()) {
                Session.sessionBackgroundChronometer.resumeTicking();
            }

            if (localChronometerCountInSeconds == 0) {
                mChronometer.setBase(SystemClock.elapsedRealtime());
                Session.sessionBackgroundChronometer.setGlobalChronometerCountInSeconds(0L);
            } else {
                mChronometer.setBase(SystemClock.elapsedRealtime() - localChronometerCountInSeconds);
                Session.sessionBackgroundChronometer.setGlobalChronometerCountInSeconds(localChronometerCountInSeconds);
            }

            mChronometerIsWorking = true;
            mChronometer.start();
            currentPracticeHistory.setLastTime(Calendar.getInstance().getTimeInMillis());

            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean(ActivityMain.APP_PREFERENCES_CHRONO_IS_WORKING, true);
            editor.apply();

        } else {

            Session.sessionBackgroundChronometer.pauseTicking();

            localChronometerCountInSeconds = sessionBackgroundChronometer.getGlobalChronometerCountInSeconds();
            mChronometer.stop();
            mChronometerIsWorking = false;
            currentPracticeHistory.setLastTime(Calendar.getInstance().getTimeInMillis());
            currentPracticeHistory.dbSave(DB);

            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean(ActivityMain.APP_PREFERENCES_CHRONO_IS_WORKING, false);
            editor.apply();

        }
        updateAllRows();

    }


    private void updateAllRows() {

        String areaName = "";
        int areaColor = 0;
        try {
            Practice practice = DB.getPractice(currentPracticeHistory.getIdPractice());
            Project project = DB.getProject(practice.getIdProject());
            Area area = DB.getArea(project.getIdArea());
            areaName = area.getName();
            areaColor = area.getColor();
        } catch (TableDoesNotContainElementException e) {

        }

        int tvIDCurrentDay = getResources().getIdentifier("tvCurrentDay", "id", getPackageName());
        TextView tvCurrentDay = (TextView) findViewById(tvIDCurrentDay);
        if (tvCurrentDay != null) {
            tvCurrentDay.setText(ConvertMillisToStringDate(currentDateInMillis));
        }

        int tvIDCurrentName = getResources().getIdentifier("tvCurrentWorkName", "id", getPackageName());
        TextView tvCurrentName = (TextView) findViewById(tvIDCurrentName);
        if (tvCurrentName != null) {
            tvCurrentName.setText(DB.getPractice(currentPracticeHistory.getIdPractice()).getName());
        }
        int tvIDCurrentTime = getResources().getIdentifier("tvCurrentWorkTime", "id", getPackageName());
        TextView tvCurrentTime = (TextView) findViewById(tvIDCurrentTime);
        if (tvCurrentTime != null) {
            tvCurrentTime.setText(ConvertMillisToStringWithAllTime(currentPracticeHistory.getDuration() * 1000));
        }
        int tvIDCurrentArea = getResources().getIdentifier("tvCurrentWorkArea", "id", getPackageName());
        TextView tvCurrentArea = (TextView) findViewById(tvIDCurrentArea);
        if (tvCurrentArea != null) {
            tvCurrentArea.setText(areaName);
        }
        int tvIDCurrentDate = getResources().getIdentifier("tvCurrentWorkDate", "id", getPackageName());
        TextView tvCurrentDate = (TextView) findViewById(tvIDCurrentDate);
        if (tvCurrentDate != null) {
            if (currentPracticeHistory.getLastTime() != 0) {
                tvCurrentDate.setText(ConvertMillisToStringDateTime(currentPracticeHistory.getLastTime()));

            } else {
                tvCurrentDate.setText("");
            }
        }
        int tableIDCurrentWork = getResources().getIdentifier("tableCurrentWork", "id", getPackageName());
        TableLayout tableCurrentWork = (TableLayout) findViewById(tableIDCurrentWork);
        if (tableCurrentWork != null) {
            tableCurrentWork.setBackgroundColor(areaColor);
        }

        tableHistory.removeAllViews();
        for (int i = 1; i < practices.size(); i++
                ) {
            TableRow mRow = CreateTableRow(i);
            tableHistory.addView(mRow);
        }
    }

      @NonNull
    private TableRow CreateTableRow(int i) {
        PracticeHistory practiceHistory = practices.get(i);

        String practiceName = "";
        String areaName = "";
        int areaColor = Color.WHITE;
        try {
            Practice practice = DB.getPractice(practiceHistory.getIdPractice());
            practiceName = practice.getName();
            Project project = DB.getProject(practice.getIdProject());
            Area area = DB.getArea(project.getIdArea());
            areaName = area.getName();
            areaColor = area.getColor();
        } catch (TableDoesNotContainElementException e) {

        }

        TableRow rowMain = new TableRow(this);

        rowMain.setId(practiceHistory.getId());
        TableRow.LayoutParams paramsLayout = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        paramsLayout.weight = 100;
        paramsLayout.topMargin = 10;

        TableRow.LayoutParams paramsRow = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        paramsRow.weight = 100;

        TableRow.LayoutParams paramsTextView = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT);
        paramsTextView.weight = 50;

        TableLayout layout = new TableLayout(this);
        layout.setLayoutParams(paramsLayout);
        layout.setStretchAllColumns(true);

        TableRow row1 = new TableRow(this);
        row1.setLayoutParams(paramsRow);

        TextView txtName = new TextView(this);
        txtName.setBackgroundColor(areaColor);
        txtName.setText(practiceName);
        // txtName.setMinimumHeight(mHeight);
        txtName.setLayoutParams(paramsTextView);
        row1.addView(txtName);

        TextView txtTime = new TextView(this);
        txtTime.setBackgroundColor(areaColor);
        //txtTime.setText(String.valueOf(practiceHistory.getDuration()));
        txtTime.setText(ConvertMillisToStringWithAllTime(practiceHistory.getDuration() * 1000));
        txtTime.setLayoutParams(paramsTextView);
        //txtTime.setMinimumHeight(mHeight);
        row1.addView(txtTime);

        layout.addView(row1);

        TableRow row2 = new TableRow(this);
        row2.setLayoutParams(paramsRow);


        TextView txtArea = new TextView(this);
        txtArea.setBackgroundColor(areaColor);
        txtArea.setText(areaName);
        txtArea.setLayoutParams(paramsTextView);
        //txtArea.setMinimumHeight(mHeight);
        row2.addView(txtArea);

        TextView txtDate = new TextView(this);
        txtDate.setBackgroundColor(areaColor);

        if (practiceHistory.getLastTime() != 0) {
            String date = ConvertMillisToStringDateTime(practiceHistory.getLastTime());
            txtDate.setText(date);
        }


        txtDate.setLayoutParams(paramsTextView);
        // txtDate.setMinimumHeight(mHeight);

        row2.addView(txtDate);

        layout.addView(row2);

        rowMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowWork_onClick((TableRow) v);
            }
        });
        rowMain.addView(layout);
        rowMain.setLayoutParams(paramsRow);
        return rowMain;
    }

    public void onBackPressed() {

        if (!mChronometerIsWorking) {
            Session.sessionBackgroundChronometer.pauseTicking();
            Session.sessionBackgroundChronometer.interrupt();

            SharedPreferences.Editor editor = mSettings.edit();
            editor.putBoolean(ActivityMain.APP_PREFERENCES_CHRONO_IS_WORKING, false);
            editor.apply();

        }
        finish();

    }

}
