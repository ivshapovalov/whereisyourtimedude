package ru.brainworkout.whereisyourtimedude.activities;

import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import ru.brainworkout.whereisyourtimedude.R;

import static ru.brainworkout.whereisyourtimedude.common.Common.blink;

public class ActivityDateTimePickerDialog extends AbstractActivity {

    //private static long oldTime;
    private static long newTime;
    private static boolean mCallerIsNew;
    private static String mCallerActivity;

    private static long days;
    private static long hours;
    private static long minutes;
    private static long seconds;
    private static long millis;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_time_picker);

        Intent intent = getIntent();
        newTime = intent.getLongExtra("millis", 0);
        mCallerIsNew= intent.getBooleanExtra("isNew", false);
        mCallerActivity= intent.getStringExtra("CurrentActivity");

        parseTime();
        updateScreen();
    }

    private void parseTime() {
        days = TimeUnit.MILLISECONDS.toDays(newTime);
        hours = TimeUnit.MILLISECONDS.toHours(newTime) - TimeUnit.DAYS.toHours(days);
        minutes = TimeUnit.MILLISECONDS.toMinutes(newTime) -
                TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(newTime));
        seconds = TimeUnit.MILLISECONDS.toSeconds(newTime) -
                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(newTime));
        millis = newTime -
                TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(newTime));
    }

    private void updateScreen() {

        int mDaysID = getResources().getIdentifier("tvDays", "id", getPackageName());
        TextView tvDays = (TextView) findViewById(mDaysID);
        if (tvDays != null) {
            tvDays.setText(String.valueOf(days));
        }

        int mHoursID = getResources().getIdentifier("tvHours", "id", getPackageName());
        TextView tvHours = (TextView) findViewById(mHoursID);
        if (tvHours != null) {
            tvHours.setText(String.valueOf(hours));
        }
        int mMinutesID = getResources().getIdentifier("tvMinutes", "id", getPackageName());
        TextView tvMinutes = (TextView) findViewById(mMinutesID);
        if (tvMinutes != null) {
            tvMinutes.setText(String.valueOf(minutes));
        }
        int mSecondsID = getResources().getIdentifier("tvSeconds", "id", getPackageName());
        TextView tvSeconds = (TextView) findViewById(mSecondsID);
        if (tvSeconds != null) {
            tvSeconds.setText(String.valueOf(seconds));
        }
        int mMillisID = getResources().getIdentifier("tvMillis", "id", getPackageName());
        TextView tvMillis = (TextView) findViewById(mMillisID);
        if (tvMillis != null) {
            tvMillis.setText(String.valueOf(millis));
        }
        int mNewTimeID = getResources().getIdentifier("etNewTime", "id", getPackageName());
        EditText etNewTime = (EditText) findViewById(mNewTimeID);
        if (etNewTime != null) {
            etNewTime.setText(String.valueOf(newTime));
        }

    }

    public void btDaysMinus10_onClick(View view) {
        addToNewTime(-10, 0, 0, 0, 0);
    }

    public void btDaysMinus1_onClick(View view) {
        addToNewTime(-1, 0, 0, 0, 0);
    }

    public void btDaysPlus1_onClick(View view) {
        addToNewTime(1, 0, 0, 0, 0);
    }

    public void btDaysPlus10_onClick(View view) {
        addToNewTime(10, 0, 0, 0, 0);
    }

    public void btHoursMinus10_onClick(View view) {
        addToNewTime(0, -10, 0, 0, 0);
    }

    public void btHoursMinus1_onClick(View view) {
        addToNewTime(0, -1, 0, 0, 0);
    }

    public void btHoursPlus1_onClick(View view) {
        addToNewTime(0, 1, 0, 0, 0);
    }

    public void btHoursPlus10_onClick(View view) {
        addToNewTime(0, 10, 0, 0, 0);
    }

    public void btMinutesMinus10_onClick(View view) {
        addToNewTime(0, 0, -10, 0, 0);
    }

    public void btMinutesMinus1_onClick(View view) {
        addToNewTime(0, 0, -1, 0, 0);
    }

    public void btMinutesPlus1_onClick(View view) {
        addToNewTime(0, 0, 1, 0, 0);
    }

    public void btMinutesPlus10_onClick(View view) {
        addToNewTime(0, 0, 10, 0, 0);
    }

    public void btSecondsMinus10_onClick(View view) {
        addToNewTime(0, 0, 0, -10, 0);
    }

    public void btSecondsMinus1_onClick(View view) {
        addToNewTime(0, 0, 0, -1, 0);
    }

    public void btSecondsPlus1_onClick(View view) {
        addToNewTime(0, 0, 0, 1, 0);
    }

    public void btSecondsPlus10_onClick(View view) {
        addToNewTime(0, 0, 0, 10, 0);
    }

    public void btMillisMinus10_onClick(View view) {
        addToNewTime(0, 0, 0, 0, -10);
    }

    public void btMillisMinus1_onClick(View view) {
        addToNewTime(0, 0, 0, 0, -1);
    }

    public void btMillisPlus1_onClick(View view) {
        addToNewTime(0, 0, 0, 0, 1);
    }

    public void btMillisPlus10_onClick(View view) {
        addToNewTime(0, 0, 0, 0, 10);
    }

    private void addToNewTime(long days, long hours, long minutes, long seconds, long millis) {
        newTime += millis
                + 1000 * seconds
                + 1000 * 60 * minutes
                + 1000 * 60 * 60 * hours
                + 1000 * 60 * 60 * 24 * days;
        if (newTime < 0) {
            newTime = 0;
        }
        parseTime();
        updateScreen();
    }

    public void btClose_onClick(View view) {
        blink(view,this);
        Class<?> myClass = null;
        try {
            myClass = Class.forName(getPackageName() + ".activities." + mCallerActivity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(ActivityDateTimePickerDialog.this, myClass);
        intent.putExtra("isNew", mCallerIsNew);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btSave_onClick(View view) {

        blink(view,this);
        Class<?> myClass = null;
        try {
            myClass = Class.forName(getPackageName() + ".activities." + mCallerActivity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(ActivityDateTimePickerDialog.this, myClass);
        intent.putExtra("isNew", mCallerIsNew);
        intent.putExtra("millis", newTime);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void etNewTime_onClick(View view) {

        int etNewTimeID = getResources().getIdentifier("etNewTime", "id", getPackageName());
        EditText etNewTime = (EditText) findViewById(etNewTimeID);
        if (etNewTime != null) {
            try {
                newTime = Long.parseLong(String.valueOf(etNewTime.getText()));
            } catch (NumberFormatException e) {
                newTime = Long.MAX_VALUE;
            }
        }
        parseTime();
        updateScreen();

    }
}
