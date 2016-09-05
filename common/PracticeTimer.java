package ru.brainworkout.whereisyourtimedude.common;

import java.util.Calendar;

/**
 * Created by Ivan on 05.09.2016.
 */
public class PracticeTimer {
    int id;
    String name;
    int duration;
    Calendar lastTime;
    String date;
    Area area;

    @Override
    public boolean equals(Object obj) {
        return this.getId() == ((PracticeTimer) obj).getId();
    }

    @Override
    public int hashCode() {
        return 1;
    }

    public PracticeTimer(int id, String name, Area area) {
        this.area = area;
        this.id = id;
        this.name = name;
    }

    public PracticeTimer(int id, String name, Area area, int duration) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.area = area;
    }

    public PracticeTimer(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Calendar getLastTime() {
        return lastTime;
    }

    public void setLastTime(Calendar lastTime) {
        this.lastTime = lastTime;
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }
}
