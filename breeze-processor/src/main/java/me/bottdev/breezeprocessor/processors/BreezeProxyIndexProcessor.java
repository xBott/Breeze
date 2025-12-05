package me.bottdev.breezeprocessor.processors;

import com.google.auto.service.AutoService;
import me.bottdev.breezeapi.di.annotations.Proxy;
import me.bottdev.breezeapi.index.processors.AbstractIndexProcessor;
import me.bottdev.breezeapi.index.processors.ClassIndexProcessor;
import me.bottdev.breezeapi.index.types.BreezeProxyIndex;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.lang.annotation.Annotation;

@AutoService(Processor.class)
@SupportedAnnotationTypes("me.bottdev.breezeapi.di.annotations.Proxy")
@SupportedSourceVersion(SourceVersion.RELEASE_21)
public class BreezeProxyIndexProcessor extends AbstractIndexProcessor<BreezeProxyIndex> implements ClassIndexProcessor {


    @Override
    protected String getOutputFileName() {
        return "proxy-index";
    }

    @Override
    protected Class<? extends Annotation> getAnnotationType() {
        return Proxy.class;
    }

    @Override
    protected BreezeProxyIndex createIndex() {
        return new BreezeProxyIndex();
    }

    @Override
    protected void processElement(Element element) {
        processClass(element);
    }

    @Override
    public void extractDataFromClass(TypeElement typeElement) {
        String classPath = typeElement.getQualifiedName().toString();

        BreezeProxyIndex index = getIndex();

        BreezeProxyIndex.Entry entry = BreezeProxyIndex.Entry.builder()
                .classPath(classPath)
                .build();

        index.addEntry(entry);

    }

}
