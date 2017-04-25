package ru.ivan.whereisyourtimedude.database.interfaces;

import ru.ivan.whereisyourtimedude.database.manager.SQLiteDatabaseManager;

public interface DeletingFromDb {

    void dbDelete(SQLiteDatabaseManager db);
}
