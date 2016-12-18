package ru.brainworkout.whereisyourtimedude.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.ScrollView;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.brainworkout.whereisyourtimedude.common.Common.*;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionBackgroundChronometer;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionCurrentUser;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionOpenActivities;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionOptions;

public class ActivityChrono extends AbstractActivity implements NavigationView.OnNavigationItemSelectedListener {
    private PracticeHistory currentPracticeHistory;
    private List<PracticeHistory> practiceHistories = new ArrayList<>();
    private long currentDateInMillis;

    private Chronometer mChronometer;
    private Chronometer mChronometerEternity;
    private boolean mChronometerIsWorking = false;
    private long localChronometerCount = 0;
    private long elapsedMillis;
    private boolean isToday = true;

    private Intent backgroundServiceIntent;
    private TableLayout tableHistory;
    private ConnectionParameters params;

    private int rows_number = 17;
    Map<Integer, List<PracticeHistory>> pagedPracticeHistories = new HashMap<>();
    private int currentPage = 1;
    private Area filterArea;
    private Project filterProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chrono);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


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

        if (Session.sessionOptions != null) {
            rows_number = sessionOptions.getRowNumberInLists();
        }

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
                    if (currentPracticeHistory != null && sessionBackgroundChronometer != null &&
                            sessionBackgroundChronometer.getCurrentPracticeHistory() != null) {
                        if (currentPracticeHistory.getDate() < sessionBackgroundChronometer.getCurrentPracticeHistory().getDate()) {
                            autoChangeDay(sessionBackgroundChronometer.getCurrentPracticeHistory().getDate());
                        }
                    }

                    if (!mChronometerIsWorking && sessionBackgroundChronometer != null && sessionBackgroundChronometer.isTicking()) {
                        mChronometerIsWorking = true;
                        mChronometer.start();
                        showHistories();
                    } else if (mChronometerIsWorking && sessionBackgroundChronometer != null && !sessionBackgroundChronometer.isTicking()) {
                        mChronometerIsWorking = false;
                        mChronometer.stop();
                        showHistories();
                    }

                }
            }
        });
        mChronometerEternity.start();
        Intent intent = getIntent();
        currentDateInMillis = intent.getLongExtra("CurrentDateInMillis", 0);
        int id_practice = intent.getIntExtra("CurrentPracticeID", -1);
        int idAreaFilter = intent.getIntExtra("CurrentAreaID", -1);
        if (DB.containsArea(idAreaFilter)) {
            filterArea = DB.getArea(idAreaFilter);
        }
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

        showHistories();

        //replace .
