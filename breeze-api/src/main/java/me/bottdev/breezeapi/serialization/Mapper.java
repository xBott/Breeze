package me.bottdev.breezeapi.serialization;

import me.bottdev.breezeapi.log.BreezeLogger;

import java.util.Optional;

public interface Mapper {

    BreezeLogger getLogger();

    String serialize(Object object);

    <T> Optional<T> deserialize(Class<T> clazz, String json);

}
