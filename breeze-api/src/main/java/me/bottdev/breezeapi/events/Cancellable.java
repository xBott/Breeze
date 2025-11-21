package me.bottdev.breezeapi.events;

public interface Cancellable {

    boolean isCancelled();

    void setCancelled(boolean cancel);

}
