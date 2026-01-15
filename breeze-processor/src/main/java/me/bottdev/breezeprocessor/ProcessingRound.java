package me.bottdev.breezeprocessor;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import java.util.Set;

public abstract class ProcessingRound {

    protected final Messager messager;

    protected ProcessingRound(Messager messager) {
        this.messager = messager;
    }

    protected abstract String getRoundName();

}