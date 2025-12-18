package me.bottdev.breezeapi.resource.config.validation;

import lombok.Getter;

public enum FieldStatus {

    SUCCESS("Successfully validated field %name%."),
    EMPTY("Error. Field %name% is empty."),
    INCORRECT_TYPE("Error. Type of field %name% is incorrect."),
    NOT_IN_RANGE("Error. Field value %name% is not in valid range."),
    ERROR("Error. Could not validate config %name%.");

    @Getter
    private final String message;

    public String getFormattedMessage(String fieldName) {
        return message.replace("%name%", fieldName);
    }

    FieldStatus(String message) {
        this.message = message;
    }

}
