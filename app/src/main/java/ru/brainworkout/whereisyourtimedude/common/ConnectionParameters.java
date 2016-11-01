package ru.brainworkout.whereisyourtimedude.common;


public class ConnectionParameters {

    private String transmitterActivityName;
    private String receiverActivityName;
    private boolean isTransmitterNew;
    private boolean isReceiverNew;
    private boolean isTransmitterForChoice;
    private boolean isReceiverForChoice;

    public ConnectionParameters(Builder builder) {
        this.transmitterActivityName = builder.transmitterActivityName;
        this.receiverActivityName = builder.receiverActivityName;
        this.isTransmitterNew = builder.isTransmitterNew;
        this.isReceiverNew = builder.isReceiverNew;
        this.isTransmitterForChoice = builder.isTransmitterForChoice;
        this.isReceiverForChoice = builder.isReceiverForChoice;
    }

    public String getTransmitterActivityName() {
        return transmitterActivityName;
    }

    public boolean isReceiverForChoice() {
        return isReceiverForChoice;
    }

    public boolean isTransmitterForChoice() {
        return isTransmitterForChoice;
    }

    public boolean isReceiverNew() {
        return isReceiverNew;
    }

    public boolean isTransmitterNew() {
        return isTransmitterNew;
    }

    public String getReceiverActivityName() {
        return receiverActivityName;
    }

    public static class Builder {
        private String transmitterActivityName;
        private String receiverActivityName;
        private boolean isTransmitterNew;
        private boolean isReceiverNew;
        private boolean isTransmitterForChoice;
        private boolean isReceiverForChoice;

        public Builder() {
        }

        public Builder addTransmitterActivityName(String name) {
            this.transmitterActivityName = name;
            return this;
        }

        public Builder addReceiverActivityName(String name) {
            this.receiverActivityName = name;
            return this;
        }
        public Builder isTransmitterNew(Boolean isNew) {
            this.isTransmitterNew = isNew;
            return this;
        }
        public Builder isReceiverNew(Boolean isNew) {
            this.isReceiverNew = isNew;
            return this;
        }
        public Builder isTransmitterForChoice(Boolean forChoice) {
            this.isTransmitterForChoice = forChoice;
            return this;
        }
        public Builder isReceiverForChoice(Boolean forChoice) {
            this.isReceiverForChoice = forChoice;
            return this;
        }

        public ConnectionParameters build() {
            ConnectionParameters connectionParameters= new ConnectionParameters(this);
            return connectionParameters;
        }

    }
}


