package me.bottdev.breezeapi.di.proxy;

import java.lang.reflect.Method;

public interface ProxyPostHandler {

    void invokePost(Class<?> targetClass, Object proxy, Method method, Object[] args, ProxyResult result);

}
