package ru.brainworkout.whereisyourtimedude.database.entities;

import ru.brainworkout.whereisyourtimedude.database.interfaces.DeletingFromDb;
import ru.brainworkout.whereisyourtimedude.database.interfaces.SavingIntoDB;
import ru.brainworkout.whereisyourtimedude.database.manager.SQLiteDatabaseManager;

/**
 * Created by Ivan on 05.09.2016.
 */
public class PracticeHistory extends AbstractEntityMultiUser implements SavingIntoDB, DeletingFromDb {

    private volatile Practice practice;
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
        this.practice = builder.practice;
    }

    public Practice getPractice() {
        return practice;
    }

    public void setPractice(Practice practice) {
        this.practice = practice;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
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
            this.lastTime = lastTime;
        }

    }

    @Override
    public void dbSave(SQLiteDatabaseManager db) {

        synchronized (this) {
            if (db.containsPracticeHistory(this.getId())) {
                db.updatePracticeHistory((PracticeHistory) this);
            } else {
                db.addPracticeHistory((PracticeHistory) this);
            }
        }

    }

    @Override
    public void dbDelete(SQLiteDatabaseManager db) {

        if (db.containsPracticeHistory(this.getId())) {
            db.deletePracticeHistory((PracticeHistory) this);
        }

    }

    public static class Builder extends AbstractEntity {

        private Practice practice;
        private long duration;
        private long lastTime;
        private long date;

        public Builder(SQLiteDatabaseManager DB) {
            this.id = DB.getPracticeHistoryMaxNumber() + 1;
        }

        public Builder(int id) {
            this.id = id;
        }

        public Builder addPractice(Practice practice) {
            this.practice = practice;
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
