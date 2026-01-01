package me.bottdev.breezeapi.modules;

import java.net.URL;
import java.net.URLClassLoader;

@Deprecated
public class ModuleClassLoader extends URLClassLoader {

    public ModuleClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {

            Class<?> clazz = findLoadedClass(name);
            if (clazz != null) return clazz;

            if (name.startsWith("java.")
                    || name.startsWith("me.bottdev.api.")) {

                clazz = getParent().loadClass(name);
            } else {
                try {
                    clazz = findClass(name);
                } catch (ClassNotFoundException e) {
                    clazz = getParent().loadClass(name);
                }
            }

            if (resolve) resolveClass(clazz);
            return clazz;
        }
    }

}
