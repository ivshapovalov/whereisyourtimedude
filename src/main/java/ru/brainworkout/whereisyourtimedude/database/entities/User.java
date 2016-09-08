package ru.brainworkout.whereisyourtimedude.database.entities;


import ru.brainworkout.whereisyourtimedude.database.interfaces.DeletingFromDb;
import ru.brainworkout.whereisyourtimedude.database.interfaces.SavingIntoDB;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;
import ru.brainworkout.whereisyourtimedude.database.manager.TableDoesNotContainElementException;

public class User extends AbstractEntity implements SavingIntoDB,DeletingFromDb {
    private int id;
    private String name;
    private int isCurrentUser;

    private User(Builder builder) {

        this.id = builder.id;
        this.name = builder.name;
        this.isCurrentUser = builder.isCurrentUser;

    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int isCurrentUser() {
        return isCurrentUser;
    }

    public void setIsCurrentUser(int isCurrentUser) {
        this.isCurrentUser = isCurrentUser;
    }

    @Override
    public void dbSave(DatabaseManager db) {
        User user = (User) this;
        try {
            db.getUser(this.getID());
            db.updateUser(user);

        } catch (TableDoesNotContainElementException e) {
            //нет такого
            db.addUser(user);

        }
    }

    @Override
    public void dbDelete(DatabaseManager db) {
        try {
            db.getUser(this.getID());
            db.deleteUser((User) this);
        } catch (TableDoesNotContainElementException e) {
            //нет такого
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id == user.id;

    }

    @Override
    public int hashCode() {
        return id;
    }

    public static class Builder extends AbstractEntity {

        private String name;
        private int isCurrentUser;

        public Builder(int id) {
            this.id = id;
        }

        public Builder addName(String name) {
            this.name = name;
            return this;
        }

        public Builder addIsCurrentUser(int isCurrentUser) {
            this.isCurrentUser = isCurrentUser;
            return this;
        }

        public User build() {
            User user = new User(this);
            return user;
        }

    }
}