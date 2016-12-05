package ru.brainworkout.whereisyourtimedude.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import ru.brainworkout.whereisyourtimedude.R;
import ru.brainworkout.whereisyourtimedude.common.ConnectionParameters;
import ru.brainworkout.whereisyourtimedude.database.entities.Area;
import ru.brainworkout.whereisyourtimedude.database.entities.Project;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

import static ru.brainworkout.whereisyourtimedude.common.Common.blink;
import static ru.brainworkout.whereisyourtimedude.common.Common.setTitleOfActivity;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionProjectSequence;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionOpenActivities;

public class ActivityProject extends AbstractActivity {

    private boolean isNew;
    private ConnectionParameters params;
    private Project currentProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        Intent intent = getIntent();
        getIntentParams(intent);

        if (isNew) {
            if (!sessionProjectSequence.isEmpty()) {
                currentProject = sessionProjectSequence.pollFirst();
            } else {
                currentProject = new Project.Builder(DB).build();
            }
        } else {
            if (!sessionProjectSequence.isEmpty()) {
                currentProject = sessionProjectSequence.pollFirst();
            } else {
                int id = intent.getIntExtra("CurrentProjectID", 0);
                if (DB.containsProject(id)) {
                    currentProject = DB.getProject(id);
                } else {
                    throw new TableDoesNotContainElementException(String.format("Project with id ='%s' does not exists in database", id));
                }
            }
        }
        showProjectOnScreen();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setTitleOfActivity(this);
    }

    private void getIntentParams(Intent intent) {
        if (!sessionOpenActivities.isEmpty()) {
            params = sessionOpenActivities.peek();
        }
        isNew = (params != null ? params.isReceiverNew() : false);
    }

    private void showProjectOnScreen() {

        //ID
        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            tvID.setText(String.valueOf(currentProject.getId()));
        }

        //Имя
        int mNameID = getResources().getIdentifier("etName", "id", getPackageName());
        EditText etName = (EditText) findViewById(mNameID);
        if (etName != null) {
            etName.setText(currentProject.getName());
        }

        //ID
        int mArea = getResources().getIdentifier("tvArea", "id", getPackageName());
        TextView tvArea = (TextView) findViewById(mArea);
        if (tvArea != null) {
            String nameArea = "";
            Area area = currentProject.getArea();
            if (area != null) {
                nameArea = area.getName();
                tvArea.setBackgroundColor(area.getColor());
            }
            tvArea.setText(nameArea);
        }
    }

    private void getPropertiesFromScreen() {
        //Имя
        int mNameID = getResources().getIdentifier("etName", "id", getPackageName());
        EditText etName = (EditText) findViewById(mNameID);
        if (etName != null) {
            currentProject.setName(String.valueOf(etName.getText()));
        }
    }

    public void tvArea_onClick(View view) {
        blink(view, this);
        getPropertiesFromScreen();

        int id_area = 0;
        Area area = currentProject.getArea();
        if (area != null) {
            id_area = area.getId();
        }

        Intent intent = new Intent(getApplicationContext(), ActivityAreaList.class);
        Boolean isNew = params != null ? params.isReceiverNew() : false;
        ConnectionParameters paramsNew = new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityProject")
                .isTransmitterNew(isNew)
                .isTransmitterForChoice(false)
                .addReceiverActivityName("ActivityAreaList")
                .isReceiverNew(false)
                .isReceiverForChoice(true)
                .build();
        sessionProjectSequence.push(currentProject);
        sessionOpenActivities.push(paramsNew);
        intent.putExtra("CurrentAreaID", id_area);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btClose_onClick(final View view) {
        blink(view, this);
        closeActivity(new Intent(getApplicationContext(), ActivityProjectList.class));
    }

    private void closeActivity(Intent intent) {
        intent.putExtra("CurrentProjectID", currentProject.getId());
        sessionOpenActivities.pollFirst();
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btSave_onClick(final View view) {
        blink(view, this);
        getPropertiesFromScreen();
        currentProject.dbSave(DB);
        closeActivity(new Intent(getApplicationContext(), ActivityProjectList.class));
    }

    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ActivityProjectList.class);
        if (params != null) {
            Class<?> myClass = null;
            try {
                myClass = Class.forName(getPackageName() + ".activities." + sessionOpenActivities.getFirst().getTransmitterActivityName());
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
                .setMessage("Do you want to delete project, its practices and other?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        currentProject.dbDelete(DB);
                        Intent intent = new Intent(getApplicationContext(), ActivityProjectList.class);
                        sessionOpenActivities.pollFirst();
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).setNegativeButton("Нет", null).show();
    }

    public void btPracticesOfProject_onClick(View view) {
        blink(view, this);
        Intent intent = new Intent(getApplicationContext(), ActivityPracticeList.class);
        Boolean isNew = params != null ? params.isReceiverNew() : false;
        ConnectionParameters paramsNew = new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityProject")
                .isTransmitterNew(isNew)
                .isTransmitterForChoice(false)
                .addReceiverActivityName("ActivityPracticeList")
                .isReceiverNew(false)
                .isReceiverForChoice(false)
                .build();
        sessionProjectSequence.push(currentProject);
        sessionOpenActivities.push(paramsNew);
        intent.putExtra("CurrentProjectID", currentProject.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}