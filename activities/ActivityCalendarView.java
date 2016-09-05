package ru.brainworkout.whereisyourtimedude.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CalendarView;

import java.util.Calendar;
import java.util.Date;

import ru.brainworkout.whereisyourtimedude.R;
import static ru.brainworkout.whereisyourtimedude.common.Common.*;

public class ActivityCalendarView extends AppCompatActivity {

    private boolean mIsBeginDate;

    private String mOldDateFrom;
    private String mNewDate;
    private String mOldDateTo;

    private int mCallerPracticeID;
    private String  mCallerDate;
    private String mCallerActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_view);

        getIntentParams();

        SetParametersOnScreen();

    }


    private void getIntentParams() {

        Intent intent = getIntent();

        mIsBeginDate = intent.getBooleanExtra("IsBeginDate", true);
        mCallerActivity = intent.getStringExtra("CurrentActivity");
        mCallerPracticeID = intent.getIntExtra("CurrentPracticeID", 0);

        try {
            mOldDateFrom = intent.getStringExtra("CurrentDate");
        } catch (Exception e) {
            mOldDateFrom = "";
        }
        try {
            mOldDateTo = intent.getStringExtra("CurrentDateTo");
        } catch (Exception e) {
            mOldDateTo = "";
        }

    }
    private void SetParametersOnScreen() {

        Calendar calendar = Calendar.getInstance();


        if (mIsBeginDate ) {
            if (mOldDateFrom != null && !"".equals(mOldDateFrom)) {
                Date d = ConvertStringToDate(mOldDateFrom,DATE_FORMAT_STRING);
                calendar.set(d.getYear() + 1900, d.getMonth(), d.getDate());
                mNewDate = mOldDateFrom;
            }

        } else {
            if (mOldDateTo != null && !"".equals(mOldDateTo)) {
                Date d = ConvertStringToDate(mOldDateTo,DATE_FORMAT_STRING);
                calendar.set(d.getYear() + 1900, d.getMonth(), d.getDate());
                mNewDate = mOldDateTo;
            }
        }


        if (mNewDate==null || mNewDate.equals("")) {
            mNewDate=ConvertDateToString(ConvertStringToDate(new StringBuilder().append(calendar.getTime().getYear()+1900)
                    .append("-").append(calendar.getTime().getMonth() + 1).append("-").append(calendar.getTime().getDate())
                    .append("").toString(), DATE_FORMAT_STRING), DATE_FORMAT_STRING);

        }
        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        calendarView.setDate(calendar.getTimeInMillis(), true, false);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {

            @Override
            public void onSelectedDayChange(CalendarView view, int year,
                                            int month, int dayOfMonth) {

                mNewDate = ConvertDateToString(ConvertStringToDate(new StringBuilder().append(year)
                        .append("-").append(month + 1).append("-").append(dayOfMonth)
                        .append("").toString(),DATE_FORMAT_STRING),DATE_FORMAT_STRING);
            }
        });

    }




    public void btSave_onClick(final View view) {

        blink(view);
        Class<?> myClass = null;
        try {
            myClass = Class.forName(getPackageName() + ".activities." + mCallerActivity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(ActivityCalendarView.this, myClass);
        intent.putExtra("CurrentPracticeID", mCallerPracticeID);

        if (mIsBeginDate) {
            intent.putExtra("CurrentDate", mNewDate);
            intent.putExtra("CurrentDateTo", mOldDateTo);
        } else {
            intent.putExtra("CurrentDate", mOldDateFrom);
            intent.putExtra("CurrentDateTo", mNewDate);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void btClose_onClick(final View view) {

        blink(view);
        Class<?> myClass = null;
        try {
            myClass = Class.forName(getPackageName() + ".activities." + mCallerActivity);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(ActivityCalendarView.this, myClass);

        intent.putExtra("IsBeginDate", mIsBeginDate);
        intent.putExtra("CurrentPracticeID", mCallerPracticeID);
        intent.putExtra("CurrentDate", mOldDateFrom);
        intent.putExtra("CurrentDateTo", mOldDateTo);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}
