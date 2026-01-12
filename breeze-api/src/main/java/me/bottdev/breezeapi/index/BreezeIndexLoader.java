package me.bottdev.breezeapi.index;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.log.BreezeLogger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Optional;

@RequiredArgsConstructor
public class BreezeIndexLoader {

    private static final Path INDICES_PATH = Paths.get("META-INF/");

    private final BreezeLogger logger;

    @Getter
    private final BreezeIndexRegistry registry = new BreezeIndexRegistry();
    private final HashMap<String, BreezeIndexBucket> loadedBuckets = new HashMap<>();

    public BreezeIndexBucket loadFromClassloader(ClassLoader classLoader) {
        logger.info("Loading Breeze Index Bucket from class loader...");

        BreezeIndexBucket bucket = new BreezeIndexBucket(classLoader);
        registry.getRegisteredIndices().forEach((clazz, id) -> {
            loadSingleIndexFromClassloader(classLoader, id).ifPresent(bucket::put);
        });

        logger.info("Successfully loaded Breeze Index Bucket from class loader with {} indices.", bucket.getSize());

        return bucket;
    }

    private Optional<BreezeIndex> loadSingleIndexFromClassloader(ClassLoader classLoader, String id) {
        Path path = INDICES_PATH.resolve(id + "-index.json");
        String jsonString = readFile(classLoader, path);
        if (jsonString == null || jsonString.isBlank()) return Optional.empty();
        return registry.getSerializer().deserialize(jsonString);
    }

    public void loadFromClassLoaderAndPut(ClassLoader classLoader, String id) {
        BreezeIndexBucket bucket = loadFromClassloader(classLoader);
        put(id, bucket);
        logger.info("Put Breeze Index Bucket to loaded buckets with id {}.", id);
    }

    public String readFile(ClassLoader classLoader, Path path) {

        String resourcePath = path.toString();
        StringBuilder contentBuilder = new StringBuilder();

        try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                logger.warn("Resource {} not found in classpath", resourcePath);
                return "";
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    contentBuilder.append(line).append("\n");
                }
            }

        } catch (IOException e) {
            logger.error("Failed to read resource: " + resourcePath, e);
        }

        return contentBuilder.toString();
    }

    public void put(String id, BreezeIndexBucket bucket) {
        loadedBuckets.put(id.toLowerCase(), bucket);
    }

    public Optional<BreezeIndexBucket> getBucket(String id) {
        return Optional.of(loadedBuckets.get(id.toLowerCase()));
    }

}
