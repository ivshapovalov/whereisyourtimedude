package ru.brainworkout.whereisyourtimedude.activities;

import android.content.Intent;
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

import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import ru.brainworkout.whereisyourtimedude.R;
import ru.brainworkout.whereisyourtimedude.common.Common;
import ru.brainworkout.whereisyourtimedude.database.entities.Area;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.PracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.entities.Project;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

import java.util.LinkedList;
import java.util.List;

import static ru.brainworkout.whereisyourtimedude.common.Common.*;
import static ru.brainworkout.whereisyourtimedude.common.Session.*;


public class ActivityChrono extends AppCompatActivity {

    private final DatabaseManager DB = new DatabaseManager(this);
    private static PracticeHistory currentPracticeHistory;
    private static List<PracticeHistory> practices = new LinkedList<>();
    private static long currentDateInMillis;

    private Chronometer mChronometer;
    private boolean mChronometerIsWorking = false;
    private long mChronometerCount = 0;
    private long elapsedMillis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chrono);

        mChronometer = (Chronometer) findViewById(R.id.mChronometer);
        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                elapsedMillis = SystemClock.elapsedRealtime()
                        - mChronometer.getBase();

                if (elapsedMillis > 1000) {


                    changeTimer(elapsedMillis);

                }
            }
        });

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
        practices = DB.getAllPracticeAndPracticeHistoryOfUserByDates(sessionUser.getID(),currentDateInMillis, currentDateInMillis);

        //Collections.sort(practices, new PracticeHistoryComparatorByLastTime());
        currentPracticeHistory = practices.get(0);

        updateScreen();

    }

    public void btDay_onClick(View view) {
        Common.blink(view);

        String day=String.valueOf(((TextView) view).getText());
        defineNewDayPractice(ConvertStringToDate(day).getTime());

    }

    public void btDate_onClick(View view) {

        Common.blink(view);
        stopTimer();

        Intent intent = new Intent(ActivityChrono.this, ActivityCalendarView.class);
        intent.putExtra("CurrentActivity", "ActivityChrono");
        intent.putExtra("CurrentDateInMillis", currentDateInMillis);
        currentDateInMillis = 0;
        startActivity(intent);

    }

    private void defineNewDayPractice(Long date) {

         stopTimer();
        practices = DB.getAllPracticeAndPracticeHistoryOfUserByDates(sessionUser.getID(),date, date);
        //Collections.sort(practices, new PracticeHistoryComparatorByLastTime());
        currentPracticeHistory = practices.get(0);
        currentDateInMillis = date;

        mChronometerCount = currentPracticeHistory.getDuration() * 1000;
        updateScreen();
    }

    //
    private void stopTimer() {

        if (mChronometerIsWorking) {
            currentPracticeHistory.setDuration((int) ((SystemClock.elapsedRealtime() - mChronometer.getBase()) / 1000));
            currentPracticeHistory.setLastTime(Calendar.getInstance().getTimeInMillis());
            mChronometer.stop();
            mChronometerIsWorking = false;
        }

    }

    private void changeTimer(long elapsedMillis) {
        currentPracticeHistory.setDuration((int) ((SystemClock.elapsedRealtime() - mChronometer.getBase()) / 1000));
        currentPracticeHistory.setLastTime(Calendar.getInstance().getTimeInMillis());

        int tvTimerID = getResources().getIdentifier("tvCurrentWorkTime", "id", getPackageName());
        TextView tvTimer = (TextView) findViewById(tvTimerID);

        long time = (long) (elapsedMillis / 1000);
        String strTime = convertTimeToString(time);
        String txt = String.valueOf(strTime);
        tvTimer.setText(txt);


    }

    private String convertTimeToString(long time) {
        if (time == 0) {
            return "";
        }
        StringBuilder strTime = new StringBuilder();
        String hours = String.valueOf(time / 3600);
        String minutes = addingZeros(String.valueOf((time % 3600) / 60), 2);
        String seconds = addingZeros(String.valueOf(time % 60), 2);
        strTime.append(hours).append(":").append(minutes).append(":").append(seconds);


        return strTime.toString();
    }

    private String addingZeros(String s, int length) {
        for (int i = s.length(); i < length; i++) {
            s = "0" + s;
        }
        return s;
    }

    private void rowWork_onClick(TableRow view) {

        Common.blink(view);
        stopTimer();

        int index = practices.indexOf(String.valueOf(view.getId()));
        currentPractice = practices.get(index);
        currentPractice.setLastTime(Calendar.getInstance());

        mChronometerCount = currentPractice.getDuration() * 1000;
        mChronometer.setBase(SystemClock.elapsedRealtime() - mChronometerCount);
        mChronometerIsWorking = true;
        mChronometer.start();

        practices.remove(currentPractice);
        practices.addFirst(currentPractice);

        updateScreen();

    }

    public void rowCurrentWork_onClick(View view) {
        Common.blink(view);
        if (!mChronometerIsWorking) {

            if (mChronometerCount == 0) {
                mChronometer.setBase(SystemClock.elapsedRealtime());
            } else {
                mChronometer.setBase(SystemClock.elapsedRealtime() - mChronometerCount);
            }


            mChronometerIsWorking = true;
            mChronometer.start();

        } else {

            mChronometerCount = SystemClock.elapsedRealtime() - mChronometer.getBase();
            mChronometer.stop();
            mChronometerIsWorking = false;
            currentPracticeHistory.setLastTime(Calendar.getInstance().getTimeInMillis());
        }

    }


    private void updateScreen() {

        String areaName="";
        int areaColor=0;
        try {
            Practice practice = DB.getPractice(currentPracticeHistory.getIdPractice());
            Project project = DB.getProject(practice.getIdProject());
            Area area = DB.getArea(project.getIdArea());
            areaName=area.getName();
            areaColor=area.getColor();
        } catch (TableDoesNotContainElementException e) {

        }

        int tvIDCurrentName = getResources().getIdentifier("tvCurrentWorkName", "id", getPackageName());
        TextView tvCurrentName = (TextView) findViewById(tvIDCurrentName);
        if (tvCurrentName != null) {
            tvCurrentName.setText(DB.getPractice(currentPracticeHistory.getIdPractice()).getName());
        }
        int tvIDCurrentTime = getResources().getIdentifier("tvCurrentWorkTime", "id", getPackageName());
        TextView tvCurrentTime = (TextView) findViewById(tvIDCurrentTime);
        if (tvCurrentTime != null) {
            tvCurrentTime.setText(convertTimeToString(currentPracticeHistory.getDuration() / 1000));
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
                tvCurrentDate.setText(ConvertMillisToStringDate(currentPracticeHistory.getLastTime()) +" "+ ConvertMillisToStringTime(currentPracticeHistory.getLastTime()));
                ;
            } else {
                tvCurrentDate.setText("");
            }
        }
        int tableIDCurrentWork = getResources().getIdentifier("tableCurrentWork", "id", getPackageName());
        TableLayout tableCurrentWork = (TableLayout) findViewById(tableIDCurrentWork);
        if (tableCurrentWork != null) {
            tableCurrentWork.setBackgroundColor(areaColor);
        }


        int tableLayout = getResources().getIdentifier("tablePractices", "id", getPackageName());
        TableLayout table = (TableLayout) findViewById(tableLayout);
        if (table != null) {
            table.removeAllViews();
            for (int i = 1; i < practices.size(); i++
                    ) {
                TableRow mRow = CreateTableRow(i);
                table.addView(mRow);
            }

        }

    }

    //
