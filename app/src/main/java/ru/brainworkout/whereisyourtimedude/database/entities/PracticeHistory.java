package ru.brainworkout.whereisyourtimedude.database.entities;

import java.util.Calendar;

import ru.brainworkout.whereisyourtimedude.database.interfaces.DeletingFromDb;
import ru.brainworkout.whereisyourtimedude.database.interfaces.SavingIntoDB;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

/**
 * Created by Ivan on 05.09.2016.
 */
public class PracticeHistory extends AbstractEntityMultiUser implements SavingIntoDB, DeletingFromDb {

    private volatile int id_practice;
    private volatile long duration;//seconds
    private volatile long lastTime;//millis
    private volatile long date;//millis


    @Override
    public boolean equals(Object obj) {
        return this.getId() == ((PracticeHistory) obj).getId();
    }

    @Override
    public int hashCode() {
        return 1;
    }

    public PracticeHistory(Builder builder) {

        this.id = builder.id;
        this.duration = builder.duration;
        this.lastTime = builder.lastTime;
        this.date = builder.date;
        this.id_practice = builder.id_practice;
    }

    public int getIdPractice() {
        return id_practice;
    }

    public void setIdPractice(int id_practice) {
        this.id_practice = id_practice;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public long getDuration() {
        synchronized (this) {
            return duration;
        }
    }

    public void setDuration(long duration) {
        synchronized (this) {
            this.duration = duration;
        }
    }

    public long getLastTime() {
        synchronized (this) {
            return lastTime;
        }
    }

    public void setLastTime(long lastTime) {
        synchronized (this) {
        this.lastTime = lastTime;}

    }

    @Override
    public void dbSave(DatabaseManager db) {
        synchronized (this) {

            if (db.containsPracticeHistory(this.getID())) {
                db.updatePracticeHistory((PracticeHistory) this);
            } else {
                db.addPracticeHistory((PracticeHistory) this);
            }
        }
    }

    @Override
    public void dbDelete(DatabaseManager db) {

        if (db.containsPracticeHistory(this.getID())) {
            db.deletePracticeHistory((PracticeHistory) this);
        }

    }

    public static class Builder extends AbstractEntity {

        private int id_practice;
        private long duration;
        private long lastTime;
        private long date;

        public Builder(DatabaseManager DB) {
            this.id = DB.getPracticeHistoryMaxNumber() + 1;
        }

        public Builder(int id) {
            this.id = id;
        }

        public Builder addIdPractice(int id_practice) {
            this.id_practice = id_practice;
            return this;
        }

        public Builder addDuration(long duration) {
            this.duration = duration;
            return this;
        }

        public Builder addDate(long date) {
            this.date = date;
            return this;
        }

        public Builder addLastTime(long lastTime) {
            this.lastTime = lastTime;
            return this;
        }


        public PracticeHistory build() {
            PracticeHistory practiceHistory = new PracticeHistory(this);
            return practiceHistory;
        }

    }

}