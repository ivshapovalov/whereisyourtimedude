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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import ru.brainworkout.whereisyourtimedude.R;
import ru.brainworkout.whereisyourtimedude.common.Area;
import ru.brainworkout.whereisyourtimedude.common.Common;
import ru.brainworkout.whereisyourtimedude.common.PracticeTimer;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import static ru.brainworkout.whereisyourtimedude.common.Common.areas;
import static ru.brainworkout.whereisyourtimedude.common.Common.practices;
import static ru.brainworkout.whereisyourtimedude.common.Common.DB;

public class ActivityChrono extends AppCompatActivity {

    private static PracticeTimer currentPractice;
    private static String currentDate;

    private Chronometer mChronometer;
    private boolean mChronometerIsWorking = false;
    private long mChronometerCount = 0; //millis
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

                    //elapsedMillis=0;
                }
            }
        });

        Intent intent = getIntent();

        String mCurrentDate = intent.getStringExtra("CurrentDate");
        int id = intent.getIntExtra("CurrentPracticeID", 0);


        if (("".equals(mCurrentDate)) || mCurrentDate == null) {
            init();
        } else {
            defineNewDayPractice(mCurrentDate);
        }


    }

    private void init() {

        StringBuilder date = new StringBuilder();
        Calendar curDate = Calendar.getInstance();
        date.append(curDate.get(Calendar.YEAR)).append("-")
                .append(addingZeros(String.valueOf(curDate.get(Calendar.MONTH) + 1), 2)).append("-")
                .append(addingZeros(String.valueOf(curDate.get(Calendar.DAY_OF_MONTH)), 2));
        createNewDayPractices(date.toString());

        Collections.sort(practices, new WorkComparatorByLastTime());
        currentPractice = practices.get(0);
        currentDate = date.toString();

        updateScreen();


    }

    private void createNewDayPractices(String date) {
        practices = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            int indexArea = ((int) (Math.random() * areas.size()));
            practices.add(new PracticeTimer(i, "WORK " + String.valueOf(i), areas.get(indexArea), 0));
        }
        DB.put(date, practices);

    }

    public void bt1_onClick(View view) {
        Common.blink(view);
        defineNewDayPractice("2016-08-30");

    }

    public void bt2_onClick(View view) {
        Common.blink(view);
        defineNewDayPractice("2016-08-31");

    }

    public void bt3_onClick(View view) {
        Common.blink(view);
        defineNewDayPractice("2016-09-05");

    }

    public void btDate_onClick(View view) {

        Common.blink(view);
        stopTimer();


        Intent intent = new Intent(ActivityChrono.this, ActivityCalendarView.class);
        intent.putExtra("CurrentActivity", "ActivityChrono");
        intent.putExtra("CurrentDate", currentDate);
        intent.putExtra("CurrentPracticeID", currentPractice.getId());
        currentDate = null;
        startActivity(intent);

    }

    private void defineNewDayPractice(String date) {

        if (currentDate != null && date.equals(currentDate)) {
            return;
        }
        stopTimer();
        if (DB.containsKey(date)) {
            practices = DB.get(date);
        } else {
            if (currentDate != null) {
                if (date.compareTo(currentDate) < 0) {
                    System.out.println("дата меньше текущей");
                    createNewDayPractices(date);
                } else {
                    System.out.println("дата больше текущей");
                    createNewDayPractices(date);
                }
            } else {
                createNewDayPractices(date);
            }

        }
        Collections.sort(practices, new WorkComparatorByLastTime());
        currentPractice = practices.get(0);
        currentDate = date;

        mChronometerCount = currentPractice.getDuration() * 1000;
        updateScreen();
    }

    private void stopTimer() {

        if (mChronometerIsWorking) {
            currentPractice.setDuration((int) ((SystemClock.elapsedRealtime() - mChronometer.getBase()) / 1000));
            currentPractice.setLastTime(Calendar.getInstance());
            mChronometer.stop();
            mChronometerIsWorking = false;
        }

    }

    private void changeTimer(long elapsedMillis) {
        currentPractice.setDuration((int) ((SystemClock.elapsedRealtime() - mChronometer.getBase()) / 1000));
        currentPractice.setLastTime(Calendar.getInstance());

        int tvTimerID = getResources().getIdentifier("tvCurrentWorkTime", "id", getPackageName());
        TextView tvTimer = (TextView) findViewById(tvTimerID);

        int time = (int) (elapsedMillis / 1000);
        String strTime = convertTimeToString(time);
        String txt = String.valueOf(strTime);
        tvTimer.setText(txt);


    }

    private String convertTimeToString(int time) {
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

        int index = practices.indexOf(new PracticeTimer(view.getId(), String.valueOf(view.getId())));
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
            currentPractice.setLastTime(Calendar.getInstance());
        }

    }


    private void updateScreen() {

        int tvIDCurrentName = getResources().getIdentifier("tvCurrentWorkName", "id", getPackageName());
        TextView tvCurrentName = (TextView) findViewById(tvIDCurrentName);
        if (tvCurrentName != null) {
            tvCurrentName.setText(currentPractice.getName());
        }
        int tvIDCurrentTime = getResources().getIdentifier("tvCurrentWorkTime", "id", getPackageName());
        TextView tvCurrentTime = (TextView) findViewById(tvIDCurrentTime);
        if (tvCurrentTime != null) {
            tvCurrentTime.setText(convertTimeToString(currentPractice.getDuration()));
        }
        int tvIDCurrentArea = getResources().getIdentifier("tvCurrentWorkArea", "id", getPackageName());
        TextView tvCurrentArea = (TextView) findViewById(tvIDCurrentArea);
        if (tvCurrentArea != null) {
            tvCurrentArea.setText(currentPractice.getArea().getName());
        }
        int tvIDCurrentDate = getResources().getIdentifier("tvCurrentWorkDate", "id", getPackageName());
        TextView tvCurrentDate = (TextView) findViewById(tvIDCurrentDate);
        if (tvCurrentDate != null) {
            if (currentPractice.getLastTime() != null) {
                tvCurrentDate.setText(convertCalendarToString(currentPractice.getLastTime()));
            } else {
                tvCurrentDate.setText("");
            }
        }
        int tableIDCurrentWork = getResources().getIdentifier("tableCurrentWork", "id", getPackageName());
        TableLayout tableCurrentWork = (TableLayout) findViewById(tableIDCurrentWork);
        if (tableCurrentWork != null) {
            tableCurrentWork.setBackgroundColor(currentPractice.getArea().getColor());
        }


        int tableLayout = getResources().getIdentifier("tableWorks", "id", getPackageName());
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

    @NonNull
    private String convertCalendarToString(Calendar calendar) {
        StringBuilder date = new StringBuilder("");
        date.append(calendar.get(Calendar.YEAR)).append("-")
                .append(calendar.get(Calendar.MONTH) + 1).append("-")
                .append(calendar.get(Calendar.DAY_OF_MONTH)).append(" ")
                .append(calendar.get(Calendar.HOUR_OF_DAY)).append(":")
                .append(calendar.get(Calendar.MINUTE)).append(":")
                .append(calendar.get(Calendar.SECOND)).append("");
        return date.toString();
    }

    @NonNull
    private TableRow CreateTableRow(int i) {
        PracticeTimer practiceTimer = practices.get(i);
        TableRow rowMain = new TableRow(this);

        rowMain.setId(Integer.valueOf(practiceTimer.getId()));

        TableRow.LayoutParams params100 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params100.weight = 100;
        params100.topMargin = 10;
        TableRow.LayoutParams params50 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params50.weight = 50;

        TableLayout layout = new TableLayout(this);
        layout.setLayoutParams(params100);

        TableRow row1 = new TableRow(this);
        row1.setLayoutParams(params100);

        TextView txtName = new TextView(this);
        txtName.setBackgroundColor(practiceTimer.getArea().getColor());
        txtName.setText(practiceTimer.getName());
        txtName.setLayoutParams(params50);
        row1.addView(txtName);

        TextView txtTime = new TextView(this);
        txtTime.setBackgroundColor(practiceTimer.getArea().getColor());
        txtTime.setText(convertTimeToString(practiceTimer.getDuration()));
        txtTime.setLayoutParams(params50);
        row1.addView(txtTime);

        layout.addView(row1);

        TableRow row2 = new TableRow(this);
        row2.setLayoutParams(params100);

        TextView txtArea = new TextView(this);
        txtArea.setBackgroundColor(practiceTimer.getArea().getColor());
        txtArea.setText(practiceTimer.getArea().getName());
        txtArea.setLayoutParams(params50);
        row2.addView(txtArea);


        TextView txtDate = new TextView(this);
        txtDate.setBackgroundColor(practiceTimer.getArea().getColor());

        if (practiceTimer.getLastTime() != null) {
            String date = convertCalendarToString(practiceTimer.getLastTime());
            txtDate.setText(date);
        }


        txtDate.setLayoutParams(params50);

        row2.addView(txtDate);

        layout.addView(row2);

        rowMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowWork_onClick((TableRow) v);
            }
        });
        rowMain.addView(layout);
        return rowMain;
    }


    public class WorkComparatorByLastTime implements Comparator<PracticeTimer> {

        @Override
        public int compare(PracticeTimer w1, PracticeTimer w2) {

            if (w1.getLastTime() == null && w2.getLastTime() == null) {
                return 0;
            } else if (w1.getLastTime() == null) {
                return 1;
            } else if (w2.getLastTime() == null) {
                return -1;
            }
            return (int) (w2.getLastTime().getTimeInMillis() - w1.getLastTime().getTimeInMillis());


        }

    }


}
