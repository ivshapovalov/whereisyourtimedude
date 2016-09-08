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
import ru.brainworkout.whereisyourtimedude.database.entities.Area;
import ru.brainworkout.whereisyourtimedude.database.manager.AndroidDatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;

import static ru.brainworkout.whereisyourtimedude.common.Common.HideEditorButton;
import static ru.brainworkout.whereisyourtimedude.common.Common.blink;
import static ru.brainworkout.whereisyourtimedude.common.Session.currentProject;
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

    private boolean forChoice = false;
    private String mCallerActivity;
    private int id_area;
    private boolean isNew;


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

        setTitleOfActivity(this);
    }


    @Override
    public void onResume() {
        super.onResume();

        showAreas();

        Intent intent = getIntent();
        isNew = intent.getBooleanExtra("isNew", false);
        id_area = intent.getIntExtra("CurrentAreaID", 0);
        forChoice = intent.getBooleanExtra("forChoice", false);
        mCallerActivity = intent.getStringExtra("CallerActivity");
        //id_project = intent.getIntExtra("CurrentProjectID", 0);

        TableRow mRow = (TableRow) findViewById(NUMBER_OF_VIEWS + id_area);
        if (mRow != null) {
            int mScrID = getResources().getIdentifier("svTableAreas", "id", getPackageName());
            ScrollView mScrollView = (ScrollView) findViewById(mScrID);
            if (mScrollView != null) {

                mScrollView.requestChildFocus(mRow, mRow);
            }
        }
    }


    public void btAreasAdd_onClick(final View view) {

        blink(view);
        Intent intent = new Intent(getApplicationContext(), ActivityArea.class);
        intent.putExtra("isNew", true);
        intent.putExtra("forChoice",forChoice);
        startActivity(intent);

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

            mRow.setBackgroundResource(R.drawable.bt_border);
            layout.addView(mRow);

        }
        sv.addView(layout);

    }

    private void rowArea_onClick(final TableRow v) {

        blink(v);
        Intent intent=new Intent();
        int id = v.getId() % NUMBER_OF_VIEWS;
        if (forChoice) {
            Class<?> myClass = null;
            try {
                myClass = Class.forName(getPackageName() + ".activities." + mCallerActivity);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            currentProject.setIdArea(id);
            intent = new Intent(getApplicationContext(), myClass);
            intent.putExtra("isNew", isNew);


        } else {

            intent= new Intent(getApplicationContext(), ActivityArea.class);
            intent.putExtra("CurrentAreaID", id);
            intent.putExtra("isNew", false);


        }
        startActivity(intent);

    }

    public void btEdit_onClick(final View view) {

        blink(view);

        Intent dbmanager = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }


    public void buttonHome_onClick(final View view) {

        blink(view);
        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void onBackPressed() {
        Intent intent = new Intent();
        if (forChoice) {
            Class<?> myClass = null;
            try {
                myClass = Class.forName(getPackageName() + ".activities." + mCallerActivity);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            intent = new Intent(getApplicationContext(), myClass);
            intent.putExtra("isNew", isNew);
            intent.putExtra("CurrentAreaID", id_area);

        } else {
            intent = new Intent(getApplicationContext(), ActivityMain.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}
