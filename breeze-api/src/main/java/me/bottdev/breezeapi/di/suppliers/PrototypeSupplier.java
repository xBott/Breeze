package me.bottdev.breezeapi.di.suppliers;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.di.ObjectSupplier;
import me.bottdev.breezeapi.di.SupplyType;

import java.util.function.Supplier;

@RequiredArgsConstructor
public class PrototypeSupplier implements ObjectSupplier {

    private final Supplier<?> prototype;

    @Override
    public SupplyType getType() {
        return SupplyType.PROTOTYPE;
    }

    @Override
    public Object supply() {
        return prototype.get();
    }

}
