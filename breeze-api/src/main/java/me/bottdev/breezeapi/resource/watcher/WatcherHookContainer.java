package me.bottdev.breezeapi.resource.watcher;

import me.bottdev.breezeapi.resource.types.FileResource;

import java.util.ArrayList;
import java.util.List;

public class WatcherHookContainer {

    private final List<ResourceWatcherHook>  hooks =  new ArrayList<>();

    public void add(ResourceWatcherHook hook) {
        hooks.add(hook);
    }

    public void acceptAll(FileResource resource) {
        hooks.forEach(hook -> hook.accept(resource));
    }

}
