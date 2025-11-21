package me.bottdev.breezeapi.events;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.Method;

@Getter
@RequiredArgsConstructor
public class ListenerWrapper {

    private final Object instance;
    private final Method method;
    private final int priority;

    public String getSignature() {
        return method.getDeclaringClass().getName() + "#" + method.getName();
    }

    public void accept(Event event) {
        try {
            method.invoke(instance, event);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
