package me.bottdev.breezeapi.log.trace.events;

import me.bottdev.breezeapi.log.trace.TraceEvent;

public record TraceStart(String name, int depth, long timestamp)
        implements TraceEvent {}
