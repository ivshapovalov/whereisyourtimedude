package ru.brainworkout.whereisyourtimedude.database.entities;

import ru.brainworkout.whereisyourtimedude.common.Session;

public abstract class AbstractEntityMultiUser extends AbstractEntity {

    protected int id_user;

    public AbstractEntityMultiUser() {

        User currentUser= Session.sessionUser;
        if (currentUser!=null) {
            this.id_user = currentUser.getID();
        } else {
            throw new NullPointerException("Current user is not defined!");
        }
    }

    public int getIdUser() {
        return id_user;
    }

    public void setIdUser(int id_user) {
        this.id_user = id_user;
    }

}
