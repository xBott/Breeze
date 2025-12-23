package me.bottdev.breezeapi.commons.file.temp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.bottdev.breezeapi.commons.file.input.BreezeFileReader;
import me.bottdev.breezeapi.commons.file.output.BreezeFileWriter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Getter
@AllArgsConstructor
public class TempFile {

    public static TempFile asStandalone(Path relativePath, Path absolutePath) {
        return new TempFile(relativePath, absolutePath, null);
    }
    public static TempFile asCopy(Path relativePath, Path absolutePath, Path sourcePath) {
        return new TempFile(relativePath, absolutePath, sourcePath);
    }

    private final Path relativePath;
    private final Path absolutePath;
    @Setter
    private Path sourcePath;

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

    public Optional<File> getSourceFile() {
        if (sourcePath == null) return Optional.empty();
        return Optional.of(sourcePath.toFile());
    }

    public void update() throws IOException {

        Optional<File> sourceFileOptional = getSourceFile();
        if (sourceFileOptional.isEmpty()) return;

        File sourceFile = sourceFileOptional.get();

        BreezeFileWriter.INSTANCE.writeChunks(toFile(), out -> {

            BreezeFileReader.INSTANCE.readChunks(sourceFile, (data, length) -> {
                out.write(data, 0, length);
            });

        });

    }

}
