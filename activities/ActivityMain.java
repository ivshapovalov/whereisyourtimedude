package ru.brainworkout.whereisyourtimedude.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;


public class ActivityMain extends AppCompatActivity {

    private final DatabaseManager DB = new DatabaseManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btChrono_onClick(final View view) {

        Intent intent = new Intent(ActivityMain.this, ActivityChrono.class);
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
                            Toast toast = Toast.makeText(ActivityMain.this,
                                    "Невозможно подключиться к базе данных!", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                }).setNegativeButton("Нет", null).show();

    }

    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setMessage("Вы действительно хотите покинуть программу?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                }).setNegativeButton("Нет", null).show();

    }
}
