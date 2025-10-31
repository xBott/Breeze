package me.bottdev.breezeapi.index.processors;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

public interface ClassIndexProcessor {

    void extractDataFromClass(TypeElement typeElement);

    default void processClass(Element element) {
        if (element.getKind() != ElementKind.CLASS) return;
        TypeElement typeElement = (TypeElement) element;
        extractDataFromClass(typeElement);
    }

}
