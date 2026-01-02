package me.bottdev.breezeapi.autoload;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.log.BreezeLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
public class AutoLoaderRegistry {

    private final BreezeLogger logger;
    private final HashMap<Class<?>, AutoLoader> loaders = new HashMap<>();

    public boolean isRegistered(Class<?> clazz) {
        return loaders.containsKey(clazz);
    }

    public AutoLoaderRegistry register(Class<?> clazz, AutoLoader autoLoader) {
        if (isRegistered(clazz)) return this;
        loaders.put(clazz, autoLoader);
        logger.info("Registered AutoLoader \"{}\" for class \"{}\"",
                autoLoader.getClass().getSimpleName(),
                clazz.getSimpleName()
        );
        return this;
    }

    private List<AutoLoader> get(Class<?> clazz) {

        List<AutoLoader> autoLoaders = new ArrayList<>();

        if (clazz == null) {
            return autoLoaders;
        }

        AutoLoader loader = loaders.get(clazz);
        if (loader != null) {
            autoLoaders.add(loader);
        }

        for (Class<?> iface : clazz.getInterfaces()) {
            loader = loaders.get(iface);
            if (loader != null) {
                autoLoaders.add(loader);
            }
        }

        autoLoaders.addAll(get(clazz.getSuperclass()));

        return autoLoaders;
    }

    public void accept(Object object) {
        Class<?> clazz = object.getClass();

        List<AutoLoader> autoLoaders = get(clazz);

        autoLoaders.forEach(autoLoader -> autoLoader.load(object));
    }


}
