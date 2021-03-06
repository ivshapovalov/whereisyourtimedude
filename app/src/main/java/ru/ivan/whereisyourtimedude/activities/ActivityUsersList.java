package ru.ivan.whereisyourtimedude.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.ivan.whereisyourtimedude.R;
import ru.ivan.whereisyourtimedude.common.Common;
import ru.ivan.whereisyourtimedude.common.Session;
import ru.ivan.whereisyourtimedude.database.entities.User;
import ru.ivan.whereisyourtimedude.database.manager.AndroidDatabaseManager;

import static ru.ivan.whereisyourtimedude.common.Common.hideEditorButton;
import static ru.ivan.whereisyourtimedude.common.Common.blink;
import static ru.ivan.whereisyourtimedude.common.Common.paramsTextViewWithSpanInList;
import static ru.ivan.whereisyourtimedude.common.Common.setTitleOfActivity;
import static ru.ivan.whereisyourtimedude.common.Session.sessionOptions;

public class ActivityUsersList extends AbstractActivity {

    private final int MAX_VERTICAL_BUTTON_COUNT = 17;
    private final int MAX_HORIZONTAL_BUTTON_COUNT = 2;
    private final int NUMBER_OF_VIEWS = 40000;

    private int mHeight = 0;
    private int mWidth = 0;
    private int mTextSize = 0;

    private int idUser;

    private int rowsNumber;
    Map<Integer, List<User>> pagedUsers = new HashMap<>();
    private int currentPage = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);

        if (!Common.isDebug) {
            int mEditorID = getResources().getIdentifier("btUsersDBEditor", "id", getPackageName());
            Button btEditor = (Button) findViewById(mEditorID);
            hideEditorButton(btEditor);
        }

        Intent intent = getIntent();
        getIntentParams(intent);

        if (Session.sessionOptions!=null) {
            rowsNumber =sessionOptions.getRowNumberInLists();
        }

        updateUsers();

        TableRow mRow = (TableRow) findViewById(NUMBER_OF_VIEWS + idUser);
        if (mRow != null) {
            int mScrID = getResources().getIdentifier("svTableUsers", "id", getPackageName());
            ScrollView mScrollView = (ScrollView) findViewById(mScrID);
            if (mScrollView != null) {
                mScrollView.requestChildFocus(mRow, mRow);
            }
        }
        setTitleOfActivity(this);
    }

    private void getIntentParams(Intent intent) {
        idUser = intent.getIntExtra("id", 0);
    }

    private void updateUsers() {
        pageUsers();
        showUsers();
    }

    private void pageUsers() {
        List<User> users = DB.getAllUsers();
        pagedUsers.clear();
        List<User> pageContent = new ArrayList<>();
        int pageNumber = 1;
        for (int i = 0; i < users.size(); i++) {
            if (idUser != 0) {
                if (users.get(i).getId() == idUser) {
                    currentPage = pageNumber;
                }
            }
            pageContent.add(users.get(i));
            if (pageContent.size() == rowsNumber) {
                pagedUsers.put(pageNumber, pageContent);
                pageContent = new ArrayList<>();
                pageNumber++;
            }
        }
        if (pageContent.size() != 0) {
            pagedUsers.put(pageNumber, pageContent);
        }

        if (pagedUsers.size()==0) {
            currentPage=0;
        }
    }

    public void btUsersAdd_onClick(final View view) {

        blink(view,this);
        Intent intent = new Intent(getApplicationContext(), ActivityUser.class);
        intent.putExtra("isNew", true);
        startActivity(intent);
    }

    private void showUsers() {

        Button pageNumber = (Button) findViewById(R.id.btPageNumber);
        if (pageNumber != null && pagedUsers !=null) {
            pageNumber.setText(String.valueOf(currentPage)+"/"+ pagedUsers.size());
        }

        ScrollView sv = (ScrollView) findViewById(R.id.svTableUsers);
        try {

            sv.removeAllViews();

        } catch (NullPointerException e) {
        }

        DisplayMetrics displaymetrics = getResources().getDisplayMetrics();

        mHeight = displaymetrics.heightPixels / MAX_VERTICAL_BUTTON_COUNT;
        mWidth = displaymetrics.widthPixels / MAX_HORIZONTAL_BUTTON_COUNT;
        mTextSize = (int) (Math.min(mWidth, mHeight) / 1.5 / getApplicationContext().getResources().getDisplayMetrics().density);

        TableRow trowButtons = (TableRow) findViewById(R.id.trowButtons);

        if (trowButtons != null) {
            trowButtons.setMinimumHeight(mHeight);
        }

        TableLayout layout = new TableLayout(this);
        layout.setStretchAllColumns(true);
        layout.setShrinkAllColumns(true);

        List<User> page = pagedUsers.get(currentPage);
        if (page == null) return;
        int currentPageSize = page.size();
        for (int num = 0; num < currentPageSize; num++) {
            User user=page.get(num);
            TableRow mRow = new TableRow(this);
            mRow.setId(NUMBER_OF_VIEWS + user.getId());
            mRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rowUser_onClick((TableRow) v);
                }
            });
            mRow.setMinimumHeight(mHeight);
            mRow.setBackgroundResource(R.drawable.bt_border);
            //mRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT));

            TextView txt = new TextView(this);
            txt.setText(String.valueOf(user.getId()));
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            txt.setLayoutParams(paramsTextViewWithSpanInList(5));
            mRow.addView(txt);

            txt = new TextView(this);
            String name = String.valueOf(user.getName()) + ((user.isCurrentUser() == 1) ? " (CURRENT)" : "");
            txt.setText(name);
            txt.setBackgroundResource(R.drawable.bt_border);
            txt.setGravity(Gravity.CENTER);
            txt.setTextSize(mTextSize);
            txt.setTextColor(getResources().getColor(R.color.text_color));
            txt.setLayoutParams(paramsTextViewWithSpanInList(10));
            mRow.addView(txt);

            mRow.setBackgroundResource(R.drawable.bt_border);
            layout.addView(mRow);

        }
        sv.addView(layout);
    }

    private void rowUser_onClick(final TableRow view) {

        blink(view,this);
        int id = view.getId() % NUMBER_OF_VIEWS;
        Intent intent = new Intent(getApplicationContext(), ActivityUser.class);
        intent.putExtra("id", id);
        intent.putExtra("isNew", false);
        startActivity(intent);

    }

    public void btEdit_onClick(final View view) {
        blink(view,this);
        Intent dbmanager = new Intent(getApplicationContext(), AndroidDatabaseManager.class);
        startActivity(dbmanager);
    }


    public void buttonHome_onClick(final View view) {

        blink(view,this);
        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btClear_onClick(final View view) {

        blink(view,this);

        new AlertDialog.Builder(this)
                .setMessage("Do you want to remove all the users, its areas, projects and practices with history?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        DB.deleteAllUsers();
                        Session.sessionCurrentUser = null;
                        updateUsers() ;
                    }

                }).setNegativeButton("No", null).show();
    }

    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), ActivityMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void btNextPage_onClick(View view) {
        blink(view, this);

        if (currentPage != pagedUsers.size()) {
            currentPage++;
        }
        showUsers();
    }

    public void btPreviousPage_onClick(View view) {
        blink(view, this);
        if (currentPage > 1) {
            currentPage--;
        }
        showUsers();
    }
}

