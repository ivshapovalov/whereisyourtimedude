package ru.brainworkout.whereisyourtimedude.database.interfaces;

import ru.brainworkout.whereisyourtimedude.database.manager.SqlLiteDatabaseManager;

public interface DeletingFromDb {

    void dbDelete(SqlLiteDatabaseManager db);
}
