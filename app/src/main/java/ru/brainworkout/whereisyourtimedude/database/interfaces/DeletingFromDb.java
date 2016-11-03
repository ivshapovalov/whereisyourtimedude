package ru.brainworkout.whereisyourtimedude.database.interfaces;

import ru.brainworkout.whereisyourtimedude.database.manager.SQLiteDatabaseManager;

public interface DeletingFromDb {

    void dbDelete(SQLiteDatabaseManager db);
}
