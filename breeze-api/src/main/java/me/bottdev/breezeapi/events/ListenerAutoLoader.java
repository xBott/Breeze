package me.bottdev.breezeapi.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.autoload.AutoLoader;

@Getter
@RequiredArgsConstructor
public class ListenerAutoLoader implements AutoLoader {

    private final EventBus eventBus;

    @Override
    public void load(Object object) {
        if (object instanceof Listener listener) {
            eventBus.registerListeners(listener);
        }
    }

}
