package me.bottdev.breezemc.chat;

import me.bottdev.breezeapi.i18n.I18n;

public interface TranslatableMessageReceiver extends MessageReceiver {

    I18n getI18n();

    default void sendTranslatedMessage(String key, Object... args) {
        String message = getI18n().get(key, args);
        sendMessage(message);
    }

    default void sendTranslatedMessage(String key) {
        String message = getI18n().get(key);
        sendMessage(message);
    }

}
