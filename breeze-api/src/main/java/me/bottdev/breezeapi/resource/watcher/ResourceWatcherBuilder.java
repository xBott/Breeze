package me.bottdev.breezeapi.resource.watcher;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.events.EventBus;
import me.bottdev.breezeapi.lifecycle.LifecycleBuilder;

@RequiredArgsConstructor
public class ResourceWatcherBuilder implements LifecycleBuilder<ResourceWatcher> {

    private final EventBus eventBus;

    @Override
    public ResourceWatcher build() {
        try {
            return new ResourceWatcher(eventBus);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
