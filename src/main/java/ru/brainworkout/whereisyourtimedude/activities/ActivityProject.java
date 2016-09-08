package ru.brainworkout.whereisyourtimedude.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.List;

import ru.brainworkout.whereisyourtimedude.R;
import ru.brainworkout.whereisyourtimedude.common.Session;
import ru.brainworkout.whereisyourtimedude.database.entities.Area;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.Project;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

import static ru.brainworkout.whereisyourtimedude.common.Common.blink;
import static ru.brainworkout.whereisyourtimedude.common.Common.setTitleOfActivity;
import static ru.brainworkout.whereisyourtimedude.common.Session.currentProject;

public class ActivityProject extends AppCompatActivity {

    //private Project mCurrentProject;
    private final DatabaseManager DB = new DatabaseManager(this);
    boolean isNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        Intent intent = getIntent();
        isNew = intent.getBooleanExtra("IsNew", false);


        if (isNew) {
            if (currentProject == null) {
                currentProject = new Project.Builder(DB.getProjectMaxNumber() + 1).build();
            }

        } else {
            int id = intent.getIntExtra("CurrentProjectID", 0);
            try {
                currentProject = DB.getProject(id);
            } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
                tableDoesNotContainElementException.printStackTrace();
            }
        }
//        int id_area = intent.getIntExtra("CurrentAreaID", 0);
//        if (id_area != 0) {
//            currentProject.setIdArea(id_area);
//        }

        showProjectOnScreen();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setTitleOfActivity(this);
    }


    private void showProjectOnScreen() {

        //ID
        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            tvID.setText(String.valueOf(currentProject.getID()));
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
            try {
                Area area = DB.getArea(currentProject.getIdArea());
                nameArea = area.getName();
                tvArea.setBackgroundColor(area.getColor());
            } catch (TableDoesNotContainElementException e) {

            }
            tvArea.setText(nameArea);
        }

    }

    public void btClose_onClick(final View view) {

        blink(view);
        Intent intent = new Intent(getApplicationContext(), ActivityProjectsList.class);
        intent.putExtra("CurrentProjectID", currentProject.getID());
        currentProject=null;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

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

        blink(view);

        getPropertiesFromScreen();

        //currentProject.dbSave(DB);

        int id_area = currentProject.getIdArea();

        Intent intent = new Intent(getApplicationContext(), ActivityAreasList.class);
        intent.putExtra("CurrentAreaID", id_area);
        intent.putExtra("IsNew", isNew);
        intent.putExtra("forChoice", true);
        //intent.putExtra("CurrentProjectID", mCurrentProject.getID());
        intent.putExtra("CallerActivity", "ActivityProject");
        startActivity(intent);

    }

    public void btSave_onClick(final View view) {

        blink(view);
        getPropertiesFromScreen();

        currentProject.dbSave(DB);

        Intent intent = new Intent(getApplicationContext(), ActivityProjectsList.class);
        intent.putExtra("CurrentProjectID", currentProject.getID());
        currentProject=null;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btDelete_onClick(final View view) {

        blink(view);


        new AlertDialog.Builder(this)
                .setMessage("Вы действительно хотите удалить текущий проект, его занятия и историю?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        List<Practice> practices = DB.getAllActivePracticesOfProject(currentProject.getID());

                        for (Practice practice : practices
                                ) {
                            DB.deleteAllPracticeHistoryOfPractice(practice.getID());

                        }
                        DB.deleteAllPracticesOfProject(currentProject.getID());

                        currentProject.dbDelete(DB);
                        currentProject=null;

                        Intent intent = new Intent(getApplicationContext(), ActivityProjectsList.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                }).setNegativeButton("Нет", null).show();

    }


}