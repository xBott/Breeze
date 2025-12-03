package me.bottdev.breezeapi.serialization;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MapperRegistry {

    private final Map<String, Mapper> mappersByExtension = new HashMap<>();
    private final Map<Class<? extends Mapper>, Mapper> mappersByClass = new HashMap<>();

    public void registerMapper(MapperType type, Mapper mapper) {
        mappersByExtension.put(type.extension, mapper);
        mappersByClass.put(type.mapperClass, mapper);
    }

    public Optional<Mapper> getMapperByExtension(String extension) {
        return Optional.ofNullable(mappersByExtension.get(extension));
    }

    public Optional<Mapper> getMapperByClass(Class<? extends Mapper> clazz) {
        return Optional.ofNullable(mappersByClass.get(clazz));
    }

}

