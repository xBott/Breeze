package me.bottdev.breezecore.modules.loaders;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.BreezeEngine;
import me.bottdev.breezeapi.index.BreezeIndexBucket;
import me.bottdev.breezeapi.index.BreezeIndexBucketContainer;
import me.bottdev.breezeapi.index.BreezeIndexSerializer;
import me.bottdev.breezeapi.index.types.BreezeModuleIndex;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.SimpleLogger;
import me.bottdev.breezeapi.modules.Module;
import me.bottdev.breezeapi.modules.ModuleClassLoader;
import me.bottdev.breezeapi.modules.ModuleLoader;
import me.bottdev.breezeapi.modules.ModulePreLoad;
import me.bottdev.breezecore.di.resolver.IndexBucketDependencyResolver;

import java.io.*;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

@RequiredArgsConstructor
public class DependencyModuleLoader implements ModuleLoader {

    private final BreezeLogger logger = new SimpleLogger("DependencyFolderModuleLoader");
    private final IndexBucketDependencyResolver bucketDependencyResolver = new IndexBucketDependencyResolver();
    private final BreezeIndexSerializer serializer = new BreezeIndexSerializer();

    @Getter
    private final ClassLoader parentClassLoader;
    @Getter
    private final BreezeEngine engine;
    @Getter
    private final Path targetDirectory;

    private void createDirectoryIfNotExists() {
        File folder = this.targetDirectory.toFile();
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    @Override
    public List<ModulePreLoad> load() {
        createDirectoryIfNotExists();

        List<Path> jarPaths = getJarPaths();
        List<BreezeIndexBucket> buckets = getIndexBuckets(jarPaths);
        BreezeIndexBucketContainer container = BreezeIndexBucketContainer.builder()
                .dependents(buckets)
                .build();

        List<BreezeIndexBucket> sortedBuckets = getSortedBuckets(container);
        sortedBuckets.forEach(this::loadBucket);

        return List.of();
    }

    private List<Path> getJarPaths() {
        List<Path> results = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(targetDirectory, "*.jar")) {

            stream.forEach(path -> {
                try {

                    results.add(path);

                } catch (Exception ex) {
                    logger.error("Failed to process jar " + path, ex);
                }
            });

        } catch (IOException ex) {
            logger.error("Failed to scan modules directory " + targetDirectory, ex);
        }

        return results;
    }

    private List<BreezeIndexBucket> getIndexBuckets(List<Path> paths) {

        List<BreezeIndexBucket> results = new ArrayList<>();

        paths.forEach(jarPath -> {
            try {

                URL jarUrl = jarPath.toUri().toURL();
                URLClassLoader loader = new ModuleClassLoader(new URL[]{jarUrl}, parentClassLoader);

                BreezeIndexBucket bucket = getEngine().getIndexLoader().loadFromClassloader(loader);

                results.add(bucket);

            } catch (Exception ex) {
                logger.error("Failed to process jar " + jarPath, ex);
            }
        });

        return results;
    }

    private List<BreezeIndexBucket> getSortedBuckets(BreezeIndexBucketContainer container) {
        return bucketDependencyResolver.resolve(container);
    }

    @SuppressWarnings("unchecked")
    private Optional<ModulePreLoad> loadBucket(BreezeIndexBucket bucket) {

        ClassLoader classLoader = bucket.getClassLoader();

        Optional<BreezeModuleIndex> moduleIndexOptional = bucket.getModuleIndex();
        if (moduleIndexOptional.isEmpty()) {
            logger.warn("Bucket doesn't contain Module Index.", classLoader);
            return Optional.empty();
        }

        BreezeModuleIndex moduleIndex = moduleIndexOptional.get();
        String moduleClassPath = moduleIndex.getClassPath();

        try {

            Class<? extends Module> moduleClazz = (Class<? extends Module>) classLoader.loadClass(moduleClassPath);
            if (!Module.class.isAssignableFrom(moduleClazz)) {
                logger.warn("{} does not implement Breeze Module.", moduleClazz);
                return Optional.empty();
            }

            File dataFolder = createDataFolder(moduleIndex.getModuleName());
            Supplier<Optional<Module>> moduleSupplier = createClassInstanceSupplier(moduleClazz, dataFolder);

            ModulePreLoad modulePreLoad = new ModulePreLoad(
                    classLoader,
                    bucket,
                    dataFolder.toPath(),
                    moduleClazz,
                    moduleSupplier
            );

            return Optional.of(modulePreLoad);

        } catch (Exception ex) {
            logger.error("Failed to load class from class loader " + classLoader, ex);
        }

        return Optional.empty();
    }

    private File createDataFolder(String moduleName) {
        File dataFolder = targetDirectory.resolve(moduleName).toFile();
        if (!dataFolder.exists()) dataFolder.mkdir();
        return dataFolder;
    }

    private Supplier<Optional<Module>> createClassInstanceSupplier(Class<? extends Module> clazz, File dataFolder) {

        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor(File.class);
            return () -> {
                try {
                    return Optional.of((Module) constructor.newInstance(dataFolder));
                } catch (Exception ex) {
                    logger.error("Failed to create module of class " + clazz.getName(), ex);
                }
                return Optional.empty();
            };
        } catch (Exception ex) {
            logger.error("Failed to create class instance" + clazz.getName(), ex);
        }

        return Optional::empty;
    }


}
