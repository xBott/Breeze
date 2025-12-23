package me.bottdev.breezeapi.resource.watcher;

import me.bottdev.breezeapi.lifecycle.LifecycleBuilder;

public class ResourceWatcherBuilder implements LifecycleBuilder<ResourceWatcher> {

    @Override
    public ResourceWatcher build() {
        try {
            return new ResourceWatcher();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
