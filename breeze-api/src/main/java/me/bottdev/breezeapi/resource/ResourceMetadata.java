package me.bottdev.breezeapi.resource;

import java.util.HashMap;
import java.util.Optional;

public class ResourceMetadata {

    private final HashMap<String, Object> data = new HashMap<>();

    public ResourceMetadata set(String key, Object value) {
        data.put(key, value);
        return this;
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key) {
        Object value = data.get(key);

        if (value == null) {
            return Optional.empty();
        }

        try {
            return Optional.of((T) value);
        } catch (ClassCastException e) {
            return Optional.empty();
        }
    }


}
