package me.bottdev.breezeapi.log.trace;

import java.util.ArrayList;
import java.util.List;

public class LogTrace {

    private final List<TraceListener> listeners = new ArrayList<>();
    public void addListener(TraceListener listener) {
        listeners.add(listener);
    }

    public void call(TraceEvent event) {
        listeners.forEach(listener -> listener.onTraceEvent(event));
    }

    public TraceScope scope(String name, int depth) {
        return new TraceScope(this, name, depth);
    }

}
