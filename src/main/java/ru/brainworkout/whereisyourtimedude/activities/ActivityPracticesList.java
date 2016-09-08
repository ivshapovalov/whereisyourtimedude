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

import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.Project;
import ru.brainworkout.whereisyourtimedude.database.manager.AndroidDatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

import static ru.brainworkout.whereisyourtimedude.common.Common.HideEditorButton;
import static ru.brainworkout.whereisyourtimedude.common.Common.blink;
import static ru.brainworkout.whereisyourtimedude.common.Common.setTitleOfActivity;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionUser;

public class ActivityPracticesList extends AppCompatActivity {

    private final int MAX_VERTICAL_BUTTON_COUNT = 17;
    private final int MAX_HORIZONTAL_BUTTON_COUNT = 2;
    private final int NUMBER_OF_VIEWS = 40000;

    private final DatabaseManager DB = new DatabaseManager(this);

    private int mHeight = 0;
    private int mWidth = 0;
    private int mTextSize = 0;

    private boolean forChoice = false;
    private String mCallerActivity;
    private int id_practice;
    private int id_practice_history;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practices_list);

        Intent intent = getIntent();
        id_practice = intent.getIntExtra("CurrentPracticeID", 0);
        forChoice = intent.getBooleanExtra("forChoice", false);
        mCallerActivity = intent.getStringExtra("CallerActivity");
        id_practice_history = intent.getIntExtra("CurrentPracticeHistoryID", 0);

        if (!Common.isDebug) {
            int mEditorID = getResources().getIdentifier("btPracticesDBEditor", "id", getPackageName());
            Button btEditor = (Button) findViewById(mEditorID);
            HideEditorButton(btEditor);
        }

        showPractices();

        setTitleOfActivity(this);
    }


    @Override
    public void onResume() {
        super.onResume();

        showPractices();

        Intent intent = getIntent();
        id_practice = intent.getIntExtra("CurrentPracticeID", 0);
        forChoice = intent.getBooleanExtra("forChoice", false);
        mCallerActivity = intent.getStringExtra("CallerActivity");
        id_practice_history = intent.getIntExtra("CurrentPracticeHistoryID", 0);

        TableRow mRow = (TableRow) findViewById(NUMBER_OF_VIEWS + id_practice);
        if (mRow != null) {
            int mScrID = getResources().getIdentifier("svTablePractices", "id", getPackageName());
            ScrollView mScrollView = (ScrollView) findViewById(mScrID);
            if (mScrollView != null) {

                mScrollView.requestChildFocus(mRow, mRow);
            }
        }
    }


    public void btPracticeAdd_onClick(final View view) {

        blink(view);
        Intent intent = new Intent(getApplicationContext(), ActivityPractice.class);
        intent.putExtra("IsNew", true);
        startActivity(intent);

    }

    private void showPractices() {

        List<Practice> practices;
        if (sessionUser != null) {

            practices = DB.getAllActivePracticesOfUser(sessionUser.getID());
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
            String nameProject="";
            try {
                Project project=DB.getProject(currentPractice.getIdProject());
                nameProject=project.getName();

            } catch (TableDoesNotContainElementException e) {

            }
            txt.setText(nameProject);
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setHeight(mHeight);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            mRow.addView(txt);

            mRow.setBackgroundResource(R.drawable.bt_border);
            layout.addView(mRow);

        }
        sv.addView(layout);

    }
    private void rowPractice_onClick(final TableRow v) {

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

            intent = new Intent(getApplicationContext(), myClass);
            intent.putExtra("CurrentPracticeHistoryID", id_practice_history);
            intent.putExtra("IsNew", false);
            intent.putExtra("CurrentPracticeID", id);

        } else {

            intent= new Intent(getApplicationContext(), ActivityPractice.class);
            intent.putExtra("CurrentPracticeID", id);
            intent.putExtra("IsNew", false);


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
            intent.putExtra("CurrentPracticeHistoryID", id_practice_history);
            intent.putExtra("IsNew", false);
            intent.putExtra("CurrentPracticeID", id_practice);

        } else {
            intent = new Intent(getApplicationContext(), ActivityMain.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}

