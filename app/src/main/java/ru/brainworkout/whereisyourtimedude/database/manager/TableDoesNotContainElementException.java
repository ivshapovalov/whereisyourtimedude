package ru.brainworkout.whereisyourtimedude.database.manager;

public class TableDoesNotContainElementException extends RuntimeException {

    public TableDoesNotContainElementException(String detailMessage) {
        super(detailMessage);
    }

}
