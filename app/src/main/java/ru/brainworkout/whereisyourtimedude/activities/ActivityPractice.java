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
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionDetailedPracticeHistorySequence;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionOpenActivities;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionPracticeHistorySequence;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionPracticeSequence;

public class ActivityPractice extends AbstractActivity {
    private boolean isNew;
    ConnectionParameters params;
    private Practice currentPractice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        Intent intent = getIntent();
        getIntentParams(intent);

        if (!sessionPracticeSequence.isEmpty()) {
            currentPractice = sessionPracticeSequence.pollFirst();
        } else {
            if (isNew) {
                currentPractice = new Practice.Builder(DB).build();
            } else {
                int id = intent.getIntExtra("CurrentPracticeID", 0);
                if (DB.containsPractice(id)) {
                    currentPractice = DB.getPractice(id);
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
            if (currentPractice.getIsActive() != 0) {
                cbIsActive.setChecked(true);
            } else {
                cbIsActive.setChecked(false);
            }
            cbIsActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (currentPractice != null) {
                        if (isChecked) {
                            currentPractice.setIsActive(1);
                        } else {
                            currentPractice.setIsActive(0);
                        }

                    }
                }
            });
        }

        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            tvID.setText(String.valueOf(currentPractice.getId()));
        }

        int mNameID = getResources().getIdentifier("etName", "id", getPackageName());
        EditText etName = (EditText) findViewById(mNameID);
        if (etName != null) {
            etName.setText(currentPractice.getName());
        }

        int mProject = getResources().getIdentifier("tvProject", "id", getPackageName());
        TextView tvProject = (TextView) findViewById(mProject);
        if (tvProject != null) {

            Project project = currentPractice.getProject();
            String nameProject = "";
            if (project != null) {
                nameProject = project.getName();
            }
            tvProject.setText(nameProject);
        }
    }

    public void btClose_onClick(final View view) {
        blink(view, this);
        closeActivity(new Intent(getApplicationContext(), ActivityPracticeList.class));
    }

    private void closeActivity(Intent intent) {
        intent.putExtra("CurrentPracticeID", currentPractice.getId());
        sessionOpenActivities.pollFirst();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void getPropertiesFromScreen() {

        int mNameID = getResources().getIdentifier("etName", "id", getPackageName());
        EditText etName = (EditText) findViewById(mNameID);
        if (etName != null) {
            currentPractice.setName(String.valueOf(etName.getText()));
        }
    }

    public void tvProject_onClick(View view) {

        blink(view, this);
        getPropertiesFromScreen();

        Project project = currentPractice.getProject();
        int id_project = 0;
        if (project != null) {
            id_project = currentPractice.getProject().getId();
        }

        Intent intent = new Intent(getApplicationContext(), ActivityProjectList.class);
        Boolean isNew = params != null ? params.isReceiverNew() : false;
        ConnectionParameters paramsNew = new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityPractice")
                .isTransmitterNew(isNew)
                .isTransmitterForChoice(false)
                .addReceiverActivityName("ActivityProjectList")
                .isReceiverNew(false)
                .isReceiverForChoice(true)
                .build();
        sessionPracticeSequence.push(currentPractice);
        sessionOpenActivities.push(paramsNew);
        intent.putExtra("CurrentProjectID", id_project);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btSave_onClick(final View view) {
        blink(view, this);
        getPropertiesFromScreen();
        currentPractice.dbSave(DB);
        closeActivity(new Intent(getApplicationContext(), ActivityPracticeList.class));
    }

    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ActivityPracticeList.class);
        if (params != null) {
            Class<?> myClass = null;
            try {
                myClass = Class.forName(getPackageName() + ".activities." + params.getTransmitterActivityName());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            intent = new Intent(getApplicationContext(), myClass);
        }
        closeActivity(intent);
    }

    public void btDelete_onClick(final View view) {
        blink(view, this);
        new AlertDialog.Builder(this)
                .setMessage("Вы действительно хотите удалить текущее занятие и его историю?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DB.deleteAllPracticeHistoryOfPractice(currentPractice.getId());
                        currentPractice.dbDelete(DB);
                        Intent intent = new Intent(getApplicationContext(), ActivityPracticeList.class);
                        sessionOpenActivities.pollFirst();
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                }).setNegativeButton("Нет", null).show();

    }

    public void btPracticeHistoryOfPractice_onClick(View view) {
        blink(view, this);
        Intent intent = new Intent(getApplicationContext(), ActivityPracticeHistoryList.class);
        Boolean isNew = params != null ? params.isReceiverNew() : false;
        ConnectionParameters paramsNew = new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityPractice")
                .isTransmitterNew(isNew)
                .isTransmitterForChoice(false)
                .addReceiverActivityName("ActivityPracticeHistoryList")
                .isReceiverNew(false)
                .isReceiverForChoice(false)
                .build();
        sessionPracticeSequence.push(currentPractice);
        sessionOpenActivities.push(paramsNew);
        intent.putExtra("CurrentPracticeID", currentPractice.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btDetailedPracticeHistoryOfPractice_onClick(View view) {
        blink(view, this);
        Intent intent = new Intent(getApplicationContext(), ActivityDetailedPracticeHistoryList.class);
        Boolean isNew = params != null ? params.isReceiverNew() : false;
        ConnectionParameters paramsNew = new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityPractice")
                .isTransmitterNew(isNew)
                .isTransmitterForChoice(false)
                .addReceiverActivityName("ActivityDetailedPracticeHistoryList")
                .isReceiverNew(false)
                .isReceiverForChoice(false)
                .build();
        sessionPracticeSequence.push(currentPractice);
        sessionOpenActivities.push(paramsNew);
        intent.putExtra("CurrentPracticeID", currentPractice.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
