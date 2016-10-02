package ru.brainworkout.whereisyourtimedude.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import ru.brainworkout.whereisyourtimedude.R;
import ru.brainworkout.whereisyourtimedude.common.Session;
import ru.brainworkout.whereisyourtimedude.database.entities.User;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

import static ru.brainworkout.whereisyourtimedude.common.Common.blink;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionCurrentUser;
import static ru.brainworkout.whereisyourtimedude.common.Common.setTitleOfActivity;

public class ActivityUser extends AppCompatActivity {

    private User mCurrentUser;
    private final DatabaseManager DB = new DatabaseManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Intent intent = getIntent();
        boolean isNew = intent.getBooleanExtra("isNew", false);

        if (isNew) {
            mCurrentUser = new User.Builder(DB.getUserMaxNumber() + 1).build();
        } else {
            int id = intent.getIntExtra("id", 0);
            try {
                mCurrentUser = DB.getUser(id);
            } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
                tableDoesNotContainElementException.printStackTrace();
            }
        }

        showUserOnScreen();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setTitleOfActivity(this);
    }


    private void showUserOnScreen() {

        int isCurrentID = getResources().getIdentifier("cbIsCurrent", "id", getPackageName());
        CheckBox cbIsCurrent = (CheckBox) findViewById(isCurrentID);
        if (cbIsCurrent != null) {
            if (mCurrentUser.isCurrentUser() != 0) {
                cbIsCurrent.setChecked(true);
            } else {
                cbIsCurrent.setChecked(false);
            }
        }

        cbIsCurrent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (mCurrentUser != null) {
                    if (isChecked) {
                        mCurrentUser.setIsCurrentUser(1);
                    } else {
                        mCurrentUser.setIsCurrentUser(0);
                    }

                }
            }
        });


        //ID
        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            tvID.setText(String.valueOf(mCurrentUser.getID()));
        }

        //Имя
        int mNameID = getResources().getIdentifier("etName", "id", getPackageName());
        EditText etName = (EditText) findViewById(mNameID);
        if (etName != null) {
            etName.setText(mCurrentUser.getName());
        }

    }

    public void btClose_onClick(final View view) {

        blink(view, this);
        Intent intent = new Intent(getApplicationContext(), ActivityUsersList.class);
        intent.putExtra("id", mCurrentUser.getID());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }


    private void getPropertiesFromScreen() {

        //Имя
        int mNameID = getResources().getIdentifier("etName", "id", getPackageName());
        EditText etName = (EditText) findViewById(mNameID);
        if (etName != null) {

            mCurrentUser.setName(String.valueOf(etName.getText()));

        }

    }

    public void btSave_onClick(final View view) {

        blink(view, this);
        getPropertiesFromScreen();

        if (mCurrentUser.isCurrentUser() == 1) {
            if (Session.sessionBackgroundChronometer.isTicking()) {
                Toast toast = Toast.makeText(ActivityUser.this,
                        "Вернитесь в хронометраж и остановите счетчик прежде чем поменять активного" +
                                "пользователя", Toast.LENGTH_SHORT);
                toast.show();
                return;
            }
        }
            mCurrentUser.dbSave(DB);
            setDBCurrentUser();

            Intent intent = new Intent(getApplicationContext(), ActivityUsersList.class);
            intent.putExtra("id", mCurrentUser.getID());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);


    }

    private void setDBCurrentUser() {

        if (mCurrentUser.isCurrentUser() == 1) {
            sessionCurrentUser = mCurrentUser;
            List<User> userList = DB.getAllUsers();

            for (User user : userList) {

                if (user.getID() != mCurrentUser.getID()) {
                    user.setIsCurrentUser(0);
                    user.dbSave(DB);
                }
            }
        } else {
            if (sessionCurrentUser != null && sessionCurrentUser.equals(mCurrentUser)) {
                sessionCurrentUser = null;
            }
        }


    }

    public void btDelete_onClick(final View view) {

        blink(view, this);

        new AlertDialog.Builder(this)
                .setMessage("Вы действительно хотите удалить текущего пользователя, его занятия, проекты и области?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        DB.deleteAllPracticeHistoryOfUser(sessionCurrentUser.getID());

                        DB.deleteAllPracticesOfUser(sessionCurrentUser.getID());

                        DB.deleteAllProjectsOfUser(sessionCurrentUser.getID());

                        DB.deleteAllAreasOfUser(sessionCurrentUser.getID());

                        mCurrentUser.dbDelete(DB);

                        if (mCurrentUser.equals(sessionCurrentUser)) {
                            List<User> userList = DB.getAllUsers();
                            if (userList.size() == 1) {
                                User currentUser = userList.get(0);
                                sessionCurrentUser = currentUser;
                                currentUser.setIsCurrentUser(1);
                                currentUser.dbSave(DB);
                            }
                        }

                        Intent intent = new Intent(getApplicationContext(), ActivityUsersList.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                }).setNegativeButton("Нет", null).show();

    }


}