//        SwipeDetectorActivity swipeDetectorActivity = new SwipeDetectorActivity(ActivityChrono.this);
//        ScrollView sv = (ScrollView) this.findViewById(R.id.scrollView);
//        sv.setOnTouchListener(swipeDetectorActivity);
    }

    private void init() {

        currentDateInMillis = getBeginOfCurrentDateInMillis();
        updateAndPagePractices(currentDateInMillis);

        if (practiceHistories.isEmpty()) {
            return;
        }

        currentPracticeHistory = practiceHistories.get(0);

        if (Session.sessionBackgroundChronometer != null) {

            if (Session.sessionBackgroundChronometer.isTicking()) {
                localChronometerCount = Session.sessionBackgroundChronometer.getGlobalChronometerCount();
                mChronometerIsWorking = false;
                rowCurrentWork_onClick(new TextView(this));
            } else {
                localChronometerCount = currentPracticeHistory.getDuration();
                Session.sessionBackgroundChronometer.setGlobalChronometerCount(localChronometerCount);
            }
        } else {
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

        updateAndPagePractices(newDateInMillis);

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
        showHistories();
    }

    private void defineNewDayPractice(Long date) {

        updateAndPagePractices(date);

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
            currentPracticeHistory.dbSave(DB);
        } else {
        }
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
            currentPracticeHistory.setID(DB.getPracticeHistoryMaxNumber() + 1);
        }
        currentPracticeHistory.setLastTime(Calendar.getInstance().getTimeInMillis());
        currentPracticeHistory.dbSave(DB);

        localChronometerCount = currentPracticeHistory.getDuration();

        backgroundServiceIntent = new Intent(this, BackgroundChronometerService.class);
        backgroundServiceIntent.setAction(Constants.ACTION.SHOW_NOTIFICATION_TIMER);
        startService(backgroundServiceIntent);

        Session.sessionBackgroundChronometer.setGlobalChronometerCount(localChronometerCount);
        Session.sessionBackgroundChronometer.setCurrentPracticeHistory(currentPracticeHistory);
        Session.sessionBackgroundChronometer.setDB(DB);
        Session.sessionBackgroundChronometer.resumeTicking();

        mChronometer.setBase(SystemClock.elapsedRealtime() - localChronometerCount);
        mChronometerIsWorking = true;
        mChronometer.start();

        updateAndPagePractices(currentDateInMillis);
        showHistories();
    }

    private void updateAndPagePractices(long date) {

        if (sessionCurrentUser != null) {
            if (filterArea == null && filterProject == null) {
                practiceHistories = DB.getAllPracticeAndPracticeHistoryOfUserByDates(sessionCurrentUser.getId(), date, date);
            } else {
                if (filterArea != null) {
                    practiceHistories = DB.getAllPracticeAndPracticeHistoryOfUserAndAreaByDates(sessionCurrentUser.getId()
                            , filterArea.getId(), date, date);
                } else {
                    practiceHistories = DB.getAllPracticeAndPracticeHistoryOfUserAndProjectByDates(sessionCurrentUser.getId()
                            , filterProject.getId(), date, date);
                }
            }
            pagedPracticeHistories.clear();
            currentPage = 1;
            if (practiceHistories.size() != 0) {
                List<PracticeHistory> pageContent = new ArrayList<>();
                int pageNumber = 1;
                int initNumber = 1;
                if (mChronometerIsWorking) {
                    if (currentPracticeHistory.getPractice() != null &&
                            currentPracticeHistory.getPractice() != null &&
                            currentPracticeHistory.getPractice().getProject() != null &&
                            currentPracticeHistory.getPractice().getProject().getArea() != null &&
                            currentPracticeHistory.getPractice().getProject().getArea().equals(filterArea)
                            ) {

                    } else if (filterArea == null) {
                        initNumber = 1;
                    } else {
                        initNumber = 0;
                    }
                } else {
                    pageContent.add(practiceHistories.get(0));
                    pagedPracticeHistories.put(0, pageContent);
                }
                pageContent = new ArrayList<>();
                for (int i = initNumber; i < practiceHistories.size(); i++) {
                    pageContent.add(practiceHistories.get(i));
                    if (pageContent.size() == rows_number) {
                        pagedPracticeHistories.put(pageNumber, pageContent);
                        pageContent = new ArrayList<>();
                        pageNumber++;
                    }
                }
                if (pageContent.size() != 0) {
                    pagedPracticeHistories.put(pageNumber, pageContent);
                }
                if (practiceHistories.size() == 1) {
                    currentPage = 0;
                }
                if (!mChronometerIsWorking) {
                    currentPracticeHistory = practiceHistories.get(0);
                    if (sessionBackgroundChronometer != null) {
                        sessionBackgroundChronometer.setGlobalChronometerCount(currentPracticeHistory.getDuration());
                    }
                }
            } else {
                currentPage = 0;
            }
        }
    }

    public void rowCurrentWork_onClick(View view) {

        if (!isToday || currentPracticeHistory == null) {
            return;
        }
        blink(view, this);
        if (!mChronometerIsWorking) {
            //startService(backgroundServiceIntent);
            if (sessionBackgroundChronometer.isAlive()) {
                backgroundServiceIntent = new Intent(this, BackgroundChronometerService.class);
                backgroundServiceIntent.setAction(Constants.ACTION.SHOW_NOTIFICATION_TIMER);
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
        showHistories();
    }

    private void showHistories() {

        Button pageNumber = (Button) findViewById(R.id.btPageNumber);
        if (pageNumber != null) {
            pageNumber.setText(String.valueOf(currentPage) + "/" +
                    (pagedPracticeHistories.size() == 0 ? 0 : pagedPracticeHistories.size() - 1));
        }

        String areaName = "";
        String projectName = "";
        int areaColor = 0;

        if (currentPracticeHistory != null) {
            Practice practice = currentPracticeHistory.getPractice();
            if (practice != null) {
                Project project = practice.getProject();
                if (project != null) {
                    projectName = project.getName();
                    Area area = project.getArea();
                    if (area != null) {
                        areaName = area.getName();
                        areaColor = area.getColor();
                    }
                }
            }
        }

        int tvIDCurrentDay = getResources().getIdentifier("tvCurrentDay", "id", getPackageName());
        TextView tvCurrentDay = (TextView) findViewById(tvIDCurrentDay);
        if (tvCurrentDay != null) {
            tvCurrentDay.setText(convertMillisToStringDate(currentDateInMillis));
        }

        int lineAreaFilterId = getResources().getIdentifier("lineAreaFilter", "id", getPackageName());
        LinearLayout lineAreaFilter = (LinearLayout) findViewById(lineAreaFilterId);
        if (lineAreaFilter != null) {
            List<Area> areas = DB.getAllAreasOfUser(sessionCurrentUser.getId());
            TableRow.LayoutParams paramsTxt = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);

            paramsTxt.setMargins(3, 3, 3, 3);
            lineAreaFilter.removeAllViews();

            for (Area area : areas
                    ) {
                TextView txtArea = new TextView(this);
                txtArea.setText(area.getName());
                txtArea.setId(area.getId());
                txtArea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        txtAreaFilter_onClick((TextView) v);
                    }
                });
                txtArea.setBackgroundColor(area.getColor());
                txtArea.setLayoutParams(paramsTxt);
                lineAreaFilter.addView(txtArea);

            }

        }
        //drawer

        updateMenuItems(-1);

        int tvIDCurrentName = getResources().getIdentifier("tvCurrentWorkName", "id", getPackageName());
        TextView tvCurrentName = (TextView) findViewById(tvIDCurrentName);
        if (tvCurrentName != null) {
            if (currentPracticeHistory != null) {
                Practice practice = currentPracticeHistory.getPractice();
                if (practice != null) {
                    tvCurrentName.setText(practice.getName());
                }
            }
        }

        if (currentPracticeHistory != null) {
            if (mChronometerIsWorking) {
                setTimerText(Common.SYMBOL_PLAY, currentPracticeHistory.getDuration());
            } else {
                setTimerText(Common.SYMBOL_STOP, currentPracticeHistory.getDuration());
            }
        }
        int tvIDCurrentArea = getResources().getIdentifier("tvCurrentWorkArea", "id", getPackageName());
        TextView tvCurrentArea = (TextView) findViewById(tvIDCurrentArea);
        if (tvCurrentArea != null) {
            tvCurrentArea.setText(projectName + " - " + areaName);
        }
        int tvIDCurrentDate = getResources().getIdentifier("tvCurrentWorkDate", "id", getPackageName());
        TextView tvCurrentDate = (TextView) findViewById(tvIDCurrentDate);
        if (tvCurrentDate != null) {
            if (currentPracticeHistory != null) {
                if (currentPracticeHistory.getLastTime() != 0) {
                    tvCurrentDate.setText(convertMillisToStringDateTime(currentPracticeHistory.getLastTime()));
                } else {
                    tvCurrentDate.setText("");
                }
            }
        }
        int tableIDCurrentWork = getResources().getIdentifier("tableCurrentWork", "id", getPackageName());
        TableLayout tableCurrentWork = (TableLayout) findViewById(tableIDCurrentWork);
        if (tableCurrentWork != null) {
            tableCurrentWork.setBackgroundColor(areaColor);
        }

        tableHistory.removeAllViews();

        List<PracticeHistory> page = pagedPracticeHistories.get(currentPage);
        if (page == null || currentPage == 0) return;
        int currentPageSize = page.size();
        for (int num = 0; num < currentPageSize; num++) {
            TableRow mRow = CreateTableRow(num);
            tableHistory.addView(mRow);
        }

        //focus to first row
        int mScrID = getResources().getIdentifier("scrollView", "id", getPackageName());
        ScrollView mScrollView = (ScrollView) findViewById(mScrID);
        if (mScrollView != null) {
            int firstRowID = getResources().getIdentifier(String.valueOf(page.get(0).getId()), "id", getPackageName());
            TableRow firstRow = (TableRow) findViewById(firstRowID);
            if (firstRow != null) {
                mScrollView.requestChildFocus(firstRow, firstRow);
            }
        }
    }

    private void txtAreaFilter_onClick(TextView view) {
        blink(view, this);
        int idArea = view.getId();
        showFilteredHistories(idArea, -1);

    }


    private void showFilteredHistories(int idArea, int idProject) {
        String message = "";
        if (DB.containsArea(idArea)) {
            filterArea = DB.getArea(idArea);
            message = String.format("List filtered by Area '%s'", filterArea.getName());
        } else {
            filterArea = null;
            //message = "Filter is clear";
        }

        if (DB.containsProject(idProject)) {
            filterProject = DB.getProject(idProject);
            message = String.format("List filtered by Project '%s'", filterProject.getName());
        } else {
            filterProject = null;
            //message = "Filter is clear";
        }
        updateAndPagePractices(currentDateInMillis);
        showHistories();

        Toast.makeText(ActivityChrono.this, message, Toast.LENGTH_SHORT).show();
    }

    public void txtAreaFilterClear_onClick(View view) {
        blink(view, this);
        showFilteredHistories(-1, -1);
    }

    @NonNull
    private TableRow CreateTableRow(int i) {
        PracticeHistory practiceHistory = pagedPracticeHistories.get(currentPage).get(i);

        String practiceName = "";
        String projectName = "";
        String areaName = "";
        int areaColor = Color.WHITE;
        Practice practice = practiceHistory.getPractice();
        if (practice != null) {
            practiceName = practice.getName();
            Project project = practice.getProject();
            if (project != null) {
                projectName = project.getName();
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
        row1.setBackgroundColor(areaColor);

        TextView txtName = new TextView(this);
        txtName.setText(practiceName);
        txtName.setLayoutParams(paramsTextView);
        row1.addView(txtName);

        TextView txtTime = new TextView(this);
        txtTime.setText(convertMillisToStringWithAllTime(practiceHistory.getDuration()));
        txtTime.setLayoutParams(paramsTextView);
        row1.addView(txtTime);
        layout.addView(row1);

        TableRow row2 = new TableRow(this);
        row2.setBackgroundColor(areaColor);
        row2.setLayoutParams(paramsRow);
        TextView txtArea = new TextView(this);
        txtArea.setText(projectName + " - " + areaName);
        txtArea.setLayoutParams(paramsTextView);
        row2.addView(txtArea);

        TextView txtDate = new TextView(this);
        //txtDate.setBackgroundColor(areaColor);
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


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
            Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }


    @Override
    protected void onDestroy() {
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

        blink(view, this);

        Intent intent = new Intent(ActivityChrono.this, ActivityCalendarView.class);
        intent.putExtra("CurrentActivity", "ActivityChrono");
        intent.putExtra("CurrentDateInMillis", currentDateInMillis);
        currentDateInMillis = 0;
        startActivity(intent);

    }

    public void btNextPage_onClick(View view) {
        blink(view, this);

        if (currentPage != pagedPracticeHistories.size() - 1 && pagedPracticeHistories.size() != 0) {
            currentPage++;
        }
        showHistories();
    }

    public void btPreviousPage_onClick(View view) {
        blink(view, this);
        if (currentPage > 1) {
            currentPage--;
        }
        showHistories();
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
            Toast.makeText(ActivityChrono.this, "[Next day]", Toast.LENGTH_SHORT).show();
            long nextDateInMillis = currentDateInMillis + 3600 * 24 * 1000;
            isToday = nextDateInMillis == getBeginOfCurrentDateInMillis();
            defineNewDayPractice(nextDateInMillis);
            showHistories();

        }

        public void onLeftToRightSwipe() {
            // System.out.println("Left to Right swipe [Next]");
            Toast.makeText(ActivityChrono.this, "[Previous day]", Toast.LENGTH_SHORT).show();
            long previousDateInMillis = currentDateInMillis - 3600 * 24 * 1000;
            isToday = previousDateInMillis == getBeginOfCurrentDateInMillis();
            defineNewDayPractice(previousDateInMillis);
            showHistories();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_chrono_menu_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_options) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        int idGroup = item.getGroupId();
        if (idGroup ==0) {
            if (id == 1) {
                updateMenuItems(1);
            } else if (id == 2) {
                updateMenuItems(2);
            }

        } else {
            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            Menu menu = null;
            if (navigationView != null) {
                menu = navigationView.getMenu();
            }
            if (idGroup == 1) {
                if (menu != null) {
                    menu.getItem(0).setChecked(true);
                    menu.getItem(1).setChecked(false);
                }
                showFilteredHistories(id, -1);
            } else {
                if (menu != null) {
                    menu.getItem(0).setChecked(false);
                    menu.getItem(1).setChecked(true);                }
                showFilteredHistories(-1, id);
            }

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    private void updateMenuItems(int type) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        if (navigationView != null) {
            Menu menu = navigationView.getMenu();

            menu.removeGroup(1);
            menu.removeGroup(2);

            if (filterArea == null && filterProject == null && type == -1) {
                menu.removeGroup(0);
                menu.add(0, 1, 0, "AREA FILTER").setChecked(false);
                menu.add(0, 2, 0, "PROJECT FILTER").setChecked(false);
                return;
            }

            if (type == 1 || (filterArea != null && type != 2)) {
                List<Area> areas = DB.getAllAreasOfUser(sessionCurrentUser.getId());
                Menu subMenu = menu.addSubMenu(1, 1, 0, "Area filter ");
                if (filterArea == null) {
                    subMenu.add(1, -1, 0, "NO FILTER").setChecked(true);
                } else {
                    subMenu.add(1, -1, 0, "NO FILTER").setChecked(false);
                }

                for (Area area : areas) {
                    if (filterArea != null && filterArea.equals(area)) {
                        subMenu.add(1, area.getId(), 0, area.getName()).setCheckable(true).setChecked(true);
                    } else {
                        subMenu.add(1, area.getId(), 0, area.getName()).setCheckable(true).setChecked(false);
                    }
                }
            } else if (type == 2 || (filterProject != null && type != 1)) {

                List<Project> projects = DB.getAllProjectsOfUser(sessionCurrentUser.getId());
                Menu subMenu = menu.addSubMenu(2, 1, 0, "Project filter");

                if (filterProject == null) {
                    subMenu.add(2, -1, 0, "NO FILTER").setChecked(true);
                } else {
                    subMenu.add(2, -1, 0, "NO FILTER").setChecked(false);
                }

                for (Project project : projects) {
                    if (filterProject != null && filterProject.equals(project)) {
                        subMenu.add(2, project.getId(), 0, project.getName()).setCheckable(true).setChecked(true);
                    } else {
                        subMenu.add(2, project.getId(), 0, project.getName()).setCheckable(true).setChecked(false);
                    }
                }
            }
        }
    }


}
