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

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void info (String message, Object... args) {
        logger.info(message, args);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    @Override
    public void warn(String message, Object... args) {
        logger.warn(message, args);
    }

    @Override
    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }

}
