package ru.ivan.whereisyourtimedude.activities;

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

import ru.ivan.whereisyourtimedude.R;
import ru.ivan.whereisyourtimedude.common.ConnectionParameters;
import ru.ivan.whereisyourtimedude.database.entities.Practice;
import ru.ivan.whereisyourtimedude.database.entities.PracticeHistory;
import ru.ivan.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

import static ru.ivan.whereisyourtimedude.common.Common.*;

import static ru.ivan.whereisyourtimedude.common.Common.blink;
import static ru.ivan.whereisyourtimedude.common.Common.setTitleOfActivity;
import static ru.ivan.whereisyourtimedude.common.Session.*;


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

            if (sessionCurrentPracticeHistory == null) {
                int id = intent.getIntExtra("CurrentPracticeHistoryID", 0);
                if (DB.containsPracticeHistory(id)) {
                    sessionCurrentPracticeHistory = DB.getPracticeHistory(id);
                } else {
                    throw new TableDoesNotContainElementException(String.format("Practice history with id ='%s' does not exists in database", id));
                }
            }
        }
        long currentDateInMillis = intent.getLongExtra("CurrentDateInMillis", 0);
        if (currentDateInMillis != 0) {
            sessionCurrentPracticeHistory.setDate(currentDateInMillis);
        }

        long millis = intent.getLongExtra("millis", -1);
        if (millis != -1) {
            if (sessionCurrentPracticeHistory != null) {
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

        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        tvID = (TextView) findViewById(mID);
        if (tvID != null) {
            tvID.setText(String.valueOf(sessionCurrentPracticeHistory.getId()));
        }

        int mDate = getResources().getIdentifier("tvDate", "id", getPackageName());
        tvDate = (TextView) findViewById(mDate);
        if (tvDate != null) {

            tvDate.setText(convertMillisToStringDate(sessionCurrentPracticeHistory.getDate()));
        }

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

        int mPractice = getResources().getIdentifier("tvPractice", "id", getPackageName());
        TextView tvPractice = (TextView) findViewById(mPractice);
        if (tvPractice != null) {
            Practice practice = sessionCurrentPracticeHistory.getPractice();
            String namePractice = "";
            if (practice != null) {
                namePractice = practice.getName();
            }
            tvPractice.setText(namePractice);
        }
    }

    public void btClose_onClick(final View view) {

        blink(view, this);
        closeActivity();
    }


    private void getPropertiesFromScreen() {

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
        Intent intent = new Intent(ActivityPracticeHistory.this, ActivityCalendarView.class);
        intent.putExtra("CurrentActivity", "ActivityPracticeHistory");
        intent.putExtra("CurrentDateInMillis", sessionCurrentPracticeHistory.getDate());
        intent.putExtra("isNew", false);
        startActivity(intent);
    }

    public void tvPractice_onClick(View view) {

        blink(view, this);
        getPropertiesFromScreen();

        Practice practice = sessionCurrentPracticeHistory.getPractice();
        int id_practice = 0;
        if (practice != null) {
            id_practice = practice.getId();
        }

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
                    }
                };
        showDialog(0);
    }

    public void tvLastTime_onClick(View view) {
        blink(view, this);

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
                return new DatePickerDialog(this,
                        mDateSetListener,
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
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

        closeActivity();
    }

    public void onBackPressed() {

        closeActivity();
    }

    private void closeActivity() {
        Intent intent = new Intent(getApplicationContext(), ActivityPracticeHistoryList.class);
        intent.putExtra("CurrentPracticeHistoryID", sessionCurrentPracticeHistory.getId());
        sessionCurrentPracticeHistory = null;
        sessionOpenActivities.pollFirst();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btDelete_onClick(final View view) {
        blink(view, this);
        new AlertDialog.Builder(this)
                .setMessage("Do you want to delete current practice history?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sessionCurrentPracticeHistory.dbDelete(DB);
                        closeActivity();
                    }
                }).setNegativeButton("No", null).show();
    }

    public void tvDuration_onClick(View view) {
        Intent intent = new Intent(ActivityPracticeHistory.this, ActivityDateTimePickerDialog.class);
        intent.putExtra("millis", sessionCurrentPracticeHistory.getDuration());
        intent.putExtra("CurrentActivity", "ActivityPracticeHistory");
        intent.putExtra("ID", sessionCurrentPracticeHistory.getId());
        intent.putExtra("isNew", DB.containsPracticeHistory(sessionCurrentPracticeHistory.getId()));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}