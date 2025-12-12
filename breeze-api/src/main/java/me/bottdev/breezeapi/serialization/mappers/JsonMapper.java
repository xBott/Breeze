package me.bottdev.breezeapi.serialization.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.Getter;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.SimpleTreeLogger;
import me.bottdev.breezeapi.serialization.JacksonMapper;
import me.bottdev.breezeapi.serialization.polymorphic.PolymorphicRegistry;

@Getter
public class JsonMapper implements JacksonMapper {

    private final BreezeLogger logger = new SimpleTreeLogger("JsonMapper");
    private final PolymorphicRegistry registry = new PolymorphicRegistry();


    @Override
    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        registry.getTypes().forEach(type -> {
            SimpleModule module = type.getModule();
            objectMapper.registerModule(module);
        });
        return objectMapper;
    }
}
