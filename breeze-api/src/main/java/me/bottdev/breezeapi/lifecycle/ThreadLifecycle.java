package me.bottdev.breezeapi.lifecycle;

import lombok.Getter;
import lombok.Setter;

public abstract class ThreadLifecycle extends Lifecycle {

    @Getter
    @Setter
    private LifecycleState state = LifecycleState.DEAD;

    @Getter
    private Thread thread;
    @Getter
    private volatile boolean isRunning = false;

    @Override
    public void start() {
        if (state == LifecycleState.ALIVE) return;
        setState(LifecycleState.ALIVE);
        startThread();
        onStart();
    }

    @Override
    public void shutdown() {
        if (state == LifecycleState.DEAD) return;
        setState(LifecycleState.DEAD);
        stopThread();
        onShutdown();
    }

    private void startThread() {
        if (isRunning) return;
        isRunning = true;

        thread = new Thread(this::threadRun, getThreadName());
        thread.setDaemon(isDaemon());
        thread.start();
    }

    private void stopThread() {
        if (!isRunning) return;
        isRunning = false;

        if (thread != null) {
            thread.interrupt();
        }
    }

    protected abstract String getThreadName();

    protected abstract boolean isDaemon();

    protected abstract void threadRun();

}
