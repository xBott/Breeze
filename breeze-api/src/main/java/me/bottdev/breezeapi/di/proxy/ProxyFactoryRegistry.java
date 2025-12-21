package me.bottdev.breezeapi.di.proxy;

import me.bottdev.breezeapi.commons.priority.PriorityList;
import me.bottdev.breezeapi.di.annotations.Proxy;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class ProxyFactoryRegistry {

    private final PriorityList<ProxyHandlerFactory> factories = new PriorityList<>();

    public ProxyFactoryRegistry register(ProxyHandlerFactory supplier, int priority) {
        factories.add(supplier, priority);
        return this;
    }

    private Optional<ProxyHandler> tryCreate(ProxyHandlerFactory factory, Class<?> iface) {

        if (!factory.supports(iface)) return Optional.empty();
        ProxyHandler handler = factory.create(iface);

        return Optional.of(handler);

    }

    public CompositeProxyHandler getComposite(Class<?> iface) {

        CompositeProxyHandler compositeHandler = new CompositeProxyHandler(iface);

        factories.wrapperStream()
                .map(wrapper -> {
                    int priority = wrapper.getPriority();
                    ProxyHandlerFactory factory = wrapper.getValue();
                    Optional<ProxyHandler> handlerOptional = tryCreate(factory, iface);
                    return Pair.of(priority, handlerOptional);
                })
                .forEach(pair -> {
                    int priority = pair.getLeft();
                    Optional<ProxyHandler> handlerOptional = pair.getRight();
                    handlerOptional.ifPresent(handler ->
                            compositeHandler.add(handler, priority)
                    );
                });


        return compositeHandler;

    }

    @SuppressWarnings("unchecked")
    public <T> Optional<T> createObject(Class<T> iface) {

        if (!iface.isAnnotationPresent(Proxy.class))
            throw new IllegalArgumentException("Missing @Proxy");

        CompositeProxyHandler compositeHandler = getComposite(iface);
        if (compositeHandler.isEmpty()) return Optional.empty();

        Object proxy = java.lang.reflect.Proxy.newProxyInstance(
                iface.getClassLoader(),
                new Class[] { iface },
                compositeHandler
        );

        return Optional.of((T) proxy);

    }

}
