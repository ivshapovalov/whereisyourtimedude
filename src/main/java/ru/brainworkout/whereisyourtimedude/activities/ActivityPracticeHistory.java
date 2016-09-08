package ru.brainworkout.whereisyourtimedude.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import ru.brainworkout.whereisyourtimedude.R;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.PracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

import static ru.brainworkout.whereisyourtimedude.common.Common.*;
import static ru.brainworkout.whereisyourtimedude.common.Common.blink;
import static ru.brainworkout.whereisyourtimedude.common.Common.setTitleOfActivity;
import static ru.brainworkout.whereisyourtimedude.common.Session.currentPracticeHistory;

public class ActivityPracticeHistory extends AppCompatActivity {

    private final DatabaseManager DB = new DatabaseManager(this);
    private boolean isNew;
    TextView tvID;
    TextView tvDate;
    TextView tvDuration;
    TextView tvLastDate;
    TextView tvLastTime;

    TimePickerDialog.OnTimeSetListener mTimeSetListener;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_history);

        Intent intent = getIntent();
        isNew = intent.getBooleanExtra("isNew", false);

        if (isNew) {
            if (currentPracticeHistory == null) {
                currentPracticeHistory = new PracticeHistory.Builder(DB.getPracticeHistoryMaxNumber() + 1).build();

                Calendar calendar = Calendar.getInstance();

                calendar.clear(Calendar.HOUR);
                calendar.clear(Calendar.HOUR_OF_DAY);
                calendar.clear(Calendar.MINUTE);
                calendar.clear(Calendar.SECOND);
                calendar.clear(Calendar.MILLISECOND);
                currentPracticeHistory.setDate(calendar.getTimeInMillis());
                currentPracticeHistory.setLastTime(calendar.getTimeInMillis());
            }
        } else {
            int id = intent.getIntExtra("CurrentPracticeHistoryID", 0);
            try {
                currentPracticeHistory = DB.getPracticeHistory(id);

            } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
                tableDoesNotContainElementException.printStackTrace();
            }
        }
        long currentDateInMillis = intent.getLongExtra("CurrentDateInMillis", 0);
        if (currentDateInMillis != 0) {
            currentPracticeHistory.setDate(currentDateInMillis);
        }

        showPracticeHistoryOnScreen();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setTitleOfActivity(this);
    }


    private void showPracticeHistoryOnScreen() {

        //ID
        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            tvID.setText(String.valueOf(currentPracticeHistory.getID()));
        }

        //
        int mDate = getResources().getIdentifier("tvDate", "id", getPackageName());
        tvDate = (TextView) findViewById(mDate);
        if (tvDate != null) {

            tvDate.setText(ConvertMillisToStringDate(currentPracticeHistory.getDate()));
        }

        //
        int mDuration = getResources().getIdentifier("etDuration", "id", getPackageName());
        tvDuration = (TextView) findViewById(mDuration);
        if (tvDuration != null) {

            tvDuration.setText(String.valueOf(currentPracticeHistory.getDuration()));
        }

        int mLastDate = getResources().getIdentifier("tvLastDate", "id", getPackageName());
        tvLastDate = (TextView) findViewById(mLastDate);
        if (tvLastDate != null) {

            tvLastDate.setText(ConvertMillisToStringDate(currentPracticeHistory.getLastTime()));
        }

        int mLastTime = getResources().getIdentifier("tvLastTime", "id", getPackageName());
        tvLastTime = (TextView) findViewById(mLastTime);
        if (tvLastTime != null) {

            tvLastTime.setText(ConvertMillisToStringTime(currentPracticeHistory.getLastTime()));
        }

        //ID
        int mPractice = getResources().getIdentifier("tvPractice", "id", getPackageName());
        TextView tvPractice = (TextView) findViewById(mPractice);
        if (tvPractice != null) {

            String namePractice = "";
            try {
                Practice practice = DB.getPractice(currentPracticeHistory.getIdPractice());
                namePractice = practice.getName();

            } catch (TableDoesNotContainElementException e) {

            }
            tvPractice.setText(namePractice);
        }

    }

    public void btClose_onClick(final View view) {

        blink(view);
        Intent intent = new Intent(getApplicationContext(), ActivityPracticeHistoryList.class);
        intent.putExtra("CurrentPracticeHistoryID", currentPracticeHistory.getID());
        currentPracticeHistory = null;
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
                currentPracticeHistory.setDuration(Long.valueOf(dur));
            }

        }

    }

    public void tvDate_onClick(View view) {
        blink(view);
        getPropertiesFromScreen();
        //mCurrentPracticeHistory.dbSave(DB);
        Intent intent = new Intent(ActivityPracticeHistory.this, ActivityCalendarView.class);
        intent.putExtra("CurrentActivity", "ActivityPracticeHistory");
        intent.putExtra("CurrentDateInMillis", currentPracticeHistory.getDate());
        intent.putExtra("isNew", false);
        startActivity(intent);

    }

    public void tvPractice_onClick(View view) {

        blink(view);
        getPropertiesFromScreen();

        int id_practice = currentPracticeHistory.getIdPractice();

        Intent intent = new Intent(getApplicationContext(), ActivityPracticesList.class);
        intent.putExtra("isNew", false);
        intent.putExtra("forChoice", true);
        intent.putExtra("CurrentPracticeID", id_practice);
        intent.putExtra("CallerActivity", "ActivityPracticeHistory");
        startActivity(intent);

    }

    public void tvLastDate_onClick(View view) {

        mDateSetListener =
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int yearSelected,
                                          int monthOfYear, int dayOfMonth) {
                        Calendar calendar=Calendar.getInstance();
                        calendar.setTimeInMillis(currentPracticeHistory.getLastTime());
                        calendar.set(Calendar.YEAR,yearSelected);
                        calendar.set(Calendar.MONTH,monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        currentPracticeHistory.setLastTime(calendar.getTimeInMillis());
                        showPracticeHistoryOnScreen();
                        // Set the Selected Date in Select date Button
                        //tvLastDate.setText("Date selected : " + day + "-" + month + "-" + year);
                    }
                };
        showDialog(0);

    }

    public void tvLastTime_onClick(View view) {

        // Register  TimePickerDialog listener
        mTimeSetListener =
                new TimePickerDialog.OnTimeSetListener() {
                    // the callback received when the user "sets" the TimePickerDialog in the dialog
                    public void onTimeSet(TimePicker view, int hourOfDay, int min) {
                        Calendar calendar=Calendar.getInstance();
                        calendar.setTimeInMillis(currentPracticeHistory.getLastTime());
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,min);
                        currentPracticeHistory.setLastTime(calendar.getTimeInMillis());
                        showPracticeHistoryOnScreen();
                    }
                };
        showDialog(1);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Calendar calendar=Calendar.getInstance();
        calendar.setTimeInMillis(currentPracticeHistory.getLastTime());
        switch (id) {
            case 0:
                // create a new DatePickerDialog with values you want to show
                return new DatePickerDialog(this,
                        mDateSetListener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            // create a new TimePickerDialog with values you want to show
            case 1:
                return new TimePickerDialog(this,
                        mTimeSetListener, calendar.get(Calendar.HOUR), calendar.get(Calendar.MINUTE), true);

        }
        return null;
    }


    public void btSave_onClick(final View view) {

        blink(view);
        getPropertiesFromScreen();
        currentPracticeHistory.dbSave(DB);

        Intent intent = new Intent(getApplicationContext(), ActivityPracticeHistoryList.class);
        intent.putExtra("CurrentPracticeHistoryID", currentPracticeHistory.getID());
        currentPracticeHistory = null;
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
                        currentPracticeHistory.dbDelete(DB);
                        currentPracticeHistory = null;

                        Intent intent = new Intent(getApplicationContext(), ActivityProjectsList.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                }).setNegativeButton("Нет", null).show();

    }


}