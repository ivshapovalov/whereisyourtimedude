package ru.brainworkout.whereisyourtimedude.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.brainworkout.whereisyourtimedude.R;
import static ru.brainworkout.whereisyourtimedude.common.Common.*;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionUser;

import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.User;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;


public class ActivityMain extends AppCompatActivity {

    private static final int MAX_VERTICAL_BUTTON_COUNT = 10;
    private final DatabaseManager DB = new DatabaseManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showElementsOnScreen();

        defineCurrentUser();
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

    private void defineCurrentUser() {

        if (sessionUser == null) {
            List<User> userList = DB.getAllUsers();
            if (userList.size() == 1) {
                User currentUser=userList.get(0);
                sessionUser = currentUser;
                currentUser.setIsCurrentUser(1);
                currentUser.dbSave(DB);
            } else {
                //ищем активного
                for (User user:userList
                        ) {
                    if (user.isCurrentUser()==1) {
                        sessionUser =user;
                        break;
                    }
                }
                isUserDefined();
            }
        }
    }

    public void btUsers_onClick(final View view) {

        Intent intent = new Intent(ActivityMain.this, ActivityUsersList.class);
        startActivity(intent);

    }

    public void btAreas_onClick(final View view) {

        Intent intent = new Intent(ActivityMain.this, ActivityAreasList.class);
        startActivity(intent);

    }

    public void btProjects_onClick(final View view) {

        Intent intent = new Intent(ActivityMain.this, ActivityProjectsList.class);
        startActivity(intent);

    }

    public void btPractices_onClick(final View view) {

        Intent intent = new Intent(ActivityMain.this, ActivityPracticesList.class);
        startActivity(intent);

    }

    public void btPracticeHistory_onClick(final View view) {

        Intent intent = new Intent(ActivityMain.this, ActivityPracticeHistoryList.class);
        startActivity(intent);

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

    private boolean isUserDefined() {
        if (sessionUser ==null) {
            Toast toast = Toast.makeText(ActivityMain.this,
                    "Не выбран пользатель. Создайте пользователя и сделайте его активным!", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }

    private boolean isDBNotEmpty() {

        List<Practice> list=new ArrayList<Practice>();
        if (sessionUser == null) {
            //list = DB.getAllActiveExercises();
        } else {
            list = DB.getAllActivePracticesOfUser(sessionUser.getID());
        }
        if (list.size() == 0) {
            Toast toast = Toast.makeText(ActivityMain.this,
                    "Отсутствуют активные занятия. Заполните список занятий!", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        } else {
            return true;

        }
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
