package ru.brainworkout.whereisyourtimedude.database.entities;

import ru.brainworkout.whereisyourtimedude.database.interfaces.DeletingFromDb;
import ru.brainworkout.whereisyourtimedude.database.interfaces.SavingIntoDB;
import ru.brainworkout.whereisyourtimedude.database.manager.DatabaseManager;

public class Options extends AbstractEntityMultiUser implements SavingIntoDB,DeletingFromDb {

    private int recoverySwitch;
    private int displaySwitch;
    private int saveInterval;
    private int chronoIsWorking;

    public int getRecoverySwitch() {
        return recoverySwitch;
    }

    public void setRecoverySwitch(int recoverySwitch) {
        this.recoverySwitch = recoverySwitch;
    }

    public int getDisplaySwitch() {
        return displaySwitch;
    }

    public void setDisplaySwitch(int displaySwitch) {
        this.displaySwitch = displaySwitch;
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
        this.recoverySwitch=builder.recoverySwitch;
        this.displaySwitch=builder.displaySwitch;
        this.saveInterval=builder.saveInterval;
        this.chronoIsWorking=builder.chronoIsWorking;

    }

    @Override
    public void dbSave(DatabaseManager db) {
        if (db.containsOptions(this.getID())) {
            db.updateOptions(this);
        } else {
            db.addOptions(this);
        }
    }

    @Override
    public void dbDelete(DatabaseManager db) {
        if (db.containsOptions(this.getID())) {
            db.deleteOptions(this);
        } else {
            db.addOptions(this);
        }

    }

    public static Options getOptionsFromDB(DatabaseManager DB, int id) {
        return DB.getOptions(id);
    }

    public static class Builder extends AbstractEntity {

        private int recoverySwitch;
        private int displaySwitch;
        private int saveInterval;
        private int chronoIsWorking;


        public Builder(DatabaseManager DB) {
            this.id = DB.getOptionsMaxNumber() + 1;
        }
        public Builder(int id) {
            this.id = id;
        }


        public Builder addRecoverySwitch(int recoverySwitch) {
            this.recoverySwitch = recoverySwitch;
            return this;
        }
        public Builder addDisplaySwitch(int displaySwitch) {
            this.displaySwitch = displaySwitch;
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

