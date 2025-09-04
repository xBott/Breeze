package me.bottdev.breezeapi.di.suppliers;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.di.ObjectSupplier;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class PrototypeSupplier implements ObjectSupplier {

    private final Supplier<?> prototype;

    @Override
    public Object supply() {
        return prototype.get();
    }

}
