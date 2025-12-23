package me.bottdev.breezeapi.lifecycle;

import lombok.Getter;
import lombok.Setter;

public abstract class Lifecycle {

    @Getter
    @Setter
    private LifecycleState state = LifecycleState.DEAD;

    public void start() {
        if (state == LifecycleState.ALIVE) return;
        setState(LifecycleState.ALIVE);
        onStart();
    }

    public void shutdown() {
        if (state == LifecycleState.DEAD) return;
        setState(LifecycleState.DEAD);
        onShutdown();
    }

    protected abstract void onStart();

    protected abstract void onShutdown();

}
