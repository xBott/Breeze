package me.bottdev.breezeapi.di.suppliers;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.di.ObjectSupplier;
import me.bottdev.breezeapi.di.SupplyType;

@RequiredArgsConstructor
public class SingletonSupplier implements ObjectSupplier {

    private final Object singleton;

    @Override
    public SupplyType getType() {
        return SupplyType.SINGLETON;
    }

    @Override
    public Object supply() {
        return singleton;
    }

}
