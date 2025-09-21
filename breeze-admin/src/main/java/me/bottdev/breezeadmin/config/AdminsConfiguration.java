package me.bottdev.breezeadmin.config;

import lombok.Getter;
import lombok.Setter;
import me.bottdev.breezeapi.config.Configuration;
import me.bottdev.breezeapi.config.validation.annotations.Range;

@Getter
@Setter
public class AdminsConfiguration implements Configuration {

    @Range(min = 1, max = 100)
    private int maxCount = 0;

}
