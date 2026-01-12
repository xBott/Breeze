package me.bottdev.breezeapi.log;

import me.bottdev.breezeapi.log.trace.TraceEvent;
import me.bottdev.breezeapi.log.trace.TraceListener;
import me.bottdev.breezeapi.log.trace.events.*;

import java.util.Arrays;

public interface BreezeLogger extends TraceListener {

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

    @Override
    default void onTraceEvent(TraceEvent event) {

        String indent = "   ".repeat(event.depth());

        switch (event) {
            case TraceStart start -> info(indent + start.name() + ":");
            case TraceEnd end -> info(indent + end.name() + "<green> (" + end.durationMs() + "ms)");
            case TraceInfo info -> info(indent + info.message(), info.args());
            case TraceWarn warn -> warn(indent + warn.message(), warn.args());
            case TraceError error -> error(indent + error.name() + " faced an Error", error.error());
            default -> {}
        }

    }

}
