package me.bottdev.breezeapi.resource.source.descriptor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.resource.source.SourceType;

import java.lang.annotation.Annotation;

@Getter
@RequiredArgsConstructor
public class SourceDescriptor {

    private final Annotation annotation;
    private final SourceType type;
    private final int priority;

}
