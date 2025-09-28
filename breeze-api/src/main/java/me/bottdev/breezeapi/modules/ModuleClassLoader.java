package me.bottdev.breezeapi.modules;

import java.net.URL;
import java.net.URLClassLoader;

public class ModuleClassLoader extends URLClassLoader {

    public ModuleClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        try {
            return super.loadClass(name, resolve);
        } catch (ClassNotFoundException ignored) {}

        Class<?> clazz = findClass(name);
        if (resolve) {
            resolveClass(clazz);
        }
        return clazz;
    }
}
