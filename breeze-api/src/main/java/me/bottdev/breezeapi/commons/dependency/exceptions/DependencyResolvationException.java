package me.bottdev.breezeapi.commons.dependency.exceptions;

public class DependencyResolvationException extends RuntimeException {
    public DependencyResolvationException(String message) {
        super(message);
    }

    public DependencyResolvationException(String message, Throwable cause) {
        super(message, cause);
    }

}
