package me.bottdev.breezeapi.i18n;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class TranslationModule {

    private final Map<Locale, Translation> translations = new HashMap<>();

    @Getter
    private final String name;

    public TranslationModule addTranslation(Locale locale, Translation translation) {
        translations.put(locale, translation);
        return this;
    }

    public Optional<Translation> getTranslation(Locale locale) {
        return Optional.of(translations.get(locale));
    }

    public boolean has(Locale locale, String key) {
        return getTranslation(locale)
                .map(translation -> translation.has(key))
                .orElse(false);
    }

    public Optional<String> get(Locale locale, String key) {
        return getTranslation(locale)
                .map(translation -> translation.get(key));
    }

}
