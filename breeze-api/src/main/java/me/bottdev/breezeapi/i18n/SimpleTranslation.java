package me.bottdev.breezeapi.i18n;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class SimpleTranslation implements Translation {

    @Getter
    private final Map<String, String> messages = new HashMap<>();

    public SimpleTranslation put(String key, String value) {
        messages.put(key, value);
        return this;
    }

}
