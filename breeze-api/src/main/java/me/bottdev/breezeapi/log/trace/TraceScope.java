package me.bottdev.breezeapi.log.trace;

import lombok.Getter;
import lombok.Setter;
import me.bottdev.breezeapi.log.trace.events.*;

public class TraceScope implements AutoCloseable {

    private final LogTrace trace;
    private final String name;
    private final int depth;
    private final long startTime;

    @Getter
    @Setter
    private boolean failed = false;

    public TraceScope(LogTrace trace, String name, int depth) {
        this.trace = trace;
        this.name = name;
        this.depth = depth;
        this.startTime = System.currentTimeMillis();

        trace.call(new TraceStart(name, depth, startTime));
    }

    public void info(String message) {
        trace.call(new TraceInfo(
                name,
                depth,
                System.currentTimeMillis(),
                message
        ));
    }

    public void info(String message, Object... args) {
        trace.call(new TraceInfo(
                name,
                depth,
                System.currentTimeMillis(),
                message,
                args
        ));
    }

    public void warn(String message) {
        trace.call(new TraceWarn(
                name,
                depth,
                System.currentTimeMillis(),
                message
        ));
    }

    public void warn(String message, Object... args) {
        trace.call(new TraceWarn(
                name,
                depth,
                System.currentTimeMillis(),
                message,
                args
        ));
    }

    public void error(Throwable throwable) {
        failed = true;
        trace.call(new TraceError(
                name,
                depth,
                System.currentTimeMillis(),
                throwable
        ));
    }

    public int nextDepth() {
        return depth + 1;
    }

    @Override
    public void close() {
        if (!failed) {
            long end = System.currentTimeMillis();
            trace.call(new TraceEnd(
                    name,
                    depth,
                    end,
                    end - startTime
            ));
        }
    }
}

