package me.bottdev.breezeapi.resource.watcher.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.events.Event;
import me.bottdev.breezeapi.resource.watcher.ResourceWatchSubject;
import me.bottdev.breezeapi.resource.watcher.WatchEventType;

@Getter
@RequiredArgsConstructor
public class ResourceWatchEvent implements Event {

    private final WatchEventType type;
    private final ResourceWatchSubject<?> watchSubject;

}
