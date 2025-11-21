package me.bottdev.breezeapi.events;

import lombok.Getter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Getter
public class ListenerContainer {

    private final Map<String, ListenerWrapper> listeners = new HashMap<>();

    public boolean isEmpty() {
        return listeners.isEmpty();
    }

    public boolean isRegistered(ListenerWrapper listener) {
        return listeners.containsKey(listener.getSignature());
    }

    public void register(ListenerWrapper listener) {
        if (isRegistered(listener)) return;
        listeners.put(listener.getSignature(), listener);
    }

    public void unregister(ListenerWrapper listener) {
        if (!isRegistered(listener)) return;
        listeners.remove(listener.getSignature());
    }

    public Collection<ListenerWrapper> get() {
        return listeners.values();
    }

}
