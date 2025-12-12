package me.bottdev.breezeapi.di;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Getter
@RequiredArgsConstructor
public class ReaderPriorityWrapper implements Comparable<ReaderPriorityWrapper> {

    private final ContextIndexReader<?> reader;
    private final int priority;

    @Override
    public int compareTo(@NotNull ReaderPriorityWrapper other) {
        return Integer.compare(priority, other.priority);
    }
}
