package me.bottdev.breezeapi.commons.file;

import me.bottdev.breezeapi.commons.file.input.BreezeFileReader;
import me.bottdev.breezeapi.commons.file.output.BreezeFileWriter;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.types.SimpleTreeLogger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

public class FileCommons {

    private static final BreezeLogger logger = new SimpleTreeLogger("FileCommons");

    public static void createDirectoryIfNotExists(File directory) {
        if (directory.exists()) return;
        directory.mkdirs();
    }

    public static String getExtension(String name) {
        int firstDot = name.indexOf('.');
        if (firstDot == -1) return "";
        return name.substring(firstDot + 1);
    }

    public static String getExtension(File file) {
        String fileName = file.getName();
        return getExtension(fileName);
    }

    public static String getExtension(Path path) {
        String fileName = path.getFileName().toString();
        return getExtension(fileName);
    }

    public static void walkDirectory(File dir, Consumer<File> onJoin) {

        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                walkDirectory(file, onJoin);
            }
            onJoin.accept(file);
        }

    }

    public void createFileIfNotExists(File file) {
        if (file.exists()) return;

        if (file.isDirectory()) {
            file.mkdirs();

        } else {

            try {
                file.getParentFile().mkdirs();
                file.createNewFile();

            } catch (IOException ex) {
                logger.error("Failed to create file", ex);
            }

        }

    }

    public static File createFileOrDirectory(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            Files.createDirectories(path);

        } else {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
        }

        return path.toFile();
    }

    public static void copyFile(File source, File target) throws IOException {

        if (!source.exists() || source.isDirectory() || target.isDirectory()) return;

        createDirectoryIfNotExists(target);

        new BreezeFileWriter().writeChunks(target, out -> {

            new BreezeFileReader().readChunks(source, (data, length) -> {

                out.write(data, 0, length);

            });

        });

    }

}
