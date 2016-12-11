package ru.brainworkout.whereisyourtimedude.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.brainworkout.whereisyourtimedude.R;
import ru.brainworkout.whereisyourtimedude.common.Common;
import ru.brainworkout.whereisyourtimedude.common.ConnectionParameters;
import ru.brainworkout.whereisyourtimedude.common.Session;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.PracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.manager.AndroidDatabaseManager;

import static ru.brainworkout.whereisyourtimedude.common.Common.*;
import static ru.brainworkout.whereisyourtimedude.common.Common.hideEditorButton;
import static ru.brainworkout.whereisyourtimedude.common.Common.blink;
import static ru.brainworkout.whereisyourtimedude.common.Common.setTitleOfActivity;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionOpenActivities;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionCurrentUser;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionOptions;

public class ActivityPracticeHistoryList extends AbstractActivity {

    private final int MAX_VERTICAL_BUTTON_COUNT = 17;
    private final int MAX_HORIZONTAL_BUTTON_COUNT = 2;
    private final int NUMBER_OF_VIEWS = 40000;

    private int mHeight = 0;
    private int mWidth = 0;
    private int mTextSize = 0;

    private ConnectionParameters params;
    private int idIntentPracticeHistory;

    private int rows_number = 0;
    private Map<Integer, List<PracticeHistory>> pagedPracticeHistory = new HashMap<>();
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        LOG.debug("ActivityPracticeHistoryList start");
        String message = Common.convertStackTraceToString(Thread.currentThread().getStackTrace());
        LOG.debug(message);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practices_history_list);

        if (!Common.isDebug) {
            int mEditorID = getResources().getIdentifier("btPracticeHistoryDBEditor", "id", getPackageName());
            Button btEditor = (Button) findViewById(mEditorID);
            hideEditorButton(btEditor);
        }

        LOG.debug("ActivityPracticeHistoryList before show pr history");
        message = Common.convertStackTraceToString(Thread.currentThread().getStackTrace());
        LOG.debug(message);

        if (Session.sessionOptions!=null) {
            rows_number=sessionOptions.getRowNumberInLists();
        }
        Intent intent = getIntent();
        getIntentParams(intent);
        updatePracticeHistory();

        LOG.debug("ActivityPracticeHistoryList after show pr history");
        message = Common.convertStackTraceToString(Thread.currentThread().getStackTrace());
        LOG.debug(message);

        TableRow mRow = (TableRow) findViewById(idIntentPracticeHistory);
        //TableRow mRow = (TableRow) findViewById(NUMBER_OF_VIEWS + id);
        if (mRow != null) {
            int mScrID = getResources().getIdentifier("svTablePracticeHistory", "id", getPackageName());
            ScrollView mScrollView = (ScrollView) findViewById(mScrID);
            if (mScrollView != null) {
                mScrollView.requestChildFocus(mRow, mRow);
            }
        }

        setTitleOfActivity(this);
        LOG.debug("ActivityPracticeHistoryList end");
        message = Common.convertStackTraceToString(Thread.currentThread().getStackTrace());
        LOG.debug(message);
    }

    private void pagePracticeHistories() {
        List<PracticeHistory> practiceHistories= new ArrayList<>();
        if (sessionCurrentUser == null) {
        } else {
            practiceHistories = DB.getAllPracticeHistoryOfUser(sessionCurrentUser.getId());
        }
        List<PracticeHistory> pageContent = new ArrayList<>();
        int pageNumber = 1;
        for (int i = 0; i < practiceHistories.size(); i++) {
            if (idIntentPracticeHistory != 0) {
                if (practiceHistories.get(i).getId() == idIntentPracticeHistory) {
                    currentPage = pageNumber;
                }
            }
            pageContent.add(practiceHistories.get(i));
            if (pageContent.size() == rows_number) {
                pagedPracticeHistory.put(pageNumber, pageContent);
                pageContent = new ArrayList<>();
                pageNumber++;
            }
        }
        if (pageContent.size() != 0) {
            pagedPracticeHistory.put(pageNumber, pageContent);
        }

        if (pagedPracticeHistory.size()==0) {
            currentPage=0;
        }
    }

    private void getIntentParams(Intent intent) {
        idIntentPracticeHistory = intent.getIntExtra("CurrentPracticeHistoryID", 0);
    }

    public void btPracticeHistoryAdd_onClick(final View view) {

        blink(view, this);
        ConnectionParameters paramsNew = new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityPracticeHistory")
                .isTransmitterNew(true)
                .isTransmitterForChoice(false)
                .addReceiverActivityName("ActivityPracticeHistoryList")
                .isReceiverNew(false)
                .isReceiverForChoice(false)
                .build();
        sessionOpenActivities.clear();
        sessionOpenActivities.push(paramsNew);
        Intent intent = new Intent(getApplicationContext(), ActivityPracticeHistory.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void showPracticeHistory() {

        Button pageNumber = (Button) findViewById(R.id.btPageNumber);
        if (pageNumber != null && pagedPracticeHistory !=null ) {
            pageNumber.setText(String.valueOf(currentPage)+"/"+ pagedPracticeHistory.size());
        }

        LOG.debug("ActivityPracticeHistoryList before in show pr history + sessionCurrentUser=" + sessionCurrentUser);
        String message = Common.convertStackTraceToString(Thread.currentThread().getStackTrace());
        LOG.debug(message);

        LOG.debug("after get histories from db");
        message = Common.convertStackTraceToString(Thread.currentThread().getStackTrace());
        LOG.debug(message);

        ScrollView sv = (ScrollView) findViewById(R.id.svTablePracticeHistory);
        try {
            sv.removeAllViews();
        } catch (NullPointerException e) {
        }

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();

        mHeight = displaymetrics.heightPixels / MAX_VERTICAL_BUTTON_COUNT;
        mWidth = displaymetrics.widthPixels / MAX_HORIZONTAL_BUTTON_COUNT;
        mTextSize = (int) (Math.min(mWidth, mHeight) / 1.5 / getApplicationContext().getResources().getDisplayMetrics().density);

        TableRow trowButtons = (TableRow) findViewById(R.id.trowButtons);

        if (trowButtons != null) {
            trowButtons.setMinimumHeight(mHeight);
        }

        TableLayout layout = new TableLayout(this);
        layout.setStretchAllColumns(true);
        layout.setShrinkAllColumns(true);

        List<PracticeHistory> page = pagedPracticeHistory.get(currentPage);
        if (page == null) return;
        int currentPageSize = page.size();
        for (int num = 0; num < currentPageSize; num++) {
            PracticeHistory currentPracticeHistory = page.get(num);

            TableRow mRow = new TableRow(this);
            mRow.setId(currentPracticeHistory.getId());
            mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowPracticeHistory_onClick((TableRow) v);
                }
            });
            mRow.setMinimumHeight(mHeight);
            mRow.setBackgroundResource(R.drawable.bt_border);
            mRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT));

            TextView txt = new TextView(this);
            txt.setText(String.valueOf(currentPracticeHistory.getId()));
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            txt.setLayoutParams(paramsTextViewWithSpanInList(5));
            mRow.addView(txt);

            txt = new TextView(this);
            String name = convertMillisToStringDate(currentPracticeHistory.getDate());
            txt.setText(name);
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            txt.setLayoutParams(paramsTextViewWithSpanInList(10));
            mRow.addView(txt);

            txt = new TextView(this);
            String namePractice = "";

            Practice practice = currentPracticeHistory.getPractice();
            if (practice != null) {
                namePractice = practice.getName();
            }

            txt.setText(namePractice);
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            txt.setLayoutParams(paramsTextViewWithSpanInList(10));
            mRow.addView(txt);

            txt = new TextView(this);
            txt.setText(SYMBOL_EDIT);
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtPracticeHistoryEdit_onClick((TextView) v);
                }
            });
            txt.setLayoutParams(paramsTextViewWithSpanInList(3));
            mRow.addView(txt);

            mRow.setBackgroundResource(R.drawable.bt_border);
            layout.addView(mRow);
        }
        sv.addView(layout);
    }

    private void txtPracticeHistoryEdit_onClick(TextView view) {
        blink(view, this);
        int id = ((TableRow) view.getParent()).getId();
        ConnectionParameters params = new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityPracticeHistoryList")
                .isTransmitterNew(false)
                .isTransmitterForChoice(false)
                .addReceiverActivityName("ActivityPracticeHistory")
                .isReceiverNew(false)
                .isReceiverForChoice(false)
                .build();
        sessionOpenActivities.clear();
        sessionOpenActivities.push(params);
        Intent intent = new Intent(getApplicationContext(), ActivityPracticeHistory.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("CurrentPracticeHistoryID", id);
        startActivity(intent);
    }

    private void rowPracticeHistory_onClick(final TableRow view) {

        blink(view, this);
        int id = view.getId();
        ConnectionParameters params = new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityPracticeHistoryList")
                .isTransmitterNew(false)
                .isTransmitterForChoice(false)
                .addReceiverActivityName("ActivityPracticeHistory")
                .isReceiverNew(false)
                .isReceiverForChoice(false)
                .build();
        sessionOpenActivities.clear();
        sessionOpenActivities.push(params);
        Intent intent = new Intent(getApplicationContext(), ActivityPracticeHistory.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("CurrentPracticeHistoryID", id);
        startActivity(intent);
    }

    public void btEdit_onClick(final View view) {
        blink(view, this);
        Intent dbmanager = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }


    public void buttonHome_onClick(final View view) {
        blink(view, this);
        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btClear_onClick(final View view) {

        blink(view, this);
        new AlertDialog.Builder(this)
                .setMessage("Are you really want to delete all practice history?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Session.sessionCurrentUser != null) {
                            DB.deleteAllPracticeHistoryOfUser(Session.sessionCurrentUser.getId());
                            showPracticeHistory();
                        }
                    }

                }).setNegativeButton("No", null).show();
        updatePracticeHistory();
    }

    private void updatePracticeHistory() {
        pagePracticeHistories();
        showPracticeHistory();
    }

    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        LOG.debug("ActivityPracticeHistoryList destroyd");
        super.onDestroy();
    }

    public void btNextPage_onClick(View view) {
        blink(view, this);

        if (currentPage != pagedPracticeHistory.size()) {
            currentPage++;
        }
        showPracticeHistory();
    }

    public void btPreviousPage_onClick(View view) {
        blink(view, this);
        if (currentPage > 1) {
            currentPage--;
        }
        showPracticeHistory();
    }
}

