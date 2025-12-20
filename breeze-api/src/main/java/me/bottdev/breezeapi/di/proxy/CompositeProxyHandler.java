package me.bottdev.breezeapi.di.proxy;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.commons.priority.PriorityList;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

@RequiredArgsConstructor
public class CompositeProxyHandler implements InvocationHandler {

    private final Class<?> targetClass;

    private final PriorityList<ProxyHandler> handlers = new PriorityList<>();

    public boolean isEmpty() {
        return handlers.isEmpty();
    }

    public void add(ProxyHandler handler, int priority) {
        handlers.add(handler, priority);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        ProxyResult result = invokeHandlers(proxy, method, args);
        invokePostHandlers(proxy, method, args, result);

        return result.getValue();

    }

    private ProxyResult invokeHandlers(Object proxy, Method method, Object[] args) throws Throwable {

        Throwable lastException = null;
        ProxyResult result = ProxyResult.empty();

        for (ProxyHandler handler : handlers) {

            try {

                ProxyResult newResult = handler.invoke(targetClass, proxy, method, args);

                if (!newResult.isEmpty()) {
                    result = newResult;
                    break;
                }

            } catch (Throwable throwable) {
                lastException = throwable;
            }

        }

        if (lastException != null) throw lastException;

        return result;
    }

    private void invokePostHandlers(Object proxy, Method method, Object[] args, ProxyResult result) {

        handlers.stream()
                .filter(ProxyPostHandler.class::isInstance)
                .map(ProxyPostHandler.class::cast)
                .forEach(handler ->
                        handler.invokePost(targetClass, proxy, method, args, result)
                );

    }


}
