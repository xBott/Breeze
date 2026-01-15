package me.bottdev.breezeadmin.suppliers;

import me.bottdev.breezeapi.di.annotations.Factory;
import me.bottdev.breezeapi.di.annotations.Build;

import java.util.List;

@Factory
public class AdminSupplier {

    @Build
    public List<String> adminNameList() {
        return List.of("_xBott", "OtherAdmin", "LastAdmin");
    }

}
