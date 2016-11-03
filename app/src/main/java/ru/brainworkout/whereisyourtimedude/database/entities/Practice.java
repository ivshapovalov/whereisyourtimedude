package ru.brainworkout.whereisyourtimedude.database.entities;

import ru.brainworkout.whereisyourtimedude.database.interfaces.DeletingFromDb;
import ru.brainworkout.whereisyourtimedude.database.interfaces.SavingIntoDB;
import ru.brainworkout.whereisyourtimedude.database.manager.SQLiteDatabaseManager;

public class Practice extends AbstractEntityMultiUser implements SavingIntoDB, DeletingFromDb {

    private String name;
    private Project project;
    private int is_active;

    private Practice(Builder builder) {

        this.id = builder.id;
        this.name = builder.name;
        this.project = builder.project;
        this.is_active = builder.is_active;

    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project =project;
    }

    public int getIsActive() {
        return is_active;
    }

    public void setIsActive(int is_active) {
        this.is_active = is_active;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void dbSave(SQLiteDatabaseManager db) {
            synchronized (this) {
                if (db.containsPractice(this.getId())) {
                    db.updatePractice(this);
                } else {
                    db.addPractice(this);
                }
            }
    }

    @Override
    public void dbDelete(SQLiteDatabaseManager db) {
        if (db.containsPractice(this.getId())) {
            db.deletePractice(this);
        } else {
            db.addPractice(this);
        }

    }

    public static Practice getPracticeFromDB(SQLiteDatabaseManager DB, int id) {
        return DB.getPractice(id);
    }

    public static class Builder extends AbstractEntity {

        private String name;
        private Project project;
        private int is_active;

        public Builder(SQLiteDatabaseManager DB) {
            this.id = DB.getPracticeMaxNumber() + 1;
        }

        public Builder(int id) {
            this.id = id;
        }

        public Builder addName(String name) {
            this.name = name;
            return this;
        }

        public Builder addIDProject(Project project) {
            this.project = project;
            return this;
        }

        public Builder addIsActive(int is_active) {
            this.is_active = is_active;
            return this;
        }

        public Practice build() {
            Practice practice = new Practice(this);
            return practice;
        }
    }
}

