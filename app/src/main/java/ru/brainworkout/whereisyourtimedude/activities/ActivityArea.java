package ru.brainworkout.whereisyourtimedude.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import ru.brainworkout.whereisyourtimedude.common.ConnectionParameters;
import ru.brainworkout.whereisyourtimedude.database.entities.Area;
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.Project;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

import static ru.brainworkout.whereisyourtimedude.common.Common.blink;
import static ru.brainworkout.whereisyourtimedude.common.Common.setTitleOfActivity;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionCurrentArea;
import static ru.brainworkout.whereisyourtimedude.common.Session.sessionOpenActivities;

public class ActivityArea extends AbstractActivity {

    private ConnectionParameters params;
    private boolean isNew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area);

        Intent intent = getIntent();
        getIntentParams(intent);

        if (isNew) {
            if (sessionCurrentArea == null) {
                sessionCurrentArea = new Area.Builder(DB).build();
            }
        } else {
            int id = intent.getIntExtra("CurrentAreaID", 0);
            if (DB.containsArea(id)) {
                sessionCurrentArea = DB.getArea(id);
            } else {
                throw new TableDoesNotContainElementException(String.format("Area with id ='%s' does not exists in database", id));
            }
        }
        showAreaOnScreen();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setTitleOfActivity(this);
    }

    private void getIntentParams(Intent intent) {

        if (!sessionOpenActivities.isEmpty()) {
            params = sessionOpenActivities.peek();
        }
        isNew = (params != null ? params.isReceiverNew() : false);

    }

    private void showAreaOnScreen() {

        //ID
        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            tvID.setText(String.valueOf(sessionCurrentArea.getId()));
        }

        //Имя
        int mNameID = getResources().getIdentifier("etName", "id", getPackageName());
        EditText etName = (EditText) findViewById(mNameID);
        if (etName != null) {
            etName.setText(sessionCurrentArea.getName());
        }

        //ID
        int mColor = getResources().getIdentifier("tvColor", "id", getPackageName());
        TextView tvColor = (TextView) findViewById(mColor);
        if (tvColor != null) {

            tvColor.setBackgroundColor(sessionCurrentArea.getColor());
        }
    }

    private void getPropertiesFromScreen() {
        int mNameID = getResources().getIdentifier("etName", "id", getPackageName());
        EditText etName = (EditText) findViewById(mNameID);
        if (etName != null) {
            sessionCurrentArea.setName(String.valueOf(etName.getText()));
        }
    }

    public void tvColor_onClick(View view) {

        getPropertiesFromScreen();
        ColorPickerDialogBuilder
                .with(this)
                .setTitle("Choose color")
                .initialColor(sessionCurrentArea.getColor())
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
        if (sessionCurrentArea != null) {
            sessionCurrentArea.setColor(selectedColor);
            showAreaOnScreen();
        }
    }

    public void btClose_onClick(final View view) {
        blink(view, this);
        closeActivity(new Intent(getApplicationContext(), ActivityAreasList.class));
    }

    private void closeActivity(Intent intent) {
        intent.putExtra("CurrentAreaID", sessionCurrentArea.getId());
        sessionOpenActivities.pollFirst();
        sessionCurrentArea = null;
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btSave_onClick(final View view) {

        blink(view, this);
        getPropertiesFromScreen();
        sessionCurrentArea.dbSave(DB);
        closeActivity(new Intent(getApplicationContext(), ActivityAreasList.class));
    }

    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        if (params != null) {
            intent = new Intent(getApplicationContext(), ActivityAreasList.class);
        }
        closeActivity(intent);
    }

    public void btDelete_onClick(final View view) {

        blink(view, this);
        new AlertDialog.Builder(this)
                .setMessage("Do you want to delete current area, it's projects and practices?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        sessionCurrentArea.dbDelete(DB);
                        sessionCurrentArea = null;
                        sessionOpenActivities.pollFirst();
                        Intent intent = new Intent(getApplicationContext(), ActivityAreasList.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }).setNegativeButton("Нет", null).show();
    }
}