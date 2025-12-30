package me.bottdev.breezeapi.log.types;

import lombok.Getter;
import lombok.Setter;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.LogLevel;
import me.bottdev.breezeapi.log.colour.ColoredLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleLogger implements BreezeLogger, ColoredLogger {

    @Getter
    @Setter
    private LogLevel logLevel;
    @Getter
    private final String name;

    private final Logger logger;

    public SimpleLogger(String name, LogLevel logLevel) {
        this.name = name;
        this.logLevel = logLevel;
        this.logger = LoggerFactory.getLogger(name);
    }

    public SimpleLogger(String name) {
        this.name = name;
        this.logLevel = LogLevel.INFO;
        this.logger = LoggerFactory.getLogger(name);
    }

    private String format(String message) {
        return applyColors(message);
    }

    @Override
    public void info(String message) {
        if (!isEnabled(LogLevel.INFO)) return;
        logger.info(format(message));
    }

    @Override
    public void info(String message, Object... args) {
        if (!isEnabled(LogLevel.INFO)) return;
        logger.info(format(replaceArguments(message, args)));
    }

    @Override
    public void warn(String message) {
        if (!isEnabled(LogLevel.WARN)) return;
        logger.warn(format("<yellow>[!] " + message));
    }

    @Override
    public void warn(String message, Object... args) {
        if (!isEnabled(LogLevel.WARN)) return;
        logger.warn(format("<yellow>[!] " + replaceArguments(message, args)));
    }

    @Override
    public void error(String message, Throwable throwable) {
        if (!isEnabled(LogLevel.ERROR)) return;
        logger.error(format("<red>[×] " + message), throwable);
    }

    @Override
    public void error(String message, Throwable throwable, Object... args) {
        if (!isEnabled(LogLevel.ERROR)) return;
        logger.error(format("<red>[×] " + replaceArguments(message, args)), throwable);
    }

    @Override
    public void debug(String message) {
        if (!isEnabled(LogLevel.DEBUG)) return;
        logger.debug(format("[~] " + message));
    }

    @Override
    public void debug(String message, Object... args) {
        if (!isEnabled(LogLevel.DEBUG)) return;
        logger.debug(format("[~] " + replaceArguments(message, args)));
    }

}
