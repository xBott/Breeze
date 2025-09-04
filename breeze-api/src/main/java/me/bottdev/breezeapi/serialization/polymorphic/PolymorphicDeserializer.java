package me.bottdev.breezeapi.serialization.polymorphic;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Map;

@RequiredArgsConstructor
public class PolymorphicDeserializer<T> extends JsonDeserializer<T> {

    private final Class<T> baseType;
    private final Map<String, Class<? extends T>> typeMap;
    private final String typeField;

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) p.getCodec();
        ObjectNode node = mapper.readTree(p);

        String typeName = node.has(typeField) ? node.get(typeField).asText() : null;
        if (typeName == null) {
            throw new IllegalArgumentException("Missing '" + typeField + "' field");
        }

        Class<? extends T> targetClass = typeMap.get(typeName);
        if (targetClass == null) {
            throw new IllegalArgumentException("Unknown type '" + typeName + "' for " + baseType.getSimpleName());
        }

        node.remove(typeField);
        return mapper.treeToValue(node, targetClass);
    }
}
