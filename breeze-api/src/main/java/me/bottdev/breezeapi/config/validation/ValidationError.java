package me.bottdev.breezeapi.config.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ValidationError {

    public static ValidationError nullError() {
        return new ValidationError("Value of field is null");
    }

    private final String message;
}
