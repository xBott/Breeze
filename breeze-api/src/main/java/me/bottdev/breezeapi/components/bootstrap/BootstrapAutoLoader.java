package me.bottdev.breezeapi.components.bootstrap;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.autoload.AutoLoader;

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
