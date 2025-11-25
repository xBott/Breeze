package me.bottdev.breezeapi.log;

import java.util.function.Supplier;

public interface BreezeLogger {

    LogLevel getLogLevel();
    String getName();

    default boolean isEnabled(LogLevel level) {
        return level.ordinal() >= getLogLevel().ordinal();
    }

    default String replaceArguments(String message, Object... args) {
        return String.format(message.replace("{}", "%s"), args);
    }

    void info(String message);
    void info(String message, Object... args);

    void warn(String message);
    void warn(String message, Object... args);

    void error(String message, Throwable throwable);
    void error(String message, Throwable throwable, Object... args);

    void debug(String message);
    void debug(String message, Object... args);

}
