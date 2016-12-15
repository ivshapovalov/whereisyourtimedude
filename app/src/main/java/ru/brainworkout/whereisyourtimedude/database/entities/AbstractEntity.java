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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractEntity that = (AbstractEntity) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return id;
    }
}
