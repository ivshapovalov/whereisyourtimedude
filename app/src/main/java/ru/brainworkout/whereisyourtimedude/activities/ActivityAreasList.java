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
import ru.brainworkout.whereisyourtimedude.database.entities.Area;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.Project;
import ru.brainworkout.whereisyourtimedude.database.manager.AndroidDatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

import static ru.brainworkout.whereisyourtimedude.common.Common.*;
import static ru.brainworkout.whereisyourtimedude.common.Common.blink;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionCurrentAreas;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionCurrentProjects;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionOpenActivities;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionCurrentUser;
import static ru.brainworkout.whereisyourtimedude.common.Common.setTitleOfActivity;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionOptions;

public class ActivityAreasList extends AbstractActivity {

    private final int MAX_VERTICAL_BUTTON_COUNT = 17;
    private final int MAX_HORIZONTAL_BUTTON_COUNT = 2;
    private final int NUMBER_OF_VIEWS = 40000;

    private int mHeight = 0;
    private int mWidth = 0;
    private int mTextSize = 0;

    private int idIntentArea;
    private ConnectionParameters params;

    private int rows_number = 17;
    private Map<Integer, List<Area>> pagedAreas = new HashMap<>();
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_areas_list);

        if (!Common.isDebug) {
            int mEditorID = getResources().getIdentifier("btAreasDBEditor", "id", getPackageName());
            Button btEditor = (Button) findViewById(mEditorID);
            hideEditorButton(btEditor);
        }

        if (Session.sessionOptions!=null) {
            rows_number=sessionOptions.getRowNumberInLists();
        }

        Intent intent = getIntent();
        getIntentParams(intent);
        pageAreas();
        showAreas();

        TableRow mRow = (TableRow) findViewById(NUMBER_OF_VIEWS + idIntentArea);
        if (mRow != null) {
            int mScrID = getResources().getIdentifier("svTableAreas", "id", getPackageName());
            ScrollView mScrollView = (ScrollView) findViewById(mScrID);
            if (mScrollView != null) {

                mScrollView.requestChildFocus(mRow, mRow);
            }
        }

        setTitleOfActivity(this);
    }



    private void pageAreas() {
        List<Area> areas = new ArrayList<>();
        if (sessionCurrentUser == null) {
        } else {
            areas = DB.getAllAreasOfUser(sessionCurrentUser.getId());
        }
        List<Area> pageContent = new ArrayList<>();
        int pageNumber = 1;
        for (int i = 0; i < areas.size(); i++) {
            if (idIntentArea != 0) {
                if (areas.get(i).getId() == idIntentArea) {
                    currentPage = pageNumber;
                }
            }
            pageContent.add(areas.get(i));
            if (pageContent.size() == rows_number) {
                pagedAreas.put(pageNumber, pageContent);
                pageContent = new ArrayList<>();
                pageNumber++;
            }
        }
        if (pageContent.size() != 0) {
            pagedAreas.put(pageNumber, pageContent);
        }
    }

    private void getIntentParams(Intent intent) {
        idIntentArea = intent.getIntExtra("CurrentAreaID", 0);
        if (!sessionOpenActivities.isEmpty()) {
            params = sessionOpenActivities.peek();
        }

    }

    private void showAreas() {

        Button pageNumber = (Button) findViewById(R.id.btPageNumber);
        if (pageNumber != null && pagedAreas !=null ) {
            pageNumber.setText(String.valueOf(currentPage)+"/"+ pagedAreas.size());
        }

        ScrollView sv = (ScrollView) findViewById(R.id.svTableAreas);
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

        List<Area> page = pagedAreas.get(currentPage);
        if (page == null) return;
        int currentPageSize = page.size();
        for (int num = 0; num < currentPageSize; num++) {

            Area currentArea = page.get(num);

            TableRow mRow = new TableRow(this);
            mRow.setId(NUMBER_OF_VIEWS + currentArea.getId());
            mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowArea_onClick((TableRow) v);
                }
            });
            mRow.setMinimumHeight(mHeight);
            mRow.setBackgroundResource(R.drawable.bt_border);
            mRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT));

            TextView txt = new TextView(this);
            txt.setText(String.valueOf(currentArea.getId()));
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            txt.setLayoutParams(paramsTextViewWithSpanInList(5));
            mRow.addView(txt);

            txt = new TextView(this);
            String name = String.valueOf(currentArea.getName());
            txt.setText(name);
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            txt.setLayoutParams(paramsTextViewWithSpanInList(10));
            mRow.addView(txt);

            txt = new TextView(this);
            txt.setBackgroundColor(currentArea.getColor());
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(mTextSize);
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
                    txtAreaEdit_onClick((TextView) v);
                }
            });
            txt.setLayoutParams(paramsTextViewWithSpanInList(3));
            mRow.addView(txt);

            mRow.setBackgroundResource(R.drawable.bt_border);
            layout.addView(mRow);
        }
        sv.addView(layout);
    }

    public void btAreasAdd_onClick(final View view) {

        blink(view, this);
        ConnectionParameters paramsNew = new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityAreasList")
                .isTransmitterNew(false)
                .isTransmitterForChoice(params != null ? params.isReceiverForChoice() : false)
                .addReceiverActivityName("ActivityArea")
                .isReceiverNew(true)
                .isReceiverForChoice(false)
                .build();
        sessionOpenActivities.push(paramsNew);
        Intent intent = new Intent(getApplicationContext(), ActivityArea.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void txtAreaEdit_onClick(TextView view) {

        blink(view, this);
        int id = ((TableRow) view.getParent()).getId() % NUMBER_OF_VIEWS;
        ConnectionParameters paramsNew = new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityAreasList")
                .isTransmitterNew(false)
                .isTransmitterForChoice(params != null ? params.isReceiverForChoice() : false)
                .addReceiverActivityName("ActivityArea")
                .isReceiverNew(false)
                .isReceiverForChoice(false)
                .build();
        sessionOpenActivities.push(paramsNew);
        Intent intent = new Intent(getApplicationContext(), ActivityArea.class);
        intent.putExtra("CurrentAreaID", id);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    private void rowArea_onClick(final TableRow view) {

        blink(view, this);
        int id = view.getId() % NUMBER_OF_VIEWS;
        Intent intent = new Intent(getApplicationContext(), ActivityArea.class);
        intent.putExtra("CurrentAreaID", id);
        if (params != null) {
            if (params.isReceiverForChoice()) {
                if (DB.containsArea(id)) {
                    Project project=sessionCurrentProjects.pollFirst();
                    project.setArea(DB.getArea(id));
                    project.dbSave(DB);
                } else {
                    throw new TableDoesNotContainElementException(String.format("Area with id ='%s' does not exists in database", id));
                }
                intent = new Intent(getApplicationContext(), ActivityProject.class);
                sessionOpenActivities.pollFirst();

            }
        } else {
            ConnectionParameters paramsNew = new ConnectionParameters.Builder()
                    .addTransmitterActivityName("ActivityAreasList")
                    .isTransmitterNew(false)
                    .isTransmitterForChoice(params != null ? params.isReceiverForChoice() : false)
                    .addReceiverActivityName("ActivityArea")
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
                .setMessage("Вы действительно хотите удалить все области,их проекты и занятия?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Session.sessionCurrentUser != null) {
                            List<Area> areas = DB.getAllAreasOfUser(Session.sessionCurrentUser.getId());
                            for (Area area : areas
                                    ) {
                                List<Project> projects = DB.getAllProjectsOfArea(area.getId());
                                for (Project project : projects
                                        ) {
                                    List<Practice> practices = DB.getAllActivePracticesOfProject(project.getId());
                                    for (Practice practice : practices
                                            ) {
                                        DB.deleteAllPracticeHistoryOfPractice(practice.getId());
                                    }
                                    DB.deleteAllPracticesOfProject(project.getId());
                                }
                                DB.deleteAllProjectsOfArea(area.getId());
                            }

                            DB.deleteAllAreasOfUser(Session.sessionCurrentUser.getId());
                            showAreas();
                        }
                    }

                }).setNegativeButton("Нет", null).show();
    }

    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        if (params != null) {
            if (params.isReceiverForChoice()) {
                intent = new Intent(getApplicationContext(), ActivityProject.class);
                sessionOpenActivities.pollFirst();
                intent.putExtra("CurrentAreaID", idIntentArea);
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btNextPage_onClick(View view) {
        blink(view, this);

        if (currentPage != pagedAreas.size()) {
            currentPage++;
        }
        showAreas();
    }

    public void btPreviousPage_onClick(View view) {
        blink(view, this);
        if (currentPage != 1) {
            currentPage--;
        }
        showAreas();
    }
}

