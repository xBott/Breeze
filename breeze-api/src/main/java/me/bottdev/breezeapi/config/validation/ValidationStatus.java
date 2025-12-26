package me.bottdev.breezeapi.config.validation;


public enum ValidationStatus {

    SUCCESS("<green>"),
    ERROR("<red>");

    private final String color;

    ValidationStatus(String color) {
        this.color = color;
    }

    public String getColored() {
        return this.color + this;
    }

}
