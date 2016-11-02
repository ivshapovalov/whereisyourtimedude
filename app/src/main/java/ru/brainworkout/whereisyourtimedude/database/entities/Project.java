package ru.brainworkout.whereisyourtimedude.database.entities;

import ru.brainworkout.whereisyourtimedude.database.interfaces.DeletingFromDb;
import ru.brainworkout.whereisyourtimedude.database.interfaces.SavingIntoDB;
import ru.brainworkout.whereisyourtimedude.database.manager.SqlLiteDatabaseManager;

public class Project extends AbstractEntityMultiUser implements SavingIntoDB, DeletingFromDb {

    private String name;
    private int id_area;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Project(String name) {

        this.name = name;
    }

    public int getIdArea() {
        return id_area;
    }

    public void setIdArea(int id_area) {
        this.id_area = id_area;
    }

    private Project(Builder builder) {

        this.id = builder.id;
        this.name = builder.name;
        this.id_area = builder.id_area;
    }

    @Override
    public void dbSave(SqlLiteDatabaseManager db) {

        if (db.containsProject(this.getId())) {
            db.updateProject(this);
        } else {
            db.addProject(this);
        }

    }

    @Override
    public void dbDelete(SqlLiteDatabaseManager db) {

        if (db.containsProject(this.getId())) {
            db.deleteProject(this);
        } else {
            db.addProject(this);
        }

    }

    public static Project getProjectFromDB(SqlLiteDatabaseManager DB, int id) {
        return DB.getProject(id);
    }

    public static class Builder extends AbstractEntity {

        private int id_area;
        private String name;


        public Builder(SqlLiteDatabaseManager DB) {
            this.id = DB.getProjectMaxNumber() + 1;
        }

        public Builder(int id) {
            this.id = id;
        }

        public Builder addName(String name) {
            this.name = name;
            return this;
        }

        public Builder addIdArea(int id_area) {
            this.id_area = id_area;
            return this;
        }

        public Project build() {
            Project project = new Project(this);
            return project;
        }

    }


}

