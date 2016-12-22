package ru.brainworkout.whereisyourtimedude.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import ru.brainworkout.whereisyourtimedude.R;
import ru.brainworkout.whereisyourtimedude.common.BackgroundChronometerService;
import ru.brainworkout.whereisyourtimedude.common.ConnectionParameters;
import ru.brainworkout.whereisyourtimedude.common.Constants;
import ru.brainworkout.whereisyourtimedude.common.Session;
import ru.brainworkout.whereisyourtimedude.database.entities.Options;

import static ru.brainworkout.whereisyourtimedude.common.Common.blink;
import static ru.brainworkout.whereisyourtimedude.common.Common.setTitleOfActivity;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionCurrentPractice;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionOpenActivities;


public class ActivityOptions extends AbstractActivity {
    private Options options;
    ConnectionParameters params;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        Intent intent = getIntent();
        getIntentParams(intent);

        getPreferencesFromDB();
        setPreferencesOnScreen();
        setTitleOfActivity(this);
    }

    private void getIntentParams(Intent intent) {
        if (!sessionOpenActivities.isEmpty()) {
            params = sessionOpenActivities.peek();
        }
    }

    public void buttonSave_onClick(View view) {
        blink(view, this);

        int saveIntervalID = getResources().getIdentifier("etSaveInterval", "id", getPackageName());
        EditText etSaveInterval = (EditText) findViewById(saveIntervalID);
        if (etSaveInterval != null) {
            try {
                options.setSaveInterval(Integer.valueOf(etSaveInterval.getText().toString()));
            } catch (ClassCastException e) {

            }
        }

        int rowNumberInListsID = getResources().getIdentifier("etRowNumberInLists", "id", getPackageName());
        EditText etRowNumberInLists = (EditText) findViewById(rowNumberInListsID);
        if (etRowNumberInLists != null) {
            options.setRowNumberInLists(Integer.valueOf(etRowNumberInLists.getText().toString()));
        }

        options.dbSave(DB);
        Session.sessionOptions = options;
        Session.saveInterval = options.getSaveInterval();
        if (options.getDisplayNotificationTimerSwitch() == 0) {
            if (Session.sessionBackgroundChronometer != null && Session.sessionBackgroundChronometer.getService() != null) {
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
        closeActivity();

    }

    public void buttonCancel_onClick(final View view) {
        blink(view, this);
        closeActivity();
    }

    private void closeActivity() {

        Intent intent = new Intent(getApplicationContext(), ActivityTools.class);
        if (params != null) {
            Class<?> myClass = null;
            try {
                myClass = Class.forName(getPackageName() + ".activities." + sessionOpenActivities.pollFirst().getTransmitterActivityName());
            } catch (ClassNotFoundException | NullPointerException e) {
                e.printStackTrace();
            }
            intent = new Intent(getApplicationContext(), myClass);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void getPreferencesFromDB() {
        options = DB.getOptionsOfUser(Session.sessionCurrentUser.getId());
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

        int rowNumberInListsID = getResources().getIdentifier("etRowNumberInLists", "id", getPackageName());
        EditText rowNumberInLists = (EditText) findViewById(rowNumberInListsID);
        if (rowNumberInLists != null) {
            rowNumberInLists.setText(String.valueOf(options.getRowNumberInLists()));
        }

    }
}
