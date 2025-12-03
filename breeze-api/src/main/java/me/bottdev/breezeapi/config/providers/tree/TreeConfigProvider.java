package me.bottdev.breezeapi.config.providers.tree;

import me.bottdev.breezeapi.commons.file.FileCommons;
import me.bottdev.breezeapi.config.ConfigLoader;
import me.bottdev.breezeapi.config.ConfigProvider;
import me.bottdev.breezeapi.config.Configuration;

import java.io.File;
import java.nio.file.Path;

public interface TreeConfigProvider<T extends Configuration> extends ConfigProvider<T, TreeConfigContainer<T>> {

    Path getDirectoryPath();

    @Override
    default TreeConfigContainer<T> provide() {

        ConfigLoader loader = getConfigLoader();
        Class<T> configurationClass = getConfigurationClass();

        Path directoryPath = getDirectoryPath();
        File file = directoryPath.toFile();

        if (!file.isDirectory()) {
            return new TreeConfigContainer<>();
        }

        FileCommons.createDirectoryIfNotExists(file);

        TreeConfigContainer<T> treeConfigContainer = new TreeConfigContainer<>();
        FileCommons.walkDirectory(file, leaf -> {
            if (leaf.isDirectory()) return;
            String extension = FileCommons.getExtension(leaf);

        });

        return treeConfigContainer;
    }

}
