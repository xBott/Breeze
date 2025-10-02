package me.bottdev.breezeapi.config.autoload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.bottdev.breezeapi.BreezeEngine;
import me.bottdev.breezeapi.serialization.Mapper;

@Getter
@AllArgsConstructor
public enum AutoLoadSerializer {
    JSON("json") {
        @Override
        public Mapper getMapper(BreezeEngine engine) {
            return engine.getJsonMapper();
        }
    };

    private final String extension;

    public abstract Mapper getMapper(BreezeEngine engine);
}

