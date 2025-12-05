package me.bottdev.breezeapi.di.proxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProxyHandlerRegistry {

    private final List<Class<? extends ProxyHandler>> HANDLERS = new ArrayList<>();

    public ProxyHandlerRegistry register(Class<? extends ProxyHandler> handler) {
        HANDLERS.add(handler);
        return this;
    }

    public Optional<ProxyHandler> get(Class<?> iface) {
        for (Class<? extends ProxyHandler> handlerClass : HANDLERS) {
            try {
                ProxyHandler handler = handlerClass.getConstructor(Class.class).newInstance(iface);
                if (handler.supports(iface)) {
                    return Optional.of(handler);
                }
            } catch (Exception e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

}
