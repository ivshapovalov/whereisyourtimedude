package ru.brainworkout.whereisyourtimedude.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import ru.brainworkout.whereisyourtimedude.R;
import ru.brainworkout.whereisyourtimedude.common.BackgroundChronometerService;
import ru.brainworkout.whereisyourtimedude.common.Constants;
import ru.brainworkout.whereisyourtimedude.common.Session;
import ru.brainworkout.whereisyourtimedude.database.entities.Options;

import static ru.brainworkout.whereisyourtimedude.common.Common.blink;
import static ru.brainworkout.whereisyourtimedude.common.Common.setTitleOfActivity;


public class ActivityOptions extends AbstractActivity {

    private Options options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        getPreferencesFromDB();
        setPreferencesOnScreen();
        setTitleOfActivity(this);
    }


    public void buttonSave_onClick(View view) {

        int mSaveInterval = getResources().getIdentifier("etSaveInterval", "id", getPackageName());
        EditText txt = (EditText) findViewById(mSaveInterval);
        if (txt != null) {
            try {
                options.setSaveInterval(Integer.valueOf(txt.getText().toString()));
            } catch (ClassCastException e) {

            }
        }
        blink(view, this);
        options.dbSave(DB);
        Session.sessionOptions = options;
        Session.saveInterval = options.getSaveInterval();
        if (options.getDisplayNotificationTimerSwitch() == 0) {
            if (Session.sessionBackgroundChronometer != null && Session.sessionBackgroundChronometer.getService() != null) {
//                NotificationManager mNotificationManager = (NotificationManager) Session.sessionBackgroundChronometer.getService().getSystemService(Context.NOTIFICATION_SERVICE);
//                mNotificationManager.cancel(Session.SESSION_NOTIFICATION_ID);
//                Session.sessionBackgroundChronometer.getService()
//                        .stopForeground(true);
                Session.sessionBackgroundChronometer.freezeNotification();


            }
        } else {
            if (Session.sessionBackgroundChronometer != null) {
                if (Session.sessionBackgroundChronometer.isTicking()) {
                    if (Session.sessionBackgroundChronometer.getService() != null) {
                        Session.sessionBackgroundChronometer.getService()
                                .startForeground(Session.SESSION_NOTIFICATION_ID, Session.sessionBackgroundChronometer.getCurrentNotification(Constants.ACTION.PLAY_ACTION));
                    } else {
                        Intent backgroundServiceIntent = new Intent(this, BackgroundChronometerService.class);
                        backgroundServiceIntent.setAction(Constants.ACTION.PLAY_ACTION);
                        startService(backgroundServiceIntent);
                    }
                }
            }
        }

        this.finish();

    }

    public void buttonCancel_onClick(final View view) {

        blink(view, this);
        this.finish();

    }

    private void getPreferencesFromDB() {
        options = DB.getOptionsOfUser(Session.sessionCurrentUser.getID());
    }

    private void setPreferencesOnScreen() {

        int mRecoveryID = getResources().getIdentifier("rbRecoveryOnRunSwitch" + (options.getRecoveryOnRunSwitch() == 1 ? "On" : "Off"), "id", getPackageName());
        RadioButton but = (RadioButton) findViewById(mRecoveryID);
        if (but != null) {
            but.setChecked(true);
        }
        RadioGroup radiogroup = (RadioGroup) findViewById(R.id.rgRecoveryOnRunSwitch);

        if (radiogroup != null) {
            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case -1:
                            break;
                        case R.id.rbRecoveryOnRunSwitchOn:
                            options.setRecoveryOnRunSwitch(1);
                            break;
                        case R.id.rbRecoveryOnRunSwitchOff:
                            options.setRecoveryOnRunSwitch(0);
                            break;
                        default:
                            options.setRecoveryOnRunSwitch(0);
                            break;
                    }
                }
            });
        }

        int mDisplayForegroundID = getResources().getIdentifier("rbDisplayNotificationTimer"
                + (options.getDisplayNotificationTimerSwitch() == 1 ? "On" : "Off"), "id", getPackageName());
        but = (RadioButton) findViewById(mDisplayForegroundID);
        if (but != null) {
            but.setChecked(true);
        }
        radiogroup = (RadioGroup) findViewById(R.id.rgDisplayNotificationTimer);

        if (radiogroup != null) {
            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case -1:
                            break;
                        case R.id.rbDisplayNotificationTimerOn:
                            options.setDisplayNotificationTimerSwitch(1);
                            break;
                        case R.id.rbDisplayNotificationTimerOff:
                            options.setDisplayNotificationTimerSwitch(0);
                            break;
                        default:
                            options.setDisplayNotificationTimerSwitch(0);
                            break;
                    }
                }
            });
        }

        int idSaveInterval = getResources().getIdentifier("etSaveInterval", "id", getPackageName());
        EditText txt = (EditText) findViewById(idSaveInterval);
        if (txt != null) {
            txt.setText(String.valueOf(options.getSaveInterval()));
        }

    }
}
