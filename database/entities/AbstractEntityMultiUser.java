package ru.brainworkout.whereisyourtimedude.database.entities;

import ru.brainworkout.whereisyourtimedude.common.Common;

public abstract class AbstractEntityMultiUser extends AbstractEntity {

    protected int _id_user;

    public AbstractEntityMultiUser() {

//        User currentUser=Common.dbCurrentUser;
//        if (currentUser!=null) {
//            this._id_user = currentUser.getID();
//        } else {
//            throw new NullPointerException("Current user is not defined!");
//        }
    }

    public int getIdUser() {
        return _id_user;
    }

    public void setIdUser(int _id_user) {
        this._id_user = _id_user;
    }

}
