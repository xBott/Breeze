package me.bottdev.breezeapi.log;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Deque;

@RequiredArgsConstructor
public class SimpleTreeLogger implements TreeLogger {

    @Getter
    @Setter
    private LogLevel logLevel;
    @Getter
    private final String name;
    @Getter
    private final Deque<String> stack = new ArrayDeque<>();

    private final Logger logger;

    public SimpleTreeLogger(String name, LogLevel logLevel) {
        this.name = name;
        this.logLevel = logLevel;
        this.logger = LoggerFactory.getLogger(name);
    }

    public SimpleTreeLogger(String name) {
        this.name = name;
        this.logLevel = LogLevel.INFO;
        this.logger = LoggerFactory.getLogger(name);
    }

    @Override
    public void logBranchTitle(String title) {
        logger.info(title);
    }

    @Override
    public void info(String message) {
        if (!isEnabled(LogLevel.INFO)) return;
        logger.info(formatTree(SPACE_4, "└─", message));
    }

    @Override
    public void info(String message, Object... args) {
        if (!isEnabled(LogLevel.INFO)) return;
        logger.info(formatTree(SPACE_4, "└─", replaceArguments(message, args)));
    }

    @Override
    public void warn(String message) {
        if (!isEnabled(LogLevel.WARN)) return;
        logger.warn(formatTree(SPACE_4, "└─<yellow>[!]", message));
    }

    @Override
    public void warn(String message, Object... args) {
        if (!isEnabled(LogLevel.WARN)) return;
        logger.warn(formatTree(SPACE_4, "└─<yellow>[!]", replaceArguments(message, args)));
    }

    @Override
    public void error(String message, Throwable throwable) {
        if (!isEnabled(LogLevel.ERROR)) return;
        logger.error(formatTree(SPACE_3, "└─<red>[×]", message), throwable);
    }

    @Override
    public void error(String message, Throwable throwable, Object... args) {
        if (!isEnabled(LogLevel.ERROR)) return;
        logger.error(formatTree(SPACE_3, "└─<red>[×]", replaceArguments(message, args)), throwable);
    }

    @Override
    public void debug(String message) {
        if (!isEnabled(LogLevel.DEBUG)) return;
        logger.debug(formatTree(SPACE_3, "└─[~]", message));
    }

    @Override
    public void debug(String message, Object... args) {
        if (!isEnabled(LogLevel.DEBUG)) return;
        logger.debug(formatTree(SPACE_3, "└─[~]", replaceArguments(message, args)));
    }

}
