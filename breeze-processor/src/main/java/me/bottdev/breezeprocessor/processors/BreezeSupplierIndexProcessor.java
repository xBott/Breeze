package me.bottdev.breezeprocessor.processors;

import com.google.auto.service.AutoService;
import me.bottdev.breezeapi.di.annotations.Supplier;
import me.bottdev.breezeapi.index.processors.AbstractIndexProcessor;
import me.bottdev.breezeapi.index.processors.ClassIndexProcessor;
import me.bottdev.breezeapi.index.types.BreezeSupplierIndex;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.lang.annotation.Annotation;

@AutoService(Processor.class)
@SupportedAnnotationTypes("me.bottdev.breezeapi.di.annotations.Supplier")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class BreezeSupplierIndexProcessor extends AbstractIndexProcessor<BreezeSupplierIndex> implements ClassIndexProcessor {


    @Override
    protected String getOutputFileName() {
        return "supplier-index";
    }

    @Override
    protected Class<? extends Annotation> getAnnotationType() {
        return Supplier.class;
    }

    @Override
    protected BreezeSupplierIndex createIndex() {
        return new BreezeSupplierIndex();
    }

    @Override
    protected void processElement(Element element) {
        processClass(element);
    }

    @Override
    public void extractDataFromClass(TypeElement typeElement) {
        String classPath = typeElement.getQualifiedName().toString();

        BreezeSupplierIndex index = getIndex();

        BreezeSupplierIndex.Entry entry = BreezeSupplierIndex.Entry.builder()
                .classPath(classPath)
                .build();

        index.addEntry(entry);

    }

}
