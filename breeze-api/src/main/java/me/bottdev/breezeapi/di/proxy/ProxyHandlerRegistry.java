package me.bottdev.breezeapi.di.proxy;

import me.bottdev.breezeapi.di.proxy.composite.CompositeProxyHandler;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.SimpleLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProxyHandlerRegistry {

    private final BreezeLogger logger = new SimpleLogger("ProxyHandlerRegistry");

    private final List<Class<? extends ProxyHandler>> HANDLERS = new ArrayList<>();

    public ProxyHandlerRegistry register(Class<? extends ProxyHandler> handler) {
        HANDLERS.add(handler);
        return this;
    }

    private Optional<ProxyHandler> tryCreate(Class<?> clazz, Class<?> iface) {

        try {

            ProxyHandler handler = (ProxyHandler) clazz
                    .getConstructor(Class.class)
                    .newInstance(iface);

            return handler.supports(iface) ? Optional.of(handler) : Optional.empty();

        } catch (ReflectiveOperationException ex) {
            logger.error("Could not create proxy handler for " + iface.getName(), ex);
        }

        return Optional.empty();
    }


    public Optional<ProxyHandler> get(Class<?> iface) {

        for (Class<? extends ProxyHandler> handlerClass : HANDLERS) {

            Optional<ProxyHandler> handler = tryCreate(handlerClass, iface);
            if (handler.isPresent()) {
                return handler;
            }

        }

        return Optional.empty();
    }

    public CompositeProxyHandler getComposite(Class<?> iface) {

        CompositeProxyHandler compositeHandler = new CompositeProxyHandler(iface);

        HANDLERS.stream()
                .map(clazz -> tryCreate(clazz, iface))
                .forEach(optional -> optional.ifPresent(
                        handler -> compositeHandler.add(handler, 0)
                ));


        return compositeHandler;

    }

}
