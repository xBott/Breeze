package me.bottdev.breezeprocessor.processors;

import com.google.auto.service.AutoService;
import me.bottdev.breezeapi.di.SupplyType;
import me.bottdev.breezeapi.di.annotations.Component;
import me.bottdev.breezeapi.modules.annotations.ModuleInfo;
import me.bottdev.breezeprocessor.AbstractBreezeAnnotationProcessor;
import me.bottdev.breezeprocessor.ClassInfo;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;

@AutoService(Processor.class)
@SupportedAnnotationTypes("me.bottdev.breezeapi.modules.annotations.ModuleInfo")
@SupportedSourceVersion(SourceVersion.RELEASE_23)
public class BreezeModuleInfoProcessor extends AbstractBreezeAnnotationProcessor {

    @Override
    protected String getOutputFileName() {
        return "breeze-modules-index";
    }

    @Override
    protected Class<? extends Annotation> getAnnotationClass() {
        return ModuleInfo.class;
    }

    @Override
    protected void writeLine(PrintWriter writer, ClassInfo classInfo) {
        ModuleInfo component = (ModuleInfo) classInfo.getAnnotation();
        String name = component.name();
        String version = component.version();
        String line = classInfo.getClassName() + "," + name + "," + version;
        writer.println(line);
    }
}
