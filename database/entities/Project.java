package ru.brainworkout.whereisyourtimedude.database.entities;

import ru.brainworkout.whereisyourtimedude.database.interfaces.DeletingFromDb;
import ru.brainworkout.whereisyourtimedude.database.interfaces.SavingIntoDB;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

public class Project extends AbstractEntityMultiUser implements SavingIntoDB,DeletingFromDb {

    private String _name;
    private Area _area;

    public String getName() {
        return _name;
    }

    public void setName(String _name) {
        this._name = _name;
    }

    public Project(String name) {

        this._name = name;
    }
    public Area getArea() {
        return _area;
    }

    public int getAreaID() {
        return _area!=null?_area.getID():0;
    }

    public void setArea(Area _area) {
        this._area = _area;
    }

    private Project(Builder builder) {

        this._id = builder._id;
        this._name = builder._name;
        this._area=builder._area;
    }

    @Override
    public void dbSave(DatabaseManager db) {
        try {
            db.getProject(this.getID());
            db.updateProject((Project) this);
        } catch (TableDoesNotContainElementException e) {
            //нет такого
            db.addProject((Project) this);
        }
    }

    @Override
    public void dbDelete(DatabaseManager db) {

            try {
                db.getProject(this.getID());
                db.deleteProject((Project) this);
            } catch (TableDoesNotContainElementException e) {
                //нет такого

            }

    }

    public static Project getProjectFromDB(DatabaseManager DB, int id) {
        return DB.getProject(id);
    }

    public static class Builder extends AbstractEntity {

        private Area _area;
        private String _name;


        public Builder(DatabaseManager DB) {
            this._id = DB.getProjectMaxNumber() + 1;
        }
        public Builder(int _id) {
            this._id = _id;
        }

        public Builder addName(String _name) {
            this._name = _name;
            return this;
        }

        public Builder addArea(Area _area) {
            this._area = _area;
            return this;
        }

        public Project build() {
            Project project = new Project(this);
            return project;
        }

    }


}

