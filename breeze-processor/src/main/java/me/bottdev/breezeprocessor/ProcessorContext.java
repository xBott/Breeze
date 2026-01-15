package me.bottdev.breezeprocessor;

import lombok.Getter;

import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

@Getter
public class ProcessorContext {
    
    private final Types typeUtils;
    private final Elements elementUtils;
    private final Messager messager;
    private final Filer filer;
    
    public ProcessorContext(Types typeUtils, Elements elementUtils, 
                           Messager messager, Filer filer) {
        this.typeUtils = typeUtils;
        this.elementUtils = elementUtils;
        this.messager = messager;
        this.filer = filer;
    }
}