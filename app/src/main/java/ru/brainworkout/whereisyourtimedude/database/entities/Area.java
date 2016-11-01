package ru.brainworkout.whereisyourtimedude.database.entities;

import ru.brainworkout.whereisyourtimedude.database.interfaces.DeletingFromDb;
import ru.brainworkout.whereisyourtimedude.database.interfaces.SavingIntoDB;
import ru.brainworkout.whereisyourtimedude.database.manager.SqlLiteDatabaseManager;

public class Area extends AbstractEntityMultiUser implements SavingIntoDB,DeletingFromDb {

    private String name;
    private int color;


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


    private Area(Builder builder) {

        this.id = builder.id;
        this.name = builder.name;
        this.color=builder.color;
    }

    @Override
    public void dbSave(SqlLiteDatabaseManager db) {
        if (db.containsArea(this.getID())) {
            db.updateArea(this);
        } else {
            db.addArea(this);
        }
    }

    @Override
    public void dbDelete(SqlLiteDatabaseManager db) {
        if (db.containsArea(this.getID())) {
            db.deleteArea(this);
        } else {
            db.addArea(this);
        }

    }

    public static Area getAreaFromDB(SqlLiteDatabaseManager DB, int id) {
        return DB.getArea(id);
    }

    public static class Builder extends AbstractEntity {

        private String name;
        private int color;

        public Builder(SqlLiteDatabaseManager DB) {
            this.id = DB.getAreaMaxNumber() + 1;
        }
        public Builder(int id) {
            this.id = id;
        }

        public Builder addName(String name) {
            this.name = name;
            return this;
        }

        public Builder addColor(int color) {
            this.color = color;
            return this;
        }

        public Area build() {
            Area area = new Area(this);
            return area;
        }

    }


}

