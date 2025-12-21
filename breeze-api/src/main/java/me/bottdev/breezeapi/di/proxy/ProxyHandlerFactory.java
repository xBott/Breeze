package me.bottdev.breezeapi.di.proxy;

public interface ProxyHandlerFactory {

    boolean supports(Class<?> iface);

    ProxyHandler create(Class<?> targetClass);

}
