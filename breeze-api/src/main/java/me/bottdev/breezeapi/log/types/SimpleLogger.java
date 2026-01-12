package me.bottdev.breezeapi.log.types;

import lombok.Getter;
import lombok.Setter;
import me.bottdev.breezeapi.log.BreezeLogPlatform;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.LogLevel;
import me.bottdev.breezeapi.log.colour.ColoredLogger;

@Getter
public class SimpleLogger implements BreezeLogger, ColoredLogger {

    private final BreezeLogPlatform platform;
    @Setter
    private LogLevel logLevel;
    private final String name;

    public SimpleLogger(BreezeLogPlatform platform, String name, LogLevel logLevel) {
        this.platform = platform;
        this.name = name;
        this.logLevel = logLevel;
    }

    private String format(String message) {
        return applyColors("[%s] %s".formatted(name, message));
    }

    @Override
    public void info(String message) {
        if (!isEnabled(LogLevel.INFO)) return;

        String formatted = format(" " + message);

        platform.log(LogLevel.INFO, formatted, null);
    }

    @Override
    public void info(String message, Object... args) {
        if (!isEnabled(LogLevel.INFO)) return;

        String withArguments = replaceArguments(message, args);
        String formatted = format(" " + withArguments);

        platform.log(LogLevel.INFO, formatted, null);
    }

    @Override
    public void warn(String message) {
        if (!isEnabled(LogLevel.WARN)) return;

        String formatted = format("<yellow> " + message);

        platform.log(LogLevel.WARN, formatted, null);
    }

    @Override
    public void warn(String message, Object... args) {
        if (!isEnabled(LogLevel.WARN)) return;

        String withArguments = replaceArguments(message, args);
        String formatted = format("<yellow> " + withArguments);

        platform.log(LogLevel.WARN, formatted, null);
    }

    @Override
    public void error(String message, Throwable throwable) {
        if (!isEnabled(LogLevel.ERROR)) return;

        String formatted = format("<red>" + message);

        platform.log(LogLevel.ERROR, formatted, throwable);
    }

    @Override
    public void error(String message, Throwable throwable, Object... args) {
        if (!isEnabled(LogLevel.ERROR)) return;

        String withArguments = replaceArguments(message, args);
        String formatted = format("<red>" +withArguments);

        platform.log(LogLevel.ERROR, formatted, throwable);
    }

    @Override
    public void debug(String message) {
        if (!isEnabled(LogLevel.DEBUG)) return;

        String formatted = format(message);

        platform.log(LogLevel.DEBUG, formatted, null);
    }

    @Override
    public void debug(String message, Object... args) {
        if (!isEnabled(LogLevel.DEBUG)) return;

        String withArguments = replaceArguments(message, args);
        String formatted = format(withArguments);

        platform.log(LogLevel.DEBUG, formatted, null);
    }

}
