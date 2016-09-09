package ru.brainworkout.whereisyourtimedude.activities;

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
import ru.brainworkout.whereisyourtimedude.database.entities.Area;
import ru.brainworkout.whereisyourtimedude.database.manager.AndroidDatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;

import static ru.brainworkout.whereisyourtimedude.common.Common.HideEditorButton;
import static ru.brainworkout.whereisyourtimedude.common.Common.blink;
import static ru.brainworkout.whereisyourtimedude.common.Session.currentPractice;
import static ru.brainworkout.whereisyourtimedude.common.Session.currentProject;
import static ru.brainworkout.whereisyourtimedude.common.Session.openActivities;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionUser;
import static ru.brainworkout.whereisyourtimedude.common.Common.setTitleOfActivity;

public class ActivityAreasList extends AppCompatActivity {

    private final int MAX_VERTICAL_BUTTON_COUNT = 17;
    private final int MAX_HORIZONTAL_BUTTON_COUNT = 2;
    private final int NUMBER_OF_VIEWS = 40000;

    private final DatabaseManager DB = new DatabaseManager(this);

    private int mHeight = 0;
    private int mWidth = 0;
    private int mTextSize = 0;

    private int id_area;
    ConnectionParameters params;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_areas_list);

        if (!Common.isDebug) {
            int mEditorID = getResources().getIdentifier("btAreasDBEditor", "id", getPackageName());
            Button btEditor = (Button) findViewById(mEditorID);
            HideEditorButton(btEditor);
        }

        showAreas();
        Intent intent = getIntent();
        getIntentParams(intent);

        TableRow mRow = (TableRow) findViewById(NUMBER_OF_VIEWS + id_area);
        if (mRow != null) {
            int mScrID = getResources().getIdentifier("svTableAreas", "id", getPackageName());
            ScrollView mScrollView = (ScrollView) findViewById(mScrID);
            if (mScrollView != null) {

                mScrollView.requestChildFocus(mRow, mRow);
            }
        }

        setTitleOfActivity(this);
    }

    private void getIntentParams(Intent intent) {
        id_area = intent.getIntExtra("CurrentAreaID", 0);
        if (!openActivities.empty()) {
            params = openActivities.peek();
        }

    }

    private void showAreas() {

        List<Area> areas;
        if (sessionUser != null) {

            areas = DB.getAllAreasOfUser(sessionUser.getID());
        } else {
            areas = DB.getAllAreas();
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

        for (int numArea = 0; numArea < areas.size(); numArea++) {

            Area currentArea = areas.get(numArea);

            TableRow mRow = new TableRow(this);
            mRow.setId(NUMBER_OF_VIEWS + currentArea.getID());
            mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowArea_onClick((TableRow) v);
                }
            });
            mRow.setMinimumHeight(mHeight);
            mRow.setBackgroundResource(R.drawable.bt_border);

            TextView txt = new TextView(this);
            txt.setText(String.valueOf(currentArea.getID()));
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            mRow.addView(txt);

            txt = new TextView(this);
            String name = String.valueOf(currentArea.getName());
            txt.setText(name);
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            mRow.addView(txt);

            txt = new TextView(this);
            txt.setBackgroundColor(currentArea.getColor());
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            mRow.addView(txt);

            txt = new TextView(this);
            txt.setText("►");
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            txt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtAreaEdit_onClick((TextView) v);
                }
            });
            mRow.addView(txt);

            mRow.setBackgroundResource(R.drawable.bt_border);
            layout.addView(mRow);

        }
        sv.addView(layout);

    }


    public void btAreasAdd_onClick(final View view) {

        blink(view);
        ConnectionParameters paramsNew = new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityAreasList")
                .isTransmitterNew(false)
                .isTransmitterForChoice(params != null ? params.isReceiverForChoice() : false)
                .addReceiverActivityName("ActivityArea")
                .isReceiverNew(true)
                .isReceiverForChoice(false)
                .build();
        openActivities.push(paramsNew);
        Intent intent = new Intent(getApplicationContext(), ActivityArea.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void txtAreaEdit_onClick(TextView v) {

        blink(v);
        int id = ((TableRow) v.getParent()).getId() % NUMBER_OF_VIEWS;
        ConnectionParameters paramsNew = new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityAreasList")
                .isTransmitterNew(false)
                .isTransmitterForChoice(params != null ? params.isReceiverForChoice() : false)
                .addReceiverActivityName("ActivityArea")
                .isReceiverNew(false)
                .isReceiverForChoice(false)
                .build();
        openActivities.push(paramsNew);
        Intent intent = new Intent(getApplicationContext(), ActivityArea.class);
        intent.putExtra("CurrentAreaID", id);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    private void rowArea_onClick(final TableRow v) {

        blink(v);
        int id = v.getId() % NUMBER_OF_VIEWS;
        Intent intent = new Intent(getApplicationContext(), ActivityArea.class);
        intent.putExtra("CurrentAreaID", id);
        if (params != null) {
            if (params.isReceiverForChoice()) {
                currentProject.setIdArea(id);

                intent = new Intent(getApplicationContext(), ActivityProject.class);
                openActivities.pop();

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
            openActivities.push(paramsNew);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btEdit_onClick(final View view) {

        blink(view);

        Intent dbmanager = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }


    public void buttonHome_onClick(final View view) {

        blink(view);
        openActivities.clear();
        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);

        if (params != null) {
            if (params.isReceiverForChoice()) {
                intent = new Intent(getApplicationContext(), ActivityProject.class);
                openActivities.pop();
                intent.putExtra("CurrentAreaID", id_area);
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}

