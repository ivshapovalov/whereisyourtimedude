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
import ru.brainworkout.whereisyourtimedude.database.entities.Project;
import ru.brainworkout.whereisyourtimedude.database.manager.AndroidDatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

import static ru.brainworkout.whereisyourtimedude.common.Common.*;
import static ru.brainworkout.whereisyourtimedude.common.Session.*;

public class ActivityPracticeList extends AbstractActivity {

    private final int MAX_VERTICAL_BUTTON_COUNT = 17;
    private final int MAX_HORIZONTAL_BUTTON_COUNT = 2;
    private final int NUMBER_OF_VIEWS = 40000;

    private int mHeight = 0;
    private int mWidth = 0;
    private int mTextSize = 0;

    private int idIntentPractice;
    private ConnectionParameters params;

    private int rows_number = 17;
    private Map<Integer, List<Practice>> pagedPractices = new HashMap<>();
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_list);
        Intent intent = getIntent();
        getIntentParams(intent);

        if (!Common.isDebug) {
            int mEditorID = getResources().getIdentifier("btPracticesDBEditor", "id", getPackageName());
            Button btEditor = (Button) findViewById(mEditorID);
            hideEditorButton(btEditor);
        }

        if (Session.sessionOptions!=null) {
            rows_number=sessionOptions.getRowNumberInLists();
        }

        pagePractices();
        showPractices();

        TableRow mRow = (TableRow) findViewById(NUMBER_OF_VIEWS + idIntentPractice);
        if (mRow != null) {
            int mScrID = getResources().getIdentifier("svTablePractices", "id", getPackageName());
            ScrollView mScrollView = (ScrollView) findViewById(mScrID);
            if (mScrollView != null) {
                mScrollView.requestChildFocus(mRow, mRow);
            }
        }
        setTitleOfActivity(this);
    }

    private void pagePractices() {
        List<Practice> practices = new ArrayList<>();
        if (sessionCurrentUser == null) {
        } else {
            practices = DB.getAllActivePracticesOfUser(sessionCurrentUser.getId());
        }
        List<Practice> pageContent = new ArrayList<>();
        int pageNumber = 1;
        for (int i = 0; i < practices.size(); i++) {
            if (idIntentPractice != 0) {
                if (practices.get(i).getId() == idIntentPractice) {
                    currentPage = pageNumber;
                }
            }
            pageContent.add(practices.get(i));
            if (pageContent.size() == rows_number) {
                pagedPractices.put(pageNumber, pageContent);
                pageContent = new ArrayList<>();
                pageNumber++;
            }
        }
        if (pageContent.size() != 0) {
            pagedPractices.put(pageNumber, pageContent);
        }
    }

    private void getIntentParams(Intent intent) {
        idIntentPractice = intent.getIntExtra("CurrentPracticeID", 0);
        if (!sessionOpenActivities.isEmpty()) {
            params = sessionOpenActivities.peek();
        }
    }

    private void showPractices() {

        Button pageNumber = (Button) findViewById(R.id.btPageNumber);
        if (pageNumber != null && pagedPractices !=null ) {
            pageNumber.setText(String.valueOf(currentPage)+"/"+ pagedPractices.size());
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
        layout.setShrinkAllColumns(true);

        List<Practice> page = pagedPractices.get(currentPage);
        if (page == null) return;
        int currentPageSize = page.size();
        for (int num = 0; num < currentPageSize; num++) {
            Practice currentPractice = page.get(num);

            TableRow mRow = new TableRow(this);
            mRow.setId(NUMBER_OF_VIEWS + currentPractice.getId());
            mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowPractice_onClick((TableRow) v);
                }
            });
            mRow.setMinimumHeight(mHeight);
            mRow.setBackgroundResource(R.drawable.bt_border);
            mRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT));

            TextView txt = new TextView(this);
            txt.setText(String.valueOf(currentPractice.getId()));
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            txt.setLayoutParams(paramsTextViewWithSpanInList(5));
            mRow.addView(txt);

            txt = new TextView(this);
            String name = String.valueOf(currentPractice.getName());
            txt.setText(name);
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            txt.setLayoutParams(paramsTextViewWithSpanInList(10));
            mRow.addView(txt);

            txt = new TextView(this);
            String nameProject = "";
            Project project = currentPractice.getProject();
            if (project != null) {
                nameProject = project.getName();
            }

            txt.setText(nameProject);
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
                    txtPracticeEdit_onClick((TextView) v);
                }
            });
            mRow.addView(txt);
            mRow.setBackgroundResource(R.drawable.bt_border);
            txt.setLayoutParams(paramsTextViewWithSpanInList(3));
            layout.addView(mRow);
        }
        sv.addView(layout);
    }

    public void btPracticeAdd_onClick(final View view) {

        blink(view, this);

        ConnectionParameters paramsNew = new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityPracticeList")
                .isTransmitterNew(false)
                .isTransmitterForChoice(params != null && params.isReceiverForChoice())
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
        blink(view, this);

        int id = ((TableRow) view.getParent()).getId() % NUMBER_OF_VIEWS;
        ConnectionParameters paramsNew = new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityPracticeList")
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

        blink(view, this);

        int id = view.getId() % NUMBER_OF_VIEWS;
        Intent intent = new Intent(getApplicationContext(), ActivityPractice.class);
        intent.putExtra("CurrentPracticeID", id);
        if (params != null) {
            if (params.isReceiverForChoice()) {
                Class<?> transmitterClass = null;
                try {
                    transmitterClass = Class.forName(getPackageName() + ".activities." + params.getTransmitterActivityName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (DB.containsPractice(id)) {
                    if (transmitterClass == ActivityPracticeHistory.class
                            ) {
                        sessionCurrentPracticeHistory.setPractice(DB.getPractice(id));
                    } else if (transmitterClass == ActivityDetailedPracticeHistory.class
                            ) {
                        sessionCurrentDetailedPracticeHistory.setPractice(DB.getPractice(id));
                    }
                } else {
                    throw new TableDoesNotContainElementException(String.format("Practice with id ='%s' does not exists in database", id));
                }
                intent = new Intent(getApplicationContext(), transmitterClass);
                sessionOpenActivities.pollFirst();
                intent.putExtra("CurrentPracticeID", id);
            }
        } else {
            ConnectionParameters paramsNew = new ConnectionParameters.Builder()
                    .addTransmitterActivityName("ActivityPracticeList")
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
                .setMessage("Вы действительно хотите удалить занятия и их историю?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Session.sessionCurrentUser != null) {
                            List<Practice> practices = DB.getAllActivePracticesOfUser(Session.sessionCurrentUser.getId());
                            for (Practice practice : practices
                                    ) {
                                DB.deleteAllPracticeHistoryOfPractice(practice.getId());
                            }
                            DB.deleteAllPracticesOfUser(Session.sessionCurrentUser.getId());
                            showPractices();
                        }
                    }

                }).setNegativeButton("Нет", null).show();
    }

    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        if (params != null) {
            if (params.isReceiverForChoice()) {
                Class<?> transmitterClass = null;
                try {
                    transmitterClass = Class.forName(getPackageName() + ".activities." + params.getTransmitterActivityName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                intent = new Intent(getApplicationContext(), transmitterClass);
                sessionOpenActivities.pollFirst();
                intent.putExtra("CurrentPracticeID", idIntentPractice);
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btNextPage_onClick(View view) {
        blink(view, this);

        if (currentPage != pagedPractices.size()) {
            currentPage++;
        }
        showPractices();
    }

    public void btPreviousPage_onClick(View view) {
        blink(view, this);
        if (currentPage != 1) {
            currentPage--;
        }
        showPractices();
    }
}

