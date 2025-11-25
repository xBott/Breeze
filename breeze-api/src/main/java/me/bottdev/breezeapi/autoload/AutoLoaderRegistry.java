package me.bottdev.breezeapi.autoload;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.log.BreezeLogger;

import java.util.HashMap;
import java.util.Optional;

@RequiredArgsConstructor
public class AutoLoaderRegistry {

    private static final String loggerPrefix = "[AutoLoaderRegistry]";

    private final BreezeLogger logger;
    private final HashMap<Class<?>, AutoLoader> loaders = new HashMap<>();

    public boolean isRegistered(Class<?> clazz) {
        return loaders.containsKey(clazz);
    }

    public void register(Class<?> clazz, AutoLoader autoLoader) {
        if (isRegistered(clazz)) return;
        loaders.put(clazz, autoLoader);
        logger.info("{} Registered AutoLoader \"{}\" for class \"{}\"",
                loggerPrefix,
                autoLoader.getClass().getSimpleName(),
                clazz.getSimpleName()
        );
    }

    private Optional<AutoLoader> get(Class<?> clazz) {
        if (clazz == null) {
            return Optional.empty();
        }

        AutoLoader loader = loaders.get(clazz);
        if (loader != null) {
            return Optional.of(loader);
        }

        for (Class<?> iface : clazz.getInterfaces()) {
            loader = loaders.get(iface);
            if (loader != null) {
                return Optional.of(loader);
            }
        }

        return get(clazz.getSuperclass());
    }

    public void accept(Object object) {
        Class<?> clazz = object.getClass();

        Optional<AutoLoader> autoLoader = get(clazz);

        if (autoLoader.isEmpty()) {
            return;
        }

        AutoLoader loader = autoLoader.get();
        loader.load(object);
    }


}
