package ru.brainworkout.whereisyourtimedude;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    LinkedList<Work> works = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {

        ArrayList<Area> areas = new ArrayList<>();

        areas.add(new Area(Color.GREEN,"GREEN"));
        areas.add(new Area(Color.RED,"RED"));
        areas.add(new Area(Color.YELLOW,"YELLOW"));
        areas.add(new Area(Color.BLUE,"BLUE"));
        areas.add(new Area(Color.GRAY,"GRAY"));



        for (int i = 0; i < 25; i++) {
            int indexArea = ((int)(Math.random()*areas.size()));
            works.add(new Work(areas.get(indexArea),i, String.valueOf(i)));
        }
        updateScreen();


    }

    private void rowWork_onClick(TableRow v) {
        System.out.println(v.getId());

        int index = works.indexOf(new Work(v.getId(), String.valueOf(v.getId())));
        Work currentWork = works.get(index);
        works.remove(currentWork);
        works.addFirst(currentWork);
        updateScreen();
    }

    private void updateScreen() {

        int tvIDCurrentName = getResources().getIdentifier("tvCurrentWorkName", "id", getPackageName());
        TextView tvCurrentName = (TextView) findViewById(tvIDCurrentName);
        if (tvCurrentName != null) {
            tvCurrentName.setText(works.get(0).getName());
        }
        int tvIDCurrentTime = getResources().getIdentifier("tvCurrentWorkTime", "id", getPackageName());
        TextView tvCurrentTime = (TextView) findViewById(tvIDCurrentTime);
        if (tvCurrentTime != null) {
            tvCurrentTime.setText("0 time");
        }
        int tvIDCurrentArea = getResources().getIdentifier("tvCurrentWorkArea", "id", getPackageName());
        TextView tvCurrentArea = (TextView) findViewById(tvIDCurrentArea);
        if (tvCurrentArea != null) {
            tvCurrentArea.setText(works.get(0).getArea().getName());
        }
        int tvIDCurrentDate = getResources().getIdentifier("tvCurrentWorkDate", "id", getPackageName());
        TextView tvCurrentDate = (TextView) findViewById(tvIDCurrentDate);
        if (tvCurrentDate != null) {
            tvCurrentDate.setText("0 date");
        }

        int tableLayout = getResources().getIdentifier("tableWorks", "id", getPackageName());
        TableLayout table = (TableLayout) findViewById(tableLayout);
        if (table != null) {
            table.removeAllViews();
            for (int i = 1; i < works.size(); i++
                    ) {
                TableRow mRow = CreateTableRow(i);
                table.addView(mRow);
            }

        }

    }

    @NonNull
    private TableRow CreateTableRow(int i) {
        Work currentWork=works.get(i);
        TableRow rowMain = new TableRow(this);

        rowMain.setId(Integer.valueOf(currentWork.getId()));

        TableRow.LayoutParams params100 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params100.weight = 100;
        params100.topMargin=10;
        TableRow.LayoutParams params50 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
        params50.weight = 50;

        TableLayout layout=new TableLayout(this);
        layout.setLayoutParams(params100);

        TableRow row1 = new TableRow(this);
        row1.setLayoutParams(params100);

        TextView txtName = new TextView(this);
        txtName.setBackgroundColor(currentWork.getArea().getColor());
        txtName.setText(currentWork.getName());
        txtName.setLayoutParams(params50);
        row1.addView(txtName);

        TextView txtTime = new TextView(this);
        txtTime.setBackgroundColor(currentWork.getArea().getColor());
        txtTime.setText("0 time");
        txtTime.setLayoutParams(params50);
        row1.addView(txtTime);

        layout.addView(row1);

        TableRow row2 = new TableRow(this);
        row2.setLayoutParams(params100);

        TextView txtArea = new TextView(this);
        txtArea.setBackgroundColor(currentWork.getArea().getColor());
        txtArea.setText(currentWork.getArea().getName());
        txtArea.setLayoutParams(params50);
        row2.addView(txtArea);

        TextView txtDate = new TextView(this);
        txtDate.setBackgroundColor(currentWork.getArea().getColor());
        txtDate.setText("0 date");
        txtDate.setLayoutParams(params50);
        row2.addView(txtDate);

        layout.addView(row2);

        rowMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rowWork_onClick((TableRow) v);
            }
        });
        rowMain.addView(layout);
        return rowMain;
    }

    class Work {
        int id;
        String name;
        int seconds;
        int lastDate;
        Area area;

        @Override
        public boolean equals(Object obj) {
            return this.getId() == ((Work) obj).getId();
        }

        @Override
        public int hashCode() {
            return 1;
        }

        public Work(Area area, int id, String name) {
            this.area = area;
            this.id = id;
            this.name = name;
        }

        public Work(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getSeconds() {
            return seconds;
        }

        public void setSeconds(int seconds) {
            this.seconds = seconds;
        }

        public int getLastDate() {
            return lastDate;
        }

        public void setLastDate(int lastDate) {
            this.lastDate = lastDate;
        }

        public Area getArea() {
            return area;
        }

        public void setArea(Area area) {
            this.area = area;
        }
    }

    class Area {
        int color;
        String name;

        public int getColor() {
            return color;
        }

        public void setColor(int color) {
            this.color = color;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Area(int color, String name) {

            this.color = color;
            this.name = name;
        }
    }

}
