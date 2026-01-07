package me.bottdev.breezeapi.di;

public interface ObjectSupplier {

    SupplyType getType();

    Object supply();

}
