package me.bottdev.breezeapi.di.proxy;

import java.util.ArrayList;
import java.util.List;

public class ProxyHandlerRegistry {

    private static final List<Class<? extends ProxyHandler>> HANDLERS = new ArrayList<>();

    public static void register(Class<? extends ProxyHandler> handler) {
        HANDLERS.add(handler);
    }

    public static ProxyHandler getFor(Class<?> iface) {
        for (Class<? extends ProxyHandler> handlerClass : HANDLERS) {
            try {
                ProxyHandler handler = handlerClass.getConstructor(Class.class).newInstance(iface);
                if (handler.supports(iface)) {
                    return handler;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalStateException("No ProxyHandler found for " + iface.getName());
    }

}
