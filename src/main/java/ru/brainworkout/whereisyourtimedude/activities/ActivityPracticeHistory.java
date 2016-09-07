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
import ru.brainworkout.whereisyourtimedude.database.entities.Practice;
import ru.brainworkout.whereisyourtimedude.database.entities.Project;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

import static ru.brainworkout.whereisyourtimedude.common.Common.blink;
import static ru.brainworkout.whereisyourtimedude.common.Common.setTitleOfActivity;

public class ActivityPracticeHistory extends AppCompatActivity {

    private Practice mCurrentPractice;
    private final DatabaseManager DB = new DatabaseManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);

        Intent intent = getIntent();
        boolean mPracticeIsNew = intent.getBooleanExtra("IsNew", false);

        if (mPracticeIsNew) {
            mCurrentPractice = new Practice.Builder(DB.getPracticeMaxNumber() + 1).build();
        } else {
            int id = intent.getIntExtra("id", 0);
            try {
                mCurrentPractice = DB.getPractice(id);
            } catch (TableDoesNotContainElementException tableDoesNotContainElementException) {
                tableDoesNotContainElementException.printStackTrace();
            }
        }
        int id_project = intent.getIntExtra("id_project", 0);
        if (id_project != 0) {
            mCurrentPractice.setIdProject(id_project);
        }

        showPracticeOnScreen();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setTitleOfActivity(this);
    }


    private void showPracticeOnScreen() {


        int mIsActiveID = getResources().getIdentifier("cbIsActive", "id", getPackageName());
        CheckBox cbIsActive = (CheckBox) findViewById(mIsActiveID);
        if (cbIsActive != null) {
            if (mCurrentPractice.getIsActive() != 0) {
                cbIsActive.setChecked(true);
            } else {
                cbIsActive.setChecked(false);
            }
            cbIsActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (mCurrentPractice != null) {
                        if (isChecked) {
                            mCurrentPractice.setIsActive(1);
                        } else {
                            mCurrentPractice.setIsActive(0);
                        }

                    }
                }
            });

        }


        //ID
        int mID = getResources().getIdentifier("tvID", "id", getPackageName());
        TextView tvID = (TextView) findViewById(mID);
        if (tvID != null) {

            tvID.setText(String.valueOf(mCurrentPractice.getID()));
        }

        //Имя
        int mNameID = getResources().getIdentifier("etName", "id", getPackageName());
        EditText etName = (EditText) findViewById(mNameID);
        if (etName != null) {
            etName.setText(mCurrentPractice.getName());
        }

        //ID
        int mProject = getResources().getIdentifier("tvProject", "id", getPackageName());
        TextView tvProject = (TextView) findViewById(mProject);
        if (tvProject != null) {

            String nameProject = "";
            try {
                Project project = DB.getProject(mCurrentPractice.getIdProject());
                nameProject = project.getName();

            } catch (TableDoesNotContainElementException e) {

            }
            tvProject.setText(nameProject);
        }

    }

    public void btClose_onClick(final View view) {

        blink(view);
        Intent intent = new Intent(getApplicationContext(), ActivityPracticesList.class);
        intent.putExtra("id", mCurrentPractice.getID());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }


    private void getPropertiesFromScreen() {

        //Имя
        int mNameID = getResources().getIdentifier("etName", "id", getPackageName());
        EditText etName = (EditText) findViewById(mNameID);
        if (etName != null) {

            mCurrentPractice.setName(String.valueOf(etName.getText()));

        }

    }

    public void tvProject_onClick(View view) {

        blink(view);

        getPropertiesFromScreen();

        mCurrentPractice.dbSave(DB);

        int id_project = mCurrentPractice.getIdProject();

        Intent intent = new Intent(getApplicationContext(), ActivityProjectsList.class);
        intent.putExtra("id_practice", mCurrentPractice.getID());
        intent.putExtra("IsNew", false);
        intent.putExtra("forChoice", true);
        intent.putExtra("id", id_project);
        intent.putExtra("CallerActivity", "ActivityPractice");
        startActivity(intent);

    }

    public void btSave_onClick(final View view) {

        blink(view);
        getPropertiesFromScreen();

        mCurrentPractice.dbSave(DB);

        Intent intent = new Intent(getApplicationContext(), ActivityPracticesList.class);
        intent.putExtra("id", mCurrentPractice.getID());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btDelete_onClick(final View view) {

        blink(view);


        new AlertDialog.Builder(this)
                .setMessage("Вы действительно хотите удалить текущее занятие и его историю?")
                .setCancelable(false)
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DB.deleteAllPracticeHistoryOfPractice(mCurrentPractice.getID());

                        mCurrentPractice.dbDelete(DB);

                        Intent intent = new Intent(getApplicationContext(), ActivityProjectsList.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                }).setNegativeButton("Нет", null).show();

    }


}