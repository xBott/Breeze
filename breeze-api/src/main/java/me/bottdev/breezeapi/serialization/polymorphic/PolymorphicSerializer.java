package me.bottdev.breezeapi.serialization.polymorphic;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@RequiredArgsConstructor
public class PolymorphicSerializer extends JsonSerializer<Object> {

    private final String typeField;

    private static final ObjectMapper CLEAN_MAPPER = new ObjectMapper();

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        ObjectNode node = CLEAN_MAPPER.valueToTree(value); // больше не вызывает этот сериализатор
        node.put(typeField, value.getClass().getSimpleName());
        gen.writeTree(node);
    }

}
