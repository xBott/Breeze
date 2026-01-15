package me.bottdev.breezeapi.di.exceptions;

public class ContextInjectionException extends RuntimeException {
    public ContextInjectionException(String message) {
        super(message);
    }

    public ContextInjectionException(String message, Throwable cause) {
        super(message, cause);
    }

}
