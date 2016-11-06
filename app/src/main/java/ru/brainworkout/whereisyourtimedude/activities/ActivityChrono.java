package ru.brainworkout.whereisyourtimedude.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import ru.brainworkout.whereisyourtimedude.R;
import ru.brainworkout.whereisyourtimedude.common.BackgroundChronometer;
import ru.brainworkout.whereisyourtimedude.common.BackgroundChronometerService;
import ru.brainworkout.whereisyourtimedude.common.Common;
import ru.brainworkout.whereisyourtimedude.common.ConnectionParameters;
import ru.brainworkout.whereisyourtimedude.common.Constants;
import ru.brainworkout.whereisyourtimedude.common.Session;
import ru.brainworkout.whereisyourtimedude.database.entities.Area;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.PracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.entities.Project;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

import java.util.List;

import static ru.brainworkout.whereisyourtimedude.common.Common.*;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionBackgroundChronometer;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionCurrentUser;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionOpenActivities;


public class ActivityChrono extends AbstractActivity {

    private static PracticeHistory currentPracticeHistory;
    private static List<PracticeHistory> practiceHistories = new ArrayList<>();
    private static long currentDateInMillis;

    private Chronometer mChronometer;
    private Chronometer mChronometerEternity;
    private boolean mChronometerIsWorking = false;
    private long localChronometerCount = 0;
    private long elapsedMillis;
    private boolean isToday = true;

