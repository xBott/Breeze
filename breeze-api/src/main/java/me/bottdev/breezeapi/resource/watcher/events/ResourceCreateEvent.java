package me.bottdev.breezeapi.resource.watcher.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.events.Event;
import me.bottdev.breezeapi.resource.watcher.ResourceWatchSubject;

import java.nio.file.Path;

@Getter
@RequiredArgsConstructor
public class ResourceCreateEvent implements Event {

    private final Path createdPath;
    private final ResourceWatchSubject<?> watchSubject;

}
