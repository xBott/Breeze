package me.bottdev.breezeapi.di.proxy;

import java.lang.reflect.InvocationHandler;

public interface ProxyHandler extends InvocationHandler {
    boolean supports(Class<?> iface);

}
