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
import ru.brainworkout.whereisyourtimedude.common.Session;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;
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

        Toast toast = Toast.makeText(ActivityTools.this,
                "База данных заполнена тестовыми данными!", Toast.LENGTH_SHORT);
        toast.show();
        setTitleOfActivity(this);

    }

    public void btOptions_onClick(final View view) {

        Intent intent = new Intent(ActivityTools.this, ActivityOptions.class);
        startActivity(intent);

    }

    public void btClearBD_onClick(final View view) {

        if (sessionBackgroundChronometer!=null && sessionBackgroundChronometer.isTicking()) {
            Toast toast = Toast.makeText(ActivityTools.this,
                    "Остановите хронометраж. Нельзя очистить базу данных при работающем хронометраже!", Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage("Вы действительно хотите очистить базу данных?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        try {

                            SQLiteDatabase dbSQL = DB.getWritableDatabase();
                            //DB.DeleteDB(dbSQL);
                            DB.onUpgrade(dbSQL, 1, 2);

                            Toast toast = Toast.makeText(ActivityTools.this,
                                    "База данных очищена!", Toast.LENGTH_SHORT);
                            toast.show();
                            setTitleOfActivity(ActivityTools.this);



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