package me.bottdev.breezeapi.serialization;

import me.bottdev.breezeapi.log.BreezeLogger;

import java.util.Optional;

public interface Mapper {

    BreezeLogger getLogger();

    String getExtension();

    String serialize(Object object);

    <T> Optional<T> deserialize(Class<T> clazz, String serialized);

    Optional<ObjectNode> deserializeTree(String serialized);

}
