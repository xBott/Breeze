package me.bottdev.breezeapi.modules;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.index.IndexMap;

import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
public class ModulePreLoad {

    private final ClassLoader classLoader;
    private final IndexMap indexBucket;

    private final Path moduleDataFolder;
    private final Class<? extends Module> moduleClass;
    private final Supplier<Optional<Module>> moduleSupplier;

}
