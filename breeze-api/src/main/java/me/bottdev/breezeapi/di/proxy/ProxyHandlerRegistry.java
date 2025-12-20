package me.bottdev.breezeapi.di.proxy;

import me.bottdev.breezeapi.commons.priority.PriorityList;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.SimpleTreeLogger;

import java.util.*;

public class ProxyHandlerRegistry {

    private final BreezeLogger logger = new SimpleTreeLogger("ProxyHandlerRegistry");

    private final PriorityList<Class<? extends ProxyHandler>> handlers = new PriorityList<>();

    public ProxyHandlerRegistry register(Class<? extends ProxyHandler> handler, int priority) {
        handlers.add(handler, priority);
        return this;
    }

    private Optional<ProxyHandler> tryCreate(Class<?> clazz, Class<?> iface) {

        try {

            ProxyHandler handler = (ProxyHandler) clazz
                    .getDeclaredConstructor()
                    .newInstance();

            return handler.supports(iface) ? Optional.of(handler) : Optional.empty();

        } catch (ReflectiveOperationException ex) {
            logger.error("Could not create proxy handler for " + iface.getName(), ex);
        }

        return Optional.empty();
    }


    public Optional<ProxyHandler> get(Class<?> iface) {

        for (Class<? extends ProxyHandler> handlerClass : handlers) {

            Optional<ProxyHandler> handler = tryCreate(handlerClass, iface);
            if (handler.isPresent()) {
                return handler;
            }

        }

        return Optional.empty();
    }

    public CompositeProxyHandler getComposite(Class<?> iface) {

        CompositeProxyHandler compositeHandler = new CompositeProxyHandler(iface);

        handlers.stream()
                .map(clazz -> tryCreate(clazz, iface))
                .forEach(optional -> optional.ifPresent(
                        handler -> compositeHandler.add(handler, 0)
                ));


        return compositeHandler;

    }

}
