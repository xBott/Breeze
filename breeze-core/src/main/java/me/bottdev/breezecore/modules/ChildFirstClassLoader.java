package me.bottdev.breezecore.modules;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;

public class ChildFirstClassLoader extends URLClassLoader {

    public ChildFirstClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected synchronized Class<?> loadClass(String name, boolean resolve)
            throws ClassNotFoundException {

        Class<?> clazz = findLoadedClass(name);
        if (clazz == null) {

            if (name.startsWith("java.")) {
                clazz = getParent().loadClass(name);

            } else {
                try {
                    clazz = findClass(name);
                } catch (ClassNotFoundException e) {
                    clazz = getParent().loadClass(name);
                }
            }
        }

        if (resolve) {
            resolveClass(clazz);
        }

        return clazz;
    }

    @Override
    public URL getResource(String name) {
        URL url = findResource(name);
        if (url != null) return url;
        return getParent().getResource(name);
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        Enumeration<URL> local = findResources(name);
        Enumeration<URL> parent = getParent().getResources(name);
        return new CompoundEnumeration<>(new Enumeration[]{local, parent});
    }
}
