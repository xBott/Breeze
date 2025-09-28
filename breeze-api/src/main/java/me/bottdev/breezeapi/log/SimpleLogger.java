package me.bottdev.breezeapi.log;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class SimpleLogger implements BreezeLogger {

    @Getter
    private final String name;

    private final Logger logger;

    public SimpleLogger(String name) {
        this.name = name;
        this.logger = LoggerFactory.getLogger(name);
    }

    private String format(String message) {
        return "[" + name + "] " + message;
    }

    @Override
    public void info(String message) {
        logger.info(format(message));
    }

    @Override
    public void info(String message, Object... args) {
        logger.info(format(message), args);
    }

    @Override
    public void warn(String message) {
        logger.warn(format(message));
    }

    @Override
    public void warn(String message, Object... args) {
        logger.warn(format(message), args);
    }

    @Override
    public void error(String message, Throwable throwable) {
        logger.error(format(message), throwable);
    }
}
