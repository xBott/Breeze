package me.bottdev.breezeapi.resource.watcher.services;

import lombok.Getter;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.types.SimpleLogger;

import java.io.IOException;
import java.nio.file.*;

public class SingleWatchService extends AbstractWatchService {

    @Getter
    private final BreezeLogger logger = new SimpleLogger("SingleWatchService");

    public SingleWatchService() throws IOException {
        super();
    }

    @Override
    public boolean register(Path path) {
        return registerWatchDirectory(path);
    }

    @Override
    public boolean unregister(Path path) {
        return unregisterWatchDirectory(path);
    }

    @Override
    public void reset(Path path) {

    }

}
