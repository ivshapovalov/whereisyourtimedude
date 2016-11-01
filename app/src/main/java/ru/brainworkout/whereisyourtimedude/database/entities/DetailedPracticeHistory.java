package ru.brainworkout.whereisyourtimedude.database.entities;

import ru.brainworkout.whereisyourtimedude.database.interfaces.DeletingFromDb;
import ru.brainworkout.whereisyourtimedude.database.interfaces.SavingIntoDB;
import ru.brainworkout.whereisyourtimedude.database.manager.SqlLiteDatabaseManager;


public class DetailedPracticeHistory extends AbstractEntityMultiUser implements SavingIntoDB, DeletingFromDb {

    private volatile int id_practice;
    private volatile long duration;//seconds
    private volatile long time;//millis
    private volatile long date;//millis


    @Override
    public boolean equals(Object obj) {
        return this.getId() == ((DetailedPracticeHistory) obj).getId();
    }

    @Override
    public int hashCode() {
        return 1;
    }

    public DetailedPracticeHistory(Builder builder) {

        this.id = builder.id;
        this.duration = builder.duration;
        this.time = builder.time;
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

    public long getTime() {
        synchronized (this) {
            return time;
        }
    }

    public void setTime(long time) {
        synchronized (this) {
        this.time = time;}
    }

    @Override
    public void dbSave(SqlLiteDatabaseManager db) {
        synchronized (this) {

            if (db.containsDetailedPracticeHistory(this.getID())) {
                db.updateDetailedPracticeHistory((DetailedPracticeHistory) this);
            } else {
                db.addDetailedPracticeHistory((DetailedPracticeHistory) this);
            }
        }
    }

    @Override
    public void dbDelete(SqlLiteDatabaseManager db) {

        if (db.containsDetailedPracticeHistory(this.getID())) {
            db.deleteDetailedPracticeHistory((DetailedPracticeHistory) this);
        }

    }

    public static class Builder extends AbstractEntity {

        private int id_practice;
        private long duration;
        private long time;
        private long date;

        public Builder(SqlLiteDatabaseManager DB) {
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

        public Builder addTime(long time) {
            this.time = time;
            return this;
        }

        public DetailedPracticeHistory build() {
            DetailedPracticeHistory practiceHistory = new DetailedPracticeHistory(this);
            return practiceHistory;
        }

    }

}
