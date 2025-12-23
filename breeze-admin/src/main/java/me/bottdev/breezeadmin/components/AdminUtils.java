package me.bottdev.breezeadmin.components;

import lombok.Getter;
import me.bottdev.breezeapi.di.annotations.Component;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.di.annotations.Named;

import java.util.List;

@Component
public class AdminUtils {

    @Getter
    private final List<String> adminNameList;

    @Inject
    public AdminUtils(
        List<String> adminNameList
    ) {
        this.adminNameList = adminNameList;
    }

}
