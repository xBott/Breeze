package me.bottdev.breezeprocessor.processors;

import com.google.auto.service.AutoService;
import me.bottdev.breezeapi.config.autoload.AutoLoadSerializer;
import me.bottdev.breezeapi.config.autoload.annotations.AutoLoad;
import me.bottdev.breezeapi.di.annotations.Component;
import me.bottdev.breezeapi.di.annotations.Supplier;
import me.bottdev.breezeapi.index.processors.AbstractIndexProcessor;
import me.bottdev.breezeapi.index.processors.ClassIndexProcessor;
import me.bottdev.breezeapi.index.types.BreezeAutoLoadIndex;
import me.bottdev.breezeapi.index.types.BreezeSupplierIndex;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

@AutoService(Processor.class)
@SupportedAnnotationTypes("me.bottdev.breezeapi.config.autoload.annotations.AutoLoad")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class BreezeAutoLoadIndexProcessor extends AbstractIndexProcessor<BreezeAutoLoadIndex> implements ClassIndexProcessor {


    @Override
    protected String getOutputFileName() {
        return "auto-load-index";
    }

    @Override
    protected Class<? extends Annotation> getAnnotationType() {
        return Supplier.class;
    }

    @Override
    protected BreezeAutoLoadIndex createIndex() {
        return new BreezeAutoLoadIndex();
    }

    @Override
    protected void processElement(Element element) {
        processClass(element);
    }

    @Override
    public void extractDataFromClass(TypeElement typeElement) {
        String classPath = typeElement.getQualifiedName().toString();
        AutoLoad annotation = (AutoLoad) typeElement.getAnnotation(getAnnotationType());

        BreezeAutoLoadIndex index = getIndex();

        String filePath = annotation.path();
        AutoLoadSerializer serializer = annotation.serializer();

        BreezeAutoLoadIndex.Entry entry = BreezeAutoLoadIndex.Entry.builder()
                .classPath(classPath)
                .filePath(filePath)
                .serializer(serializer)
                .build();

        index.addEntry(entry);

    }

}
