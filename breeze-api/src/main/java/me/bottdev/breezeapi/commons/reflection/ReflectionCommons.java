package me.bottdev.breezeapi.commons.reflection;

public class ReflectionCommons {

    public static String asFieldName(Class<?> clazz) {
        String name = clazz.getSimpleName();
        name = name.substring(0, 1).toLowerCase() + name.substring(1);
        return name;
    }

}
