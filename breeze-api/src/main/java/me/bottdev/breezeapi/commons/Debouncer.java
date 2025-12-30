package me.bottdev.breezeapi.commons;

import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class Debouncer<T> {

    private final long delay;
    private final ScheduledExecutorService debounceExecutor;
    private final Map<T, ScheduledFuture<?>> debounceTasks = new ConcurrentHashMap<>();

    public Debouncer(String threadName, long delay) {
        this.delay = delay;
        this.debounceExecutor =
                Executors.newSingleThreadScheduledExecutor(runnable -> {
                    Thread thread = new Thread(runnable, threadName);
                    thread.setDaemon(true);
                    return thread;
                });
    }

    public void startDebounce(T target, Consumer<T> handler) {

        ScheduledFuture<?> previous = debounceTasks.get(target);
        if (previous != null) {
            previous.cancel(false);
        }

        ScheduledFuture<?> future = debounceExecutor.schedule(
                () -> onDebounce(target, handler),
                delay,
                TimeUnit.MILLISECONDS
        );

        debounceTasks.put(target, future);
    }

    private void onDebounce(T target, Consumer<T> handler) {
        debounceTasks.remove(target);
        handler.accept(target);
    }

    public void close() {
        debounceExecutor.shutdown();
        debounceTasks.clear();
    }

}
