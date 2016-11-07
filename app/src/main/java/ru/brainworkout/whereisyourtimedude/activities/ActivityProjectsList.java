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

import java.util.List;

import ru.brainworkout.whereisyourtimedude.R;
import ru.brainworkout.whereisyourtimedude.common.Common;
import ru.brainworkout.whereisyourtimedude.common.ConnectionParameters;
import ru.brainworkout.whereisyourtimedude.common.Session;
import ru.brainworkout.whereisyourtimedude.database.entities.Area;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.Project;
import ru.brainworkout.whereisyourtimedude.database.manager.AndroidDatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

import static ru.brainworkout.whereisyourtimedude.common.Common.hideEditorButton;
import static ru.brainworkout.whereisyourtimedude.common.Common.blink;

import static ru.brainworkout.whereisyourtimedude.common.Common.*;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionCurrentPractice;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionOpenActivities;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionCurrentUser;

public class ActivityProjectsList extends AbstractActivity {

    private final int MAX_VERTICAL_BUTTON_COUNT = 17;
    private final int MAX_HORIZONTAL_BUTTON_COUNT = 2;
    private final int NUMBER_OF_VIEWS = 40000;

    private int mHeight = 0;
    private int mWidth = 0;
    private int mTextSize = 0;

    private int id_project;

    ConnectionParameters params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projects_list);

        Intent intent = getIntent();
        getIntentParams(intent);

        if (!Common.isDebug) {
            int mEditorID = getResources().getIdentifier("btProjectsDBEditor", "id", getPackageName());
            Button btEditor = (Button) findViewById(mEditorID);
            hideEditorButton(btEditor);
        }

        showProjects();
        TableRow mRow = (TableRow) findViewById(NUMBER_OF_VIEWS + id_project);
        if (mRow != null) {
            int mScrID = getResources().getIdentifier("svTableProjects", "id", getPackageName());
            ScrollView mScrollView = (ScrollView) findViewById(mScrID);
            if (mScrollView != null) {

                mScrollView.requestChildFocus(mRow, mRow);
            }
        }

        setTitleOfActivity(this);
    }

    private void getIntentParams(Intent intent) {
        id_project = intent.getIntExtra("CurrentProjectID", 0);
        if (!sessionOpenActivities.isEmpty()) {
            params = sessionOpenActivities.peek();
        }
    }


    private void showProjects() {

        List<Project> projects;
        if (sessionCurrentUser != null) {
            projects = DB.getAllProjectsOfUser(sessionCurrentUser.getId());
        } else {
            projects = DB.getAllProjects();
        }

        ScrollView sv = (ScrollView) findViewById(R.id.svTableProjects);
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

        for (int numProject = 0; numProject < projects.size(); numProject++) {

            Project currentProject = projects.get(numProject);

            TableRow mRow = new TableRow(this);
            mRow.setId(NUMBER_OF_VIEWS + currentProject.getId());
            mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowProject_onClick((TableRow) v);
                }
            });
            mRow.setMinimumHeight(mHeight);
            mRow.setBackgroundResource(R.drawable.bt_border);

            TextView txt = new TextView(this);
            txt.setText(String.valueOf(currentProject.getId()));
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            mRow.addView(txt);

            txt = new TextView(this);
            String name = String.valueOf(currentProject.getName());
            txt.setText(name);
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            mRow.addView(txt);

            txt = new TextView(this);
            String nameArea = "";
            try {
                Area area = currentProject.getArea();
                if (area != null) {
                    nameArea = area.getName();
                }
                txt.setBackgroundColor(area.getColor());
            } catch (TableDoesNotContainElementException e) {
                txt.setBackgroundResource(R.drawable.bt_border);
            }
            txt.setText(nameArea);

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
                    txtProjectEdit_onClick((TextView) v);
                }
            });
            mRow.addView(txt);

            mRow.setBackgroundResource(R.drawable.bt_border);
            layout.addView(mRow);

        }
        sv.addView(layout);

    }

    public void btProjectAdd_onClick(final View view) {
        blink(view, this);
        ConnectionParameters paramsNew = new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityProjectsList")
                .isTransmitterNew(false)
                .isTransmitterForChoice(params != null ? params.isReceiverForChoice() : false)
                .addReceiverActivityName("ActivityProject")
                .isReceiverNew(true)
                .isReceiverForChoice(false)
                .build();
        sessionOpenActivities.push(paramsNew);
        Intent intent = new Intent(getApplicationContext(), ActivityProject.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void txtProjectEdit_onClick(TextView view) {
        blink(view, this);
        int id = ((TableRow) view.getParent()).getId() % NUMBER_OF_VIEWS;
        ConnectionParameters paramsNew = new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityProjectsList")
                .isTransmitterNew(false)
                .isTransmitterForChoice(params != null ? params.isReceiverForChoice() : false)
                .addReceiverActivityName("ActivityProject")
                .isReceiverNew(false)
                .isReceiverForChoice(false)
                .build();
        sessionOpenActivities.push(paramsNew);
        Intent intent = new Intent(getApplicationContext(), ActivityProject.class);
        intent.putExtra("CurrentProjectID", id);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void rowProject_onClick(final TableRow view) {
        blink(view, this);
        int id = view.getId() % NUMBER_OF_VIEWS;
        Intent intent = new Intent(getApplicationContext(), ActivityProject.class);
        intent.putExtra("CurrentProjectID", id);
        if (params != null) {
            if (params.isReceiverForChoice()) {
                if (DB.containsProject(id)) {
                    sessionCurrentPractice.setProject(DB.getProject(id));
                }
                intent = new Intent(getApplicationContext(), ActivityPractice.class);
                sessionOpenActivities.pop();
            }
        } else {
            ConnectionParameters paramsNew = new ConnectionParameters.Builder()
                    .addTransmitterActivityName("ActivityProjectsList")
                    .isTransmitterNew(false)
                    .isTransmitterForChoice(params != null ? params.isReceiverForChoice() : false)
                    .addReceiverActivityName("ActivityProject")
                    .isReceiverNew(false)
                    .isReceiverForChoice(false)
                    .build();
            sessionOpenActivities.push(paramsNew);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btEdit_onClick(final View view) {
        blink(view, this);
        Intent dbmanager = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }


    public void buttonHome_onClick(final View view) {
        blink(view, this);
        sessionOpenActivities.clear();
        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btClear_onClick(final View view) {
        blink(view, this);
        new AlertDialog.Builder(this)
                .setMessage("Вы действительно хотите удалить все проекты и занятия?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        if (Session.sessionCurrentUser != null) {

                            List<Project> projects = DB.getAllProjectsOfUser(Session.sessionCurrentUser.getId());
                            for (Project project : projects
                                    ) {
                                List<Practice> practices = DB.getAllActivePracticesOfProject(project.getId());
                                for (Practice practice : practices
                                        ) {
                                    DB.deleteAllPracticeHistoryOfPractice(practice.getId());
                                }
                                DB.deleteAllPracticesOfProject(project.getId());
                            }
                            DB.deleteAllProjectsOfUser(Session.sessionCurrentUser.getId());
                            showProjects();
                        }
                    }

                }).setNegativeButton("Нет", null).show();
    }

    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        if (params != null) {
            if (params.isReceiverForChoice()) {
                intent = new Intent(getApplicationContext(), ActivityPractice.class);
                sessionOpenActivities.pop();
                intent.putExtra("CurrentProjectID", id_project);
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}

