package ru.brainworkout.whereisyourtimedude.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import ru.brainworkout.whereisyourtimedude.R;
import ru.brainworkout.whereisyourtimedude.common.Common;
import ru.brainworkout.whereisyourtimedude.common.Session;
import ru.brainworkout.whereisyourtimedude.database.entities.Options;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;

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
        if (options.getDisplaySwitch() == 0) {
            if (Session.sessionBackgroundChronometer != null && Session.sessionBackgroundChronometer.getService() != null) {
//                NotificationManager mNotificationManager = (NotificationManager) Session.sessionBackgroundChronometer.getService().getSystemService(Context.NOTIFICATION_SERVICE);
//                mNotificationManager.cancel(Session.SESSION_NOTIFICATION_ID);
                Session.sessionBackgroundChronometer.getService()
                        .stopForeground(true);


            }
        } else {
            if (Session.sessionBackgroundChronometer != null && Session.sessionBackgroundChronometer.getService() != null) {
                if (Session.sessionBackgroundChronometer.isTicking()) {
                    Session.sessionBackgroundChronometer.getService()
                            .startForeground(Session.SESSION_NOTIFICATION_ID, Session.sessionBackgroundChronometer.getCurrentNotification(Common.SYMBOL_PLAY));
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

        int mRecoveryID = getResources().getIdentifier("rbRecoverySwitch" + (options.getRecoverySwitch() == 1 ? "On" : "Off"), "id", getPackageName());
        RadioButton but = (RadioButton) findViewById(mRecoveryID);
        if (but != null) {
            but.setChecked(true);
        }
        RadioGroup radiogroup = (RadioGroup) findViewById(R.id.rgRecoverySwitch);

        if (radiogroup != null) {
            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case -1:
                            break;
                        case R.id.rbRecoverySwitchOn:
                            options.setRecoverySwitch(1);
                            break;
                        case R.id.rbRecoverySwitchOff:
                            options.setRecoverySwitch(0);
                            break;
                        default:
                            options.setRecoverySwitch(0);
                            break;
                    }
                }
            });
        }

        int mDisplayForegroundID = getResources().getIdentifier("rbDisplayForeground" + (options.getDisplaySwitch() == 1 ? "On" : "Off"), "id", getPackageName());
        but = (RadioButton) findViewById(mDisplayForegroundID);
        if (but != null) {
            but.setChecked(true);
        }
        radiogroup = (RadioGroup) findViewById(R.id.rgDisplayForeground);

        if (radiogroup != null) {
            radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case -1:
                            break;
                        case R.id.rbDisplayForegroundOn:
                            options.setDisplaySwitch(1);
                            break;
                        case R.id.rbDisplayForegroundOff:
                            options.setDisplaySwitch(0);
                            break;
                        default:
                            options.setDisplaySwitch(0);
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
