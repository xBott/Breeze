package me.bottdev.breezeapi.config.validation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.serialization.ObjectNode;

@Getter
@RequiredArgsConstructor
public class ValidationContext {
    private final ObjectNode node;
    private final String path;
    private final String fieldName;
}
