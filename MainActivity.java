package ru.brainworkout.whereisyourtimedude;

import android.graphics.Color;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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

    private void buttonWork_onClick(Button v) {
        System.out.println(v.getId());

        int index = works.indexOf(new Work(v.getId(), String.valueOf(v.getId())));
        Work currentWork = works.get(index);
        works.remove(currentWork);
        works.addFirst(currentWork);
        updateScreen();
    }

    private void updateScreen() {

        int btCurrentId = getResources().getIdentifier("btCurrentWork", "id", getPackageName());
        Button btCurrent = (Button) findViewById(btCurrentId);
        if (btCurrent != null) {
            btCurrent.setText(works.get(0).getName());
            btCurrent.setBackgroundColor(works.get(0).getArea().getColor());

        }

        int tableLayout = getResources().getIdentifier("tableWorks", "id", getPackageName());
        TableLayout table = (TableLayout) findViewById(tableLayout);
        if (table != null) {
            table.removeAllViews();
            for (int i = 1; i < works.size(); i++
                    ) {
                TableRow mRow = new TableRow(this);
                Work currentWork=works.get(i);
                Button but = new Button(this);
                but.setBackgroundColor(currentWork.getArea().getColor());
                but.setId(Integer.valueOf(currentWork.getId()));
                but.setText(currentWork.getName());
                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                params.weight = 100;
                but.setLayoutParams(params);
                but.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buttonWork_onClick((Button) v);
                    }
                });
                mRow.addView(but);
                table.addView(mRow);
            }

        }

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
