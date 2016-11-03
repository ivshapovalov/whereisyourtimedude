package ru.brainworkout.whereisyourtimedude.database.entities;

import ru.brainworkout.whereisyourtimedude.database.interfaces.DeletingFromDb;
import ru.brainworkout.whereisyourtimedude.database.interfaces.SavingIntoDB;
import ru.brainworkout.whereisyourtimedude.database.manager.SQLiteDatabaseManager;

public class Options extends AbstractEntityMultiUser implements SavingIntoDB, DeletingFromDb {

    private int recoveryOnRunSwitch;
    private int displayNotificationTimerSwitch;
    private int saveInterval;
    private int chronoIsWorking;

    public int getRecoveryOnRunSwitch() {
        return recoveryOnRunSwitch;
    }

    public void setRecoveryOnRunSwitch(int recoveryOnRunSwitch) {
        this.recoveryOnRunSwitch = recoveryOnRunSwitch;
    }

    public int getDisplayNotificationTimerSwitch() {
        return displayNotificationTimerSwitch;
    }

    public void setDisplayNotificationTimerSwitch(int displayNotificationTimerSwitch) {
        this.displayNotificationTimerSwitch = displayNotificationTimerSwitch;
    }

    public int getSaveInterval() {
        return saveInterval;
    }

    public void setSaveInterval(int saveInterval) {
        this.saveInterval = saveInterval;
    }

    public int getChronoIsWorking() {
        return chronoIsWorking;
    }

    public void setChronoIsWorking(int chronoIsWorking) {
        this.chronoIsWorking = chronoIsWorking;
    }

    private Options(Builder builder) {

        this.id = builder.id;
        this.recoveryOnRunSwitch = builder.recoveryOnRunSwitch;
        this.displayNotificationTimerSwitch = builder.displayNotificationTimerSwitch;
        this.saveInterval = builder.saveInterval;
        this.chronoIsWorking = builder.chronoIsWorking;

    }

    @Override
    public void dbSave(SQLiteDatabaseManager db) {
            synchronized (this) {
                if (db.containsOptions(this.getId())) {
                    db.updateOptions(this);
                } else {
                    db.addOptions(this);
                }
            }
    }

    @Override
    public void dbDelete(SQLiteDatabaseManager db) {
        if (db.containsOptions(this.getId())) {
            db.deleteOptions(this);
        } else {
            db.addOptions(this);
        }

    }

    public static Options getOptionsFromDB(SQLiteDatabaseManager DB, int id) {
        return DB.getOptions(id);
    }

    public static class Builder extends AbstractEntity {

        private int recoveryOnRunSwitch;
        private int displayNotificationTimerSwitch;
        private int saveInterval;
        private int chronoIsWorking;


        public Builder(SQLiteDatabaseManager DB) {
            this.id = DB.getOptionsMaxNumber() + 1;
        }

        public Builder(int id) {
            this.id = id;
        }


        public Builder addRecoverySwitch(int recoverySwitch) {
            this.recoveryOnRunSwitch = recoverySwitch;
            return this;
        }

        public Builder addDisplaySwitch(int displaySwitch) {
            this.displayNotificationTimerSwitch = displaySwitch;
            return this;
        }

        public Builder addSaveInterval(int saveInterval) {
            this.saveInterval = saveInterval;
            return this;
        }

        public Builder addChronoIsWorking(int chronoIsWorking) {
            this.chronoIsWorking = chronoIsWorking;
            return this;
        }

        public Options build() {
            Options options = new Options(this);
            return options;
        }

    }


}

