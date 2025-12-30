package me.bottdev.breezeapi.resource.watcher.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.events.Event;
import me.bottdev.breezeapi.resource.watcher.ResourceWatchSubject;

import java.nio.file.Path;

@Getter
@RequiredArgsConstructor
public class ResourceDeleteEvent implements Event {

    private final Path deletedPath;
    private final ResourceWatchSubject<?> watchSubject;

}
