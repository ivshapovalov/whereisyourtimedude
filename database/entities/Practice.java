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

    private String name;
    private int id_project;
    private int is_active;

    private Practice(Builder builder) {

        this.id = builder.id;
        this.name = builder.name;
        this.id_project=builder.id_project;

    }

    public int getIdProject() {
        return id_project;
    }

    public void setIdProject(int id_project) {
        this.id_project = id_project;
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

        private String name;
        private int id_project;
        private int is_active;

        public Builder(DatabaseManager DB) {
            this.id = DB.getPracticeMaxNumber() + 1;
        }
        public Builder(int id) {
            this.id = id;
        }

        public Builder addName(String name) {
            this.name = name;
            return this;
        }

        public Builder addIDProject(int id_project) {
            this.id_project = id_project;
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

