package me.bottdev.breezeapi.log.trace;

public interface TraceEvent {

    String name();
    int depth();
    long timestamp();

}

