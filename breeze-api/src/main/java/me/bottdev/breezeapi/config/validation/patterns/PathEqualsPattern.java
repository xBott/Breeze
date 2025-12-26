package me.bottdev.breezeapi.config.validation.patterns;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.config.validation.FieldPathPattern;

@RequiredArgsConstructor
public class PathEqualsPattern implements FieldPathPattern {

    private final String targetPath;

    @Override
    public boolean matches(String path) {
        return path.equals(targetPath);
    }

}
