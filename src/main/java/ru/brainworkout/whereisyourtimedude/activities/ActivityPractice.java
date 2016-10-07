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

import ru.brainworkout.whereisyourtimedude.R;
import ru.brainworkout.whereisyourtimedude.common.ConnectionParameters;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.Project;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

import static ru.brainworkout.whereisyourtimedude.common.Common.blink;
import static ru.brainworkout.whereisyourtimedude.common.Common.setTitleOfActivity;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionCurrentPractice;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionOpenActivities;


public class ActivityPractice extends AppCompatActivity {

    private final DatabaseManager DB = new DatabaseManager(this);
    private boolean isNew;
    ConnectionParameters params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        Intent intent = getIntent();
        getIntentParams(intent);

        if (isNew) {
            if (sessionCurrentPractice == null) {
                sessionCurrentPractice = new Practice.Builder(DB.getPracticeMaxNumber() + 1).build();
            }
        } else {
            int id = intent.getIntExtra("CurrentPracticeID", 0);
            try {
                sessionCurrentPractice = DB.getPractice(id);
            } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
                tableDoesNotContainElementException.printStackTrace();
            }
        }

        showPracticeOnScreen();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setTitleOfActivity(this);
    }

    private void getIntentParams(Intent intent) {
        if (!sessionOpenActivities.empty()) {
            params = sessionOpenActivities.peek();
        }
        isNew = (params != null ? params.isReceiverNew() : false);
    }

    private void showPracticeOnScreen() {


        int mIsActiveID = getResources().getIdentifier("cbIsActive", "id", getPackageName());
        CheckBox cbIsActive = (CheckBox) findViewById(mIsActiveID);
        if (cbIsActive != null) {
            if (sessionCurrentPractice.getIsActive() != 0) {
                cbIsActive.setChecked(true);
            } else {
                cbIsActive.setChecked(false);
            }
            cbIsActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (sessionCurrentPractice != null) {
                        if (isChecked) {
                            sessionCurrentPractice.setIsActive(1);
                        } else {
                            sessionCurrentPractice.setIsActive(0);
                        }

                    }
                }
            });

        }


        //ID
        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            tvID.setText(String.valueOf(sessionCurrentPractice.getID()));
        }

        //Имя
        int mNameID = getResources().getIdentifier("etName", "id", getPackageName());
        EditText etName = (EditText) findViewById(mNameID);
        if (etName != null) {
            etName.setText(sessionCurrentPractice.getName());
        }

        //ID
        int mProject = getResources().getIdentifier("tvProject", "id", getPackageName());
        TextView tvProject = (TextView) findViewById(mProject);
        if (tvProject != null) {

            String nameProject = "";
            try {
                Project project = DB.getProject(sessionCurrentPractice.getIdProject());
                nameProject = project.getName();

            } catch (TableDoesNotContainElementException e) {

            }
            tvProject.setText(nameProject);
        }

    }

    public void btClose_onClick(final View view) {

        blink(view,this);
        Intent intent = new Intent(getApplicationContext(), ActivityPracticesList.class);
        intent.putExtra("CurrentPracticeID", sessionCurrentPractice.getID());
        sessionOpenActivities.pop();
        sessionCurrentPractice = null;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }


    private void getPropertiesFromScreen() {

        //Имя
        int mNameID = getResources().getIdentifier("etName", "id", getPackageName());
        EditText etName = (EditText) findViewById(mNameID);
        if (etName != null) {

            sessionCurrentPractice.setName(String.valueOf(etName.getText()));

        }

    }

    public void tvProject_onClick(View view) {

        blink(view,this);
        getPropertiesFromScreen();
        int id_project = sessionCurrentPractice.getIdProject();

        Intent intent = new Intent(getApplicationContext(), ActivityProjectsList.class);
        Boolean isNew = params!=null?params.isReceiverNew():false;
        ConnectionParameters paramsNew= new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityPractice")
                .isTransmitterNew(isNew)
                .isTransmitterForChoice(false)
                .addReceiverActivityName("ActivityProjectsList")
                .isReceiverNew(false)
                .isReceiverForChoice(true)
                .build();
        sessionOpenActivities.push(paramsNew);
        intent.putExtra("CurrentProjectID", id_project);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void btSave_onClick(final View view) {

        blink(view,this);
        getPropertiesFromScreen();

        sessionCurrentPractice.dbSave(DB);

        Intent intent = new Intent(getApplicationContext(), ActivityPracticesList.class);
        intent.putExtra("CurrentPracticeID", sessionCurrentPractice.getID());
        sessionOpenActivities.pop();
        sessionCurrentPractice = null;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);

        if (params != null) {
                intent = new Intent(getApplicationContext(), ActivityPracticesList.class);
                sessionOpenActivities.pop();
                intent.putExtra("CurrentPracticeID", sessionCurrentPractice.getID());
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public void btDelete_onClick(final View view) {
        blink(view,this);


        new AlertDialog.Builder(this)
                .setMessage("Вы действительно хотите удалить текущее занятие и его историю?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DB.deleteAllPracticeHistoryOfPractice(sessionCurrentPractice.getID());

                        sessionCurrentPractice.dbDelete(DB);
                        sessionCurrentPractice = null;

                        Intent intent = new Intent(getApplicationContext(), ActivityPracticesList.class);
                        sessionOpenActivities.pop();
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                }).setNegativeButton("Нет", null).show();

    }


}
