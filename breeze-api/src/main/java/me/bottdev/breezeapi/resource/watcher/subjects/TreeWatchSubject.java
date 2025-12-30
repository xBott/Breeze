package me.bottdev.breezeapi.resource.watcher.subjects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.commons.file.temp.TempFile;
import me.bottdev.breezeapi.resource.ResourceTree;
import me.bottdev.breezeapi.resource.types.FileResource;
import me.bottdev.breezeapi.resource.watcher.ResourceWatchSubject;

import java.nio.file.Path;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public class TreeWatchSubject implements ResourceWatchSubject<ResourceTree<? extends FileResource>> {

    private final ResourceTree<? extends FileResource> target;
    private final String eventId;

    @Override
    public Optional<Path> getPath() {
        return target.getRoot();
    }

    @Override
    public Optional<String> getRegistrationKey() {
        return getPath().map(Path::toString);
    }

}
