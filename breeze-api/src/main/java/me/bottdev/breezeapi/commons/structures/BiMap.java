package me.bottdev.breezeapi.commons.structures;

import java.util.HashMap;
import java.util.Map;

public class BiMap<K, V> {

    private final Map<K, V> forward = new HashMap<>();
    private final Map<V, K> backward = new HashMap<>();

    public void put(K k, V v) {
        removeByKey(k);
        removeByValue(v);
        forward.put(k, v);
        backward.put(v, k);
    }

    public boolean containsKey(K k) {
        return forward.containsKey(k);
    }

    public boolean containsValue(V v) {
        return backward.containsKey(v);
    }

    public V getByKey(K k) {
        return forward.get(k);
    }

    public K getByValue(V v) {
        return backward.get(v);
    }

    public void removeByKey(K k) {
        V v = forward.remove(k);
        if (v != null) backward.remove(v);
    }

    public void removeByValue(V v) {
        K k = backward.remove(v);
        if (k != null) forward.remove(k);
    }

    public void clear() {
        forward.clear();
        backward.clear();
    }

}
