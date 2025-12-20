package me.bottdev.breezeapi.di.proxy;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ProxyResult {

    public static ProxyResult empty() {
        return new ProxyResult(null);
    }

    public static ProxyResult of(Object value) {
        return new ProxyResult(value);
    }

    private final Object value;

    public boolean isEmpty() {
        return value == null;
    }

}
