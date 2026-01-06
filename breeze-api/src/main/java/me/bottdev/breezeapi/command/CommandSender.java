package me.bottdev.breezeapi.command;

public interface CommandSender {

    void send(String message);

    void send(String message, Object... args);

}
