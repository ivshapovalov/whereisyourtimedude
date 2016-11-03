package ru.brainworkout.whereisyourtimedude.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import ru.brainworkout.whereisyourtimedude.R;
import ru.brainworkout.whereisyourtimedude.common.ConnectionParameters;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.PracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

import static ru.brainworkout.whereisyourtimedude.common.Common.*;

import static ru.brainworkout.whereisyourtimedude.common.Common.blink;
import static ru.brainworkout.whereisyourtimedude.common.Common.setTitleOfActivity;
import static ru.brainworkout.whereisyourtimedude.common.Session.*;


public class ActivityPracticeHistory extends AbstractActivity {

    private boolean isNew;
    private TextView tvID;

    private TextView tvDate;
    private TextView tvDuration;
    private TextView tvLastDate;
    private TextView tvLastTime;

    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private ConnectionParameters params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_history);

        Intent intent = getIntent();
        getIntentParams(intent);

        if (isNew) {
            if (sessionCurrentPracticeHistory == null) {
                sessionCurrentPracticeHistory = new PracticeHistory.Builder(DB.getPracticeHistoryMaxNumber() + 1).build();

                Calendar calendar = Calendar.getInstance();

                calendar.clear(Calendar.HOUR);
                calendar.clear(Calendar.HOUR_OF_DAY);
                calendar.clear(Calendar.MINUTE);
                calendar.clear(Calendar.SECOND);
                calendar.clear(Calendar.MILLISECOND);
                sessionCurrentPracticeHistory.setDate(calendar.getTimeInMillis());
                sessionCurrentPracticeHistory.setLastTime(calendar.getTimeInMillis());
            }
        } else {
            int id = intent.getIntExtra("CurrentPracticeHistoryID", 0);
            try {
                sessionCurrentPracticeHistory = DB.getPracticeHistory(id);

            } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
                tableDoesNotContainElementException.printStackTrace();
            }
        }
        long currentDateInMillis = intent.getLongExtra("CurrentDateInMillis", 0);
        if (currentDateInMillis != 0) {
            sessionCurrentPracticeHistory.setDate(currentDateInMillis);
        }

        long millis = intent.getLongExtra("millis", -1);
        if (millis!=-1) {
            if (sessionCurrentPracticeHistory!=null) {
                sessionCurrentPracticeHistory.setDuration(millis);
            }
        }

        showPracticeHistoryOnScreen();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setTitleOfActivity(this);
    }

    private void getIntentParams(Intent intent) {
        params = sessionOpenActivities.peek();
        isNew = (params != null ? params.isTransmitterNew() : false);
    }

    private void showPracticeHistoryOnScreen() {

        //ID
        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            tvID.setText(String.valueOf(sessionCurrentPracticeHistory.getId()));
        }

        //
        int mDate = getResources().getIdentifier("tvDate", "id", getPackageName());
        tvDate = (TextView) findViewById(mDate);
        if (tvDate != null) {

            tvDate.setText(convertMillisToStringDate(sessionCurrentPracticeHistory.getDate()));
        }

        //
        int mDuration = getResources().getIdentifier("tvDuration", "id", getPackageName());
        tvDuration = (TextView) findViewById(mDuration);
        if (tvDuration != null) {

            tvDuration.setText(String.valueOf(sessionCurrentPracticeHistory.getDuration()));
        }

        int mLastDate = getResources().getIdentifier("tvLastDate", "id", getPackageName());
        tvLastDate = (TextView) findViewById(mLastDate);
        if (tvLastDate != null) {

            tvLastDate.setText(convertMillisToStringDate(sessionCurrentPracticeHistory.getLastTime()));
        }

        int mLastTime = getResources().getIdentifier("tvLastTime", "id", getPackageName());
        tvLastTime = (TextView) findViewById(mLastTime);
        if (tvLastTime != null) {

            tvLastTime.setText(convertMillisToStringTime(sessionCurrentPracticeHistory.getLastTime()));
        }

        //ID
        int mPractice = getResources().getIdentifier("tvPractice", "id", getPackageName());
        TextView tvPractice = (TextView) findViewById(mPractice);
        if (tvPractice != null) {

            String namePractice = "";
            try {
                Practice practice = sessionCurrentPracticeHistory.getPractice();
                namePractice = practice.getName();

            } catch (TableDoesNotContainElementException e) {

            }
            tvPractice.setText(namePractice);
        }

    }

    public void btClose_onClick(final View view) {

        blink(view, this);
        Intent intent = new Intent(getApplicationContext(), ActivityPracticeHistoryList.class);
        intent.putExtra("CurrentPracticeHistoryID", sessionCurrentPracticeHistory.getId());
        sessionCurrentPracticeHistory = null;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }


    private void getPropertiesFromScreen() {

        //Имя
        int mDurationID = getResources().getIdentifier("tvDuration", "id", getPackageName());
        TextView tvDuration = (TextView) findViewById(mDurationID);
        if (tvDuration != null) {
            String dur = String.valueOf(tvDuration.getText());
            if (!"".equals(dur)) {
                sessionCurrentPracticeHistory.setDuration(Long.valueOf(dur));
            }
        }
    }

    public void tvDate_onClick(View view) {
        blink(view, this);
        getPropertiesFromScreen();
        //mCurrentPracticeHistory.dbSave(DB);
        Intent intent = new Intent(ActivityPracticeHistory.this, ActivityCalendarView.class);
        intent.putExtra("CurrentActivity", "ActivityPracticeHistory");
        intent.putExtra("CurrentDateInMillis", sessionCurrentPracticeHistory.getDate());
        intent.putExtra("isNew", false);
        startActivity(intent);
    }

    public void tvPractice_onClick(View view) {

        blink(view, this);
        getPropertiesFromScreen();

        int id_practice = sessionCurrentPracticeHistory.getPractice().getId();

        Intent intent = new Intent(getApplicationContext(), ActivityPracticesList.class);
        Boolean isNew = !DB.containsPracticeHistory(sessionCurrentPracticeHistory.getId());
        ConnectionParameters params = new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityPracticeHistory")
                .isTransmitterNew(isNew)
                .isTransmitterForChoice(false)
                .addReceiverActivityName("ActivityPracticesList")
                .isReceiverNew(false)
                .isReceiverForChoice(true)
                .build();
        sessionOpenActivities.push(params);
        intent.putExtra("CurrentPracticeID", id_practice);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void tvLastDate_onClick(View view) {


        blink(view, this);

        mDateSetListener =
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int yearSelected,
                                          int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(sessionCurrentPracticeHistory.getLastTime());
                        calendar.set(Calendar.YEAR, yearSelected);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        sessionCurrentPracticeHistory.setLastTime(calendar.getTimeInMillis());
                        showPracticeHistoryOnScreen();
                        // Set the Selected Date in Select date Button
                        //tvLastDate.setText("Date selected : " + day + "-" + month + "-" + year);
                    }
                };
        showDialog(0);

    }

    public void tvLastTime_onClick(View view) {

        blink(view, this);

        // Register  TimePickerDialog listener
        mTimeSetListener =
                new TimePickerDialog.OnTimeSetListener() {
                    // the callback received when the user "sets" the TimePickerDialog in the dialog
                    public void onTimeSet(TimePicker view, int hourOfDay, int min) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(sessionCurrentPracticeHistory.getLastTime());
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, min);
                        sessionCurrentPracticeHistory.setLastTime(calendar.getTimeInMillis());
                        showPracticeHistoryOnScreen();
                    }
                };
        showDialog(1);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(sessionCurrentPracticeHistory.getLastTime());
        switch (id) {
            case 0:
                // create a new DatePickerDialog with values you want to show
                return new DatePickerDialog(this,
                        mDateSetListener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            // create a new TimePickerDialog with values you want to show
            case 1:
                return new TimePickerDialog(this,
                        mTimeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

        }
        return null;
    }


    public void btSave_onClick(final View view) {

        blink(view, this);
        getPropertiesFromScreen();
        sessionCurrentPracticeHistory.dbSave(DB);

        Intent intent = new Intent(getApplicationContext(), ActivityPracticeHistoryList.class);
        intent.putExtra("CurrentPracticeHistoryID", sessionCurrentPracticeHistory.getId());
        sessionCurrentPracticeHistory = null;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), ActivityPracticeHistoryList.class);
        intent.putExtra("CurrentPracticeHistoryID", sessionCurrentPracticeHistory.getId());
        sessionCurrentPracticeHistory = null;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btDelete_onClick(final View view) {

        blink(view, this);


        new AlertDialog.Builder(this)
                .setMessage("Вы действительно хотите удалить текущую историю?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sessionCurrentPracticeHistory.dbDelete(DB);
                        sessionCurrentPracticeHistory = null;

                        Intent intent = new Intent(getApplicationContext(), ActivityPracticeHistoryList.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                }).setNegativeButton("Нет", null).show();

    }


    public void tvDuration_onClick(View view) {

        Intent intent = new Intent(ActivityPracticeHistory.this, ActivityDateTimePickerDialog.class);
        intent.putExtra("millis", sessionCurrentPracticeHistory.getDuration());
        intent.putExtra("CurrentActivity", "ActivityPracticeHistory");
        intent.putExtra("isNew", false);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}