package me.bottdev.breezeapi.di.proxy.composite;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.di.proxy.ProxyHandler;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public class HandlerPriorityWrapper implements Comparable<HandlerPriorityWrapper> {

    private final ProxyHandler handler;
    private final int priority;

    @Override
    public int compareTo(@NotNull HandlerPriorityWrapper other) {
        return Integer.compare(priority, other.priority);
    }
}
