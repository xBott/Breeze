package me.bottdev.breezeapi.di.suppliers;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.di.ObjectSupplier;

@RequiredArgsConstructor
public class SingletonSupplier implements ObjectSupplier {

    private final Object singleton;

    @Override
    public Object supply() {
        return singleton;
    }

}
