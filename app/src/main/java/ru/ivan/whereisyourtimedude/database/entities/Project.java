package ru.ivan.whereisyourtimedude.database.entities;

import ru.ivan.whereisyourtimedude.database.interfaces.DeletingFromDb;
import ru.ivan.whereisyourtimedude.database.interfaces.SavingIntoDB;
import ru.ivan.whereisyourtimedude.database.manager.SQLiteDatabaseManager;

public class Project extends AbstractEntityMultiUser implements SavingIntoDB, DeletingFromDb {

    private String name;
    private Area area;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Project(String name) {

        this.name = name;
    }

    public Area getArea() {
        return area;
    }

    public int getAreaId() {
        if (area != null) {
            return area.getId();
        } else {
            return -1;
        }
    }

    public void setArea(Area area) {
        this.area = area;
    }

    private Project(Builder builder) {

        this.id = builder.id;
        this.name = builder.name;
        this.area = builder.area;
    }

    @Override
    public void dbSave(SQLiteDatabaseManager db) {
        synchronized (this) {
            if (db.containsProject(this.getId())) {
                db.updateProject(this);
            } else {
                db.addProject(this);
            }
        }
    }

    @Override
    public void dbDelete(SQLiteDatabaseManager db) {

        if (db.containsProject(this.getId())) {
            db.deleteProject(this);
        }
    }

    public static Project getProjectFromDB(SQLiteDatabaseManager DB, int id) {
        return DB.getProject(id);
    }

    public static class Builder extends AbstractEntity {

        private Area area;
        private String name;

        public Builder(SQLiteDatabaseManager DB) {
            this.id = DB.getProjectMaxNumber() + 1;
        }

        public Builder(int id) {
            this.id = id;
        }

        public Builder addName(String name) {
            this.name = name;
            return this;
        }

        public Builder addArea(Area area) {
            this.area = area;
            return this;
        }

        public Project build() {
            Project project = new Project(this);
            return project;
        }

    }


}

