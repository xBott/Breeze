package me.bottdev.breezeapi.di.proxy;

import me.bottdev.breezeapi.di.annotations.Proxy;

public class ProxyFactory {

    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> iface) {

        if (!iface.isAnnotationPresent(Proxy.class))
            throw new IllegalArgumentException("Missing @Proxy");

        ProxyHandler handler = ProxyHandlerRegistry.getFor(iface);

        return (T) java.lang.reflect.Proxy.newProxyInstance(
                iface.getClassLoader(),
                new Class[] { iface },
                handler
        );
    }

}
