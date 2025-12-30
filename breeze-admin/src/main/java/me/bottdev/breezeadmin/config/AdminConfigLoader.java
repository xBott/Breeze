package me.bottdev.breezeadmin.config;

import me.bottdev.breezeadmin.resource.AdminResourceProvider;
import me.bottdev.breezeapi.config.SimpleConfigLoader;
import me.bottdev.breezeapi.config.validation.patterns.PathEndsPattern;
import me.bottdev.breezeapi.config.validation.rules.RangeRule;
import me.bottdev.breezeapi.config.validation.rules.StructureRule;
import me.bottdev.breezeapi.config.validation.types.RuleConfigValidator;
import me.bottdev.breezeapi.di.annotations.Component;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.serialization.mappers.JsonMapper;

import java.util.Optional;

@Component
public class AdminConfigLoader {

    private final AdminResourceProvider adminResourceProvider;
    private final SimpleConfigLoader<SettingsConfiguration> settingsLoader;

    @Inject
    public AdminConfigLoader(AdminResourceProvider adminResourceProvider) {
        this.adminResourceProvider = adminResourceProvider;

        JsonMapper jsonMapper = new JsonMapper();
        RuleConfigValidator validator = new RuleConfigValidator();
        validator.getRuleRegistry()
                .addRootRule(new StructureRule(SettingsConfiguration.class))
                .addRule(new PathEndsPattern("version"), new RangeRule(0.0, 1.0));

        this.settingsLoader = new SimpleConfigLoader<>(SettingsConfiguration.class, jsonMapper, validator);
    }

    public Optional<SettingsConfiguration> getSettings() {
        return adminResourceProvider.getSettingsResource().flatMap(settingsLoader::load);
    }

}
