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
import ru.brainworkout.whereisyourtimedude.database.entities.DetailedPracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.manager.AndroidDatabaseManager;

import static ru.brainworkout.whereisyourtimedude.common.Common.convertMillisToStringDate;
import static ru.brainworkout.whereisyourtimedude.common.Common.hideEditorButton;
import static ru.brainworkout.whereisyourtimedude.common.Common.SYMBOL_EDIT;
import static ru.brainworkout.whereisyourtimedude.common.Common.blink;
import static ru.brainworkout.whereisyourtimedude.common.Common.paramsTextViewWithSpanInList;
import static ru.brainworkout.whereisyourtimedude.common.Common.setTitleOfActivity;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionCurrentUser;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionOpenActivities;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionOptions;

public class ActivityDetailedPracticeHistoryList extends AbstractActivity {

    private final int MAX_VERTICAL_BUTTON_COUNT = 17;
    private final int MAX_HORIZONTAL_BUTTON_COUNT = 2;
    private final int NUMBER_OF_VIEWS = 40000;

    private int mHeight = 0;
    private int mWidth = 0;
    private int mTextSize = 0;

    private int idIntentDetailedPracticeHistory;
    private ConnectionParameters params;

    private int rows_number = 0;
    private Map<Integer, List<DetailedPracticeHistory>> pagedDetailedPracticeHistory = new HashMap<>();
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_practices_history_list);

        if (!Common.isDebug) {
            int mEditorID = getResources().getIdentifier("btDetailedPracticeHistoryDBEditor", "id", getPackageName());
            Button btEditor = (Button) findViewById(mEditorID);
            hideEditorButton(btEditor);
        }

        if (Session.sessionOptions!=null) {
            rows_number=sessionOptions.getRowNumberInLists();
        }

        Intent intent = getIntent();
        getIntentParams(intent);
        pageDetailedPracticeHistory();
        showDetailedPracticeHistory();

        TableRow mRow = (TableRow) findViewById(idIntentDetailedPracticeHistory);
        if (mRow != null) {
            int mScrID = getResources().getIdentifier("svTableDetailedPracticeHistory", "id", getPackageName());
            ScrollView mScrollView = (ScrollView) findViewById(mScrID);
            if (mScrollView != null) {

                mScrollView.requestChildFocus(mRow, mRow);
            }
        }

        setTitleOfActivity(this);

    }

    private void updateDetailedPracticeHistory() {
        pageDetailedPracticeHistory();
        showDetailedPracticeHistory();
    }

    private void pageDetailedPracticeHistory() {
        List<DetailedPracticeHistory> areas = new ArrayList<>();
        if (sessionCurrentUser == null) {
        } else {
            areas = DB.getAllDetailedPracticeHistoryOfUser(sessionCurrentUser.getId());
        }
        List<DetailedPracticeHistory> pageContent = new ArrayList<>();
        int pageNumber = 1;
        for (int i = 0; i < areas.size(); i++) {
            if (idIntentDetailedPracticeHistory != 0) {
                if (areas.get(i).getId() == idIntentDetailedPracticeHistory) {
                    currentPage = pageNumber;
                }
            }
            pageContent.add(areas.get(i));
            if (pageContent.size() == rows_number) {
                pagedDetailedPracticeHistory.put(pageNumber, pageContent);
                pageContent = new ArrayList<>();
                pageNumber++;
            }
        }
        if (pageContent.size() != 0) {
            pagedDetailedPracticeHistory.put(pageNumber, pageContent);
        }

        if (pagedDetailedPracticeHistory.size()==0) {
            currentPage=0;
        }

    }

    private void getIntentParams(Intent intent) {
        idIntentDetailedPracticeHistory = intent.getIntExtra("CurrentDetailedPracticeHistoryID", 0);
    }

    public void btDetailedPracticeHistoryAdd_onClick(final View view) {

        blink(view, this);
        ConnectionParameters paramsNew = new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityDetailedPracticeHistory")
                .isTransmitterNew(true)
                .isTransmitterForChoice(false)
                .addReceiverActivityName("ActivityDetailedPracticeHistoryList")
                .isReceiverNew(false)
                .isReceiverForChoice(false)
                .build();
        sessionOpenActivities.clear();
        sessionOpenActivities.push(paramsNew);
        Intent intent = new Intent(getApplicationContext(), ActivityDetailedPracticeHistory.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    private void showDetailedPracticeHistory() {

        Button pageNumber = (Button) findViewById(R.id.btPageNumber);
        if (pageNumber != null && pagedDetailedPracticeHistory !=null ) {
            pageNumber.setText(String.valueOf(currentPage)+"/"+ pagedDetailedPracticeHistory.size());
        }

        ScrollView sv = (ScrollView) findViewById(R.id.svTableDetailedPracticeHistory);
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

        List<DetailedPracticeHistory> page = pagedDetailedPracticeHistory.get(currentPage);
        if (page == null) return;
        int currentPageSize = page.size();
        for (int num = 0; num < currentPageSize; num++) {
            DetailedPracticeHistory currentDetailedPracticeHistory = page.get(num);

            TableRow mRow = new TableRow(this);
            mRow.setId(currentDetailedPracticeHistory.getId());
            mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowDetailedPracticeHistory_onClick((TableRow) v);
                }
            });
            mRow.setMinimumHeight(mHeight);
            mRow.setBackgroundResource(R.drawable.bt_border);
            mRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT));

            TextView txt = new TextView(this);
            txt.setText(String.valueOf(currentDetailedPracticeHistory.getId()));
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            txt.setLayoutParams(paramsTextViewWithSpanInList(5));
            mRow.addView(txt);

            txt = new TextView(this);
            String name = convertMillisToStringDate(currentDetailedPracticeHistory.getDate());
            txt.setText(name);
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            txt.setLayoutParams(paramsTextViewWithSpanInList(10));
            mRow.addView(txt);

            txt = new TextView(this);
            Practice practice = currentDetailedPracticeHistory.getPractice();
            String namePractice = "";
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
                    txtDetailedPracticeHistoryEdit_onClick((TextView) v);
                }
            });
            txt.setLayoutParams(paramsTextViewWithSpanInList(3));
            mRow.addView(txt);

            mRow.setBackgroundResource(R.drawable.bt_border);
            layout.addView(mRow);

        }
        sv.addView(layout);

    }

    private void txtDetailedPracticeHistoryEdit_onClick(TextView view) {

        blink(view, this);
        int id = ((TableRow) view.getParent()).getId();
        ConnectionParameters params = new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityDetailedPracticeHistoryList")
                .isTransmitterNew(false)
                .isTransmitterForChoice(false)
                .addReceiverActivityName("ActivityDetailedPracticeHistory")
                .isReceiverNew(false)
                .isReceiverForChoice(false)
                .build();
        sessionOpenActivities.clear();
        sessionOpenActivities.push(params);
        Intent intent = new Intent(getApplicationContext(), ActivityDetailedPracticeHistory.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("CurrentDetailedPracticeHistoryID", id);
        startActivity(intent);

    }

    private void rowDetailedPracticeHistory_onClick(final TableRow view) {
        blink(view, this);
        int id = view.getId();
        ConnectionParameters params = new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityDetailedPracticeHistoryList")
                .isTransmitterNew(false)
                .isTransmitterForChoice(false)
                .addReceiverActivityName("ActivityDetailedPracticeHistory")
                .isReceiverNew(false)
                .isReceiverForChoice(false)
                .build();
        sessionOpenActivities.clear();
        sessionOpenActivities.push(params);
        Intent intent = new Intent(getApplicationContext(), ActivityDetailedPracticeHistory.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("CurrentDetailedPracticeHistoryID", id);
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
                .setMessage("Do you want to remove all the detailed practice history?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Session.sessionCurrentUser != null) {
                            DB.deleteAllDetailedPracticeHistoryOfUser(Session.sessionCurrentUser.getId());
                            updateDetailedPracticeHistory();
                        }
                    }

                }).setNegativeButton("No", null).show();
    }

    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void btNextPage_onClick(View view) {
        blink(view, this);
        if (currentPage != pagedDetailedPracticeHistory.size()) {
            currentPage++;
        }
        showDetailedPracticeHistory();
    }

    public void btPreviousPage_onClick(View view) {
        blink(view, this);
        if (currentPage > 1) {
            currentPage--;
        }
        showDetailedPracticeHistory();
    }
}

