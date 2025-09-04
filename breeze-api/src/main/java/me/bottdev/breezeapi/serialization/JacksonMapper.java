package me.bottdev.breezeapi.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.bottdev.breezeapi.log.BreezeLogger;

import java.util.Optional;

public interface JacksonMapper extends Mapper, PolymorphicMapper {

    ObjectMapper getObjectMapper();

    @Override
    default String serialize(Object object) {
        ObjectMapper objectMapper = getObjectMapper();
        BreezeLogger logger = getLogger();
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return null;
        }
    }

    @Override
    default  <T> Optional<T> deserialize(Class<T> clazz, String json) {
        ObjectMapper objectMapper = getObjectMapper();
        BreezeLogger logger = getLogger();
        try {
            T object = objectMapper.readValue(json, clazz);
            return Optional.of(object);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return Optional.empty();
        }
    }

}
