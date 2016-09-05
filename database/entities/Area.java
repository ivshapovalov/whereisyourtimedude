package ru.brainworkout.whereisyourtimedude.database.entities;

import ru.brainworkout.whereisyourtimedude.database.interfaces.DeletingFromDb;
import ru.brainworkout.whereisyourtimedude.database.interfaces.SavingIntoDB;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

public class Area extends AbstractEntityMultiUser implements SavingIntoDB,DeletingFromDb {

    private String _name;
    private int color;


    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public Area(int color, String name) {

        this.color = color;
        this._name = name;
    }


    private Area(Builder builder) {

        this._id = builder._id;
        this._name = builder._name;
    }

    @Override
    public void dbSave(DatabaseManager db) {
        try {
            db.getArea(this.getID());
            db.updateArea((Area) this);
        } catch (TableDoesNotContainElementException e) {
            //нет такого
            db.addArea((Area) this);
        }
    }

    @Override
    public void dbDelete(DatabaseManager db) {

            try {
                db.getArea(this.getID());
                db.deleteArea((Area) this);
            } catch (TableDoesNotContainElementException e) {
                //нет такого

            }

    }

    public static Area getAreaFromDB(DatabaseManager DB, int id) {
        return DB.getArea(id);
    }

    public static class Builder extends AbstractEntity {

        private String _name;
        private int _color;

        public Builder(DatabaseManager DB) {
            this._id = DB.getAreaMaxNumber() + 1;
        }
        public Builder(int _id) {
            this._id = _id;
        }

        public Builder addName(String _name) {
            this._name = _name;
            return this;
        }

        public Builder addColor(int _color) {
            this._color = _color;
            return this;
        }

        public Area build() {
            Area area = new Area(this);
            return area;
        }

    }


}

