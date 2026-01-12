package me.bottdev.breezeapi.process;

import lombok.Getter;
import lombok.Setter;
import me.bottdev.breezeapi.log.trace.LogTrace;
import me.bottdev.breezeapi.log.trace.TraceScope;

import java.util.*;

@Getter
public class PipelineContext {

    private final Map<PipelineContextKey<?>, Object> data = new HashMap<>();
    private final List<PipelineError> errors = new ArrayList<>();
    private final LogTrace trace = new LogTrace();
    @Setter
    private int depthOffset = 0;

    @Setter
    private boolean failed = false;

    public <T> void put(PipelineContextKey<T> key, T value) {
        data.put(key, value);
    }

    public <T> Optional<T> get(PipelineContextKey<T> key) {
        Object value = data.get(key);
        if (value == null) return Optional.empty();
        return Optional.of(key.getType().cast(value));
    }

    public boolean contains(PipelineContextKey<?> key) {
        return data.containsKey(key);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public void addError(PipelineError error) {
        errors.add(error);
        if (error.isCritical()) {
            setFailed(true);
        }
    }

    public TraceScope scope(String name) {
        return trace.scope(name, depthOffset);
    }

}
