package me.bottdev.breezeapi.commons.structures.priority;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PriorityWrapper<T> {

    private final T value;
    private final int priority;

}
