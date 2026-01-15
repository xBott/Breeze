package me.bottdev.breezeapi.di.exceptions;

public class ContextReadException extends RuntimeException {
    public ContextReadException(String message) {
        super(message);
    }

    public ContextReadException(String message, Throwable cause) {
        super(message, cause);
    }

}
