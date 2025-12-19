package me.bottdev.breezeapi.resource.source.descriptor;

import me.bottdev.breezeapi.resource.annotations.ResourceSourceDef;
import me.bottdev.breezeapi.resource.source.SourceType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SourceDescriptorFactory {

    public static List<SourceDescriptor> createFromMethod(Method method) {

        List<SourceDescriptor> descriptors = new ArrayList<>();

        Arrays.stream(method.getAnnotations())
                .filter(annotation ->
                        annotation.annotationType().isAnnotationPresent(ResourceSourceDef.class)
                )
                .map(annotation -> {

                    Class<? extends Annotation> annotationType = annotation.annotationType();
                    ResourceSourceDef resourceSourceDef = annotationType.getAnnotation(ResourceSourceDef.class);

                    SourceType sourceType = resourceSourceDef.type();
                    int defaultPriority = resourceSourceDef.defaultPriority();
                    int priority = extractPriority(annotation, defaultPriority);

                    return new SourceDescriptor(annotation, sourceType, priority);

                })
                .sorted(Comparator.comparingInt(SourceDescriptor::getPriority))
                .forEach(descriptors::add);

        return descriptors;

    }

    private static int extractPriority(
            Annotation ann,
            int fallback
    ) {
        try {
            Method m = ann.annotationType().getMethod("priority");
            int value = (int) m.invoke(ann);
            return value == Integer.MIN_VALUE ? fallback : value;

        } catch (NoSuchMethodException e) {
            return fallback;

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
