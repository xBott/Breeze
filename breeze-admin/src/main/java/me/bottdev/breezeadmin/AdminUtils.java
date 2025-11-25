package me.bottdev.breezeadmin;

import lombok.Getter;
import me.bottdev.breezeapi.di.annotations.Component;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.di.annotations.Named;

import java.util.List;

@Component
public class AdminUtils {

    @Getter
    private List<String> adminNameList;

    @Inject
    public AdminUtils(
        @Named("adminnamelist") List<String> adminNameList
    ) {
        this.adminNameList = adminNameList;
    }

}
