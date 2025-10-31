package me.bottdev.breezeapi.modules;

import java.net.URL;
import java.net.URLClassLoader;

public class ModuleClassLoader extends URLClassLoader {

    public ModuleClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = findLoadedClass(name);
        if (clazz != null) return clazz;

        if (name.startsWith("me.bottdev.")) {
            try {
                clazz = findClass(name);
            } catch (ClassNotFoundException ignored) {}
        }

        if (clazz == null) {
            try {
                clazz = getParent().loadClass(name);
            } catch (ClassNotFoundException ignored) {}
        }

        if (clazz == null) {
            clazz = findClass(name);
        }

        if (resolve) resolveClass(clazz);
        return clazz;

    }

}
