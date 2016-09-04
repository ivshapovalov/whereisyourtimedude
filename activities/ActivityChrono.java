package ru.brainworkout.whereisyourtimedude.activities;

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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class ActivityChrono extends AppCompatActivity {

    private static PracticeTimer currentPractice;

    private Chronometer mChronometer;
    private boolean mChronometerIsWorking = false;
    private long mChronometerCount = 0; //millis
    private long elapsedMillis;

    ArrayList<Area> areas;
    LinkedList<PracticeTimer> practices = new LinkedList<>();
    Map<String, LinkedList<PracticeTimer>> DB = new HashMap<>();

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
        init();
    }

    private void init() {

        areas = new ArrayList<>();

        areas.add(new Area(Color.GREEN, "GREEN"));
        areas.add(new Area(Color.RED, "RED"));
        areas.add(new Area(Color.YELLOW, "YELLOW"));
        areas.add(new Area(Color.BLUE, "BLUE"));
        areas.add(new Area(Color.GRAY, "GRAY"));

        StringBuilder date = new StringBuilder();
        Calendar curDate = Calendar.getInstance();
        date.append(curDate.get(Calendar.YEAR)).append("_")
                .append(addingZeros(String.valueOf(curDate.get(Calendar.MONTH) + 1), 2)).append("_")
                .append(addingZeros(String.valueOf(curDate.get(Calendar.DAY_OF_MONTH)), 2));
        createNewDayPractices(date.toString());

        Collections.sort(practices, new WorkComparatorByLastTime());
        currentPractice = practices.get(0);

        updateScreen();

    }

    private void createNewDayPractices(String date) {
        practices = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            int indexArea = ((int) (Math.random() * areas.size()));
            practices.add(new PracticeTimer(i, String.valueOf(i), areas.get(indexArea), 0));
        }
        DB.put(date, practices);

    }

    public void bt1_onClick(View view) {
        newDayTest("2016_08_30");

    }

    public void bt2_onClick(View view) {
        newDayTest("2016_08_31");

    }

    public void bt3_onClick(View view) {
        newDayTest("2016_09_03");

    }

    public void bt4_onClick(View view) {
        newDayTest("2016_09_04");

    }

    private void newDayTest(String date) {

        stopTimer();
        if (DB.containsKey(date)) {
            practices = DB.get(date);
        } else {
            createNewDayPractices(date);
        }
        Collections.sort(practices, new WorkComparatorByLastTime());
        currentPractice = practices.get(0);

        mChronometerCount = currentPractice.getDuration()*1000;
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

    private void rowWork_onClick(TableRow v) {

        stopTimer();

        int index = practices.indexOf(new PracticeTimer(v.getId(), String.valueOf(v.getId())));
        currentPractice = practices.get(index);
        currentPractice.setLastTime(Calendar.getInstance());

        mChronometerCount = currentPractice.getDuration()*1000;
        mChronometer.setBase(SystemClock.elapsedRealtime() - mChronometerCount);
        mChronometerIsWorking = true;
        mChronometer.start();

        practices.remove(currentPractice);
        practices.addFirst(currentPractice);

        updateScreen();

    }

    public void rowCurrentWork_onClick(View view) {
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
            }
        }

        int rowIDCurrentWork = getResources().getIdentifier("rowCurrentWork", "id", getPackageName());
        TableRow rowCurrentWork = (TableRow) findViewById(rowIDCurrentWork);
        if (rowCurrentWork != null) {
            rowCurrentWork.setBackgroundColor(currentPractice.getArea().getColor());
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

    private class PracticeTimer {
        int id;
        String name;
        int duration;
        Calendar lastTime;
        String date;
        Area area;

        @Override
        public boolean equals(Object obj) {
            return this.getId() == ((PracticeTimer) obj).getId();
        }

        @Override
        public int hashCode() {
            return 1;
        }

        public PracticeTimer(int id, String name, Area area) {
            this.area = area;
            this.id = id;
            this.name = name;
        }

        public PracticeTimer(int id, String name, Area area, int duration) {
            this.id = id;
            this.name = name;
            this.duration = duration;
            this.area = area;
        }

        public PracticeTimer(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public Calendar getLastTime() {
            return lastTime;
        }

        public void setLastTime(Calendar lastTime) {
            this.lastTime = lastTime;
        }

        public Area getArea() {
            return area;
        }

        public void setArea(Area area) {
            this.area = area;
        }
    }

    class Area {
        int color;
        String name;

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Area(int color, String name) {

            this.color = color;
            this.name = name;
        }
    }

}
