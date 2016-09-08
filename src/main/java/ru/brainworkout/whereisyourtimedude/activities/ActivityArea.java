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

import com.flask.colorpicker.*;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.List;

import ru.brainworkout.whereisyourtimedude.R;
import ru.brainworkout.whereisyourtimedude.database.entities.Area;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.Project;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

import static ru.brainworkout.whereisyourtimedude.common.Common.blink;
import static ru.brainworkout.whereisyourtimedude.common.Common.setTitleOfActivity;
import static ru.brainworkout.whereisyourtimedude.common.Session.currentArea;
import static ru.brainworkout.whereisyourtimedude.common.Session.currentProject;

public class ActivityArea extends AppCompatActivity {

    private final DatabaseManager DB = new DatabaseManager(this);
    private boolean forChoice;
    private String mCallerActivity;
    private boolean isNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);

        Intent intent = getIntent();
        isNew = intent.getBooleanExtra("isNew", false);
        forChoice = intent.getBooleanExtra("forChoice", false);
        mCallerActivity = intent.getStringExtra("CallerActivity");

        if (isNew) {
            if (currentArea == null) {
                currentArea = new Area.Builder(DB.getAreaMaxNumber() + 1).build();
            }
        } else {
            int id = intent.getIntExtra("CurrentAreaID", 0);
            try {
                currentArea = DB.getArea(id);
            } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
                tableDoesNotContainElementException.printStackTrace();
            }
        }

        showAreaOnScreen();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setTitleOfActivity(this);
    }


    private void showAreaOnScreen() {

        //ID
        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            tvID.setText(String.valueOf(currentArea.getID()));
        }

        //Имя
        int mNameID = getResources().getIdentifier("etName", "id", getPackageName());
        EditText etName = (EditText) findViewById(mNameID);
        if (etName != null) {
            etName.setText(currentArea.getName());
        }

        //ID
        int mColor = getResources().getIdentifier("tvColor", "id", getPackageName());
        TextView tvColor = (TextView) findViewById(mColor);
        if (tvColor != null) {

            tvColor.setBackgroundColor(currentArea.getColor());
        }

    }

    public void btClose_onClick(final View view) {

        blink(view);
        Intent intent = new Intent(getApplicationContext(), ActivityAreasList.class);
        intent.putExtra("CurrentAreaID", currentArea.getID());
        currentArea = null;
        intent.putExtra("forChoice", forChoice);
        intent.putExtra("CallerActivity", "ActivityProject");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }


    private void getPropertiesFromScreen() {

        //Имя
        int mNameID = getResources().getIdentifier("etName", "id", getPackageName());
        EditText etName = (EditText) findViewById(mNameID);
        if (etName != null) {

            currentArea.setName(String.valueOf(etName.getText()));

        }

    }

    public void tvColor_onClick(View view) {

        getPropertiesFromScreen();
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(currentArea.getColor())
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .density(12)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        //toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                    }
                })
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        changeColor(selectedColor);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    private void changeColor(int selectedColor) {
        if (currentArea != null) {
            currentArea.setColor(selectedColor);
            showAreaOnScreen();
        }
    }

    public void btSave_onClick(final View view) {

        blink(view);
        getPropertiesFromScreen();

        currentArea.dbSave(DB);


        Intent intent = new Intent(getApplicationContext(), ActivityAreasList.class);
        intent.putExtra("CurrentAreaID", currentArea.getID());
        currentArea = null;
        intent.putExtra("forChoice", forChoice);
        intent.putExtra("CallerActivity", "ActivityProject");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btDelete_onClick(final View view) {

        blink(view);


        new AlertDialog.Builder(this)
                .setMessage("Вы действительно хотите удалить текущую область, ее занятия, проекты и историю?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        List<Project> projects = DB.getAllProjectsOfArea(currentArea.getID());

                        for (Project project : projects
                                ) {
                            List<Practice> practices = DB.getAllActivePracticesOfProject(project.getID());
                            for (Practice practice : practices
                                    ) {
                                DB.deleteAllPracticeHistoryOfPractice(practice.getID());
                            }
                            DB.deleteAllPracticesOfProject(project.getID());
                        }
                        DB.deleteAllProjectsOfArea(currentArea.getID());

                        currentArea.dbDelete(DB);
                        currentArea = null;

                        Intent intent = new Intent(getApplicationContext(), ActivityAreasList.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                }).setNegativeButton("Нет", null).show();

    }
}