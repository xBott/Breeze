package me.bottdev.breezeapi.cache.proxy;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.cache.Cache;
import me.bottdev.breezeapi.cache.CacheBuilder;
import me.bottdev.breezeapi.cache.CacheManager;
import me.bottdev.breezeapi.cache.proxy.annotations.Cached;
import me.bottdev.breezeapi.di.proxy.ProxyHandler;
import me.bottdev.breezeapi.di.proxy.ProxyPostHandler;
import me.bottdev.breezeapi.di.proxy.ProxyResult;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Optional;

public class CacheProxyHandler implements ProxyHandler, ProxyPostHandler {

    private final CacheManager cacheManager = new CacheManager();

    private boolean isMethodAnnotated(Method method) {
        return method.isAnnotationPresent(Cached.class);
    }

    private MethodInvocationKey getMethodCacheSignature(Method method, Object[] args) {
        return new MethodInvocationKey(method, args);
    }

    private int getTTL(Method method) {
        Cached cached = method.getAnnotation(Cached.class);
        return cached == null ? 1000 : cached.ttl();
    }

    private Cache<MethodInvocationKey, Object> getCache(Class<?> targetClass) {
        String cacheName = targetClass.getName() + ".method-cache";
        return cacheManager.getOrCreate(
                cacheName,
                new CacheBuilder<MethodInvocationKey, Object>().maxSize(10)
        );
    }

    @Override
    public boolean supports(Class<?> iface) {
        return Cacheable.class.isAssignableFrom(iface);
    }

    @Override
    public ProxyResult invoke(Class<?> targetClass, Object proxy, Method method, Object[] args) throws Throwable {

        if (!isMethodAnnotated(method)) {
            if (method.isDefault()) return invokeDefault(proxy, method, args);
            return ProxyResult.empty();
        }

        Optional<Object> cached = getCached(targetClass, method, args);
        if (cached.isEmpty()) {
            if (method.isDefault()) {
                return invokeDefault(proxy, method, args);
            }
            return ProxyResult.empty();
        }

        return ProxyResult.of(cached.get());

    }

    private Optional<Object> getCached(Class<?> targetClass, Method method, Object[] args) {

        Cache<MethodInvocationKey, Object> cache = getCache(targetClass);
        if (cache == null) return Optional.empty();

        MethodInvocationKey methodCacheSignature = getMethodCacheSignature(method, args);

        return cache.get(methodCacheSignature);
    }

    @Override
    public void invokePost(Class<?> targetClass, Object proxy, Method method, Object[] args, ProxyResult result) {

        if (!isMethodAnnotated(method) || result.isEmpty()) return;

        Object value = result.getValue();
        putCache(targetClass, method, args, value);

    }

    private void putCache(Class<?> targetClass, Method method, Object[] args, Object value) {

        Cache<MethodInvocationKey, Object> cache = getCache(targetClass);
        if (cache == null) return;

        MethodInvocationKey methodCacheSignature = getMethodCacheSignature(method, args);
        int ttl = getTTL(method);

        if (ttl > 0) {
            cache.put(methodCacheSignature, value, Duration.ofMillis(ttl));

        } else {
            cache.put(methodCacheSignature, value);
        }

    }

}
