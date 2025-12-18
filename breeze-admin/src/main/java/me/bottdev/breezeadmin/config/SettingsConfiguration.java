package me.bottdev.breezeadmin.config;

import lombok.*;
import me.bottdev.breezeapi.resource.config.Configuration;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SettingsConfiguration implements Configuration {

    private double version;

}
