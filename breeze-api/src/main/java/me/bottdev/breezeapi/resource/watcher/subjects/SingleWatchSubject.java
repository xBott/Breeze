package me.bottdev.breezeapi.resource.watcher.subjects;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.resource.types.FileResource;
import me.bottdev.breezeapi.resource.watcher.ResourceWatchSubject;

import java.nio.file.Path;
import java.util.Optional;

@Getter
@RequiredArgsConstructor
public class SingleWatchSubject implements ResourceWatchSubject<FileResource> {

    private final FileResource target;
    private final String eventId;

    @Override
    public Optional<Path> getPath() {
        return target.getTempFile().getSourcePath().map(Path::getParent);
    }

    @Override
    public Optional<String> getRegistrationKey() {
        return target.getTempFile().getSourcePath().map(Path::toString);
    }

}