    private Intent backgroundServiceIntent;
    private TableLayout tableHistory;
    private ConnectionParameters params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chrono);
        int tableLayout = getResources().getIdentifier("tablePractices", "id", getPackageName());
        tableHistory = (TableLayout) findViewById(tableLayout);

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

                    if (!isToday) {
                        return;
                    }
                    if (currentPracticeHistory != null && sessionBackgroundChronometer != null && sessionBackgroundChronometer.getCurrentPracticeHistory() != null) {
                        if (currentPracticeHistory.getDate() < sessionBackgroundChronometer.getCurrentPracticeHistory().getDate()) {

                            autoChangeDay(sessionBackgroundChronometer.getCurrentPracticeHistory().getDate());
                        }
                    }

                    if (!mChronometerIsWorking && sessionBackgroundChronometer != null && sessionBackgroundChronometer.isTicking()) {
                        mChronometerIsWorking = true;
                        mChronometer.start();
                        updateAllRows();
                    } else if (mChronometerIsWorking && sessionBackgroundChronometer != null && !sessionBackgroundChronometer.isTicking()) {
                        mChronometerIsWorking = false;
                        mChronometer.stop();
                        updateAllRows();
                    }

                }
            }
        });
        mChronometerEternity.start();
        Intent intent = getIntent();
        currentDateInMillis = intent.getLongExtra("CurrentDateInMillis", 0);
        int id_practice = intent.getIntExtra("CurrentPracticeID", -1);

        if (currentDateInMillis == 0) {
            init();
        } else {

            if (currentDateInMillis != getBeginOfCurrentDateInMillis()) {
                isToday = false;
            }
            defineNewDayPractice(currentDateInMillis);
        }

        if (id_practice != -1) {
            if (DB.containsPractice(id_practice)) {
                Practice practice = DB.getPractice(id_practice);

                if (practice.getIsActive() == 1) {
                    for (PracticeHistory practiceHistory : practiceHistories
                            ) {
                        if (practiceHistory.getPractice() != null) {
                            if (practiceHistory.getPractice().getId() == id_practice) {
                                if (sessionBackgroundChronometer != null && sessionBackgroundChronometer.isTicking()) {
                                    startPracticeHistoryTimerOnEvent(practiceHistory.getId());
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        updateAllRows();

        //уберу до поры до времени.
//        SwipeDetectorActivity swipeDetectorActivity = new SwipeDetectorActivity(ActivityChrono.this);
//        ScrollView sv = (ScrollView) this.findViewById(R.id.scrollView);
//        sv.setOnTouchListener(swipeDetectorActivity);
    }

    private void init() {

        currentDateInMillis = getBeginOfCurrentDateInMillis();
        updatePractices(currentDateInMillis);

        if (practiceHistories.isEmpty()) {
            return;
        }

        currentPracticeHistory = practiceHistories.get(0);

        if (Session.sessionBackgroundChronometer != null) {

            if (Session.sessionBackgroundChronometer.isTicking()) {
                localChronometerCount = Session.sessionBackgroundChronometer.getGlobalChronometerCount();
                rowCurrentWork_onClick(new TextView(this));
            } else {
                localChronometerCount = currentPracticeHistory.getDuration();
                Session.sessionBackgroundChronometer.setGlobalChronometerCount(localChronometerCount);
            }


        } else {
            LOG.debug("init:before create background chronometer");
            Session.sessionBackgroundChronometer = new BackgroundChronometer();
            Session.sessionBackgroundChronometer.setCurrentPracticeHistory(currentPracticeHistory);
            Session.sessionBackgroundChronometer.setDB(DB);
            Session.sessionBackgroundChronometer.pauseTicking();
            Session.sessionBackgroundChronometer.start();
            localChronometerCount = currentPracticeHistory.getDuration();
            Session.sessionBackgroundChronometer.setGlobalChronometerCount(localChronometerCount);
        }
    }

    private void autoChangeDay(Long newDateInMillis) {
        mChronometerIsWorking = false;

        updatePractices(newDateInMillis);

        if (practiceHistories.isEmpty()) {
            return;
        }
        currentPracticeHistory = practiceHistories.get(0);
        currentDateInMillis = newDateInMillis;

        if (Session.sessionBackgroundChronometer.isAlive()) {

            if (Session.sessionBackgroundChronometer.isTicking()) {
                localChronometerCount = Session.sessionBackgroundChronometer.getGlobalChronometerCount();
                rowCurrentWork_onClick(new TextView(this));
            } else {
                localChronometerCount = currentPracticeHistory.getDuration();
                Session.sessionBackgroundChronometer.setGlobalChronometerCount(localChronometerCount);
            }
        }
        updateAllRows();
    }

    private void defineNewDayPractice(Long date) {

//        if (mChronometerIsWorking) {
//            stopTimer();
//        }
        updatePractices(date);

        if (practiceHistories.isEmpty()) {
            return;
        }
        currentPracticeHistory = practiceHistories.get(0);
        currentDateInMillis = date;

        if (isToday) {
            if (Session.sessionBackgroundChronometer.isAlive()) {
                if (Session.sessionBackgroundChronometer.isTicking()) {
                    localChronometerCount = Session.sessionBackgroundChronometer.getGlobalChronometerCount();
                    rowCurrentWork_onClick(new TextView(this));
                } else {
                    localChronometerCount = currentPracticeHistory.getDuration();
                    Session.sessionBackgroundChronometer.setGlobalChronometerCount(localChronometerCount);
                }
                Session.sessionBackgroundChronometer.setCurrentPracticeHistory(currentPracticeHistory);

            } else {
                backgroundServiceIntent.setAction(Constants.ACTION.SHOW_NOTIFICATION_TIMER);
                startService(backgroundServiceIntent);
                Session.sessionBackgroundChronometer.start();
                Session.sessionBackgroundChronometer.pauseTicking();
                Session.sessionBackgroundChronometer.setCurrentPracticeHistory(currentPracticeHistory);
                Session.sessionBackgroundChronometer.setDB(DB);
                localChronometerCount = currentPracticeHistory.getDuration();
                Session.sessionBackgroundChronometer.setGlobalChronometerCount(localChronometerCount);
            }
        }
    }

    //
    private void stopTimer() {

        if (mChronometerIsWorking) {
            currentPracticeHistory.setLastTime(Calendar.getInstance().getTimeInMillis());
            currentPracticeHistory.setDuration(sessionBackgroundChronometer.getGlobalChronometerCount());
            mChronometer.stop();
            localChronometerCount = sessionBackgroundChronometer.getGlobalChronometerCount();
            setTimerText(Common.SYMBOL_STOP, localChronometerCount);
            Session.sessionBackgroundChronometer.pauseTicking();
            mChronometerIsWorking = false;
            sessionBackgroundChronometer.updateNotification(Constants.ACTION.PAUSE_ACTION);

        } else {
        }
        currentPracticeHistory.dbSave(DB);
    }

    private void changeTimer() {
        if (sessionBackgroundChronometer != null && sessionBackgroundChronometer.getCurrentPracticeHistory() != null
                ) {
            if (currentPracticeHistory.getDate() < sessionBackgroundChronometer.getCurrentPracticeHistory().getDate()) {
            } else {
                localChronometerCount = sessionBackgroundChronometer.getGlobalChronometerCount();
                currentPracticeHistory.setDuration(localChronometerCount);
                currentPracticeHistory.setLastTime(Calendar.getInstance().getTimeInMillis());
            }
            setTimerText(Common.SYMBOL_PLAY, localChronometerCount);
        }
    }

    private void setTimerText(String symbol, long millis) {
        int tvTimerID = getResources().getIdentifier("tvCurrentWorkTime", "id", getPackageName());
        TextView tvTimer = (TextView) findViewById(tvTimerID);
        String strTime = convertMillisToStringWithAllTime(millis);
        String txt = symbol.concat(" ").concat(String.valueOf(strTime));
        tvTimer.setText(txt);
    }

    private void rowWork_onClick(TableRow view) {
        if (!isToday) {
            return;
        }

        LOG.debug("Timer start rowWork_onClick");
        blink(view, this);
        int id_practice_history = view.getId();
        startPracticeHistoryTimerOnEvent(id_practice_history);
    }

    private void startPracticeHistoryTimerOnEvent(int id_practice_history) {
        stopTimer();
        if (DB.containsPracticeHistory(id_practice_history)) {
            currentPracticeHistory = DB.getPracticeHistory(id_practice_history);
        } else {
            int index = practiceHistories.indexOf(new PracticeHistory.Builder(id_practice_history).build());
            currentPracticeHistory = practiceHistories.get(index);
        }
        currentPracticeHistory.setLastTime(Calendar.getInstance().getTimeInMillis());
        currentPracticeHistory.dbSave(DB);

        localChronometerCount = currentPracticeHistory.getDuration();
        LOG.debug("rowWork_onClick:before start service");

        backgroundServiceIntent = new Intent(this, BackgroundChronometerService.class);
        backgroundServiceIntent.setAction(Constants.ACTION.SHOW_NOTIFICATION_TIMER);
        LOG.debug("Before service start");
        startService(backgroundServiceIntent);

        Session.sessionBackgroundChronometer.setGlobalChronometerCount(localChronometerCount);
        Session.sessionBackgroundChronometer.setCurrentPracticeHistory(currentPracticeHistory);
        Session.sessionBackgroundChronometer.setDB(DB);
        Session.sessionBackgroundChronometer.resumeTicking();

        mChronometer.setBase(SystemClock.elapsedRealtime() - localChronometerCount);
        mChronometerIsWorking = true;
        mChronometer.start();

        updatePractices(currentDateInMillis);
        updateAllRows();
    }

    private void updatePractices(long date) {

        if (sessionCurrentUser != null) {
            practiceHistories = DB.getAllPracticeAndPracticeHistoryOfUserByDates(sessionCurrentUser.getId(), date, date);
        }

    }

    public void rowCurrentWork_onClick(View view) {

        if (!isToday) {
            return;
        }
        blink(view, this);
        if (!mChronometerIsWorking) {
            LOG.debug("Timer start currentWork_onClick");
            //startService(backgroundServiceIntent);
            if (sessionBackgroundChronometer.isAlive()) {
                backgroundServiceIntent = new Intent(this, BackgroundChronometerService.class);
                backgroundServiceIntent.setAction(Constants.ACTION.SHOW_NOTIFICATION_TIMER);
                LOG.debug("Before service start");
                startService(backgroundServiceIntent);
                sessionBackgroundChronometer.setCurrentPracticeHistory(currentPracticeHistory);
                if (!sessionBackgroundChronometer.isTicking()) {
                    sessionBackgroundChronometer.resumeTicking();
                }
            }

            if (localChronometerCount == 0) {
                mChronometer.setBase(SystemClock.elapsedRealtime());
                sessionBackgroundChronometer.setGlobalChronometerCount(0L);
            } else {
                mChronometer.setBase(SystemClock.elapsedRealtime() - localChronometerCount);
                sessionBackgroundChronometer.setGlobalChronometerCount(localChronometerCount);
            }

            mChronometerIsWorking = true;
            mChronometer.start();
            currentPracticeHistory.setLastTime(Calendar.getInstance().getTimeInMillis());

        } else {
            stopTimer();

        }
        updateAllRows();
    }


    private void updateAllRows() {

        String areaName = "";
        int areaColor = 0;

        Practice practice = currentPracticeHistory.getPractice();
        if (practice != null) {
            Project project = practice.getProject();
            if (project != null) {
                Area area = project.getArea();
                if (area != null) {
                    areaName = area.getName();
                    areaColor = area.getColor();
                }
            }
        }

        int tvIDCurrentDay = getResources().getIdentifier("tvCurrentDay", "id", getPackageName());
        TextView tvCurrentDay = (TextView) findViewById(tvIDCurrentDay);
        if (tvCurrentDay != null) {
            tvCurrentDay.setText(convertMillisToStringDate(currentDateInMillis));
        }

        int tvIDCurrentName = getResources().getIdentifier("tvCurrentWorkName", "id", getPackageName());
        TextView tvCurrentName = (TextView) findViewById(tvIDCurrentName);
        if (tvCurrentName != null) {
            if (currentPracticeHistory != null) {
                practice = currentPracticeHistory.getPractice();
                if (practice != null) {
                    tvCurrentName.setText(practice.getName());
                }
            }
        }

        if (mChronometerIsWorking) {
            setTimerText(Common.SYMBOL_PLAY, currentPracticeHistory.getDuration());
        } else {
            setTimerText(Common.SYMBOL_STOP, currentPracticeHistory.getDuration());
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
                tvCurrentDate.setText(convertMillisToStringDateTime(currentPracticeHistory.getLastTime()));
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
        for (int i = 1; i < practiceHistories.size(); i++
                ) {
            TableRow mRow = CreateTableRow(i);
            tableHistory.addView(mRow);
        }
    }

    @NonNull
    private TableRow CreateTableRow(int i) {
        PracticeHistory practiceHistory = practiceHistories.get(i);

        String practiceName = "";
        String areaName = "";
        int areaColor = Color.WHITE;
        Practice practice = practiceHistory.getPractice();
        if (practice != null) {
            practiceName = practice.getName();
            Project project = practice.getProject();
            if (project != null) {
                Area area = project.getArea();
                if (area != null) {
                    areaName = area.getName();
                    areaColor = area.getColor();
                }
            }
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
        txtName.setLayoutParams(paramsTextView);
        row1.addView(txtName);

        TextView txtTime = new TextView(this);
        txtTime.setBackgroundColor(areaColor);
        txtTime.setText(convertMillisToStringWithAllTime(practiceHistory.getDuration()));
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
            String date = convertMillisToStringDateTime(practiceHistory.getLastTime());
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

    public void onBackPressed() {

//        if (!mChronometerIsWorking) {
//            sessionBackgroundChronometer.pauseTicking();
//            //Session.sessionBackgroundChronometer.updateNotification(Common.SYMBOL_STOP);
//            if (sessionBackgroundChronometer.getService() != null) {
//                sessionBackgroundChronometer.getService().stopForeground(true);
//                if (backgroundServiceIntent != null) {
//                    stopService(backgroundServiceIntent);
//                }
//            }
//            sessionBackgroundChronometer.interrupt();
//
//        }
        LOG.debug("Close ActivityChrono");
        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        LOG.debug("ActivityChrono away from screen ");
        super.onDestroy();
    }

    public void tv_AddNewPractice_onClick(View view) {

        blink(view, this);

        ConnectionParameters paramsNew = new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityChrono")
                .isTransmitterNew(false)
                .isTransmitterForChoice(false)
                .addReceiverActivityName("ActivityPractice")
                .isReceiverNew(true)
                .isReceiverForChoice(false)
                .build();
        sessionOpenActivities.push(paramsNew);
        Intent intent = new Intent(getApplicationContext(), ActivityPractice.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void tvDate_onClick(View view) {

        LOG.debug("TvDate on click ");
        blink(view, this);

        Intent intent = new Intent(ActivityChrono.this, ActivityCalendarView.class);
        intent.putExtra("CurrentActivity", "ActivityChrono");
        intent.putExtra("CurrentDateInMillis", currentDateInMillis);
        currentDateInMillis = 0;
        startActivity(intent);

    }

    //пока не использовать.
    private class SwipeDetectorActivity extends AppCompatActivity implements View.OnTouchListener {

        private Activity activity;
        static final int MIN_DISTANCE = 200;
        private float downX, downY, upX, upY;

        public SwipeDetectorActivity(final Activity activity) {
            this.activity = activity;
        }

        public final void onRightToLeftSwipe() {
            // System.out.println("Right to Left swipe [Previous]");
            Toast.makeText(ActivityChrono.this, "[Следующий день]", Toast.LENGTH_SHORT).show();
            long nextDateInMillis = currentDateInMillis + 3600 * 24 * 1000;
            isToday = nextDateInMillis == getBeginOfCurrentDateInMillis();
            defineNewDayPractice(nextDateInMillis);
            updateAllRows();

        }

        public void onLeftToRightSwipe() {
            // System.out.println("Left to Right swipe [Next]");
            Toast.makeText(ActivityChrono.this, "[Предыдущий день]", Toast.LENGTH_SHORT).show();
            long previousDateInMillis = currentDateInMillis - 3600 * 24 * 1000;
            isToday = previousDateInMillis == getBeginOfCurrentDateInMillis();
            defineNewDayPractice(previousDateInMillis);
            updateAllRows();
        }

        public void onTopToBottomSwipe() {

            //Toast.makeText(ActivityTraining.this, "Top to Bottom swipe [Down]", Toast.LENGTH_SHORT).show();

        }

        public void onBottomToTopSwipe() {

            //Toast.makeText(ActivityTraining.this, "Bottom to Top swipe [Up]", Toast.LENGTH_SHORT).show ();

        }


        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    downX = event.getX();
                    downY = event.getY();
                    return true;
                    //break;
                }
                case MotionEvent.ACTION_UP: {
                    upX = event.getX();
                    upY = event.getY();

                    float deltaX = downX - upX;
                    float deltaY = downY - upY;

                    // swipe horizontal?
                    if (Math.abs(deltaX) > MIN_DISTANCE) {
                        // left or right
                        //Toast.makeText(ActivityTraining.this, "DeltaX="+String.valueOf(deltaX), Toast.LENGTH_SHORT).show();
                        if (deltaX < 0) {
                            this.onLeftToRightSwipe();
                            return true;
                        }
                        if (deltaX > 0) {
                            this.onRightToLeftSwipe();
                            return true;
                        }
                    } else {

                    }

                    // swipe vertical?
                    if (Math.abs(deltaY) > MIN_DISTANCE) {
                        // top or down
                        //Toast.makeText(ActivityTraining.this, "DeltaY="+String.valueOf(deltaY), Toast.LENGTH_SHORT).show();
                        if (deltaY < 0) {
                            this.onTopToBottomSwipe();
                            //break;
                            return true;
                        }
                        if (deltaY > 0) {
                            this.onBottomToTopSwipe();
                            //break;

                            return true;
                        }
                    } else {

                    }
                    //break;
                    return true;
                }
            }
            //Toast.makeText(ActivityTraining.this, "ЖОПА", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
