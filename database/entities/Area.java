package ru.brainworkout.whereisyourtimedude.database.entities;

import ru.brainworkout.whereisyourtimedude.database.interfaces.DeletingFromDb;
import ru.brainworkout.whereisyourtimedude.database.interfaces.SavingIntoDB;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

public class Area  {

//    private String _name;
//
//    private Area(Builder builder) {
//
//        this._id = builder._id;
//        this._name = builder._name;
//    }
//
//    public String get_name() {
//        return _name;
//    }
//
//    public void set_name(String _name) {
//        this._name = _name;
//    }
//
//    @Override
//    public void dbSave(DatabaseManager db) {
//        try {
//            db.getPractice(this.getID());
//            db.updatePractice((Area) this);
//        } catch (TableDoesNotContainElementException e) {
//            //нет такого
//            db.addPractice((Area) this);
//        }
//    }
//
//    @Override
//    public void dbDelete(DatabaseManager db) {
//
//            try {
//                db.getPractice(this.getID());
//                db.deletePractice((Area) this);
//            } catch (TableDoesNotContainElementException e) {
//                //нет такого
//
//            }
//
//    }
//
//    public static Area getPracticeFromDB(DatabaseManager DB, int id) {
//        return DB.getPractice(id);
//    }
//
//    public static class Builder extends AbstractEntity {
//
//        private String _name;
//
//        public Builder(DatabaseManager DB) {
//            this._id = DB.getPracticeMaxNumber() + 1;
//        }
//        public Builder(int _id) {
//            this._id = _id;
//        }
//
//        public Builder addName(String name) {
//            this._name = name;
//            return this;
//        }
//
//        public Area build() {
//            Area practice = new Area(this);
//            return practice;
//        }
//
//    }


}

