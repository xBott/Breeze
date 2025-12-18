package me.bottdev.breezeapi.resource.types;

import me.bottdev.breezeapi.commons.file.input.BreezeFileReader;
import me.bottdev.breezeapi.commons.file.temp.TempFile;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.SimpleTreeLogger;
import me.bottdev.breezeapi.resource.Resource;

import java.io.IOException;
import java.util.Optional;

public interface FileResource extends Resource {

    BreezeLogger logger = new SimpleTreeLogger("FileResource");

    TempFile getTempFile();

    default String getName() {
        return getTempFile().getAbsolutePath().getFileName().toString();
    }

    default Optional<String> read() {
        try {
            String content = BreezeFileReader.INSTANCE.readLines(getTempFile().toFile());
            return Optional.ofNullable(content);

        } catch (IOException ex) {
            logger.error("Could not read file", ex);
        }
        return Optional.empty();
    }

}
