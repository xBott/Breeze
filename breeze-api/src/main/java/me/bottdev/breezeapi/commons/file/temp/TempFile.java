package me.bottdev.breezeapi.commons.file.temp;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
@RequiredArgsConstructor
public class TempFile {

    public static TempFile asStandalone(Path relativePath, Path absolutePath) {
        return new TempFile(relativePath, absolutePath, null);
    }
    public static TempFile asCopy(Path relativePath, Path absolutePath, Path sourcePath) {
        return new TempFile(relativePath, absolutePath, sourcePath);
    }

    private final Path relativePath;
    private final Path absolutePath;
    private final Path sourcePath;

    public boolean isCopy() {
        return sourcePath != null;
    }

    public boolean exists() {
        return Files.exists(absolutePath);
    }

    public void deleteOnExit() {
        absolutePath.toFile().deleteOnExit();
    }

    public File toFile() {
        return absolutePath.toFile();
    }

}
