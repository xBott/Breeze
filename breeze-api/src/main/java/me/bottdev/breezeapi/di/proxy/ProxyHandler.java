package me.bottdev.breezeapi.di.proxy;

import java.lang.reflect.Method;

public interface ProxyHandler {

    boolean supports(Class<?> iface);

    Object invoke(Class<?> targetClass, Object proxy, Method method, Object[] args) throws Throwable;

}
