package me.bottdev.breezeapi.bootstrap;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.autoload.AutoLoader;
import me.bottdev.breezeapi.events.EventBus;
import me.bottdev.breezeapi.events.Listener;

@Getter
@RequiredArgsConstructor
public class BootstrapAutoLoader implements AutoLoader {

    @Override
    public void load(Object object) {
        if (object instanceof Bootstrap bootstrap) {
            bootstrap.bootstrap();
        }
    }

}
