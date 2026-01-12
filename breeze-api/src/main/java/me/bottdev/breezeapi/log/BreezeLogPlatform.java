package me.bottdev.breezeapi.log;

public interface BreezeLogPlatform {

    void log(LogLevel level, String message, Throwable throwable);

}
