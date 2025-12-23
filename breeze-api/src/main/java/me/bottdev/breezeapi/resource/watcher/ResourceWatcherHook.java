package me.bottdev.breezeapi.resource.watcher;

import me.bottdev.breezeapi.resource.types.FileResource;

@FunctionalInterface
public interface ResourceWatcherHook {

    void accept(FileResource resource);

}
