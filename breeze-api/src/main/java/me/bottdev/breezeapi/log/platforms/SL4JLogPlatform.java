package me.bottdev.breezeapi.log.platforms;

import lombok.Getter;
import me.bottdev.breezeapi.log.BreezeLogPlatform;
import me.bottdev.breezeapi.log.BreezeLoggerFactory;
import me.bottdev.breezeapi.log.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SL4JLogPlatform implements BreezeLogPlatform {

    @Getter
    private static final BreezeLoggerFactory factory = new BreezeLoggerFactory(new SL4JLogPlatform());

    private final Logger slf4jLogger = LoggerFactory.getLogger(SL4JLogPlatform.class);

    @Override
    public void log(LogLevel level, String message, Throwable throwable) {
        switch (level) {
            case INFO:
                slf4jLogger.info(message);
                break;
            case WARN:
                slf4jLogger.warn(message);
                break;
            case DEBUG:
                slf4jLogger.debug(message);
                break;
            case ERROR:
                slf4jLogger.error(message, throwable);
                break;
        }
    }

}
