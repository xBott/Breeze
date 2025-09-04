package me.bottdev.breezeapi.di;

import me.bottdev.breezeapi.di.suppliers.PrototypeSupplier;
import me.bottdev.breezeapi.di.suppliers.SingletonSupplier;

import java.util.Objects;
import java.util.function.Supplier;

public class SupplierFactory {

    public static ObjectSupplier create(SupplyType type, Supplier<?> supplier) {
        ObjectSupplier objectSupplier;

        if (Objects.requireNonNull(type) == SupplyType.PROTOTYPE) {
            objectSupplier = new PrototypeSupplier(supplier);
        } else {
            Object supplied = supplier.get();
            objectSupplier = new SingletonSupplier(supplied);
        }

        return objectSupplier;
    }

}
