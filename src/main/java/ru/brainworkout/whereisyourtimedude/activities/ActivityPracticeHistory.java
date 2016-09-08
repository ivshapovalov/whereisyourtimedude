package ru.brainworkout.whereisyourtimedude.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

import ru.brainworkout.whereisyourtimedude.R;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.PracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.entities.Project;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

import static ru.brainworkout.whereisyourtimedude.common.Common.ConvertMillisToString;
import static ru.brainworkout.whereisyourtimedude.common.Common.blink;
import static ru.brainworkout.whereisyourtimedude.common.Common.setTitleOfActivity;

public class ActivityPracticeHistory extends AppCompatActivity {

    private PracticeHistory mCurrentPracticeHistory;
    private final DatabaseManager DB = new DatabaseManager(this);
    private  boolean isNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_history);

        Intent intent = getIntent();
        isNew= intent.getBooleanExtra("isNew", false);

        if (isNew) {
            mCurrentPracticeHistory = new PracticeHistory.Builder(DB.getPracticeHistoryMaxNumber() + 1).build();
            Calendar calendar = Calendar.getInstance();

            calendar.clear(Calendar.HOUR);
            calendar.clear(Calendar.HOUR_OF_DAY);
            calendar.clear(Calendar.MINUTE);
            calendar.clear(Calendar.SECOND);
            calendar.clear(Calendar.MILLISECOND);
            mCurrentPracticeHistory.setDate(calendar.getTimeInMillis());

        } else {
            int id = intent.getIntExtra("CurrentPracticeHistoryID", 0);
            try {
                mCurrentPracticeHistory = DB.getPracticeHistory(id);
                long currentDateInMillis = intent.getLongExtra("CurrentDateInMillis", 0);
                if (currentDateInMillis != 0) {
                    mCurrentPracticeHistory.setDate(currentDateInMillis);
                }
            } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
                tableDoesNotContainElementException.printStackTrace();
            }
        }
        int id_practice = intent.getIntExtra("CurrentPracticeID", 0);
        if (id_practice != 0) {
            mCurrentPracticeHistory.setIdPractice(id_practice);
        }

        showPracticeHistoryOnScreen();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setTitleOfActivity(this);
    }


    private void showPracticeHistoryOnScreen() {

        //ID
        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            tvID.setText(String.valueOf(mCurrentPracticeHistory.getID()));
        }

        //ID
        int mDate = getResources().getIdentifier("tvDate", "id", getPackageName());
        TextView tvDate = (TextView) findViewById(mDate);
        if (tvDate != null) {

            tvDate.setText(ConvertMillisToString(mCurrentPracticeHistory.getDate()));
        }

        //ID
        int mDuration = getResources().getIdentifier("etDuration", "id", getPackageName());
        TextView tvDuration = (TextView) findViewById(mDuration);
        if (tvDuration != null) {

            tvDuration.setText(String.valueOf(mCurrentPracticeHistory.getDuration()));
        }

        //ID
        int mPractice = getResources().getIdentifier("tvPractice", "id", getPackageName());
        TextView tvPractice = (TextView) findViewById(mPractice);
        if (tvPractice != null) {

            String namePractice = "";
            try {
                Practice practice = DB.getPractice(mCurrentPracticeHistory.getIdPractice());
                namePractice = practice.getName();

            } catch (TableDoesNotContainElementException e) {

            }
            tvPractice.setText(namePractice);
        }

    }

    public void btClose_onClick(final View view) {

        blink(view);
        Intent intent = new Intent(getApplicationContext(), ActivityPracticeHistoryList.class);
        intent.putExtra("CurrentPracticeHistoryID", mCurrentPracticeHistory.getID());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }


    private void getPropertiesFromScreen() {

        //Имя
        int mDurationID = getResources().getIdentifier("etDuration", "id", getPackageName());
        EditText etDuration = (EditText) findViewById(mDurationID);
        if (etDuration != null) {
            String dur = String.valueOf(etDuration.getText());
            if (!"".equals(dur)) {
                mCurrentPracticeHistory.setDuration(Long.valueOf(dur));
            }

        }

    }

    public void tvDate_onClick(View view) {


        blink(view);

        getPropertiesFromScreen();
        mCurrentPracticeHistory.dbSave(DB);

        Intent intent = new Intent(ActivityPracticeHistory.this, ActivityCalendarView.class);
        intent.putExtra("CurrentActivity", "ActivityPracticeHistory");
        intent.putExtra("CurrentDateInMillis", mCurrentPracticeHistory.getDate());
        intent.putExtra("CurrentPracticeHistoryID", mCurrentPracticeHistory.getId());
        startActivity(intent);

    }

    public void tvPractice_onClick(View view) {

        blink(view);

        getPropertiesFromScreen();

        mCurrentPracticeHistory.dbSave(DB);

        int id_practice = mCurrentPracticeHistory.getIdPractice();

        Intent intent = new Intent(getApplicationContext(), ActivityPracticesList.class);
        intent.putExtra("CurrentPracticeHistoryID", mCurrentPracticeHistory.getID());
        intent.putExtra("isNew", false);
        intent.putExtra("forChoice", true);
        intent.putExtra("CurrentPracticeID", id_practice);
        intent.putExtra("CallerActivity", "ActivityPracticeHistory");
        startActivity(intent);

    }

    public void btSave_onClick(final View view) {

        blink(view);
        getPropertiesFromScreen();

        mCurrentPracticeHistory.dbSave(DB);

        Intent intent = new Intent(getApplicationContext(), ActivityPracticeHistoryList.class);
        intent.putExtra("CurrentPracticeHistoryID", mCurrentPracticeHistory.getID());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btDelete_onClick(final View view) {

        blink(view);


        new AlertDialog.Builder(this)
                .setMessage("Вы действительно хотите удалить текущую историю?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mCurrentPracticeHistory.dbDelete(DB);

                        Intent intent = new Intent(getApplicationContext(), ActivityProjectsList.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                }).setNegativeButton("Нет", null).show();

    }


}