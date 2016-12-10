package ru.brainworkout.whereisyourtimedude.database.entities;

public abstract class AbstractEntity {

    protected int id;

    public AbstractEntity() {

    }

    public int getId() {
        return this.id;
    }

    public void setID(int id) {
        this.id = id;
    }



}
