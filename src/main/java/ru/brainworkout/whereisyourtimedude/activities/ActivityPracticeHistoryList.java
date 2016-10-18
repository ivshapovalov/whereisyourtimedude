package ru.brainworkout.whereisyourtimedude.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.apache.log4j.Logger;

import java.util.List;

import ru.brainworkout.whereisyourtimedude.R;
import ru.brainworkout.whereisyourtimedude.common.ALogger;
import ru.brainworkout.whereisyourtimedude.common.Common;
import ru.brainworkout.whereisyourtimedude.common.ConnectionParameters;
import ru.brainworkout.whereisyourtimedude.common.Session;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.PracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.manager.AndroidDatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

import static ru.brainworkout.whereisyourtimedude.common.Common.*;
import static ru.brainworkout.whereisyourtimedude.common.Common.HideEditorButton;
import static ru.brainworkout.whereisyourtimedude.common.Common.blink;
import static ru.brainworkout.whereisyourtimedude.common.Common.setTitleOfActivity;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionOpenActivities;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionCurrentUser;

public class ActivityPracticeHistoryList extends AbstractActivity {

    private final int MAX_VERTICAL_BUTTON_COUNT = 17;
    private final int MAX_HORIZONTAL_BUTTON_COUNT = 2;
    private final int NUMBER_OF_VIEWS = 40000;


    private int mHeight = 0;
    private int mWidth = 0;
    private int mTextSize = 0;

    ConnectionParameters params;

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
            HideEditorButton(btEditor);
        }

        LOG.debug("ActivityPracticeHistoryList before show pr history");
        message = Common.convertStackTraceToString(Thread.currentThread().getStackTrace());
        LOG.debug(message);

        showPracticeHistory();

        LOG.debug("ActivityPracticeHistoryList after show pr history");
        message = Common.convertStackTraceToString(Thread.currentThread().getStackTrace());
        LOG.debug(message);


        Intent intent = getIntent();
        int id = intent.getIntExtra("CurrentPracticeHistoryID", 0);

        TableRow mRow = (TableRow) findViewById(id);
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


        LOG.debug("ActivityPracticeHistoryList before in show pr history + sessionCurrentUser="+sessionCurrentUser);
        String message = Common.convertStackTraceToString(Thread.currentThread().getStackTrace());
        LOG.debug(message);

        List<PracticeHistory> practiceHistoryList;
        if (sessionCurrentUser != null) {

            practiceHistoryList = DB.getAllPracticeHistoryOfUser(sessionCurrentUser.getID());
        } else {
            practiceHistoryList = DB.getAllPracticeHistory();
        }

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

        for (int numPracticeHistory = 0; numPracticeHistory < practiceHistoryList.size(); numPracticeHistory++) {

            PracticeHistory currentPracticeHistory = practiceHistoryList.get(numPracticeHistory);

            TableRow mRow = new TableRow(this);
            mRow.setId(currentPracticeHistory.getID());
            mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowPracticeHistory_onClick((TableRow) v);
                }
            });
            mRow.setMinimumHeight(mHeight);
            mRow.setBackgroundResource(R.drawable.bt_border);

            TextView txt = new TextView(this);
            txt.setText(String.valueOf(currentPracticeHistory.getID()));
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            mRow.addView(txt);

            txt = new TextView(this);
            String name = ConvertMillisToStringDate(currentPracticeHistory.getDate());
            txt.setText(name);
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            mRow.addView(txt);

            txt = new TextView(this);
            String namePractice = "";
            try {
                Practice practice = DB.getPractice(currentPracticeHistory.getIdPractice());
                namePractice = practice.getName();

            } catch (TableDoesNotContainElementException e) {

            }
            txt.setText(namePractice);
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            mRow.addView(txt);

            txt = new TextView(this);
            txt.setText(SYMBOL_EDIT);
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtPracticeHistoryEdit_onClick((TextView) v);
                }
            });
            mRow.addView(txt);

            mRow.setBackgroundResource(R.drawable.bt_border);
            layout.addView(mRow);

        }
        sv.addView(layout);

    }

    private void txtPracticeHistoryEdit_onClick(TextView view) {

        blink(view, this);
        int id = ((TableRow) view.getParent()).getId();
        //int id = ((TableRow)view.getParent()).getId() % NUMBER_OF_VIEWS;
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
        //int id = view.getId() % NUMBER_OF_VIEWS;

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
                .setMessage("Вы действительно хотите удалить всю историю занятий?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Session.sessionCurrentUser != null) {
                            DB.deleteAllPracticeHistoryOfUser(Session.sessionCurrentUser.getID());
                            showPracticeHistory();
                        }
                    }

                }).setNegativeButton("Нет", null).show();
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
}

