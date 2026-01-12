package me.bottdev.breezeapi.log.trace.events;

import me.bottdev.breezeapi.log.trace.TraceEvent;

public record TraceError(String name, int depth, long timestamp, Throwable error)
        implements TraceEvent {}
