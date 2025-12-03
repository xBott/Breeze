package me.bottdev.breezeadmin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.bottdev.breezeapi.config.Configuration;
import me.bottdev.breezeapi.config.validation.annotations.Range;

@AllArgsConstructor
@Getter
public class TestConfiguration implements Configuration {

    @Range(min = 1, max = 100)
    private int age;

}
