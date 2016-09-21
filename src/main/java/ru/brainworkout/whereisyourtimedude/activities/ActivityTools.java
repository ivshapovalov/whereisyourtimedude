package ru.brainworkout.whereisyourtimedude.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ru.brainworkout.whereisyourtimedude.R;
import ru.brainworkout.whereisyourtimedude.common.Common;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;
import static ru.brainworkout.whereisyourtimedude.common.Common.setTitleOfActivity;


public class ActivityTools extends AppCompatActivity {

    private static final int MAX_VERTICAL_BUTTON_COUNT = 10;
    private final DatabaseManager DB = new DatabaseManager(this);

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

    public void btExportImport_onClick(final View view) {

//        Intent intent = new Intent(ActivityTools.this, ActivityFileExportImport.class);
//        startActivity(intent);

    }

    public void btAbout_onClick(final View view) {

        Intent intent = new Intent(ActivityTools.this, ActivityAbout.class);
        startActivity(intent);

    }

    public void btTestFill_onClick(final View view) {

        Common.DefaultTestFilling(DB);

    }

    public void btOptions_onClick(final View view) {

        Intent intent = new Intent(ActivityTools.this, ActivityOptions.class);
        startActivity(intent);

    }

    public void btClearBD_onClick(final View view) {

        new AlertDialog.Builder(this)
                .setMessage("Вы действительно хотите очистить базу данных?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {

                            SQLiteDatabase dbSQL = DB.getWritableDatabase();
                            //DB.DeleteDB(dbSQL);
                            DB.onUpgrade(dbSQL, 1, 2);



                        } catch (Exception e) {
                            Toast toast = Toast.makeText(ActivityTools.this,
                                    "Невозможно подключиться к базе данных!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }).setNegativeButton("Нет", null).show();

    }

    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }


}
