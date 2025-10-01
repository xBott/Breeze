package me.bottdev.breezeapi.modules;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.di.index.ComponentIndex;
import me.bottdev.breezeapi.di.index.SupplierIndex;

import java.util.Optional;
import java.util.function.Supplier;

@RequiredArgsConstructor
@Getter
public class ModulePreLoad {

    private final ClassLoader classLoader;
    private final SupplierIndex supplierIndex;
    private final ComponentIndex componentIndex;

    private final Class<? extends Module> moduleClass;
    private final Supplier<Optional<Module>> moduleSupplier;


}
