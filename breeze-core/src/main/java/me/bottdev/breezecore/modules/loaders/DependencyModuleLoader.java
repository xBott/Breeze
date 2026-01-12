package me.bottdev.breezecore.modules.loaders;

import lombok.Getter;
import me.bottdev.breezeapi.BreezeEngine;
import me.bottdev.breezeapi.index.BreezeIndexBucket;
import me.bottdev.breezeapi.index.BreezeIndexBucketContainer;
import me.bottdev.breezeapi.index.types.BreezeModuleIndex;
import me.bottdev.breezeapi.modules.*;
import me.bottdev.breezeapi.modules.Module;
import me.bottdev.breezeapi.modules.annotations.ModuleInfo;
import me.bottdev.breezecore.di.resolver.IndexBucketDependencyResolver;
import me.bottdev.breezecore.modules.ChildFirstClassLoader;

import java.io.*;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Supplier;

public class DependencyModuleLoader implements ModuleLoader {

    private final TreeLogger logger;
    private final IndexBucketDependencyResolver bucketDependencyResolver;

    @Getter
    private final ClassLoader parentClassLoader;
    @Getter
    private final BreezeEngine engine;
    @Getter
    private final Path targetDirectory;


    public DependencyModuleLoader(
            TreeLogger logger,
            ClassLoader parentClassLoader,
            BreezeEngine engine,
            Path targetDirectory
    ) {
        this.logger = logger;
        this.parentClassLoader = parentClassLoader;
        this.engine = engine;
        this.targetDirectory = targetDirectory;
        this.bucketDependencyResolver = new IndexBucketDependencyResolver(logger);
    }

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

        return sortedBuckets.stream()
                .map(this::loadBucket)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
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

                URLClassLoader loader = new ChildFirstClassLoader(new URL[]{jarUrl}, parentClassLoader);

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

            ModuleInfo info = moduleClazz.getAnnotation(ModuleInfo.class);
            if (info == null) return Optional.empty();
            ModuleDescriptor descriptor = new ModuleDescriptor(info.name(), info.version());

            Supplier<Optional<Module>> moduleSupplier = createClassInstanceSupplier(moduleClazz, dataFolder, descriptor);

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

    private Supplier<Optional<Module>> createClassInstanceSupplier(
            Class<? extends Module> clazz,
            File dataFolder,
            ModuleDescriptor descriptor
    ) {

        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor(File.class, ModuleDescriptor.class);
            return () -> {
                try {
                    Module instance = (Module) constructor.newInstance(dataFolder, descriptor);
                    return Optional.of(instance);
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
