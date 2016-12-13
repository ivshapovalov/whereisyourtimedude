package ru.brainworkout.whereisyourtimedude.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import ru.brainworkout.whereisyourtimedude.R;
import ru.brainworkout.whereisyourtimedude.common.Common;
import ru.brainworkout.whereisyourtimedude.common.Session;

import static ru.brainworkout.whereisyourtimedude.common.Common.setTitleOfActivity;
import static ru.brainworkout.whereisyourtimedude.common.Session.*;

public class ActivityTools extends AbstractActivity {

    private static final int MAX_VERTICAL_BUTTON_COUNT = 10;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tools);

        showElementsOnScreen();
        setTitleOfActivity(this);
    }

    private void showElementsOnScreen() {

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
        int mHeight = displaymetrics.heightPixels / MAX_VERTICAL_BUTTON_COUNT;
        for (int i = 0; i <= MAX_VERTICAL_BUTTON_COUNT; i++) {
            int btID = getResources().getIdentifier("btMain" + String.valueOf(i), "id", getPackageName());
            Button btName = (Button) findViewById(btID);
            if (btName != null) {
                btName.setHeight(mHeight);
            }
        }
    }

    public void btAbout_onClick(final View view) {
        Intent intent = new Intent(ActivityTools.this, ActivityAbout.class);
        startActivity(intent);
    }

    public void btTestFill_onClick(final View view) {
        if (backgroundChronometerIsWorking()) return;
        new AlertDialog.Builder(this)
                .setMessage("Do you want to clear the database and fill it by test data?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {

                            SQLiteDatabase db = DB.getWritableDatabase();
                            DB.ClearDB(db);
                            sessionCurrentUser = null;

                            Common.defaultTestFilling(DB);
                            db.close();

                            if (Session.sessionBackgroundChronometer != null && Session.sessionBackgroundChronometer.getService() != null) {
                                sessionBackgroundChronometer.getService().stopForeground(true);
                                sessionBackgroundChronometer.getService().stopSelf();
                            }
                            Toast toast = Toast.makeText(ActivityTools.this,
                                    "Database cleared and filled by test data!", Toast.LENGTH_SHORT);
                            toast.show();
                            setTitleOfActivity(ActivityTools.this);

                        } catch (Exception e) {
                            Toast toast = Toast.makeText(ActivityTools.this,
                                    "Unable to connect to database!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }).setNegativeButton("No", null).show();
    }

    public void btOptions_onClick(final View view) {
        if (isUserDefined()) {
            Intent intent = new Intent(ActivityTools.this, ActivityOptions.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    public void btClearBD_onClick(final View view) {

        if (backgroundChronometerIsWorking()) return;

        new AlertDialog.Builder(this)
                .setMessage("Do you want to clear the database?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {

                            SQLiteDatabase db = DB.getWritableDatabase();
                            DB.ClearDB(db);
                            sessionCurrentUser = null;
                            db.close();

                            if (Session.sessionBackgroundChronometer != null) {
                                sessionBackgroundChronometer.setCurrentPracticeHistory(null);
                                sessionBackgroundChronometer.setCurrentDetailedPracticeHistory(null);
                                if (Session.sessionBackgroundChronometer.getService()!=null){
                                    sessionBackgroundChronometer.getService().stopForeground(true);
                                    sessionBackgroundChronometer.getService().stopSelf();
                                }
                            }

                            Toast toast = Toast.makeText(ActivityTools.this,
                                    "Database cleared!", Toast.LENGTH_SHORT);
                            toast.show();
                            setTitleOfActivity(ActivityTools.this);
                        } catch (Exception e) {
                            Toast toast = Toast.makeText(ActivityTools.this,
                                    "Unable to connect to database!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }).setNegativeButton("No", null).show();
    }

    private boolean backgroundChronometerIsWorking() {
        if (sessionBackgroundChronometer != null && sessionBackgroundChronometer.isTicking()) {
            Toast toast = Toast.makeText(ActivityTools.this,
                    "Stop the chronometer. Unable to clear the database when chronometer is working!", Toast.LENGTH_SHORT);
            toast.show();
            return true;
        }
        return false;
    }

    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btExportImport_onClick(View view) {
        Intent intent = new Intent(ActivityTools.this, ActivityFileExportImport.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}
