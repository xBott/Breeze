package me.bottdev.breezeapi.i18n;

public interface I18n {

    String get(String key);

    String get(String key, Object... args);

}
