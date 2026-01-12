package me.bottdev.breezeapi.log.trace.events;

import me.bottdev.breezeapi.log.trace.TraceEvent;

public record TraceEnd(String name, int depth, long timestamp, long durationMs)
        implements TraceEvent {}
