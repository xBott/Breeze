package me.bottdev.breezeprocessor.rounds;

import lombok.Getter;
import me.bottdev.breezeprocessor.ProcessingRound;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;

public class ClassIndexRound extends DynamicProcessingRound {

    @FunctionalInterface
    public interface Handler {
        void handle(TypeElement element);
    }

    @Getter
    private final String roundName;
    private final Handler handler;

    public ClassIndexRound(Messager messager, String roundName, Handler handler) {
        super(messager);
        this.roundName = roundName;
        this.handler = handler;
    }

    @Override
    protected boolean processElement(Element element) {

        if (element.getKind() != ElementKind.CLASS && element.getKind() != ElementKind.INTERFACE) return false;
        TypeElement typeElement = (TypeElement) element;

        handler.handle(typeElement);

        logElementProcessing(
                element,
                true,
                "Element " + typeElement.getQualifiedName().toString() + " was processed successfully."
        );

        return true;

    }

}
