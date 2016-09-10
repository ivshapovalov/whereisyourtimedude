package ru.brainworkout.whereisyourtimedude.database.entities;

import ru.brainworkout.whereisyourtimedude.database.interfaces.DeletingFromDb;
import ru.brainworkout.whereisyourtimedude.database.interfaces.SavingIntoDB;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

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
    public void dbSave(DatabaseManager db) {

        if (db.containsProject(this.getID())) {
            db.updateProject(this);
        } else {
            db.addProject(this);
        }

    }

    @Override
    public void dbDelete(DatabaseManager db) {

        if (db.containsProject(this.getID())) {
            db.deleteProject(this);
        } else {
            db.addProject(this);
        }

    }

    public static Project getProjectFromDB(DatabaseManager DB, int id) {
        return DB.getProject(id);
    }

    public static class Builder extends AbstractEntity {

        private int id_area;
        private String name;


        public Builder(DatabaseManager DB) {
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

