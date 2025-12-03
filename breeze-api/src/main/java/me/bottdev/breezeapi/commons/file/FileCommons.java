package me.bottdev.breezeapi.commons.file;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Consumer;

public class FileCommons {

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

}
