package me.bottdev.breezeapi.modules;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.di.index.ComponentIndex;
import me.bottdev.breezeapi.di.index.SupplierIndex;

@RequiredArgsConstructor
@Getter
public class ModulePreLoad {

    private final ClassLoader classLoader;
    private final Module module;
    private final SupplierIndex supplierIndex;
    private final ComponentIndex componentIndex;

}
