package ru.brainworkout.whereisyourtimedude.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

import static ru.brainworkout.whereisyourtimedude.common.Common.blink;
import static ru.brainworkout.whereisyourtimedude.common.Common.setTitleOfActivity;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionCurrentPractice;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionOpenActivities;

public class ActivityPractice extends AbstractActivity {

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

            if (sessionCurrentPractice == null) {
                int id = intent.getIntExtra("CurrentPracticeID", 0);
                if (DB.containsPractice(id)) {
                    sessionCurrentPractice = DB.getPractice(id);
                } else {
                    throw new TableDoesNotContainElementException(String.format("Practice with id ='%s' does not exists in database", id));
                }
            }
        }

        showPracticeOnScreen();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setTitleOfActivity(this);
    }

    private void getIntentParams(Intent intent) {
        if (!sessionOpenActivities.isEmpty()) {
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

        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            tvID.setText(String.valueOf(sessionCurrentPractice.getId()));
        }

        int mNameID = getResources().getIdentifier("etName", "id", getPackageName());
        EditText etName = (EditText) findViewById(mNameID);
        if (etName != null) {
            etName.setText(sessionCurrentPractice.getName());
        }

        int mProject = getResources().getIdentifier("tvProject", "id", getPackageName());
        TextView tvProject = (TextView) findViewById(mProject);
        if (tvProject != null) {

            Project project = sessionCurrentPractice.getProject();
            String nameProject = "";
            if (project != null) {
                nameProject = project.getName();
            }
            tvProject.setText(nameProject);
        }

    }

    public void btClose_onClick(final View view) {
        blink(view, this);
        closeActivity();
    }

    private void closeActivity() {

        Intent intent = new Intent(getApplicationContext(), ActivityPracticesList.class);
        if (params != null) {
            Class<?> myClass = null;
            try {
                myClass = Class.forName(getPackageName() + ".activities." + sessionOpenActivities.pollFirst().getTransmitterActivityName());
            } catch (ClassNotFoundException | NullPointerException e) {
                e.printStackTrace();
            }
            intent = new Intent(getApplicationContext(), myClass);
        }
        intent.putExtra("CurrentPracticeID", sessionCurrentPractice.getId());
        sessionCurrentPractice = null;
        sessionOpenActivities.pollFirst();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void getPropertiesFromScreen() {

        int mNameID = getResources().getIdentifier("etName", "id", getPackageName());
        EditText etName = (EditText) findViewById(mNameID);
        if (etName != null) {
            sessionCurrentPractice.setName(String.valueOf(etName.getText()));
        }
    }

    public void tvProject_onClick(View view) {

        blink(view, this);
        getPropertiesFromScreen();

        Project project = sessionCurrentPractice.getProject();
        int id_project = 0;
        if (project != null) {
            id_project = sessionCurrentPractice.getProject().getId();
        }

        Intent intent = new Intent(getApplicationContext(), ActivityProjectsList.class);
        Boolean isNew = params != null ? params.isReceiverNew() : false;
        ConnectionParameters paramsNew = new ConnectionParameters.Builder()
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
        blink(view, this);
        getPropertiesFromScreen();
        sessionCurrentPractice.dbSave(DB);
        closeActivity();
    }

    public void onBackPressed() {
        closeActivity();
    }

    public void btDelete_onClick(final View view) {
        blink(view, this);
        new AlertDialog.Builder(this)
                .setMessage("Do you want to remove the current practice and its history?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sessionCurrentPractice.dbDelete(DB);
                       sessionCurrentPractice = null;
                        Intent intent = new Intent(getApplicationContext(), ActivityPracticesList.class);
                        sessionOpenActivities.pollFirst();
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                }).setNegativeButton("No", null).show();
    }
}
