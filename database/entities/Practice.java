package ru.brainworkout.whereisyourtimedude.database.entities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.brainworkout.whereisyourtimedude.database.interfaces.DeletingFromDb;
import ru.brainworkout.whereisyourtimedude.database.interfaces.SavingIntoDB;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

import static ru.brainworkout.whereisyourtimedude.common.Common.ConvertStringToDate;
import static ru.brainworkout.whereisyourtimedude.common.Common.DATE_FORMAT_STRING;

public class Practice extends AbstractEntityMultiUser implements SavingIntoDB,DeletingFromDb{

    private String _name;
    private Project _project;
    private int _is_active;

    private Practice(Builder builder) {

        this._id = builder._id;
        this._name = builder._name;
        this._project=builder._project;

    }

    public Project getProject() {
        return _project;
    }

    public int getProjectID() {
        return _project!=null?_project.getID():0;
    }

    public void setProject(Project _project) {
        this._project = _project;
    }

    public int getIsActive() {
        return _is_active;
    }

    public void setIsActive(int _is_active) {
        this._is_active = _is_active;
    }

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    @Override
    public void dbSave(DatabaseManager db) {
        try {
            db.getPractice(this.getID());
            db.updatePractice((Practice) this);
        } catch (TableDoesNotContainElementException e) {
            //нет такого
            db.addPractice((Practice) this);
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

    public static Practice getPracticeFromDB(DatabaseManager DB, int id) {
        return DB.getPractice(id);
    }

    public static class Builder extends AbstractEntity {

        private String _name;
        private Project _project;
        private int _is_active;

        public Builder(DatabaseManager DB) {
            this._id = DB.getPracticeMaxNumber() + 1;
        }
        public Builder(int _id) {
            this._id = _id;
        }

        public Builder addName(String name) {
            this._name = name;
            return this;
        }

        public Builder addProject(Project _project) {
            this._project = _project;
            return this;
        }

        public Builder setIsActive(int _is_active) {
            this._is_active = _is_active;
            return this;
        }

        public Practice build() {
            Practice practice = new Practice(this);
            return practice;
        }

    }


}

