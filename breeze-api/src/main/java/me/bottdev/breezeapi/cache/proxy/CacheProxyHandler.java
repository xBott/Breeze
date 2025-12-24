package me.bottdev.breezeapi.cache.proxy;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.cache.Cache;
import me.bottdev.breezeapi.cache.CacheBuilder;
import me.bottdev.breezeapi.cache.CacheManager;
import me.bottdev.breezeapi.cache.proxy.annotations.CacheEvict;
import me.bottdev.breezeapi.cache.proxy.annotations.CachePut;
import me.bottdev.breezeapi.commons.reflection.MethodParameterDiscoverer;
import me.bottdev.breezeapi.di.proxy.ProxyHandler;
import me.bottdev.breezeapi.di.proxy.ProxyPostHandler;
import me.bottdev.breezeapi.di.proxy.ProxyResult;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.*;

@RequiredArgsConstructor
public class CacheProxyHandler implements ProxyHandler, ProxyPostHandler {

    private final CacheManager cacheManager;

    private Map<String, Object> getCacheSubKeys(Method method, Object[] args) {

        String[] names = MethodParameterDiscoverer.getParameterNames(method);

        if (names == null || args == null || names.length != args.length) {
            return Map.of();
        }

        Map<String, Object> cacheSubKeys = new LinkedHashMap<>();

        for (int i = 0; i < names.length; i++) {
            cacheSubKeys.put(names[i], args[i]);
        }

        return cacheSubKeys;
    }

    private Optional<Cache<String, Object>> getCache(String group) {
        return cacheManager.get(group);
    }

    private Cache<String, Object> getOrCreateCache(String group, int size) {
        return cacheManager.getOrCreate(
                group,
                new CacheBuilder<String, Object>().maxSize(size)
        );
    }

    private String getCacheKey(String format, Map<String, Object> cacheSubKeys) {
        String result = format;

        for (Map.Entry<String, Object> entry : cacheSubKeys.entrySet()) {
            String placeholder = "{" + entry.getKey() + "}";
            result = result.replace(placeholder, String.valueOf(entry.getValue()));
        }

        return result;
    }


    @Override
    public ProxyResult invoke(Class<?> targetClass, Object proxy, Method method, Object[] args) throws Throwable {

        if (method.isAnnotationPresent(CachePut.class)) {
            return invokeCachePut(proxy, method, args);

        } else if (method.isAnnotationPresent(CacheEvict.class)) {
            return invokeCacheEvict(proxy, method, args);

        } else if (method.isDefault()) {
            return invokeDefault(proxy, method, args);
        }

        return ProxyResult.empty();
    }

    private ProxyResult invokeCachePut(Object proxy, Method method, Object[] args) throws Throwable {

        CachePut cachePut = method.getAnnotation(CachePut.class);
        String group = cachePut.group();
        String keyFormat = cachePut.key();

        Map<String, Object> cacheSubKeys = getCacheSubKeys(method, args);
        String cacheKey = getCacheKey(keyFormat, cacheSubKeys);

        Optional<Object> cached = getCached(group, cacheKey);
        if (cached.isPresent()) return ProxyResult.of(cached.get());

        if (method.isDefault()) return invokeDefault(proxy, method, args);

        return ProxyResult.empty();

    }

    private ProxyResult invokeCacheEvict(Object proxy, Method method, Object[] args) throws Throwable {

        CacheEvict cachePut = method.getAnnotation(CacheEvict.class);
        String group = cachePut.group();
        String keyFormat = cachePut.key();
        boolean isAll = cachePut.all();

        Map<String, Object> cacheSubKeys = getCacheSubKeys(method, args);

        if (isAll) {
            evictCacheAll(group);
        } else {
            String cacheKey = getCacheKey(keyFormat, cacheSubKeys);
            evictCache(group, cacheKey);
        }

        if (method.isDefault()) return invokeDefault(proxy, method, args);

        return ProxyResult.empty();

    }

    @Override
    public void invokePost(Class<?> targetClass, Object proxy, Method method, Object[] args, ProxyResult result) {

        if (!method.isAnnotationPresent(CachePut.class) || result.isEmpty()) return;

        CachePut cachePut = method.getAnnotation(CachePut.class);
        String group = cachePut.group();
        String keyFormat = cachePut.key();
        int size = cachePut.size();
        int ttl = cachePut.ttl();

        Map<String, Object> cacheSubKeys = getCacheSubKeys(method, args);
        String key = getCacheKey(keyFormat, cacheSubKeys);

        Object value = result.getValue();
        putCache(group, key, value, size, ttl);

    }

    private Optional<Object> getCached(String group, String key) {
        return getCache(group).map(cache -> cache.get(key));
    }

    private void putCache(String group, String key, Object value, int size, int ttl) {

        Cache<String, Object> cache = getOrCreateCache(group, size);
        if (cache == null) return;

        if (ttl > 0) {
            cache.put(key, value, Duration.ofMillis(ttl));

        } else {
            cache.put(key, value);
        }

    }

    private void evictCacheAll(String group) {
        getCache(group).ifPresent(Cache::clear);
    }

    private void evictCache(String group, String key) {
        getCache(group).ifPresent(cache -> cache.remove(key));
    }

}
