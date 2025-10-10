package me.bottdev.breezeapi.serialization.polymorphic;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

@RequiredArgsConstructor
public class PolymorphicSerializer extends JsonSerializer<Object> {

    private final String typeField;

    private static final ThreadLocal<Set<Object>> seenObjects =
            ThreadLocal.withInitial(() -> Collections.newSetFromMap(new IdentityHashMap<>()));

    @Override
    public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        Set<Object> seen = seenObjects.get();
        if (seen.contains(value)) {
            gen.writeString("[Circular Reference]");
            return;
        }

        try {
            seen.add(value);

            gen.writeStartObject();

            gen.writeStringField(typeField, value.getClass().getSimpleName());

            Class<?> clazz = value.getClass();
            while (clazz != null && clazz != Object.class) {
                for (Field field : clazz.getDeclaredFields()) {
                    field.setAccessible(true);

                    if (field.isAnnotationPresent(com.fasterxml.jackson.annotation.JsonIgnore.class)) continue;
                    if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) continue;

                    String fieldName = field.getName();
                    JsonProperty prop = field.getAnnotation(JsonProperty.class);
                    if (prop != null && !prop.value().isEmpty()) {
                        fieldName = prop.value();
                    }

                    Object fieldValue = field.get(value);
                    gen.writeFieldName(fieldName);
                    serializers.defaultSerializeValue(fieldValue, gen);
                }
                clazz = clazz.getSuperclass();
            }

            gen.writeEndObject();
        } catch (IllegalAccessException e) {
            throw new IOException("Serialization failed", e);
        } finally {
            seen.remove(value);
            if (seen.isEmpty()) seenObjects.remove();
        }
    }
}
