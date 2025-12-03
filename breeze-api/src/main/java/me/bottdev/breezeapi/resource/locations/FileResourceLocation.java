package me.bottdev.breezeapi.resource.locations;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.commons.file.FileCommons;
import me.bottdev.breezeapi.resource.ResourceLocation;

import java.nio.file.Path;

@Getter
@RequiredArgsConstructor
public class FileResourceLocation implements ResourceLocation {

    private final Path path;

    @Override
    public String asString() {
        return path.toString();
    }

    public String getExtension() {
        return FileCommons.getExtension(path);
    }

}
