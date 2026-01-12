package me.bottdev.breezeapi.process;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public final class PipelineContextKey<T> {

    private final String name;
    private final Class<T> type;

    public static <T> PipelineContextKey<T> of(String name, Class<T> type) {
        return new PipelineContextKey<>(name, type);
    }

}
