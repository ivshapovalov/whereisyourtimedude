package ru.brainworkout.whereisyourtimedude.database.entities;

import ru.brainworkout.whereisyourtimedude.common.Session;

public abstract class AbstractEntityMultiUser extends AbstractEntity {

    protected User user;

    public AbstractEntityMultiUser() {

        User currentUser= Session.sessionCurrentUser;
        if (currentUser!=null) {
            this.user = currentUser;
        } else {
            throw new NullPointerException("Current user is not defined!");
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

}
