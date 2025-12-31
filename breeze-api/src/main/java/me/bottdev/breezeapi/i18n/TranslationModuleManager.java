package me.bottdev.breezeapi.i18n;

import lombok.Getter;
import me.bottdev.breezeapi.i18n.types.SimpleI18n;

import java.util.*;

public class TranslationModuleManager {

    private final Map<String, TranslationModule> modules = new HashMap<>();
    @Getter
    private final Locale defaultFallback = Locale.forLanguageTag("en-US");

    public List<TranslationModule> getModules() {
        return new ArrayList<>(modules.values());
    }

    public void register(TranslationModule module) {
        modules.put(module.getName(), module);
    }

    public void unregister(String name) {
        modules.remove(name);
    }

    public Optional<String> getMessage(Locale locale, String key) {

        for (TranslationModule module : getModules()) {

            Optional<String> messageOptional = module.get(locale, key);
            if (messageOptional.isEmpty()) continue;
            return messageOptional;

        }

        return Optional.empty();
    }

    public I18n simple(Locale locale, Locale fallback) {
        return new SimpleI18n(locale, fallback, this);
    }

    public I18n simple(Locale locale) {
        return simple(locale, defaultFallback);
    }

}
