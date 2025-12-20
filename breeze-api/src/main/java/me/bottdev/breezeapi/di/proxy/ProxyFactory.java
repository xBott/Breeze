package me.bottdev.breezeapi.di.proxy;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.di.annotations.Proxy;

import java.util.Optional;

@RequiredArgsConstructor
public class ProxyFactory {

    private final ProxyHandlerRegistry registry;

    @SuppressWarnings("unchecked")
    public <T> Optional<T> create(Class<T> iface) {

        if (!iface.isAnnotationPresent(Proxy.class))
            throw new IllegalArgumentException("Missing @Proxy");

        CompositeProxyHandler compositeHandler = registry.getComposite(iface);
        if (compositeHandler.isEmpty()) return Optional.empty();

        Object proxy = java.lang.reflect.Proxy.newProxyInstance(
                iface.getClassLoader(),
                new Class[] { iface },
                compositeHandler
        );

        return Optional.of((T) proxy);

    }

}
