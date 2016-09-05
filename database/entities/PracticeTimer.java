package ru.brainworkout.whereisyourtimedude.database.entities;

import java.util.Calendar;

import ru.brainworkout.whereisyourtimedude.database.interfaces.DeletingFromDb;
import ru.brainworkout.whereisyourtimedude.database.interfaces.SavingIntoDB;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

/**
 * Created by Ivan on 05.09.2016.
 */
public class PracticeTimer extends AbstractEntityMultiUser implements SavingIntoDB,DeletingFromDb{

    private String name;
    private int duration;
    private Calendar lastTime;
    private String date;
    private Practice practice;

    @Override
    public boolean equals(Object obj) {
        return this.getId() == ((PracticeTimer) obj).getId();
    }

    @Override
    public int hashCode() {
        return 1;
    }

    public PracticeTimer(Builder builder) {

        this._id = builder._id;
        this.duration = builder.duration;
        this.lastTime=builder.lastTime;
        this.date=builder.date;
        this.practice=builder.practice;
    }

    public Practice getPractice() {
        return practice;
    }
    public int getPracticeID() {
        return practice!=null?practice.getID():0;
    }

    public void setPractice(Practice practice) {
        this.practice = practice;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return _id;
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

    @Override
    public void dbSave(DatabaseManager db) {
        try {
            db.getPracticeTimer(this.getID());
            db.updatePracticeTimer((PracticeTimer) this);
        } catch (TableDoesNotContainElementException e) {
            //нет такого
            db.addPracticeTimer((PracticeTimer) this);
        }
    }

    @Override
    public void dbDelete(DatabaseManager db) {

        try {
            db.getPractice(this.getID());
            db.deletePractice((Practice) this);
        } catch (TableDoesNotContainElementException e) {
            //нет такого

        }

    }

    public static Practice getPracticeTimerFromDB(DatabaseManager DB, int id) {
        return DB.getPracticeTimer(id);
    }

    public static class Builder extends AbstractEntity {

        private String name;
        private Practice practice;
        private int duration;
        private Calendar lastTime;
        private String date;

        public Builder(DatabaseManager DB) {
            this._id = DB.getPracticeTimerMaxNumber() + 1;
        }
        public Builder(int _id) {
            this._id = _id;
        }

        public Builder addName(String name) {
            this.name = name;
            return this;
        }

        public Builder addPractice(Practice practice) {
            this.practice = practice;
            return this;
        }

        public Builder addDuration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder addDate(String date) {
            this.date = date;
            return this;
        }

        public Builder addLastTime(Calendar lastTime) {
            this.lastTime = lastTime;
            return this;
        }


        public PracticeTimer build() {
            PracticeTimer practiceTimer = new PracticeTimer(this);
            return practiceTimer;
        }

    }

}
