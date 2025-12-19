package me.bottdev.breezeapi.resource.source;

import java.util.HashMap;
import java.util.Optional;

public class ResourceSourceRegistry {

    private static final HashMap<SourceType, ResourceSource> sources = new HashMap<>();

    public static void register(SourceType sourceType, ResourceSource source) {
        sources.put(sourceType, source);
    }

    public static Optional<ResourceSource> get(SourceType sourceType) {
        return Optional.ofNullable(sources.get(sourceType));
    }

}
