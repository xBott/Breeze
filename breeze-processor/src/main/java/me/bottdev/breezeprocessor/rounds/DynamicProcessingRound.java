package me.bottdev.breezeprocessor.rounds;

import me.bottdev.breezeprocessor.ProcessingRound;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import java.util.Set;

public abstract class DynamicProcessingRound extends ProcessingRound {

    protected DynamicProcessingRound(Messager messager) {
        super(messager);
    }

    public void process(Set<? extends Element> elements) {

        logRoundStart(elements.size());

        int processedCount = 0;
        for (Element element : elements) {
            if (processElement(element)) {
                processedCount++;
            }
        }

        logRoundEnd(processedCount);
    }

    protected abstract boolean processElement(Element element);

    protected void logRoundStart(int elementCount) {
        messager.printMessage(Diagnostic.Kind.NOTE,
                "[" + getRoundName() + "] Starting processing of " + elementCount + " elements");
    }

    protected void logRoundEnd(int processedCount) {
        messager.printMessage(Diagnostic.Kind.NOTE,
                "[" + getRoundName() + "] Finished processing " + processedCount + " elements");
    }

    protected void logElementProcessing(Element element, boolean success, String message) {
        String status = success ? "✓" : "✗";
        messager.printMessage(Diagnostic.Kind.NOTE,
                "  " + status + " " + element.toString() + ": " + message);
    }

}