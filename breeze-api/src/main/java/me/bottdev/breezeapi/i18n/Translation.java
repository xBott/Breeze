package me.bottdev.breezeapi.i18n;

import java.util.Map;

public interface Translation {

    Map<String, String> getMessages();

    default boolean has(String key) {
        return getMessages().containsKey(key);
    }

    default String get(String key) {
        return getMessages().get(key);
    }

}
