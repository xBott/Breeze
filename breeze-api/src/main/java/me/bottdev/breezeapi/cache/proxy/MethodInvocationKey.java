package me.bottdev.breezeapi.cache.proxy;

import java.lang.reflect.Method;
import java.util.Arrays;

public class MethodInvocationKey {

    private final Method method;
    private final Object[] args;
    private final int hash;

    public MethodInvocationKey(Method method, Object[] args) {
        this.method = method;
        this.args = args == null ? new Object[0] : args.clone();
        this.hash = computeHash();
    }

    private int computeHash() {
        int result = method.hashCode();
        result = 31 * result + Arrays.deepHashCode(args);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MethodInvocationKey that)) return false;
        return method.equals(that.method)
                && Arrays.deepEquals(args, that.args);
    }

    @Override
    public int hashCode() {
        return hash;
    }

}

