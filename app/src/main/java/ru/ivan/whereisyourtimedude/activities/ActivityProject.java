package ru.ivan.whereisyourtimedude.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import ru.ivan.whereisyourtimedude.R;
import ru.ivan.whereisyourtimedude.common.ConnectionParameters;
import ru.ivan.whereisyourtimedude.database.entities.Area;
import ru.ivan.whereisyourtimedude.database.entities.Project;
import ru.ivan.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

import static ru.ivan.whereisyourtimedude.common.Common.blink;
import static ru.ivan.whereisyourtimedude.common.Common.setTitleOfActivity;
import static ru.ivan.whereisyourtimedude.common.Session.sessionCurrentProject;
import static ru.ivan.whereisyourtimedude.common.Session.sessionOpenActivities;

public class ActivityProject extends AbstractActivity {

    private boolean isNew;

    private ConnectionParameters params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        Intent intent = getIntent();
        getIntentParams(intent);

        if (isNew) {
            if (sessionCurrentProject == null) {
                sessionCurrentProject = new Project.Builder(DB).build();
            }

        } else {
            if (sessionCurrentProject == null) {
                int id = intent.getIntExtra("CurrentProjectID", 0);
                if (DB.containsProject(id)) {
                    sessionCurrentProject = DB.getProject(id);
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

            tvID.setText(String.valueOf(sessionCurrentProject.getId()));
        }

        //Имя
        int mNameID = getResources().getIdentifier("etName", "id", getPackageName());
        EditText etName = (EditText) findViewById(mNameID);
        if (etName != null) {
            etName.setText(sessionCurrentProject.getName());
        }

        //ID
        int mArea = getResources().getIdentifier("tvArea", "id", getPackageName());
        TextView tvArea = (TextView) findViewById(mArea);
        if (tvArea != null) {
            String nameArea = "";
            Area area = sessionCurrentProject.getArea();
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

            sessionCurrentProject.setName(String.valueOf(etName.getText()));
        }
    }

    public void tvArea_onClick(View view) {
        blink(view, this);
        getPropertiesFromScreen();

        int id_area = 0;
        Area area = sessionCurrentProject.getArea();
        if (area != null) {
            id_area = area.getId();
        }

        Intent intent = new Intent(getApplicationContext(), ActivityAreasList.class);
        Boolean isNew = params != null ? params.isReceiverNew() : false;
        ConnectionParameters paramsNew = new ConnectionParameters.Builder()
                .addTransmitterActivityName("ActivityProject")
                .isTransmitterNew(isNew)
                .isTransmitterForChoice(false)
                .addReceiverActivityName("ActivityAreasList")
                .isReceiverNew(false)
                .isReceiverForChoice(true)
                .build();
        sessionOpenActivities.push(paramsNew);
        intent.putExtra("CurrentAreaID", id_area);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btClose_onClick(final View view) {
        blink(view, this);
        closeActivity(new Intent(getApplicationContext(), ActivityProjectsList.class));
    }

    private void closeActivity(Intent intent) {
        intent.putExtra("CurrentProjectID", sessionCurrentProject.getId());
        sessionOpenActivities.pollFirst();
        sessionCurrentProject = null;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btSave_onClick(final View view) {
        blink(view, this);
        getPropertiesFromScreen();
        sessionCurrentProject.dbSave(DB);
        closeActivity(new Intent(getApplicationContext(), ActivityProjectsList.class));
    }

    public void onBackPressed() {

        Intent intent = new Intent(getApplicationContext(), ActivityProjectsList.class);
        if (params != null) {
            intent = new Intent(getApplicationContext(), ActivityProjectsList.class);
        }
        closeActivity(intent);
    }

    public void btDelete_onClick(final View view) {
        blink(view, this);
        new AlertDialog.Builder(this)
                .setMessage("Do you want to delete project, its practices and other?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sessionCurrentProject.dbDelete(DB);
                        sessionCurrentProject = null;
                        Intent intent = new Intent(getApplicationContext(), ActivityProjectsList.class);
                        sessionOpenActivities.pollFirst();
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).setNegativeButton("No", null).show();
    }
}