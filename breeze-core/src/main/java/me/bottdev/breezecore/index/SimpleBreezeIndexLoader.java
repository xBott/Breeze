package me.bottdev.breezecore.index;

import me.bottdev.breezeapi.commons.file.input.BreezeStreamReader;
import me.bottdev.breezeapi.index.BreezeIndex;
import me.bottdev.breezeapi.index.IndexMap;
import me.bottdev.breezeapi.index.IndexLoader;
import me.bottdev.breezeapi.index.IndexRegistry;
import me.bottdev.breezeapi.log.BreezeLogger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class SimpleBreezeIndexLoader implements IndexLoader {

    private static final Path INDICES_PATH = Paths.get("META-INF/");

    private final IndexRegistry registry;
    private final BreezeLogger logger;

    public SimpleBreezeIndexLoader(IndexRegistry registry, BreezeLogger mainLogger) {
        this.registry = registry;
        this.logger = mainLogger;
    }

    @Override
    public IndexMap loadFromClassloader(ClassLoader classLoader) {
        logger.info("Loading Breeze Index Bucket from class loader...");

        IndexMap bucket = new IndexMap(classLoader);
        registry.getRegisteredIndices().forEach((clazz, id) ->
                loadSingleIndexFromClassloader(classLoader, id).ifPresent(bucket::put)
        );

        logger.info("Successfully loaded Breeze Index Bucket from class loader with {} indices.", bucket.getSize());

        return bucket;
    }

    private Optional<BreezeIndex> loadSingleIndexFromClassloader(ClassLoader classLoader, String id) {
        Path path = INDICES_PATH.resolve(id + "-index.json");
        String jsonString = readFile(classLoader, path);
        if (jsonString == null || jsonString.isBlank()) return Optional.empty();
        return registry.getSerializer().deserialize(jsonString);
    }

    private String readFile(ClassLoader classLoader, Path path) {


        String resourcePath = path.toString();

        try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                logger.warn("Resource {} not found in classpath", resourcePath);
                return null;
            }

            return BreezeStreamReader.INSTANCE.readString(inputStream);

        } catch (IOException e) {
            logger.error("Failed to read resource: " + resourcePath, e);
        }

        return null;
    }

}
