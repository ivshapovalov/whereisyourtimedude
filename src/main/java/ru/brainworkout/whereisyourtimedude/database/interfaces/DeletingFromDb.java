package ru.brainworkout.whereisyourtimedude.database.interfaces;

import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;

public interface DeletingFromDb {

    void dbDelete(DatabaseManager db);
}
