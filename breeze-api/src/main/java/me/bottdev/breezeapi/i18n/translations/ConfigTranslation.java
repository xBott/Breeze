package me.bottdev.breezeapi.i18n.translations;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.bottdev.breezeapi.config.Configuration;
import me.bottdev.breezeapi.i18n.Translation;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ConfigTranslation implements Configuration, Translation {
    private Map<String, String> messages;
}