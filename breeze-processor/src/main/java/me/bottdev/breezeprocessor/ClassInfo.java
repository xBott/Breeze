package me.bottdev.breezeprocessor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.Annotation;

@RequiredArgsConstructor
@Getter
public class ClassInfo {
    private final String className;
    private final Annotation annotation;
}
