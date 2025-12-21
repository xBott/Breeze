package me.bottdev.breezeapi.resource.source;

import java.util.HashMap;
import java.util.Optional;

public class ResourceSourceRegistry {

    private final HashMap<SourceType, ResourceSource> sources = new HashMap<>();

    public ResourceSourceRegistry register(SourceType sourceType, ResourceSource source) {
        sources.put(sourceType, source);
        return this;
    }

    public Optional<ResourceSource> get(SourceType sourceType) {
        return Optional.ofNullable(sources.get(sourceType));
    }

}
