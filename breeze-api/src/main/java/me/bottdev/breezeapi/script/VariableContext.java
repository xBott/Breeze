package me.bottdev.breezeapi.script;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class VariableContext {

    private final Map<String, Value> values = new HashMap<>();

    public void set(String id, Value value) {
        values.put(id, value);
    }

    public Optional<Object> get(String id) {
        Value value = values.get(id);
        if (value == null) return Optional.empty();
        Object object = value.getValue();
        return Optional.ofNullable(object);
    }

    public Optional<Integer> getInteger(String id) {
        return get(id)
                .map(Object::toString)
                .map(s -> {
                    try {
                        return Integer.parseInt(s);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                });
    }




}
