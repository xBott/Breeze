package me.bottdev.breezeapi.resource.config.validation;

import lombok.Getter;

public enum ConfigStatus {

    SUCCESS("Successfully validated configuration."),
    ERROR("Error. Could not validate configuration.");

    @Getter
    private final String message;

    ConfigStatus(String message) {
        this.message = message;
    }

}
