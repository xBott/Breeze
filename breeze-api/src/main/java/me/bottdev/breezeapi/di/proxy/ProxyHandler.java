package me.bottdev.breezeapi.di.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public interface ProxyHandler {

    boolean supports(Class<?> iface);

    ProxyResult invoke(Class<?> targetClass, Object proxy, Method method, Object[] args) throws Throwable;

    default ProxyResult invokeDefault(Object proxy, Method method, Object[] args) throws Throwable {
        Object value = InvocationHandler.invokeDefault(proxy, method, args);
        return ProxyResult.of(value);
    }

}
