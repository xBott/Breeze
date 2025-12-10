package me.bottdev.breezeapi.di.proxy.composite;

import lombok.RequiredArgsConstructor;
import me.bottdev.breezeapi.di.proxy.ProxyHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.PriorityQueue;

@RequiredArgsConstructor
public class CompositeProxyHandler implements InvocationHandler {

    private final Class<?> targetClass;

    private final PriorityQueue<HandlerPriorityWrapper> handlerQueue = new PriorityQueue<>();

    public boolean isEmpty() {
        return handlerQueue.isEmpty();
    }

    public void add(ProxyHandler handler, int priority) {
        HandlerPriorityWrapper priorityWrapper = new HandlerPriorityWrapper(handler, priority);
        handlerQueue.add(priorityWrapper);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        PriorityQueue<HandlerPriorityWrapper> queue = new PriorityQueue<>(handlerQueue);

        Throwable lastException = null;

        while (!queue.isEmpty()) {

            HandlerPriorityWrapper wrapper = queue.poll();

            try {

                ProxyHandler handler = wrapper.getHandler();

                Object result = handler.invoke(targetClass, proxy, method, args);
                if (result != null) return result;

            } catch (Throwable throwable) {
                lastException = throwable;
            }

        }

        if (lastException != null) throw lastException;

        return null;
    }


}
