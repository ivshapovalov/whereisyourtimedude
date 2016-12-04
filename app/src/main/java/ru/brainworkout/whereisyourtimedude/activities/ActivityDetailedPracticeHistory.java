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
import ru.brainworkout.whereisyourtimedude.database.entities.DetailedPracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.PracticeHistory;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

import static ru.brainworkout.whereisyourtimedude.common.Common.*;
import static ru.brainworkout.whereisyourtimedude.common.Session.*;


public class ActivityDetailedPracticeHistory extends AbstractActivity {
    private boolean isNew;
    private TextView tvID;

    private TextView tvDate;
    private TextView tvDuration;
    private TextView tvTimeDate;
    private TextView tvTimeTime;

    private TimePickerDialog.OnTimeSetListener mTimeSetListener;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private ConnectionParameters params;
    private DetailedPracticeHistory currentDetailedPracticeHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_practice_history);

        Intent intent = getIntent();
        getIntentParams(intent);

        if (isNew) {
            if (!sessionPracticeHistorySequence.isEmpty()) {
                currentDetailedPracticeHistory = sessionDetailedPracticeHistorySequence.pollFirst();
            } else {
                currentDetailedPracticeHistory = new DetailedPracticeHistory.Builder(DB).build();
                Calendar calendar = Calendar.getInstance();
                calendar.clear(Calendar.HOUR);
                calendar.clear(Calendar.HOUR_OF_DAY);
                calendar.clear(Calendar.MINUTE);
                calendar.clear(Calendar.SECOND);
                calendar.clear(Calendar.MILLISECOND);
                currentDetailedPracticeHistory.setDate(calendar.getTimeInMillis());
                currentDetailedPracticeHistory.setTime(calendar.getTimeInMillis());
            }
        } else {
            int id = intent.getIntExtra("CurrentDetailedPracticeHistoryID", 0);
            if (DB.containsDetailedPracticeHistory(id)) {
                currentDetailedPracticeHistory = DB.getDetailedPracticeHistory(id);
            } else {
                throw new TableDoesNotContainElementException(String.format("Practice history with id ='%s' does not exists in database", id));
            }
        }

        if (currentDetailedPracticeHistory != null) {
            long currentDateInMillis = intent.getLongExtra("CurrentDateInMillis", 0);
            if (currentDateInMillis != 0) {
                currentDetailedPracticeHistory.setDate(currentDateInMillis);
            }

            long millis = intent.getLongExtra("millis", -1);
            if (millis != -1) {
                currentDetailedPracticeHistory.setDuration(millis);
            }
        }
        showDetailedPracticeHistoryOnScreen();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setTitleOfActivity(this);

    }

    private void getIntentParams(Intent intent) {
        params = sessionOpenActivities.peek();
        isNew = (params != null ? params.isTransmitterNew() : false);
    }

    private void showDetailedPracticeHistoryOnScreen() {

        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        tvID = (TextView) findViewById(mID);
        if (tvID != null) {
            tvID.setText(String.valueOf(currentDetailedPracticeHistory.getId()));
        }

        int mDate = getResources().getIdentifier("tvDate", "id", getPackageName());
        tvDate = (TextView) findViewById(mDate);
        if (tvDate != null) {
            tvDate.setText(convertMillisToStringDate(currentDetailedPracticeHistory.getDate()));
        }

        int mDuration = getResources().getIdentifier("tvDuration", "id", getPackageName());
        tvDuration = (TextView) findViewById(mDuration);
        if (tvDuration != null) {
            tvDuration.setText(String.valueOf(currentDetailedPracticeHistory.getDuration()));
        }

        int mTimeDate = getResources().getIdentifier("tvTimeDate", "id", getPackageName());
        tvTimeDate = (TextView) findViewById(mTimeDate);
        if (tvTimeDate != null) {
            tvTimeDate.setText(convertMillisToStringDate(currentDetailedPracticeHistory.getTime()));
        }

        int mTimeTime = getResources().getIdentifier("tvTimeTime", "id", getPackageName());
        tvTimeTime = (TextView) findViewById(mTimeTime);
        if (tvTimeTime != null) {
            tvTimeTime.setText(convertMillisToStringTime(currentDetailedPracticeHistory.getTime()));
        }

        int mPractice = getResources().getIdentifier("tvPractice", "id", getPackageName());
        TextView tvPractice = (TextView) findViewById(mPractice);
        if (tvPractice != null) {
            Practice practice = currentDetailedPracticeHistory.getPractice();
            String namePractice = "";
            if (practice != null) {
                namePractice = practice.getName();
            }
            tvPractice.setText(namePractice);
        }
    }

    public void btClose_onClick(final View view) {

        blink(view, this);
        closeActivity(new Intent(getApplicationContext(), ActivityDetailedPracticeHistoryList.class));

    }

    private void closeActivity(Intent intent) {
        intent.putExtra("CurrentDetailedPracticeHistoryID", currentDetailedPracticeHistory.getId());
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
                currentDetailedPracticeHistory.setDuration(Long.valueOf(dur));
            }
        }
    }

    public void tvDate_onClick(View view) {
        blink(view, this);
        getPropertiesFromScreen();
        Intent intent = new Intent(ActivityDetailedPracticeHistory.this, ActivityCalendarView.class);
        intent.putExtra("CurrentActivity", "ActivityDetailedPracticeHistory");
        intent.putExtra("CurrentDateInMillis", currentDetailedPracticeHistory.getDate());
        intent.putExtra("isNew", false);
        startActivity(intent);
    }

    public void tvPractice_onClick(View view) {

        blink(view, this);
        getPropertiesFromScreen();

        int id_practice = 0;
        Practice practice = currentDetailedPracticeHistory.getPractice();
        if (practice != null) {
            id_practice = practice.getId();
        }

        Intent intent = new Intent(getApplicationContext(), ActivityPracticeList.class);
        Boolean isNew = !DB.containsDetailedPracticeHistory(currentDetailedPracticeHistory.getId());
        ConnectionParameters params = new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityDetailedPracticeHistory")
                .isTransmitterNew(isNew)
                .isTransmitterForChoice(false)
                .addReceiverActivityName("ActivityPracticeList")
                .isReceiverNew(false)
                .isReceiverForChoice(true)
                .build();
        sessionDetailedPracticeHistorySequence.push(currentDetailedPracticeHistory);
        sessionOpenActivities.push(params);
        intent.putExtra("CurrentPracticeID", id_practice);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void tvTimeDate_onClick(View view) {
        blink(view, this);

        mDateSetListener =
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view, int yearSelected,
                                          int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(currentDetailedPracticeHistory.getTime());
                        calendar.set(Calendar.YEAR, yearSelected);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        currentDetailedPracticeHistory.setTime(calendar.getTimeInMillis());
                        showDetailedPracticeHistoryOnScreen();
                        // Set the Selected Date in Select date Button
                        //tvLastDate.setText("Date selected : " + day + "-" + month + "-" + year);
                    }
                };
        showDialog(0);

    }

    public void tvTimeTime_onClick(View view) {
        blink(view, this);

        mTimeSetListener =
                new TimePickerDialog.OnTimeSetListener() {
                    // the callback received when the user "sets" the TimePickerDialog in the dialog
                    public void onTimeSet(TimePicker view, int hourOfDay, int min) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(currentDetailedPracticeHistory.getTime());
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, min);
                        currentDetailedPracticeHistory.setTime(calendar.getTimeInMillis());
                        showDetailedPracticeHistoryOnScreen();
                    }
                };
        showDialog(1);

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentDetailedPracticeHistory.getTime());
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
        currentDetailedPracticeHistory.dbSave(DB);
        closeActivity(new Intent(getApplicationContext(), ActivityDetailedPracticeHistoryList.class));

    }

    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), ActivityDetailedPracticeHistoryList.class);
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
                .setMessage("Do you want to delete current detailed practice history?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        currentDetailedPracticeHistory.dbDelete(DB);
                        Intent intent = new Intent(getApplicationContext(), ActivityDetailedPracticeHistoryList.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).setNegativeButton("Нет", null).show();
    }

    public void tvDuration_onClick(View view) {
        Intent intent = new Intent(ActivityDetailedPracticeHistory.this, ActivityDateTimePickerDialog.class);
        intent.putExtra("millis", currentDetailedPracticeHistory.getDuration());
        intent.putExtra("CurrentActivity", "ActivityDetailedPracticeHistory");
        intent.putExtra("ID", currentDetailedPracticeHistory.getId());
        intent.putExtra("isNew", DB.containsDetailedPracticeHistory(currentDetailedPracticeHistory.getId()));
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}