//    @NonNull
//    private String convertCalendarToString(Calendar calendar) {
//        StringBuilder date = new StringBuilder("");
//        date.append(calendar.get(Calendar.YEAR)).append("-")
//                .append(calendar.get(Calendar.MONTH) + 1).append("-")
//                .append(calendar.get(Calendar.DAY_OF_MONTH)).append(" ")
//                .append(calendar.get(Calendar.HOUR_OF_DAY)).append(":")
//                .append(calendar.get(Calendar.MINUTE)).append(":")
//                .append(calendar.get(Calendar.SECOND)).append("");
//        return date.toString();
//    }
//
    @NonNull
    private TableRow CreateTableRow(int i) {
        PracticeHistory practiceHistory = practices.get(i);

        String practiceName="";
        String areaName="";
        int areaColor= Color.WHITE;
        try {
            Practice practice = DB.getPractice(practiceHistory.getIdPractice());
            practiceName=practice.getName();
            Project project = DB.getProject(practice.getIdProject());
            Area area = DB.getArea(project.getIdArea());
            areaName=area.getName();
            areaColor=area.getColor();
        } catch (TableDoesNotContainElementException e) {

        }
        TableRow rowMain = new TableRow(this);

        rowMain.setId(Integer.valueOf(practiceHistory.getId()));
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
        txtName.setLayoutParams(paramsTextView);
        row1.addView(txtName);

        TextView txtTime = new TextView(this);
        txtTime.setBackgroundColor(areaColor);
        txtTime.setText(convertTimeToString(practiceHistory.getDuration()));
        txtTime.setLayoutParams(paramsTextView);
        row1.addView(txtTime);

        layout.addView(row1);

        TableRow row2 = new TableRow(this);
        row2.setLayoutParams(paramsRow);

        TextView txtArea = new TextView(this);
        txtArea.setBackgroundColor(areaColor);
        txtArea.setText(areaName);
        txtArea.setLayoutParams(paramsTextView);
        row2.addView(txtArea);

        TextView txtDate = new TextView(this);
        txtDate.setBackgroundColor(areaColor);

        if (practiceHistory.getLastTime() != 0) {
            String date = ConvertMillisToStringDate(practiceHistory.getLastTime()) + " " + ConvertMillisToStringTime(practiceHistory.getLastTime());
            txtDate.setText(date);
        }


        txtDate.setLayoutParams(paramsTextView);

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


    public class PracticeHistoryComparatorByLastTime implements Comparator<PracticeHistory> {

        @Override
        public int compare(PracticeHistory w1, PracticeHistory w2) {

            return (int) (w2.getLastTime() - w1.getLastTime());


        }

    }


}
