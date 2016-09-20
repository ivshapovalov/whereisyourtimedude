package ru.brainworkout.whereisyourtimedude.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import ru.brainworkout.whereisyourtimedude.R;
import ru.brainworkout.whereisyourtimedude.common.Common;

import static ru.brainworkout.whereisyourtimedude.common.Common.blink;
import static ru.brainworkout.whereisyourtimedude.common.Common.setTitleOfActivity;


public class ActivityOptions extends AppCompatActivity {

    private SharedPreferences mSettings;
    private int mSaveInterval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        getPreferencesFromFile();
        setPreferencesOnScreen();

        setTitleOfActivity(this);
    }


    public void buttonSave_onClick(View view) {

        int mSaveInterval = getResources().getIdentifier("etSaveInterval", "id", getPackageName());
        EditText txt = (EditText) findViewById(mSaveInterval);
        if (txt != null) {
            try {
                mSaveInterval = Integer.valueOf(txt.getText().toString());
            } catch (ClassCastException e) {

            }
        }
        blink(view,this);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(ActivityMain.APP_PREFERENCES_SAVE_INTERVAL, mSaveInterval);
        Common.SAVE_INTERVAL = mSaveInterval;

        editor.apply();

        this.finish();

    }

    public void buttonCancel_onClick(final View view) {

        blink(view,this);
        this.finish();

    }

    private void getPreferencesFromFile() {
        mSettings = getSharedPreferences(ActivityMain.APP_PREFERENCES, Context.MODE_PRIVATE);

        if (mSettings.contains(ActivityMain.APP_PREFERENCES_SAVE_INTERVAL)) {
            mSaveInterval = mSettings.getInt(ActivityMain.APP_PREFERENCES_SAVE_INTERVAL, 10);
        } else {
            mSaveInterval = 10;
        }


    }

    private void setPreferencesOnScreen() {

        int idSaveInterval = getResources().getIdentifier("etSaveInterval", "id", getPackageName());
        EditText txt = (EditText) findViewById(idSaveInterval);
        if (txt != null) {
            txt.setText(String.valueOf(mSaveInterval));
        }



    }
}
