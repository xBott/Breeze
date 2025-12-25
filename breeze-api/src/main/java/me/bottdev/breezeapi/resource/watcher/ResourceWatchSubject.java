package me.bottdev.breezeapi.resource.watcher;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.resource.types.FileResource;

@Getter
@RequiredArgsConstructor
public class ResourceWatchSubject {

    private final FileResource resource;
    private final String eventId;

}
