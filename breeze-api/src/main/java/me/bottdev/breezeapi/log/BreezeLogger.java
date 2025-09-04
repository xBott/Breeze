package me.bottdev.breezeapi.log;

public interface BreezeLogger {

    String getName();

    void info(String message);

    void info (String message, Object... args);

    void warn(String message);

    void warn (String message, Object... args);

    void error(String message, Throwable throwable);

}
