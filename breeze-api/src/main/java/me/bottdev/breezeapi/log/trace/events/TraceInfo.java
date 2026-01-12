package me.bottdev.breezeapi.log.trace.events;

import me.bottdev.breezeapi.log.trace.TraceEvent;

public record TraceInfo(String name, int depth, long timestamp, String message, Object... args)
        implements TraceEvent {

    boolean hasArgs() {
        return args.length > 0;
    }

}
