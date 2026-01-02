package me.bottdev.breezeapi.events;

import me.bottdev.breezeapi.di.annotations.Inject;
import me.bottdev.breezeapi.events.annotations.Listen;
import me.bottdev.breezeapi.lifecycle.Lifecycle;
import me.bottdev.breezeapi.log.TreeLogger;

import java.lang.reflect.Method;
import java.util.*;

public abstract class EventBus extends Lifecycle {

    private final Map<Class<? extends Event>, ListenerContainer> containers = new HashMap<>();

    private final TreeLogger mainLogger;

    @Inject
    public EventBus(TreeLogger mainLogger) {
        this.mainLogger = mainLogger;
    }

    @Override
    protected void onShutdown() {
        unregisterAllListeners();
    }

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
        mainLogger.info("Registered listener \"{}\" for event \"{}\"!", listener.getSignature(), event.getSimpleName());
    }

    public void unregisterListener(Class<? extends Event> event, ListenerWrapper listener) {
        ListenerContainer container = getContainer(event);
        if (!container.isRegistered(listener)) return;
        container.unregister(listener);
        removeContainerIfEmpty(event);
        mainLogger.info("Unregistered listener \"{}\" for event \"{}\"!", listener.getSignature(), event.getSimpleName());
    }

    @SuppressWarnings("unchecked")
    public void registerListeners(Listener listener) {

        String className = listener.getClass().getSimpleName();
        mainLogger.withSection("Registering listeners from object " + className, "Event Registration", () -> {

            for (Method method : listener.getClass().getDeclaredMethods()) {
                if (!method.isAnnotationPresent(Listen.class)) continue;

                String name = method.getName();

                Class<?>[] params = method.getParameterTypes();
                if (params.length != 1 || !Event.class.isAssignableFrom(params[0])) {
                    mainLogger.warn("Could not register listener \"{}\". Listener method must have only one Event parameter!", name);
                }

                Class<? extends Event> eventType = (Class<? extends Event>) params[0];
                Listen annotation = method.getAnnotation(Listen.class);
                method.setAccessible(true);

                ListenerWrapper methodWrapper = new ListenerWrapper(listener, method, annotation.priority());
                registerListener(eventType, methodWrapper);

            }

        });

        mainLogger.info("Registered listeners from object \"{}\"!", className);

    }

    public void unregisterListeners(Listener listener) {

        String className = listener.getClass().getSimpleName();

        for (Map.Entry<Class<? extends Event>, ListenerContainer> entry : new HashMap<>(containers).entrySet()) {

            Class<? extends Event> eventType = entry.getKey();
            ListenerContainer container = entry.getValue();

            container.get().stream()
                    .filter(wrapper -> wrapper.getInstance() == listener)
                    .toList()
                    .forEach(listenerWrapper -> {
                        unregisterListener(eventType, listenerWrapper);
                    });

            if (container.isEmpty()) {
                containers.remove(eventType);
            }

        }

        mainLogger.info("Unregistered listeners from object \"{}\"!", className);

    }

    public void unregisterAllListeners() {
        containers.clear();
        mainLogger.info("Unregistered all listeners!");
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
