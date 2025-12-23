package me.bottdev.breezeapi.commons.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;

public class MethodParameterDiscoverer {

    public static String[] getParameterNames(Method method) {
        Parameter[] params = method.getParameters();

        if (params.length == 0) return new String[0];

        if (!params[0].isNamePresent()) {
            return null;
        }

        return Arrays.stream(params)
                .map(Parameter::getName)
                .toArray(String[]::new);
    }


}
