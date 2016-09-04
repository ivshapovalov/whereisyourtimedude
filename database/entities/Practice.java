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

public class Practice extends AbstractEntityMultiUser implements SavingIntoDB,DeletingFromDb {

    private String _name;

    private Practice(Builder builder) {

        this._id = builder._id;
        this._name = builder._name;
    }

    public String get_name() {
        return _name;
    }

    public void set_name(String _name) {
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

        public Practice build() {
            Practice practice = new Practice(this);
            return practice;
        }

    }


}

