package me.bottdev.breezeadmin;

import me.bottdev.breezeapi.di.annotations.Supplier;
import me.bottdev.breezeapi.di.annotations.Supply;

import java.util.List;

@Supplier
public class AdminSupplier {

    @Supply
    public List<String> adminNameList() {
        return List.of("_xBott", "OtherAdmin", "LastAdmin");
    }

}
