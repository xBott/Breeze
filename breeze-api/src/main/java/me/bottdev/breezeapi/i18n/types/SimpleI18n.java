package me.bottdev.breezeapi.i18n.types;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.i18n.I18n;
import me.bottdev.breezeapi.i18n.TranslationModuleManager;

import java.util.Locale;

@RequiredArgsConstructor
public class SimpleI18n implements I18n {

    private final Locale locale;
    private final Locale fallbackLocale;
    private final TranslationModuleManager translationModuleManager;


    @Override
    public String get(String key) {
        return translationModuleManager.getMessage(locale, key).orElse(
                translationModuleManager.getMessage(fallbackLocale, key).orElse("")
        );
    }

    @Override
    public String get(String key, Object... args) {
        String message = get(key);
        for (Object arg : args) {
            message = message.replaceFirst("\\{}", arg == null ? "null" : arg.toString());
        }
        return message;
    }

}
