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

import java.util.List;

import ru.brainworkout.whereisyourtimedude.R;
import ru.brainworkout.whereisyourtimedude.common.Common;

import ru.brainworkout.whereisyourtimedude.common.ConnectionParameters;
import ru.brainworkout.whereisyourtimedude.common.Session;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.Project;
import ru.brainworkout.whereisyourtimedude.database.manager.AndroidDatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

import static ru.brainworkout.whereisyourtimedude.common.Common.*;
import static ru.brainworkout.whereisyourtimedude.common.Common.blink;
import static ru.brainworkout.whereisyourtimedude.common.Common.setTitleOfActivity;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionOpenActivities;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionCurrentUser;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionCurrentPracticeHistory;

public class ActivityPracticesList extends AbstractActivity {

    private final int MAX_VERTICAL_BUTTON_COUNT = 17;
    private final int MAX_HORIZONTAL_BUTTON_COUNT = 2;
    private final int NUMBER_OF_VIEWS = 40000;

    private int mHeight = 0;
    private int mWidth = 0;
    private int mTextSize = 0;

    private int id_practice;
    private ConnectionParameters params;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practices_list);
        Intent intent = getIntent();
        getIntentParams(intent);

        if (!Common.isDebug) {
            int mEditorID = getResources().getIdentifier("btPracticesDBEditor", "id", getPackageName());
            Button btEditor = (Button) findViewById(mEditorID);
            HideEditorButton(btEditor);
        }

        showPractices();

        TableRow mRow = (TableRow) findViewById(NUMBER_OF_VIEWS + id_practice);
        if (mRow != null) {
            int mScrID = getResources().getIdentifier("svTablePractices", "id", getPackageName());
            ScrollView mScrollView = (ScrollView) findViewById(mScrID);
            if (mScrollView != null) {

                mScrollView.requestChildFocus(mRow, mRow);
            }
        }

        setTitleOfActivity(this);
    }

    private void getIntentParams(Intent intent) {

        id_practice = intent.getIntExtra("CurrentPracticeID", 0);
        if (!sessionOpenActivities.isEmpty()) {
            params = sessionOpenActivities.peek();
        }

    }

    private void showPractices() {

        List<Practice> practices;
        if (sessionCurrentUser != null) {

            practices = DB.getAllActivePracticesOfUser(sessionCurrentUser.getID());
        } else {
            practices = DB.getAllActivePractices();
        }

        ScrollView sv = (ScrollView) findViewById(R.id.svTablePractices);
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

        for (int numPractice = 0; numPractice < practices.size(); numPractice++) {

            Practice currentPractice = practices.get(numPractice);

            TableRow mRow = new TableRow(this);
            mRow.setId(NUMBER_OF_VIEWS + currentPractice.getID());
            mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowPractice_onClick((TableRow) v);
                }
            });
            mRow.setMinimumHeight(mHeight);
            mRow.setBackgroundResource(R.drawable.bt_border);

            TextView txt = new TextView(this);
            txt.setText(String.valueOf(currentPractice.getID()));
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            mRow.addView(txt);

            txt = new TextView(this);
            String name = String.valueOf(currentPractice.getName());
            txt.setText(name);
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            mRow.addView(txt);

            txt = new TextView(this);
            String nameProject = "";
            try {
                Project project = DB.getProject(currentPractice.getIdProject());
                nameProject = project.getName();

            } catch (TableDoesNotContainElementException e) {

            }
            txt.setText(nameProject);
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
                    txtPracticeEdit_onClick((TextView) v);
                }
            });
            mRow.addView(txt);


            mRow.setBackgroundResource(R.drawable.bt_border);
            layout.addView(mRow);

        }
        sv.addView(layout);

    }

    public void btPracticeAdd_onClick(final View view) {

        blink(view,this);

        ConnectionParameters paramsNew = new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityPracticesList")
                .isTransmitterNew(false)
                .isTransmitterForChoice(params != null ? params.isReceiverForChoice() : false)
                .addReceiverActivityName("ActivityPractice")
                .isReceiverNew(true)
                .isReceiverForChoice(false)
                .build();
        sessionOpenActivities.push(paramsNew);
        Intent intent = new Intent(getApplicationContext(), ActivityPractice.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    private void txtPracticeEdit_onClick(TextView view) {
        blink(view,this);

        int id = ((TableRow)view.getParent()).getId() % NUMBER_OF_VIEWS;
        ConnectionParameters paramsNew = new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityPracticesList")
                .isTransmitterNew(false)
                .isTransmitterForChoice(params != null ? params.isReceiverForChoice() : false)
                .addReceiverActivityName("ActivityPractice")
                .isReceiverNew(false)
                .isReceiverForChoice(false)
                .build();
        sessionOpenActivities.push(paramsNew);
        Intent intent = new Intent(getApplicationContext(), ActivityPractice.class);
        intent.putExtra("CurrentPracticeID", id);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    private void rowPractice_onClick(final TableRow view) {

        blink(view,this);

        int id = view.getId() % NUMBER_OF_VIEWS;
        Intent intent = new Intent(getApplicationContext(), ActivityPractice.class);
        intent.putExtra("CurrentPracticeID", id);
        if (params != null) {
            if (params.isReceiverForChoice()) {
                sessionCurrentPracticeHistory.setIdPractice(id);

                intent = new Intent(getApplicationContext(), ActivityPracticeHistory.class);
                sessionOpenActivities.pop();
                intent.putExtra("CurrentPracticeID", id);
            }
        } else {
            ConnectionParameters paramsNew = new ConnectionParameters.Builder()
                    .addTransmitterActivityName("ActivityPracticesList")
                    .isTransmitterNew(false)
                    .isTransmitterForChoice(params != null ? params.isReceiverForChoice() : false)
                    .addReceiverActivityName("ActivityPractice")
                    .isReceiverNew(false)
                    .isReceiverForChoice(false)
                    .build();
            sessionOpenActivities.push(paramsNew);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void btEdit_onClick(final View view) {

        blink(view,this);

        Intent dbmanager = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }


    public void buttonHome_onClick(final View view) {

        blink(view,this);
        sessionOpenActivities.clear();
        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void btClear_onClick(final View view) {

        blink(view,this);

        new AlertDialog.Builder(this)
                .setMessage("Вы действительно хотите удалить занятия и их историю?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Session.sessionCurrentUser != null) {
                                List<Practice> practices = DB.getAllActivePracticesOfUser(Session.sessionCurrentUser.getID());
                                for (Practice practice : practices
                                        ) {
                                    DB.deleteAllPracticeHistoryOfPractice(practice.getID());
                                }
                                DB.deleteAllPracticesOfUser(Session.sessionCurrentUser.getID());
                            showPractices();
                        }
                    }

                }).setNegativeButton("Нет", null).show();
    }

    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);

        if (params != null) {
            if (params.isReceiverForChoice()) {
                intent = new Intent(getApplicationContext(), ActivityPracticeHistory.class);
                sessionOpenActivities.pop();
                intent.putExtra("CurrentPracticeID", id_practice);
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}

