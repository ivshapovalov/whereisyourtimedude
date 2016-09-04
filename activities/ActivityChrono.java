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

import java.util.LinkedList;

import ru.brainworkout.whereisyourtimedude.R;


public class ActivityChrono extends AppCompatActivity {

    private static Work currentWork;

    private Chronometer mChronometer;
    private boolean mChronometerIsWorking = false;
    private long mChronometerCount = 0;
    private long elapsedMillis;


    LinkedList<Work> works = new LinkedList<>();

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

        ArrayList<Area> areas = new ArrayList<>();

        areas.add(new Area(Color.GREEN, "GREEN"));
        areas.add(new Area(Color.RED, "RED"));
        areas.add(new Area(Color.YELLOW, "YELLOW"));
        areas.add(new Area(Color.BLUE, "BLUE"));
        areas.add(new Area(Color.GRAY, "GRAY"));


        for (int i = 0; i < 10; i++) {
            int indexArea = ((int) (Math.random() * areas.size()));
            works.add(new Work( i, String.valueOf(i),areas.get(indexArea),7195));
        }

        currentWork = works.get(0);
        updateScreen();


    }

    private void changeTimer(long elapsedMillis) {
        currentWork.setSeconds((int) ((SystemClock.elapsedRealtime() - mChronometer.getBase()) / 1000));
        currentWork.setDate(Calendar.getInstance());

        int tvTimerID = getResources().getIdentifier("tvCurrentWorkTime", "id", getPackageName());
        TextView tvTimer = (TextView) findViewById(tvTimerID);

        int time = (int) (elapsedMillis / 1000);
        String strTime = convertTimeToString(time);
        String txt = String.valueOf(strTime);
        tvTimer.setText(txt);


    }

    private String convertTimeToString(int time) {
        if (time==0) {
            return "";
        }
        StringBuilder strTime = new StringBuilder();
        String hours = String.valueOf(time / 3600);
        String minutes = AddingZeros(String.valueOf((time % 3600) / 60), 2);
        String seconds = AddingZeros(String.valueOf(time % 60), 2);
        strTime.append(hours).append(":").append(minutes).append(":").append(seconds);


        return strTime.toString();
    }

    private String AddingZeros(String s, int length) {
        for (int i = s.length(); i < length; i++) {
            s = "0" + s;
        }
        return s;
    }

    private void rowWork_onClick(TableRow v) {
        System.out.println(v.getId());

        currentWork.setSeconds((int) ((SystemClock.elapsedRealtime() - mChronometer.getBase()) / 1000));
        currentWork.setDate(Calendar.getInstance());

        int index = works.indexOf(new Work(v.getId(), String.valueOf(v.getId())));
        currentWork = works.get(index);
        currentWork.setDate(Calendar.getInstance());
        mChronometerCount = currentWork.getSeconds();
        mChronometer.setBase(SystemClock.elapsedRealtime() - mChronometerCount * 1000);
        mChronometerIsWorking = true;
        mChronometer.start();

        works.remove(currentWork);
        works.add(5, currentWork);

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
            currentWork.setDate(Calendar.getInstance());
        }

    }


    private void updateScreen() {

        int tvIDCurrentName = getResources().getIdentifier("tvCurrentWorkName", "id", getPackageName());
        TextView tvCurrentName = (TextView) findViewById(tvIDCurrentName);
        if (tvCurrentName != null) {
            tvCurrentName.setText(currentWork.getName());
        }
        int tvIDCurrentTime = getResources().getIdentifier("tvCurrentWorkTime", "id", getPackageName());
        TextView tvCurrentTime = (TextView) findViewById(tvIDCurrentTime);
        if (tvCurrentTime != null) {
            tvCurrentTime.setText(convertTimeToString(currentWork.getSeconds()));
        }
        int tvIDCurrentArea = getResources().getIdentifier("tvCurrentWorkArea", "id", getPackageName());
        TextView tvCurrentArea = (TextView) findViewById(tvIDCurrentArea);
        if (tvCurrentArea != null) {
            tvCurrentArea.setText(currentWork.getArea().getName());
        }
        int tvIDCurrentDate = getResources().getIdentifier("tvCurrentWorkDate", "id", getPackageName());
        TextView tvCurrentDate = (TextView) findViewById(tvIDCurrentDate);
        if (tvCurrentDate != null) {
            if (currentWork.getDate() != null) {
                tvCurrentDate.setText(convertCalendarToString(currentWork.getDate()));
            }
        }

        int rowIDCurrentWork = getResources().getIdentifier("rowCurrentWork", "id", getPackageName());
        TableRow rowCurrentWork = (TableRow) findViewById(rowIDCurrentWork);
        if (rowCurrentWork != null) {
            rowCurrentWork.setBackgroundColor(currentWork.getArea().getColor());
        }


        int tableLayout = getResources().getIdentifier("tableWorks", "id", getPackageName());
        TableLayout table = (TableLayout) findViewById(tableLayout);
        if (table != null) {
            table.removeAllViews();
            for (int i = 1; i < works.size(); i++
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
        Work work = works.get(i);
        TableRow rowMain = new TableRow(this);

        rowMain.setId(Integer.valueOf(work.getId()));

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
        txtName.setBackgroundColor(work.getArea().getColor());
        txtName.setText(work.getName());
        txtName.setLayoutParams(params50);
        row1.addView(txtName);

        TextView txtTime = new TextView(this);
        txtTime.setBackgroundColor(work.getArea().getColor());
        txtTime.setText(convertTimeToString(work.getSeconds()));
        txtTime.setLayoutParams(params50);
        row1.addView(txtTime);

        layout.addView(row1);

        TableRow row2 = new TableRow(this);
        row2.setLayoutParams(params100);

        TextView txtArea = new TextView(this);
        txtArea.setBackgroundColor(work.getArea().getColor());
        txtArea.setText(work.getArea().getName());
        txtArea.setLayoutParams(params50);
        row2.addView(txtArea);


        TextView txtDate = new TextView(this);
        txtDate.setBackgroundColor(work.getArea().getColor());

        if (work.getDate() != null) {
            String date = convertCalendarToString(work.getDate());
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

    public void btUpdate_onClick(View view) {

        Collections.sort(works, new WorkComparatorByDate());
        updateScreen();

    }


    public class WorkComparatorByDate implements Comparator<Work> {

        @Override
        public int compare(Work w1, Work w2) {

            if (w1.getDate() == null && w2.getDate() == null) {
                return 0;
            } else if (w1.getDate() == null) {
                return 1;
            } else if (w2.getDate() == null) {
                return -1;
            }
            return (int) (w2.getDate().getTimeInMillis() - w1.getDate().getTimeInMillis());


        }

    }

    private class Work {
        int id;
        String name;
        int seconds;
        Calendar date;
        Area area;

        @Override
        public boolean equals(Object obj) {
            return this.getId() == ((Work) obj).getId();
        }

        @Override
        public int hashCode() {
            return 1;
        }

        public Work(int id, String name,Area area) {
            this.area = area;
            this.id = id;
            this.name = name;
        }

        public Work(int id, String name, Area area,int seconds) {
            this.id = id;
            this.name = name;
            this.seconds = seconds;
            this.area = area;
        }

        public Work(int id, String name) {
            this.id = id;
            this.name = name;
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

        public int getSeconds() {
            return seconds;
        }

        public void setSeconds(int seconds) {
            this.seconds = seconds;
        }

        public Calendar getDate() {
            return date;
        }

        public void setDate(Calendar date) {
            this.date = date;
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
