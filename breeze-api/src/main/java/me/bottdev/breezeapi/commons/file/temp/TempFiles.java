package me.bottdev.breezeapi.commons.file.temp;

import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.SimpleTreeLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class TempFiles {

    private static final BreezeLogger logger = new SimpleTreeLogger("TempFiles");
    private static final Path tempDirectoryPath = Paths.get(System.getProperty("java.io.tmpdir")).resolve("breeze");

    private static Path resolvePath(Path filePath) {
        return tempDirectoryPath.resolve(filePath);
    }

    private static TempFile getFile(Path filePath) {
        Path absolutePath = resolvePath(filePath);
        return TempFile.asStandalone(filePath, absolutePath);
    }

    public static boolean exists(Path filePath) {
        return getFile(filePath).exists();
    }

    public static Optional<TempFile> create(Path filePath) {

        TempFile file = getFile(filePath);
        if (file.exists()) return Optional.of(file);

        Path absolutePath = file.getAbsolutePath();

        try {

            if (Files.isDirectory(absolutePath)) {
                Files.createDirectories(absolutePath);

            } else {
                Files.createDirectories(absolutePath.getParent());
                Files.createFile(absolutePath);
                file.deleteOnExit();
            }

        } catch (IOException ex) {
            logger.error("Could not create temporary file", ex);
            return Optional.empty();
        }

        return Optional.of(file);
    }


    public static void delete(TempFile tempFile) {
        try {
            Files.delete(tempFile.getAbsolutePath());

        } catch (IOException ex) {
            logger.error("Could not delete temporary file", ex);
        }
    }

    public static void cleanup() {

        try {

            if (!Files.exists(tempDirectoryPath)) return;

            List<Path> paths;
            try (Stream<Path> stream = Files.walk(tempDirectoryPath)) {
                paths = stream.sorted(Comparator.reverseOrder()).toList();
            }

            for (Path p : paths) {
                Files.delete(p);
            }

        } catch (IOException ex) {
            logger.error("Could not cleanup temporary file", ex);
        }

    }


}
