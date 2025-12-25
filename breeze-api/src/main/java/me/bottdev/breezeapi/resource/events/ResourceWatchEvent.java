package me.bottdev.breezeapi.resource.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.events.Event;
import me.bottdev.breezeapi.resource.watcher.ResourceWatchSubject;

@Getter
@RequiredArgsConstructor
public class ResourceWatchEvent implements Event {

    private final ResourceWatchSubject watchSubject;

}
