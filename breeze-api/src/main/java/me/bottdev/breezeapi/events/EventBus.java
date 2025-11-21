package me.bottdev.breezeapi.events;

import me.bottdev.breezeapi.events.annotations.Listener;
import me.bottdev.breezeapi.log.BreezeLogger;
import me.bottdev.breezeapi.log.SimpleLogger;

import java.lang.reflect.Method;
import java.util.*;

public class EventBus {

    private final BreezeLogger logger = new SimpleLogger("EventBus");
    private final Map<Class<? extends Event>, ListenerContainer> containers = new HashMap<>();

    private ListenerContainer getContainer(Class<? extends Event> event) {
        return containers.getOrDefault(event, new ListenerContainer());
    }

    private void putContainerIfAbsent(Class<? extends Event> event, ListenerContainer container) {
        containers.putIfAbsent(event, container);
    }

    private void removeContainerIfEmpty(Class<? extends Event> event) {
        ListenerContainer container = getContainer(event);
        if (!container.isEmpty()) return;
        containers.remove(event);
    }

    public void registerListener(Class<? extends Event> event, ListenerWrapper listener) {
        ListenerContainer container = getContainer(event);
        if (container.isRegistered(listener)) return;
        container.register(listener);
        putContainerIfAbsent(event, container);
        logger.info("Registered listener \"{}\" for event \"{}\"!", listener.getSignature(), event.getSimpleName());
    }

    public void unregisterListener(Class<? extends Event> event, ListenerWrapper listener) {
        ListenerContainer container = getContainer(event);
        if (!container.isRegistered(listener)) return;
        container.unregister(listener);
        removeContainerIfEmpty(event);
        logger.info("Unregistered listener \"{}\" for event \"{}\"!", listener.getSignature(), event.getSimpleName());
    }

    @SuppressWarnings("unchecked")
    public void registerListeners(Object object) {

        logger.info("Registering listeners from object \"{}\"...", object.getClass().getSimpleName());

        for (Method method : object.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Listener.class)) continue;

            String name = method.getName();

            Class<?>[] params = method.getParameterTypes();
            if (params.length != 1 || !Event.class.isAssignableFrom(params[0])) {
                logger.warn("Could not register listener \"{}\". Listener method must have only one Event parameter!", name);
            }

            Class<? extends Event> eventType = (Class<? extends Event>) params[0];
            Listener annotation = method.getAnnotation(Listener.class);
            method.setAccessible(true);

            ListenerWrapper listener = new ListenerWrapper(object, method, annotation.priority());
            registerListener(eventType, listener);

        }

        logger.info("Registered listeners from object \"{}\"!", object.getClass().getSimpleName());

    }

    public void unregisterListeners(Object object) {

        logger.info("Unregistering listeners from object \"{}\"...", object.getClass().getSimpleName());

        for (Map.Entry<Class<? extends Event>, ListenerContainer> entry : new HashMap<>(containers).entrySet()) {

            Class<? extends Event> eventType = entry.getKey();
            ListenerContainer container = entry.getValue();

            container.get().stream()
                    .filter(wrapper -> wrapper.getInstance() == object)
                    .toList()
                    .forEach(listenerWrapper -> {
                        unregisterListener(eventType, listenerWrapper);
                    });

            if (container.isEmpty()) {
                containers.remove(eventType);
            }

        }

        logger.info("Unregistered listeners from object \"{}\"!", object.getClass().getSimpleName());

    }

    public void unregisterAllListeners() {
        containers.clear();
        logger.info("Unregistered all listeners!");
    }

    public void call(Event event) {

        Class<? extends Event> eventClass = event.getClass();

        ListenerContainer container = getContainer(eventClass);
        if (container.isEmpty()) return;

        Collection<ListenerWrapper> listeners = container.get();

        listeners.stream()
                .sorted(Comparator.comparingInt(ListenerWrapper::getPriority))
                .forEach(listenerWrapper -> listenerWrapper.accept(event));

    }
    
}
