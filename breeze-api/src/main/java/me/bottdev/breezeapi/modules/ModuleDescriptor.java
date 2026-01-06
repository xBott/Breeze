package me.bottdev.breezeapi.modules;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ModuleDescriptor {
    private final String name;
    private final String version;
}
