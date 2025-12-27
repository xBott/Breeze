package me.bottdev.breezeapi.resource.types;

import me.bottdev.breezeapi.commons.file.FileCommons;
import me.bottdev.breezeapi.commons.file.input.BreezeFileReader;
import me.bottdev.breezeapi.commons.file.output.BreezeFileWriter;
import me.bottdev.breezeapi.commons.file.temp.TempFile;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.types.SimpleLogger;
import me.bottdev.breezeapi.resource.Resource;
import me.bottdev.breezeapi.resource.source.SourceType;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public interface FileResource extends Resource {

    BreezeLogger logger = new SimpleLogger("FileResource");

    TempFile getTempFile();
    SourceType getSourceType();

    default String getName() {
        return getTempFile().getAbsolutePath().getFileName().toString();
    }

    default String getExtension() {
        return FileCommons.getExtension(getName());
    }

    default Optional<String> read() {
        try {
            String content = BreezeFileReader.INSTANCE.readString(getTempFile().toFile());
            return Optional.ofNullable(content);

        } catch (IOException ex) {
            logger.error("Could not read file", ex);
        }
        return Optional.empty();
    }

    default Optional<String> readTrimmed() {
        return read().map(String::trim);
    }

    default boolean isEmpty() {
        Optional<String> contentOptional = read();
        if (contentOptional.isEmpty()) return true;
        String content = contentOptional.get();
        return content.isEmpty();
    }

    default boolean write(String content) {

        try {
            BreezeFileWriter.INSTANCE.writeString(getTempFile().toFile(), bufferedWriter ->
                    bufferedWriter.write(content)
            );
            logger.info("Successfully wrote to file resource {}.", getTempFile().getAbsolutePath());

            return true;

        } catch (IOException ex) {
            logger.error("Could not write file", ex);
        }

        return false;

    }

    default boolean save() {

        try {
            TempFile tempFile = getTempFile();

            Optional<File> targetOptional = tempFile.getSourceFile();
            if (targetOptional.isEmpty()) {
                logger.warn("No source file found for file resource {}.", getTempFile().getAbsolutePath());
                return false;
            }

            File source = tempFile.toFile();
            File target = targetOptional.get();

            FileCommons.copyFile(source, target);

            logger.info("Successfully saved file resource {}.", getTempFile().getAbsolutePath());

            return true;

        } catch (IOException ex) {
            logger.error("Could not save file", ex);
        }

        return false;

    }

    default void writeAndSave(String content) {
        boolean written = write(content);
        if (written) save();
    }

}
