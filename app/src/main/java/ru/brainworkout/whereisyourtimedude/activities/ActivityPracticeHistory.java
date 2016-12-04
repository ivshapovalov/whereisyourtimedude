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
    private PracticeHistory currentPracticeHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_history);

        Intent intent = getIntent();
        getIntentParams(intent);

        if (isNew) {
            if (!sessionPracticeHistorySequence.isEmpty()) {
                currentPracticeHistory = sessionPracticeHistorySequence.pollFirst();
            } else {
                currentPracticeHistory = new PracticeHistory.Builder(DB).build();
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

            if (currentPracticeHistory == null) {
                int id = intent.getIntExtra("CurrentPracticeHistoryID", 0);
                if (DB.containsPracticeHistory(id)) {
                    currentPracticeHistory = DB.getPracticeHistory(id);
                } else {
                    throw new TableDoesNotContainElementException(String.format("Practice history with id ='%s' does not exists in database", id));
                }
            }
        }
        long currentDateInMillis = intent.getLongExtra("CurrentDateInMillis", 0);
        if (currentDateInMillis != 0) {
            currentPracticeHistory.setDate(currentDateInMillis);
        }

        long millis = intent.getLongExtra("millis", -1);
        if (millis != -1) {
            if (currentPracticeHistory != null) {
                currentPracticeHistory.setDuration(millis);
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
            tvID.setText(String.valueOf(currentPracticeHistory.getId()));
        }

        int mDate = getResources().getIdentifier("tvDate", "id", getPackageName());
        tvDate = (TextView) findViewById(mDate);
        if (tvDate != null) {

            tvDate.setText(convertMillisToStringDate(currentPracticeHistory.getDate()));
        }

        int mDuration = getResources().getIdentifier("tvDuration", "id", getPackageName());
        tvDuration = (TextView) findViewById(mDuration);
        if (tvDuration != null) {

            tvDuration.setText(String.valueOf(currentPracticeHistory.getDuration()));
        }

        int mLastDate = getResources().getIdentifier("tvLastDate", "id", getPackageName());
        tvLastDate = (TextView) findViewById(mLastDate);
        if (tvLastDate != null) {

            tvLastDate.setText(convertMillisToStringDate(currentPracticeHistory.getLastTime()));
        }

        int mLastTime = getResources().getIdentifier("tvLastTime", "id", getPackageName());
        tvLastTime = (TextView) findViewById(mLastTime);
        if (tvLastTime != null) {

            tvLastTime.setText(convertMillisToStringTime(currentPracticeHistory.getLastTime()));
        }

        int mPractice = getResources().getIdentifier("tvPractice", "id", getPackageName());
        TextView tvPractice = (TextView) findViewById(mPractice);
        if (tvPractice != null) {
            Practice practice = currentPracticeHistory.getPractice();
            String namePractice = "";
            if (practice != null) {
                namePractice = practice.getName();
            }
            tvPractice.setText(namePractice);
        }
    }

    public void btClose_onClick(final View view) {

        blink(view, this);
        closeActivity(new Intent(getApplicationContext(), ActivityPracticeHistoryList.class));

    }

    private void closeActivity(Intent intent) {
        intent.putExtra("CurrentPracticeHistoryID", currentPracticeHistory.getId());
        sessionOpenActivities.pollFirst();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void getPropertiesFromScreen() {
        int mDurationID = getResources().getIdentifier("tvDuration", "id", getPackageName());
        TextView tvDuration = (TextView) findViewById(mDurationID);
        if (tvDuration != null) {
            String dur = String.valueOf(tvDuration.getText());
            if (!"".equals(dur)) {
                currentPracticeHistory.setDuration(Long.valueOf(dur));
            }
        }
    }

    public void tvDate_onClick(View view) {
        blink(view, this);
        getPropertiesFromScreen();
        Intent intent = new Intent(ActivityPracticeHistory.this, ActivityCalendarView.class);
        intent.putExtra("CurrentActivity", "ActivityPracticeHistory");
        intent.putExtra("CurrentDateInMillis", currentPracticeHistory.getDate());
        intent.putExtra("isNew", false);
        startActivity(intent);
    }

    public void tvPractice_onClick(View view) {

        blink(view, this);
        getPropertiesFromScreen();

        Practice practice = currentPracticeHistory.getPractice();
        int id_practice = 0;
        if (practice != null) {
            id_practice = practice.getId();
        }

        Intent intent = new Intent(getApplicationContext(), ActivityPracticeList.class);
        Boolean isNew = !DB.containsPracticeHistory(currentPracticeHistory.getId());
        ConnectionParameters params = new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityPracticeHistory")
                .isTransmitterNew(isNew)
                .isTransmitterForChoice(false)
                .addReceiverActivityName("ActivityPracticeList")
                .isReceiverNew(false)
                .isReceiverForChoice(true)
                .build();
        sessionPracticeHistorySequence.push(currentPracticeHistory);
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
                        calendar.setTimeInMillis(currentPracticeHistory.getLastTime());
                        calendar.set(Calendar.YEAR, yearSelected);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        currentPracticeHistory.setLastTime(calendar.getTimeInMillis());
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
                        calendar.setTimeInMillis(currentPracticeHistory.getLastTime());
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, min);
                        currentPracticeHistory.setLastTime(calendar.getTimeInMillis());
                        showPracticeHistoryOnScreen();
                    }
                };
        showDialog(1);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentPracticeHistory.getLastTime());
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
        currentPracticeHistory.dbSave(DB);
        closeActivity(new Intent(getApplicationContext(), ActivityPracticeHistoryList.class));
    }

    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), ActivityPracticeHistoryList.class);
        if (params != null) {
            Class<?> myClass = null;
            try {
                myClass = Class.forName(getPackageName() + ".activities." + params.getTransmitterActivityName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            intent = new Intent(getApplicationContext(), myClass);
        }
        closeActivity(intent);
    }

    public void btDelete_onClick(final View view) {
        blink(view, this);
        new AlertDialog.Builder(this)
                .setMessage("Do you want to delete current practice history?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        currentPracticeHistory.dbDelete(DB);
                        Intent intent = new Intent(getApplicationContext(), ActivityPracticeHistoryList.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).setNegativeButton("Нет", null).show();
    }


    public void tvDuration_onClick(View view) {
        Intent intent = new Intent(ActivityPracticeHistory.this, ActivityDateTimePickerDialog.class);
        intent.putExtra("millis", currentPracticeHistory.getDuration());
        intent.putExtra("CurrentActivity", "ActivityPracticeHistory");
        intent.putExtra("ID", currentPracticeHistory.getId());
        intent.putExtra("isNew", DB.containsPracticeHistory(currentPracticeHistory.getId()));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}