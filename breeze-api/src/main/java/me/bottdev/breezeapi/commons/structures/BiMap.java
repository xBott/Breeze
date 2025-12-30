package me.bottdev.breezeapi.commons.structures;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BiMap<K, V> {

    private final Map<K, V> forward = new HashMap<>();
    private final Map<V, K> backward = new HashMap<>();

    public void put(K k, V v) {
        removeByKey(k);
        removeByValue(v);
        forward.put(k, v);
        backward.put(v, k);
    }

    public Set<K> keySet() {
        return new HashSet<>(forward.keySet());
    }

    public Set<V> values() {
        return new HashSet<>(forward.values());
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

    public V removeByKey(K k) {
        V v = forward.remove(k);
        if (v != null) backward.remove(v);
        return v;
    }

    public K removeByValue(V v) {
        K k = backward.remove(v);
        if (k != null) forward.remove(k);
        return k;
    }

    public void clear() {
        forward.clear();
        backward.clear();
    }

}
