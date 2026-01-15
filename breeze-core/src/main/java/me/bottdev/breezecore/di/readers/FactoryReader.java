package me.bottdev.breezecore.di.readers;

import me.bottdev.breezeapi.di.ContextIndexReader;
import me.bottdev.breezeapi.di.BreezeContext;
import me.bottdev.breezeapi.di.BeanScope;
import me.bottdev.breezeapi.di.annotations.Build;
import me.bottdev.breezeapi.di.exceptions.ContextReadException;
import me.bottdev.breezeapi.index.types.FactoryIndex;

import java.lang.reflect.Method;

public class FactoryReader implements ContextIndexReader<FactoryIndex> {

    @Override
    public Class<FactoryIndex> getIndexClass() {
        return FactoryIndex.class;
    }

    @Override
    public void read(BreezeContext context, ClassLoader classLoader, FactoryIndex index)
            throws ContextReadException
    {
        index.getEntries().forEach(entry -> {
            try {
                Class<?> clazz = classLoader.loadClass(entry.getClassPath());
                Object instance = clazz.getDeclaredConstructor().newInstance();
                bindBeans(context, clazz, instance);

            } catch (Exception ex) {
                throw new ContextReadException(
                        "Failed to read factories from ",
                        ex
                );

            }
        });
    }

    private void bindBeans(BreezeContext context, Class<?> clazz, Object instance) throws ContextReadException {

        try {

            for (Method method : clazz.getDeclaredMethods()) {

                if (!method.isAnnotationPresent(Build.class)) continue;
                Build build = method.getAnnotation(Build.class);

                BeanScope type = build.type();
                String methodName = method.getName();
                Class<?> returnType = method.getReturnType();

                if (method.getReturnType() == void.class) {
                    throw new ContextReadException(
                            "@Build method must return a value: " + method
                    );
                }

                if (method.getParameterCount() != 0) {
                    throw new ContextReadException(
                            "@Build methods with parameters are not supported: " + method
                    );
                }


                context.bind(returnType)
                        .qualified(methodName)
                        .scope(type)
                        .failure(() -> {
                            throw new ContextReadException(
                                    "Failed to register bean: " + methodName
                            );
                        })
                        .unchecked(() -> {
                            try {
                                return method.invoke(instance);

                            } catch (Exception ex) {
                                throw new ContextReadException(
                                        "Failed to invoke build method: " + methodName, ex
                                );

                            }
                        });

            }

        } catch (Exception ex) {
            throw new ContextReadException(
                    "Failed to bind beans from factory: " + instance.getClass().getName(),
                    ex
            );

        }

    }



}

