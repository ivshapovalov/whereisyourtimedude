package ru.brainworkout.whereisyourtimedude.common;

/**
 * Created by Ivan on 05.09.2016.
 */
public class Area {
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
