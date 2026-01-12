package me.bottdev.breezeapi.log;

import lombok.Getter;
import lombok.Setter;
import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.log.types.SimpleLogger;

@Getter
@Setter
public class BreezeLoggerFactory {

    private BreezeLogPlatform platform;

    @Inject
    public BreezeLoggerFactory(BreezeLogPlatform platform) {
        this.platform = platform;
    }

    public SimpleLogger simple(String name) {
        return new SimpleLogger(platform, name, LogLevel.INFO);
    }

    public SimpleLogger simple(String name, LogLevel level) {
        return new SimpleLogger(platform, name, level);
    }


}
